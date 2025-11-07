-- ============================================================================
-- eContract KI - Billing Module Migration
-- Version: V7
-- Description: Add tables for service recording, price management, and invoicing
-- Author: jb-x Development Team
-- Date: 2025-11-01
-- ============================================================================

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
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    INDEX idx_code (code),
    INDEX idx_active (is_active)
)   ;

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
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY fk_cp_contract (contract_id) REFERENCES contracts(id) ON DELETE CASCADE,
    FOREIGN KEY fk_cp_service_category (service_category_id) REFERENCES service_categories(id) ON DELETE SET NULL,
    
    INDEX idx_contract_id (contract_id),
    INDEX idx_service_category_id (service_category_id),
    INDEX idx_valid_period (valid_from, valid_to),
    INDEX idx_active (is_active)
)   ;

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
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY fk_pt_contract_price (contract_price_id) REFERENCES contract_prices(id) ON DELETE CASCADE,
    
    INDEX idx_contract_price_id (contract_price_id),
    INDEX idx_quantity_range (min_quantity, max_quantity)
)   ;

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
    status ENUM('DRAFT', 'APPROVED', 'INVOICED', 'REJECTED') NOT NULL DEFAULT 'DRAFT',
    invoice_item_id BIGINT,
    invoiced_date DATE,
    performed_by_user_id BIGINT,
    approved_by_user_id BIGINT,
    approved_date TIMESTAMP,
    notes TEXT,
    created_by_user_id BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY fk_sr_contract (contract_id) REFERENCES contracts(id) ON DELETE CASCADE,
    FOREIGN KEY fk_sr_service_category (service_category_id) REFERENCES service_categories(id) ON DELETE SET NULL,
    FOREIGN KEY fk_sr_performed_by (performed_by_user_id) REFERENCES users(id) ON DELETE SET NULL,
    FOREIGN KEY fk_sr_approved_by (approved_by_user_id) REFERENCES users(id) ON DELETE SET NULL,
    FOREIGN KEY fk_sr_created_by (created_by_user_id) REFERENCES users(id) ON DELETE SET NULL,
    
    INDEX idx_contract_id (contract_id),
    INDEX idx_service_date (service_date),
    INDEX idx_service_category_id (service_category_id),
    INDEX idx_status (status),
    INDEX idx_not_invoiced (status, invoice_item_id),
    INDEX idx_service_period (service_period_start, service_period_end)
)   ;

-- ============================================================================
-- 5. INVOICES (Rechnungen)
-- ============================================================================

CREATE TABLE IF NOT EXISTS invoices (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    invoice_number VARCHAR(50) NOT NULL UNIQUE,
    invoice_type ENUM('SINGLE', 'COLLECTIVE') NOT NULL DEFAULT 'SINGLE',
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
    status ENUM('DRAFT', 'APPROVED', 'SENT', 'PAID', 'OVERDUE', 'CANCELLED') NOT NULL DEFAULT 'DRAFT',
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
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    cancelled_date TIMESTAMP,
    cancelled_reason TEXT,
    
    FOREIGN KEY fk_inv_contract (contract_id) REFERENCES contracts(id) ON DELETE SET NULL,
    FOREIGN KEY fk_inv_created_by (created_by_user_id) REFERENCES users(id) ON DELETE SET NULL,
    FOREIGN KEY fk_inv_approved_by (approved_by_user_id) REFERENCES users(id) ON DELETE SET NULL,
    FOREIGN KEY fk_inv_sent_by (sent_by_user_id) REFERENCES users(id) ON DELETE SET NULL,
    
    INDEX idx_invoice_number (invoice_number),
    INDEX idx_invoice_date (invoice_date),
    INDEX idx_due_date (due_date),
    INDEX idx_status (status),
    INDEX idx_contract_id (contract_id),
    INDEX idx_billing_period (billing_period_start, billing_period_end)
)   ;

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
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY fk_ii_invoice (invoice_id) REFERENCES invoices(id) ON DELETE CASCADE,
    FOREIGN KEY fk_ii_service_record (service_record_id) REFERENCES service_records(id) ON DELETE SET NULL,
    FOREIGN KEY fk_ii_contract (contract_id) REFERENCES contracts(id) ON DELETE SET NULL,
    
    INDEX idx_invoice_id (invoice_id),
    INDEX idx_service_record_id (service_record_id),
    INDEX idx_position_number (invoice_id, position_number)
)   ;

-- ============================================================================
-- 7. BILLING PERIODS (Abrechnungszeiträume)
-- ============================================================================

CREATE TABLE IF NOT EXISTS billing_periods (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    contract_id BIGINT NOT NULL,
    period_name VARCHAR(100) NOT NULL,
    period_start DATE NOT NULL,
    period_end DATE NOT NULL,
    billing_type ENUM('MONTHLY', 'QUARTERLY', 'YEARLY', 'CUSTOM') NOT NULL DEFAULT 'MONTHLY',
    status ENUM('OPEN', 'INVOICED', 'CLOSED') NOT NULL DEFAULT 'OPEN',
    invoice_id BIGINT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY fk_bp_contract (contract_id) REFERENCES contracts(id) ON DELETE CASCADE,
    FOREIGN KEY fk_bp_invoice (invoice_id) REFERENCES invoices(id) ON DELETE SET NULL,
    
    INDEX idx_contract_id (contract_id),
    INDEX idx_period (period_start, period_end),
    INDEX idx_status (status)
)   ;

-- ============================================================================
-- 8. INVOICE TEMPLATES (Rechnungsvorlagen)
-- ============================================================================

CREATE TABLE IF NOT EXISTS invoice_templates (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    template_type ENUM('SINGLE', 'COLLECTIVE') NOT NULL DEFAULT 'SINGLE',
    header_text TEXT,
    footer_text TEXT,
    payment_terms_text TEXT,
    logo_path VARCHAR(500),
    company_info TEXT,
    is_default BOOLEAN NOT NULL DEFAULT FALSE,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    INDEX idx_name (name),
    INDEX idx_default (is_default),
    INDEX idx_active (is_active)
)   ;

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
    'Zahlbar innerhalb von 14 Tagen ohne Abzug.\nBitte verwenden Sie die Rechnungsnummer als Verwendungszweck.',
    'jb-x business solutions GmbH\nMusterstraße 123\n12345 Musterstadt\nTel: +49 123 456789\nE-Mail: info@jb-x.de\nWeb: www.jb-x.de\n\nGeschäftsführer: Max Mustermann\nRegistergericht: Amtsgericht Musterstadt\nHRB 12345\nUSt-IdNr.: DE123456789',
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
    DATEDIFF(CURRENT_DATE, i.due_date) AS days_overdue,
    COUNT(ii.id) AS item_count
FROM invoices i
LEFT JOIN invoice_items ii ON i.id = ii.invoice_id
GROUP BY i.id;

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
GROUP BY c.id;

-- ============================================================================
-- MIGRATION COMPLETE
-- ============================================================================
