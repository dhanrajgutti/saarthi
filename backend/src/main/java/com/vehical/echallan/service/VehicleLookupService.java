package com.vehical.echallan.service;

import com.vehical.echallan.dto.PreviousChallanSummary;
import com.vehical.echallan.dto.PreviousChallansResponse;
import com.vehical.echallan.dto.VehicleLookupResponse;
import com.vehical.echallan.model.Challan;
import com.vehical.echallan.model.ChallanStatus;
import com.vehical.echallan.model.Owner;
import com.vehical.echallan.model.Vehicle;
import com.vehical.echallan.model.ViolationMaster;
import com.vehical.echallan.repository.ChallanRepository;
import com.vehical.echallan.repository.OwnerRepository;
import com.vehical.echallan.repository.VehicleRepository;
import com.vehical.echallan.repository.ViolationMasterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VehicleLookupService {

    private final VehicleRepository vehicleRepository;
    private final OwnerRepository ownerRepository;
    private final ChallanRepository challanRepository;
    private final ViolationMasterRepository violationMasterRepository;

    public String normalizeVehicleNo(String vehicleNo) {
        if (vehicleNo == null || vehicleNo.isBlank()) {
            return vehicleNo;
        }
        String clean = vehicleNo.toUpperCase().replaceAll("[^A-Z0-9]", "");
        if (clean.length() < 10) {
            return vehicleNo.toUpperCase();
        }
        StringBuilder sb = new StringBuilder();
        sb.append(clean, 0, 2);
        sb.append("-");
        sb.append(clean, 2, 4);
        sb.append(" ");
        sb.append(clean, 4, 6);
        sb.append(" ");
        sb.append(clean.substring(6));
        return sb.toString();
    }

    public VehicleLookupResponse getVehicleByNo(String vehicleNo) {
        String normalized = normalizeVehicleNo(vehicleNo);

        Vehicle vehicle = vehicleRepository.findById(normalized)
                .orElseThrow(() -> new RuntimeException("Vehicle not found with number: " + normalized));

        Owner owner = null;
        if (vehicle.getOwnerId() != null) {
            owner = ownerRepository.findById(vehicle.getOwnerId()).orElse(null);
        }

        List<Challan> allChallans = challanRepository.findByVehicleNoOrderByCreatedAtDesc(normalized);
        int pendingCount = (int) challanRepository.countByVehicleNoAndStatus(normalized, ChallanStatus.PENDING_PAYMENT);
        Integer totalPendingFine = challanRepository.sumTotalFineByVehicleNoAndStatus(normalized, ChallanStatus.PENDING_PAYMENT);
        if (totalPendingFine == null) {
            totalPendingFine = 0;
        }

        return VehicleLookupResponse.builder()
                .vehicleNo(vehicle.getVehicleNo())
                .ownerId(vehicle.getOwnerId())
                .ownerName(owner != null ? owner.getOwnerName() : null)
                .vehicleType(vehicle.getVehicleType())
                .make(vehicle.getMake())
                .model(vehicle.getModel())
                .fuelType(vehicle.getFuelType())
                .registrationDate(vehicle.getRegistrationDate())
                .registrationState(vehicle.getRegistrationState())
                .insuranceStatus(vehicle.getInsuranceStatus())
                .pucStatus(vehicle.getPucStatus())
                .rcStatus(vehicle.getRcStatus())
                .engineNumber(vehicle.getEngineNumber())
                .chassisNumber(vehicle.getChassisNumber())
                .ownerMobile(owner != null ? owner.getMobile() : null)
                .ownerAddress(owner != null ? owner.getAddress() : null)
                .blacklisted(vehicle.isBlacklisted())
                .previousChallansCount(allChallans.size())
                .pendingChallansCount(pendingCount)
                .totalPendingFine(totalPendingFine)
                .build();
    }

    public PreviousChallansResponse getPreviousChallans(String vehicleNo) {
        String normalized = normalizeVehicleNo(vehicleNo);

        if (!vehicleRepository.existsById(normalized)) {
            throw new RuntimeException("Vehicle not found with number: " + normalized);
        }

        List<Challan> allChallans = challanRepository.findByVehicleNoOrderByCreatedAtDesc(normalized);
        int pendingCount = (int) challanRepository.countByVehicleNoAndStatus(normalized, ChallanStatus.PENDING_PAYMENT);
        Integer totalPendingFine = challanRepository.sumTotalFineByVehicleNoAndStatus(normalized, ChallanStatus.PENDING_PAYMENT);
        if (totalPendingFine == null) {
            totalPendingFine = 0;
        }

        List<PreviousChallanSummary> summaries = allChallans.stream()
                .map(challan -> {
                    String violationsText = getViolationsText(challan.getViolationIds());
                    return PreviousChallanSummary.builder()
                            .challanNumber(challan.getChallanNumber())
                            .challanDate(challan.getChallanDate())
                            .violations(violationsText)
                            .totalFine(challan.getTotalFineAmount())
                            .status(challan.getStatus().name())
                            .build();
                })
                .collect(Collectors.toList());

        return PreviousChallansResponse.builder()
                .totalChallans(allChallans.size())
                .pendingChallans(pendingCount)
                .totalPendingFine(totalPendingFine)
                .challans(summaries)
                .build();
    }

    private String getViolationsText(String violationIdsStr) {
        if (violationIdsStr == null || violationIdsStr.isBlank()) {
            return "";
        }
        try {
            List<Integer> ids = Arrays.stream(violationIdsStr.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .map(Integer::parseInt)
                    .collect(Collectors.toList());

            return ids.stream()
                    .map(id -> violationMasterRepository.findById(id).orElse(null))
                    .filter(vm -> vm != null)
                    .map(ViolationMaster::getViolationName)
                    .collect(Collectors.joining(", "));
        } catch (Exception e) {
            return violationIdsStr;
        }
    }
}
