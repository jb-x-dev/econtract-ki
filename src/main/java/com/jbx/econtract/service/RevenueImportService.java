package com.jbx.econtract.service;

import com.jbx.econtract.model.entity.Contract;
import com.jbx.econtract.model.entity.Invoice;
import com.jbx.econtract.model.entity.RevenueItem;
import com.jbx.econtract.repository.ContractRepository;
import com.jbx.econtract.repository.InvoiceRepository;
import com.jbx.econtract.repository.RevenueItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Revenue Import Service
 * Imports revenue data from CSV/Excel files
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RevenueImportService {
    
    private final RevenueItemRepository revenueItemRepository;
    private final ContractRepository contractRepository;
    private final InvoiceRepository invoiceRepository;
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    
    /**
     * Import revenue data from CSV file
     */
    @Transactional
    public Map<String, Object> importFromCSV(Long contractId, MultipartFile file, Long userId) throws Exception {
        log.info("Importing revenue data from CSV for contract {}", contractId);
        
        Contract contract = contractRepository.findById(contractId)
            .orElseThrow(() -> new IllegalArgumentException("Contract not found: " + contractId));
        
        List<RevenueItem> items = new ArrayList<>();
        List<String> errors = new ArrayList<>();
        
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            String line;
            int lineNumber = 0;
            String[] headers = null;
            
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                
                // Skip empty lines
                if (line.trim().isEmpty()) {
                    continue;
                }
                
                String[] values = line.split(",");
                
                // First line is header
                if (lineNumber == 1) {
                    headers = values;
                    continue;
                }
                
                try {
                    RevenueItem item = parseCSVLine(values, headers, contract, userId);
                    items.add(item);
                } catch (Exception e) {
                    errors.add("Line " + lineNumber + ": " + e.getMessage());
                    log.warn("Error parsing line {}: {}", lineNumber, e.getMessage());
                }
            }
        }
        
        // Save all valid items
        items = revenueItemRepository.saveAll(items);
        
        // Try to match items to invoices
        matchRevenueToInvoices(contractId);
        
        Map<String, Object> result = new HashMap<>();
        result.put("imported", items.size());
        result.put("errors", errors);
        result.put("errorCount", errors.size());
        
        log.info("Imported {} revenue items for contract {}, {} errors", items.size(), contractId, errors.size());
        return result;
    }
    
    /**
     * Import revenue data from Excel file
     */
    @Transactional
    public Map<String, Object> importFromExcel(Long contractId, MultipartFile file, Long userId) throws Exception {
        log.info("Importing revenue data from Excel for contract {}", contractId);
        
        Contract contract = contractRepository.findById(contractId)
            .orElseThrow(() -> new IllegalArgumentException("Contract not found: " + contractId));
        
        List<RevenueItem> items = new ArrayList<>();
        List<String> errors = new ArrayList<>();
        
        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            
            Row headerRow = sheet.getRow(0);
            if (headerRow == null) {
                throw new IllegalArgumentException("Excel file has no header row");
            }
            
            String[] headers = new String[headerRow.getLastCellNum()];
            for (int i = 0; i < headerRow.getLastCellNum(); i++) {
                Cell cell = headerRow.getCell(i);
                headers[i] = cell != null ? cell.getStringCellValue() : "";
            }
            
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) {
                    continue;
                }
                
                try {
                    String[] values = new String[headers.length];
                    for (int j = 0; j < headers.length; j++) {
                        Cell cell = row.getCell(j);
                        values[j] = getCellValueAsString(cell);
                    }
                    
                    RevenueItem item = parseCSVLine(values, headers, contract, userId);
                    items.add(item);
                } catch (Exception e) {
                    errors.add("Row " + (i + 1) + ": " + e.getMessage());
                    log.warn("Error parsing row {}: {}", i + 1, e.getMessage());
                }
            }
        }
        
        // Save all valid items
        items = revenueItemRepository.saveAll(items);
        
        // Try to match items to invoices
        matchRevenueToInvoices(contractId);
        
        Map<String, Object> result = new HashMap<>();
        result.put("imported", items.size());
        result.put("errors", errors);
        result.put("errorCount", errors.size());
        
        log.info("Imported {} revenue items for contract {}, {} errors", items.size(), contractId, errors.size());
        return result;
    }
    
    /**
     * Parse CSV line to RevenueItem
     * Expected columns: date, description, amount, type
     */
    private RevenueItem parseCSVLine(String[] values, String[] headers, Contract contract, Long userId) {
        Map<String, String> data = new HashMap<>();
        for (int i = 0; i < Math.min(headers.length, values.length); i++) {
            data.put(headers[i].toLowerCase().trim(), values[i].trim());
        }
        
        RevenueItem item = new RevenueItem();
        item.setContract(contract);
        item.setImportedBy(userId);
        
        // Parse date
        String dateStr = data.getOrDefault("date", data.getOrDefault("datum", ""));
        if (dateStr.isEmpty()) {
            throw new IllegalArgumentException("Missing date field");
        }
        item.setRevenueDate(LocalDate.parse(dateStr, DATE_FORMATTER));
        
        // Parse amount
        String amountStr = data.getOrDefault("amount", data.getOrDefault("betrag", ""));
        if (amountStr.isEmpty()) {
            throw new IllegalArgumentException("Missing amount field");
        }
        item.setAmount(new BigDecimal(amountStr.replace(",", ".")));
        
        // Parse description
        item.setDescription(data.getOrDefault("description", data.getOrDefault("beschreibung", "")));
        
        // Parse type
        String typeStr = data.getOrDefault("type", data.getOrDefault("typ", "RECURRING"));
        try {
            item.setRevenueType(RevenueItem.RevenueType.valueOf(typeStr.toUpperCase()));
        } catch (Exception e) {
            item.setRevenueType(RevenueItem.RevenueType.RECURRING);
        }
        
        // Notes
        item.setNotes(data.getOrDefault("notes", data.getOrDefault("notizen", "")));
        
        return item;
    }
    
    /**
     * Get cell value as string
     */
    private String getCellValueAsString(Cell cell) {
        if (cell == null) {
            return "";
        }
        
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue();
            case NUMERIC -> {
                if (DateUtil.isCellDateFormatted(cell)) {
                    yield cell.getLocalDateTimeCellValue().toLocalDate().format(DATE_FORMATTER);
                } else {
                    yield String.valueOf(cell.getNumericCellValue());
                }
            }
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            case FORMULA -> cell.getCellFormula();
            default -> "";
        };
    }
    
    /**
     * Match revenue items to invoices based on date
     */
    @Transactional
    public void matchRevenueToInvoices(Long contractId) {
        log.info("Matching revenue items to invoices for contract {}", contractId);
        
        List<RevenueItem> unassignedItems = revenueItemRepository.findByContractIdAndInvoiceIsNull(contractId);
        List<Invoice> invoices = invoiceRepository.findByContractId(contractId);
        
        int matched = 0;
        for (RevenueItem item : unassignedItems) {
            for (Invoice invoice : invoices) {
                if (invoice.getBillingPeriodStart() != null && invoice.getBillingPeriodEnd() != null) {
                    LocalDate itemDate = item.getRevenueDate();
                    if (!itemDate.isBefore(invoice.getBillingPeriodStart()) && 
                        !itemDate.isAfter(invoice.getBillingPeriodEnd())) {
                        item.setInvoice(invoice);
                        matched++;
                        break;
                    }
                }
            }
        }
        
        revenueItemRepository.saveAll(unassignedItems);
        log.info("Matched {} revenue items to invoices", matched);
    }
    
    /**
     * Get revenue summary for contract
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getRevenueSummary(Long contractId) {
        Map<String, Object> summary = new HashMap<>();
        
        BigDecimal totalRevenue = revenueItemRepository.calculateTotalRevenue(contractId);
        BigDecimal recurringRevenue = revenueItemRepository.calculateRevenueByType(contractId, RevenueItem.RevenueType.RECURRING);
        BigDecimal oneTimeRevenue = revenueItemRepository.calculateRevenueByType(contractId, RevenueItem.RevenueType.ONE_TIME);
        BigDecimal usageRevenue = revenueItemRepository.calculateRevenueByType(contractId, RevenueItem.RevenueType.USAGE_BASED);
        
        summary.put("totalRevenue", totalRevenue);
        summary.put("recurringRevenue", recurringRevenue);
        summary.put("oneTimeRevenue", oneTimeRevenue);
        summary.put("usageRevenue", usageRevenue);
        
        List<RevenueItem> items = revenueItemRepository.findByContractIdOrderByRevenueDateDesc(contractId);
        summary.put("itemCount", items.size());
        
        long unassigned = items.stream().filter(item -> item.getInvoice() == null).count();
        summary.put("unassignedCount", unassigned);
        
        return summary;
    }
}
