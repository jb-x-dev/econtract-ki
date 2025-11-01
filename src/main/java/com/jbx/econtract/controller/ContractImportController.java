package com.jbx.econtract.controller;

import com.jbx.econtract.model.entity.ContractImportQueue;
import com.jbx.econtract.model.entity.ImportBatch;
import com.jbx.econtract.service.ContractImportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/import")
@RequiredArgsConstructor
@Tag(name = "Contract Import", description = "Vertragsimport und Arbeitsvorrat")
public class ContractImportController {
    
    private final ContractImportService importService;
    
    @PostMapping("/single")
    @Operation(summary = "Einzelne Datei hochladen")
    public ResponseEntity<ContractImportQueue> uploadSingle(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "uploadedBy", defaultValue = "system") String uploadedBy) {
        try {
            ContractImportQueue result = importService.uploadSingleFile(file, uploadedBy);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PostMapping("/batch")
    @Operation(summary = "Mehrere Dateien als Batch hochladen")
    public ResponseEntity<ImportBatch> uploadBatch(
            @RequestParam("files") List<MultipartFile> files,
            @RequestParam(value = "batchName", required = false) String batchName,
            @RequestParam(value = "uploadedBy", defaultValue = "system") String uploadedBy) {
        try {
            ImportBatch result = importService.uploadBatch(files, batchName, uploadedBy);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/queue")
    @Operation(summary = "Arbeitsvorrat abrufen")
    public ResponseEntity<List<ContractImportQueue>> getWorkQueue() {
        return ResponseEntity.ok(importService.getWorkQueue());
    }
    
    @PostMapping("/queue/{id}/approve")
    @Operation(summary = "Queue-Item genehmigen")
    public ResponseEntity<Void> approve(
            @PathVariable Long id,
            @RequestParam(value = "reviewedBy", defaultValue = "system") String reviewedBy) {
        importService.approveQueueItem(id, reviewedBy);
        return ResponseEntity.ok().build();
    }
    
    @PostMapping("/queue/{id}/reject")
    @Operation(summary = "Queue-Item ablehnen")
    public ResponseEntity<Void> reject(
            @PathVariable Long id,
            @RequestParam(value = "reviewedBy", defaultValue = "system") String reviewedBy,
            @RequestParam(value = "reason", required = false) String reason) {
        importService.rejectQueueItem(id, reviewedBy, reason);
        return ResponseEntity.ok().build();
    }
    
    @GetMapping("/stats")
    @Operation(summary = "Import-Statistiken")
    public ResponseEntity<Map<String, Long>> getStatistics() {
        return ResponseEntity.ok(importService.getStatistics());
    }
    
    @PostMapping("/queue/{id}/reprocess")
    @Operation(summary = "Queue-Item erneut verarbeiten")
    public ResponseEntity<Void> reprocess(@PathVariable Long id) {
        importService.processQueueItem(id);
        return ResponseEntity.ok().build();
    }
    
    @PutMapping("/queue/{id}/update")
    @Operation(summary = "Queue-Item Daten aktualisieren")
    public ResponseEntity<ContractImportQueue> updateQueueItem(
            @PathVariable Long id,
            @RequestBody Map<String, Object> updatedData) {
        try {
            ContractImportQueue updated = importService.updateQueueItem(id, updatedData);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/history")
    @Operation(summary = "Import-Historie abrufen")
    public ResponseEntity<List<ContractImportQueue>> getHistory() {
        return ResponseEntity.ok(importService.getImportHistory());
    }
}
