-- ============================================
-- V4: Vertragsimport und Arbeitsvorrat
-- ============================================

-- Tabelle für Import-Queue (Arbeitsvorrat)
CREATE TABLE IF NOT EXISTS contract_import_queue (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    original_filename VARCHAR(255) NOT NULL,
    file_path VARCHAR(500) NOT NULL,
    file_size BIGINT,
    mime_type VARCHAR(100),
    
    -- Extrahierte Daten (JSON)
    extracted_data TEXT,
    
    -- Status
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    -- PENDING: Wartet auf Verarbeitung
    -- PROCESSING: Wird gerade verarbeitet
    -- EXTRACTED: Daten extrahiert, wartet auf Review
    -- APPROVED: Genehmigt, kann als Vertrag erstellt werden
    -- REJECTED: Abgelehnt
    -- COMPLETED: Vertrag wurde erstellt
    -- ERROR: Fehler bei Verarbeitung
    
    -- Verknüpfung
    contract_id BIGINT NULL,
    
    -- Verarbeitung
    extraction_started_at TIMESTAMP NULL,
    extraction_completed_at TIMESTAMP NULL,
    error_message TEXT,
    
    -- Benutzer
    uploaded_by VARCHAR(100),
    reviewed_by VARCHAR(100),
    reviewed_at TIMESTAMP NULL,
    
    -- Timestamps
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    INDEX idx_import_queue_status (status),
    INDEX idx_import_queue_uploaded_by (uploaded_by),
    INDEX idx_import_queue_created (created_at),
    FOREIGN KEY (contract_id) REFERENCES contracts(id) ON DELETE SET NULL
)  ;

-- Tabelle für Batch-Uploads
CREATE TABLE IF NOT EXISTS import_batches (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    batch_name VARCHAR(255),
    total_files INT DEFAULT 0,
    processed_files INT DEFAULT 0,
    successful_files INT DEFAULT 0,
    failed_files INT DEFAULT 0,
    
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    -- PENDING, PROCESSING, COMPLETED, FAILED
    
    uploaded_by VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    INDEX idx_import_batches_status (status),
    INDEX idx_import_batches_uploaded_by (uploaded_by)
)  ;

-- Verknüpfung zwischen Batches und Queue-Items
ALTER TABLE contract_import_queue 
ADD COLUMN batch_id BIGINT NULL AFTER id,
ADD INDEX idx_import_queue_batch (batch_id),
ADD FOREIGN KEY (batch_id) REFERENCES import_batches(id) ON DELETE CASCADE;

