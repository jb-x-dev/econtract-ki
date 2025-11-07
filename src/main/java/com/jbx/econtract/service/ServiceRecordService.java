package com.jbx.econtract.service;

import com.jbx.econtract.model.entity.ServiceRecord;
import com.jbx.econtract.model.entity.ServiceRecord.ServiceRecordStatus;
import com.jbx.econtract.repository.ServiceRecordRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service for managing service records.
 * 
 * @author jb-x Development Team
 * @version 1.0
 */
@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class ServiceRecordService {

    private final ServiceRecordRepository serviceRecordRepository;

    /**
     * Create a new service record.
     *
     * @param serviceRecord the service record to create
     * @return the created service record
     */
    public ServiceRecord createServiceRecord(ServiceRecord serviceRecord) {
        log.info("Creating new service record for contract ID: {}", serviceRecord.getContractId());
        
        // Calculate total net
        serviceRecord.calculateTotalNet();
        
        // Set default status if not set
        if (serviceRecord.getStatus() == null) {
            serviceRecord.setStatus(ServiceRecordStatus.DRAFT);
        }
        
        ServiceRecord saved = serviceRecordRepository.save(serviceRecord);
        log.info("Service record created with ID: {}", saved.getId());
        
        return saved;
    }

    /**
     * Update an existing service record.
     *
     * @param id the service record ID
     * @param serviceRecord the updated service record data
     * @return the updated service record
     * @throws RuntimeException if service record not found
     */
    public ServiceRecord updateServiceRecord(Long id, ServiceRecord serviceRecord) {
        log.info("Updating service record ID: {}", id);
        
        ServiceRecord existing = serviceRecordRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Service record not found with ID: " + id));
        
        // Check if already invoiced
        if (existing.getInvoiceItemId() != null) {
            throw new RuntimeException("Cannot update service record that has been invoiced");
        }
        
        // Update fields
        existing.setServiceDate(serviceRecord.getServiceDate());
        existing.setServicePeriodStart(serviceRecord.getServicePeriodStart());
        existing.setServicePeriodEnd(serviceRecord.getServicePeriodEnd());
        existing.setServiceCategoryId(serviceRecord.getServiceCategoryId());
        existing.setDescription(serviceRecord.getDescription());
        existing.setQuantity(serviceRecord.getQuantity());
        existing.setUnit(serviceRecord.getUnit());
        existing.setUnitPriceNet(serviceRecord.getUnitPriceNet());
        existing.setPerformedByUserId(serviceRecord.getPerformedByUserId());
        existing.setNotes(serviceRecord.getNotes());
        
        // Recalculate total
        existing.calculateTotalNet();
        
        ServiceRecord updated = serviceRecordRepository.save(existing);
        log.info("Service record updated: {}", id);
        
        return updated;
    }

    /**
     * Delete a service record.
     *
     * @param id the service record ID
     * @throws RuntimeException if service record not found or already invoiced
     */
    public void deleteServiceRecord(Long id) {
        log.info("Deleting service record ID: {}", id);
        
        ServiceRecord existing = serviceRecordRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Service record not found with ID: " + id));
        
        // Check if already invoiced
        if (existing.getInvoiceItemId() != null) {
            throw new RuntimeException("Cannot delete service record that has been invoiced");
        }
        
        serviceRecordRepository.deleteById(id);
        log.info("Service record deleted: {}", id);
    }

    /**
     * Get service record by ID.
     *
     * @param id the service record ID
     * @return optional service record
     */
    @Transactional(readOnly = true)
    public Optional<ServiceRecord> getServiceRecordById(Long id) {
        return serviceRecordRepository.findById(id);
    }

    /**
     * Get all service records.
     *
     * @return list of all service records
     */
    @Transactional(readOnly = true)
    public List<ServiceRecord> getAllServiceRecords() {
        return serviceRecordRepository.findAll();
    }

    /**
     * Get service records by contract ID.
     *
     * @param contractId the contract ID
     * @return list of service records
     */
    @Transactional(readOnly = true)
    public List<ServiceRecord> getServiceRecordsByContract(Long contractId) {
        return serviceRecordRepository.findByContractId(contractId);
    }

    /**
     * Get uninvoiced service records (approved but not yet invoiced).
     *
     * @return list of uninvoiced service records
     */
    @Transactional(readOnly = true)
    public List<ServiceRecord> getUninvoicedServiceRecords() {
        return serviceRecordRepository.findUninvoicedRecords(ServiceRecordStatus.APPROVED);
    }

    /**
     * Get uninvoiced service records for a specific contract.
     *
     * @param contractId the contract ID
     * @return list of uninvoiced service records
     */
    @Transactional(readOnly = true)
    public List<ServiceRecord> getUninvoicedServiceRecordsByContract(Long contractId) {
        return serviceRecordRepository.findUninvoicedRecordsByContract(
            contractId, 
            ServiceRecordStatus.APPROVED
        );
    }

    /**
     * Get uninvoiced service records for a contract within a date range.
     *
     * @param contractId the contract ID
     * @param startDate the start date
     * @param endDate the end date
     * @return list of uninvoiced service records
     */
    @Transactional(readOnly = true)
    public List<ServiceRecord> getUninvoicedServiceRecordsByContractAndDateRange(
        Long contractId, 
        LocalDate startDate, 
        LocalDate endDate
    ) {
        return serviceRecordRepository.findUninvoicedRecordsByContractAndDateRange(
            contractId, 
            ServiceRecordStatus.APPROVED, 
            startDate, 
            endDate
        );
    }

    /**
     * Approve a service record.
     *
     * @param id the service record ID
     * @param approvedByUserId the approving user ID
     * @return the approved service record
     * @throws RuntimeException if service record not found or already invoiced
     */
    public ServiceRecord approveServiceRecord(Long id, Long approvedByUserId) {
        log.info("Approving service record ID: {} by user: {}", id, approvedByUserId);
        
        ServiceRecord existing = serviceRecordRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Service record not found with ID: " + id));
        
        // Check if already invoiced
        if (existing.getInvoiceItemId() != null) {
            throw new RuntimeException("Cannot approve service record that has been invoiced");
        }
        
        // Check if already approved
        if (existing.getStatus() == ServiceRecordStatus.APPROVED) {
            throw new RuntimeException("Service record is already approved");
        }
        
        existing.setStatus(ServiceRecordStatus.APPROVED);
        existing.setApprovedByUserId(approvedByUserId);
        existing.setApprovedDate(LocalDateTime.now());
        
        ServiceRecord approved = serviceRecordRepository.save(existing);
        log.info("Service record approved: {}", id);
        
        return approved;
    }

    /**
     * Reject a service record.
     *
     * @param id the service record ID
     * @return the rejected service record
     * @throws RuntimeException if service record not found or already invoiced
     */
    public ServiceRecord rejectServiceRecord(Long id) {
        log.info("Rejecting service record ID: {}", id);
        
        ServiceRecord existing = serviceRecordRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Service record not found with ID: " + id));
        
        // Check if already invoiced
        if (existing.getInvoiceItemId() != null) {
            throw new RuntimeException("Cannot reject service record that has been invoiced");
        }
        
        existing.setStatus(ServiceRecordStatus.REJECTED);
        
        ServiceRecord rejected = serviceRecordRepository.save(existing);
        log.info("Service record rejected: {}", id);
        
        return rejected;
    }

    /**
     * Mark service record as invoiced.
     *
     * @param id the service record ID
     * @param invoiceItemId the invoice item ID
     * @throws RuntimeException if service record not found
     */
    public void markAsInvoiced(Long id, Long invoiceItemId) {
        log.info("Marking service record ID: {} as invoiced with invoice item ID: {}", id, invoiceItemId);
        
        ServiceRecord existing = serviceRecordRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Service record not found with ID: " + id));
        
        existing.setStatus(ServiceRecordStatus.INVOICED);
        existing.setInvoiceItemId(invoiceItemId);
        existing.setInvoicedDate(LocalDate.now());
        
        serviceRecordRepository.save(existing);
        log.info("Service record marked as invoiced: {}", id);
    }

    /**
     * Count uninvoiced service records for a contract.
     *
     * @param contractId the contract ID
     * @return count of uninvoiced records
     */
    @Transactional(readOnly = true)
    public Long countUninvoicedRecordsByContract(Long contractId) {
        return serviceRecordRepository.countUninvoicedRecordsByContract(
            contractId, 
            ServiceRecordStatus.APPROVED
        );
    }
}
