package com.vehical.echallan.service;

import com.vehical.echallan.dto.ViolationDTO;
import com.vehical.echallan.model.ViolationMaster;
import com.vehical.echallan.repository.ViolationMasterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ViolationService {

    private final ViolationMasterRepository violationMasterRepository;

    public List<ViolationDTO> getAllViolations() {
        return violationMasterRepository.findByIsActiveTrue().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public Integer calculateFine(List<Integer> violationIds) {
        if (violationIds == null || violationIds.isEmpty()) {
            throw new RuntimeException("At least one violation ID is required");
        }

        int total = 0;
        for (Integer id : violationIds) {
            ViolationMaster vm = violationMasterRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Violation not found with ID: " + id));
            if (!vm.isActive()) {
                throw new RuntimeException("Violation is not active: " + vm.getViolationName());
            }
            total += vm.getFineAmount();
        }
        return total;
    }

    public ViolationMaster getById(Integer id) {
        return violationMasterRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Violation not found with ID: " + id));
    }

    private ViolationDTO toDTO(ViolationMaster vm) {
        return ViolationDTO.builder()
                .violationId(vm.getViolationId())
                .violationName(vm.getViolationName())
                .mvActSection(vm.getMvActSection())
                .fineAmount(vm.getFineAmount())
                .build();
    }
}
