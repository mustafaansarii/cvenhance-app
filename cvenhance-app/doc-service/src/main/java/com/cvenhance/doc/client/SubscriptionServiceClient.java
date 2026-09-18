package com.cvenhance.doc.client;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

@Component
public class SubscriptionServiceClient {

    private final RestClient restClient;

    public SubscriptionServiceClient(RestClient.Builder restClientBuilder) {
        this.restClient = restClientBuilder.baseUrl("http://localhost:8083").build();
    }

    public boolean isUnlocked(String email, String key) {
        try {
            return Boolean.TRUE.equals(restClient.get()
                    .uri("/internal/subscriptions/{email}/unlocked/{key}", email, key)
                    .retrieve()
                    .body(Boolean.class));
        } catch (RestClientResponseException e) {
            return false;
        }
    }

    public UnlockView unlockViewFor(String email) {
        try {
            return restClient.get()
                    .uri("/internal/subscriptions/{email}/unlock-view", email)
                    .retrieve()
                    .body(UnlockView.class);
        } catch (RestClientResponseException e) {
            return key -> false;
        }
    }

    public boolean unlock(String email, String key) {
        try {
            return Boolean.TRUE.equals(restClient.post()
                    .uri("/internal/subscriptions/{email}/unlock", email)
                    .body(new UnlockRequest(key))
                    .retrieve()
                    .body(Boolean.class));
        } catch (RestClientResponseException e) {
            return false;
        }
    }

    public interface UnlockView {
        boolean isUnlocked(String key);
    }

    public record UnlockRequest(String key) {}
}
