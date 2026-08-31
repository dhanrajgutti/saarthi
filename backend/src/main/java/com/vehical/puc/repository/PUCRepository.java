package com.vehical.puc.repository;

import com.vehical.puc.model.PUCCertificate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface PUCRepository extends JpaRepository<PUCCertificate, Long> {

    Optional<PUCCertificate> findByCertificateNumber(String certificateNumber);

    List<PUCCertificate> findByVehicleNoOrderByCreatedAtDesc(String vehicleNo);

    List<PUCCertificate> findByCenterIdOrderByCreatedAtDesc(String centerId);

    List<PUCCertificate> findByIssuedByUserIdOrderByCreatedAtDesc(Long userId);

    List<PUCCertificate> findByValidUptoBeforeAndStatus(LocalDate date, String status);

    long countByStatus(String status);

    @Query("SELECT COUNT(p) FROM PUCCertificate p WHERE p.centerId = :centerId AND DATE(p.createdAt) = CURRENT_DATE")
    long countTodayTestsByCenterId(@Param("centerId") String centerId);

    @Query("SELECT COUNT(p) FROM PUCCertificate p WHERE p.centerId = :centerId AND p.pollutionUnderControl = 'Yes' AND DATE(p.createdAt) = CURRENT_DATE")
    long countTodayPassByCenterId(@Param("centerId") String centerId);

    @Query("SELECT COALESCE(COUNT(p), 0) FROM PUCCertificate p WHERE DATE(p.createdAt) BETWEEN :from AND :to")
    long countByDateRange(@Param("from") LocalDate from, @Param("to") LocalDate to);

    boolean existsByVehicleNoAndStatus(String vehicleNo, String status);
}
