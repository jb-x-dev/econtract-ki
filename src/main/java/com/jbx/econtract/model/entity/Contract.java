package com.jbx.econtract.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Contract Entity
 * Repräsentiert einen Vertrag im System
 */
@Entity
@Table(name = "contracts")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Contract {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "contract_number", unique = true, nullable = false, length = 50)
    private String contractNumber;

    @Column(name = "title", nullable = false, length = 500)
    private String title;

    @Column(name = "contract_type", nullable = false, length = 100)
    private String contractType;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ContractStatus status = ContractStatus.DRAFT;

    @Column(name = "partner_name", nullable = false)
    private String partnerName;

    @Column(name = "partner_id")
    private Long partnerId;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "notice_period_days")
    private Integer noticePeriodDays;

    @Column(name = "auto_renewal")
    private Boolean autoRenewal = false;

    @Column(name = "contract_value", precision = 15, scale = 2)
    private BigDecimal contractValue;

    @Column(name = "currency", length = 3)
    private String currency = "EUR";

    @Column(name = "department", length = 100)
    private String department;

    // Billing fields
    @Column(name = "billing_cycle", length = 20)
    private String billingCycle;

    @Column(name = "billing_amount", precision = 15, scale = 2)
    private BigDecimal billingAmount;

    @Column(name = "billing_start_date")
    private LocalDate billingStartDate;

    @Column(name = "payment_term_days")
    private Integer paymentTermDays = 30;

    @Column(name = "owner_user_id", nullable = false)
    private Long ownerUserId;

    @Column(name = "created_by", nullable = false)
    private Long createdBy;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    /**
     * Contract Status Enum
     */
    public enum ContractStatus {
        DRAFT,
        IN_NEGOTIATION,
        IN_APPROVAL,
        APPROVED,
        ACTIVE,
        EXPIRED,
        TERMINATED
    }
    
    // Framework Contract Relationship
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "framework_contract_id")
    private FrameworkContract frameworkContract;
    
    @Column(name = "is_framework_child")
    private Boolean isFrameworkChild = false;
}
