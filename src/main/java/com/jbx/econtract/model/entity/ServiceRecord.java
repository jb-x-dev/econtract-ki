package com.jbx.econtract.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entity representing a service record for billing.
 * 
 * @author jb-x Development Team
 * @version 1.0
 */
@Entity
@Table(name = "service_records")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServiceRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Contract ID is required")
    @Column(name = "contract_id", nullable = false)
    private Long contractId;

    @NotNull(message = "Service date is required")
    @PastOrPresent(message = "Service date cannot be in the future")
    @Column(name = "service_date", nullable = false)
    private LocalDate serviceDate;

    @Column(name = "service_period_start")
    private LocalDate servicePeriodStart;

    @Column(name = "service_period_end")
    private LocalDate servicePeriodEnd;

    @Column(name = "service_category_id")
    private Long serviceCategoryId;

    @NotBlank(message = "Description is required")
    @Column(name = "description", nullable = false, columnDefinition = "TEXT")
    private String description;

    @NotNull(message = "Quantity is required")
    @DecimalMin(value = "0.01", message = "Quantity must be greater than 0")
    @Column(name = "quantity", nullable = false, precision = 10, scale = 2)
    private BigDecimal quantity;

    @NotBlank(message = "Unit is required")
    @Size(max = 50, message = "Unit must not exceed 50 characters")
    @Column(name = "unit", nullable = false, length = 50)
    private String unit;

    @NotNull(message = "Unit price is required")
    @DecimalMin(value = "0.00", message = "Unit price must be non-negative")
    @Column(name = "unit_price_net", nullable = false, precision = 15, scale = 2)
    private BigDecimal unitPriceNet;

    @NotNull(message = "Total net is required")
    @DecimalMin(value = "0.00", message = "Total net must be non-negative")
    @Column(name = "total_net", nullable = false, precision = 15, scale = 2)
    private BigDecimal totalNet;

    @NotNull(message = "Status is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ServiceRecordStatus status = ServiceRecordStatus.DRAFT;

    @Column(name = "invoice_item_id")
    private Long invoiceItemId;

    @Column(name = "invoiced_date")
    private LocalDate invoicedDate;

    @Column(name = "performed_by_user_id")
    private Long performedByUserId;

    @Column(name = "approved_by_user_id")
    private Long approvedByUserId;

    @Column(name = "approved_date")
    private LocalDateTime approvedDate;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @NotNull(message = "Created by user ID is required")
    @Column(name = "created_by_user_id", nullable = false)
    private Long createdByUserId;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (status == null) {
            status = ServiceRecordStatus.DRAFT;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    /**
     * Calculate total net amount based on quantity and unit price.
     */
    public void calculateTotalNet() {
        if (quantity != null && unitPriceNet != null) {
            totalNet = quantity.multiply(unitPriceNet);
        }
    }

    /**
     * Enum for service record status.
     */
    public enum ServiceRecordStatus {
        DRAFT,
        APPROVED,
        INVOICED,
        REJECTED
    }
}
