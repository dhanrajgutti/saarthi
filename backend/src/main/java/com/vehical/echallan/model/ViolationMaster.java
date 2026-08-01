package com.vehical.echallan.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "violation_master")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ViolationMaster {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "violation_id")
    private Integer violationId;

    @Column(name = "violation_name", nullable = false, unique = true, length = 100)
    private String violationName;

    @Column(name = "mv_act_section", length = 20)
    private String mvActSection;

    @Column(name = "fine_amount", nullable = false)
    private Integer fineAmount;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "is_active")
    @Builder.Default
    private boolean isActive = true;
}
