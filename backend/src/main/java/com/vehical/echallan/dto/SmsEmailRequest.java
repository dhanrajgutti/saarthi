package com.vehical.echallan.dto;

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
public class SmsEmailRequest {

    @NotNull(message = "Challan ID is required")
    private Long challanId;

    @NotEmpty(message = "At least one channel is required")
    private List<String> channels;
}
