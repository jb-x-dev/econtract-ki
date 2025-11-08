package com.jbx.econtract.service;

import com.jbx.econtract.model.dto.ContractDTO;
import com.jbx.econtract.model.entity.Contract;
import com.jbx.econtract.repository.ContractRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service für Vertragsverwaltung
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ContractService {

    private final ContractRepository contractRepository;

    /**
     * Erstellt einen neuen Vertrag
     */
    @Transactional
    public ContractDTO createContract(ContractDTO dto) {
        log.info("Creating new contract: {}", dto.getTitle());
        
        Contract contract = new Contract();
        contract.setContractNumber(generateContractNumber());
        contract.setTitle(dto.getTitle());
        contract.setContractType(dto.getContractType());
        contract.setStatus(Contract.ContractStatus.DRAFT);
        contract.setPartnerName(dto.getPartnerName());
        contract.setPartnerId(dto.getPartnerId());
        contract.setStartDate(dto.getStartDate());
        contract.setEndDate(dto.getEndDate());
        contract.setNoticePeriodDays(dto.getNoticePeriodDays());
        contract.setAutoRenewal(dto.getAutoRenewal());
        contract.setContractValue(dto.getContractValue());
        contract.setCurrency(dto.getCurrency());
        contract.setDepartment(dto.getDepartment());
        contract.setOwnerUserId(dto.getOwnerUserId());
        contract.setCreatedBy(dto.getCreatedBy());
        
        Contract saved = contractRepository.save(contract);
        log.info("Contract created with ID: {}", saved.getId());
        
        return ContractDTO.fromEntity(saved);
    }

    /**
     * Findet Vertrag nach ID
     */
    @Transactional(readOnly = true)
    public ContractDTO getContractById(Long id) {
        log.debug("Fetching contract with ID: {}", id);
        Contract contract = contractRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Contract not found with ID: " + id));
        return ContractDTO.fromEntity(contract);
    }

    /**
     * Findet alle Verträge ohne Pagination (für Dashboard)
     */
    @Transactional(readOnly = true)
    public List<ContractDTO> getAllContractsUnpaged() {
        log.debug("Fetching all contracts without pagination");
        return contractRepository.findAll().stream()
                .map(ContractDTO::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Findet alle Verträge (paginiert)
     */
    @Transactional(readOnly = true)
    public Page<ContractDTO> getAllContracts(Pageable pageable) {
        log.debug("Fetching all contracts, page: {}", pageable.getPageNumber());
        return contractRepository.findAll(pageable)
                .map(ContractDTO::fromEntity);
    }

    /**
     * Findet Verträge nach Status
     */
    @Transactional(readOnly = true)
    public Page<ContractDTO> getContractsByStatus(String status, Pageable pageable) {
        Contract.ContractStatus contractStatus = Contract.ContractStatus.valueOf(status);
        return contractRepository.findByStatus(contractStatus, pageable)
                .map(ContractDTO::fromEntity);
    }

    /**
     * Findet Verträge nach Typ
     */
    @Transactional(readOnly = true)
    public Page<ContractDTO> getContractsByType(String contractType, Pageable pageable) {
        return contractRepository.findByContractType(contractType, pageable)
                .map(ContractDTO::fromEntity);
    }

    /**
     * Aktualisiert einen Vertrag
     */
    @Transactional
    public ContractDTO updateContract(Long id, ContractDTO dto) {
        log.info("Updating contract with ID: {}", id);
        
        Contract contract = contractRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Contract not found with ID: " + id));
        
        contract.setTitle(dto.getTitle());
        contract.setContractType(dto.getContractType());
        contract.setPartnerName(dto.getPartnerName());
        contract.setPartnerId(dto.getPartnerId());
        contract.setStartDate(dto.getStartDate());
        contract.setEndDate(dto.getEndDate());
        contract.setNoticePeriodDays(dto.getNoticePeriodDays());
        contract.setAutoRenewal(dto.getAutoRenewal());
        contract.setContractValue(dto.getContractValue());
        contract.setCurrency(dto.getCurrency());
        contract.setDepartment(dto.getDepartment());
        
        Contract updated = contractRepository.save(contract);
        log.info("Contract updated: {}", updated.getId());
        
        return ContractDTO.fromEntity(updated);
    }

    /**
     * Löscht einen Vertrag
     */
    @Transactional
    public void deleteContract(Long id) {
        log.info("Deleting contract with ID: {}", id);
        
        if (!contractRepository.existsById(id)) {
            throw new RuntimeException("Contract not found with ID: " + id);
        }
        
        contractRepository.deleteById(id);
        log.info("Contract deleted: {}", id);
    }

    /**
     * Reicht Vertrag zur Genehmigung ein
     */
    @Transactional
    public ContractDTO submitForApproval(Long id) {
        log.info("Submitting contract for approval: {}", id);
        
        Contract contract = contractRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Contract not found with ID: " + id));
        
        if (contract.getStatus() != Contract.ContractStatus.DRAFT) {
            throw new RuntimeException("Only DRAFT contracts can be submitted for approval");
        }
        
        contract.setStatus(Contract.ContractStatus.IN_APPROVAL);
        Contract updated = contractRepository.save(contract);
        
        // TODO: Workflow starten
        log.info("Contract submitted for approval: {}", id);
        
        return ContractDTO.fromEntity(updated);
    }

    /**
     * Sucht Verträge
     */
    @Transactional(readOnly = true)
    public Page<ContractDTO> searchContracts(String keyword, Pageable pageable) {
        log.debug("Searching contracts with keyword: {}", keyword);
        return contractRepository.searchByTitle(keyword, pageable)
                .map(ContractDTO::fromEntity);
    }

    /**
     * Generiert eine neue Vertragsnummer
     */
    private String generateContractNumber() {
        String year = String.valueOf(LocalDateTime.now().getYear());
        Long count = contractRepository.count() + 1;
        return String.format("CON-%s-%06d", year, count);
    }

    /**
     * Zählt Verträge nach Status
     */
    @Transactional(readOnly = true)
    public Long countByStatus(String status) {
        try {
            Contract.ContractStatus contractStatus = Contract.ContractStatus.valueOf(status);
            return contractRepository.countByStatus(contractStatus);
        } catch (Exception e) {
            log.error("Error counting by status: {}", status, e);
            return 0L;
        }
    }
    
    /**
     * Zählt alle Verträge
     */
    @Transactional(readOnly = true)
    public Long countAll() {
        return contractRepository.count();
    }
}

