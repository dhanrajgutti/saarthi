package com.vehical.echallan.repository;

import com.vehical.echallan.model.Owner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OwnerRepository extends JpaRepository<Owner, Long> {

    Optional<Owner> findByMobile(String mobile);

    List<Owner> findByOwnerNameContainingIgnoreCase(String ownerName);
}
