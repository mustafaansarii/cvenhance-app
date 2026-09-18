package com.cvenhance.auth.service;

import com.cvenhance.common.audit.AuditAction;
import com.cvenhance.auth.dto.AdminUserUpdate;
import com.cvenhance.auth.dto.AssignResumeRequest;
import com.cvenhance.common.audit.AuditEvent;
import com.cvenhance.auth.entity.AuthUser;
import com.cvenhance.common.exception.ApiException;
import com.cvenhance.common.audit.AuditEventRepository;
import com.cvenhance.auth.repo.AuthUserRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.cvenhance.common.audit.Auditable;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class AdminUserService {

    private final AuditEventRepository auditEventRepository;
    private final AuthUserRepository authUserRepository;
    private final AccountMailer accountMailer;
    private final ObjectMapper objectMapper;

    // ---------------- Audit events (read-only) ----------------

    @Transactional(readOnly = true)
    public Page<AuditEvent> listAudit(String keyword, AuditAction action, Pageable pageable) {
        return auditEventRepository.search(blankToNull(keyword), action, pageable);
    }

    // ---------------- Users ----------------

    @Transactional(readOnly = true)
    public Page<AuthUser> listUsers(String keyword, Pageable pageable) {
        return authUserRepository.search(blankToNull(keyword), pageable);
    }

    @Transactional
    public List<AuthUser> updateUsers(List<AdminUserUpdate> updates) {
        List<AuthUser> saved = updates.stream().map(u -> {
            AuthUser user = authUserRepository.findById(u.getId())
                    .orElseThrow(() -> ApiException.notFound("User not found: " + u.getId()));
            if (u.getFullName() != null && !u.getFullName().isBlank()) {
                user.setFullName(u.getFullName().trim());
            }
            if (u.getVerified() != null) {
                user.setVerified(u.getVerified());
            }
            // Roles are intentionally not updatable via admin APIs.
            return user;
        }).toList();
        return authUserRepository.saveAll(saved);
    }

    @Transactional
    @Auditable(
            action = AuditAction.RESUME_ASSIGNED, targetType = "AUTH_USER",
            targetId = "#request.targetEmail", detail = "'template=' + #request.templateCode")
    public void assignResume(String adminEmail, AssignResumeRequest request) {
        String targetEmail = request.getTargetEmail().trim().toLowerCase();
        AuthUser user = authUserRepository.findByEmail(targetEmail)
                .orElseThrow(() -> ApiException.notFound("No user found with email: " + targetEmail));

        user.setProfileData(writeJson(request.getProfileData()));
        authUserRepository.save(user);
        accountMailer.sendResumeAssigned(targetEmail, user.getFullName());
    }

    private String writeJson(JsonNode node) {
        try {
            return objectMapper.writeValueAsString(node);
        } catch (Exception e) {
            throw ApiException.badData("Invalid resume data");
        }
    }

    private String blankToNull(String value) {
        return Objects.isNull(value) || value.isBlank() ? null : value.trim();
    }
}
