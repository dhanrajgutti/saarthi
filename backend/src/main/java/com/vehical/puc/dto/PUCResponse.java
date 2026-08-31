package com.vehical.puc.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PUCResponse {

    private Long pucId;
    private String certificateNumber;
    private String vehicleNo;
    private String ownerName;
    private String vehicleType;
    private String fuelType;
    private LocalDate registrationDate;
    private String engineNumber;
    private String chassisNumber;
    private String centerId;
    private String centerName;
    private LocalDateTime testDateTime;
    private String emissionNorms;
    private BigDecimal odometerKm;
    private BigDecimal coPercent;
    private BigDecimal hcPpm;
    private BigDecimal co2Percent;
    private BigDecimal lambdaValue;
    private String pollutionUnderControl;
    private LocalDate validFrom;
    private LocalDate validUpto;
    private String status;
    private String remarks;
    private Long issuedByUserId;
    private String issuedByName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
