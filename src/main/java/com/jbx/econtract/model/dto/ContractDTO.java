package com.jbx.econtract.model.dto;

import com.jbx.econtract.model.entity.Contract;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Contract Data Transfer Object
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContractDTO {
    
    private Long id;
    private String contractNumber;
    private String title;
    private String contractType;
    private String status;
    private String partnerName;
    private Long partnerId;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer noticePeriodDays;
    private Boolean autoRenewal;
    private BigDecimal contractValue;
    private String currency;
    private String department;
    private Long ownerUserId;
    private String ownerUserName;
    private Long createdBy;
    private String createdByName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Zusätzliche Felder
    private Integer daysUntilExpiry;
    private Boolean requiresAction;
    
    /**
     * Konvertiert Contract Entity zu DTO
     */
    public static ContractDTO fromEntity(Contract contract) {
        ContractDTO dto = new ContractDTO();
        dto.setId(contract.getId());
        dto.setContractNumber(contract.getContractNumber());
        dto.setTitle(contract.getTitle());
        dto.setContractType(contract.getContractType());
        dto.setStatus(contract.getStatus().name());
        dto.setPartnerName(contract.getPartnerName());
        dto.setPartnerId(contract.getPartnerId());
        dto.setStartDate(contract.getStartDate());
        dto.setEndDate(contract.getEndDate());
        dto.setNoticePeriodDays(contract.getNoticePeriodDays());
        dto.setAutoRenewal(contract.getAutoRenewal());
        dto.setContractValue(contract.getContractValue());
        dto.setCurrency(contract.getCurrency());
        dto.setDepartment(contract.getDepartment());
        dto.setOwnerUserId(contract.getOwnerUserId());
        dto.setCreatedBy(contract.getCreatedBy());
        dto.setCreatedAt(contract.getCreatedAt());
        dto.setUpdatedAt(contract.getUpdatedAt());
        
        // Berechne Tage bis Ablauf
        if (contract.getEndDate() != null) {
            LocalDate now = LocalDate.now();
            dto.setDaysUntilExpiry((int) java.time.temporal.ChronoUnit.DAYS.between(now, contract.getEndDate()));
        }
        
        return dto;
    }
}

