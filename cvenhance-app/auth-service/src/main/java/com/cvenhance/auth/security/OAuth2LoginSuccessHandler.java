package com.cvenhance.auth.security;

import com.cvenhance.auth.config.AppProperties;
import com.cvenhance.auth.dto.DeviceMetadata;
import com.cvenhance.auth.service.AuthService;
import com.cvenhance.common.security.AuthCookies;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Locale;

@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final AuthService authService;
    private final AuthCookies authCookies;
    private final AppProperties appProperties;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        OAuth2User principal = (OAuth2User) authentication.getPrincipal();
        String email = principal.getAttribute("email");
        String name = principal.getAttribute("name");
        String provider = providerOf(authentication);

        DeviceMetadata device = RequestMetadataExtractor.extract(request);
        AuthService.LoginResult result = authService.loginWithOAuth(email, name, provider, device);

        response.addHeader(HttpHeaders.SET_COOKIE, authCookies.access(result.accessToken()).toString());
        getRedirectStrategy().sendRedirect(request, response, appProperties.getOauthSuccessRedirect());
    }

    private String providerOf(Authentication authentication) {
        if (authentication instanceof OAuth2AuthenticationToken token) {
            return token.getAuthorizedClientRegistrationId().toUpperCase(Locale.ROOT);
        }
        return "OAUTH";
    }
}
