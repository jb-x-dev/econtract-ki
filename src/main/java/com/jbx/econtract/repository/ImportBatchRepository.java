package com.jbx.econtract.repository;

import com.jbx.econtract.model.entity.ImportBatch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ImportBatchRepository extends JpaRepository<ImportBatch, Long> {
    
    List<ImportBatch> findByUploadedByOrderByCreatedAtDesc(String uploadedBy);
    
    List<ImportBatch> findByStatusOrderByCreatedAtDesc(ImportBatch.BatchStatus status);
}

