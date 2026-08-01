package com.vehical.echallan.repository;

import com.vehical.echallan.model.ViolationMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ViolationMasterRepository extends JpaRepository<ViolationMaster, Integer> {

    List<ViolationMaster> findByIsActiveTrue();

    Optional<ViolationMaster> findByViolationNameIgnoreCase(String violationName);
}
