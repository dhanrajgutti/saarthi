package com.vehical.echallan.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "evidence")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Evidence {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "evidence_id")
    private Long evidenceId;

    @Column(name = "challan_id", nullable = false)
    private Long challanId;

    @Column(name = "vehicle_photo", columnDefinition = "TEXT")
    private String vehiclePhoto;

    @Column(name = "plate_photo", columnDefinition = "TEXT")
    private String platePhoto;

    @Column(name = "driver_photo", columnDefinition = "TEXT")
    private String driverPhoto;

    @Column(columnDefinition = "TEXT")
    private String video;

    @Column(name = "gps_location", length = 100)
    private String gpsLocation;

    @Column(name = "captured_timestamp")
    private LocalDateTime capturedTimestamp;

    @Column(name = "officer_id", length = 20)
    private String officerId;

    @Column(name = "police_station", length = 100)
    private String policeStation;
}
