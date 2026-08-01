package com.vehical.echallan.service;

import com.vehical.echallan.dto.ChallanResponse;
import com.vehical.echallan.model.Challan;
import com.vehical.echallan.repository.ChallanRepository;
import com.vehical.echallan.repository.ViolationMasterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PdfService {

    private final ChallanService challanService;
    private final ChallanRepository challanRepository;
    private final ViolationMasterRepository violationMasterRepository;

    public byte[] generateChallanPdfBytes(Long challanId) {
        ChallanResponse challan = challanService.getChallanById(challanId);
        Challan rawChallan = challanRepository.findById(challanId).orElse(null);

        try {
            return generateWithOpenPdf(challan, rawChallan);
        } catch (Exception e) {
            return generateTextReportBytes(challan);
        }
    }

    private byte[] generateWithOpenPdf(ChallanResponse challan, Challan rawChallan) throws Exception {
        Class<?> documentClass = Class.forName("com.lowagie.text.Document");
        Class<?> pageSizeClass = Class.forName("com.lowagie.text.PageSize");
        Class<?> paragraphClass = Class.forName("com.lowagie.text.Paragraph");
        Class<?> pdfWriterClass = Class.forName("com.lowagie.text.pdf.PdfWriter");
        Class<?> fontClass = Class.forName("com.lowagie.text.Font");
        Class<?> fontFactoryClass = Class.forName("com.lowagie.text.FontFactory");
        Class<?> elementClass = Class.forName("com.lowagie.text.Element");
        Class<?> phraseClass = Class.forName("com.lowagie.text.Phrase");
        Class<?> chunkClass = Class.forName("com.lowagie.text.Chunk");

        Object document = documentClass.getDeclaredConstructor(pageSizeClass).newInstance(
                pageSizeClass.getField("A4").get(null)
        );
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        pdfWriterClass.getMethod("getInstance", documentClass, java.io.OutputStream.class)
                .invoke(null, document, baos);

        documentClass.getMethod("open").invoke(document);

        Object titleFont = fontFactoryClass.getMethod("getFont", String.class, float.class, int.class)
                .invoke(null, "Helvetica-Bold", 18f, fontClass.getField("UNDERLINE").getInt(null));
        Object headingFont = fontFactoryClass.getMethod("getFont", String.class, float.class, int.class)
                .invoke(null, "Helvetica-Bold", 14f, fontClass.getField("NORMAL").getInt(null));
        Object normalFont = fontFactoryClass.getMethod("getFont", String.class, float.class)
                .invoke(null, "Helvetica", 11f);
        Object smallFont = fontFactoryClass.getMethod("getFont", String.class, float.class)
                .invoke(null, "Helvetica", 10f);

        Object title = paragraphClass.getDeclaredConstructor(String.class, fontClass)
                .newInstance("E-CHALLAN REPORT", titleFont);
        paragraphClass.getMethod("setAlignment", int.class).invoke(title, elementClass.getField("ALIGN_CENTER").getInt(null));
        paragraphClass.getMethod("setSpacingAfter", float.class).invoke(title, 20f);
        documentClass.getMethod("add", elementClass).invoke(document, title);

        Object challanInfo = paragraphClass.getDeclaredConstructor(String.class, headingFont)
                .newInstance("Challan Information:", headingFont);
        paragraphClass.getMethod("setSpacingAfter", float.class).invoke(challanInfo, 10f);
        documentClass.getMethod("add", elementClass).invoke(document, challanInfo);

        addInfoLine(document, paragraphClass, normalFont, "Challan Number:", challan.getChallanNumber());
        addInfoLine(document, paragraphClass, normalFont, "Challan Date:", challan.getChallanDate() != null ? challan.getChallanDate().toString() : "N/A");
        addInfoLine(document, paragraphClass, normalFont, "Challan Time:", challan.getChallanTime() != null ? challan.getChallanTime().toString() : "N/A");
        addInfoLine(document, paragraphClass, normalFont, "Location:", challan.getLocation() != null ? challan.getLocation() : "N/A");
        addInfoLine(document, paragraphClass, normalFont, "Status:", challan.getStatus() != null ? challan.getStatus() : "N/A");
        addInfoLine(document, paragraphClass, normalFont, "Payment Status:", challan.getPaymentStatus() != null ? challan.getPaymentStatus() : "N/A");

        addSpacing(document, paragraphClass, 15f);

        Object vehicleInfo = paragraphClass.getDeclaredConstructor(String.class, headingFont)
                .newInstance("Vehicle Information:", headingFont);
        paragraphClass.getMethod("setSpacingAfter", float.class).invoke(vehicleInfo, 10f);
        documentClass.getMethod("add", elementClass).invoke(document, vehicleInfo);

        addInfoLine(document, paragraphClass, normalFont, "Vehicle Number:", challan.getVehicleNo());
        addInfoLine(document, paragraphClass, normalFont, "Owner Name:", challan.getOwnerName() != null ? challan.getOwnerName() : "N/A");

        addSpacing(document, paragraphClass, 15f);

        Object violationsInfo = paragraphClass.getDeclaredConstructor(String.class, headingFont)
                .newInstance("Violations Committed:", headingFont);
        paragraphClass.getMethod("setSpacingAfter", float.class).invoke(violationsInfo, 10f);
        documentClass.getMethod("add", elementClass).invoke(document, violationsInfo);

        if (challan.getViolations() != null && !challan.getViolations().isEmpty()) {
            for (int i = 0; i < challan.getViolations().size(); i++) {
                addInfoLine(document, paragraphClass, normalFont,
                        "  " + (i + 1) + ".", challan.getViolations().get(i));
            }
        }
        addInfoLine(document, paragraphClass, normalFont, "Total Fine Amount:", "Rs. " + challan.getTotalFine());

        addSpacing(document, paragraphClass, 15f);

        Object officerInfo = paragraphClass.getDeclaredConstructor(String.class, headingFont)
                .newInstance("Issuing Authority:", headingFont);
        paragraphClass.getMethod("setSpacingAfter", float.class).invoke(officerInfo, 10f);
        documentClass.getMethod("add", elementClass).invoke(document, officerInfo);

        addInfoLine(document, paragraphClass, normalFont, "Officer Name:", challan.getOfficerName() != null ? challan.getOfficerName() : "N/A");
        addInfoLine(document, paragraphClass, normalFont, "Police Station:", challan.getPoliceStation() != null ? challan.getPoliceStation() : "N/A");

        addSpacing(document, paragraphClass, 20f);

        Object paymentNote = paragraphClass.getDeclaredConstructor(String.class, smallFont)
                .newInstance("Payment Note: Please pay the fine amount within 60 days from the date of challan issuance. "
                        + "Visit the official e-Challan portal to make the payment online or visit the nearest police station.", smallFont);
        paragraphClass.getMethod("setSpacingAfter", float.class).invoke(paymentNote, 10f);
        documentClass.getMethod("add", elementClass).invoke(document, paymentNote);

        LocalDate dueDate = challan.getChallanDate() != null ? challan.getChallanDate().plusDays(60) : LocalDate.now().plusDays(60);
        Object paymentLink = paragraphClass.getDeclaredConstructor(String.class, smallFont)
                .newInstance("Payment Link: https://echallan.example.com/pay/" + challan.getChallanNumber()
                        + " | Due Date: " + dueDate.format(DateTimeFormatter.ISO_LOCAL_DATE), smallFont);
        paragraphClass.getMethod("setSpacingAfter", float.class).invoke(paymentLink, 20f);
        documentClass.getMethod("add", elementClass).invoke(document, paymentLink);

        Object footer = paragraphClass.getDeclaredConstructor(String.class, smallFont)
                .newInstance("This is a system generated challan report. For official records only.", smallFont);
        paragraphClass.getMethod("setAlignment", int.class).invoke(footer, elementClass.getField("ALIGN_CENTER").getInt(null));
        documentClass.getMethod("add", elementClass).invoke(document, footer);

        documentClass.getMethod("close").invoke(document);
        return baos.toByteArray();
    }

    private void addInfoLine(Object document, Class<?> paragraphClass, Object font, String label, String value) throws Exception {
        Object line = paragraphClass.getDeclaredConstructor().newInstance();
        Class<?> phraseClass = Class.forName("com.lowagie.text.Phrase");
        Class<?> chunkClass = Class.forName("com.lowagie.text.Chunk");
        Class<?> fontClass = Class.forName("com.lowagie.text.Font");
        Class<?> fontFactoryClass = Class.forName("com.lowagie.text.FontFactory");
        Object boldFont = fontFactoryClass.getMethod("getFont", String.class, float.class, int.class)
                .invoke(null, "Helvetica-Bold", 11f, fontClass.getField("NORMAL").getInt(null));

        Object labelChunk = chunkClass.getDeclaredConstructor(String.class, fontClass)
                .newInstance(label + " ", boldFont);
        Object valueChunk = chunkClass.getDeclaredConstructor(String.class, fontClass)
                .newInstance(value != null ? value : "N/A", font);

        phraseClass.getMethod("add", chunkClass).invoke(
                phraseClass.cast(line),
                labelChunk
        );
        phraseClass.getMethod("add", chunkClass).invoke(
                phraseClass.cast(line),
                valueChunk
        );

        paragraphClass.getMethod("setSpacingAfter", float.class).invoke(line, 5f);
        document.getClass().getMethod("add", Class.forName("com.lowagie.text.Element")).invoke(document, line);
    }

    private void addSpacing(Object document, Class<?> paragraphClass, float spacing) throws Exception {
        Object space = paragraphClass.getDeclaredConstructor(String.class)
                .newInstance(" ");
        paragraphClass.getMethod("setSpacingAfter", float.class).invoke(space, spacing);
        document.getClass().getMethod("add", Class.forName("com.lowagie.text.Element")).invoke(document, space);
    }

    private byte[] generateTextReportBytes(ChallanResponse challan) {
        StringBuilder sb = new StringBuilder();
        sb.append("===========================================\n");
        sb.append("          E-CHALLAN REPORT\n");
        sb.append("===========================================\n\n");
        sb.append("Challan Information:\n");
        sb.append("  Challan Number: ").append(challan.getChallanNumber()).append("\n");
        sb.append("  Challan Date: ").append(challan.getChallanDate()).append("\n");
        sb.append("  Challan Time: ").append(challan.getChallanTime()).append("\n");
        sb.append("  Location: ").append(challan.getLocation()).append("\n");
        sb.append("  Status: ").append(challan.getStatus()).append("\n");
        sb.append("  Payment Status: ").append(challan.getPaymentStatus()).append("\n\n");
        sb.append("Vehicle Information:\n");
        sb.append("  Vehicle Number: ").append(challan.getVehicleNo()).append("\n");
        sb.append("  Owner Name: ").append(challan.getOwnerName()).append("\n\n");
        sb.append("Violations Committed:\n");
        if (challan.getViolations() != null) {
            for (int i = 0; i < challan.getViolations().size(); i++) {
                sb.append("  ").append(i + 1).append(". ").append(challan.getViolations().get(i)).append("\n");
            }
        }
        sb.append("\nTotal Fine Amount: Rs. ").append(challan.getTotalFine()).append("\n\n");
        sb.append("Issuing Authority:\n");
        sb.append("  Officer Name: ").append(challan.getOfficerName()).append("\n");
        sb.append("  Police Station: ").append(challan.getPoliceStation()).append("\n\n");
        LocalDate dueDate = challan.getChallanDate() != null ? challan.getChallanDate().plusDays(60) : LocalDate.now().plusDays(60);
        sb.append("Payment Note: Please pay within 60 days. Due Date: ").append(dueDate).append("\n");
        sb.append("Payment Link: https://echallan.example.com/pay/").append(challan.getChallanNumber()).append("\n");
        sb.append("===========================================\n");
        return sb.toString().getBytes(StandardCharsets.UTF_8);
    }
}
