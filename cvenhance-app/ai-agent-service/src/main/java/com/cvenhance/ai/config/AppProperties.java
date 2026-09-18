package com.cvenhance.ai.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
public class AppProperties {

    @Value("${spring.application.name:AI-AGENT-SERVICE}")
    private String appName;

    @Value("${frontend.url:http://localhost:5173}")
    private String frontendUrl;

    @Value("${cors.allowed-origins:http://localhost:5173}")
    private String corsAllowedOrigins;

    @Value("${server.port:8085}")
    private int serverPort;

    @Value("${auth.jwt.secret}")
    private String jwtSecret;

    @Value("${auth.jwt.expiry-ms:3600000}")
    private long jwtExpiryMs;

    @Value("${auth.session.expiry-ms:2592000000}")
    private long sessionExpiryMs;

    @Value("${auth.cookie.name:ACCESS_TOKEN}")
    private String cookieName;

    @Value("${auth.cookie.secure:false}")
    private boolean cookieSecure;

    @Value("${auth.cookie.same-site:Lax}")
    private String cookieSameSite;

    @Value("${auth.cookie.path:/}")
    private String cookiePath;

    @Value("${upstash.redis.rest-url:}")
    private String upstashRedisRestUrl;

    @Value("${upstash.redis.rest-token:}")
    private String upstashRedisRestToken;

    @Value("${spring.ai.google.genai.chat.options.model:gemini-flash-latest}")
    private String geminiModel;

    @Value("${ai.openrouter.api-key:}")
    private String openRouterApiKey;

    @Value("${ai.openrouter.base-url:https://openrouter.ai/api}")
    private String openRouterBaseUrl;

    @Value("${ai.openrouter.model:openrouter/free}")
    private String openRouterModel;

    @Value("${ai.groq.api-key:}")
    private String groqApiKey;

    @Value("${ai.groq.base-url:https://api.groq.com/openai}")
    private String groqBaseUrl;

    @Value("${ai.groq.model:openai/gpt-oss-120b}")
    private String groqModel;

    @Value("${ai.primary-provider:openrouter}")
    private String aiPrimaryProvider;

    @Value("${storage.s3.bucket:resume_pdf}")
    private String s3Bucket;

    @Value("${storage.s3.endpoint:}")
    private String s3Endpoint;

    @Value("${storage.s3.region:ap-south-1}")
    private String s3Region;

    @Value("${storage.s3.access-key:}")
    private String s3AccessKey;

    @Value("${storage.s3.secret-key:}")
    private String s3SecretKey;
}
