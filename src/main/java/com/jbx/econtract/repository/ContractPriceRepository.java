package com.jbx.econtract.repository;

import com.jbx.econtract.model.entity.ContractPrice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repository for ContractPrice entity.
 * 
 * @author jb-x Development Team
 * @version 1.0
 */
@Repository
public interface ContractPriceRepository extends JpaRepository<ContractPrice, Long> {

    /**
     * Find all prices for a contract.
     *
     * @param contractId the contract ID
     * @return list of contract prices
     */
    List<ContractPrice> findByContractId(Long contractId);

    /**
     * Find active prices for a contract.
     *
     * @param contractId the contract ID
     * @param isActive the active status
     * @return list of active contract prices
     */
    List<ContractPrice> findByContractIdAndIsActive(Long contractId, Boolean isActive);

    /**
     * Find price by contract and service category for a specific date.
     *
     * @param contractId the contract ID
     * @param serviceCategoryId the service category ID
     * @param date the date to check
     * @return optional contract price
     */
    @Query("SELECT cp FROM ContractPrice cp WHERE cp.contractId = :contractId " +
           "AND cp.serviceCategoryId = :serviceCategoryId " +
           "AND cp.isActive = true " +
           "AND cp.validFrom <= :date " +
           "AND (cp.validTo IS NULL OR cp.validTo >= :date) " +
           "ORDER BY cp.validFrom DESC")
    Optional<ContractPrice> findByContractIdAndServiceCategoryIdAndValidDate(
        @Param("contractId") Long contractId,
        @Param("serviceCategoryId") Long serviceCategoryId,
        @Param("date") LocalDate date
    );

    /**
     * Find all valid prices for a contract on a specific date.
     *
     * @param contractId the contract ID
     * @param date the date to check
     * @return list of valid contract prices
     */
    @Query("SELECT cp FROM ContractPrice cp WHERE cp.contractId = :contractId " +
           "AND cp.isActive = true " +
           "AND cp.validFrom <= :date " +
           "AND (cp.validTo IS NULL OR cp.validTo >= :date)")
    List<ContractPrice> findByContractIdAndValidDate(
        @Param("contractId") Long contractId,
        @Param("date") LocalDate date
    );

    /**
     * Find prices by service category.
     *
     * @param serviceCategoryId the service category ID
     * @return list of contract prices
     */
    List<ContractPrice> findByServiceCategoryId(Long serviceCategoryId);

    /**
     * Find all active prices.
     *
     * @param isActive the active status
     * @return list of active prices
     */
    List<ContractPrice> findByIsActive(Boolean isActive);
}
