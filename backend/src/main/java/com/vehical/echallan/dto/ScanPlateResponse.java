package com.vehical.echallan.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScanPlateResponse {

    private String vehicleNumber;
    private Double confidencePercent;
    private boolean success;
    private String message;
}
