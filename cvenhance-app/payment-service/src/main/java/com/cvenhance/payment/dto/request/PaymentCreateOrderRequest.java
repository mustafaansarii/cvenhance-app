package com.cvenhance.payment.dto.request;

import jakarta.validation.constraints.NotBlank;

public record PaymentCreateOrderRequest(
        @NotBlank(message = "planId is required")
        String planId,
        String customerPhone
) {
}
