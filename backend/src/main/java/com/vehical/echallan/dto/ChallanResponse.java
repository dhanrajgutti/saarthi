package com.vehical.echallan.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChallanResponse {

    private Long challanId;
    private String challanNumber;
    private String vehicleNo;
    private String ownerName;
    private List<String> violations;
    private Integer totalFine;
    private LocalDate challanDate;
    private LocalTime challanTime;
    private String location;
    private String status;
    private String officerName;
    private String policeStation;
    private String paymentStatus;
}
