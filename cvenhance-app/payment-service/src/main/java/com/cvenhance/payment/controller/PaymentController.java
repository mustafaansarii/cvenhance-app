package com.cvenhance.payment.controller;

import com.cvenhance.common.dto.MessageResponse;
import com.cvenhance.payment.dto.request.PaymentCreateOrderRequest;
import com.cvenhance.payment.dto.request.VerifyOrderRequest;
import com.cvenhance.payment.dto.response.CreateOrderResponse;
import com.cvenhance.payment.dto.response.VerifyOrderResponse;
import com.cvenhance.payment.service.PaymentOrderService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentOrderService paymentOrderService;

    public PaymentController(PaymentOrderService paymentOrderService) {
        this.paymentOrderService = paymentOrderService;
    }

    @PostMapping("/create-order")
    public CreateOrderResponse createOrder(Authentication authentication, @Valid @RequestBody PaymentCreateOrderRequest request) {
        return paymentOrderService.createOrder(authentication.getName(), request.planId(), request.customerPhone());
    }

    @PostMapping("/verify")
    public VerifyOrderResponse verify(@Valid @RequestBody VerifyOrderRequest request) {
        return paymentOrderService.confirmAndGrant(request.orderId());
    }

    @PostMapping("/webhook")
    public MessageResponse webhook(@RequestBody byte[] rawBody,
                                   @RequestHeader(value = "x-webhook-signature", required = false) String signature,
                                   @RequestHeader(value = "x-webhook-timestamp", required = false) String timestamp) {
        return paymentOrderService.handleWebhook(rawBody, signature, timestamp);
    }
}
