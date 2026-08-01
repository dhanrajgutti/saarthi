package com.vehical.echallan.service;

import com.vehical.echallan.dto.ChallanResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);

    private final ChallanService challanService;

    public boolean sendSms(Long challanId) {
        ChallanResponse challan = challanService.getChallanById(challanId);
        logger.info("========== SMS NOTIFICATION ==========");
        logger.info("To: Vehicle Owner of " + challan.getVehicleNo());
        logger.info("Message: Dear Vehicle Owner, a traffic challan has been issued against your vehicle "
                + challan.getVehicleNo() + ". Challan No: " + challan.getChallanNumber()
                + ", Fine Amount: Rs." + challan.getTotalFine()
                + ", Violations: " + String.join(", ", challan.getViolations())
                + ". Please pay within 60 days at the e-Challan portal.");
        logger.info("Status: SMS SENT (simulated)");
        logger.info("======================================");
        return true;
    }

    public boolean sendEmail(Long challanId) {
        ChallanResponse challan = challanService.getChallanById(challanId);
        logger.info("========== EMAIL NOTIFICATION ========");
        logger.info("To: Vehicle Owner of " + challan.getVehicleNo());
        logger.info("Subject: Traffic Challan Issued - " + challan.getChallanNumber());
        logger.info("Body:");
        logger.info("Dear Vehicle Owner,");
        logger.info("");
        logger.info("This is to inform you that a traffic challan has been issued.");
        logger.info("Challan Number: " + challan.getChallanNumber());
        logger.info("Vehicle Number: " + challan.getVehicleNo());
        logger.info("Date/Time: " + challan.getChallanDate() + " " + challan.getChallanTime());
        logger.info("Location: " + challan.getLocation());
        logger.info("Violations: " + String.join(", ", challan.getViolations()));
        logger.info("Total Fine: Rs." + challan.getTotalFine());
        logger.info("Issuing Officer: " + challan.getOfficerName() + " (" + challan.getPoliceStation() + ")");
        logger.info("");
        logger.info("Please visit the e-Challan payment portal to pay the fine within 60 days.");
        logger.info("Payment Link: https://echallan.example.com/pay/" + challan.getChallanNumber());
        logger.info("");
        logger.info("Regards,");
        logger.info("e-Challan System");
        logger.info("Status: EMAIL SENT (simulated)");
        logger.info("======================================");
        return true;
    }

    public boolean sendNotifications(Long challanId, List<String> channels) {
        if (channels == null || channels.isEmpty()) {
            throw new RuntimeException("At least one notification channel is required");
        }

        boolean allSuccess = true;
        for (String channel : channels) {
            try {
                String normalizedChannel = channel.trim().toUpperCase();
                switch (normalizedChannel) {
                    case "SMS":
                        sendSms(challanId);
                        break;
                    case "EMAIL":
                        sendEmail(challanId);
                        break;
                    default:
                        logger.warn("Unknown notification channel: " + channel);
                        allSuccess = false;
                }
            } catch (Exception e) {
                logger.error("Failed to send notification via " + channel + ": " + e.getMessage());
                allSuccess = false;
            }
        }
        return allSuccess;
    }
}
