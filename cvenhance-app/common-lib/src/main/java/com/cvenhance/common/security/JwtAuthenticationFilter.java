package com.cvenhance.common.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

/**
 * JWT authentication filter shared across all services.
 * <p>
 * In the Auth Service, the optional {@link SessionValidator} checks and touches
 * server-side sessions. In downstream services that only validate tokens,
 * pass {@code null} for the validator — the filter will trust the JWT signature alone.
 */
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final SessionValidator sessionValidator;
    private final AuthCookies authCookies;

    /**
     * Callback interface for session validation. Only the Auth Service implements this;
     * other services pass {@code null} to skip server-side session checks.
     */
    @FunctionalInterface
    public interface SessionValidator {
        boolean validateAndTouch(String tokenId);
    }

    public JwtAuthenticationFilter(JwtService jwtService, SessionValidator sessionValidator, AuthCookies authCookies) {
        this.jwtService = jwtService;
        this.sessionValidator = sessionValidator;
        this.authCookies = authCookies;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        String token = fromCookie(request);
        if (Objects.isNull(token)) {
            token = fromAuthorizationHeader(request);
        }

        if (Objects.nonNull(token) && Objects.isNull(SecurityContextHolder.getContext().getAuthentication())) {
            JwtService.TokenInspection inspection = jwtService.inspect(token);
            if (inspection.usable() && isSessionValid(inspection.tokenId())) {
                if (inspection.status() == JwtService.Status.EXPIRED && sessionValidator != null) {
                    reissueAccessToken(response, inspection);
                }
                authenticate(request, inspection);
            }
        }

        chain.doFilter(request, response);
    }

    private boolean isSessionValid(String tokenId) {
        // If no session validator is configured (downstream services), trust the JWT
        if (sessionValidator == null) {
            return true;
        }
        return sessionValidator.validateAndTouch(tokenId);
    }

    private void reissueAccessToken(HttpServletResponse response, JwtService.TokenInspection inspection) {
        String refreshed = jwtService.generate(inspection.email(), inspection.tokenId(), inspection.roles());
        response.addHeader("Set-Cookie", authCookies.access(refreshed).toString());
    }

    private void authenticate(HttpServletRequest request, JwtService.TokenInspection inspection) {
        List<SimpleGrantedAuthority> authorities = inspection.roles().stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .toList();
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(inspection.email(), inspection.tokenId(), authorities);
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private String fromCookie(HttpServletRequest request) {
        if (Objects.isNull(request.getCookies())) {
            return null;
        }
        for (Cookie cookie : request.getCookies()) {
            if (authCookies.name().equals(cookie.getName())
                    && Objects.nonNull(cookie.getValue()) && !cookie.getValue().isBlank()) {
                return cookie.getValue();
            }
        }
        return null;
    }

    private String fromAuthorizationHeader(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (Objects.nonNull(header) && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        return null;
    }
}

