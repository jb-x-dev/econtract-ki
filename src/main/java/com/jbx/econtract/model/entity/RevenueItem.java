package com.jbx.econtract.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Revenue Item Entity
 * Tracks revenue data imported from external sources
 */
@Entity
@Table(name = "revenue_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RevenueItem {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contract_id", nullable = false)
    private Contract contract;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invoice_id")
    private Invoice invoice;
    
    @Column(length = 500)
    private String description;
    
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;
    
    @Column(name = "revenue_date", nullable = false)
    private LocalDate revenueDate;
    
    @Column(name = "revenue_type", length = 50)
    @Enumerated(EnumType.STRING)
    private RevenueType revenueType;
    
    @Column(name = "imported_at")
    private LocalDateTime importedAt;
    
    @Column(name = "imported_by")
    private Long importedBy;
    
    @Column(columnDefinition = "TEXT")
    private String notes;
    
    @PrePersist
    protected void onCreate() {
        importedAt = LocalDateTime.now();
    }
    
    /**
     * Revenue Type Enum
     */
    public enum RevenueType {
        RECURRING,      // Recurring revenue (subscription, monthly fee)
        ONE_TIME,       // One-time revenue (setup fee, project)
        USAGE_BASED     // Usage-based revenue (per transaction, per unit)
    }
}
