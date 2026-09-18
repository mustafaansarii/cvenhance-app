package com.cvenhance.auth.dto;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.List;

@Data
@Builder
public class AdminUserDto {
    private Long id;
    private String email;
    private String fullName;
    private boolean verified;
    private String provider;
    private List<Role> roles;
    private Instant createdAt;
}
