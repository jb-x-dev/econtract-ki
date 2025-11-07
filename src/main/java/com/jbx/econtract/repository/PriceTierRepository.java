package com.jbx.econtract.repository;

import com.jbx.econtract.model.entity.PriceTier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Repository for PriceTier entity.
 * 
 * @author jb-x Development Team
 * @version 1.0
 */
@Repository
public interface PriceTierRepository extends JpaRepository<PriceTier, Long> {

    /**
     * Find all price tiers for a contract price.
     *
     * @param contractPriceId the contract price ID
     * @return list of price tiers
     */
    List<PriceTier> findByContractPriceId(Long contractPriceId);

    /**
     * Find price tier for a specific quantity.
     *
     * @param contractPriceId the contract price ID
     * @param quantity the quantity
     * @return optional price tier
     */
    @Query("SELECT pt FROM PriceTier pt WHERE pt.contractPriceId = :contractPriceId " +
           "AND pt.minQuantity <= :quantity " +
           "AND (pt.maxQuantity IS NULL OR pt.maxQuantity >= :quantity) " +
           "ORDER BY pt.minQuantity DESC")
    Optional<PriceTier> findByContractPriceIdAndQuantity(
        @Param("contractPriceId") Long contractPriceId,
        @Param("quantity") BigDecimal quantity
    );

    /**
     * Delete all price tiers for a contract price.
     *
     * @param contractPriceId the contract price ID
     */
    void deleteByContractPriceId(Long contractPriceId);
}
