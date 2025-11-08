package com.jbx.econtract.service;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.jbx.econtract.model.entity.Contract;
import com.jbx.econtract.model.entity.Invoice;
import com.jbx.econtract.repository.ContractRepository;
import com.jbx.econtract.repository.InvoiceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Service für Report-Generierung (PDF & Excel)
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ReportService {

    private final ContractRepository contractRepository;
    private final InvoiceRepository invoiceRepository;
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    // ========================================================================
    // VERTRAGSBERICHTE
    // ========================================================================

    /**
     * Generiert Vertragsbericht als PDF
     */
    public byte[] generateContractReportPDF() throws Exception {
        log.info("Generating contract report PDF");
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(baos);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        // Title
        Paragraph title = new Paragraph("Vertragsbericht")
                .setFontSize(20)
                .setBold()
                .setTextAlignment(TextAlignment.CENTER);
        document.add(title);

        Paragraph date = new Paragraph("Erstellt am: " + LocalDate.now().format(DATE_FORMATTER))
                .setFontSize(10)
                .setTextAlignment(TextAlignment.CENTER);
        document.add(date);

        document.add(new Paragraph("\n"));

        // Table
        float[] columnWidths = {1, 3, 2, 2, 2, 2};
        Table table = new Table(columnWidths);
        
        // Header
        table.addHeaderCell("Nr.");
        table.addHeaderCell("Titel");
        table.addHeaderCell("Typ");
        table.addHeaderCell("Status");
        table.addHeaderCell("Partner");
        table.addHeaderCell("Wert");

        // Data
        List<Contract> contracts = contractRepository.findAll();
        for (Contract contract : contracts) {
            table.addCell(contract.getContractNumber());
            table.addCell(contract.getTitle());
            table.addCell(contract.getContractType());
            table.addCell(contract.getStatus().name());
            table.addCell(contract.getPartnerName() != null ? contract.getPartnerName() : "-");
            table.addCell(formatCurrency(contract.getContractValue(), contract.getCurrency()));
        }

        document.add(table);

        // Summary
        document.add(new Paragraph("\n"));
        long totalContracts = contracts.size();
        BigDecimal totalValue = contracts.stream()
                .map(c -> c.getContractValue() != null ? c.getContractValue() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        document.add(new Paragraph("Zusammenfassung:").setBold());
        document.add(new Paragraph("Anzahl Verträge: " + totalContracts));
        document.add(new Paragraph("Gesamtwert: " + formatCurrency(totalValue, "EUR")));

        document.close();
        
        log.info("Contract report PDF generated successfully");
        return baos.toByteArray();
    }

    /**
     * Generiert Vertragsbericht als Excel
     */
    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public byte[] generateContractReportExcel() throws Exception {
        log.info("Generating contract report Excel");
        
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Verträge");

        // Header Style
        CellStyle headerStyle = workbook.createCellStyle();
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerStyle.setFont(headerFont);

        // Header Row
        Row headerRow = sheet.createRow(0);
        String[] headers = {"Vertragsnummer", "Titel", "Typ", "Status", "Partner", "Startdatum", "Enddatum", "Wert", "Währung"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        // Data Rows
        List<Contract> contracts = contractRepository.findAll();
        int rowNum = 1;
        for (Contract contract : contracts) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(contract.getContractNumber());
            row.createCell(1).setCellValue(contract.getTitle());
            row.createCell(2).setCellValue(contract.getContractType());
            row.createCell(3).setCellValue(contract.getStatus().name());
            row.createCell(4).setCellValue(contract.getPartnerName() != null ? contract.getPartnerName() : "-");
            row.createCell(5).setCellValue(contract.getStartDate() != null ? contract.getStartDate().format(DATE_FORMATTER) : "-");
            row.createCell(6).setCellValue(contract.getEndDate() != null ? contract.getEndDate().format(DATE_FORMATTER) : "-");
            row.createCell(7).setCellValue(contract.getContractValue() != null ? contract.getContractValue().doubleValue() : 0);
            row.createCell(8).setCellValue(contract.getCurrency() != null ? contract.getCurrency() : "EUR");
        }

        // Set fixed column widths (faster than autoSizeColumn)
        sheet.setColumnWidth(0, 4000);  // Vertragsnummer
        sheet.setColumnWidth(1, 8000);  // Titel
        sheet.setColumnWidth(2, 4000);  // Typ
        sheet.setColumnWidth(3, 3000);  // Status
        sheet.setColumnWidth(4, 6000);  // Partner
        sheet.setColumnWidth(5, 3000);  // Startdatum
        sheet.setColumnWidth(6, 3000);  // Enddatum
        sheet.setColumnWidth(7, 3000);  // Wert
        sheet.setColumnWidth(8, 2000);  // Währung

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        workbook.write(baos);
        workbook.close();

        log.info("Contract report Excel generated successfully");
        return baos.toByteArray();
    }

    // ========================================================================
    // COMPLIANCE-BERICHTE
    // ========================================================================

    /**
     * Generiert Compliance-Bericht als PDF
     */
    public byte[] generateComplianceReportPDF() throws Exception {
        log.info("Generating compliance report PDF");
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(baos);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        // Title
        document.add(new Paragraph("Compliance-Bericht")
                .setFontSize(20)
                .setBold()
                .setTextAlignment(TextAlignment.CENTER));
        document.add(new Paragraph("Erstellt am: " + LocalDate.now().format(DATE_FORMATTER))
                .setFontSize(10)
                .setTextAlignment(TextAlignment.CENTER));
        document.add(new Paragraph("\n"));

        // Expiring Contracts
        LocalDate today = LocalDate.now();
        LocalDate in90Days = today.plusDays(90);
        List<Contract> expiringContracts = contractRepository.findExpiringContracts(today, in90Days);

        document.add(new Paragraph("Ablaufende Verträge (nächste 90 Tage):").setBold());
        
        if (expiringContracts.isEmpty()) {
            document.add(new Paragraph("Keine ablaufenden Verträge gefunden."));
        } else {
            float[] columnWidths = {2, 3, 2, 2, 2};
            Table table = new Table(columnWidths);
            
            table.addHeaderCell("Vertragsnummer");
            table.addHeaderCell("Titel");
            table.addHeaderCell("Partner");
            table.addHeaderCell("Enddatum");
            table.addHeaderCell("Tage bis Ablauf");

            for (Contract contract : expiringContracts) {
                long daysUntilExpiry = java.time.temporal.ChronoUnit.DAYS.between(today, contract.getEndDate());
                table.addCell(contract.getContractNumber());
                table.addCell(contract.getTitle());
                table.addCell(contract.getPartnerName() != null ? contract.getPartnerName() : "-");
                table.addCell(contract.getEndDate().format(DATE_FORMATTER));
                table.addCell(String.valueOf(daysUntilExpiry));
            }

            document.add(table);
        }

        document.close();
        return baos.toByteArray();
    }

    /**
     * Generiert Compliance-Bericht als Excel
     */
    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public byte[] generateComplianceReportExcel() throws Exception {
        log.info("Generating compliance report Excel");
        
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Compliance");

        // Header
        Row headerRow = sheet.createRow(0);
        String[] headers = {"Vertragsnummer", "Titel", "Partner", "Enddatum", "Tage bis Ablauf", "Kündigungsfrist (Tage)"};
        for (int i = 0; i < headers.length; i++) {
            headerRow.createCell(i).setCellValue(headers[i]);
        }

        // Data
        LocalDate today = LocalDate.now();
        LocalDate in90Days = today.plusDays(90);
        List<Contract> expiringContracts = contractRepository.findExpiringContracts(today, in90Days);

        int rowNum = 1;
        for (Contract contract : expiringContracts) {
            Row row = sheet.createRow(rowNum++);
            long daysUntilExpiry = java.time.temporal.ChronoUnit.DAYS.between(today, contract.getEndDate());
            
            row.createCell(0).setCellValue(contract.getContractNumber());
            row.createCell(1).setCellValue(contract.getTitle());
            row.createCell(2).setCellValue(contract.getPartnerName() != null ? contract.getPartnerName() : "-");
            row.createCell(3).setCellValue(contract.getEndDate().format(DATE_FORMATTER));
            row.createCell(4).setCellValue(daysUntilExpiry);
            row.createCell(5).setCellValue(contract.getNoticePeriodDays() != null ? contract.getNoticePeriodDays() : 0);
        }

        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        workbook.write(baos);
        workbook.close();

        return baos.toByteArray();
    }

    // ========================================================================
    // FINANZBERICHTE
    // ========================================================================

    /**
     * Generiert Finanzbericht als PDF
     */
    public byte[] generateFinancialReportPDF() throws Exception {
        log.info("Generating financial report PDF");
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(baos);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        document.add(new Paragraph("Finanzbericht")
                .setFontSize(20)
                .setBold()
                .setTextAlignment(TextAlignment.CENTER));
        document.add(new Paragraph("Erstellt am: " + LocalDate.now().format(DATE_FORMATTER))
                .setFontSize(10)
                .setTextAlignment(TextAlignment.CENTER));
        document.add(new Paragraph("\n"));

        List<Contract> contracts = contractRepository.findAll();

        // Summary by Status
        document.add(new Paragraph("Vertragswerte nach Status:").setBold());
        
        for (Contract.ContractStatus status : Contract.ContractStatus.values()) {
            BigDecimal total = contracts.stream()
                    .filter(c -> c.getStatus() == status)
                    .map(c -> c.getContractValue() != null ? c.getContractValue() : BigDecimal.ZERO)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            document.add(new Paragraph(status.name() + ": " + formatCurrency(total, "EUR")));
        }

        document.add(new Paragraph("\n"));

        // Total
        BigDecimal grandTotal = contracts.stream()
                .map(c -> c.getContractValue() != null ? c.getContractValue() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        document.add(new Paragraph("Gesamtwert aller Verträge: " + formatCurrency(grandTotal, "EUR"))
                .setBold()
                .setFontSize(14));

        document.close();
        return baos.toByteArray();
    }

    /**
     * Generiert Finanzbericht als Excel
     */
    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public byte[] generateFinancialReportExcel() throws Exception {
        log.info("Generating financial report Excel");
        
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Finanzen");

        Row headerRow = sheet.createRow(0);
        String[] headers = {"Status", "Anzahl", "Gesamtwert (EUR)"};
        for (int i = 0; i < headers.length; i++) {
            headerRow.createCell(i).setCellValue(headers[i]);
        }

        List<Contract> contracts = contractRepository.findAll();
        int rowNum = 1;

        for (Contract.ContractStatus status : Contract.ContractStatus.values()) {
            long count = contracts.stream().filter(c -> c.getStatus() == status).count();
            BigDecimal total = contracts.stream()
                    .filter(c -> c.getStatus() == status)
                    .map(c -> c.getContractValue() != null ? c.getContractValue() : BigDecimal.ZERO)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(status.name());
            row.createCell(1).setCellValue(count);
            row.createCell(2).setCellValue(total.doubleValue());
        }

        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        workbook.write(baos);
        workbook.close();

        return baos.toByteArray();
    }

    // ========================================================================
    // ZAHLUNGSBERICHTE
    // ========================================================================

    /**
     * Generiert Zahlungsbericht als PDF
     */
    public byte[] generatePaymentReportPDF() throws Exception {
        log.info("Generating payment report PDF");
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(baos);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        document.add(new Paragraph("Zahlungsbericht")
                .setFontSize(20)
                .setBold()
                .setTextAlignment(TextAlignment.CENTER));
        document.add(new Paragraph("Erstellt am: " + LocalDate.now().format(DATE_FORMATTER))
                .setFontSize(10)
                .setTextAlignment(TextAlignment.CENTER));
        document.add(new Paragraph("\n"));

        List<Invoice> invoices = invoiceRepository.findAll();

        if (invoices.isEmpty()) {
            document.add(new Paragraph("Keine Rechnungen vorhanden."));
        } else {
            float[] columnWidths = {2, 3, 2, 2, 2};
            Table table = new Table(columnWidths);
            
            table.addHeaderCell("Rechnungsnr.");
            table.addHeaderCell("Vertrag");
            table.addHeaderCell("Betrag");
            table.addHeaderCell("Fälligkeitsdatum");
            table.addHeaderCell("Status");

            for (Invoice invoice : invoices) {
                table.addCell(invoice.getInvoiceNumber());
                table.addCell(invoice.getContractId() != null ? "Contract #" + invoice.getContractId() : "-");
                table.addCell(formatCurrency(invoice.getTotalGross(), "EUR"));
                table.addCell(invoice.getDueDate() != null ? invoice.getDueDate().format(DATE_FORMATTER) : "-");
                table.addCell(invoice.getStatus() != null ? invoice.getStatus().name() : "-");
            }

            document.add(table);

            // Summary
            document.add(new Paragraph("\n"));
            BigDecimal totalAmount = invoices.stream()
                    .map(i -> i.getTotalGross() != null ? i.getTotalGross() : BigDecimal.ZERO)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            document.add(new Paragraph("Gesamtbetrag: " + formatCurrency(totalAmount, "EUR")).setBold());
        }

        document.close();
        return baos.toByteArray();
    }

    /**
     * Generiert Zahlungsbericht als Excel
     */
    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public byte[] generatePaymentReportExcel() throws Exception {
        log.info("Generating payment report Excel");
        
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Zahlungen");

        Row headerRow = sheet.createRow(0);
        String[] headers = {"Rechnungsnummer", "Vertrag", "Betrag", "Fälligkeitsdatum", "Status"};
        for (int i = 0; i < headers.length; i++) {
            headerRow.createCell(i).setCellValue(headers[i]);
        }

        List<Invoice> invoices = invoiceRepository.findAll();
        int rowNum = 1;

        for (Invoice invoice : invoices) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(invoice.getInvoiceNumber());
            row.createCell(1).setCellValue(invoice.getContractId() != null ? "Contract #" + invoice.getContractId() : "-");
            row.createCell(2).setCellValue(invoice.getTotalGross() != null ? invoice.getTotalGross().doubleValue() : 0);
            row.createCell(3).setCellValue(invoice.getDueDate() != null ? invoice.getDueDate().format(DATE_FORMATTER) : "-");
            row.createCell(4).setCellValue(invoice.getStatus() != null ? invoice.getStatus().name() : "-");
        }

        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        workbook.write(baos);
        workbook.close();

        return baos.toByteArray();
    }

    // ========================================================================
    // HELPER METHODS
    // ========================================================================

    private String formatCurrency(BigDecimal value, String currency) {
        if (value == null) return "0,00 " + currency;
        return String.format("%,.2f %s", value, currency);
    }
}
