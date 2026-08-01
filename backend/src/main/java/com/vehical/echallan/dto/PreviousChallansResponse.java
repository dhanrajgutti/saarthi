package com.vehical.echallan.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PreviousChallansResponse {

    private Integer totalChallans;
    private Integer pendingChallans;
    private Integer totalPendingFine;
    private List<PreviousChallanSummary> challans;
}
