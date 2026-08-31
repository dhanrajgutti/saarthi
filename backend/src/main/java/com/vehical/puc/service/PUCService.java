package com.vehical.puc.service;

import com.vehical.echallan.dto.GenericApiResponse;
import com.vehical.echallan.dto.VehicleLookupResponse;
import com.vehical.echallan.model.Owner;
import com.vehical.echallan.model.Vehicle;
import com.vehical.echallan.repository.OwnerRepository;
import com.vehical.echallan.repository.VehicleRepository;
import com.vehical.puc.dto.PUCCreateRequest;
import com.vehical.puc.dto.PUCResponse;
import com.vehical.puc.model.PUCCertificate;
import com.vehical.puc.repository.PUCRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PUCService {

    private final PUCRepository pucRepository;
    private final VehicleRepository vehicleRepository;
    private final OwnerRepository ownerRepository;

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("ddMMyy");

    @Transactional
    public GenericApiResponse<PUCResponse> createCertificate(PUCCreateRequest req) {
        try {
            String certNo = generateCertificateNumber(
                    req.getVehicleNo(),
                    req.getTestDateTime() != null ? req.getTestDateTime() : LocalDateTime.now(),
                    req.getCenterId()
            );

            PUCCertificate cert = PUCCertificate.builder()
                    .certificateNumber(certNo)
                    .vehicleNo(req.getVehicleNo())
                    .ownerName(req.getOwnerName())
                    .vehicleType(req.getVehicleType())
                    .fuelType(req.getFuelType())
                    .registrationDate(req.getRegistrationDate() != null ?
                            parseDate(req.getRegistrationDate()) : null)
                    .engineNumber(req.getEngineNumber())
                    .chassisNumber(req.getChassisNumber())
                    .centerId(req.getCenterId())
                    .centerName(req.getCenterName())
                    .testDateTime(req.getTestDateTime() != null ? req.getTestDateTime() : LocalDateTime.now())
                    .emissionNorms(req.getEmissionNorms() != null ? req.getEmissionNorms() : "BHARAT STAGE VI")
                    .odometerKm(req.getOdometerKm())
                    .coPercent(req.getCoPercent())
                    .hcPpm(req.getHcPpm())
                    .co2Percent(req.getCo2Percent())
                    .lambdaValue(req.getLambdaValue())
                    .pollutionUnderControl(req.getPollutionUnderControl() != null ? req.getPollutionUnderControl() : "Yes")
                    .validFrom(req.getValidFrom() != null ? req.getValidFrom() : LocalDate.now())
                    .validUpto(req.getValidUpto())
                    .status("Yes".equalsIgnoreCase(req.getPollutionUnderControl()) ? "Valid" : "Fail")
                    .remarks(req.getRemarks())
                    .issuedByUserId(req.getIssuedByUserId())
                    .issuedByName(req.getIssuedByName())
                    .build();

            cert = pucRepository.save(cert);

            try {
                Vehicle veh = vehicleRepository.findById(req.getVehicleNo()).orElse(null);
                if (veh != null) {
                    veh.setPucStatus("Yes".equalsIgnoreCase(cert.getPollutionUnderControl()) ? "Valid" : "Expired");
                    vehicleRepository.save(veh);
                }
            } catch (Exception ignored) {}

            return GenericApiResponse.success("PUC Certificate issued successfully", toResponse(cert));
        } catch (Exception e) {
            return GenericApiResponse.error("Failed to issue PUC Certificate: " + e.getMessage(), "PUC_CREATE_ERROR");
        }
    }

    public GenericApiResponse<PUCResponse> getByCertificateNumber(String certNo) {
        Optional<PUCCertificate> opt = pucRepository.findByCertificateNumber(certNo);
        if (opt.isEmpty()) {
            return GenericApiResponse.error("PUC Certificate not found", "NOT_FOUND");
        }
        return GenericApiResponse.success("Certificate retrieved", toResponse(opt.get()));
    }

    public GenericApiResponse<List<PUCResponse>> getByVehicleNo(String vehicleNo) {
        List<PUCCertificate> list = pucRepository.findByVehicleNoOrderByCreatedAtDesc(vehicleNo);
        return GenericApiResponse.success(
                "Found " + list.size() + " certificate(s) for vehicle",
                list.stream().map(this::toResponse).collect(Collectors.toList())
        );
    }

    public GenericApiResponse<List<PUCResponse>> getByCenterId(String centerId) {
        List<PUCCertificate> list = pucRepository.findByCenterIdOrderByCreatedAtDesc(centerId);
        return GenericApiResponse.success(
                "Found " + list.size() + " certificate(s) for center",
                list.stream().map(this::toResponse).collect(Collectors.toList())
        );
    }

    public GenericApiResponse<VehicleLookupResponse> searchVehicle(String vehicleNo) {
        try {
            Optional<Vehicle> vehOpt = vehicleRepository.findById(vehicleNo);
            if (vehOpt.isEmpty()) {
                String normalized = vehicleNo.replace("-", " ");
                vehOpt = vehicleRepository.findById(normalized);
                if (vehOpt.isEmpty()) {
                    String noSpaces = vehicleNo.replace(" ", "-");
                    vehOpt = vehicleRepository.findById(noSpaces);
                }
            }
            if (vehOpt.isEmpty()) {
                return GenericApiResponse.error("Vehicle not found with number: " + vehicleNo, "VEHICLE_NOT_FOUND");
            }
            Vehicle v = vehOpt.get();
            VehicleLookupResponse.VehicleLookupResponseBuilder b = VehicleLookupResponse.builder()
                    .vehicleNo(v.getVehicleNo())
                    .ownerId(v.getOwnerId())
                    .vehicleType(v.getVehicleType())
                    .make(v.getMake())
                    .model(v.getModel())
                    .fuelType(v.getFuelType())
                    .registrationDate(v.getRegistrationDate())
                    .registrationState(v.getRegistrationState())
                    .insuranceStatus(v.getInsuranceStatus())
                    .pucStatus(v.getPucStatus())
                    .rcStatus(v.getRcStatus())
                    .engineNumber(v.getEngineNumber())
                    .chassisNumber(v.getChassisNumber())
                    .blacklisted(v.isBlacklisted());

            if (v.getOwnerId() != null) {
                Owner o = ownerRepository.findById(v.getOwnerId()).orElse(null);
                if (o != null) {
                    b.ownerName(o.getOwnerName());
                    b.ownerMobile(o.getMobile());
                    b.ownerAddress(o.getAddress());
                }
            }
            return GenericApiResponse.success("Vehicle found", b.build());
        } catch (Exception e) {
            return GenericApiResponse.error("Vehicle lookup failed: " + e.getMessage(), "LOOKUP_ERROR");
        }
    }

    private String generateCertificateNumber(String vehicleNo, LocalDateTime dt, String centerId) {
        String vn = (vehicleNo != null ? vehicleNo : "MH13").replaceAll("[^A-Za-z0-9]", "").toUpperCase();
        if (vn.length() < 4) vn = "MH13" + vn;
        String prefix = vn.substring(0, 4);
        String datePart = dt.format(DATE_FMT);
        String ce = (centerId != null ? centerId : "PUC10025").replaceAll("\\D", "");
        if (ce.length() < 4) ce = String.format("%04d", Integer.parseInt(ce.isEmpty() ? "25" : ce));
        else ce = ce.substring(ce.length() - 4);
        Random r = new Random();
        String rand = String.format("%03d", r.nextInt(1000));
        return (prefix + datePart + "P" + ce + rand).toUpperCase();
    }

    private LocalDate parseDate(String s) {
        if (s == null) return null;
        String[] parts = s.split("/");
        if (parts.length == 3) {
            try {
                int d = Integer.parseInt(parts[0]);
                int m = Integer.parseInt(parts[1]);
                int y = Integer.parseInt(parts[2]);
                return LocalDate.of(y, m, d);
            } catch (Exception ignored) {}
        }
        try { return LocalDate.parse(s); } catch (Exception ignored) {}
        return null;
    }

    private PUCResponse toResponse(PUCCertificate c) {
        return PUCResponse.builder()
                .pucId(c.getPucId())
                .certificateNumber(c.getCertificateNumber())
                .vehicleNo(c.getVehicleNo())
                .ownerName(c.getOwnerName())
                .vehicleType(c.getVehicleType())
                .fuelType(c.getFuelType())
                .registrationDate(c.getRegistrationDate())
                .engineNumber(c.getEngineNumber())
                .chassisNumber(c.getChassisNumber())
                .centerId(c.getCenterId())
                .centerName(c.getCenterName())
                .testDateTime(c.getTestDateTime())
                .emissionNorms(c.getEmissionNorms())
                .odometerKm(c.getOdometerKm())
                .coPercent(c.getCoPercent())
                .hcPpm(c.getHcPpm())
                .co2Percent(c.getCo2Percent())
                .lambdaValue(c.getLambdaValue())
                .pollutionUnderControl(c.getPollutionUnderControl())
                .validFrom(c.getValidFrom())
                .validUpto(c.getValidUpto())
                .status(c.getStatus())
                .remarks(c.getRemarks())
                .issuedByUserId(c.getIssuedByUserId())
                .issuedByName(c.getIssuedByName())
                .createdAt(c.getCreatedAt())
                .updatedAt(c.getUpdatedAt())
                .build();
    }
}
