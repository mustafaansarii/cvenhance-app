package com.cvenhance.ai.service;

import com.cvenhance.ai.core.AiPrompt;
import com.cvenhance.ai.core.AiRequest;
import com.cvenhance.ai.core.AiService;
import com.cvenhance.ai.dto.Profile;
import com.cvenhance.ai.dto.ImportResumeRequest;
import com.cvenhance.common.exception.ApiException;
import com.cvenhance.ai.util.ParseProfileDataHelper;
import com.cvenhance.ai.client.AuthServiceClient;
import com.cvenhance.ai.client.SubscriptionServiceClient;
import com.cvenhance.common.audit.Auditable;
import com.cvenhance.common.audit.AuditAction;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Objects;

@Service
public class ResumeImportService {

    private static final Logger logger = LoggerFactory.getLogger(ResumeImportService.class);

    private static final double EXTRACT_TEMPERATURE = 0.2;

    private final AiService aiService;
    private final AuthServiceClient authServiceClient;
    private final ObjectMapper objectMapper;
    private final ParseProfileDataHelper parseProfileDataHelper;
    private final RedisRateLimiter redisRateLimiter;
    private final SubscriptionServiceClient subscriptionServiceClient;

    private String profileSchema = "{}";

    public ResumeImportService(AiService aiService, AuthServiceClient authServiceClient, ObjectMapper objectMapper,
                               ParseProfileDataHelper parseProfileDataHelper, RedisRateLimiter redisRateLimiter,
                               SubscriptionServiceClient subscriptionServiceClient) {
        this.aiService = aiService;
        this.authServiceClient = authServiceClient;
        this.objectMapper = objectMapper;
        this.parseProfileDataHelper = parseProfileDataHelper;
        this.redisRateLimiter = redisRateLimiter;
        this.subscriptionServiceClient = subscriptionServiceClient;
    }

    @PostConstruct
    void loadProfileSchema() {
        try (InputStream in = new ClassPathResource("sample-resume.json").getInputStream()) {
            profileSchema = new String(in.readAllBytes(), StandardCharsets.UTF_8);
        } catch (Exception ignored) {}
    }

    @Auditable(action = AuditAction.RESUME_IMPORTED, actor = "#ownerEmail")
    public Map<String, Object> importFromText(String ownerEmail, ImportResumeRequest request) {
        redisRateLimiter.checkAiDailyLimit(ownerEmail, subscriptionServiceClient.hasActivePlan(ownerEmail));
        String resumeText = Objects.isNull(request) || Objects.isNull(request.getResumeText())
                ? "" : request.getResumeText().trim();
        String guidance = Objects.isNull(request) || Objects.isNull(request.getJobDescription())
                ? "" : request.getJobDescription().trim();

        if (resumeText.isBlank() && guidance.isBlank()) {
            throw ApiException.badData("Please upload a resume or provide some guidance.");
        }

        String sourceText = resumeText;
        if (sourceText.isBlank()) {
            // we skip existingProfileText fetch in microservices unless we explicitly call auth service
            // but the user just wants standard import, if sourceText is blank we just throw
            throw ApiException.badData("No existing resume to tailor — please upload your resume too.");
        }

        Map<String, Object> profile = parseProfile(sourceText, guidance);
        saveProfile(ownerEmail, profile);
        return profile;
    }

    private Map<String, Object> parseProfile(String resumeText, String guidance) {
        try {
            return parseProfileWithAi(resumeText, guidance);
        } catch (Exception e) {
            logger.error("AI parsing failed, falling back to manual parsing", e);
            return parseProfileDataHelper.parseProfileWithManual(resumeText, objectMapper, profileSchema);
        }
    }

    private Map<String, Object> parseProfileWithAi(String resumeText, String guidance) {
        AiRequest request = new AiRequest(
                buildUserPrompt(resumeText, guidance),
                buildSystemInstruction(guidance),
                EXTRACT_TEMPERATURE);
        Profile profile = aiService.generate(request, Profile.class);
        if (profile == null) {
            throw ApiException.badData("Could not turn that resume into profile data. Please try a clearer resume.");
        }
        return objectMapper.convertValue(profile, new TypeReference<Map<String, Object>>() { });
    }

    private String buildSystemInstruction(String guidance) {
        return (guidance.isBlank() ? AiPrompt.RESUME_PARSER_SYSTEM : AiPrompt.RESUME_GUIDED_SYSTEM).getPrompt();
    }

    private String buildUserPrompt(String resumeText, String guidance) {
        if (guidance.isBlank()) {
            return "RESUME TEXT:\n" + resumeText;
        }
        return "GUIDANCE (target job description and/or improvement feedback):\n" + guidance
                + "\n\nRESUME TEXT:\n" + resumeText;
    }

    private void saveProfile(String ownerEmail, Map<String, Object> profile) {
        try {
            authServiceClient.updateProfile(ownerEmail, objectMapper.writeValueAsString(profile));
        } catch (Exception e) {
            logger.error("Saving imported profile failed", e);
            throw new RuntimeException("Failed to save the imported profile", e);
        }
    }
}
