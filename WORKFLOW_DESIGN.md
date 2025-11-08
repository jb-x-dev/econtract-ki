# Contract Upload & AI Extraction Workflow

## Übersicht

Automatisierter Workflow für:
1. Vertrag hochladen (PDF/Word/Text)
2. KI-basiertes Auslesen der Vertragsdaten
3. Automatische Vertragsanlage
4. Generierung von Rechnungszeitpunkten
5. Import von Umsatzdaten

## Architektur

### 1. File Upload API
- **Endpoint:** `POST /api/v1/contracts/upload`
- **Input:** Multipart file (PDF, DOCX, TXT)
- **Output:** Upload-ID für Tracking

### 2. AI Extraction Service
- **LLM:** OpenAI GPT-4.1-mini (via pre-configured API)
- **Input:** Extracted text from document
- **Output:** Strukturierte Vertragsdaten (JSON)

### 3. Extracted Data Model
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
  "noticePeriodDays": 90,
  "autoRenewal": true,
  "billingCycle": "MONTHLY",  // MONTHLY, QUARTERLY, YEARLY, ONE_TIME
  "billingAmount": 10000.00,
  "billingStartDate": "2025-01-15",
  "paymentTermDays": 30,
  "revenueItems": [
    {
      "description": "Basisgebühr",
      "amount": 8000.00,
      "type": "RECURRING"
    },
    {
      "description": "Setup-Fee",
      "amount": 5000.00,
      "type": "ONE_TIME"
    }
  ]
}
```

### 4. Invoice Schedule Generation
- Basierend auf `billingCycle` und `billingStartDate`
- Automatische Erstellung von Invoice-Einträgen
- Status: `SCHEDULED`

### 5. Revenue Import
- CSV/Excel Upload für Umsatzdaten
- Zuordnung zu Contract
- Aktualisierung von Invoice-Beträgen

## Datenbank-Erweiterungen

### Neue Felder in `contracts` Tabelle
- `billing_cycle` VARCHAR(20)
- `billing_amount` DECIMAL(15,2)
- `billing_start_date` DATE
- `payment_term_days` INT

### Neue Tabelle: `contract_uploads`
```sql
CREATE TABLE contract_uploads (
    id BIGSERIAL PRIMARY KEY,
    filename VARCHAR(255) NOT NULL,
    file_path VARCHAR(500) NOT NULL,
    file_size BIGINT,
    mime_type VARCHAR(100),
    upload_status VARCHAR(50), -- UPLOADED, PROCESSING, EXTRACTED, COMPLETED, FAILED
    extracted_data JSONB,
    contract_id BIGINT REFERENCES contracts(id),
    error_message TEXT,
    uploaded_by BIGINT NOT NULL,
    uploaded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    processed_at TIMESTAMP
);
```

### Erweiterung `invoices` Tabelle
- `billing_period_start` DATE
- `billing_period_end` DATE
- `scheduled_date` DATE

## API Endpoints

### 1. Upload Contract
```
POST /api/v1/contracts/upload
Content-Type: multipart/form-data

Response:
{
  "uploadId": 123,
  "status": "PROCESSING",
  "message": "Contract uploaded successfully"
}
```

### 2. Get Extraction Status
```
GET /api/v1/contracts/upload/{uploadId}/status

Response:
{
  "uploadId": 123,
  "status": "EXTRACTED",
  "extractedData": { ... },
  "contractId": 456
}
```

### 3. Confirm & Create Contract
```
POST /api/v1/contracts/upload/{uploadId}/confirm
Body: { "extractedData": { ... } }  // User can edit before confirming

Response:
{
  "contractId": 456,
  "invoicesCreated": 12,
  "message": "Contract created successfully"
}
```

### 4. Import Revenue Data
```
POST /api/v1/contracts/{contractId}/revenue/import
Content-Type: multipart/form-data
File: revenue_data.csv

Response:
{
  "imported": 24,
  "updated": 12,
  "errors": []
}
```

## Frontend Flow

1. **Upload Page** (`contract-upload.html`)
   - File Drop Zone
   - Upload Button
   - Progress Indicator

2. **Extraction Review Page** (`contract-review.html`)
   - Extracted Data Form (editable)
   - Original Document Preview
   - Confirm/Reject Buttons

3. **Invoice Schedule Preview**
   - Generated Invoices List
   - Edit Schedule Option
   - Finalize Button

4. **Revenue Import Page**
   - CSV/Excel Upload
   - Column Mapping
   - Import Preview

## Implementation Steps

1. ✅ Design Architecture
2. ⏳ Create Database Migration (V14)
3. ⏳ Implement ContractUploadController
4. ⏳ Implement AIExtractionService
5. ⏳ Implement InvoiceScheduleService
6. ⏳ Implement RevenueImportService
7. ⏳ Create Frontend Pages
8. ⏳ Integration Testing
9. ⏳ Deployment

## Technologies

- **File Processing:** Apache PDFBox (PDF), Apache POI (Word)
- **AI:** OpenAI GPT-4.1-mini
- **File Storage:** Local filesystem (configurable to S3)
- **Frontend:** HTML/JS with Fetch API
