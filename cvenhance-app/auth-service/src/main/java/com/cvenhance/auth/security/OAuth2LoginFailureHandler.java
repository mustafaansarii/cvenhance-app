package com.cvenhance.auth.security;

import com.cvenhance.auth.config.AppProperties;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
public class OAuth2LoginFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    private final AppProperties appProperties;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException {
        String reason = URLEncoder.encode(
                exception.getMessage() == null ? "oauth_failed" : exception.getMessage(), StandardCharsets.UTF_8);
        String target = appProperties.getOauthFailureRedirect()
                + (appProperties.getOauthFailureRedirect().contains("?") ? "&" : "?") + "error=" + reason;
        getRedirectStrategy().sendRedirect(request, response, target);
    }
}
