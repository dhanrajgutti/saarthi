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
public class PreviousChallanSummary {

    private String challanNumber;
    private LocalDate challanDate;
    private String violations;
    private Integer totalFine;
    private String status;
}
