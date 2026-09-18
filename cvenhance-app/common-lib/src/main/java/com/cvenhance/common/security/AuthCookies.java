package com.cvenhance.common.security;

import org.springframework.http.ResponseCookie;

/**
 * Cookie helpers for the JWT access token.
 * Shared across services so each can read the auth cookie.
 */
public class AuthCookies {

    private final String name;
    private final boolean secure;
    private final String sameSite;
    private final String path;
    private final long maxAgeSeconds;

    public AuthCookies(String name, boolean secure, String sameSite, String path, long sessionExpiryMs) {
        this.name = name;
        this.secure = secure;
        this.sameSite = sameSite;
        this.path = path;
        this.maxAgeSeconds = sessionExpiryMs / 1000;
    }

    public String name() {
        return name;
    }

    public ResponseCookie access(String token) {
        return base(token).maxAge(maxAgeSeconds).build();
    }

    public ResponseCookie clear() {
        return base("").maxAge(0).build();
    }

    private ResponseCookie.ResponseCookieBuilder base(String value) {
        return ResponseCookie.from(name, value)
                .httpOnly(true)
                .secure(secure)
                .path(path)
                .sameSite(sameSite);
    }
}

