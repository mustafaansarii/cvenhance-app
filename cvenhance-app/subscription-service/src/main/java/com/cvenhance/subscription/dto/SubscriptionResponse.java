package com.cvenhance.subscription.dto;

import com.cvenhance.subscription.entity.Subscription;

public record SubscriptionResponse(
        String ownerEmail,
        Plan plan,
        String validUntil,
        Integer creditsRemaining
) {
    public static SubscriptionResponse fromEntity(Subscription subscription) {
        if (subscription == null) return null;
        return new SubscriptionResponse(
                subscription.getOwnerEmail(),
                subscription.getPlan(),
                subscription.getValidUntil() != null ? subscription.getValidUntil().toString() : null,
                subscription.getCreditsRemaining()
        );
    }
}
