-- ============================================
-- V4: Vertragsimport und Arbeitsvorrat
-- PostgreSQL Compatible
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
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (contract_id) REFERENCES contracts(id) ON DELETE SET NULL
);

CREATE INDEX idx_import_queue_status ON contract_import_queue(status);
CREATE INDEX idx_import_queue_uploaded_by ON contract_import_queue(uploaded_by);
CREATE INDEX idx_import_queue_created ON contract_import_queue(created_at);

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
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_import_batches_status ON import_batches(status);
CREATE INDEX idx_import_batches_uploaded_by ON import_batches(uploaded_by);

-- Verknüpfung zwischen Batches und Queue-Items
ALTER TABLE contract_import_queue 
ADD COLUMN batch_id BIGINT NULL,
ADD CONSTRAINT fk_import_queue_batch 
    FOREIGN KEY (batch_id) 
    REFERENCES import_batches(id) 
    ON DELETE CASCADE;

CREATE INDEX idx_import_queue_batch ON contract_import_queue(batch_id);
