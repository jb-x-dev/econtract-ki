package com.jbx.econtract.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "import_batches")
@Data
public class ImportBatch {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "batch_name")
    private String batchName;
    
    @Column(name = "total_files")
    private Integer totalFiles = 0;
    
    @Column(name = "processed_files")
    private Integer processedFiles = 0;
    
    @Column(name = "successful_files")
    private Integer successfulFiles = 0;
    
    @Column(name = "failed_files")
    private Integer failedFiles = 0;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, columnDefinition = "VARCHAR(50)")
    private BatchStatus status = BatchStatus.PENDING;
    
    @Column(name = "uploaded_by")
    private String uploadedBy;
    
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
    
    public enum BatchStatus {
        PENDING,
        PROCESSING,
        COMPLETED,
        FAILED
    }
}

