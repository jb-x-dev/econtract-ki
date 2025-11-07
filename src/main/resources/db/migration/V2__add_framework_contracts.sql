-- Migration V2: Rahmenverträge und Vertragspflege
-- PostgreSQL Compatible

-- Create additional ENUM types
CREATE TYPE framework_contract_type AS ENUM ('SUPPLIER', 'CUSTOMER', 'SERVICE', 'NDA', 'EMPLOYMENT');
CREATE TYPE framework_status AS ENUM ('DRAFT', 'IN_APPROVAL', 'ACTIVE', 'EXPIRED', 'TERMINATED');
CREATE TYPE maintenance_type AS ENUM ('REVIEW', 'UPDATE', 'RENEWAL', 'TERMINATION', 'AMENDMENT', 'AUDIT');
CREATE TYPE maintenance_status AS ENUM ('SCHEDULED', 'IN_PROGRESS', 'COMPLETED', 'CANCELLED');
CREATE TYPE event_type AS ENUM ('DEADLINE', 'REMINDER', 'CANCELLATION', 'RENEWAL', 'REVIEW', 'MEETING');
CREATE TYPE event_status AS ENUM ('SCHEDULED', 'COMPLETED', 'CANCELLED');
CREATE TYPE priority_level AS ENUM ('LOW', 'MEDIUM', 'HIGH', 'CRITICAL');

-- Rahmenverträge Tabelle
CREATE TABLE framework_contracts (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    framework_number VARCHAR(50) UNIQUE NOT NULL,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    partner_name VARCHAR(255) NOT NULL,
    contract_type framework_contract_type NOT NULL,
    start_date DATE,
    end_date DATE,
    total_volume DECIMAL(15,2),
    currency VARCHAR(3) DEFAULT 'EUR',
    department VARCHAR(100),
    status framework_status DEFAULT 'DRAFT',
    owner_user_id BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by BIGINT,
    updated_by BIGINT
);

CREATE INDEX idx_framework_contracts_framework_number ON framework_contracts(framework_number);
CREATE INDEX idx_framework_contracts_partner ON framework_contracts(partner_name);
CREATE INDEX idx_framework_contracts_status ON framework_contracts(status);
CREATE INDEX idx_framework_contracts_end_date ON framework_contracts(end_date);

-- Verknüpfung von Einzelverträgen zu Rahmenverträgen
ALTER TABLE contracts 
ADD COLUMN framework_contract_id BIGINT,
ADD COLUMN is_framework_child BOOLEAN DEFAULT FALSE,
ADD CONSTRAINT fk_framework_contract 
    FOREIGN KEY (framework_contract_id) 
    REFERENCES framework_contracts(id) 
    ON DELETE SET NULL;

CREATE INDEX idx_contracts_framework ON contracts(framework_contract_id);

-- Vertragspflege Aktivitäten
CREATE TABLE contract_maintenance (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    contract_id BIGINT,
    framework_contract_id BIGINT,
    maintenance_type maintenance_type NOT NULL,
    scheduled_date DATE NOT NULL,
    completed_date DATE,
    status maintenance_status DEFAULT 'SCHEDULED',
    assigned_to BIGINT,
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by BIGINT,
    CONSTRAINT fk_maintenance_contract 
        FOREIGN KEY (contract_id) 
        REFERENCES contracts(id) 
        ON DELETE CASCADE,
    CONSTRAINT fk_maintenance_framework 
        FOREIGN KEY (framework_contract_id) 
        REFERENCES framework_contracts(id) 
        ON DELETE CASCADE
);

CREATE INDEX idx_contract_maintenance_contract ON contract_maintenance(contract_id);
CREATE INDEX idx_contract_maintenance_framework ON contract_maintenance(framework_contract_id);
CREATE INDEX idx_contract_maintenance_scheduled_date ON contract_maintenance(scheduled_date);
CREATE INDEX idx_contract_maintenance_status ON contract_maintenance(status);

-- Kalender-Events für Fristenmanagement
CREATE TABLE calendar_events (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    contract_id BIGINT,
    framework_contract_id BIGINT,
    event_type event_type NOT NULL,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    event_date DATE NOT NULL,
    event_time TIME,
    all_day BOOLEAN DEFAULT TRUE,
    reminder_days INT DEFAULT 0,
    status event_status DEFAULT 'SCHEDULED',
    priority priority_level DEFAULT 'MEDIUM',
    assigned_to BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_event_contract 
        FOREIGN KEY (contract_id) 
        REFERENCES contracts(id) 
        ON DELETE CASCADE,
    CONSTRAINT fk_event_framework 
        FOREIGN KEY (framework_contract_id) 
        REFERENCES framework_contracts(id) 
        ON DELETE CASCADE
);

CREATE INDEX idx_calendar_events_contract ON calendar_events(contract_id);
CREATE INDEX idx_calendar_events_framework ON calendar_events(framework_contract_id);
CREATE INDEX idx_calendar_events_event_date ON calendar_events(event_date);
CREATE INDEX idx_calendar_events_event_type ON calendar_events(event_type);
CREATE INDEX idx_calendar_events_status ON calendar_events(status);

-- Vertragspflege Checklisten
CREATE TABLE maintenance_checklists (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    maintenance_id BIGINT NOT NULL,
    item_title VARCHAR(255) NOT NULL,
    item_description TEXT,
    is_completed BOOLEAN DEFAULT FALSE,
    completed_at TIMESTAMP,
    completed_by BIGINT,
    sort_order INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_checklist_maintenance 
        FOREIGN KEY (maintenance_id) 
        REFERENCES contract_maintenance(id) 
        ON DELETE CASCADE
);

CREATE INDEX idx_maintenance_checklists_maintenance ON maintenance_checklists(maintenance_id);

-- Rahmenvertrag Volumen Tracking
CREATE TABLE framework_volume_tracking (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    framework_contract_id BIGINT NOT NULL,
    contract_id BIGINT,
    volume_used DECIMAL(15,2) NOT NULL,
    volume_date DATE NOT NULL,
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_volume_framework 
        FOREIGN KEY (framework_contract_id) 
        REFERENCES framework_contracts(id) 
        ON DELETE CASCADE,
    CONSTRAINT fk_volume_contract 
        FOREIGN KEY (contract_id) 
        REFERENCES contracts(id) 
        ON DELETE CASCADE
);

CREATE INDEX idx_framework_volume_tracking_framework ON framework_volume_tracking(framework_contract_id);
CREATE INDEX idx_framework_volume_tracking_contract ON framework_volume_tracking(contract_id);
