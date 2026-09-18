package com.cvenhance.auth.dtoApi;

import com.cvenhance.auth.dto.SigninRequest;
import com.cvenhance.auth.dto.SignupRequest;
import com.cvenhance.common.dto.MessageResponse;
import com.cvenhance.auth.dto.UserResponse;
import com.cvenhance.auth.entity.AuthUser;
import com.cvenhance.common.security.AuthCookies;
import com.cvenhance.auth.security.RequestMetadataExtractor;
import com.cvenhance.common.exception.ApiException;
import com.cvenhance.auth.service.AuthService;
import com.cvenhance.auth.util.AbstractDtoUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class AuthDtoApi extends AbstractDtoUtil {

    private static final Logger logger = LoggerFactory.getLogger(AuthDtoApi.class);

    private final AuthService authService;
    private final AuthCookies authCookies;
    private final ObjectMapper objectMapper;

    public MessageResponse signup(SignupRequest request) {
        validate(request);
        return authService.signup(request);
    }

    public UserResponse register(SignupRequest request) {
        validate(request);
        return toUserResponse(authService.register(request));
    }

    public UserResponse signin(SigninRequest request, HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
        validate(request);
        AuthService.LoginResult result = authService.login(request, RequestMetadataExtractor.extract(httpRequest));
        httpResponse.addHeader(HttpHeaders.SET_COOKIE, authCookies.access(result.accessToken()).toString());
        return toUserResponse(result.user());
    }

    @com.cvenhance.common.audit.Auditable(
            action = com.cvenhance.common.audit.AuditAction.LOGOUT,
            actor = "#authentication.name")
    public MessageResponse logout(Authentication authentication, HttpServletResponse httpResponse) {
        authService.revokeSession(tokenIdOf(authentication));
        httpResponse.addHeader(HttpHeaders.SET_COOKIE, authCookies.clear().toString());
        return MessageResponse.of("Logged out successfully");
    }

    public UserResponse me(Authentication authentication) {
        return toUserResponse(authService.getActiveUser(authentication.getName()));
    }

    public UserResponse updateProfile(String email, Map<String, Object> profile) {
        String json;
        try {
            json = objectMapper.writeValueAsString(profile == null ? Map.of() : profile);
        } catch (Exception e) {
            logger.error("Failed to serialize profile data", e);
            throw ApiException.badData("Invalid profile data");
        }
        return toUserResponse(authService.updateProfile(email, json));
    }
    
//-----------------------------------private methods-----------------------------------
    public UserResponse toUserResponse(AuthUser user) {
        Object profile = null;
        if (user.getProfileData() != null && !user.getProfileData().isBlank()) {
            try {
                profile = objectMapper.readValue(user.getProfileData(), Object.class);
            } catch (Exception ignored) {
                logger.warn("Could not parse stored profileData for user {}", user.getEmail());
            }
        }
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .verified(user.isVerified())
                .roles(user.getRoles())
                .profileData(profile)
                .build();
    }

    private String tokenIdOf(Authentication authentication) {
        if (Objects.nonNull(authentication) && authentication.getCredentials() instanceof String tokenId) {
            return tokenId;
        }
        return null;
    }
}
