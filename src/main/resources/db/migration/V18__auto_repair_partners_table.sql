-- V18: Auto-Repair Partners Table
-- 
-- This migration automatically repairs the partners table issue
-- by checking if the table exists and creating it if needed.
--
-- This works even if V16/V17 failed, providing automatic recovery.

-- Step 1: Create partners table if it doesn't exist
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

-- Step 2: Create indexes if they don't exist
CREATE INDEX IF NOT EXISTS idx_partners_name ON partners(name);
CREATE INDEX IF NOT EXISTS idx_partners_partner_type ON partners(partner_type);
CREATE INDEX IF NOT EXISTS idx_partners_is_active ON partners(is_active);
CREATE INDEX IF NOT EXISTS idx_partners_partner_number ON partners(partner_number);

-- Step 3: Add Foreign Key constraint if it doesn't exist
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

-- Step 4: Migrate data from contracts if partners table is empty
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
    END IF;
END $$;

-- Step 5: Add comments
COMMENT ON TABLE partners IS 'Business partners (customers, suppliers, service providers) - Auto-created by V18';
COMMENT ON COLUMN partners.partner_type IS 'Type: CUSTOMER, SUPPLIER, SERVICE_PROVIDER, OTHER';

-- Success message
DO $$
BEGIN
    RAISE NOTICE 'V18: Partners table auto-repair completed successfully';
END $$;
