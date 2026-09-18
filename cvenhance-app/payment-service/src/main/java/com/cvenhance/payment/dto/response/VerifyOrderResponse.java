package com.cvenhance.payment.dto.response;

public record VerifyOrderResponse(
        String orderId,
        String orderStatus,
        String paymentMethod,
        Double paymentAmount
) {
}
