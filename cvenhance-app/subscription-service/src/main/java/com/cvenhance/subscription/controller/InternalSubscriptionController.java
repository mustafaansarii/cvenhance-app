package com.cvenhance.subscription.controller;

import com.cvenhance.subscription.dto.GrantSubscriptionRequest;
import com.cvenhance.subscription.dto.SubscriptionResponse;
import com.cvenhance.subscription.dto.UnlockRequest;
import com.cvenhance.subscription.entity.Subscription;
import com.cvenhance.subscription.service.EntitlementService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/internal/subscriptions")
@RequiredArgsConstructor
public class InternalSubscriptionController {

    private final EntitlementService entitlementService;

    @PostMapping("/grant")
    public SubscriptionResponse grantSubscription(@RequestBody GrantSubscriptionRequest request) {
        Subscription sub = entitlementService.grant(request.ownerEmail(), request.plan());
        return SubscriptionResponse.fromEntity(sub);
    }

    @GetMapping("/{email}")
    public SubscriptionResponse getSubscription(@PathVariable String email) {
        return entitlementService.find(email)
                .map(SubscriptionResponse::fromEntity)
                .orElse(null);
    }

    @GetMapping("/{email}/active")
    public boolean hasActivePlan(@PathVariable String email) {
        return entitlementService.hasActivePlan(email);
    }

    @PostMapping("/{email}/unlock")
    public boolean unlock(@PathVariable String email, @RequestBody UnlockRequest request) {
        return entitlementService.unlock(email, request.resumeKey());
    }

    @GetMapping("/{email}/unlock-view")
    public EntitlementService.UnlockView getUnlockView(@PathVariable String email) {
        return entitlementService.unlockViewFor(email);
    }

    @GetMapping("/{email}/unlocked/{resumeKey}")
    public boolean isUnlocked(@PathVariable String email, @PathVariable String resumeKey) {
        return entitlementService.isUnlocked(email, resumeKey);
    }
}
