package com.cvenhance.payment.service;

import com.cvenhance.common.audit.Auditable;
import com.cvenhance.common.dto.MessageResponse;
import com.cvenhance.common.exception.ApiException;
import com.cvenhance.common.audit.AuditAction;
import com.cvenhance.payment.client.AuthServiceClient;
import com.cvenhance.payment.client.AuthUserDto;
import com.cvenhance.payment.client.SubscriptionDto;
import com.cvenhance.payment.client.SubscriptionServiceClient;
import com.cvenhance.payment.dto.PaymentOrderStatus;
import com.cvenhance.payment.dto.Plan;
import com.cvenhance.payment.dto.response.CreateOrderResponse;
import com.cvenhance.payment.dto.response.VerifyOrderResponse;
import com.cvenhance.payment.entity.PaymentOrder;
import com.cvenhance.payment.repo.PaymentOrderRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

@Service
public class PaymentOrderService {

    private static final Logger log = LoggerFactory.getLogger(PaymentOrderService.class);

    private static final String STATUS_PAID = "PAID";
    private static final String FALLBACK_PHONE = "9999999999";

    private final CashfreePaymentClient cashfreePaymentClient;
    private final SubscriptionServiceClient subscriptionServiceClient;
    private final AuthServiceClient authServiceClient;
    private final PaymentOrderRepository paymentOrderRepository;
    private final WebhookVerifier webhookVerifier;
    private final ObjectMapper objectMapper;

    public PaymentOrderService(CashfreePaymentClient cashfreePaymentClient,
                               SubscriptionServiceClient subscriptionServiceClient,
                               AuthServiceClient authServiceClient,
                               PaymentOrderRepository paymentOrderRepository,
                               WebhookVerifier webhookVerifier,
                               ObjectMapper objectMapper) {
        this.cashfreePaymentClient = cashfreePaymentClient;
        this.subscriptionServiceClient = subscriptionServiceClient;
        this.authServiceClient = authServiceClient;
        this.paymentOrderRepository = paymentOrderRepository;
        this.webhookVerifier = webhookVerifier;
        this.objectMapper = objectMapper;
    }

    @Transactional
    @Auditable(
            action = AuditAction.PAYMENT_ORDER_CREATED,
            actor = "#ownerEmail", targetType = "PAYMENT_ORDER", detail = "'plan=' + #planId")
    public CreateOrderResponse createOrder(String ownerEmail, String planId, String customerPhone) {
        Plan plan = parsePlan(planId);
        AuthUserDto user = authServiceClient.getActiveUser(ownerEmail);
        rejectNonUpgrade(ownerEmail, plan);

        CreateOrderResponse order = placeOrder(plan, user, resolvePhone(customerPhone));
        savePendingOrder(ownerEmail, plan, order.orderId());
        return order;
    }

    @Transactional
    public VerifyOrderResponse confirmAndGrant(String orderId) {
        VerifyOrderResponse verification = cashfreePaymentClient.verifyOrder(orderId);
        if (isPaid(verification) && claimForGrant(orderId)) {
            grantPlanForOrder(orderId);
        }
        return verification;
    }

    @Transactional
    public MessageResponse handleWebhook(byte[] rawBody, String signature, String timestamp) {
        String body = new String(rawBody, StandardCharsets.UTF_8);
        if (!webhookVerifier.isValid(timestamp, body, signature)) {
            log.warn("Webhook rejected: invalid signature");
            throw ApiException.unauthorized("invalid signature");
        }
        processWebhook(body);
        return new MessageResponse("ok");
    }

//-----------------------------------private methods-----------------------------------

    private void processWebhook(String rawBody) {
        String orderId = extractOrderId(rawBody);
        if (Objects.isNull(orderId)) {
            log.warn("Webhook received with no order_id in payload");
            return;
        }
        log.info("Webhook received for order {} — confirming with Cashfree", orderId);
        try {
            VerifyOrderResponse verification = confirmAndGrant(orderId);
            log.info("Webhook processed for order {} — Cashfree status {}", orderId, verification.orderStatus());
        } catch (Exception e) {
            log.warn("Webhook for order {} could not be confirmed: {}", orderId, e.getMessage());
        }
    }

    private CreateOrderResponse placeOrder(Plan plan, AuthUserDto user, String phone) {
        return cashfreePaymentClient.createOrder(
                plan.getPriceInr(), user.fullName(), user.email(), phone);
    }

    private void savePendingOrder(String ownerEmail, Plan plan, String orderId) {
        PaymentOrder order = new PaymentOrder();
        order.setOrderId(orderId);
        order.setOwnerEmail(ownerEmail);
        order.setPlan(plan);
        order.setAmount(plan.getPriceInr());
        order.setStatus(PaymentOrderStatus.CREATED);
        paymentOrderRepository.save(order);
    }

    private String resolvePhone(String customerPhone) {
        return (Objects.isNull(customerPhone) || customerPhone.isBlank()) ? FALLBACK_PHONE : customerPhone;
    }

    private boolean claimForGrant(String orderId) {
        return paymentOrderRepository.markPaid(orderId, PaymentOrderStatus.PAID, PaymentOrderStatus.CREATED) == 1;
    }

    private void grantPlanForOrder(String orderId) {
        PaymentOrder order = paymentOrderRepository.findByOrderId(orderId).orElse(null);
        if (Objects.nonNull(order)) {
            subscriptionServiceClient.grant(order.getOwnerEmail(), order.getPlan());
            log.info("Granted plan {} to {} for order {}", order.getPlan(), order.getOwnerEmail(), orderId);
        }
    }

    private Plan parsePlan(String planId) {
        if (Objects.isNull(planId) || planId.isBlank()) {
            throw ApiException.badData("planId is required");
        }
        try {
            return Plan.valueOf(planId.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw ApiException.badData("Unknown plan: " + planId);
        }
    }

    private void rejectNonUpgrade(String ownerEmail, Plan requested) {
        SubscriptionDto current = subscriptionServiceClient.find(ownerEmail).orElse(null);
        if (current != null && current.active() && current.plan() != null
                && requested.getLevel() <= current.plan().getLevel()) {
            throw ApiException.badData("You already have the " + current.plan().name()
                    + " plan — you can only upgrade to a higher plan.");
        }
    }

    private boolean isPaid(VerifyOrderResponse verification) {
        return Objects.nonNull(verification) && STATUS_PAID.equalsIgnoreCase(verification.orderStatus());
    }

    private String extractOrderId(String rawBody) {
        try {
            JsonNode orderId = objectMapper.readTree(rawBody).path("data").path("order").path("order_id");
            String value = orderId.asText(null);
            return (Objects.isNull(value) || value.isBlank()) ? null : value;
        } catch (Exception e) {
            return null;
        }
    }
}
