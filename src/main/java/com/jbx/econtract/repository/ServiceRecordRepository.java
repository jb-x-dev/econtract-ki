package com.jbx.econtract.repository;

import com.jbx.econtract.model.entity.ServiceRecord;
import com.jbx.econtract.model.entity.ServiceRecord.ServiceRecordStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * Repository for ServiceRecord entity.
 * 
 * @author jb-x Development Team
 * @version 1.0
 */
@Repository
public interface ServiceRecordRepository extends JpaRepository<ServiceRecord, Long> {

    /**
     * Find all service records by contract ID.
     *
     * @param contractId the contract ID
     * @return list of service records
     */
    List<ServiceRecord> findByContractId(Long contractId);

    /**
     * Find all service records by status.
     *
     * @param status the service record status
     * @return list of service records
     */
    List<ServiceRecord> findByStatus(ServiceRecordStatus status);

    /**
     * Find all uninvoiced service records (approved but not yet invoiced).
     *
     * @param status the status (should be APPROVED)
     * @return list of uninvoiced service records
     */
    @Query("SELECT sr FROM ServiceRecord sr WHERE sr.status = :status AND sr.invoiceItemId IS NULL")
    List<ServiceRecord> findUninvoicedRecords(@Param("status") ServiceRecordStatus status);

    /**
     * Find uninvoiced service records for a specific contract.
     *
     * @param contractId the contract ID
     * @param status the status (should be APPROVED)
     * @return list of uninvoiced service records
     */
    @Query("SELECT sr FROM ServiceRecord sr WHERE sr.contractId = :contractId AND sr.status = :status AND sr.invoiceItemId IS NULL")
    List<ServiceRecord> findUninvoicedRecordsByContract(
        @Param("contractId") Long contractId,
        @Param("status") ServiceRecordStatus status
    );

    /**
     * Find service records by contract and date range.
     *
     * @param contractId the contract ID
     * @param startDate the start date
     * @param endDate the end date
     * @return list of service records
     */
    @Query("SELECT sr FROM ServiceRecord sr WHERE sr.contractId = :contractId AND sr.serviceDate BETWEEN :startDate AND :endDate")
    List<ServiceRecord> findByContractIdAndDateRange(
        @Param("contractId") Long contractId,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );

    /**
     * Find uninvoiced service records by contract and date range.
     *
     * @param contractId the contract ID
     * @param status the status (should be APPROVED)
     * @param startDate the start date
     * @param endDate the end date
     * @return list of uninvoiced service records
     */
    @Query("SELECT sr FROM ServiceRecord sr WHERE sr.contractId = :contractId AND sr.status = :status AND sr.invoiceItemId IS NULL AND sr.serviceDate BETWEEN :startDate AND :endDate")
    List<ServiceRecord> findUninvoicedRecordsByContractAndDateRange(
        @Param("contractId") Long contractId,
        @Param("status") ServiceRecordStatus status,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );

    /**
     * Find service records by service category.
     *
     * @param serviceCategoryId the service category ID
     * @return list of service records
     */
    List<ServiceRecord> findByServiceCategoryId(Long serviceCategoryId);

    /**
     * Find service records created by a specific user.
     *
     * @param userId the user ID
     * @return list of service records
     */
    List<ServiceRecord> findByCreatedByUserId(Long userId);

    /**
     * Count uninvoiced service records for a contract.
     *
     * @param contractId the contract ID
     * @param status the status (should be APPROVED)
     * @return count of uninvoiced records
     */
    @Query("SELECT COUNT(sr) FROM ServiceRecord sr WHERE sr.contractId = :contractId AND sr.status = :status AND sr.invoiceItemId IS NULL")
    Long countUninvoicedRecordsByContract(
        @Param("contractId") Long contractId,
        @Param("status") ServiceRecordStatus status
    );
}
