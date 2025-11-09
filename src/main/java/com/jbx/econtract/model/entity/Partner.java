package com.jbx.econtract.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Partner Entity
 * Represents a business partner (customer, supplier, service provider)
 */
@Entity
@Table(name = "partners")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Partner {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "partner_type", nullable = false, length = 50)
    private PartnerType partnerType;

    @Column(name = "partner_number", unique = true, length = 50)
    private String partnerNumber;

    // Contact Information
    @Column(name = "email")
    private String email;

    @Column(name = "phone", length = 50)
    private String phone;

    @Column(name = "website")
    private String website;

    // Address
    @Column(name = "street")
    private String street;

    @Column(name = "city", length = 100)
    private String city;

    @Column(name = "postal_code", length = 20)
    private String postalCode;

    @Column(name = "country", length = 100)
    private String country;

    // Business Information
    @Column(name = "tax_id", length = 50)
    private String taxId;

    @Column(name = "vat_id", length = 50)
    private String vatId;

    @Column(name = "company_registration_number", length = 50)
    private String companyRegistrationNumber;

    // Banking Information
    @Column(name = "bank_name")
    private String bankName;

    @Column(name = "iban", length = 50)
    private String iban;

    @Column(name = "bic", length = 20)
    private String bic;

    // Status
    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    // Audit Fields
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
     * Partner Type Enum
     */
    public enum PartnerType {
        CUSTOMER,
        SUPPLIER,
        SERVICE_PROVIDER,
        OTHER
    }
}
