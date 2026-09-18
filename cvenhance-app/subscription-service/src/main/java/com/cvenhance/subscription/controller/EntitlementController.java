package com.cvenhance.subscription.controller;

import com.cvenhance.subscription.dto.EntitlementResponse;
import com.cvenhance.subscription.service.EntitlementService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class EntitlementController {

    private final EntitlementService entitlementService;

    @GetMapping("/api/me/entitlement")
    public EntitlementResponse entitlement(Authentication authentication) {
        return entitlementService.describe(authentication.getName());
    }
}
