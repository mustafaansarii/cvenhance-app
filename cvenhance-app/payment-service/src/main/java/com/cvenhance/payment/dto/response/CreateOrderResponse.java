package com.cvenhance.payment.dto.response;

public record CreateOrderResponse(
        String orderId,
        Double orderAmount,
        String orderCurrency,
        String paymentSessionId,
        String orderStatus
) {
}
