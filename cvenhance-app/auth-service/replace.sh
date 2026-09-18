#!/bin/bash
find /home/msi/projects/cvenhance/app/auth-service/src/main/java/com/cvenhance/auth/ -type f -name "*.java" -exec sed -i 's/package com.docservice.careerhub/package com.cvenhance.auth/g' {} +
find /home/msi/projects/cvenhance/app/auth-service/src/main/java/com/cvenhance/auth/ -type f -name "*.java" -exec sed -i 's/com.docservice.careerhub/com.cvenhance.auth/g' {} +
find /home/msi/projects/cvenhance/app/auth-service/src/main/java/com/cvenhance/auth/ -type f -name "*.java" -exec sed -i 's/com.cvenhance.auth.exception.ApiException/com.cvenhance.common.exception.ApiException/g' {} +
find /home/msi/projects/cvenhance/app/auth-service/src/main/java/com/cvenhance/auth/ -type f -name "*.java" -exec sed -i 's/com.cvenhance.auth.exception.GlobalExceptionHandler/com.cvenhance.common.exception.GlobalExceptionHandler/g' {} +
find /home/msi/projects/cvenhance/app/auth-service/src/main/java/com/cvenhance/auth/ -type f -name "*.java" -exec sed -i 's/com.cvenhance.auth.security.JwtService/com.cvenhance.common.security.JwtService/g' {} +
find /home/msi/projects/cvenhance/app/auth-service/src/main/java/com/cvenhance/auth/ -type f -name "*.java" -exec sed -i 's/com.cvenhance.auth.security.JwtAuthenticationFilter/com.cvenhance.common.security.JwtAuthenticationFilter/g' {} +
find /home/msi/projects/cvenhance/app/auth-service/src/main/java/com/cvenhance/auth/ -type f -name "*.java" -exec sed -i 's/com.cvenhance.auth.security.AuthCookies/com.cvenhance.common.security.AuthCookies/g' {} +
find /home/msi/projects/cvenhance/app/auth-service/src/main/java/com/cvenhance/auth/ -type f -name "*.java" -exec sed -i 's/com.cvenhance.auth.dto.response.ErrorResponse/com.cvenhance.common.dto.ErrorResponse/g' {} +
find /home/msi/projects/cvenhance/app/auth-service/src/main/java/com/cvenhance/auth/ -type f -name "*.java" -exec sed -i 's/com.cvenhance.auth.dto.response.MessageResponse/com.cvenhance.common.dto.MessageResponse/g' {} +
find /home/msi/projects/cvenhance/app/auth-service/src/main/java/com/cvenhance/auth/ -type f -name "*.java" -exec sed -i 's/com.cvenhance.auth.dto.response.PageResponse/com.cvenhance.common.dto.PageResponse/g' {} +
find /home/msi/projects/cvenhance/app/auth-service/src/main/java/com/cvenhance/auth/ -type f -name "*.java" -exec sed -i 's/com.cvenhance.auth.dto.request.PageQuery/com.cvenhance.common.dto.PageQuery/g' {} +
find /home/msi/projects/cvenhance/app/auth-service/src/main/java/com/cvenhance/auth/ -type f -name "*.java" -exec sed -i 's/com.cvenhance.auth.util.PageUtil/com.cvenhance.common.util.PageUtil/g' {} +
find /home/msi/projects/cvenhance/app/auth-service/src/main/java/com/cvenhance/auth/ -type f -name "*.java" -exec sed -i 's/com.cvenhance.auth.service.AuditService/com.cvenhance.common.audit.AuditService/g' {} +
find /home/msi/projects/cvenhance/app/auth-service/src/main/java/com/cvenhance/auth/ -type f -name "*.java" -exec sed -i 's/com.cvenhance.auth.dto.constants.AuditAction/com.cvenhance.common.audit.AuditAction/g' {} +
find /home/msi/projects/cvenhance/app/auth-service/src/main/java/com/cvenhance/auth/ -type f -name "*.java" -exec sed -i 's/com.cvenhance.auth.entity.Auditable/com.cvenhance.common.audit.Auditable/g' {} +
find /home/msi/projects/cvenhance/app/auth-service/src/main/java/com/cvenhance/auth/ -type f -name "*.java" -exec sed -i 's/com.cvenhance.auth.config.AuditAspect/com.cvenhance.common.audit.AuditAspect/g' {} +
find /home/msi/projects/cvenhance/app/auth-service/src/main/java/com/cvenhance/auth/ -type f -name "*.java" -exec sed -i 's/com.cvenhance.auth.entity.AuditEvent/com.cvenhance.common.audit.AuditEvent/g' {} +
find /home/msi/projects/cvenhance/app/auth-service/src/main/java/com/cvenhance/auth/ -type f -name "*.java" -exec sed -i 's/com.cvenhance.auth.repo.AuditEventRepository/com.cvenhance.common.audit.AuditEventRepository/g' {} +
find /home/msi/projects/cvenhance/app/auth-service/src/main/java/com/cvenhance/auth/ -type f -name "*.java" -exec sed -i 's/com.cvenhance.auth.dto.constants.Role/com.cvenhance.auth.dto.Role/g' {} +
find /home/msi/projects/cvenhance/app/auth-service/src/main/java/com/cvenhance/auth/ -type f -name "*.java" -exec sed -i 's/com.cvenhance.auth.dto.request./com.cvenhance.auth.dto./g' {} +
find /home/msi/projects/cvenhance/app/auth-service/src/main/java/com/cvenhance/auth/ -type f -name "*.java" -exec sed -i 's/com.cvenhance.auth.dto.response./com.cvenhance.auth.dto./g' {} +
find /home/msi/projects/cvenhance/app/auth-service/src/main/java/com/cvenhance/auth/ -type f -name "*.java" -exec sed -i 's/com.cvenhance.auth.dto.constants./com.cvenhance.auth.dto./g' {} +
