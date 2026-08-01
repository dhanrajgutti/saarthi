package com.vehical.auth.service;

import com.vehical.auth.config.JwtUtil;
import com.vehical.auth.dto.LoginRequest;
import com.vehical.auth.dto.LoginResponse;
import com.vehical.auth.model.User;
import com.vehical.auth.model.UserRole;
import com.vehical.auth.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public LoginResponse login(LoginRequest request) {
        try {
            Optional<User> userOpt;
            if (request.getRole() != null) {
                userOpt = userRepository.findByUsernameAndRole(request.getUsername(), request.getRole());
                if (userOpt.isEmpty()) {
                    throw new BadCredentialsException("Invalid credentials or role mismatch");
                }
            } else {
                userOpt = userRepository.findByUsername(request.getUsername());
                if (userOpt.isEmpty()) {
                    throw new BadCredentialsException("Invalid username or password");
                }
            }

            User user = userOpt.get();

            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );

            UserDetails userDetails = userDetailsService.loadUserByUsername(request.getUsername());
            String token = jwtUtil.generateToken(userDetails, user.getId(), user.getRole().name());

            return LoginResponse.builder()
                    .token(token)
                    .tokenType("Bearer")
                    .expiresIn(jwtUtil.getExpirationSeconds())
                    .userId(user.getId())
                    .username(user.getUsername())
                    .fullName(user.getFullName())
                    .email(user.getEmail())
                    .phone(user.getPhone())
                    .role(user.getRole())
                    .department(user.getDepartment())
                    .centerName(user.getCenterName())
                    .agencyName(user.getAgencyName())
                    .licenseNumber(user.getLicenseNumber())
                    .loginTime(LocalDateTime.now())
                    .build();

        } catch (DisabledException e) {
            throw new RuntimeException("User account is disabled");
        } catch (BadCredentialsException e) {
            throw new RuntimeException("Invalid username or password");
        }
    }

    public User getCurrentUser(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
