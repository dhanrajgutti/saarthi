package com.vehical.echallan.repository;

import com.vehical.echallan.model.Challan;
import com.vehical.echallan.model.ChallanStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChallanRepository extends JpaRepository<Challan, Long> {

    Optional<Challan> findByChallanNumber(String challanNumber);

    List<Challan> findByVehicleNoOrderByCreatedAtDesc(String vehicleNo);

    long countByVehicleNoAndStatus(String vehicleNo, ChallanStatus status);

    @Query("SELECT COALESCE(SUM(c.totalFineAmount), 0) FROM Challan c WHERE c.vehicleNo = :vehicleNo AND c.status = :status")
    Integer sumTotalFineByVehicleNoAndStatus(@Param("vehicleNo") String vehicleNo, @Param("status") ChallanStatus status);

    List<Challan> findByOfficerIdOrderByCreatedAtDesc(String officerId);

    boolean existsByVehicleNoAndStatus(String vehicleNo, ChallanStatus status);
}
