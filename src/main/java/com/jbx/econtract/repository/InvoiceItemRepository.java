package com.jbx.econtract.repository;

import com.jbx.econtract.model.entity.InvoiceItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for InvoiceItem entity.
 * 
 * @author jb-x Development Team
 * @version 1.0
 */
@Repository
public interface InvoiceItemRepository extends JpaRepository<InvoiceItem, Long> {

    /**
     * Find all items for an invoice.
     *
     * @param invoiceId the invoice ID
     * @return list of invoice items
     */
    List<InvoiceItem> findByInvoiceIdOrderByPositionNumber(Long invoiceId);

    /**
     * Find invoice item by service record ID.
     *
     * @param serviceRecordId the service record ID
     * @return list of invoice items
     */
    List<InvoiceItem> findByServiceRecordId(Long serviceRecordId);

    /**
     * Find invoice items by contract ID.
     *
     * @param contractId the contract ID
     * @return list of invoice items
     */
    List<InvoiceItem> findByContractId(Long contractId);

    /**
     * Get the highest position number for an invoice.
     *
     * @param invoiceId the invoice ID
     * @return the highest position number
     */
    @Query("SELECT MAX(ii.positionNumber) FROM InvoiceItem ii WHERE ii.invoiceId = :invoiceId")
    Integer findMaxPositionNumberByInvoiceId(@Param("invoiceId") Long invoiceId);

    /**
     * Delete all items for an invoice.
     *
     * @param invoiceId the invoice ID
     */
    void deleteByInvoiceId(Long invoiceId);
}
