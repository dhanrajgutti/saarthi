package com.vehical.puc.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "puc_certificate")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PUCCertificate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "puc_id")
    private Long pucId;

    @Column(name = "certificate_number", nullable = false, unique = true, length = 30)
    private String certificateNumber;

    @Column(name = "vehicle_no", nullable = false, length = 20)
    private String vehicleNo;

    @Column(name = "owner_name", length = 150)
    private String ownerName;

    @Column(name = "vehicle_type", length = 60)
    private String vehicleType;

    @Column(name = "fuel_type", length = 30)
    private String fuelType;

    @Column(name = "registration_date")
    private LocalDate registrationDate;

    @Column(name = "engine_number", length = 50)
    private String engineNumber;

    @Column(name = "chassis_number", length = 50)
    private String chassisNumber;

    @Column(name = "center_id", length = 60)
    private String centerId;

    @Column(name = "center_name", length = 150)
    private String centerName;

    @Column(name = "test_date_time", nullable = false)
    private LocalDateTime testDateTime;

    @Column(name = "emission_norms", length = 30)
    @Builder.Default
    private String emissionNorms = "BHARAT STAGE VI";

    @Column(name = "odometer_km", precision = 10, scale = 1)
    private BigDecimal odometerKm;

    @Column(name = "co_percent", precision = 5, scale = 2)
    private BigDecimal coPercent;

    @Column(name = "hc_ppm", precision = 6, scale = 2)
    private BigDecimal hcPpm;

    @Column(name = "co2_percent", precision = 5, scale = 2)
    private BigDecimal co2Percent;

    @Column(name = "lambda_value", precision = 4, scale = 2)
    private BigDecimal lambdaValue;

    @Column(name = "pollution_under_control", nullable = false, length = 5)
    @Builder.Default
    private String pollutionUnderControl = "Yes";

    @Column(name = "valid_from", nullable = false)
    private LocalDate validFrom;

    @Column(name = "valid_upto", nullable = false)
    private LocalDate validUpto;

    @Column(name = "status", length = 20)
    @Builder.Default
    private String status = "Valid";

    @Column(name = "remarks", columnDefinition = "TEXT", length = 500)
    private String remarks;

    @Column(name = "issued_by_user_id")
    private Long issuedByUserId;

    @Column(name = "issued_by_name", length = 100)
    private String issuedByName;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
