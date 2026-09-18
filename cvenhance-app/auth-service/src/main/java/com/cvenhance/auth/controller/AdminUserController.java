package com.cvenhance.auth.controller;

import com.cvenhance.common.audit.AuditAction;
import com.cvenhance.auth.dto.AdminUserUpdate;
import com.cvenhance.common.dto.PageQuery;
import com.cvenhance.auth.dto.AdminUserDto;
import com.cvenhance.common.dto.PageResponse;
import com.cvenhance.auth.dtoApi.AdminUserDtoApi;
import com.cvenhance.common.audit.AuditEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminUserController {

    private final AdminUserDtoApi adminUserDtoApi;

    // ---------------- Audit events (read-only) ----------------

    @GetMapping("/audit-events")
    public PageResponse<AuditEvent> auditEvents(PageQuery query,
                                                @RequestParam(required = false) AuditAction action) {
        return adminUserDtoApi.listAudit(query, action);
    }

    // ---------------- Users ----------------

    @GetMapping("/users")
    public PageResponse<AdminUserDto> users(PageQuery query) {
        return adminUserDtoApi.listUsers(query);
    }

    @PatchMapping("/users")
    public List<AdminUserDto> updateUsers(@RequestBody List<AdminUserUpdate> updates) {
        return adminUserDtoApi.updateUsers(updates);
    }

    @PostMapping("/users/resume")
    public com.cvenhance.common.dto.MessageResponse assignResume(
            org.springframework.security.core.Authentication authentication,
            @RequestBody com.cvenhance.auth.dto.AssignResumeRequest request) {
        return adminUserDtoApi.assignResume(authentication.getName(), request);
    }

}
