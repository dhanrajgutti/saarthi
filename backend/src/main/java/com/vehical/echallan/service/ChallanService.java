package com.vehical.echallan.service;

import com.vehical.echallan.dto.ChallanCreateRequest;
import com.vehical.echallan.dto.ChallanResponse;
import com.vehical.echallan.dto.SuccessChallanDTO;
import com.vehical.echallan.model.*;
import com.vehical.echallan.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Year;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChallanService {

    private final ChallanRepository challanRepository;
    private final EvidenceRepository evidenceRepository;
    private final VehicleRepository vehicleRepository;
    private final OwnerRepository ownerRepository;
    private final PoliceOfficerRepository policeOfficerRepository;
    private final ViolationMasterRepository violationMasterRepository;

    @Transactional
    public synchronized String generateChallanNumber() {
        int year = Year.now().getValue();
        String prefix = "CH" + year;
        List<Challan> allYearChallans = challanRepository.findAll().stream()
                .filter(c -> c.getChallanNumber() != null && c.getChallanNumber().startsWith(prefix))
                .toList();

        int maxSeq = 0;
        for (Challan c : allYearChallans) {
            try {
                String suffix = c.getChallanNumber().substring(prefix.length());
                int seq = Integer.parseInt(suffix);
                if (seq > maxSeq) {
                    maxSeq = seq;
                }
            } catch (Exception ignored) {
            }
        }

        int nextSeq = maxSeq + 1;
        return prefix + String.format("%07d", nextSeq);
    }

    @Transactional
    public SuccessChallanDTO createChallan(ChallanCreateRequest req, String officerId) {
        if (req.getVehicleNo() == null || req.getVehicleNo().isBlank()) {
            throw new RuntimeException("Vehicle number is required");
        }
        if (req.getViolationIds() == null || req.getViolationIds().isEmpty()) {
            throw new RuntimeException("At least one violation is required");
        }

        String normalizedVehicleNo = normalizeVehicleNo(req.getVehicleNo());
        Vehicle vehicle = vehicleRepository.findById(normalizedVehicleNo)
                .orElseThrow(() -> new RuntimeException("Vehicle not found with number: " + normalizedVehicleNo));

        PoliceOfficer officer = policeOfficerRepository.findById(officerId)
                .orElseThrow(() -> new RuntimeException("Police officer not found with ID: " + officerId));

        int computedFine = 0;
        List<String> violationNames = new java.util.ArrayList<>();
        for (Integer vid : req.getViolationIds()) {
            ViolationMaster vm = violationMasterRepository.findById(vid)
                    .orElseThrow(() -> new RuntimeException("Violation not found with ID: " + vid));
            if (!vm.isActive()) {
                throw new RuntimeException("Violation is not active: " + vm.getViolationName());
            }
            computedFine += vm.getFineAmount();
            violationNames.add(vm.getViolationName());
        }

        if (req.getTotalFine() != null && !req.getTotalFine().equals(computedFine)) {
            throw new RuntimeException("Total fine mismatch. Expected: " + computedFine + ", Got: " + req.getTotalFine());
        }

        String challanNumber = generateChallanNumber();
        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();

        Challan challan = Challan.builder()
                .challanNumber(challanNumber)
                .vehicleNo(normalizedVehicleNo)
                .officerId(officerId)
                .violationIds(req.getViolationIds().stream().map(String::valueOf).collect(Collectors.joining(",")))
                .totalFineAmount(computedFine)
                .challanDate(today)
                .challanTime(now)
                .location(req.getLocation())
                .gpsCoords(req.getGpsCoords())
                .status(ChallanStatus.PENDING_PAYMENT)
                .remarks(req.getRemarks())
                .evidenceVehiclePhoto(req.getEvidenceVehiclePhoto())
                .evidencePlatePhoto(req.getEvidencePlatePhoto())
                .evidenceDriverPhoto(req.getEvidenceDriverPhoto())
                .paymentStatus("Unpaid")
                .build();

        Challan savedChallan = challanRepository.save(challan);

        Evidence evidence = Evidence.builder()
                .challanId(savedChallan.getChallanId())
                .vehiclePhoto(req.getEvidenceVehiclePhoto())
                .platePhoto(req.getEvidencePlatePhoto())
                .driverPhoto(req.getEvidenceDriverPhoto())
                .gpsLocation(req.getGpsCoords())
                .capturedTimestamp(LocalDateTime.now())
                .officerId(officerId)
                .policeStation(officer.getPoliceStation())
                .build();
        evidenceRepository.save(evidence);

        Owner owner = null;
        if (vehicle.getOwnerId() != null) {
            owner = ownerRepository.findById(vehicle.getOwnerId()).orElse(null);
        }

        return SuccessChallanDTO.builder()
                .challanId(savedChallan.getChallanId())
                .challanNumber(savedChallan.getChallanNumber())
                .vehicleNo(savedChallan.getVehicleNo())
                .ownerName(owner != null ? owner.getOwnerName() : "Unknown")
                .violations(violationNames)
                .totalFine(computedFine)
                .challanDate(today)
                .challanTime(now)
                .location(savedChallan.getLocation())
                .status(savedChallan.getStatus().name())
                .officerName(officer.getOfficerName())
                .policeStation(officer.getPoliceStation())
                .paymentStatus(savedChallan.getPaymentStatus())
                .paymentDueDate(today.plusDays(60))
                .qrCode("CHALLAN:" + challanNumber + ":" + normalizedVehicleNo)
                .build();
    }

    public ChallanResponse getChallanById(Long challanId) {
        Challan challan = challanRepository.findById(challanId)
                .orElseThrow(() -> new RuntimeException("Challan not found with ID: " + challanId));
        return toChallanResponse(challan);
    }

    public ChallanResponse getChallanByNumber(String challanNumber) {
        Challan challan = challanRepository.findByChallanNumber(challanNumber)
                .orElseThrow(() -> new RuntimeException("Challan not found with number: " + challanNumber));
        return toChallanResponse(challan);
    }

    public List<ChallanResponse> listChallansByVehicle(String vehicleNo) {
        String normalized = normalizeVehicleNo(vehicleNo);
        List<Challan> challans = challanRepository.findByVehicleNoOrderByCreatedAtDesc(normalized);
        return challans.stream().map(this::toChallanResponse).collect(Collectors.toList());
    }

    public List<ChallanResponse> listChallansByOfficer(String officerId) {
        List<Challan> challans = challanRepository.findByOfficerIdOrderByCreatedAtDesc(officerId);
        return challans.stream().map(this::toChallanResponse).collect(Collectors.toList());
    }

    @Transactional
    public ChallanResponse updateStatus(Long challanId, String newStatus) {
        Challan challan = challanRepository.findById(challanId)
                .orElseThrow(() -> new RuntimeException("Challan not found with ID: " + challanId));

        ChallanStatus status;
        try {
            status = ChallanStatus.valueOf(newStatus.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid challan status: " + newStatus);
        }

        challan.setStatus(status);
        if (status == ChallanStatus.PAID) {
            challan.setPaymentStatus("Paid");
            challan.setPaymentDate(LocalDate.now());
            challan.setPaymentReference("PAY-" + UUID.randomUUID().toString().substring(0, 12).toUpperCase());
        }
        Challan updated = challanRepository.save(challan);
        return toChallanResponse(updated);
    }

    private ChallanResponse toChallanResponse(Challan challan) {
        Vehicle vehicle = vehicleRepository.findById(challan.getVehicleNo()).orElse(null);
        Owner owner = null;
        if (vehicle != null && vehicle.getOwnerId() != null) {
            owner = ownerRepository.findById(vehicle.getOwnerId()).orElse(null);
        }

        PoliceOfficer officer = null;
        if (challan.getOfficerId() != null) {
            officer = policeOfficerRepository.findById(challan.getOfficerId()).orElse(null);
        }

        List<String> violationNames = getViolationNames(challan.getViolationIds());

        return ChallanResponse.builder()
                .challanId(challan.getChallanId())
                .challanNumber(challan.getChallanNumber())
                .vehicleNo(challan.getVehicleNo())
                .ownerName(owner != null ? owner.getOwnerName() : "Unknown")
                .violations(violationNames)
                .totalFine(challan.getTotalFineAmount())
                .challanDate(challan.getChallanDate())
                .challanTime(challan.getChallanTime())
                .location(challan.getLocation())
                .status(challan.getStatus().name())
                .officerName(officer != null ? officer.getOfficerName() : "Unknown")
                .policeStation(officer != null ? officer.getPoliceStation() : null)
                .paymentStatus(challan.getPaymentStatus())
                .build();
    }

    private List<String> getViolationNames(String violationIdsStr) {
        if (violationIdsStr == null || violationIdsStr.isBlank()) {
            return List.of();
        }
        try {
            return Arrays.stream(violationIdsStr.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .map(Integer::parseInt)
                    .map(id -> violationMasterRepository.findById(id).orElse(null))
                    .filter(vm -> vm != null)
                    .map(ViolationMaster::getViolationName)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            return List.of(violationIdsStr);
        }
    }

    private String normalizeVehicleNo(String vehicleNo) {
        if (vehicleNo == null || vehicleNo.isBlank()) {
            return vehicleNo;
        }
        String clean = vehicleNo.toUpperCase().replaceAll("[^A-Z0-9]", "");
        if (clean.length() < 10) {
            return vehicleNo.toUpperCase();
        }
        return clean.substring(0, 2) + "-" + clean.substring(2, 4) + " "
                + clean.substring(4, 6) + " " + clean.substring(6);
    }
}
