package com.jbx.econtract.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entity representing an invoice.
 * 
 * @author jb-x Development Team
 * @version 1.0
 */
@Entity
@Table(name = "invoices")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Invoice number is required")
    @Size(max = 50, message = "Invoice number must not exceed 50 characters")
    @Column(name = "invoice_number", nullable = false, unique = true, length = 50)
    private String invoiceNumber;

    @NotNull(message = "Invoice type is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "invoice_type", nullable = false)
    private InvoiceType invoiceType = InvoiceType.SINGLE;

    @NotNull(message = "Invoice date is required")
    @PastOrPresent(message = "Invoice date cannot be in the future")
    @Column(name = "invoice_date", nullable = false)
    private LocalDate invoiceDate;

    @NotNull(message = "Billing period start is required")
    @Column(name = "billing_period_start", nullable = false)
    private LocalDate billingPeriodStart;

    @NotNull(message = "Billing period end is required")
    @Column(name = "billing_period_end", nullable = false)
    private LocalDate billingPeriodEnd;

    @NotNull(message = "Due date is required")
    @Column(name = "due_date", nullable = false)
    private LocalDate dueDate;

    @Column(name = "scheduled_date")
    private LocalDate scheduledDate;

    @Column(name = "contract_id")
    private Long contractId;

    @NotNull(message = "Partner ID is required")
    @Column(name = "partner_id", nullable = false)
    private Long partnerId;

    @NotBlank(message = "Partner name is required")
    @Size(max = 255, message = "Partner name must not exceed 255 characters")
    @Column(name = "partner_name", nullable = false)
    private String partnerName;

    @NotBlank(message = "Partner address is required")
    @Column(name = "partner_address", nullable = false, columnDefinition = "TEXT")
    private String partnerAddress;

    @Size(max = 50, message = "Partner tax ID must not exceed 50 characters")
    @Column(name = "partner_tax_id", length = 50)
    private String partnerTaxId;

    @NotNull(message = "Subtotal net is required")
    @DecimalMin(value = "0.00", message = "Subtotal net must be non-negative")
    @Column(name = "subtotal_net", nullable = false, precision = 15, scale = 2)
    private BigDecimal subtotalNet = BigDecimal.ZERO;

    @NotNull(message = "Tax rate is required")
    @DecimalMin(value = "0.00", message = "Tax rate must be non-negative")
    @DecimalMax(value = "100.00", message = "Tax rate must not exceed 100")
    @Column(name = "tax_rate", nullable = false, precision = 5, scale = 2)
    private BigDecimal taxRate = new BigDecimal("19.00");

    @NotNull(message = "Tax amount is required")
    @DecimalMin(value = "0.00", message = "Tax amount must be non-negative")
    @Column(name = "tax_amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal taxAmount = BigDecimal.ZERO;

    @NotNull(message = "Total gross is required")
    @DecimalMin(value = "0.00", message = "Total gross must be non-negative")
    @Column(name = "total_gross", nullable = false, precision = 15, scale = 2)
    private BigDecimal totalGross = BigDecimal.ZERO;

    @DecimalMin(value = "0.00", message = "Discount percentage must be non-negative")
    @DecimalMax(value = "100.00", message = "Discount percentage must not exceed 100")
    @Column(name = "discount_percentage", precision = 5, scale = 2)
    private BigDecimal discountPercentage;

    @DecimalMin(value = "0.00", message = "Discount amount must be non-negative")
    @Column(name = "discount_amount", precision = 15, scale = 2)
    private BigDecimal discountAmount;

    @NotBlank(message = "Currency is required")
    @Size(max = 3, message = "Currency must be 3 characters")
    @Column(name = "currency", nullable = false, length = 3)
    private String currency = "EUR";

    @NotNull(message = "Status is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private InvoiceStatus status = InvoiceStatus.DRAFT;

    @Size(max = 255, message = "Payment terms must not exceed 255 characters")
    @Column(name = "payment_terms")
    private String paymentTerms;

    @Size(max = 50, message = "Payment method must not exceed 50 characters")
    @Column(name = "payment_method", length = 50)
    private String paymentMethod;

    @Size(max = 100, message = "Bank account must not exceed 100 characters")
    @Column(name = "bank_account", length = 100)
    private String bankAccount;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "customer_notes", columnDefinition = "TEXT")
    private String customerNotes;

    @Size(max = 500, message = "PDF file path must not exceed 500 characters")
    @Column(name = "pdf_file_path", length = 500)
    private String pdfFilePath;

    @Column(name = "sent_date")
    private LocalDateTime sentDate;

    @Column(name = "sent_by_user_id")
    private Long sentByUserId;

    @Column(name = "approved_date")
    private LocalDateTime approvedDate;

    @Column(name = "approved_by_user_id")
    private Long approvedByUserId;

    @NotNull(message = "Created by user ID is required")
    @Column(name = "created_by_user_id", nullable = false)
    private Long createdByUserId;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "cancelled_date")
    private LocalDateTime cancelledDate;

    @Column(name = "cancelled_reason", columnDefinition = "TEXT")
    private String cancelledReason;

    @OneToMany(mappedBy = "invoiceId", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<InvoiceItem> items = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (status == null) {
            status = InvoiceStatus.DRAFT;
        }
        if (invoiceType == null) {
            invoiceType = InvoiceType.SINGLE;
        }
        if (currency == null) {
            currency = "EUR";
        }
        if (taxRate == null) {
            taxRate = new BigDecimal("19.00");
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    /**
     * Invoice type enum.
     */
    public enum InvoiceType {
        SINGLE,
        COLLECTIVE
    }

    /**
     * Invoice status enum.
     */
    public enum InvoiceStatus {
        DRAFT,
        APPROVED,
        SENT,
        PAID,
        OVERDUE,
        CANCELLED
    }

    /**
     * Validate billing period.
     */
    @AssertTrue(message = "Billing period end must be after or equal to start")
    public boolean isValidBillingPeriod() {
        if (billingPeriodStart == null || billingPeriodEnd == null) {
            return true;
        }
        return !billingPeriodEnd.isBefore(billingPeriodStart);
    }

    /**
     * Validate due date.
     */
    @AssertTrue(message = "Due date must be after or equal to invoice date")
    public boolean isValidDueDate() {
        if (invoiceDate == null || dueDate == null) {
            return true;
        }
        return !dueDate.isBefore(invoiceDate);
    }
}
