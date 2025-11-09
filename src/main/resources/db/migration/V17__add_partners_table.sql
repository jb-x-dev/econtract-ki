-- V17: Add Partners Table
-- 
-- This migration creates the missing 'partners' table that is referenced
-- by contracts.partner_id but was never created in previous migrations.
--
-- Partners represent business entities (customers, suppliers, service providers)
-- that the organization has contracts with.

CREATE TABLE IF NOT EXISTS partners (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    
    -- Basic Information
    name VARCHAR(255) NOT NULL,
    partner_type VARCHAR(50) NOT NULL, -- 'CUSTOMER', 'SUPPLIER', 'SERVICE_PROVIDER', 'OTHER'
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
    
    -- Banking Information (for payments)
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

-- Indexes for performance
CREATE INDEX IF NOT EXISTS idx_partners_name ON partners(name);
CREATE INDEX IF NOT EXISTS idx_partners_partner_type ON partners(partner_type);
CREATE INDEX IF NOT EXISTS idx_partners_is_active ON partners(is_active);
CREATE INDEX IF NOT EXISTS idx_partners_partner_number ON partners(partner_number);

-- Add Foreign Key constraint to contracts table
-- Note: This will NOT fail if partner_id values don't exist in partners table
-- because we use ON DELETE SET NULL
ALTER TABLE contracts 
    DROP CONSTRAINT IF EXISTS fk_contracts_partner_id;

ALTER TABLE contracts 
    ADD CONSTRAINT fk_contracts_partner_id 
    FOREIGN KEY (partner_id) 
    REFERENCES partners(id) 
    ON DELETE SET NULL;

-- Create sample partners from existing contracts
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
    1 as created_by, -- Admin user
    CURRENT_TIMESTAMP as created_at
FROM contracts
WHERE partner_name IS NOT NULL
ON CONFLICT (partner_number) DO NOTHING;

-- Update contracts to link to partners
UPDATE contracts c
SET partner_id = p.id
FROM partners p
WHERE c.partner_name = p.name
  AND c.partner_id IS NULL;

-- Add comment
COMMENT ON TABLE partners IS 'Business partners (customers, suppliers, service providers) that the organization has contracts with';
COMMENT ON COLUMN partners.partner_type IS 'Type of partner: CUSTOMER, SUPPLIER, SERVICE_PROVIDER, OTHER';
COMMENT ON COLUMN partners.is_active IS 'Whether the partner is currently active';
