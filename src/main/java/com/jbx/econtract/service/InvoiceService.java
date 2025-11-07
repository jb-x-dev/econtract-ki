package com.jbx.econtract.service;

import com.jbx.econtract.model.entity.Invoice;
import com.jbx.econtract.model.entity.Invoice.InvoiceStatus;
import com.jbx.econtract.model.entity.InvoiceItem;
import com.jbx.econtract.model.entity.ServiceRecord;
import com.jbx.econtract.repository.InvoiceItemRepository;
import com.jbx.econtract.repository.InvoiceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Year;
import java.util.List;
import java.util.Optional;

/**
 * Service for managing invoices.
 * 
 * @author jb-x Development Team
 * @version 1.0
 */
@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final InvoiceItemRepository invoiceItemRepository;
    private final ServiceRecordService serviceRecordService;

    /**
     * Create a new invoice.
     *
     * @param invoice the invoice to create
     * @return the created invoice
     */
    public Invoice createInvoice(Invoice invoice) {
        log.info("Creating new invoice");
        
        // Generate invoice number if not set
        if (invoice.getInvoiceNumber() == null || invoice.getInvoiceNumber().isEmpty()) {
            invoice.setInvoiceNumber(generateInvoiceNumber());
        }
        
        // Set default values
        if (invoice.getInvoiceDate() == null) {
            invoice.setInvoiceDate(LocalDate.now());
        }
        
        // Calculate due date if not set (14 days payment term)
        if (invoice.getDueDate() == null) {
            invoice.setDueDate(invoice.getInvoiceDate().plusDays(14));
        }
        
        // Initialize totals
        if (invoice.getSubtotalNet() == null) {
            invoice.setSubtotalNet(BigDecimal.ZERO);
        }
        if (invoice.getTaxAmount() == null) {
            invoice.setTaxAmount(BigDecimal.ZERO);
        }
        if (invoice.getTotalGross() == null) {
            invoice.setTotalGross(BigDecimal.ZERO);
        }
        
        Invoice saved = invoiceRepository.save(invoice);
        log.info("Invoice created with ID: {} and number: {}", saved.getId(), saved.getInvoiceNumber());
        
        return saved;
    }

    /**
     * Generate a unique invoice number.
     * Format: INV-YYYY-NNNN (e.g., INV-2025-0001)
     *
     * @return the generated invoice number
     */
    public String generateInvoiceNumber() {
        int currentYear = Year.now().getValue();
        List<String> latestNumbers = invoiceRepository.findLatestInvoiceNumberByYear(currentYear);
        
        int nextNumber = 1;
        if (!latestNumbers.isEmpty()) {
            String latestNumber = latestNumbers.get(0);
            try {
                // Extract number part from INV-2025-0001
                String[] parts = latestNumber.split("-");
                if (parts.length == 3) {
                    nextNumber = Integer.parseInt(parts[2]) + 1;
                }
            } catch (NumberFormatException e) {
                log.warn("Could not parse invoice number: {}", latestNumbers.get(0));
            }
        }
        
        return String.format("INV-%d-%04d", currentYear, nextNumber);
    }

    /**
     * Update an existing invoice.
     *
     * @param id the invoice ID
     * @param invoice the updated invoice data
     * @return the updated invoice
     * @throws RuntimeException if invoice not found or already sent/paid
     */
    public Invoice updateInvoice(Long id, Invoice invoice) {
        log.info("Updating invoice ID: {}", id);
        
        Invoice existing = invoiceRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Invoice not found with ID: " + id));
        
        // Check if invoice can be updated
        if (existing.getStatus() == InvoiceStatus.SENT || 
            existing.getStatus() == InvoiceStatus.PAID ||
            existing.getStatus() == InvoiceStatus.CANCELLED) {
            throw new RuntimeException("Cannot update invoice with status: " + existing.getStatus());
        }
        
        // Update fields
        existing.setInvoiceDate(invoice.getInvoiceDate());
        existing.setBillingPeriodStart(invoice.getBillingPeriodStart());
        existing.setBillingPeriodEnd(invoice.getBillingPeriodEnd());
        existing.setDueDate(invoice.getDueDate());
        existing.setPartnerName(invoice.getPartnerName());
        existing.setPartnerAddress(invoice.getPartnerAddress());
        existing.setPartnerTaxId(invoice.getPartnerTaxId());
        existing.setPaymentTerms(invoice.getPaymentTerms());
        existing.setPaymentMethod(invoice.getPaymentMethod());
        existing.setBankAccount(invoice.getBankAccount());
        existing.setNotes(invoice.getNotes());
        existing.setCustomerNotes(invoice.getCustomerNotes());
        existing.setDiscountPercentage(invoice.getDiscountPercentage());
        
        Invoice updated = invoiceRepository.save(existing);
        log.info("Invoice updated: {}", id);
        
        return updated;
    }

    /**
     * Delete an invoice.
     *
     * @param id the invoice ID
     * @throws RuntimeException if invoice not found or already sent/paid
     */
    public void deleteInvoice(Long id) {
        log.info("Deleting invoice ID: {}", id);
        
        Invoice existing = invoiceRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Invoice not found with ID: " + id));
        
        // Check if invoice can be deleted
        if (existing.getStatus() == InvoiceStatus.SENT || 
            existing.getStatus() == InvoiceStatus.PAID) {
            throw new RuntimeException("Cannot delete invoice with status: " + existing.getStatus());
        }
        
        // Delete associated items first
        invoiceItemRepository.deleteByInvoiceId(id);
        
        invoiceRepository.deleteById(id);
        log.info("Invoice deleted: {}", id);
    }

    /**
     * Get invoice by ID.
     *
     * @param id the invoice ID
     * @return optional invoice
     */
    @Transactional(readOnly = true)
    public Optional<Invoice> getInvoiceById(Long id) {
        return invoiceRepository.findById(id);
    }

    /**
     * Get all invoices.
     *
     * @return list of all invoices
     */
    @Transactional(readOnly = true)
    public List<Invoice> getAllInvoices() {
        return invoiceRepository.findAll();
    }

    /**
     * Get invoices by contract ID.
     *
     * @param contractId the contract ID
     * @return list of invoices
     */
    @Transactional(readOnly = true)
    public List<Invoice> getInvoicesByContract(Long contractId) {
        return invoiceRepository.findByContractId(contractId);
    }

    /**
     * Get invoices by status.
     *
     * @param status the invoice status
     * @return list of invoices
     */
    @Transactional(readOnly = true)
    public List<Invoice> getInvoicesByStatus(InvoiceStatus status) {
        return invoiceRepository.findByStatus(status);
    }

    /**
     * Get overdue invoices.
     *
     * @return list of overdue invoices
     */
    @Transactional(readOnly = true)
    public List<Invoice> getOverdueInvoices() {
        return invoiceRepository.findOverdueInvoices(LocalDate.now());
    }

    /**
     * Add item to invoice.
     *
     * @param invoiceId the invoice ID
     * @param item the invoice item to add
     * @return the created invoice item
     * @throws RuntimeException if invoice not found or already sent/paid
     */
    public InvoiceItem addInvoiceItem(Long invoiceId, InvoiceItem item) {
        log.info("Adding item to invoice ID: {}", invoiceId);
        
        Invoice invoice = invoiceRepository.findById(invoiceId)
            .orElseThrow(() -> new RuntimeException("Invoice not found with ID: " + invoiceId));
        
        // Check if invoice can be modified
        if (invoice.getStatus() == InvoiceStatus.SENT || 
            invoice.getStatus() == InvoiceStatus.PAID ||
            invoice.getStatus() == InvoiceStatus.CANCELLED) {
            throw new RuntimeException("Cannot add items to invoice with status: " + invoice.getStatus());
        }
        
        // Set invoice ID
        item.setInvoiceId(invoiceId);
        
        // Set position number
        Integer maxPosition = invoiceItemRepository.findMaxPositionNumberByInvoiceId(invoiceId);
        item.setPositionNumber(maxPosition != null ? maxPosition + 1 : 1);
        
        // Calculate amounts
        item.calculateAll();
        
        InvoiceItem saved = invoiceItemRepository.save(item);
        
        // Recalculate invoice totals
        recalculateInvoiceTotals(invoiceId);
        
        log.info("Invoice item added with ID: {}", saved.getId());
        
        return saved;
    }

    /**
     * Create invoice from service records.
     *
     * @param contractId the contract ID
     * @param serviceRecordIds the list of service record IDs
     * @param createdByUserId the user ID creating the invoice
     * @return the created invoice
     * @throws RuntimeException if service records not found or already invoiced
     */
    public Invoice createInvoiceFromServiceRecords(
        Long contractId, 
        List<Long> serviceRecordIds,
        Long createdByUserId
    ) {
        log.info("Creating invoice from {} service records for contract ID: {}", 
                 serviceRecordIds.size(), contractId);
        
        // Validate service records
        List<ServiceRecord> serviceRecords = serviceRecordIds.stream()
            .map(id -> serviceRecordService.getServiceRecordById(id)
                .orElseThrow(() -> new RuntimeException("Service record not found with ID: " + id)))
            .toList();
        
        // Check if all records are approved and not invoiced
        for (ServiceRecord record : serviceRecords) {
            if (record.getStatus() != ServiceRecord.ServiceRecordStatus.APPROVED) {
                throw new RuntimeException("Service record " + record.getId() + " is not approved");
            }
            if (record.getInvoiceItemId() != null) {
                throw new RuntimeException("Service record " + record.getId() + " is already invoiced");
            }
        }
        
        // Determine billing period from service records
        LocalDate minDate = serviceRecords.stream()
            .map(ServiceRecord::getServiceDate)
            .min(LocalDate::compareTo)
            .orElse(LocalDate.now());
        
        LocalDate maxDate = serviceRecords.stream()
            .map(ServiceRecord::getServiceDate)
            .max(LocalDate::compareTo)
            .orElse(LocalDate.now());
        
        // Create invoice
        Invoice invoice = new Invoice();
        invoice.setInvoiceType(Invoice.InvoiceType.SINGLE);
        invoice.setInvoiceDate(LocalDate.now());
        invoice.setBillingPeriodStart(minDate);
        invoice.setBillingPeriodEnd(maxDate);
        invoice.setDueDate(LocalDate.now().plusDays(14));
        invoice.setContractId(contractId);
        invoice.setCreatedByUserId(createdByUserId);
        
        // Get partner info from first service record's contract
        // (In real implementation, fetch from Contract entity)
        invoice.setPartnerId(1L); // Placeholder
        invoice.setPartnerName("Partner Name"); // Placeholder
        invoice.setPartnerAddress("Partner Address"); // Placeholder
        
        Invoice savedInvoice = createInvoice(invoice);
        
        // Create invoice items from service records
        int position = 1;
        for (ServiceRecord record : serviceRecords) {
            InvoiceItem item = new InvoiceItem();
            item.setInvoiceId(savedInvoice.getId());
            item.setPositionNumber(position++);
            item.setServiceRecordId(record.getId());
            item.setContractId(record.getContractId());
            item.setDescription(record.getDescription());
            item.setQuantity(record.getQuantity());
            item.setUnit(record.getUnit());
            item.setUnitPriceNet(record.getUnitPriceNet());
            item.setServicePeriodStart(record.getServicePeriodStart());
            item.setServicePeriodEnd(record.getServicePeriodEnd());
            
            item.calculateAll();
            invoiceItemRepository.save(item);
            
            // Mark service record as invoiced
            serviceRecordService.markAsInvoiced(record.getId(), item.getId());
        }
        
        // Recalculate invoice totals
        recalculateInvoiceTotals(savedInvoice.getId());
        
        log.info("Invoice created from service records with ID: {}", savedInvoice.getId());
        
        return getInvoiceById(savedInvoice.getId()).orElse(savedInvoice);
    }

    /**
     * Recalculate invoice totals from items.
     *
     * @param invoiceId the invoice ID
     */
    public void recalculateInvoiceTotals(Long invoiceId) {
        log.debug("Recalculating totals for invoice ID: {}", invoiceId);
        
        Invoice invoice = invoiceRepository.findById(invoiceId)
            .orElseThrow(() -> new RuntimeException("Invoice not found with ID: " + invoiceId));
        
        List<InvoiceItem> items = invoiceItemRepository.findByInvoiceIdOrderByPositionNumber(invoiceId);
        
        BigDecimal subtotalNet = items.stream()
            .map(InvoiceItem::getSubtotalNet)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        // Apply invoice-level discount if any
        if (invoice.getDiscountPercentage() != null && 
            invoice.getDiscountPercentage().compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal discount = subtotalNet.multiply(invoice.getDiscountPercentage())
                .divide(new BigDecimal("100"), 2, java.math.RoundingMode.HALF_UP);
            invoice.setDiscountAmount(discount);
            subtotalNet = subtotalNet.subtract(discount);
        }
        
        BigDecimal taxAmount = subtotalNet.multiply(invoice.getTaxRate())
            .divide(new BigDecimal("100"), 2, java.math.RoundingMode.HALF_UP);
        
        BigDecimal totalGross = subtotalNet.add(taxAmount);
        
        invoice.setSubtotalNet(subtotalNet);
        invoice.setTaxAmount(taxAmount);
        invoice.setTotalGross(totalGross);
        
        invoiceRepository.save(invoice);
        
        log.debug("Invoice totals recalculated: subtotal={}, tax={}, total={}", 
                  subtotalNet, taxAmount, totalGross);
    }

    /**
     * Approve an invoice.
     *
     * @param id the invoice ID
     * @param approvedByUserId the approving user ID
     * @return the approved invoice
     */
    public Invoice approveInvoice(Long id, Long approvedByUserId) {
        log.info("Approving invoice ID: {} by user: {}", id, approvedByUserId);
        
        Invoice invoice = invoiceRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Invoice not found with ID: " + id));
        
        if (invoice.getStatus() != InvoiceStatus.DRAFT) {
            throw new RuntimeException("Only draft invoices can be approved");
        }
        
        invoice.setStatus(InvoiceStatus.APPROVED);
        invoice.setApprovedByUserId(approvedByUserId);
        invoice.setApprovedDate(LocalDateTime.now());
        
        Invoice approved = invoiceRepository.save(invoice);
        log.info("Invoice approved: {}", id);
        
        return approved;
    }

    /**
     * Mark invoice as sent.
     *
     * @param id the invoice ID
     * @param sentByUserId the user ID who sent the invoice
     * @return the updated invoice
     */
    public Invoice markAsSent(Long id, Long sentByUserId) {
        log.info("Marking invoice ID: {} as sent by user: {}", id, sentByUserId);
        
        Invoice invoice = invoiceRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Invoice not found with ID: " + id));
        
        if (invoice.getStatus() != InvoiceStatus.APPROVED) {
            throw new RuntimeException("Only approved invoices can be sent");
        }
        
        invoice.setStatus(InvoiceStatus.SENT);
        invoice.setSentByUserId(sentByUserId);
        invoice.setSentDate(LocalDateTime.now());
        
        Invoice sent = invoiceRepository.save(invoice);
        log.info("Invoice marked as sent: {}", id);
        
        return sent;
    }

    /**
     * Mark invoice as paid.
     *
     * @param id the invoice ID
     * @return the updated invoice
     */
    public Invoice markAsPaid(Long id) {
        log.info("Marking invoice ID: {} as paid", id);
        
        Invoice invoice = invoiceRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Invoice not found with ID: " + id));
        
        if (invoice.getStatus() == InvoiceStatus.CANCELLED) {
            throw new RuntimeException("Cannot mark cancelled invoice as paid");
        }
        
        invoice.setStatus(InvoiceStatus.PAID);
        
        Invoice paid = invoiceRepository.save(invoice);
        log.info("Invoice marked as paid: {}", id);
        
        return paid;
    }

    /**
     * Cancel an invoice.
     *
     * @param id the invoice ID
     * @param reason the cancellation reason
     * @return the cancelled invoice
     */
    public Invoice cancelInvoice(Long id, String reason) {
        log.info("Cancelling invoice ID: {} with reason: {}", id, reason);
        
        Invoice invoice = invoiceRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Invoice not found with ID: " + id));
        
        if (invoice.getStatus() == InvoiceStatus.PAID) {
            throw new RuntimeException("Cannot cancel paid invoice");
        }
        
        invoice.setStatus(InvoiceStatus.CANCELLED);
        invoice.setCancelledDate(LocalDateTime.now());
        invoice.setCancelledReason(reason);
        
        Invoice cancelled = invoiceRepository.save(invoice);
        log.info("Invoice cancelled: {}", id);
        
        return cancelled;
    }

    /**
     * Get invoice items by invoice ID.
     *
     * @param invoiceId the invoice ID
     * @return list of invoice items
     */
    @Transactional(readOnly = true)
    public List<InvoiceItem> getInvoiceItems(Long invoiceId) {
        return invoiceItemRepository.findByInvoiceIdOrderByPositionNumber(invoiceId);
    }
}
