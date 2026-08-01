package com.vehical.echallan.repository;

import com.vehical.echallan.model.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, String> {

    List<Vehicle> findByOwnerId(Long ownerId);

    List<Vehicle> findByVehicleType(String vehicleType);

    List<Vehicle> findByBlacklistedTrue();
}
