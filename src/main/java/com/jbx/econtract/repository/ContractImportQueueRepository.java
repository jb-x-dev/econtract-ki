package com.jbx.econtract.repository;

import com.jbx.econtract.model.entity.ContractImportQueue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ContractImportQueueRepository extends JpaRepository<ContractImportQueue, Long> {
    
    List<ContractImportQueue> findByStatus(ContractImportQueue.ImportStatus status);
    
    List<ContractImportQueue> findByBatchId(Long batchId);
    
    List<ContractImportQueue> findByUploadedBy(String uploadedBy);
    
    Long countByStatus(ContractImportQueue.ImportStatus status);
    
    List<ContractImportQueue> findByStatusOrderByCreatedAtDesc(ContractImportQueue.ImportStatus status);
}

