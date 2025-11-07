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
 * Entity representing an invoice item (position).
 * 
 * @author jb-x Development Team
 * @version 1.0
 */
@Entity
@Table(name = "invoice_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Invoice ID is required")
    @Column(name = "invoice_id", nullable = false)
    private Long invoiceId;

    @NotNull(message = "Position number is required")
    @Min(value = 1, message = "Position number must be at least 1")
    @Column(name = "position_number", nullable = false)
    private Integer positionNumber;

    @Column(name = "service_record_id")
    private Long serviceRecordId;

    @Column(name = "contract_id")
    private Long contractId;

    @NotBlank(message = "Description is required")
    @Column(name = "description", nullable = false, columnDefinition = "TEXT")
    private String description;

    @Size(max = 100, message = "Service category must not exceed 100 characters")
    @Column(name = "service_category", length = 100)
    private String serviceCategory;

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

    @DecimalMin(value = "0.00", message = "Discount percentage must be non-negative")
    @DecimalMax(value = "100.00", message = "Discount percentage must not exceed 100")
    @Column(name = "discount_percentage", precision = 5, scale = 2)
    private BigDecimal discountPercentage;

    @DecimalMin(value = "0.00", message = "Discount amount must be non-negative")
    @Column(name = "discount_amount", precision = 15, scale = 2)
    private BigDecimal discountAmount;

    @NotNull(message = "Subtotal net is required")
    @DecimalMin(value = "0.00", message = "Subtotal net must be non-negative")
    @Column(name = "subtotal_net", nullable = false, precision = 15, scale = 2)
    private BigDecimal subtotalNet;

    @NotNull(message = "Tax rate is required")
    @DecimalMin(value = "0.00", message = "Tax rate must be non-negative")
    @DecimalMax(value = "100.00", message = "Tax rate must not exceed 100")
    @Column(name = "tax_rate", nullable = false, precision = 5, scale = 2)
    private BigDecimal taxRate = new BigDecimal("19.00");

    @NotNull(message = "Tax amount is required")
    @DecimalMin(value = "0.00", message = "Tax amount must be non-negative")
    @Column(name = "tax_amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal taxAmount;

    @NotNull(message = "Total gross is required")
    @DecimalMin(value = "0.00", message = "Total gross must be non-negative")
    @Column(name = "total_gross", nullable = false, precision = 15, scale = 2)
    private BigDecimal totalGross;

    @Column(name = "service_period_start")
    private LocalDate servicePeriodStart;

    @Column(name = "service_period_end")
    private LocalDate servicePeriodEnd;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (taxRate == null) {
            taxRate = new BigDecimal("19.00");
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    /**
     * Calculate subtotal net (quantity * unit price - discount).
     */
    public void calculateSubtotalNet() {
        if (quantity == null || unitPriceNet == null) {
            return;
        }
        
        BigDecimal baseAmount = quantity.multiply(unitPriceNet);
        
        if (discountAmount != null && discountAmount.compareTo(BigDecimal.ZERO) > 0) {
            subtotalNet = baseAmount.subtract(discountAmount);
        } else if (discountPercentage != null && discountPercentage.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal discount = baseAmount.multiply(discountPercentage).divide(new BigDecimal("100"), 2, java.math.RoundingMode.HALF_UP);
            discountAmount = discount;
            subtotalNet = baseAmount.subtract(discount);
        } else {
            subtotalNet = baseAmount;
        }
        
        if (subtotalNet.compareTo(BigDecimal.ZERO) < 0) {
            subtotalNet = BigDecimal.ZERO;
        }
    }

    /**
     * Calculate tax amount based on subtotal and tax rate.
     */
    public void calculateTaxAmount() {
        if (subtotalNet == null || taxRate == null) {
            return;
        }
        
        taxAmount = subtotalNet.multiply(taxRate).divide(new BigDecimal("100"), 2, java.math.RoundingMode.HALF_UP);
    }

    /**
     * Calculate total gross (subtotal + tax).
     */
    public void calculateTotalGross() {
        if (subtotalNet == null || taxAmount == null) {
            return;
        }
        
        totalGross = subtotalNet.add(taxAmount);
    }

    /**
     * Calculate all amounts (subtotal, tax, total).
     */
    public void calculateAll() {
        calculateSubtotalNet();
        calculateTaxAmount();
        calculateTotalGross();
    }
}
