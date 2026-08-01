package com.vehical.echallan.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VehicleLookupResponse {

    private String vehicleNo;
    private Long ownerId;
    private String ownerName;
    private String vehicleType;
    private String make;
    private String model;
    private String fuelType;
    private LocalDate registrationDate;
    private String registrationState;
    private String insuranceStatus;
    private String pucStatus;
    private String rcStatus;
    private String engineNumber;
    private String chassisNumber;
    private String ownerMobile;
    private String ownerAddress;
    private boolean blacklisted;
    private Integer previousChallansCount;
    private Integer pendingChallansCount;
    private Integer totalPendingFine;
}
