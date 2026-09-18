package com.cvenhance.ai.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class AuthServiceClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthServiceClient.class);

    private final RestClient restClient;

    public AuthServiceClient(@Value("${services.auth-service.url}") String authServiceUrl) {
        this.restClient = RestClient.builder()
                .baseUrl(authServiceUrl)
                .build();
    }

    public void updateProfile(String email, String profileJson) {
        try {
            restClient.patch()
                    .uri("/internal/users/{email}/profile", email)
                    .body(profileJson)
                    .header("Content-Type", "application/json")
                    .retrieve()
                    .toBodilessEntity();
        } catch (Exception e) {
            LOGGER.error("Failed to update profile for {}: {}", email, e.getMessage());
            throw new RuntimeException("Failed to update profile via Auth Service", e);
        }
    }
}
