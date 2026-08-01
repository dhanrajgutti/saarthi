package com.vehical.echallan.controller;

import com.vehical.echallan.dto.*;
import com.vehical.echallan.service.ChallanService;
import com.vehical.echallan.service.NotificationService;
import com.vehical.echallan.service.PdfService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/challan")
@CrossOrigin(origins = "*", maxAge = 3600)
@RequiredArgsConstructor
public class ChallanController {

    private final ChallanService challanService;
    private final NotificationService notificationService;
    private final PdfService pdfService;

    @PostMapping
    public ResponseEntity<GenericApiResponse<SuccessChallanDTO>> createChallan(
            @Valid @RequestBody ChallanCreateRequest request) {
        try {
            String officerId = request.getOfficerId();
            SuccessChallanDTO response = challanService.createChallan(request, officerId);
            return ResponseEntity.ok(GenericApiResponse.success("Challan created successfully", response));
        } catch (RuntimeException e) {
            return ResponseEntity.ok(GenericApiResponse.error(e.getMessage(), "CHALLAN_CREATE_FAILED"));
        }
    }

    @GetMapping("/{challanIdOrNumber}")
    public ResponseEntity<GenericApiResponse<ChallanResponse>> getChallan(
            @PathVariable String challanIdOrNumber) {
        try {
            ChallanResponse response;
            if (challanIdOrNumber.matches("\\d+")) {
                response = challanService.getChallanById(Long.parseLong(challanIdOrNumber));
            } else {
                response = challanService.getChallanByNumber(challanIdOrNumber);
            }
            return ResponseEntity.ok(GenericApiResponse.success("Challan details retrieved", response));
        } catch (RuntimeException e) {
            return ResponseEntity.ok(GenericApiResponse.error(e.getMessage(), "CHALLAN_NOT_FOUND"));
        }
    }

    @GetMapping("/{challanId}/pdf")
    public ResponseEntity<byte[]> getChallanPdf(@PathVariable Long challanId) {
        try {
            byte[] pdfBytes = pdfService.generateChallanPdfBytes(challanId);
            ChallanResponse challan = challanService.getChallanById(challanId);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment",
                    "challan-" + challan.getChallanNumber() + ".pdf");
            headers.setContentLength(pdfBytes.length);

            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PostMapping("/send-sms")
    public ResponseEntity<GenericApiResponse<String>> sendSms(
            @Valid @RequestBody SmsEmailRequest request) {
        try {
            List<String> channels = List.of("SMS");
            notificationService.sendNotifications(request.getChallanId(), channels);
            return ResponseEntity.ok(GenericApiResponse.success("SMS sent successfully", "SMS_SENT"));
        } catch (RuntimeException e) {
            return ResponseEntity.ok(GenericApiResponse.error(e.getMessage(), "SMS_SEND_FAILED"));
        }
    }

    @PostMapping("/send-email")
    public ResponseEntity<GenericApiResponse<String>> sendEmail(
            @Valid @RequestBody SmsEmailRequest request) {
        try {
            List<String> channels = List.of("EMAIL");
            notificationService.sendNotifications(request.getChallanId(), channels);
            return ResponseEntity.ok(GenericApiResponse.success("Email sent successfully", "EMAIL_SENT"));
        } catch (RuntimeException e) {
            return ResponseEntity.ok(GenericApiResponse.error(e.getMessage(), "EMAIL_SEND_FAILED"));
        }
    }

    @GetMapping("/vehicle/{vehicleNo}")
    public ResponseEntity<GenericApiResponse<List<ChallanResponse>>> listChallansByVehicle(
            @PathVariable String vehicleNo) {
        try {
            List<ChallanResponse> responses = challanService.listChallansByVehicle(vehicleNo);
            return ResponseEntity.ok(GenericApiResponse.success(
                    "Challans for vehicle retrieved", responses));
        } catch (RuntimeException e) {
            return ResponseEntity.ok(GenericApiResponse.error(e.getMessage(), "CHALLAN_LIST_FAILED"));
        }
    }

    @GetMapping("/officer/{officerId}")
    public ResponseEntity<GenericApiResponse<List<ChallanResponse>>> listChallansByOfficer(
            @PathVariable String officerId) {
        try {
            List<ChallanResponse> responses = challanService.listChallansByOfficer(officerId);
            return ResponseEntity.ok(GenericApiResponse.success(
                    "Challans for officer retrieved", responses));
        } catch (RuntimeException e) {
            return ResponseEntity.ok(GenericApiResponse.error(e.getMessage(), "CHALLAN_LIST_FAILED"));
        }
    }
}
