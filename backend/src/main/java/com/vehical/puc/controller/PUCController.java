package com.vehical.puc.controller;

import com.vehical.echallan.dto.GenericApiResponse;
import com.vehical.echallan.dto.VehicleLookupResponse;
import com.vehical.puc.dto.PUCCreateRequest;
import com.vehical.puc.dto.PUCResponse;
import com.vehical.puc.service.PUCService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/puc")
@CrossOrigin(origins = "*", maxAge = 3600)
@RequiredArgsConstructor
public class PUCController {

    private final PUCService pucService;

    @PostMapping("/issue")
    public ResponseEntity<GenericApiResponse<PUCResponse>> issueCertificate(
            @RequestBody PUCCreateRequest request) {
        return ResponseEntity.ok(pucService.createCertificate(request));
    }

    @GetMapping("/cert/{certificateNumber}")
    public ResponseEntity<GenericApiResponse<PUCResponse>> getCertificate(
            @PathVariable String certificateNumber) {
        return ResponseEntity.ok(pucService.getByCertificateNumber(certificateNumber));
    }

    @GetMapping("/vehicle/{vehicleNo}")
    public ResponseEntity<GenericApiResponse<List<PUCResponse>>> getByVehicle(
            @PathVariable String vehicleNo) {
        return ResponseEntity.ok(pucService.getByVehicleNo(vehicleNo));
    }

    @GetMapping("/center/{centerId}")
    public ResponseEntity<GenericApiResponse<List<PUCResponse>>> getByCenter(
            @PathVariable String centerId) {
        return ResponseEntity.ok(pucService.getByCenterId(centerId));
    }

    @GetMapping("/search-vehicle/{vehicleNo}")
    public ResponseEntity<GenericApiResponse<VehicleLookupResponse>> searchVehicle(
            @PathVariable String vehicleNo) {
        return ResponseEntity.ok(pucService.searchVehicle(vehicleNo));
    }
}
