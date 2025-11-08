package com.jbx.econtract.repository;

import com.jbx.econtract.model.entity.RevenueItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Repository for RevenueItem
 */
@Repository
public interface RevenueItemRepository extends JpaRepository<RevenueItem, Long> {
    
    /**
     * Find revenue items by contract
     */
    List<RevenueItem> findByContractIdOrderByRevenueDateDesc(Long contractId);
    
    /**
     * Find revenue items by invoice
     */
    List<RevenueItem> findByInvoiceId(Long invoiceId);
    
    /**
     * Find unassigned revenue items for a contract
     */
    List<RevenueItem> findByContractIdAndInvoiceIsNull(Long contractId);
    
    /**
     * Find revenue items by date range
     */
    List<RevenueItem> findByContractIdAndRevenueDateBetween(Long contractId, LocalDate startDate, LocalDate endDate);
    
    /**
     * Calculate total revenue for a contract
     */
    @Query("SELECT COALESCE(SUM(r.amount), 0) FROM RevenueItem r WHERE r.contract.id = :contractId")
    BigDecimal calculateTotalRevenue(Long contractId);
    
    /**
     * Calculate revenue by type
     */
    @Query("SELECT COALESCE(SUM(r.amount), 0) FROM RevenueItem r WHERE r.contract.id = :contractId AND r.revenueType = :type")
    BigDecimal calculateRevenueByType(Long contractId, RevenueItem.RevenueType type);
}
