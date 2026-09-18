package com.cvenhance.payment.client;

import com.cvenhance.common.exception.ApiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class AuthServiceClient {
    private static final Logger log = LoggerFactory.getLogger(AuthServiceClient.class);
    private final RestClient restClient;

    public AuthServiceClient(RestClient.Builder restClientBuilder) {
        this.restClient = restClientBuilder.baseUrl("http://localhost:8081").build();
    }

    public AuthUserDto getActiveUser(String email) {
        try {
            return restClient.get()
                    .uri("/internal/users/{email}", email)
                    .retrieve()
                    .body(AuthUserDto.class);
        } catch (Exception e) {
            log.error("Failed to fetch user {}: {}", email, e.getMessage());
            throw ApiException.notFound("User not found or inactive: " + email);
        }
    }
}
