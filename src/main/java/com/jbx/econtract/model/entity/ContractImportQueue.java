package com.jbx.econtract.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "contract_import_queue")
@Data
public class ContractImportQueue {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "batch_id")
    private Long batchId;
    
    @Column(name = "original_filename", nullable = false)
    private String originalFilename;
    
    @Column(name = "file_path", nullable = false)
    private String filePath;
    
    @Column(name = "file_size")
    private Long fileSize;
    
    @Column(name = "mime_type")
    private String mimeType;
    
    @Column(name = "extracted_data", columnDefinition = "TEXT")
    private String extractedData; // JSON
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, columnDefinition = "VARCHAR(50)")
    private ImportStatus status = ImportStatus.PENDING;
    
    @Column(name = "contract_id")
    private Long contractId;
    
    @Column(name = "extraction_started_at")
    private LocalDateTime extractionStartedAt;
    
    @Column(name = "extraction_completed_at")
    private LocalDateTime extractionCompletedAt;
    
    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;
    
    @Column(name = "uploaded_by")
    private String uploadedBy;
    
    @Column(name = "reviewed_by")
    private String reviewedBy;
    
    @Column(name = "reviewed_at")
    private LocalDateTime reviewedAt;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    public enum ImportStatus {
        PENDING,        // Wartet auf Verarbeitung
        PROCESSING,     // Wird gerade verarbeitet
        EXTRACTED,      // Daten extrahiert, wartet auf Review
        APPROVED,       // Genehmigt, kann als Vertrag erstellt werden
        REJECTED,       // Abgelehnt
        COMPLETED,      // Vertrag wurde erstellt
        ERROR           // Fehler bei Verarbeitung
    }
}

