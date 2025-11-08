package com.jbx.econtract.controller;

import com.jbx.econtract.model.dto.ContractDTO;
import com.jbx.econtract.service.ContractService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * REST Controller für Vertragsverwaltung
 */
@RestController
@RequestMapping("/api/v1/contracts")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Contract Management", description = "APIs für Vertragsverwaltung")
@CrossOrigin(origins = "*")
public class ContractController {

    private final ContractService contractService;

    /**
     * Erstellt einen neuen Vertrag
     */
    @PostMapping
    @Operation(summary = "Neuen Vertrag erstellen")
    public ResponseEntity<ContractDTO> createContract(@RequestBody ContractDTO dto) {
        log.info("POST /api/v1/contracts - Creating new contract");
        ContractDTO created = contractService.createContract(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * Holt einen Vertrag nach ID
     */
    @GetMapping("/{id}")
    @Operation(summary = "Vertrag nach ID abrufen")
    public ResponseEntity<ContractDTO> getContract(@PathVariable Long id) {
        log.info("GET /api/v1/contracts/{} - Fetching contract", id);
        ContractDTO contract = contractService.getContractById(id);
        return ResponseEntity.ok(contract);
    }

    /**
     * Holt alle Verträge ohne Pagination (für Dashboard)
     */
    @GetMapping(params = "all")
    @Operation(summary = "Alle Verträge ohne Pagination")
    public ResponseEntity<java.util.List<ContractDTO>> getAllContractsUnpaged(@RequestParam(required = false) String all) {
        log.info("GET /api/v1/contracts?all - Fetching all contracts");
        java.util.List<ContractDTO> contracts = contractService.getAllContractsUnpaged();
        return ResponseEntity.ok(contracts);
    }

    /**
     * Holt alle Verträge (paginiert)
     */
    @GetMapping
    @Operation(summary = "Alle Verträge abrufen (paginiert)")
    public ResponseEntity<Page<ContractDTO>> getAllContracts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDir,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String contractType) {
        
        log.info("GET /api/v1/contracts - page: {}, size: {}", page, size);
        
        Sort sort = sortDir.equalsIgnoreCase("ASC") 
                ? Sort.by(sortBy).ascending() 
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<ContractDTO> contracts;
        if (status != null) {
            contracts = contractService.getContractsByStatus(status, pageable);
        } else if (contractType != null) {
            contracts = contractService.getContractsByType(contractType, pageable);
        } else {
            contracts = contractService.getAllContracts(pageable);
        }
        
        return ResponseEntity.ok(contracts);
    }

    /**
     * Aktualisiert einen Vertrag
     */
    @PutMapping("/{id}")
    @Operation(summary = "Vertrag aktualisieren")
    public ResponseEntity<ContractDTO> updateContract(
            @PathVariable Long id,
            @RequestBody ContractDTO dto) {
        log.info("PUT /api/v1/contracts/{} - Updating contract", id);
        ContractDTO updated = contractService.updateContract(id, dto);
        return ResponseEntity.ok(updated);
    }

    /**
     * Löscht einen Vertrag
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Vertrag löschen")
    public ResponseEntity<Void> deleteContract(@PathVariable Long id) {
        log.info("DELETE /api/v1/contracts/{} - Deleting contract", id);
        contractService.deleteContract(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Reicht Vertrag zur Genehmigung ein
     */
    @PostMapping("/{id}/submit-for-approval")
    @Operation(summary = "Vertrag zur Genehmigung einreichen")
    public ResponseEntity<ContractDTO> submitForApproval(@PathVariable Long id) {
        log.info("POST /api/v1/contracts/{}/submit-for-approval", id);
        ContractDTO contract = contractService.submitForApproval(id);
        return ResponseEntity.ok(contract);
    }

    /**
     * Sucht Verträge
     */
    @GetMapping("/search")
    @Operation(summary = "Verträge durchsuchen")
    public ResponseEntity<Page<ContractDTO>> searchContracts(
            @RequestParam String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        log.info("GET /api/v1/contracts/search?q={}", q);
        Pageable pageable = PageRequest.of(page, size);
        Page<ContractDTO> results = contractService.searchContracts(q, pageable);
        return ResponseEntity.ok(results);
    }

    /**
     * Dashboard-Statistiken
     */
    @GetMapping("/stats")
    @Operation(summary = "Dashboard-Statistiken abrufen")
    public ResponseEntity<Map<String, Object>> getStatistics() {
        log.info("GET /api/v1/contracts/stats");
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("total", contractService.countAll());
        stats.put("draft", contractService.countByStatus("DRAFT"));
        stats.put("in_approval", contractService.countByStatus("IN_APPROVAL"));
        stats.put("active", contractService.countByStatus("ACTIVE"));
        stats.put("expired", contractService.countByStatus("EXPIRED"));
        
        return ResponseEntity.ok(stats);
    }
}

