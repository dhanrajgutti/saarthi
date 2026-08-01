package com.vehical.auth.controller;

import com.vehical.auth.dto.ApiResponse;
import com.vehical.auth.dto.LoginRequest;
import com.vehical.auth.dto.LoginResponse;
import com.vehical.auth.model.User;
import com.vehical.auth.model.UserRole;
import com.vehical.auth.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            LoginResponse response = authService.login(loginRequest);
            return ResponseEntity.ok(ApiResponse.success("Login successful", response));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/roles")
    public ResponseEntity<ApiResponse<List<String>>> getAvailableRoles() {
        List<String> roles = Arrays.stream(UserRole.values())
                .map(Enum::name)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success("Available roles", roles));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<User>> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Not authenticated"));
        }
        String username = authentication.getName();
        User user = authService.getCurrentUser(username);
        user.setPassword(null);
        return ResponseEntity.ok(ApiResponse.success("Current user details", user));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<String>> logout() {
        SecurityContextHolder.clearContext();
        return ResponseEntity.ok(ApiResponse.success("Logged out successfully", null));
    }
}
