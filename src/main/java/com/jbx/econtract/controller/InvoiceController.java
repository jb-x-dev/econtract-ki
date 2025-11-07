package com.jbx.econtract.controller;

import com.jbx.econtract.model.entity.Invoice;
import com.jbx.econtract.model.entity.Invoice.InvoiceStatus;
import com.jbx.econtract.model.entity.InvoiceItem;
import com.jbx.econtract.service.InvoiceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for managing invoices.
 * 
 * @author jb-x Development Team
 * @version 1.0
 */
@RestController
@RequestMapping("/api/v1/invoices")
@RequiredArgsConstructor
@Tag(name = "Invoices", description = "Invoice management APIs")
public class InvoiceController {

    private final InvoiceService invoiceService;

    @PostMapping
    @Operation(summary = "Create a new invoice")
    public ResponseEntity<Invoice> createInvoice(@Valid @RequestBody Invoice invoice) {
        Invoice created = invoiceService.createInvoice(invoice);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(summary = "Get all invoices")
    public ResponseEntity<List<Invoice>> getAllInvoices() {
        List<Invoice> invoices = invoiceService.getAllInvoices();
        return ResponseEntity.ok(invoices);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get invoice by ID")
    public ResponseEntity<Invoice> getInvoiceById(@PathVariable Long id) {
        return invoiceService.getInvoiceById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an invoice")
    public ResponseEntity<Invoice> updateInvoice(
        @PathVariable Long id,
        @Valid @RequestBody Invoice invoice
    ) {
        try {
            Invoice updated = invoiceService.updateInvoice(id, invoice);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete an invoice")
    public ResponseEntity<Void> deleteInvoice(@PathVariable Long id) {
        try {
            invoiceService.deleteInvoice(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/contract/{contractId}")
    @Operation(summary = "Get invoices by contract ID")
    public ResponseEntity<List<Invoice>> getInvoicesByContract(@PathVariable Long contractId) {
        List<Invoice> invoices = invoiceService.getInvoicesByContract(contractId);
        return ResponseEntity.ok(invoices);
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Get invoices by status")
    public ResponseEntity<List<Invoice>> getInvoicesByStatus(@PathVariable InvoiceStatus status) {
        List<Invoice> invoices = invoiceService.getInvoicesByStatus(status);
        return ResponseEntity.ok(invoices);
    }

    @GetMapping("/overdue")
    @Operation(summary = "Get overdue invoices")
    public ResponseEntity<List<Invoice>> getOverdueInvoices() {
        List<Invoice> invoices = invoiceService.getOverdueInvoices();
        return ResponseEntity.ok(invoices);
    }

    @PostMapping("/{id}/items")
    @Operation(summary = "Add item to invoice")
    public ResponseEntity<InvoiceItem> addInvoiceItem(
        @PathVariable Long id,
        @Valid @RequestBody InvoiceItem item
    ) {
        try {
            InvoiceItem created = invoiceService.addInvoiceItem(id, item);
            return new ResponseEntity<>(created, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{id}/items")
    @Operation(summary = "Get invoice items")
    public ResponseEntity<List<InvoiceItem>> getInvoiceItems(@PathVariable Long id) {
        List<InvoiceItem> items = invoiceService.getInvoiceItems(id);
        return ResponseEntity.ok(items);
    }

    @PostMapping("/from-service-records")
    @Operation(summary = "Create invoice from service records")
    public ResponseEntity<Invoice> createInvoiceFromServiceRecords(
        @Valid @RequestBody CreateInvoiceFromServiceRecordsRequest request
    ) {
        try {
            Invoice invoice = invoiceService.createInvoiceFromServiceRecords(
                request.getContractId(),
                request.getServiceRecordIds(),
                request.getCreatedByUserId()
            );
            return new ResponseEntity<>(invoice, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/{id}/approve")
    @Operation(summary = "Approve an invoice")
    public ResponseEntity<Invoice> approveInvoice(
        @PathVariable Long id,
        @RequestParam Long approvedByUserId
    ) {
        try {
            Invoice approved = invoiceService.approveInvoice(id, approvedByUserId);
            return ResponseEntity.ok(approved);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/{id}/send")
    @Operation(summary = "Mark invoice as sent")
    public ResponseEntity<Invoice> markAsSent(
        @PathVariable Long id,
        @RequestParam Long sentByUserId
    ) {
        try {
            Invoice sent = invoiceService.markAsSent(id, sentByUserId);
            return ResponseEntity.ok(sent);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/{id}/paid")
    @Operation(summary = "Mark invoice as paid")
    public ResponseEntity<Invoice> markAsPaid(@PathVariable Long id) {
        try {
            Invoice paid = invoiceService.markAsPaid(id);
            return ResponseEntity.ok(paid);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/{id}/cancel")
    @Operation(summary = "Cancel an invoice")
    public ResponseEntity<Invoice> cancelInvoice(
        @PathVariable Long id,
        @RequestBody CancelInvoiceRequest request
    ) {
        try {
            Invoice cancelled = invoiceService.cancelInvoice(id, request.getReason());
            return ResponseEntity.ok(cancelled);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/{id}/recalculate")
    @Operation(summary = "Recalculate invoice totals")
    public ResponseEntity<Void> recalculateTotals(@PathVariable Long id) {
        try {
            invoiceService.recalculateInvoiceTotals(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Request DTO for creating invoice from service records.
     */
    @Data
    public static class CreateInvoiceFromServiceRecordsRequest {
        private Long contractId;
        private List<Long> serviceRecordIds;
        private Long createdByUserId;
    }

    /**
     * Request DTO for cancelling invoice.
     */
    @Data
    public static class CancelInvoiceRequest {
        private String reason;
    }
}
