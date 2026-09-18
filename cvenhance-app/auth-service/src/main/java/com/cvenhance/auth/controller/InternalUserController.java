package com.cvenhance.auth.controller;

import com.cvenhance.auth.dto.UserResponse;
import com.cvenhance.auth.dtoApi.AuthDtoApi;
import com.cvenhance.auth.entity.AuthUser;
import com.cvenhance.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/internal/users")
@RequiredArgsConstructor
public class InternalUserController {

    private final AuthService authService;
    private final AuthDtoApi authDtoApi;

    @GetMapping("/{email}")
    public UserResponse getUserDetails(@PathVariable String email) {
        AuthUser user = authService.getActiveUser(email);
        return authDtoApi.toUserResponse(user);
    }

    @PatchMapping("/{email}/profile")
    public UserResponse updateProfile(@PathVariable String email, @RequestBody Map<String, Object> profile) {
        return authDtoApi.updateProfile(email, profile);
    }
}
