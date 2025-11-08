package com.jbx.econtract.controller;

import com.jbx.econtract.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * REST Controller für Berichte
 */
@RestController
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Reports", description = "Berichte und Exports")
public class ReportController {

    private final ReportService reportService;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    // ========================================================================
    // VERTRAGSBERICHTE
    // ========================================================================

    @GetMapping("/contracts/pdf")
    @Operation(summary = "Vertragsbericht als PDF exportieren")
    public ResponseEntity<byte[]> exportContractsPDF() {
        log.info("GET /api/v1/reports/contracts/pdf");
        
        try {
            byte[] pdfBytes = reportService.generateContractReportPDF();
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", 
                    "Vertragsbericht_" + LocalDate.now().format(DATE_FORMATTER) + ".pdf");
            
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(pdfBytes);
        } catch (Exception e) {
            log.error("Error generating contract PDF report", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/contracts/excel")
    @Operation(summary = "Vertragsbericht als Excel exportieren")
    public ResponseEntity<byte[]> exportContractsExcel() {
        log.info("GET /api/v1/reports/contracts/excel");
        
        try {
            byte[] excelBytes = reportService.generateContractReportExcel();
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
            headers.setContentDispositionFormData("attachment", 
                    "Vertragsbericht_" + LocalDate.now().format(DATE_FORMATTER) + ".xlsx");
            
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(excelBytes);
        } catch (Exception e) {
            log.error("Error generating contract Excel report", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    // ========================================================================
    // COMPLIANCE-BERICHTE
    // ========================================================================

    @GetMapping("/compliance/pdf")
    @Operation(summary = "Compliance-Bericht als PDF exportieren")
    public ResponseEntity<byte[]> exportCompliancePDF() {
        log.info("GET /api/v1/reports/compliance/pdf");
        
        try {
            byte[] pdfBytes = reportService.generateComplianceReportPDF();
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", 
                    "Compliance-Bericht_" + LocalDate.now().format(DATE_FORMATTER) + ".pdf");
            
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(pdfBytes);
        } catch (Exception e) {
            log.error("Error generating compliance PDF report", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/compliance/excel")
    @Operation(summary = "Compliance-Bericht als Excel exportieren")
    public ResponseEntity<byte[]> exportComplianceExcel() {
        log.info("GET /api/v1/reports/compliance/excel");
        
        try {
            byte[] excelBytes = reportService.generateComplianceReportExcel();
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
            headers.setContentDispositionFormData("attachment", 
                    "Compliance-Bericht_" + LocalDate.now().format(DATE_FORMATTER) + ".xlsx");
            
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(excelBytes);
        } catch (Exception e) {
            log.error("Error generating compliance Excel report", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    // ========================================================================
    // FINANZBERICHTE
    // ========================================================================

    @GetMapping("/financial/pdf")
    @Operation(summary = "Finanzbericht als PDF exportieren")
    public ResponseEntity<byte[]> exportFinancialPDF() {
        log.info("GET /api/v1/reports/financial/pdf");
        
        try {
            byte[] pdfBytes = reportService.generateFinancialReportPDF();
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", 
                    "Finanzbericht_" + LocalDate.now().format(DATE_FORMATTER) + ".pdf");
            
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(pdfBytes);
        } catch (Exception e) {
            log.error("Error generating financial PDF report", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/financial/excel")
    @Operation(summary = "Finanzbericht als Excel exportieren")
    public ResponseEntity<byte[]> exportFinancialExcel() {
        log.info("GET /api/v1/reports/financial/excel");
        
        try {
            byte[] excelBytes = reportService.generateFinancialReportExcel();
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
            headers.setContentDispositionFormData("attachment", 
                    "Finanzbericht_" + LocalDate.now().format(DATE_FORMATTER) + ".xlsx");
            
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(excelBytes);
        } catch (Exception e) {
            log.error("Error generating financial Excel report", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    // ========================================================================
    // ZAHLUNGSBERICHTE
    // ========================================================================

    @GetMapping("/payments/pdf")
    @Operation(summary = "Zahlungsbericht als PDF exportieren")
    public ResponseEntity<byte[]> exportPaymentsPDF() {
        log.info("GET /api/v1/reports/payments/pdf");
        
        try {
            byte[] pdfBytes = reportService.generatePaymentReportPDF();
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", 
                    "Zahlungsbericht_" + LocalDate.now().format(DATE_FORMATTER) + ".pdf");
            
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(pdfBytes);
        } catch (Exception e) {
            log.error("Error generating payment PDF report", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/payments/excel")
    @Operation(summary = "Zahlungsbericht als Excel exportieren")
    public ResponseEntity<byte[]> exportPaymentsExcel() {
        log.info("GET /api/v1/reports/payments/excel");
        
        try {
            byte[] excelBytes = reportService.generatePaymentReportExcel();
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
            headers.setContentDispositionFormData("attachment", 
                    "Zahlungsbericht_" + LocalDate.now().format(DATE_FORMATTER) + ".xlsx");
            
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(excelBytes);
        } catch (Exception e) {
            log.error("Error generating payment Excel report", e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
