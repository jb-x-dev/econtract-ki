package com.jbx.econtract.controller;

import com.jbx.econtract.model.entity.Contract;
import com.jbx.econtract.model.entity.FrameworkContract;
import com.jbx.econtract.service.FrameworkContractService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/framework-contracts")
@Tag(name = "Framework Contracts", description = "Rahmenverträge und Einzelverträge")
public class FrameworkContractController {
    
    @Autowired
    private FrameworkContractService frameworkContractService;
    
    @GetMapping
    @Operation(summary = "Alle Rahmenverträge abrufen")
    public ResponseEntity<Page<FrameworkContract>> getAllFrameworkContracts(Pageable pageable) {
        return ResponseEntity.ok(frameworkContractService.findAll(pageable));
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Rahmenvertrag nach ID")
    public ResponseEntity<FrameworkContract> getFrameworkContract(@PathVariable Long id) {
        return frameworkContractService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    @Operation(summary = "Neuen Rahmenvertrag erstellen")
    public ResponseEntity<FrameworkContract> createFrameworkContract(@RequestBody FrameworkContract frameworkContract) {
        return ResponseEntity.ok(frameworkContractService.save(frameworkContract));
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Rahmenvertrag aktualisieren")
    public ResponseEntity<FrameworkContract> updateFrameworkContract(
            @PathVariable Long id,
            @RequestBody FrameworkContract frameworkContract) {
        frameworkContract.setId(id);
        return ResponseEntity.ok(frameworkContractService.save(frameworkContract));
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Rahmenvertrag löschen")
    public ResponseEntity<Void> deleteFrameworkContract(@PathVariable Long id) {
        frameworkContractService.deleteById(id);
        return ResponseEntity.ok().build();
    }
    
    @GetMapping("/{id}/child-contracts")
    @Operation(summary = "Alle Einzelverträge eines Rahmenvertrags")
    public ResponseEntity<List<Contract>> getChildContracts(@PathVariable Long id) {
        return ResponseEntity.ok(frameworkContractService.getChildContracts(id));
    }
    
    @PostMapping("/{frameworkId}/add-child/{contractId}")
    @Operation(summary = "Einzelvertrag zu Rahmenvertrag hinzufügen")
    public ResponseEntity<Contract> addChildContract(
            @PathVariable Long frameworkId,
            @PathVariable Long contractId) {
        return ResponseEntity.ok(frameworkContractService.addChildContract(frameworkId, contractId));
    }
    
    @DeleteMapping("/{frameworkId}/remove-child/{contractId}")
    @Operation(summary = "Einzelvertrag von Rahmenvertrag entfernen")
    public ResponseEntity<Void> removeChildContract(
            @PathVariable Long frameworkId,
            @PathVariable Long contractId) {
        frameworkContractService.removeChildContract(frameworkId, contractId);
        return ResponseEntity.ok().build();
    }
    
    @GetMapping("/{id}/volume-usage")
    @Operation(summary = "Volumen-Nutzung des Rahmenvertrags")
    public ResponseEntity<Map<String, Object>> getVolumeUsage(@PathVariable Long id) {
        return ResponseEntity.ok(frameworkContractService.getVolumeUsage(id));
    }
    
    @GetMapping("/search")
    @Operation(summary = "Rahmenverträge durchsuchen")
    public ResponseEntity<List<FrameworkContract>> searchFrameworkContracts(@RequestParam String q) {
        return ResponseEntity.ok(frameworkContractService.search(q));
    }
    
    @GetMapping("/stats")
    @Operation(summary = "Rahmenvertrags-Statistiken")
    public ResponseEntity<Map<String, Object>> getFrameworkStats() {
        return ResponseEntity.ok(frameworkContractService.getStatistics());
    }
}

