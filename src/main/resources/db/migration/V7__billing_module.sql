-- ============================================================================
-- eContract KI - Billing Module Migration
-- Version: V7
-- Description: Add tables for service recording, price management, and invoicing
-- Author: jb-x Development Team
-- Date: 2025-11-01
-- PostgreSQL Compatible
-- ============================================================================

-- Create ENUM types for billing module
CREATE TYPE service_record_status AS ENUM ('DRAFT', 'APPROVED', 'INVOICED', 'REJECTED');
CREATE TYPE invoice_type AS ENUM ('SINGLE', 'COLLECTIVE');
CREATE TYPE invoice_status AS ENUM ('DRAFT', 'APPROVED', 'SENT', 'PAID', 'OVERDUE', 'CANCELLED');
CREATE TYPE billing_type AS ENUM ('MONTHLY', 'QUARTERLY', 'YEARLY', 'CUSTOM');
CREATE TYPE billing_period_status AS ENUM ('OPEN', 'INVOICED', 'CLOSED');

-- ============================================================================
-- 1. SERVICE CATEGORIES (Leistungskategorien)
-- ============================================================================

CREATE TABLE IF NOT EXISTS service_categories (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    code VARCHAR(20) NOT NULL UNIQUE,
    description TEXT,
    default_unit VARCHAR(50),
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_service_categories_code ON service_categories(code);
CREATE INDEX idx_service_categories_active ON service_categories(is_active);

-- ============================================================================
-- 2. CONTRACT PRICES (Preislisten)
-- ============================================================================

CREATE TABLE IF NOT EXISTS contract_prices (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    contract_id BIGINT NOT NULL,
    service_category_id BIGINT,
    description VARCHAR(255) NOT NULL,
    unit VARCHAR(50) NOT NULL,
    unit_price_net DECIMAL(15,2) NOT NULL,
    valid_from DATE NOT NULL,
    valid_to DATE,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_cp_contract FOREIGN KEY (contract_id) REFERENCES contracts(id) ON DELETE CASCADE,
    CONSTRAINT fk_cp_service_category FOREIGN KEY (service_category_id) REFERENCES service_categories(id) ON DELETE SET NULL
);

CREATE INDEX idx_contract_prices_contract_id ON contract_prices(contract_id);
CREATE INDEX idx_contract_prices_service_category_id ON contract_prices(service_category_id);
CREATE INDEX idx_contract_prices_valid_period ON contract_prices(valid_from, valid_to);
CREATE INDEX idx_contract_prices_active ON contract_prices(is_active);

-- ============================================================================
-- 3. PRICE TIERS (Staffelpreise)
-- ============================================================================

CREATE TABLE IF NOT EXISTS price_tiers (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    contract_price_id BIGINT NOT NULL,
    min_quantity DECIMAL(10,2) NOT NULL,
    max_quantity DECIMAL(10,2),
    unit_price_net DECIMAL(15,2) NOT NULL,
    discount_percentage DECIMAL(5,2),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_pt_contract_price FOREIGN KEY (contract_price_id) REFERENCES contract_prices(id) ON DELETE CASCADE
);

CREATE INDEX idx_price_tiers_contract_price_id ON price_tiers(contract_price_id);
CREATE INDEX idx_price_tiers_quantity_range ON price_tiers(min_quantity, max_quantity);

-- ============================================================================
-- 4. SERVICE RECORDS (Leistungserfassung)
-- ============================================================================

CREATE TABLE IF NOT EXISTS service_records (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    contract_id BIGINT NOT NULL,
    service_date DATE NOT NULL,
    service_period_start DATE,
    service_period_end DATE,
    service_category_id BIGINT,
    description TEXT NOT NULL,
    quantity DECIMAL(10,2) NOT NULL,
    unit VARCHAR(50) NOT NULL,
    unit_price_net DECIMAL(15,2) NOT NULL,
    total_net DECIMAL(15,2) NOT NULL,
    status service_record_status NOT NULL DEFAULT 'DRAFT',
    invoice_item_id BIGINT,
    invoiced_date DATE,
    performed_by_user_id BIGINT,
    approved_by_user_id BIGINT,
    approved_date TIMESTAMP,
    notes TEXT,
    created_by_user_id BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_sr_contract FOREIGN KEY (contract_id) REFERENCES contracts(id) ON DELETE CASCADE,
    CONSTRAINT fk_sr_service_category FOREIGN KEY (service_category_id) REFERENCES service_categories(id) ON DELETE SET NULL,
    CONSTRAINT fk_sr_performed_by FOREIGN KEY (performed_by_user_id) REFERENCES users(id) ON DELETE SET NULL,
    CONSTRAINT fk_sr_approved_by FOREIGN KEY (approved_by_user_id) REFERENCES users(id) ON DELETE SET NULL,
    CONSTRAINT fk_sr_created_by FOREIGN KEY (created_by_user_id) REFERENCES users(id) ON DELETE SET NULL
);

CREATE INDEX idx_service_records_contract_id ON service_records(contract_id);
CREATE INDEX idx_service_records_service_date ON service_records(service_date);
CREATE INDEX idx_service_records_service_category_id ON service_records(service_category_id);
CREATE INDEX idx_service_records_status ON service_records(status);
CREATE INDEX idx_service_records_not_invoiced ON service_records(status, invoice_item_id);
CREATE INDEX idx_service_records_service_period ON service_records(service_period_start, service_period_end);

-- ============================================================================
-- 5. INVOICES (Rechnungen)
-- ============================================================================

CREATE TABLE IF NOT EXISTS invoices (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    invoice_number VARCHAR(50) NOT NULL UNIQUE,
    invoice_type invoice_type NOT NULL DEFAULT 'SINGLE',
    invoice_date DATE NOT NULL,
    billing_period_start DATE NOT NULL,
    billing_period_end DATE NOT NULL,
    due_date DATE NOT NULL,
    contract_id BIGINT,
    partner_id BIGINT NOT NULL,
    partner_name VARCHAR(255) NOT NULL,
    partner_address TEXT NOT NULL,
    partner_tax_id VARCHAR(50),
    subtotal_net DECIMAL(15,2) NOT NULL DEFAULT 0.00,
    tax_rate DECIMAL(5,2) NOT NULL DEFAULT 19.00,
    tax_amount DECIMAL(15,2) NOT NULL DEFAULT 0.00,
    total_gross DECIMAL(15,2) NOT NULL DEFAULT 0.00,
    discount_percentage DECIMAL(5,2),
    discount_amount DECIMAL(15,2),
    currency VARCHAR(3) NOT NULL DEFAULT 'EUR',
    status invoice_status NOT NULL DEFAULT 'DRAFT',
    payment_terms VARCHAR(255),
    payment_method VARCHAR(50),
    bank_account VARCHAR(100),
    notes TEXT,
    customer_notes TEXT,
    pdf_file_path VARCHAR(500),
    sent_date TIMESTAMP,
    sent_by_user_id BIGINT,
    approved_date TIMESTAMP,
    approved_by_user_id BIGINT,
    created_by_user_id BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    cancelled_date TIMESTAMP,
    cancelled_reason TEXT,
    
    CONSTRAINT fk_inv_contract FOREIGN KEY (contract_id) REFERENCES contracts(id) ON DELETE SET NULL,
    CONSTRAINT fk_inv_created_by FOREIGN KEY (created_by_user_id) REFERENCES users(id) ON DELETE SET NULL,
    CONSTRAINT fk_inv_approved_by FOREIGN KEY (approved_by_user_id) REFERENCES users(id) ON DELETE SET NULL,
    CONSTRAINT fk_inv_sent_by FOREIGN KEY (sent_by_user_id) REFERENCES users(id) ON DELETE SET NULL
);

CREATE INDEX idx_invoices_invoice_number ON invoices(invoice_number);
CREATE INDEX idx_invoices_invoice_date ON invoices(invoice_date);
CREATE INDEX idx_invoices_due_date ON invoices(due_date);
CREATE INDEX idx_invoices_status ON invoices(status);
CREATE INDEX idx_invoices_contract_id ON invoices(contract_id);
CREATE INDEX idx_invoices_billing_period ON invoices(billing_period_start, billing_period_end);

-- ============================================================================
-- 6. INVOICE ITEMS (Rechnungspositionen)
-- ============================================================================

CREATE TABLE IF NOT EXISTS invoice_items (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    invoice_id BIGINT NOT NULL,
    position_number INT NOT NULL,
    service_record_id BIGINT,
    contract_id BIGINT,
    description TEXT NOT NULL,
    service_category VARCHAR(100),
    quantity DECIMAL(10,2) NOT NULL,
    unit VARCHAR(50) NOT NULL,
    unit_price_net DECIMAL(15,2) NOT NULL,
    discount_percentage DECIMAL(5,2),
    discount_amount DECIMAL(15,2),
    subtotal_net DECIMAL(15,2) NOT NULL,
    tax_rate DECIMAL(5,2) NOT NULL DEFAULT 19.00,
    tax_amount DECIMAL(15,2) NOT NULL,
    total_gross DECIMAL(15,2) NOT NULL,
    service_period_start DATE,
    service_period_end DATE,
    notes TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_ii_invoice FOREIGN KEY (invoice_id) REFERENCES invoices(id) ON DELETE CASCADE,
    CONSTRAINT fk_ii_service_record FOREIGN KEY (service_record_id) REFERENCES service_records(id) ON DELETE SET NULL,
    CONSTRAINT fk_ii_contract FOREIGN KEY (contract_id) REFERENCES contracts(id) ON DELETE SET NULL
);

CREATE INDEX idx_invoice_items_invoice_id ON invoice_items(invoice_id);
CREATE INDEX idx_invoice_items_service_record_id ON invoice_items(service_record_id);
CREATE INDEX idx_invoice_items_position_number ON invoice_items(invoice_id, position_number);

-- ============================================================================
-- 7. BILLING PERIODS (Abrechnungszeiträume)
-- ============================================================================

CREATE TABLE IF NOT EXISTS billing_periods (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    contract_id BIGINT NOT NULL,
    period_name VARCHAR(100) NOT NULL,
    period_start DATE NOT NULL,
    period_end DATE NOT NULL,
    billing_type billing_type NOT NULL DEFAULT 'MONTHLY',
    status billing_period_status NOT NULL DEFAULT 'OPEN',
    invoice_id BIGINT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_bp_contract FOREIGN KEY (contract_id) REFERENCES contracts(id) ON DELETE CASCADE,
    CONSTRAINT fk_bp_invoice FOREIGN KEY (invoice_id) REFERENCES invoices(id) ON DELETE SET NULL
);

CREATE INDEX idx_billing_periods_contract_id ON billing_periods(contract_id);
CREATE INDEX idx_billing_periods_period ON billing_periods(period_start, period_end);
CREATE INDEX idx_billing_periods_status ON billing_periods(status);

-- ============================================================================
-- 8. INVOICE TEMPLATES (Rechnungsvorlagen)
-- ============================================================================

CREATE TABLE IF NOT EXISTS invoice_templates (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    template_type invoice_type NOT NULL DEFAULT 'SINGLE',
    header_text TEXT,
    footer_text TEXT,
    payment_terms_text TEXT,
    logo_path VARCHAR(500),
    company_info TEXT,
    is_default BOOLEAN NOT NULL DEFAULT FALSE,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_invoice_templates_name ON invoice_templates(name);
CREATE INDEX idx_invoice_templates_default ON invoice_templates(is_default);
CREATE INDEX idx_invoice_templates_active ON invoice_templates(is_active);

-- ============================================================================
-- 9. ADD FOREIGN KEY TO SERVICE_RECORDS (after invoice_items table exists)
-- ============================================================================

ALTER TABLE service_records 
ADD CONSTRAINT fk_sr_invoice_item 
FOREIGN KEY (invoice_item_id) REFERENCES invoice_items(id) ON DELETE SET NULL;

-- ============================================================================
-- 10. INSERT DEFAULT DATA
-- ============================================================================

-- Default Service Categories
INSERT INTO service_categories (name, code, description, default_unit, is_active) VALUES
('Beratung', 'CONSULT', 'Beratungsleistungen', 'Stunden', TRUE),
('Entwicklung', 'DEV', 'Softwareentwicklung', 'Stunden', TRUE),
('Support', 'SUPPORT', 'Support und Wartung', 'Stunden', TRUE),
('Schulung', 'TRAINING', 'Schulungen und Workshops', 'Tage', TRUE),
('Projektmanagement', 'PM', 'Projektmanagement-Leistungen', 'Stunden', TRUE),
('Analyse', 'ANALYSIS', 'Analyse und Konzeption', 'Stunden', TRUE),
('Testing', 'TEST', 'Testing und Qualitätssicherung', 'Stunden', TRUE),
('Dokumentation', 'DOC', 'Dokumentationserstellung', 'Seiten', TRUE),
('Lizenz', 'LICENSE', 'Softwarelizenzen', 'Stück', TRUE),
('Hardware', 'HW', 'Hardware und Geräte', 'Stück', TRUE);

-- Default Invoice Template
INSERT INTO invoice_templates (
    name, 
    template_type, 
    header_text, 
    footer_text, 
    payment_terms_text,
    company_info,
    is_default, 
    is_active
) VALUES (
    'Standard Rechnung',
    'SINGLE',
    'Rechnung',
    'Vielen Dank für Ihr Vertrauen und die gute Zusammenarbeit.',
    'Zahlbar innerhalb von 14 Tagen ohne Abzug.
Bitte verwenden Sie die Rechnungsnummer als Verwendungszweck.',
    'jb-x business solutions GmbH
Musterstraße 123
12345 Musterstadt
Tel: +49 123 456789
E-Mail: info@jb-x.de
Web: www.jb-x.de

Geschäftsführer: Max Mustermann
Registergericht: Amtsgericht Musterstadt
HRB 12345
USt-IdNr.: DE123456789',
    TRUE,
    TRUE
);

-- ============================================================================
-- 11. CREATE VIEWS FOR REPORTING
-- ============================================================================

-- View: Uninvoiced Service Records
CREATE OR REPLACE VIEW v_uninvoiced_services AS
SELECT 
    sr.id,
    sr.contract_id,
    c.title AS contract_title,
    c.partner_name,
    sr.service_date,
    sr.service_period_start,
    sr.service_period_end,
    sc.name AS service_category,
    sr.description,
    sr.quantity,
    sr.unit,
    sr.unit_price_net,
    sr.total_net,
    sr.status,
    sr.created_at
FROM service_records sr
JOIN contracts c ON sr.contract_id = c.id
LEFT JOIN service_categories sc ON sr.service_category_id = sc.id
WHERE sr.status = 'APPROVED'
  AND sr.invoice_item_id IS NULL;

-- View: Invoice Summary
CREATE OR REPLACE VIEW v_invoice_summary AS
SELECT 
    i.id,
    i.invoice_number,
    i.invoice_type,
    i.invoice_date,
    i.due_date,
    i.partner_name,
    i.subtotal_net,
    i.tax_amount,
    i.total_gross,
    i.status,
    i.currency,
    CASE 
        WHEN i.status = 'PAID' THEN 'Bezahlt'
        WHEN i.status = 'OVERDUE' THEN 'Überfällig'
        WHEN i.status = 'SENT' THEN 'Versendet'
        WHEN i.status = 'APPROVED' THEN 'Freigegeben'
        WHEN i.status = 'DRAFT' THEN 'Entwurf'
        WHEN i.status = 'CANCELLED' THEN 'Storniert'
    END AS status_text,
    EXTRACT(DAY FROM (CURRENT_DATE - i.due_date)) AS days_overdue,
    COUNT(ii.id) AS item_count
FROM invoices i
LEFT JOIN invoice_items ii ON i.id = ii.invoice_id
GROUP BY i.id, i.invoice_number, i.invoice_type, i.invoice_date, i.due_date, i.partner_name, 
         i.subtotal_net, i.tax_amount, i.total_gross, i.status, i.currency;

-- View: Contract Billing Summary
CREATE OR REPLACE VIEW v_contract_billing_summary AS
SELECT 
    c.id AS contract_id,
    c.title AS contract_title,
    c.partner_name,
    c.contract_value,
    COUNT(DISTINCT sr.id) AS total_services,
    COUNT(DISTINCT CASE WHEN sr.status = 'APPROVED' AND sr.invoice_item_id IS NULL THEN sr.id END) AS uninvoiced_services,
    COUNT(DISTINCT CASE WHEN sr.status = 'INVOICED' THEN sr.id END) AS invoiced_services,
    COALESCE(SUM(CASE WHEN sr.status = 'APPROVED' AND sr.invoice_item_id IS NULL THEN sr.total_net END), 0) AS uninvoiced_amount,
    COALESCE(SUM(CASE WHEN sr.status = 'INVOICED' THEN sr.total_net END), 0) AS invoiced_amount,
    COUNT(DISTINCT i.id) AS invoice_count
FROM contracts c
LEFT JOIN service_records sr ON c.id = sr.contract_id
LEFT JOIN invoice_items ii ON sr.id = ii.service_record_id
LEFT JOIN invoices i ON ii.invoice_id = i.id
GROUP BY c.id, c.title, c.partner_name, c.contract_value;

-- ============================================================================
-- MIGRATION COMPLETE
-- ============================================================================
