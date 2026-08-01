package com.vehical.echallan.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "challan")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Challan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "challan_id")
    private Long challanId;

    @Column(name = "challan_number", nullable = false, unique = true, length = 20)
    private String challanNumber;

    @Column(name = "vehicle_no", nullable = false, length = 20)
    private String vehicleNo;

    @Column(name = "officer_id", length = 20)
    private String officerId;

    @Column(name = "violation_ids", columnDefinition = "TEXT")
    private String violationIds;

    @Column(name = "total_fine_amount", nullable = false)
    private Integer totalFineAmount;

    @Column(name = "challan_date", nullable = false)
    private LocalDate challanDate;

    @Column(name = "challan_time", nullable = false)
    private LocalTime challanTime;

    @Column(nullable = false, length = 255)
    private String location;

    @Column(name = "gps_coords", length = 100)
    private String gpsCoords;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private ChallanStatus status = ChallanStatus.PENDING_PAYMENT;

    @Column(columnDefinition = "TEXT", length = 500)
    private String remarks;

    @Column(name = "evidence_vehicle_photo", columnDefinition = "TEXT")
    private String evidenceVehiclePhoto;

    @Column(name = "evidence_plate_photo", columnDefinition = "TEXT")
    private String evidencePlatePhoto;

    @Column(name = "evidence_driver_photo", columnDefinition = "TEXT")
    private String evidenceDriverPhoto;

    @Column(name = "evidence_video", columnDefinition = "TEXT")
    private String evidenceVideo;

    @Column(name = "payment_status", length = 20)
    @Builder.Default
    private String paymentStatus = "Unpaid";

    @Column(name = "payment_date")
    private LocalDate paymentDate;

    @Column(name = "payment_reference", length = 100)
    private String paymentReference;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
