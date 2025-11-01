package com.jbx.econtract.repository;

import com.jbx.econtract.model.entity.Contract.ContractStatus;
import com.jbx.econtract.model.entity.FrameworkContract;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FrameworkContractRepository extends JpaRepository<FrameworkContract, Long> {
    
    List<FrameworkContract> findByTitleContainingOrPartnerNameContaining(String title, String partnerName);
    
    long countByStatus(ContractStatus status);
    
    List<FrameworkContract> findByStatus(ContractStatus status);
}

