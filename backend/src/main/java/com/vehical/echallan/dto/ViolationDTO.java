package com.vehical.echallan.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ViolationDTO {

    private Integer violationId;
    private String violationName;
    private String mvActSection;
    private Integer fineAmount;
}
