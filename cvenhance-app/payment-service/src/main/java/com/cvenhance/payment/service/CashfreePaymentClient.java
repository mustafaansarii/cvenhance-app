package com.cvenhance.payment.service;

import com.cvenhance.payment.dto.request.PaymentCreateOrderRequest;
import com.cvenhance.payment.dto.response.CreateOrderResponse;
import com.cvenhance.payment.dto.response.VerifyOrderResponse;
import com.cvenhance.payment.exception.PaymentException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.security.SecureRandom;
import java.util.HexFormat;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

@Service
public class CashfreePaymentClient {

    private static final Logger logger = LoggerFactory.getLogger(CashfreePaymentClient.class);

    private final SecureRandom random = new SecureRandom();
    private volatile RestClient restClient;

    @Value("${cashfree.app.id:}")
    private String appId;

    @Value("${cashfree.secret.key:}")
    private String secretKey;

    @Value("${cashfree.environment:PRODUCTION}")
    private String environment;

    @Value("${cashfree.api-version:2023-08-01}")
    private String apiVersion;

    @Value("${cashfree.return-url:https://cvenhance.in/payment/status?order_id={order_id}}")
    private String returnUrl;

    public CreateOrderResponse createOrder(double amount, String customerName, String customerEmail, String customerPhone) {
        if (amount <= 0 || isBlank(customerName) || isBlank(customerEmail) || isBlank(customerPhone)) {
            throw new PaymentException("amount, customerName, customerEmail and customerPhone are required and amount must be positive");
        }

        String orderId = generateOrderId();
        Map<?, ?> response = exchange("Order creation",
                client -> client.post().uri("/orders").body(buildOrderPayload(amount, customerName, customerEmail, customerPhone, orderId)).retrieve().body(Map.class));
        return toOrderResponse(response);
    }

    public VerifyOrderResponse verifyOrder(String orderId) {
        if (isBlank(orderId)) {
            throw new PaymentException("Order ID is required");
        }
        Map<?, ?> order = exchange("Order verification",
                client -> client.get().uri("/orders/{id}", orderId).retrieve().body(Map.class));
        String orderStatus = asString(order.get("order_status"));
        PaymentInfo payment = fetchFirstPayment(orderId);
        return new VerifyOrderResponse(orderId, orderStatus, payment.method(), payment.amount());
    }

    // ── private helpers ─────────────────────────────────────────────────

    private Map<?, ?> exchange(String action, Function<RestClient, Map> call) {
        try {
            Map<?, ?> body = call.apply(cashfreeClient());
            if (Objects.isNull(body)) {
                throw new PaymentException(action + " failed: empty response from Cashfree");
            }
            return body;
        } catch (PaymentException e) {
            throw e;
        } catch (Exception e) {
            throw new PaymentException(action + " failed: " + e.getMessage(), e);
        }
    }

    private PaymentInfo fetchFirstPayment(String orderId) {
        try {
            List<?> payments = cashfreeClient().get().uri("/orders/{id}/payments", orderId).retrieve().body(List.class);
            if (Objects.nonNull(payments) && !payments.isEmpty() && payments.get(0) instanceof Map<?, ?> firstPayment) {
                return new PaymentInfo(asString(firstPayment.get("payment_method")), asDouble(firstPayment.get("payment_amount")));
            }
        } catch (Exception e) {
            logger.warn("Failed to fetch/parse Cashfree payment details: {}", e.getMessage());
        }
        return new PaymentInfo(null, null);
    }

    private RestClient cashfreeClient() {
        if (isBlank(appId) || isBlank(secretKey)) {
            throw new PaymentException("Cashfree credentials are not configured");
        }
        RestClient client = restClient;
        if (Objects.isNull(client)) {
            String baseUrl = "SANDBOX".equalsIgnoreCase(environment)
                    ? "https://sandbox.cashfree.com/pg"
                    : "https://api.cashfree.com/pg";
            client = restClient = RestClient.builder()
                    .baseUrl(baseUrl)
                    .defaultHeader("x-client-id", appId)
                    .defaultHeader("x-client-secret", secretKey)
                    .defaultHeader("x-api-version", apiVersion)
                    .defaultHeader("Content-Type", "application/json")
                    .defaultHeader("Accept", "application/json")
                    .build();
        }
        return client;
    }

    private Map<String, Object> buildOrderPayload(double amount, String customerName, String customerEmail, String customerPhone, String orderId) {
        Map<String, Object> customerDetails = Map.of(
                "customer_id", "CUST_" + orderId,
                "customer_name", customerName.trim(),
                "customer_email", customerEmail.trim(),
                "customer_phone", customerPhone.trim()
        );
        return Map.of(
                "order_id", orderId,
                "order_amount", amount,
                "order_currency", "INR",
                "customer_details", customerDetails,
                "order_meta", Map.of("return_url", returnUrl)
        );
    }

    private CreateOrderResponse toOrderResponse(Map<?, ?> response) {
        return new CreateOrderResponse(
                asString(response.get("order_id")),
                asDouble(response.get("order_amount")),
                asString(response.get("order_currency")),
                asString(response.get("payment_session_id")),
                asString(response.get("order_status"))
        );
    }

    private String generateOrderId() {
        byte[] bytes = new byte[6];
        random.nextBytes(bytes);
        return HexFormat.of().formatHex(bytes);
    }

    private static boolean isBlank(String value) {
        return Objects.isNull(value) || value.isBlank();
    }

    private static String asString(Object value) {
        return Objects.isNull(value) ? null : value.toString();
    }

    private static Double asDouble(Object value) {
        if (value instanceof Number number) {
            return number.doubleValue();
        }
        if (Objects.isNull(value)) {
            return null;
        }
        try {
            return Double.valueOf(value.toString());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private record PaymentInfo(String method, Double amount) {
    }
}
