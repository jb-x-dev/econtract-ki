package com.jbx.econtract.service;

import com.jbx.econtract.model.entity.Contract;
import com.jbx.econtract.model.entity.FrameworkContract;
import com.jbx.econtract.repository.ContractRepository;
import com.jbx.econtract.repository.FrameworkContractRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

@Service
public class FrameworkContractService {
    
    @Autowired
    private FrameworkContractRepository frameworkContractRepository;
    
    @Autowired
    private ContractRepository contractRepository;
    
    public Page<FrameworkContract> findAll(Pageable pageable) {
        return frameworkContractRepository.findAll(pageable);
    }
    
    public Optional<FrameworkContract> findById(Long id) {
        return frameworkContractRepository.findById(id);
    }
    
    @Transactional
    public FrameworkContract save(FrameworkContract frameworkContract) {
        if (frameworkContract.getFrameworkNumber() == null) {
            frameworkContract.setFrameworkNumber(generateFrameworkNumber());
        }
        return frameworkContractRepository.save(frameworkContract);
    }
    
    public void deleteById(Long id) {
        frameworkContractRepository.deleteById(id);
    }
    
    public List<Contract> getChildContracts(Long frameworkId) {
        return contractRepository.findByFrameworkContractId(frameworkId);
    }
    
    @Transactional
    public Contract addChildContract(Long frameworkId, Long contractId) {
        FrameworkContract framework = frameworkContractRepository.findById(frameworkId)
                .orElseThrow(() -> new RuntimeException("Framework contract not found"));
        
        Contract contract = contractRepository.findById(contractId)
                .orElseThrow(() -> new RuntimeException("Contract not found"));
        
        contract.setFrameworkContract(framework);
        contract.setIsFrameworkChild(true);
        
        return contractRepository.save(contract);
    }
    
    @Transactional
    public void removeChildContract(Long frameworkId, Long contractId) {
        Contract contract = contractRepository.findById(contractId)
                .orElseThrow(() -> new RuntimeException("Contract not found"));
        
        contract.setFrameworkContract(null);
        contract.setIsFrameworkChild(false);
        
        contractRepository.save(contract);
    }
    
    public Map<String, Object> getVolumeUsage(Long frameworkId) {
        FrameworkContract framework = frameworkContractRepository.findById(frameworkId)
                .orElseThrow(() -> new RuntimeException("Framework contract not found"));
        
        List<Contract> childContracts = contractRepository.findByFrameworkContractId(frameworkId);
        
        BigDecimal totalVolume = framework.getTotalVolume() != null ? framework.getTotalVolume() : BigDecimal.ZERO;
        BigDecimal usedVolume = childContracts.stream()
                .map(c -> c.getContractValue() != null ? c.getContractValue() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal remainingVolume = totalVolume.subtract(usedVolume);
        double usagePercentage = totalVolume.compareTo(BigDecimal.ZERO) > 0 
                ? usedVolume.divide(totalVolume, 4, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(100)).doubleValue()
                : 0.0;
        
        Map<String, Object> result = new HashMap<>();
        result.put("frameworkId", frameworkId);
        result.put("frameworkNumber", framework.getFrameworkNumber());
        result.put("totalVolume", totalVolume);
        result.put("usedVolume", usedVolume);
        result.put("remainingVolume", remainingVolume);
        result.put("usagePercentage", usagePercentage);
        result.put("childContractsCount", childContracts.size());
        result.put("currency", framework.getCurrency());
        
        return result;
    }
    
    public List<FrameworkContract> search(String query) {
        return frameworkContractRepository.findByTitleContainingOrPartnerNameContaining(query, query);
    }
    
    public Map<String, Object> getStatistics() {
        long total = frameworkContractRepository.count();
        long active = frameworkContractRepository.countByStatus(com.jbx.econtract.model.entity.Contract.ContractStatus.ACTIVE);
        long draft = frameworkContractRepository.countByStatus(com.jbx.econtract.model.entity.Contract.ContractStatus.DRAFT);
        long expired = frameworkContractRepository.countByStatus(com.jbx.econtract.model.entity.Contract.ContractStatus.EXPIRED);
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("total", total);
        stats.put("active", active);
        stats.put("draft", draft);
        stats.put("expired", expired);
        
        return stats;
    }
    
    private String generateFrameworkNumber() {
        int year = LocalDate.now().getYear();
        long count = frameworkContractRepository.count() + 1;
        return String.format("FW-%d-%06d", year, count);
    }
}

