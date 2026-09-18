package com.cvenhance.payment.client;

import com.cvenhance.payment.dto.Plan;

public record SubscriptionDto(
        String ownerEmail,
        Plan plan,
        boolean active
) {}
