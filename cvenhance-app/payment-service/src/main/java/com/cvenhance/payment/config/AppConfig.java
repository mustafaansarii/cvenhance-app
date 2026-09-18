package com.cvenhance.payment.config;

import com.cvenhance.common.security.AuthCookies;
import com.cvenhance.common.security.JwtService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Bean
    public JwtService jwtService(
            @Value("${auth.jwt.secret}") String secret,
            @Value("${auth.jwt.expiry-ms}") long expiryMs) {
        return new JwtService(secret, expiryMs);
    }

    @Bean
    public AuthCookies authCookies(
            @Value("${auth.cookie.name}") String cookieName,
            @Value("${auth.cookie.secure}") boolean secure,
            @Value("${auth.cookie.domain}") String domain,
            @Value("${auth.cookie.path}") String path) {
        return new AuthCookies(cookieName, secure, domain, path, 86400000L);
    }
}
