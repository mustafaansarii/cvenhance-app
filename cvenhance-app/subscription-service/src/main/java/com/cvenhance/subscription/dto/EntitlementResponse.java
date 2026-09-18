package com.cvenhance.subscription.dto;

public record EntitlementResponse(
        String plan,
        boolean active,
        boolean unlimited,
        int creditsRemaining,
        String validUntil
) {
    public static final int UNLIMITED_CREDITS = -1;

    public static EntitlementResponse free() {
        return new EntitlementResponse("FREE", false, false, 0, null);
    }

    public static EntitlementResponse unlimited(String plan) {
        return new EntitlementResponse(plan, true, true, UNLIMITED_CREDITS, null);
    }
}
