package com.cvenhance.payment.client;

import com.cvenhance.payment.dto.Plan;

public record GrantRequest(
        String email,
        Plan plan
) {}
