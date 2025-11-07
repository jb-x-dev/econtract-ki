package com.jbx.econtract.controller;

import com.jbx.econtract.model.entity.ContractPrice;
import com.jbx.econtract.model.entity.PriceTier;
import com.jbx.econtract.service.PricingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * REST Controller for managing contract prices and pricing.
 * 
 * @author jb-x Development Team
 * @version 1.0
 */
@RestController
@RequestMapping("/api/v1/pricing")
@RequiredArgsConstructor
@Tag(name = "Pricing", description = "Price management APIs")
public class PricingController {

    private final PricingService pricingService;

    // ========== Contract Prices ==========

    @PostMapping("/contract-prices")
    @Operation(summary = "Create a new contract price")
    public ResponseEntity<ContractPrice> createContractPrice(@Valid @RequestBody ContractPrice contractPrice) {
        ContractPrice created = pricingService.createContractPrice(contractPrice);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping("/contract-prices")
    @Operation(summary = "Get all contract prices")
    public ResponseEntity<List<ContractPrice>> getAllContractPrices() {
        List<ContractPrice> prices = pricingService.getAllContractPrices();
        return ResponseEntity.ok(prices);
    }

    @GetMapping("/contract-prices/{id}")
    @Operation(summary = "Get contract price by ID")
    public ResponseEntity<ContractPrice> getContractPriceById(@PathVariable Long id) {
        return pricingService.getContractPriceById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/contract-prices/{id}")
    @Operation(summary = "Update a contract price")
    public ResponseEntity<ContractPrice> updateContractPrice(
        @PathVariable Long id,
        @Valid @RequestBody ContractPrice contractPrice
    ) {
        try {
            ContractPrice updated = pricingService.updateContractPrice(id, contractPrice);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/contract-prices/{id}")
    @Operation(summary = "Delete a contract price")
    public ResponseEntity<Void> deleteContractPrice(@PathVariable Long id) {
        pricingService.deleteContractPrice(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/contracts/{contractId}/prices")
    @Operation(summary = "Get all prices for a contract")
    public ResponseEntity<List<ContractPrice>> getContractPricesByContract(@PathVariable Long contractId) {
        List<ContractPrice> prices = pricingService.getContractPricesByContract(contractId);
        return ResponseEntity.ok(prices);
    }

    @GetMapping("/contracts/{contractId}/prices/active")
    @Operation(summary = "Get active prices for a contract")
    public ResponseEntity<List<ContractPrice>> getActiveContractPricesByContract(@PathVariable Long contractId) {
        List<ContractPrice> prices = pricingService.getActiveContractPricesByContract(contractId);
        return ResponseEntity.ok(prices);
    }

    @GetMapping("/contracts/{contractId}/unit-price")
    @Operation(summary = "Find unit price for a contract, service category, and date")
    public ResponseEntity<BigDecimal> findUnitPrice(
        @PathVariable Long contractId,
        @RequestParam Long serviceCategoryId,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        try {
            BigDecimal price = pricingService.findUnitPrice(contractId, serviceCategoryId, date);
            return ResponseEntity.ok(price);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/contracts/{contractId}/unit-price-with-tiers")
    @Operation(summary = "Find unit price with quantity-based pricing (price tiers)")
    public ResponseEntity<BigDecimal> findUnitPriceWithTiers(
        @PathVariable Long contractId,
        @RequestParam Long serviceCategoryId,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
        @RequestParam BigDecimal quantity
    ) {
        try {
            BigDecimal price = pricingService.findUnitPriceWithTiers(
                contractId, serviceCategoryId, date, quantity
            );
            return ResponseEntity.ok(price);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // ========== Price Tiers ==========

    @PostMapping("/price-tiers")
    @Operation(summary = "Create a new price tier")
    public ResponseEntity<PriceTier> createPriceTier(@Valid @RequestBody PriceTier priceTier) {
        PriceTier created = pricingService.createPriceTier(priceTier);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PutMapping("/price-tiers/{id}")
    @Operation(summary = "Update a price tier")
    public ResponseEntity<PriceTier> updatePriceTier(
        @PathVariable Long id,
        @Valid @RequestBody PriceTier priceTier
    ) {
        try {
            PriceTier updated = pricingService.updatePriceTier(id, priceTier);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/price-tiers/{id}")
    @Operation(summary = "Delete a price tier")
    public ResponseEntity<Void> deletePriceTier(@PathVariable Long id) {
        pricingService.deletePriceTier(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/contract-prices/{contractPriceId}/tiers")
    @Operation(summary = "Get price tiers for a contract price")
    public ResponseEntity<List<PriceTier>> getPriceTiersByContractPrice(@PathVariable Long contractPriceId) {
        List<PriceTier> tiers = pricingService.getPriceTiersByContractPrice(contractPriceId);
        return ResponseEntity.ok(tiers);
    }
}
