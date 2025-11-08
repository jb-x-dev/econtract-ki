package com.jbx.econtract.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Contract Upload Entity
 * Tracks uploaded contract files and their processing status
 */
@Entity
@Table(name = "contract_uploads")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContractUpload {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 255)
    private String filename;
    
    @Column(nullable = false, length = 500)
    private String filePath;
    
    @Column
    private Long fileSize;
    
    @Column(length = 100)
    private String mimeType;
    
    @Column(nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    private UploadStatus uploadStatus = UploadStatus.UPLOADED;
    
    @Column(columnDefinition = "jsonb")
    private String extractedData;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contract_id")
    private Contract contract;
    
    @Column(columnDefinition = "TEXT")
    private String errorMessage;
    
    @Column(nullable = false)
    private Long uploadedBy;
    
    @Column(name = "uploaded_at")
    private LocalDateTime uploadedAt;
    
    @Column(name = "processed_at")
    private LocalDateTime processedAt;
    
    @PrePersist
    protected void onCreate() {
        uploadedAt = LocalDateTime.now();
    }
    
    /**
     * Upload Status Enum
     */
    public enum UploadStatus {
        UPLOADED,      // File uploaded, waiting for processing
        PROCESSING,    // AI extraction in progress
        EXTRACTED,     // Data extracted, waiting for user confirmation
        COMPLETED,     // Contract created successfully
        FAILED         // Processing failed
    }
}
