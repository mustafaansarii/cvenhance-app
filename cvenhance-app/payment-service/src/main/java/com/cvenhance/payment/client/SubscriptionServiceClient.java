package com.cvenhance.payment.client;

import com.cvenhance.common.exception.ApiException;
import com.cvenhance.payment.dto.Plan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Optional;

@Component
public class SubscriptionServiceClient {
    private static final Logger log = LoggerFactory.getLogger(SubscriptionServiceClient.class);
    private final RestClient restClient;

    public SubscriptionServiceClient(RestClient.Builder restClientBuilder) {
        this.restClient = restClientBuilder.baseUrl("http://localhost:8083").build();
    }

    public Optional<SubscriptionDto> find(String email) {
        try {
            SubscriptionDto dto = restClient.get()
                    .uri("/internal/subscriptions/{email}", email)
                    .retrieve()
                    .body(SubscriptionDto.class);
            return Optional.ofNullable(dto);
        } catch (Exception e) {
            log.warn("Failed to find subscription for {}: {}", email, e.getMessage());
            return Optional.empty();
        }
    }

    public void grant(String email, Plan plan) {
        try {
            restClient.post()
                    .uri("/internal/subscriptions/grant")
                    .body(new GrantRequest(email, plan))
                    .retrieve()
                    .toBodilessEntity();
        } catch (Exception e) {
            log.error("Failed to grant plan {} for {}: {}", plan, email, e.getMessage());
            throw ApiException.badData("Could not grant subscription");
        }
    }
}
