package com.jbx.econtract.service;

import com.jbx.econtract.model.entity.ContractPrice;
import com.jbx.econtract.model.entity.PriceTier;
import com.jbx.econtract.repository.ContractPriceRepository;
import com.jbx.econtract.repository.PriceTierRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Service for managing contract prices and pricing logic.
 * 
 * @author jb-x Development Team
 * @version 1.0
 */
@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class PricingService {

    private final ContractPriceRepository contractPriceRepository;
    private final PriceTierRepository priceTierRepository;

    /**
     * Create a new contract price.
     *
     * @param contractPrice the contract price to create
     * @return the created contract price
     */
    public ContractPrice createContractPrice(ContractPrice contractPrice) {
        log.info("Creating new contract price for contract ID: {}", contractPrice.getContractId());
        
        ContractPrice saved = contractPriceRepository.save(contractPrice);
        log.info("Contract price created with ID: {}", saved.getId());
        
        return saved;
    }

    /**
     * Update an existing contract price.
     *
     * @param id the contract price ID
     * @param contractPrice the updated contract price data
     * @return the updated contract price
     * @throws RuntimeException if contract price not found
     */
    public ContractPrice updateContractPrice(Long id, ContractPrice contractPrice) {
        log.info("Updating contract price ID: {}", id);
        
        ContractPrice existing = contractPriceRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Contract price not found with ID: " + id));
        
        // Update fields
        existing.setServiceCategoryId(contractPrice.getServiceCategoryId());
        existing.setDescription(contractPrice.getDescription());
        existing.setUnit(contractPrice.getUnit());
        existing.setUnitPriceNet(contractPrice.getUnitPriceNet());
        existing.setValidFrom(contractPrice.getValidFrom());
        existing.setValidTo(contractPrice.getValidTo());
        existing.setIsActive(contractPrice.getIsActive());
        
        ContractPrice updated = contractPriceRepository.save(existing);
        log.info("Contract price updated: {}", id);
        
        return updated;
    }

    /**
     * Delete a contract price.
     *
     * @param id the contract price ID
     */
    public void deleteContractPrice(Long id) {
        log.info("Deleting contract price ID: {}", id);
        
        // Delete associated price tiers first
        priceTierRepository.deleteByContractPriceId(id);
        
        contractPriceRepository.deleteById(id);
        log.info("Contract price deleted: {}", id);
    }

    /**
     * Get contract price by ID.
     *
     * @param id the contract price ID
     * @return optional contract price
     */
    @Transactional(readOnly = true)
    public Optional<ContractPrice> getContractPriceById(Long id) {
        return contractPriceRepository.findById(id);
    }

    /**
     * Get all contract prices.
     *
     * @return list of all contract prices
     */
    @Transactional(readOnly = true)
    public List<ContractPrice> getAllContractPrices() {
        return contractPriceRepository.findAll();
    }

    /**
     * Get contract prices by contract ID.
     *
     * @param contractId the contract ID
     * @return list of contract prices
     */
    @Transactional(readOnly = true)
    public List<ContractPrice> getContractPricesByContract(Long contractId) {
        return contractPriceRepository.findByContractId(contractId);
    }

    /**
     * Get active contract prices by contract ID.
     *
     * @param contractId the contract ID
     * @return list of active contract prices
     */
    @Transactional(readOnly = true)
    public List<ContractPrice> getActiveContractPricesByContract(Long contractId) {
        return contractPriceRepository.findByContractIdAndIsActive(contractId, true);
    }

    /**
     * Find unit price for a contract, service category, and date.
     *
     * @param contractId the contract ID
     * @param serviceCategoryId the service category ID
     * @param date the date
     * @return the unit price
     * @throws RuntimeException if no price found
     */
    @Transactional(readOnly = true)
    public BigDecimal findUnitPrice(Long contractId, Long serviceCategoryId, LocalDate date) {
        log.debug("Finding unit price for contract: {}, category: {}, date: {}", 
                  contractId, serviceCategoryId, date);
        
        ContractPrice price = contractPriceRepository
            .findByContractIdAndServiceCategoryIdAndValidDate(contractId, serviceCategoryId, date)
            .orElseThrow(() -> new RuntimeException(
                String.format("No price found for contract %d, category %d on date %s", 
                              contractId, serviceCategoryId, date)
            ));
        
        return price.getUnitPriceNet();
    }

    /**
     * Find unit price with quantity-based pricing (price tiers).
     *
     * @param contractId the contract ID
     * @param serviceCategoryId the service category ID
     * @param date the date
     * @param quantity the quantity
     * @return the unit price
     * @throws RuntimeException if no price found
     */
    @Transactional(readOnly = true)
    public BigDecimal findUnitPriceWithTiers(
        Long contractId, 
        Long serviceCategoryId, 
        LocalDate date, 
        BigDecimal quantity
    ) {
        log.debug("Finding unit price with tiers for contract: {}, category: {}, date: {}, quantity: {}", 
                  contractId, serviceCategoryId, date, quantity);
        
        ContractPrice price = contractPriceRepository
            .findByContractIdAndServiceCategoryIdAndValidDate(contractId, serviceCategoryId, date)
            .orElseThrow(() -> new RuntimeException(
                String.format("No price found for contract %d, category %d on date %s", 
                              contractId, serviceCategoryId, date)
            ));
        
        // Check for price tiers
        Optional<PriceTier> tier = priceTierRepository
            .findByContractPriceIdAndQuantity(price.getId(), quantity);
        
        if (tier.isPresent()) {
            log.debug("Found price tier with unit price: {}", tier.get().getUnitPriceNet());
            return tier.get().getUnitPriceNet();
        }
        
        // Return base price if no tier found
        log.debug("No price tier found, using base price: {}", price.getUnitPriceNet());
        return price.getUnitPriceNet();
    }

    /**
     * Create a price tier.
     *
     * @param priceTier the price tier to create
     * @return the created price tier
     */
    public PriceTier createPriceTier(PriceTier priceTier) {
        log.info("Creating new price tier for contract price ID: {}", priceTier.getContractPriceId());
        
        PriceTier saved = priceTierRepository.save(priceTier);
        log.info("Price tier created with ID: {}", saved.getId());
        
        return saved;
    }

    /**
     * Update a price tier.
     *
     * @param id the price tier ID
     * @param priceTier the updated price tier data
     * @return the updated price tier
     * @throws RuntimeException if price tier not found
     */
    public PriceTier updatePriceTier(Long id, PriceTier priceTier) {
        log.info("Updating price tier ID: {}", id);
        
        PriceTier existing = priceTierRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Price tier not found with ID: " + id));
        
        existing.setMinQuantity(priceTier.getMinQuantity());
        existing.setMaxQuantity(priceTier.getMaxQuantity());
        existing.setUnitPriceNet(priceTier.getUnitPriceNet());
        existing.setDiscountPercentage(priceTier.getDiscountPercentage());
        
        PriceTier updated = priceTierRepository.save(existing);
        log.info("Price tier updated: {}", id);
        
        return updated;
    }

    /**
     * Delete a price tier.
     *
     * @param id the price tier ID
     */
    public void deletePriceTier(Long id) {
        log.info("Deleting price tier ID: {}", id);
        priceTierRepository.deleteById(id);
        log.info("Price tier deleted: {}", id);
    }

    /**
     * Get price tiers by contract price ID.
     *
     * @param contractPriceId the contract price ID
     * @return list of price tiers
     */
    @Transactional(readOnly = true)
    public List<PriceTier> getPriceTiersByContractPrice(Long contractPriceId) {
        return priceTierRepository.findByContractPriceId(contractPriceId);
    }
}
