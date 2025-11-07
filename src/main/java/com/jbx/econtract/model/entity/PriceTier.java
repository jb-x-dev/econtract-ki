package com.jbx.econtract.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entity representing a price tier (Staffelpreis) for quantity-based pricing.
 * 
 * @author jb-x Development Team
 * @version 1.0
 */
@Entity
@Table(name = "price_tiers")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PriceTier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Contract price ID is required")
    @Column(name = "contract_price_id", nullable = false)
    private Long contractPriceId;

    @NotNull(message = "Minimum quantity is required")
    @DecimalMin(value = "0.00", message = "Minimum quantity must be non-negative")
    @Column(name = "min_quantity", nullable = false, precision = 10, scale = 2)
    private BigDecimal minQuantity;

    @Column(name = "max_quantity", precision = 10, scale = 2)
    private BigDecimal maxQuantity;

    @NotNull(message = "Unit price is required")
    @DecimalMin(value = "0.00", message = "Unit price must be non-negative")
    @Column(name = "unit_price_net", nullable = false, precision = 15, scale = 2)
    private BigDecimal unitPriceNet;

    @DecimalMin(value = "0.00", message = "Discount percentage must be non-negative")
    @DecimalMax(value = "100.00", message = "Discount percentage must not exceed 100")
    @Column(name = "discount_percentage", precision = 5, scale = 2)
    private BigDecimal discountPercentage;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
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
     * Check if a quantity falls within this tier's range.
     *
     * @param quantity the quantity to check
     * @return true if quantity is in range, false otherwise
     */
    public boolean isQuantityInRange(BigDecimal quantity) {
        boolean aboveMin = quantity.compareTo(minQuantity) >= 0;
        boolean belowMax = maxQuantity == null || quantity.compareTo(maxQuantity) <= 0;
        return aboveMin && belowMax;
    }
}
