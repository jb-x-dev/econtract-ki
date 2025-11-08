package com.jbx.econtract.service;

import com.jbx.econtract.model.entity.Contract;
import com.jbx.econtract.model.entity.Invoice;
import com.jbx.econtract.repository.InvoiceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Invoice Schedule Service
 * Generates invoice schedules based on contract billing cycles
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class InvoiceScheduleService {
    
    private final InvoiceRepository invoiceRepository;
    
    /**
     * Generate invoice schedule for a contract
     */
    @Transactional
    public List<Invoice> generateInvoiceSchedule(Contract contract) {
        log.info("Generating invoice schedule for contract ID: {}", contract.getId());
        
        if (contract.getBillingCycle() == null || contract.getBillingStartDate() == null) {
            log.warn("Contract {} has no billing cycle or start date, skipping invoice generation", contract.getId());
            return List.of();
        }
        
        List<Invoice> invoices = new ArrayList<>();
        LocalDate currentDate = contract.getBillingStartDate();
        LocalDate endDate = contract.getEndDate() != null ? contract.getEndDate() : currentDate.plusYears(2);
        
        int invoiceNumber = 1;
        
        while (currentDate.isBefore(endDate) || currentDate.isEqual(endDate)) {
            Invoice invoice = new Invoice();
            invoice.setContractId(contract.getId());
            invoice.setInvoiceNumber(generateInvoiceNumber(contract, invoiceNumber));
            invoice.setInvoiceDate(currentDate);
            invoice.setDueDate(currentDate.plusDays(contract.getPaymentTermDays() != null ? contract.getPaymentTermDays() : 30));
            invoice.setStatus(Invoice.InvoiceStatus.SCHEDULED);
            
            // Set partner information
            invoice.setPartnerId(contract.getPartnerId() != null ? contract.getPartnerId() : 1L);
            invoice.setPartnerName(contract.getPartnerName());
            invoice.setPartnerAddress("TBD"); // To be filled later
            invoice.setCurrency(contract.getCurrency() != null ? contract.getCurrency() : "EUR");
            invoice.setCreatedByUserId(contract.getCreatedBy());
            
            // Set billing period
            LocalDate periodEnd = calculatePeriodEnd(currentDate, contract.getBillingCycle());
            invoice.setBillingPeriodStart(currentDate);
            invoice.setBillingPeriodEnd(periodEnd);
            invoice.setScheduledDate(currentDate);
            
            // Set amounts
            BigDecimal amount = contract.getBillingAmount() != null ? contract.getBillingAmount() : BigDecimal.ZERO;
            invoice.setSubtotalNet(amount);
            invoice.setTaxRate(BigDecimal.valueOf(19)); // 19% MwSt
            BigDecimal taxAmount = amount.multiply(BigDecimal.valueOf(0.19));
            invoice.setTaxAmount(taxAmount);
            invoice.setTotalGross(amount.add(taxAmount));
            
            invoices.add(invoice);
            
            // Move to next billing period
            currentDate = calculateNextBillingDate(currentDate, contract.getBillingCycle());
            invoiceNumber++;
            
            // Safety limit
            if (invoiceNumber > 100) {
                log.warn("Invoice generation limit reached for contract {}", contract.getId());
                break;
            }
        }
        
        // Save all invoices
        invoices = invoiceRepository.saveAll(invoices);
        
        log.info("Generated {} invoices for contract {}", invoices.size(), contract.getId());
        return invoices;
    }
    
    /**
     * Calculate next billing date based on cycle
     */
    private LocalDate calculateNextBillingDate(LocalDate currentDate, String billingCycle) {
        return switch (billingCycle.toUpperCase()) {
            case "MONTHLY" -> currentDate.plusMonths(1);
            case "QUARTERLY" -> currentDate.plusMonths(3);
            case "YEARLY" -> currentDate.plusYears(1);
            case "ONE_TIME" -> currentDate.plusYears(10); // End loop
            default -> currentDate.plusMonths(1);
        };
    }
    
    /**
     * Calculate billing period end date
     */
    private LocalDate calculatePeriodEnd(LocalDate startDate, String billingCycle) {
        return switch (billingCycle.toUpperCase()) {
            case "MONTHLY" -> startDate.plusMonths(1).minusDays(1);
            case "QUARTERLY" -> startDate.plusMonths(3).minusDays(1);
            case "YEARLY" -> startDate.plusYears(1).minusDays(1);
            case "ONE_TIME" -> startDate;
            default -> startDate.plusMonths(1).minusDays(1);
        };
    }
    
    /**
     * Generate invoice number
     */
    private String generateInvoiceNumber(Contract contract, int sequenceNumber) {
        return String.format("%s-INV-%03d", contract.getContractNumber(), sequenceNumber);
    }
    
    /**
     * Regenerate invoice schedule (delete old, create new)
     */
    @Transactional
    public List<Invoice> regenerateInvoiceSchedule(Contract contract) {
        log.info("Regenerating invoice schedule for contract ID: {}", contract.getId());
        
        // Delete existing scheduled invoices
        List<Invoice> existingInvoices = invoiceRepository.findByContractId(contract.getId());
        List<Invoice> scheduledInvoices = existingInvoices.stream()
            .filter(inv -> Invoice.InvoiceStatus.SCHEDULED.equals(inv.getStatus()))
            .toList();
        
        invoiceRepository.deleteAll(scheduledInvoices);
        log.info("Deleted {} scheduled invoices", scheduledInvoices.size());
        
        // Generate new schedule
        return generateInvoiceSchedule(contract);
    }
}
