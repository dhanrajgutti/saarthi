package com.vehical.echallan.controller;

import com.vehical.echallan.dto.GenericApiResponse;
import com.vehical.echallan.dto.PreviousChallansResponse;
import com.vehical.echallan.dto.VehicleLookupResponse;
import com.vehical.echallan.service.VehicleLookupService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/vehicle")
@CrossOrigin(origins = "*", maxAge = 3600)
@RequiredArgsConstructor
public class VehicleController {

    private final VehicleLookupService vehicleLookupService;

    @GetMapping("/{vehicleNumber}")
    public ResponseEntity<GenericApiResponse<VehicleLookupResponse>> getVehicle(
            @PathVariable String vehicleNumber) {
        try {
            VehicleLookupResponse response = vehicleLookupService.getVehicleByNo(vehicleNumber);
            return ResponseEntity.ok(GenericApiResponse.success("Vehicle details retrieved", response));
        } catch (RuntimeException e) {
            return ResponseEntity.ok(GenericApiResponse.error(e.getMessage(), "VEHICLE_NOT_FOUND"));
        }
    }

    @GetMapping("/{vehicleNumber}/challans")
    public ResponseEntity<GenericApiResponse<PreviousChallansResponse>> getVehicleChallans(
            @PathVariable String vehicleNumber) {
        try {
            PreviousChallansResponse response = vehicleLookupService.getPreviousChallans(vehicleNumber);
            return ResponseEntity.ok(GenericApiResponse.success("Previous challans retrieved", response));
        } catch (RuntimeException e) {
            return ResponseEntity.ok(GenericApiResponse.error(e.getMessage(), "VEHICLE_NOT_FOUND"));
        }
    }
}
