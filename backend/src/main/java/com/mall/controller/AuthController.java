package com.mall.controller;

import com.mall.common.ApiResponse;
import com.mall.dto.Dtos;
import com.mall.security.UserPrincipal;
import com.mall.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    public ApiResponse<Dtos.AuthResponse> register(@Valid @RequestBody Dtos.RegisterRequest request) {
        return ApiResponse.success(authService.register(request));
    }

    @PostMapping("/login")
    public ApiResponse<Dtos.AuthResponse> login(@Valid @RequestBody Dtos.LoginRequest request) {
        return ApiResponse.success(authService.login(request));
    }

    @GetMapping("/me")
    public ApiResponse<Dtos.UserView> me(@AuthenticationPrincipal UserPrincipal principal) {
        return ApiResponse.success(authService.currentUser(principal.getId()));
    }
}
