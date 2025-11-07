package com.jbx.econtract.repository;

import com.jbx.econtract.model.entity.Invoice;
import com.jbx.econtract.model.entity.Invoice.InvoiceStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repository for Invoice entity.
 * 
 * @author jb-x Development Team
 * @version 1.0
 */
@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

    /**
     * Find invoice by invoice number.
     *
     * @param invoiceNumber the invoice number
     * @return optional invoice
     */
    Optional<Invoice> findByInvoiceNumber(String invoiceNumber);

    /**
     * Check if invoice number exists.
     *
     * @param invoiceNumber the invoice number
     * @return true if exists, false otherwise
     */
    boolean existsByInvoiceNumber(String invoiceNumber);

    /**
     * Find invoices by contract ID.
     *
     * @param contractId the contract ID
     * @return list of invoices
     */
    List<Invoice> findByContractId(Long contractId);

    /**
     * Find invoices by partner ID.
     *
     * @param partnerId the partner ID
     * @return list of invoices
     */
    List<Invoice> findByPartnerId(Long partnerId);

    /**
     * Find invoices by status.
     *
     * @param status the invoice status
     * @return list of invoices
     */
    List<Invoice> findByStatus(InvoiceStatus status);

    /**
     * Find invoices by invoice date range.
     *
     * @param startDate the start date
     * @param endDate the end date
     * @return list of invoices
     */
    @Query("SELECT i FROM Invoice i WHERE i.invoiceDate BETWEEN :startDate AND :endDate ORDER BY i.invoiceDate DESC")
    List<Invoice> findByInvoiceDateRange(
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );

    /**
     * Find overdue invoices.
     *
     * @param currentDate the current date
     * @return list of overdue invoices
     */
    @Query("SELECT i FROM Invoice i WHERE i.status IN ('SENT', 'APPROVED') AND i.dueDate < :currentDate")
    List<Invoice> findOverdueInvoices(@Param("currentDate") LocalDate currentDate);

    /**
     * Find invoices by billing period.
     *
     * @param startDate the billing period start
     * @param endDate the billing period end
     * @return list of invoices
     */
    @Query("SELECT i FROM Invoice i WHERE i.billingPeriodStart >= :startDate AND i.billingPeriodEnd <= :endDate")
    List<Invoice> findByBillingPeriod(
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );

    /**
     * Get the latest invoice number for a given year.
     *
     * @param year the year
     * @return the latest invoice number
     */
    @Query("SELECT i.invoiceNumber FROM Invoice i WHERE YEAR(i.invoiceDate) = :year ORDER BY i.invoiceNumber DESC")
    List<String> findLatestInvoiceNumberByYear(@Param("year") int year);

    /**
     * Find invoices created by a specific user.
     *
     * @param userId the user ID
     * @return list of invoices
     */
    List<Invoice> findByCreatedByUserId(Long userId);
}
