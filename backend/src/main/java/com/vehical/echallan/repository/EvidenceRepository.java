package com.vehical.echallan.repository;

import com.vehical.echallan.model.Evidence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EvidenceRepository extends JpaRepository<Evidence, Long> {

    List<Evidence> findByChallanId(Long challanId);
}
