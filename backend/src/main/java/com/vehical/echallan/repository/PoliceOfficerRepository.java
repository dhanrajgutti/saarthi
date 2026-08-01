package com.vehical.echallan.repository;

import com.vehical.echallan.model.PoliceOfficer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PoliceOfficerRepository extends JpaRepository<PoliceOfficer, String> {

    List<PoliceOfficer> findByOfficerNameIgnoreCaseContaining(String officerName);

    List<PoliceOfficer> findByPoliceStation(String policeStation);

    Optional<PoliceOfficer> findByUserId(Long userId);
}
