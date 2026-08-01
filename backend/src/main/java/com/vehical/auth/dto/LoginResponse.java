package com.vehical.auth.dto;

import com.vehical.auth.model.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {

    private String token;
    private String tokenType;
    private Long expiresIn;
    private Long userId;
    private String username;
    private String fullName;
    private String email;
    private String phone;
    private UserRole role;
    private String department;
    private String centerName;
    private String agencyName;
    private String licenseNumber;
    private LocalDateTime loginTime;
}
