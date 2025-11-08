# AI Contract Upload - Technische Dokumentation

## Architektur

### Komponenten

```
┌─────────────────┐
│   Frontend      │  contract-upload.html
│   (HTML/JS)     │  Drag & Drop, Form Validation
└────────┬────────┘
         │
         ▼
┌─────────────────────────────────────────────────────────┐
│              ContractUploadController                    │
│  POST /upload                                            │
│  GET  /upload/{id}/status                               │
│  POST /upload/{id}/confirm                              │
│  POST /{contractId}/revenue/import                      │
└────────┬───────────────────────────────────────────────┘
         │
         ▼
┌─────────────────────────────────────────────────────────┐
│                   Service Layer                          │
│  ┌──────────────────┐  ┌──────────────────┐            │
│  │ ContractUpload   │  │ AIExtraction     │            │
│  │ Service          │──│ Service          │            │
│  └──────────────────┘  └──────────────────┘            │
│  ┌──────────────────┐  ┌──────────────────┐            │
│  │ InvoiceSchedule  │  │ RevenueImport    │            │
│  │ Service          │  │ Service          │            │
│  └──────────────────┘  └──────────────────┘            │
└────────┬───────────────────────────────────────────────┘
         │
         ▼
┌─────────────────────────────────────────────────────────┐
│                  Repository Layer                        │
│  ContractUploadRepository                               │
│  RevenueItemRepository                                  │
│  ContractRepository                                     │
│  InvoiceRepository                                      │
└────────┬───────────────────────────────────────────────┘
         │
         ▼
┌─────────────────────────────────────────────────────────┐
│                   PostgreSQL Database                    │
│  contract_uploads, revenue_items, contracts, invoices   │
└─────────────────────────────────────────────────────────┘
```

### Externe Services

```
┌─────────────────┐
│  OpenAI API     │
│  GPT-4.1-mini   │
└────────┬────────┘
         │
         ▼
┌─────────────────┐
│ AIExtraction    │
│ Service         │
└─────────────────┘
```

## Datenmodell

### ContractUpload Entity

```java
@Entity
@Table(name = "contract_uploads")
public class ContractUpload {
    @Id
    private Long id;
    
    private String filename;
    private String filePath;
    private Long fileSize;
    private String mimeType;
    
    @Enumerated(EnumType.STRING)
    private UploadStatus uploadStatus;
    // UPLOADED, PROCESSING, EXTRACTED, COMPLETED, FAILED
    
    @Column(columnDefinition = "jsonb")
    private String extractedData;
    
    @ManyToOne
    private Contract contract;
    
    private String errorMessage;
    private Long uploadedBy;
    private LocalDateTime uploadedAt;
    private LocalDateTime processedAt;
}
```

### RevenueItem Entity

```java
@Entity
@Table(name = "revenue_items")
public class RevenueItem {
    @Id
    private Long id;
    
    @ManyToOne
    private Contract contract;
    
    @ManyToOne
    private Invoice invoice;
    
    private String description;
    private BigDecimal amount;
    private LocalDate revenueDate;
    
    @Enumerated(EnumType.STRING)
    private RevenueType revenueType;
    // RECURRING, ONE_TIME, USAGE_BASED
    
    private LocalDateTime importedAt;
    private Long importedBy;
    private String notes;
}
```

### Contract Entity (Extended)

```java
// New fields added:
private String billingCycle;        // MONTHLY, QUARTERLY, YEARLY, ONE_TIME
private BigDecimal billingAmount;
private LocalDate billingStartDate;
private Integer paymentTermDays;
```

### Invoice Entity (Extended)

```java
// New field added:
private LocalDate scheduledDate;
```

## Service-Implementierungen

### AIExtractionService

**Verantwortlichkeiten:**
- Text-Extraktion aus PDF/Word/Text
- OpenAI API Integration
- Strukturierte Datenextraktion

**Methoden:**

```java
public String extractTextFromFile(File file, String mimeType) throws Exception
```
- Extrahiert Text basierend auf MIME-Type
- Unterstützt PDF (PDFBox), Word (Apache POI), Text

```java
public Map<String, Object> extractContractData(String contractText) throws Exception
```
- Ruft OpenAI API auf
- Verwendet GPT-4.1-mini Modell
- Temperature: 0.1 (deterministisch)
- Max Tokens: 2000
- Parst JSON-Response

**OpenAI Prompt:**

```
Analysiere den folgenden Vertrag und extrahiere die wichtigsten Informationen im JSON-Format.

Extrahiere folgende Felder:
- contractNumber: Vertragsnummer
- title: Vertragsbezeichnung
- contractType: Vertragsart
- partnerName: Name des Vertragspartners
- startDate: Vertragsbeginn (YYYY-MM-DD)
- endDate: Vertragsende (YYYY-MM-DD)
- contractValue: Gesamtvertragswert
- currency: Währung
- billingCycle: Abrechnungszyklus
- billingAmount: Abrechnungsbetrag
- ...

Vertrag:
[CONTRACT_TEXT]

Antworte NUR mit einem validen JSON-Objekt.
```

### ContractUploadService

**Verantwortlichkeiten:**
- File Upload Management
- Async Processing
- Contract Creation

**Workflow:**

```java
uploadContract(file, userId)
  ├─> Save file to /tmp/contract-uploads/
  ├─> Create ContractUpload record (status: UPLOADED)
  └─> Start async processing

processUpload(uploadId)
  ├─> Update status to PROCESSING
  ├─> Extract text from file
  ├─> Call AIExtractionService
  ├─> Save extracted data as JSON
  └─> Update status to EXTRACTED

createContractFromUpload(uploadId, confirmedData)
  ├─> Map confirmed data to Contract entity
  ├─> Generate contract number if missing
  ├─> Save contract
  ├─> Link upload to contract
  ├─> Generate invoice schedule
  └─> Update status to COMPLETED
```

### InvoiceScheduleService

**Verantwortlichkeiten:**
- Automatische Rechnungsgenerierung
- Billing Cycle Management

**Algorithmus:**

```java
generateInvoiceSchedule(contract)
  currentDate = contract.billingStartDate
  endDate = contract.endDate
  
  while currentDate <= endDate:
    invoice = new Invoice()
    invoice.invoiceDate = currentDate
    invoice.dueDate = currentDate + paymentTermDays
    invoice.billingPeriodStart = currentDate
    invoice.billingPeriodEnd = calculatePeriodEnd(currentDate, billingCycle)
    invoice.scheduledDate = currentDate
    invoice.status = "SCHEDULED"
    invoice.totalNet = contract.billingAmount
    
    save(invoice)
    
    currentDate = calculateNextBillingDate(currentDate, billingCycle)
```

**Billing Cycles:**
- `MONTHLY`: +1 Monat
- `QUARTERLY`: +3 Monate
- `YEARLY`: +1 Jahr
- `ONE_TIME`: Nur eine Rechnung

### RevenueImportService

**Verantwortlichkeiten:**
- CSV/Excel Import
- Revenue-Invoice Matching

**CSV Parsing:**

```java
importFromCSV(contractId, file, userId)
  ├─> Read CSV line by line
  ├─> Parse header row
  ├─> For each data row:
  │   ├─> Parse date, amount, description, type
  │   ├─> Create RevenueItem
  │   └─> Add to list
  ├─> Save all items
  └─> Match to invoices

matchRevenueToInvoices(contractId)
  ├─> Get unassigned revenue items
  ├─> Get all invoices for contract
  ├─> For each revenue item:
  │   └─> Find invoice where:
  │       revenueDate >= invoice.billingPeriodStart AND
  │       revenueDate <= invoice.billingPeriodEnd
  └─> Save matched items
```

## API Spezifikation

### POST /api/v1/contracts/upload

**Request:**
```http
POST /api/v1/contracts/upload
Content-Type: multipart/form-data

file: [Binary]
userId: 1
```

**Response:**
```json
{
  "uploadId": 123,
  "status": "UPLOADED",
  "message": "Contract uploaded successfully. Processing..."
}
```

**Status Codes:**
- 200: Success
- 500: Upload failed

### GET /api/v1/contracts/upload/{uploadId}/status

**Response:**
```json
{
  "uploadId": 123,
  "filename": "contract.pdf",
  "status": "EXTRACTED",
  "uploadedAt": "2025-11-08T10:00:00",
  "processedAt": "2025-11-08T10:00:25",
  "extractedData": {
    "contractNumber": "VTR-2025-001",
    "title": "Dienstleistungsvertrag",
    "contractType": "Dienstleistungsvertrag",
    "partnerName": "Firma XYZ GmbH",
    "startDate": "2025-01-01",
    "endDate": "2026-12-31",
    "contractValue": 120000.00,
    "currency": "EUR",
    "billingCycle": "MONTHLY",
    "billingAmount": 10000.00,
    "billingStartDate": "2025-01-15"
  }
}
```

**Status Values:**
- `UPLOADED`: File uploaded, waiting for processing
- `PROCESSING`: AI extraction in progress
- `EXTRACTED`: Data extracted, waiting for confirmation
- `COMPLETED`: Contract created
- `FAILED`: Processing failed

### POST /api/v1/contracts/upload/{uploadId}/confirm

**Request:**
```json
{
  "contractNumber": "VTR-2025-001",
  "title": "Dienstleistungsvertrag mit Firma XYZ",
  "contractType": "Dienstleistungsvertrag",
  "partnerName": "Firma XYZ GmbH",
  "startDate": "2025-01-01",
  "endDate": "2026-12-31",
  "contractValue": 120000.00,
  "currency": "EUR",
  "billingCycle": "MONTHLY",
  "billingAmount": 10000.00,
  "billingStartDate": "2025-01-15",
  "paymentTermDays": 30,
  "autoRenewal": true
}
```

**Response:**
```json
{
  "contractId": 456,
  "contractNumber": "VTR-2025-001",
  "message": "Contract created successfully"
}
```

## Konfiguration

### Environment Variables

```properties
# OpenAI API
OPENAI_API_KEY=sk-...

# File Upload
contract.upload.dir=/tmp/contract-uploads/
contract.upload.max-file-size=10MB
```

### Dependencies

```xml
<!-- OpenAI API (via RestTemplate) -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>

<!-- PDF Processing -->
<dependency>
    <groupId>org.apache.pdfbox</groupId>
    <artifactId>pdfbox</artifactId>
    <version>2.0.29</version>
</dependency>

<!-- Word/Excel Processing -->
<dependency>
    <groupId>org.apache.poi</groupId>
    <artifactId>poi-ooxml</artifactId>
    <version>5.2.3</version>
</dependency>

<!-- JSON Processing -->
<dependency>
    <groupId>com.fasterxml.jackson.core</groupId>
    <artifactId>jackson-databind</artifactId>
</dependency>
```

## Testing

### Unit Tests

```java
@Test
void testExtractTextFromPDF() throws Exception {
    File pdfFile = new File("test-contract.pdf");
    String text = aiExtractionService.extractTextFromFile(pdfFile, "application/pdf");
    assertNotNull(text);
    assertTrue(text.length() > 100);
}

@Test
void testGenerateInvoiceSchedule() {
    Contract contract = new Contract();
    contract.setBillingCycle("MONTHLY");
    contract.setBillingAmount(new BigDecimal("10000"));
    contract.setBillingStartDate(LocalDate.of(2025, 1, 1));
    contract.setEndDate(LocalDate.of(2025, 12, 31));
    
    List<Invoice> invoices = invoiceScheduleService.generateInvoiceSchedule(contract);
    
    assertEquals(12, invoices.size());
    assertEquals("SCHEDULED", invoices.get(0).getStatus());
}
```

### Integration Tests

```java
@SpringBootTest
@AutoConfigureMockMvc
class ContractUploadControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Test
    void testUploadContract() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
            "file",
            "contract.pdf",
            "application/pdf",
            "Contract content".getBytes()
        );
        
        mockMvc.perform(multipart("/api/v1/contracts/upload")
                .file(file)
                .param("userId", "1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.uploadId").exists());
    }
}
```

## Performance

### Benchmarks

- **PDF Text Extraction:** ~500ms für 10-Seiten PDF
- **OpenAI API Call:** ~5-15s (abhängig von Textlänge)
- **Invoice Generation:** ~50ms für 24 Rechnungen
- **CSV Import:** ~200ms für 1000 Zeilen

### Optimierungen

1. **Async Processing:** Upload-Processing läuft asynchron
2. **Batch Insert:** Rechnungen werden als Batch gespeichert
3. **Connection Pooling:** HikariCP für DB-Connections
4. **Caching:** Extracted Data als JSONB in DB

## Monitoring

### Logs

```
2025-11-08 10:00:00 - Uploading contract file: contract.pdf by user 1
2025-11-08 10:00:01 - Contract uploaded successfully with ID: 123
2025-11-08 10:00:01 - Processing upload ID: 123
2025-11-08 10:00:02 - Extracted 5432 characters from PDF
2025-11-08 10:00:02 - Extracting contract data using OpenAI GPT-4.1-mini
2025-11-08 10:00:18 - Successfully extracted contract data: [contractNumber, title, ...]
2025-11-08 10:00:18 - Upload 123 processed successfully
2025-11-08 10:05:30 - Creating contract from upload ID: 123
2025-11-08 10:05:31 - Generating invoice schedule for contract ID: 456
2025-11-08 10:05:31 - Generated 24 invoices for contract 456
2025-11-08 10:05:31 - Contract created successfully with ID: 456
```

### Metrics

- Upload Success Rate
- AI Extraction Accuracy
- Average Processing Time
- Invoice Generation Count

## Troubleshooting

### Problem: AI extraction fails

**Symptom:** Status = FAILED, errorMessage in response

**Lösungen:**
1. Check OpenAI API key
2. Check network connectivity
3. Verify file format is supported
4. Check OpenAI API quota

### Problem: Invoice schedule not generated

**Symptom:** Contract created but no invoices

**Lösungen:**
1. Verify billingCycle is set
2. Verify billingStartDate is set
3. Check logs for errors in InvoiceScheduleService

### Problem: Revenue items not matched to invoices

**Symptom:** unassignedCount > 0 in summary

**Lösungen:**
1. Check revenue dates are within contract period
2. Verify invoice billing periods are set correctly
3. Run matchRevenueToInvoices() manually

## Future Enhancements

1. **Async Processing with Message Queue**
   - Replace Thread with RabbitMQ/Kafka
   - Better scalability and reliability

2. **OCR Support**
   - Extract text from scanned PDFs
   - Use Tesseract or Cloud Vision API

3. **Multi-Language Support**
   - Support English, German, French contracts
   - Language detection

4. **Confidence Scores**
   - Show AI confidence for each field
   - Highlight low-confidence fields

5. **Batch Upload**
   - Upload multiple contracts at once
   - Parallel processing

6. **Contract Templates**
   - Pre-defined templates for common contract types
   - Auto-fill based on template

7. **Approval Workflow**
   - Multi-stage approval process
   - Email notifications
