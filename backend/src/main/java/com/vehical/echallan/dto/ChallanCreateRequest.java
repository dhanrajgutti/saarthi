package com.vehical.echallan.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChallanCreateRequest {

    @NotBlank(message = "Vehicle number is required")
    private String vehicleNo;

    @NotEmpty(message = "At least one violation is required")
    private List<Integer> violationIds;

    @NotNull(message = "Total fine is required")
    private Integer totalFine;

    @NotBlank(message = "Location is required")
    private String location;

    private String gpsCoords;
    private String remarks;
    private String evidenceVehiclePhoto;
    private String evidencePlatePhoto;
    private String evidenceDriverPhoto;

    @NotBlank(message = "Officer ID is required")
    private String officerId;
}
