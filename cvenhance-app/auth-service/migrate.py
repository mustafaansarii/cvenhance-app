import os
import re
import shutil

source_dir = "/home/msi/projects/cvenhance/doc-service/src/main/java/com/docservice/careerhub/"
dest_dir = "/home/msi/projects/cvenhance/app/auth-service/src/main/java/com/cvenhance/auth/"
common_pkg = "com.cvenhance.common."
auth_pkg = "com.cvenhance.auth."

files_to_copy = [
    ("service/AuthService.java", "service/AuthService.java"),
    ("entity/AuthUser.java", "entity/AuthUser.java"),
    ("entity/UserSession.java", "entity/UserSession.java"),
    ("repo/AuthUserRepository.java", "repo/AuthUserRepository.java"),
    ("repo/UserSessionRepository.java", "repo/UserSessionRepository.java"),
    ("security/CustomOAuth2UserService.java", "security/CustomOAuth2UserService.java"),
    ("security/OAuth2LoginSuccessHandler.java", "security/OAuth2LoginSuccessHandler.java"),
    ("security/OAuth2LoginFailureHandler.java", "security/OAuth2LoginFailureHandler.java"),
    ("security/CookieAuthorizationRequestRepository.java", "security/CookieAuthorizationRequestRepository.java"),
    ("security/RequestMetadataExtractor.java", "security/RequestMetadataExtractor.java"),
    ("config/SecurityConfig.java", "security/SecurityConfig.java"), # moved to security? no, keep in config maybe. Wait, instructions say "Security (change package to com.cvenhance.auth.security): SecurityConfig...". Okay.
    ("config/AppProperties.java", "config/AppProperties.java"),
    ("config/RoleEndpointAccessLoader.java", "config/RoleEndpointAccessLoader.java"),
    ("controller/AuthController.java", "controller/AuthController.java"),
    ("controller/ContactController.java", "controller/ContactController.java"),
    ("controller/HealthController.java", "controller/HealthController.java"),
    ("dtoApi/AuthDtoApi.java", "dtoApi/AuthDtoApi.java"),
    ("dtoApi/ContactDtoApi.java", "dtoApi/ContactDtoApi.java"),
    ("service/MailService.java", "service/MailService.java"),
    ("service/OtpMailer.java", "service/OtpMailer.java"),
    ("service/AccountMailer.java", "service/AccountMailer.java"),
    ("service/ContactMailer.java", "service/ContactMailer.java"),
    ("util/OtpGenerator.java", "util/OtpGenerator.java"),
    ("util/EmailBodies.java", "util/EmailBodies.java"),
    ("util/AbstractDtoUtil.java", "util/AbstractDtoUtil.java"),
    ("dto/constants/Role.java", "dto/Role.java"),
    ("dto/request/SignupRequest.java", "dto/SignupRequest.java"),
    ("dto/request/SigninRequest.java", "dto/SigninRequest.java"),
    ("dto/request/DeviceMetadata.java", "dto/DeviceMetadata.java"),
    ("dto/request/ContactRequest.java", "dto/ContactRequest.java"),
    ("dto/request/MailRequest.java", "dto/MailRequest.java"),
    ("dto/request/AdminUserUpdate.java", "dto/AdminUserUpdate.java"),
    ("dto/request/AssignResumeRequest.java", "dto/AssignResumeRequest.java"),
    ("dto/response/UserResponse.java", "dto/UserResponse.java"),
    ("dto/response/AdminUserDto.java", "dto/AdminUserDto.java")
]

def migrate_content(content, filename):
    # Change main package
    content = re.sub(r'package com\.docservice\.careerhub\.[a-zA-Z0-9.]+;', lambda m: 'package com.cvenhance.auth.' + (
        'security;' if 'SecurityConfig.java' in filename else
        'dto;' if '/dto/' in filename else
        m.group(0).split('.')[-1]
    ), content)
    
    # Change imports
    content = content.replace("com.docservice.careerhub.", "com.cvenhance.auth.")
    
    # Replace common imports
    common_replacements = {
        "com.cvenhance.auth.exception.ApiException": "com.cvenhance.common.exception.ApiException",
        "com.cvenhance.auth.exception.GlobalExceptionHandler": "com.cvenhance.common.exception.GlobalExceptionHandler",
        "com.cvenhance.auth.security.JwtService": "com.cvenhance.common.security.JwtService",
        "com.cvenhance.auth.security.JwtAuthenticationFilter": "com.cvenhance.common.security.JwtAuthenticationFilter",
        "com.cvenhance.auth.security.AuthCookies": "com.cvenhance.common.security.AuthCookies",
        "com.cvenhance.auth.dto.response.ErrorResponse": "com.cvenhance.common.dto.ErrorResponse",
        "com.cvenhance.auth.dto.response.MessageResponse": "com.cvenhance.common.dto.MessageResponse",
        "com.cvenhance.auth.dto.response.PageResponse": "com.cvenhance.common.dto.PageResponse",
        "com.cvenhance.auth.dto.request.PageQuery": "com.cvenhance.common.dto.PageQuery",
        "com.cvenhance.auth.util.PageUtil": "com.cvenhance.common.util.PageUtil",
        "com.cvenhance.auth.service.AuditService": "com.cvenhance.common.audit.AuditService",
        "com.cvenhance.auth.dto.constants.AuditAction": "com.cvenhance.common.audit.AuditAction",
        "com.cvenhance.auth.entity.Auditable": "com.cvenhance.common.audit.Auditable",
        "com.cvenhance.auth.config.AuditAspect": "com.cvenhance.common.audit.AuditAspect",
        "com.cvenhance.auth.entity.AuditEvent": "com.cvenhance.common.audit.AuditEvent",
        "com.cvenhance.auth.repo.AuditEventRepository": "com.cvenhance.common.audit.AuditEventRepository",
        "com.cvenhance.auth.dto.constants.Role": "com.cvenhance.auth.dto.Role",
        "com.cvenhance.auth.dto.request.": "com.cvenhance.auth.dto.",
        "com.cvenhance.auth.dto.response.": "com.cvenhance.auth.dto."
    }
    for old, new in common_replacements.items():
        content = content.replace(old, new)

    return content

for src, dest in files_to_copy:
    src_path = os.path.join(source_dir, src)
    dest_path = os.path.join(dest_dir, dest)
    
    if os.path.exists(src_path):
        os.makedirs(os.path.dirname(dest_path), exist_ok=True)
        with open(src_path, "r") as f:
            content = f.read()
            content = migrate_content(content, dest)
        with open(dest_path, "w") as f:
            f.write(content)

print("Migration script executed")
