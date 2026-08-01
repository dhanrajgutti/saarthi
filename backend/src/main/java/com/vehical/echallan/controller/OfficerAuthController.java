package com.vehical.echallan.controller;

import com.vehical.auth.config.JwtUtil;
import com.vehical.auth.model.User;
import com.vehical.auth.repository.UserRepository;
import com.vehical.echallan.dto.GenericApiResponse;
import com.vehical.echallan.dto.LoginRequest;
import com.vehical.echallan.dto.OfficerDetailsDTO;
import com.vehical.echallan.dto.OfficerLoginResponse;
import com.vehical.echallan.service.OfficerAuthService;
import io.jsonwebtoken.Claims;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/officer")
@CrossOrigin(origins = "*", maxAge = 3600)
@RequiredArgsConstructor
public class OfficerAuthController {

    private final OfficerAuthService officerAuthService;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    @PostMapping("/login")
    public ResponseEntity<GenericApiResponse<OfficerLoginResponse>> login(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            OfficerLoginResponse response = officerAuthService.login(loginRequest);
            return ResponseEntity.ok(GenericApiResponse.success("Officer login successful", response));
        } catch (RuntimeException e) {
            return ResponseEntity.ok(GenericApiResponse.error(e.getMessage(), "AUTH_FAILED"));
        }
    }

    @GetMapping("/me")
    public ResponseEntity<GenericApiResponse<OfficerDetailsDTO>> getCurrentOfficer(
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        try {
            Long userId;
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                Claims claims = extractAllClaims(token);
                userId = claims.get("userId", Long.class);
            } else if (authentication != null && authentication.isAuthenticated()
                    && !"anonymousUser".equals(authentication.getPrincipal())) {
                String username = authentication.getName();
                User user = userRepository.findByUsername(username)
                        .orElseThrow(() -> new RuntimeException("User not found"));
                userId = user.getId();
            } else {
                return ResponseEntity.ok(GenericApiResponse.error("Not authenticated", "NOT_AUTHENTICATED"));
            }

            OfficerDetailsDTO officer = officerAuthService.getOfficerByUserId(userId);
            return ResponseEntity.ok(GenericApiResponse.success("Officer details retrieved", officer));
        } catch (RuntimeException e) {
            return ResponseEntity.ok(GenericApiResponse.error(e.getMessage(), "OFFICER_NOT_FOUND"));
        }
    }

    private Claims extractAllClaims(String token) {
        try {
            java.lang.reflect.Method method = jwtUtil.getClass().getSuperclass() != null
                    ? findExtractClaimsMethod(jwtUtil.getClass())
                    : null;
            if (method != null) {
                method.setAccessible(true);
                return (Claims) method.invoke(jwtUtil, token);
            }
        } catch (Exception ignored) {
        }
        try {
            io.jsonwebtoken.JwtParser parser = io.jsonwebtoken.Jwts.parser()
                    .verifyWith(getSigningKeyViaReflection())
                    .build();
            return parser.parseSignedClaims(token).getPayload();
        } catch (Exception e) {
            throw new RuntimeException("Invalid token");
        }
    }

    private javax.crypto.SecretKey getSigningKeyViaReflection() throws Exception {
        java.lang.reflect.Field field = JwtUtil.class.getDeclaredField("jwtSecret");
        field.setAccessible(true);
        String jwtSecret = (String) field.get(jwtUtil);
        byte[] keyBytes = jwtSecret.getBytes(java.nio.charset.StandardCharsets.UTF_8);
        return io.jsonwebtoken.security.Keys.hmacShaKeyFor(keyBytes);
    }

    private java.lang.reflect.Method findExtractClaimsMethod(Class<?> clazz) {
        for (java.lang.reflect.Method m : clazz.getDeclaredMethods()) {
            if (m.getParameterCount() == 1 && Claims.class.isAssignableFrom(m.getReturnType())) {
                return m;
            }
        }
        return null;
    }
}
