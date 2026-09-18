package com.cvenhance.subscription.service;

import com.cvenhance.common.audit.Auditable;
import com.cvenhance.common.audit.AuditAction;
import com.cvenhance.subscription.dto.Plan;
import com.cvenhance.subscription.dto.EntitlementResponse;
import com.cvenhance.subscription.entity.Subscription;
import com.cvenhance.subscription.repository.SubscriptionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class EntitlementService {

    private static final long VALIDITY_DAYS = 365;
    private static final String ADMIN_AUTHORITY = "ROLE_ADMIN";

    private final SubscriptionRepository subscriptionRepository;

    public boolean isAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (Objects.isNull(authentication)) {
            return false;
        }
        for (GrantedAuthority authority : authentication.getAuthorities()) {
            if (ADMIN_AUTHORITY.equals(authority.getAuthority())) {
                return true;
            }
        }
        return false;
    }

    @Transactional(readOnly = true)
    public Optional<Subscription> find(String ownerEmail) {
        return subscriptionRepository.findByOwnerEmail(ownerEmail);
    }
    
    @Transactional(readOnly = true)
    public EntitlementResponse describe(String ownerEmail) {
        if (isAdmin()) {
            return EntitlementResponse.unlimited("ADMIN");
        }
        Subscription subscription = subscriptionRepository.findByOwnerEmail(ownerEmail).orElse(null);
        if (!isActive(subscription)) {
            return EntitlementResponse.free();
        }
        boolean unlimited = subscription.getCreditsRemaining() == null;
        int creditsRemaining = unlimited ? EntitlementResponse.UNLIMITED_CREDITS : subscription.getCreditsRemaining();
        return new EntitlementResponse(
                subscription.getPlan().name(),
                true,
                unlimited,
                creditsRemaining,
                subscription.getValidUntil().toString());
    }

    public boolean isActive(Subscription subscription) {
        return Objects.nonNull(subscription)
                && Objects.nonNull(subscription.getValidUntil())
                && subscription.getValidUntil().isAfter(Instant.now());
    }

    @Transactional(readOnly = true)
    public boolean hasActivePlan(String ownerEmail) {
        if (isAdmin()) {
            return true;
        }
        return isActive(subscriptionRepository.findByOwnerEmail(ownerEmail).orElse(null));
    }

    @Transactional(readOnly = true)
    public boolean isUnlocked(String ownerEmail, String resumeKey) {
        return unlockViewFor(ownerEmail).isUnlocked(resumeKey);
    }

    @Transactional(readOnly = true)
    public UnlockView unlockViewFor(String ownerEmail) {
        if (isAdmin()) {
            return UnlockView.ALL;
        }
        Subscription subscription = subscriptionRepository.findByOwnerEmail(ownerEmail).orElse(null);
        if (!isActive(subscription)) {
            return UnlockView.NONE;
        }
        boolean unlimited = subscription.getCreditsRemaining() == null;
        return new UnlockView(false, unlimited, Set.copyOf(subscription.getUnlockedTemplateCodes()));
    }

    public record UnlockView(boolean unlockAll, boolean unlimited, Set<String> unlockedKeys) {

        public static final UnlockView ALL = new UnlockView(true, true, Set.of());
        public static final UnlockView NONE = new UnlockView(false, false, Set.of());

        public boolean isUnlocked(String resumeKey) {
            return unlockAll || unlimited || unlockedKeys.contains(resumeKey);
        }
    }

    @Transactional
    public boolean unlock(String ownerEmail, String resumeKey) {
        if (isAdmin()) {
            return true;
        }
        Subscription subscription = subscriptionRepository.findByOwnerEmail(ownerEmail).orElse(null);
        if (!isActive(subscription)) {
            return false;
        }
        if (subscription.getUnlockedTemplateCodes().contains(resumeKey)) {
            return true;
        }
        boolean unlimited = subscription.getCreditsRemaining() == null;
        if (!unlimited && subscription.getCreditsRemaining() <= 0) {
            return false;
        }
        subscription.getUnlockedTemplateCodes().add(resumeKey);
        if (!unlimited) {
            subscription.setCreditsRemaining(subscription.getCreditsRemaining() - 1);
        }
        subscriptionRepository.save(subscription);
        return true;
    }

    @Transactional
    @Auditable(
            action = AuditAction.SUBSCRIPTION_GRANTED,
            actor = "#ownerEmail", targetType = "SUBSCRIPTION", detail = "'plan=' + #plan")
    public Subscription grant(String ownerEmail, Plan plan) {
        Subscription subscription = subscriptionRepository.findByOwnerEmail(ownerEmail).orElseGet(() -> {
            Subscription fresh = new Subscription();
            fresh.setOwnerEmail(ownerEmail);
            fresh.setCreditsRemaining(0);
            return fresh;
        });

        Instant base = isActive(subscription) ? subscription.getValidUntil() : Instant.now();
        subscription.setValidUntil(base.plus(VALIDITY_DAYS, ChronoUnit.DAYS));
        subscription.setPlan(plan);

        if (plan.isUnlimited()) {
            subscription.setCreditsRemaining(null);
        } else {
            int current = subscription.getCreditsRemaining() == null ? 0 : subscription.getCreditsRemaining();
            subscription.setCreditsRemaining(current + plan.getCredits());
        }
        return subscriptionRepository.save(subscription);
    }
}
