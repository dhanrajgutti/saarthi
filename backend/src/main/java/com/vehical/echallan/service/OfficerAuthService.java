package com.vehical.echallan.service;

import com.vehical.auth.config.JwtUtil;
import com.vehical.auth.model.User;
import com.vehical.auth.model.UserRole;
import com.vehical.auth.repository.UserRepository;
import com.vehical.auth.service.CustomUserDetailsService;
import com.vehical.echallan.dto.LoginRequest;
import com.vehical.echallan.dto.OfficerDetailsDTO;
import com.vehical.echallan.dto.OfficerLoginResponse;
import com.vehical.echallan.model.PoliceOfficer;
import com.vehical.echallan.repository.PoliceOfficerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OfficerAuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService userDetailsService;
    private final UserRepository userRepository;
    private final PoliceOfficerRepository policeOfficerRepository;

    public OfficerLoginResponse login(LoginRequest request) {
        try {
            User user = userRepository.findByUsername(request.getUsername())
                    .orElseThrow(() -> new BadCredentialsException("Invalid username or password"));

            if (user.getRole() != UserRole.POLICE) {
                throw new BadCredentialsException("User is not authorized as Police Officer");
            }

            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );

            PoliceOfficer officer = policeOfficerRepository.findByUserId(user.getId())
                    .orElseThrow(() -> new RuntimeException("Police officer profile not found for this user"));

            UserDetails userDetails = userDetailsService.loadUserByUsername(request.getUsername());
            String token = jwtUtil.generateToken(userDetails, user.getId(), user.getRole().name());

            return OfficerLoginResponse.builder()
                    .officerId(officer.getOfficerId())
                    .officerName(officer.getOfficerName())
                    .badgeNo(officer.getBadgeNo())
                    .policeStation(officer.getPoliceStation())
                    .role(officer.getRole())
                    .token(token)
                    .username(user.getUsername())
                    .build();

        } catch (DisabledException e) {
            throw new RuntimeException("User account is disabled");
        } catch (BadCredentialsException e) {
            throw new RuntimeException(e.getMessage() != null ? e.getMessage() : "Invalid username or password");
        }
    }

    public OfficerDetailsDTO getOfficerByUserId(Long userId) {
        PoliceOfficer officer = policeOfficerRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Police officer not found for userId: " + userId));

        return OfficerDetailsDTO.builder()
                .officerId(officer.getOfficerId())
                .officerName(officer.getOfficerName())
                .badgeNo(officer.getBadgeNo())
                .policeStation(officer.getPoliceStation())
                .role(officer.getRole())
                .build();
    }
}
