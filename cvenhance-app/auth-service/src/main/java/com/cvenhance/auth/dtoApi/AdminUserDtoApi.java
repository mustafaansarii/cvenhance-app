package com.cvenhance.auth.dtoApi;

import com.cvenhance.common.audit.AuditAction;
import com.cvenhance.auth.dto.AdminUserUpdate;
import com.cvenhance.common.dto.PageQuery;
import com.cvenhance.auth.dto.AdminUserDto;
import com.cvenhance.common.dto.PageResponse;
import com.cvenhance.common.audit.AuditEvent;
import com.cvenhance.auth.entity.AuthUser;
import com.cvenhance.common.exception.ApiException;
import com.cvenhance.auth.service.AdminUserService;
import com.cvenhance.auth.util.AbstractDtoUtil;
import com.cvenhance.common.util.PageUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import com.cvenhance.common.dto.MessageResponse;

import java.util.List;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class AdminUserDtoApi extends AbstractDtoUtil {

    private static final int MAX_BATCH = 100;

    private final AdminUserService adminUserService;

    public PageResponse<AuditEvent> listAudit(PageQuery query, AuditAction action) {
        Pageable pageable = PageUtil.toPageable(query, "createdAt");
        Page<AuditEvent> result = adminUserService.listAudit(query.getKeyword(), action, pageable);
        return PageUtil.toResponse(result, result.getContent());
    }

    public PageResponse<AdminUserDto> listUsers(PageQuery query) {
        Pageable pageable = PageUtil.toPageable(query, "createdAt");
        Page<AuthUser> result = adminUserService.listUsers(query.getKeyword(), pageable);
        return PageUtil.toResponse(result, result.getContent().stream().map(this::toUserDto).toList());
    }

    public List<AdminUserDto> updateUsers(List<AdminUserUpdate> updates) {
        validateBatch(updates);
        return adminUserService.updateUsers(updates).stream().map(this::toUserDto).toList();
    }

    public MessageResponse assignResume(
            String adminEmail, com.cvenhance.auth.dto.AssignResumeRequest request) {
        validate(request);
        adminUserService.assignResume(adminEmail, request);
        return MessageResponse.of(
                "Resume assigned to " + request.getTargetEmail().trim());
    }

    private void validateBatch(List<?> updates) {
        if (Objects.isNull(updates) || updates.isEmpty()) {
            throw ApiException.badData("At least one item is required");
        }
        if (updates.size() > MAX_BATCH) {
            throw ApiException.badData("At most " + MAX_BATCH + " items can be updated at once");
        }
        updates.forEach(AbstractDtoUtil::validate);
    }

    private AdminUserDto toUserDto(AuthUser u) {
        return AdminUserDto.builder()
                .id(u.getId())
                .email(u.getEmail())
                .fullName(u.getFullName())
                .verified(u.isVerified())
                .provider(u.getProvider())
                .roles(u.getRoles())
                .createdAt(u.getCreatedAt())
                .build();
    }
}
