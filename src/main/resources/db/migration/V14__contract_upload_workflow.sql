-- ============================================================================
-- eContract KI - Contract Upload Workflow
-- Version: V14
-- Description: Add tables and fields for automated contract upload and AI extraction
-- Author: jb-x Development Team
-- Date: 2025-11-08
-- PostgreSQL Compatible
-- ============================================================================

-- Add billing fields to contracts table
ALTER TABLE contracts ADD COLUMN IF NOT EXISTS billing_cycle VARCHAR(20);
ALTER TABLE contracts ADD COLUMN IF NOT EXISTS billing_amount DECIMAL(15,2);
ALTER TABLE contracts ADD COLUMN IF NOT EXISTS billing_start_date DATE;
ALTER TABLE contracts ADD COLUMN IF NOT EXISTS payment_term_days INT DEFAULT 30;

-- Create contract_uploads table for tracking uploaded files
CREATE TABLE IF NOT EXISTS contract_uploads (
    id BIGSERIAL PRIMARY KEY,
    filename VARCHAR(255) NOT NULL,
    file_path VARCHAR(500) NOT NULL,
    file_size BIGINT,
    mime_type VARCHAR(100),
    upload_status VARCHAR(50) NOT NULL DEFAULT 'UPLOADED',
    extracted_data JSONB,
    contract_id BIGINT REFERENCES contracts(id) ON DELETE SET NULL,
    error_message TEXT,
    uploaded_by BIGINT NOT NULL,
    uploaded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    processed_at TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_contract_uploads_status ON contract_uploads(upload_status);
CREATE INDEX IF NOT EXISTS idx_contract_uploads_uploaded_by ON contract_uploads(uploaded_by);
CREATE INDEX IF NOT EXISTS idx_contract_uploads_contract_id ON contract_uploads(contract_id);

-- Add billing period fields to invoices table
ALTER TABLE invoices ADD COLUMN IF NOT EXISTS billing_period_start DATE;
ALTER TABLE invoices ADD COLUMN IF NOT EXISTS billing_period_end DATE;
ALTER TABLE invoices ADD COLUMN IF NOT EXISTS scheduled_date DATE;

-- Create revenue_items table for imported revenue data
CREATE TABLE IF NOT EXISTS revenue_items (
    id BIGSERIAL PRIMARY KEY,
    contract_id BIGINT NOT NULL REFERENCES contracts(id) ON DELETE CASCADE,
    invoice_id BIGINT REFERENCES invoices(id) ON DELETE SET NULL,
    description VARCHAR(500),
    amount DECIMAL(15,2) NOT NULL,
    revenue_date DATE NOT NULL,
    revenue_type VARCHAR(50), -- RECURRING, ONE_TIME, USAGE_BASED
    imported_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    imported_by BIGINT,
    notes TEXT
);

CREATE INDEX IF NOT EXISTS idx_revenue_items_contract ON revenue_items(contract_id);
CREATE INDEX IF NOT EXISTS idx_revenue_items_invoice ON revenue_items(invoice_id);
CREATE INDEX IF NOT EXISTS idx_revenue_items_date ON revenue_items(revenue_date);

-- ============================================================================
-- MIGRATION COMPLETE
-- New Features:
-- - Contract billing cycle management
-- - Automated contract upload tracking
-- - Revenue items for detailed financial tracking
-- - Invoice scheduling support
-- ============================================================================
