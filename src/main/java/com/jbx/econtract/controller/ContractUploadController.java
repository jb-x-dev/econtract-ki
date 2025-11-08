package com.jbx.econtract.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jbx.econtract.model.entity.Contract;
import com.jbx.econtract.model.entity.ContractUpload;
import com.jbx.econtract.service.ContractUploadService;
import com.jbx.econtract.service.RevenueImportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

/**
 * Contract Upload Controller
 * Handles contract file upload, AI extraction, and revenue import
 */
@RestController
@RequestMapping("/api/v1/contracts/upload")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Contract Upload", description = "Automated contract upload and AI extraction")
@CrossOrigin(origins = "*")
public class ContractUploadController {
    
    private final ContractUploadService contractUploadService;
    private final RevenueImportService revenueImportService;
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    /**
     * Upload contract file
     */
    @PostMapping
    @Operation(summary = "Upload contract file for AI extraction")
    public ResponseEntity<Map<String, Object>> uploadContract(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "userId", defaultValue = "1") Long userId) {
        
        log.info("POST /api/v1/contracts/upload - file: {}", file.getOriginalFilename());
        
        try {
            ContractUpload upload = contractUploadService.uploadContract(file, userId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("uploadId", upload.getId());
            response.put("status", upload.getUploadStatus().name());
            response.put("message", "Contract uploaded successfully. Processing...");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error uploading contract", e);
            
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Upload failed");
            error.put("message", e.getMessage());
            
            return ResponseEntity.internalServerError().body(error);
        }
    }
    
    /**
     * Get upload status and extracted data
     */
    @GetMapping("/{uploadId}/status")
    @Operation(summary = "Get upload status and extracted data")
    public ResponseEntity<Map<String, Object>> getUploadStatus(@PathVariable Long uploadId) {
        log.info("GET /api/v1/contracts/upload/{}/status", uploadId);
        
        try {
            ContractUpload upload = contractUploadService.getUploadStatus(uploadId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("uploadId", upload.getId());
            response.put("filename", upload.getFilename());
            response.put("status", upload.getUploadStatus().name());
            response.put("uploadedAt", upload.getUploadedAt());
            response.put("processedAt", upload.getProcessedAt());
            
            if (upload.getExtractedData() != null) {
                @SuppressWarnings("unchecked")
                Map<String, Object> extractedData = objectMapper.readValue(upload.getExtractedData(), Map.class);
                response.put("extractedData", extractedData);
            }
            
            if (upload.getContract() != null) {
                response.put("contractId", upload.getContract().getId());
            }
            
            if (upload.getErrorMessage() != null) {
                response.put("errorMessage", upload.getErrorMessage());
            }
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error getting upload status", e);
            
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Failed to get status");
            error.put("message", e.getMessage());
            
            return ResponseEntity.internalServerError().body(error);
        }
    }
    
    /**
     * Confirm extracted data and create contract
     */
    @PostMapping("/{uploadId}/confirm")
    @Operation(summary = "Confirm extracted data and create contract")
    public ResponseEntity<Map<String, Object>> confirmAndCreateContract(
            @PathVariable Long uploadId,
            @RequestBody Map<String, Object> confirmedData) {
        
        log.info("POST /api/v1/contracts/upload/{}/confirm", uploadId);
        
        try {
            Contract contract = contractUploadService.createContractFromUpload(uploadId, confirmedData);
            
            Map<String, Object> response = new HashMap<>();
            response.put("contractId", contract.getId());
            response.put("contractNumber", contract.getContractNumber());
            response.put("message", "Contract created successfully");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error creating contract from upload", e);
            
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Failed to create contract");
            error.put("message", e.getMessage());
            
            return ResponseEntity.internalServerError().body(error);
        }
    }
    
    /**
     * Import revenue data for a contract
     */
    @PostMapping("/{contractId}/revenue/import")
    @Operation(summary = "Import revenue data from CSV/Excel")
    public ResponseEntity<Map<String, Object>> importRevenue(
            @PathVariable Long contractId,
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "userId", defaultValue = "1") Long userId) {
        
        log.info("POST /api/v1/contracts/{}/revenue/import - file: {}", contractId, file.getOriginalFilename());
        
        try {
            Map<String, Object> result;
            
            String filename = file.getOriginalFilename();
            if (filename != null && filename.endsWith(".xlsx")) {
                result = revenueImportService.importFromExcel(contractId, file, userId);
            } else {
                result = revenueImportService.importFromCSV(contractId, file, userId);
            }
            
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("Error importing revenue data", e);
            
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Import failed");
            error.put("message", e.getMessage());
            
            return ResponseEntity.internalServerError().body(error);
        }
    }
    
    /**
     * Get revenue summary for a contract
     */
    @GetMapping("/{contractId}/revenue/summary")
    @Operation(summary = "Get revenue summary for a contract")
    public ResponseEntity<Map<String, Object>> getRevenueSummary(@PathVariable Long contractId) {
        log.info("GET /api/v1/contracts/{}/revenue/summary", contractId);
        
        try {
            Map<String, Object> summary = revenueImportService.getRevenueSummary(contractId);
            return ResponseEntity.ok(summary);
        } catch (Exception e) {
            log.error("Error getting revenue summary", e);
            
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Failed to get summary");
            error.put("message", e.getMessage());
            
            return ResponseEntity.internalServerError().body(error);
        }
    }
}
