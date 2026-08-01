package com.vehical.echallan.controller;

import com.vehical.echallan.dto.GenericApiResponse;
import com.vehical.echallan.dto.ViolationDTO;
import com.vehical.echallan.service.ViolationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/violations")
@CrossOrigin(origins = "*", maxAge = 3600)
@RequiredArgsConstructor
public class ViolationController {

    private final ViolationService violationService;

    @GetMapping
    public ResponseEntity<GenericApiResponse<List<ViolationDTO>>> getAllViolations() {
        try {
            List<ViolationDTO> violations = violationService.getAllViolations();
            return ResponseEntity.ok(GenericApiResponse.success("Violations list retrieved", violations));
        } catch (RuntimeException e) {
            return ResponseEntity.ok(GenericApiResponse.error(e.getMessage(), "VIOLATIONS_FETCH_FAILED"));
        }
    }
}
