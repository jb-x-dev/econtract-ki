package com.jbx.econtract.repository;

import com.jbx.econtract.model.entity.ContractParticipant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ContractParticipantRepository extends JpaRepository<ContractParticipant, Long> {
    
    List<ContractParticipant> findByContractId(Long contractId);
    
    List<ContractParticipant> findByContractIdAndParticipantType(
        Long contractId, 
        ContractParticipant.ParticipantType type
    );
    
    List<ContractParticipant> findByUserId(Long userId);
    
    void deleteByContractId(Long contractId);
}

