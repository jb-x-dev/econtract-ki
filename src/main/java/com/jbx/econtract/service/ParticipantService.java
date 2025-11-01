package com.jbx.econtract.service;

import com.jbx.econtract.model.entity.ContractParticipant;
import com.jbx.econtract.repository.ContractParticipantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ParticipantService {
    
    private final ContractParticipantRepository participantRepository;
    
    @Transactional
    public ContractParticipant addParticipant(ContractParticipant participant) {
        return participantRepository.save(participant);
    }
    
    public List<ContractParticipant> getParticipantsByContract(Long contractId) {
        return participantRepository.findByContractId(contractId);
    }
    
    public List<ContractParticipant> getResponsibleUsers(Long contractId) {
        return participantRepository.findByContractIdAndParticipantType(
            contractId, 
            ContractParticipant.ParticipantType.RESPONSIBLE
        );
    }
    
    @Transactional
    public void removeParticipant(Long participantId) {
        participantRepository.deleteById(participantId);
    }
    
    @Transactional
    public void removeAllParticipants(Long contractId) {
        participantRepository.deleteByContractId(contractId);
    }
}

