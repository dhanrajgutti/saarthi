package com.vehical.echallan.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "police_officer")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PoliceOfficer {

    @Id
    @Column(name = "officer_id", length = 20)
    private String officerId;

    @Column(name = "officer_name", nullable = false, length = 100)
    private String officerName;

    @Column(name = "badge_no", nullable = false, length = 20)
    private String badgeNo;

    @Column(name = "police_station", length = 100)
    private String policeStation;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OfficerRole role;

    @Column(name = "user_id")
    private Long userId;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
