-- ============================================================================
-- V19: COMPREHENSIVE REPAIR - All Missing Tables & Features
-- ============================================================================
-- This migration repairs ALL failed migrations (V14-V18) in one idempotent script
-- Can be run multiple times safely - checks existence before creating
-- ============================================================================

-- ============================================================================
-- PART 1: V14 - Contract Upload Workflow
-- ============================================================================

-- Add billing fields to contracts table
ALTER TABLE contracts ADD COLUMN IF NOT EXISTS billing_cycle VARCHAR(20);
ALTER TABLE contracts ADD COLUMN IF NOT EXISTS billing_amount DECIMAL(15,2);
ALTER TABLE contracts ADD COLUMN IF NOT EXISTS billing_start_date DATE;
ALTER TABLE contracts ADD COLUMN IF NOT EXISTS payment_term_days INT DEFAULT 30;

-- Create contract_uploads table
CREATE TABLE IF NOT EXISTS contract_uploads (
    id BIGSERIAL PRIMARY KEY,
    filename VARCHAR(255) NOT NULL,
    file_path VARCHAR(500) NOT NULL,
    file_size BIGINT,
    mime_type VARCHAR(100),
    upload_status VARCHAR(50) NOT NULL DEFAULT 'UPLOADED',
    extracted_data JSONB,
    contract_id BIGINT,
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

-- Create revenue_items table
CREATE TABLE IF NOT EXISTS revenue_items (
    id BIGSERIAL PRIMARY KEY,
    contract_id BIGINT NOT NULL,
    invoice_id BIGINT,
    description VARCHAR(500),
    amount DECIMAL(15,2) NOT NULL,
    revenue_date DATE NOT NULL,
    revenue_type VARCHAR(50),
    imported_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    imported_by BIGINT,
    notes TEXT
);

CREATE INDEX IF NOT EXISTS idx_revenue_items_contract ON revenue_items(contract_id);
CREATE INDEX IF NOT EXISTS idx_revenue_items_invoice ON revenue_items(invoice_id);
CREATE INDEX IF NOT EXISTS idx_revenue_items_date ON revenue_items(revenue_date);

-- ============================================================================
-- PART 2: V16 - Performance Indexes (without partners)
-- ============================================================================

-- Contracts indexes
CREATE INDEX IF NOT EXISTS idx_contracts_status ON contracts(status);
CREATE INDEX IF NOT EXISTS idx_contracts_contract_type ON contracts(contract_type);
CREATE INDEX IF NOT EXISTS idx_contracts_start_date ON contracts(start_date);
CREATE INDEX IF NOT EXISTS idx_contracts_end_date ON contracts(end_date);
CREATE INDEX IF NOT EXISTS idx_contracts_partner_name ON contracts(partner_name);

-- Invoices indexes
CREATE INDEX IF NOT EXISTS idx_invoices_status ON invoices(status);
CREATE INDEX IF NOT EXISTS idx_invoices_contract_id ON invoices(contract_id);
CREATE INDEX IF NOT EXISTS idx_invoices_invoice_date ON invoices(invoice_date);
CREATE INDEX IF NOT EXISTS idx_invoices_due_date ON invoices(due_date);

-- Users indexes
CREATE INDEX IF NOT EXISTS idx_users_username ON users(username);
CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);
CREATE INDEX IF NOT EXISTS idx_users_role ON users(role);

-- ============================================================================
-- PART 3: V17/V18 - Partners Table
-- ============================================================================

-- Create partners table
CREATE TABLE IF NOT EXISTS partners (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    
    -- Basic Information
    name VARCHAR(255) NOT NULL,
    partner_type VARCHAR(50) NOT NULL,
    partner_number VARCHAR(50) UNIQUE,
    
    -- Contact Information
    email VARCHAR(255),
    phone VARCHAR(50),
    website VARCHAR(255),
    
    -- Address
    street VARCHAR(255),
    city VARCHAR(100),
    postal_code VARCHAR(20),
    country VARCHAR(100),
    
    -- Business Information
    tax_id VARCHAR(50),
    vat_id VARCHAR(50),
    company_registration_number VARCHAR(50),
    
    -- Banking Information
    bank_name VARCHAR(255),
    iban VARCHAR(50),
    bic VARCHAR(20),
    
    -- Status
    is_active BOOLEAN DEFAULT TRUE,
    notes TEXT,
    
    -- Audit Fields
    created_by BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    -- Constraints
    CONSTRAINT chk_partner_type CHECK (partner_type IN ('CUSTOMER', 'SUPPLIER', 'SERVICE_PROVIDER', 'OTHER'))
);

-- Partners indexes
CREATE INDEX IF NOT EXISTS idx_partners_name ON partners(name);
CREATE INDEX IF NOT EXISTS idx_partners_partner_type ON partners(partner_type);
CREATE INDEX IF NOT EXISTS idx_partners_is_active ON partners(is_active);
CREATE INDEX IF NOT EXISTS idx_partners_partner_number ON partners(partner_number);

-- Add Foreign Key constraint if it doesn't exist
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint 
        WHERE conname = 'fk_contracts_partner_id'
    ) THEN
        ALTER TABLE contracts 
            ADD CONSTRAINT fk_contracts_partner_id 
            FOREIGN KEY (partner_id) 
            REFERENCES partners(id) 
            ON DELETE SET NULL;
    END IF;
END $$;

-- Add Foreign Keys for other tables
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint 
        WHERE conname = 'fk_contract_uploads_contract_id'
    ) THEN
        ALTER TABLE contract_uploads 
            ADD CONSTRAINT fk_contract_uploads_contract_id 
            FOREIGN KEY (contract_id) 
            REFERENCES contracts(id) 
            ON DELETE SET NULL;
    END IF;
END $$;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint 
        WHERE conname = 'fk_revenue_items_contract_id'
    ) THEN
        ALTER TABLE revenue_items 
            ADD CONSTRAINT fk_revenue_items_contract_id 
            FOREIGN KEY (contract_id) 
            REFERENCES contracts(id) 
            ON DELETE CASCADE;
    END IF;
END $$;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint 
        WHERE conname = 'fk_revenue_items_invoice_id'
    ) THEN
        ALTER TABLE revenue_items 
            ADD CONSTRAINT fk_revenue_items_invoice_id 
            FOREIGN KEY (invoice_id) 
            REFERENCES invoices(id) 
            ON DELETE SET NULL;
    END IF;
END $$;

-- ============================================================================
-- PART 4: Data Migration - Partners
-- ============================================================================

DO $$
DECLARE
    partner_count INTEGER;
BEGIN
    -- Check if partners table is empty
    SELECT COUNT(*) INTO partner_count FROM partners;
    
    IF partner_count = 0 THEN
        -- Extract unique partners from contracts
        INSERT INTO partners (name, partner_type, is_active, created_by, created_at)
        SELECT DISTINCT 
            partner_name,
            CASE 
                WHEN contract_type LIKE '%Lieferanten%' THEN 'SUPPLIER'
                WHEN contract_type LIKE '%Kunden%' THEN 'CUSTOMER'
                WHEN contract_type LIKE '%Dienstleistung%' THEN 'SERVICE_PROVIDER'
                ELSE 'OTHER'
            END as partner_type,
            TRUE as is_active,
            1 as created_by,
            CURRENT_TIMESTAMP as created_at
        FROM contracts
        WHERE partner_name IS NOT NULL
        ON CONFLICT (partner_number) DO NOTHING;
        
        -- Link contracts to partners
        UPDATE contracts c
        SET partner_id = p.id
        FROM partners p
        WHERE c.partner_name = p.name
          AND c.partner_id IS NULL;
          
        RAISE NOTICE 'Migrated % partners from contracts', (SELECT COUNT(*) FROM partners);
    ELSE
        RAISE NOTICE 'Partners table already has % entries, skipping migration', partner_count;
    END IF;
END $$;

-- ============================================================================
-- PART 5: Comments & Documentation
-- ============================================================================

COMMENT ON TABLE contract_uploads IS 'Tracks uploaded contract files and AI extraction status';
COMMENT ON TABLE revenue_items IS 'Detailed revenue tracking linked to contracts and invoices';
COMMENT ON TABLE partners IS 'Business partners (customers, suppliers, service providers)';

COMMENT ON COLUMN partners.partner_type IS 'Type: CUSTOMER, SUPPLIER, SERVICE_PROVIDER, OTHER';
COMMENT ON COLUMN contract_uploads.upload_status IS 'Status: UPLOADED, PROCESSING, COMPLETED, FAILED';
COMMENT ON COLUMN revenue_items.revenue_type IS 'Type: RECURRING, ONE_TIME, USAGE_BASED';

-- ============================================================================
-- SUCCESS MESSAGE
-- ============================================================================

DO $$
BEGIN
    RAISE NOTICE '=================================================================';
    RAISE NOTICE 'V19: Comprehensive Repair completed successfully!';
    RAISE NOTICE 'Created/Verified:';
    RAISE NOTICE '  - contract_uploads table';
    RAISE NOTICE '  - revenue_items table';
    RAISE NOTICE '  - partners table';
    RAISE NOTICE '  - All performance indexes';
    RAISE NOTICE '  - All foreign key constraints';
    RAISE NOTICE '  - Partner data migration';
    RAISE NOTICE '=================================================================';
END $$;
