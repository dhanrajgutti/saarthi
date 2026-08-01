package com.vehical.echallan.dto;

import com.vehical.echallan.model.OfficerRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OfficerDetailsDTO {

    private String officerId;
    private String officerName;
    private String badgeNo;
    private String policeStation;
    private OfficerRole role;
}
