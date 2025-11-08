package com.jbx.econtract.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jbx.econtract.model.entity.Contract;
import com.jbx.econtract.model.entity.ContractUpload;
import com.jbx.econtract.repository.ContractRepository;
import com.jbx.econtract.repository.ContractUploadRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * Contract Upload Service
 * Handles file upload, AI extraction, and contract creation
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ContractUploadService {
    
    private final ContractUploadRepository contractUploadRepository;
    private final ContractRepository contractRepository;
    private final AIExtractionService aiExtractionService;
    private final InvoiceScheduleService invoiceScheduleService;
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    private static final String UPLOAD_DIR = "/tmp/contract-uploads/";
    
    /**
     * Upload contract file
     */
    @Transactional
    public ContractUpload uploadContract(MultipartFile file, Long userId) throws Exception {
        log.info("Uploading contract file: {} by user {}", file.getOriginalFilename(), userId);
        
        // Create upload directory if not exists
        Files.createDirectories(Paths.get(UPLOAD_DIR));
        
        // Generate unique filename
        String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Path filePath = Paths.get(UPLOAD_DIR, filename);
        
        // Save file
        file.transferTo(filePath.toFile());
        
        // Create upload record
        ContractUpload upload = new ContractUpload();
        upload.setFilename(file.getOriginalFilename());
        upload.setFilePath(filePath.toString());
        upload.setFileSize(file.getSize());
        upload.setMimeType(file.getContentType());
        upload.setUploadStatus(ContractUpload.UploadStatus.UPLOADED);
        upload.setUploadedBy(userId);
        
        upload = contractUploadRepository.save(upload);
        
        log.info("Contract uploaded successfully with ID: {}", upload.getId());
        
        // Start async extraction
        processUploadAsync(upload.getId());
        
        return upload;
    }
    
    /**
     * Process upload asynchronously (in real app, use @Async or message queue)
     */
    private void processUploadAsync(Long uploadId) {
        new Thread(() -> {
            try {
                processUpload(uploadId);
            } catch (Exception e) {
                log.error("Error processing upload {}", uploadId, e);
                markUploadAsFailed(uploadId, e.getMessage());
            }
        }).start();
    }
    
    /**
     * Process upload: extract text and AI analysis
     */
    @Transactional
    public void processUpload(Long uploadId) throws Exception {
        log.info("Processing upload ID: {}", uploadId);
        
        ContractUpload upload = contractUploadRepository.findById(uploadId)
            .orElseThrow(() -> new IllegalArgumentException("Upload not found: " + uploadId));
        
        // Update status to PROCESSING
        upload.setUploadStatus(ContractUpload.UploadStatus.PROCESSING);
        contractUploadRepository.save(upload);
        
        // Extract text from file
        File file = new File(upload.getFilePath());
        String text = aiExtractionService.extractTextFromFile(file, upload.getMimeType());
        
        // Extract structured data using AI
        Map<String, Object> extractedData = aiExtractionService.extractContractData(text);
        
        // Save extracted data as JSON
        String extractedDataJson = objectMapper.writeValueAsString(extractedData);
        upload.setExtractedData(extractedDataJson);
        upload.setUploadStatus(ContractUpload.UploadStatus.EXTRACTED);
        upload.setProcessedAt(LocalDateTime.now());
        
        contractUploadRepository.save(upload);
        
        log.info("Upload {} processed successfully", uploadId);
    }
    
    /**
     * Mark upload as failed
     */
    @Transactional
    public void markUploadAsFailed(Long uploadId, String errorMessage) {
        contractUploadRepository.findById(uploadId).ifPresent(upload -> {
            upload.setUploadStatus(ContractUpload.UploadStatus.FAILED);
            upload.setErrorMessage(errorMessage);
            upload.setProcessedAt(LocalDateTime.now());
            contractUploadRepository.save(upload);
        });
    }
    
    /**
     * Create contract from extracted data
     */
    @Transactional
    public Contract createContractFromUpload(Long uploadId, Map<String, Object> confirmedData) throws Exception {
        log.info("Creating contract from upload ID: {}", uploadId);
        
        ContractUpload upload = contractUploadRepository.findById(uploadId)
            .orElseThrow(() -> new IllegalArgumentException("Upload not found: " + uploadId));
        
        // Create contract entity
        Contract contract = new Contract();
        
        // Map extracted data to contract fields
        if (confirmedData.containsKey("contractNumber")) {
            contract.setContractNumber((String) confirmedData.get("contractNumber"));
        } else {
            contract.setContractNumber(generateContractNumber());
        }
        
        contract.setTitle((String) confirmedData.get("title"));
        contract.setContractType((String) confirmedData.get("contractType"));
        contract.setPartnerName((String) confirmedData.get("partnerName"));
        
        // Parse dates
        if (confirmedData.containsKey("startDate") && confirmedData.get("startDate") != null) {
            contract.setStartDate(LocalDate.parse((String) confirmedData.get("startDate")));
        }
        if (confirmedData.containsKey("endDate") && confirmedData.get("endDate") != null) {
            contract.setEndDate(LocalDate.parse((String) confirmedData.get("endDate")));
        }
        
        // Financial data
        if (confirmedData.containsKey("contractValue") && confirmedData.get("contractValue") != null) {
            contract.setContractValue(new BigDecimal(confirmedData.get("contractValue").toString()));
        }
        contract.setCurrency((String) confirmedData.getOrDefault("currency", "EUR"));
        
        // Billing data
        contract.setBillingCycle((String) confirmedData.get("billingCycle"));
        if (confirmedData.containsKey("billingAmount") && confirmedData.get("billingAmount") != null) {
            contract.setBillingAmount(new BigDecimal(confirmedData.get("billingAmount").toString()));
        }
        if (confirmedData.containsKey("billingStartDate") && confirmedData.get("billingStartDate") != null) {
            contract.setBillingStartDate(LocalDate.parse((String) confirmedData.get("billingStartDate")));
        }
        
        // Other fields
        if (confirmedData.containsKey("noticePeriodDays") && confirmedData.get("noticePeriodDays") != null) {
            contract.setNoticePeriodDays(Integer.parseInt(confirmedData.get("noticePeriodDays").toString()));
        }
        if (confirmedData.containsKey("paymentTermDays") && confirmedData.get("paymentTermDays") != null) {
            contract.setPaymentTermDays(Integer.parseInt(confirmedData.get("paymentTermDays").toString()));
        }
        if (confirmedData.containsKey("autoRenewal") && confirmedData.get("autoRenewal") != null) {
            contract.setAutoRenewal(Boolean.parseBoolean(confirmedData.get("autoRenewal").toString()));
        }
        if (confirmedData.containsKey("department")) {
            contract.setDepartment((String) confirmedData.get("department"));
        }
        
        // Set default values
        contract.setStatus(Contract.ContractStatus.DRAFT);
        contract.setOwnerUserId(upload.getUploadedBy());
        contract.setCreatedBy(upload.getUploadedBy());
        
        // Save contract
        contract = contractRepository.save(contract);
        
        // Update upload record
        upload.setContract(contract);
        upload.setUploadStatus(ContractUpload.UploadStatus.COMPLETED);
        contractUploadRepository.save(upload);
        
        // Generate invoice schedule if billing cycle is set
        if (contract.getBillingCycle() != null && contract.getBillingStartDate() != null) {
            invoiceScheduleService.generateInvoiceSchedule(contract);
        }
        
        log.info("Contract created successfully with ID: {}", contract.getId());
        return contract;
    }
    
    /**
     * Generate contract number
     */
    private String generateContractNumber() {
        long count = contractRepository.count();
        return String.format("VTR-%d-%04d", LocalDate.now().getYear(), count + 1);
    }
    
    /**
     * Get upload status
     */
    @Transactional(readOnly = true)
    public ContractUpload getUploadStatus(Long uploadId) {
        return contractUploadRepository.findById(uploadId)
            .orElseThrow(() -> new IllegalArgumentException("Upload not found: " + uploadId));
    }
}
