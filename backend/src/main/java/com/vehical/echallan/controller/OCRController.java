package com.vehical.echallan.controller;

import com.vehical.echallan.dto.GenericApiResponse;
import com.vehical.echallan.dto.ScanPlateRequest;
import com.vehical.echallan.dto.ScanPlateResponse;
import com.vehical.echallan.service.OCRService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequiredArgsConstructor
public class OCRController {

    private final OCRService ocrService;

    @PostMapping("/scan-plate")
    public ResponseEntity<GenericApiResponse<ScanPlateResponse>> scanPlate(
            @Valid @RequestBody ScanPlateRequest request) {
        try {
            ScanPlateResponse response = ocrService.detectPlateFromImage(request.getPlateImage());
            if (response.isSuccess()) {
                return ResponseEntity.ok(GenericApiResponse.success(
                        response.getMessage() != null ? response.getMessage() : "Plate scanned successfully",
                        response));
            } else {
                return ResponseEntity.ok(GenericApiResponse.error(
                        response.getMessage() != null ? response.getMessage() : "Scan failed",
                        "SCAN_FAILED"));
            }
        } catch (RuntimeException e) {
            return ResponseEntity.ok(GenericApiResponse.error(e.getMessage(), "SCAN_ERROR"));
        }
    }
}
