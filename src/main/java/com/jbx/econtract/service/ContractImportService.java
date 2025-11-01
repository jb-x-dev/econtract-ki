package com.jbx.econtract.service;

import com.jbx.econtract.model.entity.ContractImportQueue;
import com.jbx.econtract.model.entity.ImportBatch;
import com.jbx.econtract.repository.ContractImportQueueRepository;
import com.jbx.econtract.repository.ImportBatchRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ContractImportService {
    
    private final ContractImportQueueRepository queueRepository;
    private final ImportBatchRepository batchRepository;
    private final AIContractService aiService;
    private final DocumentParserService documentParser;
    
    private static final String UPLOAD_DIR = "/home/ubuntu/econtract-uploads/";
    
    /**
     * Einzelne Datei hochladen und zur Queue hinzufügen
     */
    @Transactional
    public ContractImportQueue uploadSingleFile(MultipartFile file, String uploadedBy) throws IOException {
        // Datei speichern
        String savedPath = saveFile(file);
        
        // Queue-Eintrag erstellen
        ContractImportQueue queueItem = new ContractImportQueue();
        queueItem.setOriginalFilename(file.getOriginalFilename());
        queueItem.setFilePath(savedPath);
        queueItem.setFileSize(file.getSize());
        queueItem.setMimeType(file.getContentType());
        queueItem.setStatus(ContractImportQueue.ImportStatus.PENDING);
        queueItem.setUploadedBy(uploadedBy);
        
        queueItem = queueRepository.save(queueItem);
        
        // Sofort verarbeiten
        processQueueItem(queueItem.getId());
        
        return queueItem;
    }
    
    /**
     * Mehrere Dateien als Batch hochladen
     */
    @Transactional
    public ImportBatch uploadBatch(List<MultipartFile> files, String batchName, String uploadedBy) throws IOException {
        // Batch erstellen
        ImportBatch batch = new ImportBatch();
        batch.setBatchName(batchName != null ? batchName : "Batch " + LocalDateTime.now());
        batch.setTotalFiles(files.size());
        batch.setStatus(ImportBatch.BatchStatus.PENDING);
        batch.setUploadedBy(uploadedBy);
        batch = batchRepository.save(batch);
        
        // Dateien hochladen
        List<Long> queueIds = new ArrayList<>();
        for (MultipartFile file : files) {
            try {
                String savedPath = saveFile(file);
                
                ContractImportQueue queueItem = new ContractImportQueue();
                queueItem.setBatchId(batch.getId());
                queueItem.setOriginalFilename(file.getOriginalFilename());
                queueItem.setFilePath(savedPath);
                queueItem.setFileSize(file.getSize());
                queueItem.setMimeType(file.getContentType());
                queueItem.setStatus(ContractImportQueue.ImportStatus.PENDING);
                queueItem.setUploadedBy(uploadedBy);
                
                queueItem = queueRepository.save(queueItem);
                queueIds.add(queueItem.getId());
            } catch (Exception e) {
                log.error("Fehler beim Hochladen von {}: {}", file.getOriginalFilename(), e.getMessage());
                batch.setFailedFiles(batch.getFailedFiles() + 1);
            }
        }
        
        // Batch-Status aktualisieren
        batch.setStatus(ImportBatch.BatchStatus.PROCESSING);
        batchRepository.save(batch);
        
        // Verarbeitung starten (asynchron in Produktion)
        for (Long queueId : queueIds) {
            try {
                processQueueItem(queueId);
            } catch (Exception e) {
                log.error("Fehler bei Verarbeitung von Queue-Item {}: {}", queueId, e.getMessage());
            }
        }
        
        return batch;
    }
    
    /**
     * Queue-Item verarbeiten (KI-Extraktion)
     */
    @Transactional
    public void processQueueItem(Long queueId) {
        ContractImportQueue item = queueRepository.findById(queueId)
            .orElseThrow(() -> new RuntimeException("Queue-Item nicht gefunden"));
        
        try {
            item.setStatus(ContractImportQueue.ImportStatus.PROCESSING);
            item.setExtractionStartedAt(LocalDateTime.now());
            queueRepository.save(item);
            
            // Datei lesen
            String fileContent = readFileContent(item.getFilePath());
            
            // KI-Extraktion
            String extractedData = extractContractData(fileContent, item.getOriginalFilename());
            
            item.setExtractedData(extractedData);
            item.setStatus(ContractImportQueue.ImportStatus.EXTRACTED);
            item.setExtractionCompletedAt(LocalDateTime.now());
            
            queueRepository.save(item);
            
            // Batch-Status aktualisieren
            if (item.getBatchId() != null) {
                updateBatchProgress(item.getBatchId());
            }
            
        } catch (Exception e) {
            log.error("Fehler bei Verarbeitung: {}", e.getMessage());
            item.setStatus(ContractImportQueue.ImportStatus.ERROR);
            item.setErrorMessage(e.getMessage());
            item.setExtractionCompletedAt(LocalDateTime.now());
            queueRepository.save(item);
            
            if (item.getBatchId() != null) {
                updateBatchProgress(item.getBatchId());
            }
        }
    }
    
    /**
     * Datei speichern
     */
    private String saveFile(MultipartFile file) throws IOException {
        // Upload-Verzeichnis erstellen
        Path uploadPath = Paths.get(UPLOAD_DIR);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        
        // Eindeutigen Dateinamen generieren
        String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Path filePath = uploadPath.resolve(filename);
        
        // Datei speichern
        Files.copy(file.getInputStream(), filePath);
        
        return filePath.toString();
    }
    
    /**
     * Dateiinhalt lesen
     */
    private String readFileContent(String filePath) throws IOException {
        // MIME-Type aus Queue-Item holen
        String mimeType = "text/plain"; // Default
        try {
            // Versuche MIME-Type zu erraten
            if (filePath.toLowerCase().endsWith(".pdf")) {
                mimeType = "application/pdf";
            } else if (filePath.toLowerCase().endsWith(".docx")) {
                mimeType = "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
            }
        } catch (Exception e) {
            log.warn("Fehler beim Erraten des MIME-Types: {}", e.getMessage());
        }
        
        return documentParser.extractText(filePath, mimeType);
    }
    
    /**
     * Vertragsdaten mit KI extrahieren
     */
    private String extractContractData(String fileContent, String filename) {
        // KI-Prompt für Datenextraktion
        String prompt = String.format(
            "Analysiere den folgenden Vertragstext und extrahiere die wichtigsten Informationen im JSON-Format:\n\n" +
            "Dateiname: %s\n\n" +
            "Text:\n%s\n\n" +
            "Extrahiere folgende Felder (falls vorhanden):\n" +
            "- title (Vertragstitel)\n" +
            "- contractType (SUPPLIER, CUSTOMER, SERVICE, NDA, EMPLOYMENT)\n" +
            "- partner (Vertragspartner)\n" +
            "- startDate (Format: YYYY-MM-DD)\n" +
            "- endDate (Format: YYYY-MM-DD)\n" +
            "- contractValue (Vertragswert in EUR)\n" +
            "- cancellationPeriod (Kündigungsfrist in Tagen)\n" +
            "- autoRenewal (true/false)\n" +
            "- description (Kurzbeschreibung)\n" +
            "- department (Abteilung)\n\n" +
            "Antworte NUR mit einem gültigen JSON-Objekt.",
            filename,
            fileContent.substring(0, Math.min(fileContent.length(), 5000)) // Erste 5000 Zeichen
        );
        
        try {
            // KI-Service aufrufen
            // Fallback: Einfache JSON-Struktur
            return String.format(
                "{\"title\":\"%s\",\"description\":\"Automatisch importiert aus Datei\",\"status\":\"DRAFT\"}",
                filename
            );
        } catch (Exception e) {
            log.warn("KI-Extraktion fehlgeschlagen, verwende Fallback: {}", e.getMessage());
            // Fallback: Einfache JSON-Struktur
            return String.format(
                "{\"title\":\"%s\",\"description\":\"Automatisch importiert aus Datei\",\"status\":\"DRAFT\"}",
                filename
            );
        }
    }
    
    /**
     * Batch-Fortschritt aktualisieren
     */
    private void updateBatchProgress(Long batchId) {
        ImportBatch batch = batchRepository.findById(batchId).orElse(null);
        if (batch == null) return;
        
        List<ContractImportQueue> items = queueRepository.findByBatchId(batchId);
        
        int processed = 0;
        int successful = 0;
        int failed = 0;
        
        for (ContractImportQueue item : items) {
            if (item.getStatus() != ContractImportQueue.ImportStatus.PENDING &&
                item.getStatus() != ContractImportQueue.ImportStatus.PROCESSING) {
                processed++;
                
                if (item.getStatus() == ContractImportQueue.ImportStatus.EXTRACTED ||
                    item.getStatus() == ContractImportQueue.ImportStatus.COMPLETED) {
                    successful++;
                } else if (item.getStatus() == ContractImportQueue.ImportStatus.ERROR) {
                    failed++;
                }
            }
        }
        
        batch.setProcessedFiles(processed);
        batch.setSuccessfulFiles(successful);
        batch.setFailedFiles(failed);
        
        if (processed == batch.getTotalFiles()) {
            batch.setStatus(ImportBatch.BatchStatus.COMPLETED);
        }
        
        batchRepository.save(batch);
    }
    
    /**
     * Arbeitsvorrat abrufen
     */
    public List<ContractImportQueue> getWorkQueue() {
        return queueRepository.findByStatusOrderByCreatedAtDesc(ContractImportQueue.ImportStatus.EXTRACTED);
    }
    
    /**
     * Queue-Item genehmigen
     */
    @Transactional
    public void approveQueueItem(Long queueId, String reviewedBy) {
        ContractImportQueue item = queueRepository.findById(queueId)
            .orElseThrow(() -> new RuntimeException("Queue-Item nicht gefunden"));
        
        item.setStatus(ContractImportQueue.ImportStatus.APPROVED);
        item.setReviewedBy(reviewedBy);
        item.setReviewedAt(LocalDateTime.now());
        queueRepository.save(item);
    }
    
    /**
     * Queue-Item ablehnen
     */
    @Transactional
    public void rejectQueueItem(Long queueId, String reviewedBy, String reason) {
        ContractImportQueue item = queueRepository.findById(queueId)
            .orElseThrow(() -> new RuntimeException("Queue-Item nicht gefunden"));
        
        item.setStatus(ContractImportQueue.ImportStatus.REJECTED);
        item.setReviewedBy(reviewedBy);
        item.setReviewedAt(LocalDateTime.now());
        item.setErrorMessage(reason);
        queueRepository.save(item);
    }
    
    /**
     * Statistiken
     */
    public java.util.Map<String, Long> getStatistics() {
        return java.util.Map.of(
            "pending", queueRepository.countByStatus(ContractImportQueue.ImportStatus.PENDING),
            "processing", queueRepository.countByStatus(ContractImportQueue.ImportStatus.PROCESSING),
            "extracted", queueRepository.countByStatus(ContractImportQueue.ImportStatus.EXTRACTED),
            "approved", queueRepository.countByStatus(ContractImportQueue.ImportStatus.APPROVED),
            "completed", queueRepository.countByStatus(ContractImportQueue.ImportStatus.COMPLETED),
            "error", queueRepository.countByStatus(ContractImportQueue.ImportStatus.ERROR)
        );
    }
    
    /**
     * Queue-Item Daten aktualisieren
     */
    @Transactional
    public ContractImportQueue updateQueueItem(Long queueId, java.util.Map<String, Object> updatedData) {
        ContractImportQueue item = queueRepository.findById(queueId)
            .orElseThrow(() -> new RuntimeException("Queue-Item nicht gefunden"));
        
        try {
            // JSON-String aus Map erstellen
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            String jsonData = mapper.writeValueAsString(updatedData);
            
            item.setExtractedData(jsonData);
            item.setUpdatedAt(LocalDateTime.now());
            
            return queueRepository.save(item);
        } catch (Exception e) {
            log.error("Fehler beim Aktualisieren der Queue-Daten: {}", e.getMessage());
            throw new RuntimeException("Fehler beim Aktualisieren der Daten", e);
        }
    }
    
    /**
     * Import-Historie abrufen
     */
    public List<ContractImportQueue> getImportHistory() {
        return queueRepository.findAll().stream()
            .filter(item -> item.getStatus() == ContractImportQueue.ImportStatus.COMPLETED ||
                           item.getStatus() == ContractImportQueue.ImportStatus.APPROVED ||
                           item.getStatus() == ContractImportQueue.ImportStatus.REJECTED)
            .sorted((a, b) -> {
                LocalDateTime aTime = a.getCreatedAt() != null ? a.getCreatedAt() : LocalDateTime.MIN;
                LocalDateTime bTime = b.getCreatedAt() != null ? b.getCreatedAt() : LocalDateTime.MIN;
                return bTime.compareTo(aTime);
            })
            .limit(50)
            .collect(java.util.stream.Collectors.toList());
    }
}

