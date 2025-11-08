package com.jbx.econtract.repository;

import com.jbx.econtract.model.entity.ContractUpload;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for ContractUpload
 */
@Repository
public interface ContractUploadRepository extends JpaRepository<ContractUpload, Long> {
    
    /**
     * Find uploads by status
     */
    List<ContractUpload> findByUploadStatus(ContractUpload.UploadStatus status);
    
    /**
     * Find uploads by user
     */
    List<ContractUpload> findByUploadedByOrderByUploadedAtDesc(Long uploadedBy);
    
    /**
     * Find uploads by contract
     */
    List<ContractUpload> findByContractId(Long contractId);
}
