package com.cvenhance.ai.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class SubscriptionServiceClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(SubscriptionServiceClient.class);

    private final RestClient restClient;

    public SubscriptionServiceClient(@Value("${services.subscription-service.url}") String subscriptionServiceUrl) {
        this.restClient = RestClient.builder()
                .baseUrl(subscriptionServiceUrl)
                .build();
    }

    public boolean hasActivePlan(String email) {
        try {
            Boolean isActive = restClient.get()
                    .uri("/internal/subscriptions/{email}/active", email)
                    .retrieve()
                    .body(Boolean.class);
            return isActive != null && isActive;
        } catch (Exception e) {
            LOGGER.error("Failed to check subscription for {}: {}", email, e.getMessage());
            return false;
        }
    }
}
