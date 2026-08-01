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

@Entity
@Table(name = "vehicle")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Vehicle {

    @Id
    @Column(name = "vehicle_no", length = 20)
    private String vehicleNo;

    @Column(name = "owner_id")
    private Long ownerId;

    @Column(name = "vehicle_type", length = 50)
    private String vehicleType;

    @Column(length = 50)
    private String make;

    @Column(length = 50)
    private String model;

    @Column(name = "fuel_type", length = 20)
    private String fuelType;

    @Column(name = "registration_date")
    private LocalDate registrationDate;

    @Column(name = "registration_state", length = 50)
    private String registrationState;

    @Column(name = "insurance_status", length = 20)
    private String insuranceStatus;

    @Column(name = "puc_status", length = 20)
    private String pucStatus;

    @Column(name = "rc_status", length = 20)
    private String rcStatus;

    @Column(name = "engine_number", length = 50)
    private String engineNumber;

    @Column(name = "chassis_number", length = 50)
    private String chassisNumber;

    @Column(name = "is_blacklisted")
    @Builder.Default
    private boolean blacklisted = false;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
