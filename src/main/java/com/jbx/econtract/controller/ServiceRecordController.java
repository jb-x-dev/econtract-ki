package com.jbx.econtract.controller;

import com.jbx.econtract.model.entity.ServiceRecord;
import com.jbx.econtract.service.ServiceRecordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * REST Controller for managing service records.
 * 
 * @author jb-x Development Team
 * @version 1.0
 */
@RestController
@RequestMapping("/api/v1/service-records")
@RequiredArgsConstructor
@Tag(name = "Service Records", description = "Service record management APIs")
public class ServiceRecordController {

    private final ServiceRecordService serviceRecordService;

    @PostMapping
    @Operation(summary = "Create a new service record")
    public ResponseEntity<ServiceRecord> createServiceRecord(@Valid @RequestBody ServiceRecord serviceRecord) {
        ServiceRecord created = serviceRecordService.createServiceRecord(serviceRecord);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(summary = "Get all service records")
    public ResponseEntity<List<ServiceRecord>> getAllServiceRecords() {
        List<ServiceRecord> records = serviceRecordService.getAllServiceRecords();
        return ResponseEntity.ok(records);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get service record by ID")
    public ResponseEntity<ServiceRecord> getServiceRecordById(@PathVariable Long id) {
        return serviceRecordService.getServiceRecordById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a service record")
    public ResponseEntity<ServiceRecord> updateServiceRecord(
        @PathVariable Long id,
        @Valid @RequestBody ServiceRecord serviceRecord
    ) {
        try {
            ServiceRecord updated = serviceRecordService.updateServiceRecord(id, serviceRecord);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a service record")
    public ResponseEntity<Void> deleteServiceRecord(@PathVariable Long id) {
        try {
            serviceRecordService.deleteServiceRecord(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/contract/{contractId}")
    @Operation(summary = "Get service records by contract ID")
    public ResponseEntity<List<ServiceRecord>> getServiceRecordsByContract(@PathVariable Long contractId) {
        List<ServiceRecord> records = serviceRecordService.getServiceRecordsByContract(contractId);
        return ResponseEntity.ok(records);
    }

    @GetMapping("/uninvoiced")
    @Operation(summary = "Get all uninvoiced service records")
    public ResponseEntity<List<ServiceRecord>> getUninvoicedServiceRecords() {
        List<ServiceRecord> records = serviceRecordService.getUninvoicedServiceRecords();
        return ResponseEntity.ok(records);
    }

    @GetMapping("/uninvoiced/contract/{contractId}")
    @Operation(summary = "Get uninvoiced service records by contract")
    public ResponseEntity<List<ServiceRecord>> getUninvoicedServiceRecordsByContract(@PathVariable Long contractId) {
        List<ServiceRecord> records = serviceRecordService.getUninvoicedServiceRecordsByContract(contractId);
        return ResponseEntity.ok(records);
    }

    @GetMapping("/uninvoiced/contract/{contractId}/date-range")
    @Operation(summary = "Get uninvoiced service records by contract and date range")
    public ResponseEntity<List<ServiceRecord>> getUninvoicedServiceRecordsByContractAndDateRange(
        @PathVariable Long contractId,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        List<ServiceRecord> records = serviceRecordService.getUninvoicedServiceRecordsByContractAndDateRange(
            contractId, startDate, endDate
        );
        return ResponseEntity.ok(records);
    }

    @PostMapping("/{id}/approve")
    @Operation(summary = "Approve a service record")
    public ResponseEntity<ServiceRecord> approveServiceRecord(
        @PathVariable Long id,
        @RequestParam Long approvedByUserId
    ) {
        try {
            ServiceRecord approved = serviceRecordService.approveServiceRecord(id, approvedByUserId);
            return ResponseEntity.ok(approved);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/{id}/reject")
    @Operation(summary = "Reject a service record")
    public ResponseEntity<ServiceRecord> rejectServiceRecord(@PathVariable Long id) {
        try {
            ServiceRecord rejected = serviceRecordService.rejectServiceRecord(id);
            return ResponseEntity.ok(rejected);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/contract/{contractId}/uninvoiced/count")
    @Operation(summary = "Count uninvoiced service records for a contract")
    public ResponseEntity<Long> countUninvoicedRecordsByContract(@PathVariable Long contractId) {
        Long count = serviceRecordService.countUninvoicedRecordsByContract(contractId);
        return ResponseEntity.ok(count);
    }
}
