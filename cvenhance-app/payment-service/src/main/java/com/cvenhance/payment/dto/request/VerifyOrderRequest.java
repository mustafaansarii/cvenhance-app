package com.cvenhance.payment.dto.request;

import jakarta.validation.constraints.NotBlank;

public record VerifyOrderRequest(
        @NotBlank(message = "orderId is required")
        String orderId
) {
}
