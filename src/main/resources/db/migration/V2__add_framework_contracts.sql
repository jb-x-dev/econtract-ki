-- Migration V2: Rahmenverträge und Vertragspflege

-- Rahmenverträge Tabelle
CREATE TABLE framework_contracts (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    framework_number VARCHAR(50) UNIQUE NOT NULL,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    partner_name VARCHAR(255) NOT NULL,
    contract_type ENUM('SUPPLIER', 'CUSTOMER', 'SERVICE', 'NDA', 'EMPLOYMENT') NOT NULL,
    start_date DATE,
    end_date DATE,
    total_volume DECIMAL(15,2),
    currency VARCHAR(3) DEFAULT 'EUR',
    department VARCHAR(100),
    status ENUM('DRAFT', 'IN_APPROVAL', 'ACTIVE', 'EXPIRED', 'TERMINATED') DEFAULT 'DRAFT',
    owner_user_id BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by BIGINT,
    updated_by BIGINT,
    INDEX idx_framework_number (framework_number),
    INDEX idx_partner (partner_name),
    INDEX idx_status (status),
    INDEX idx_end_date (end_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Verknüpfung von Einzelverträgen zu Rahmenverträgen
ALTER TABLE contracts 
ADD COLUMN framework_contract_id BIGINT,
ADD COLUMN is_framework_child BOOLEAN DEFAULT FALSE,
ADD INDEX idx_framework (framework_contract_id),
ADD CONSTRAINT fk_framework_contract 
    FOREIGN KEY (framework_contract_id) 
    REFERENCES framework_contracts(id) 
    ON DELETE SET NULL;

-- Vertragspflege Aktivitäten
CREATE TABLE contract_maintenance (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    contract_id BIGINT,
    framework_contract_id BIGINT,
    maintenance_type ENUM('REVIEW', 'UPDATE', 'RENEWAL', 'TERMINATION', 'AMENDMENT', 'AUDIT') NOT NULL,
    scheduled_date DATE NOT NULL,
    completed_date DATE,
    status ENUM('SCHEDULED', 'IN_PROGRESS', 'COMPLETED', 'CANCELLED') DEFAULT 'SCHEDULED',
    assigned_to BIGINT,
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by BIGINT,
    INDEX idx_contract (contract_id),
    INDEX idx_framework (framework_contract_id),
    INDEX idx_scheduled_date (scheduled_date),
    INDEX idx_status (status),
    CONSTRAINT fk_maintenance_contract 
        FOREIGN KEY (contract_id) 
        REFERENCES contracts(id) 
        ON DELETE CASCADE,
    CONSTRAINT fk_maintenance_framework 
        FOREIGN KEY (framework_contract_id) 
        REFERENCES framework_contracts(id) 
        ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Kalender-Events für Fristenmanagement
CREATE TABLE calendar_events (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    contract_id BIGINT,
    framework_contract_id BIGINT,
    event_type ENUM('DEADLINE', 'REMINDER', 'CANCELLATION', 'RENEWAL', 'REVIEW', 'MEETING') NOT NULL,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    event_date DATE NOT NULL,
    event_time TIME,
    all_day BOOLEAN DEFAULT TRUE,
    reminder_days INT DEFAULT 0,
    status ENUM('SCHEDULED', 'COMPLETED', 'CANCELLED') DEFAULT 'SCHEDULED',
    priority ENUM('LOW', 'MEDIUM', 'HIGH', 'CRITICAL') DEFAULT 'MEDIUM',
    assigned_to BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_contract (contract_id),
    INDEX idx_framework (framework_contract_id),
    INDEX idx_event_date (event_date),
    INDEX idx_event_type (event_type),
    INDEX idx_status (status),
    CONSTRAINT fk_event_contract 
        FOREIGN KEY (contract_id) 
        REFERENCES contracts(id) 
        ON DELETE CASCADE,
    CONSTRAINT fk_event_framework 
        FOREIGN KEY (framework_contract_id) 
        REFERENCES framework_contracts(id) 
        ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Vertragspflege Checklisten
CREATE TABLE maintenance_checklists (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    maintenance_id BIGINT NOT NULL,
    item_title VARCHAR(255) NOT NULL,
    item_description TEXT,
    is_completed BOOLEAN DEFAULT FALSE,
    completed_at TIMESTAMP,
    completed_by BIGINT,
    sort_order INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_maintenance (maintenance_id),
    CONSTRAINT fk_checklist_maintenance 
        FOREIGN KEY (maintenance_id) 
        REFERENCES contract_maintenance(id) 
        ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Rahmenvertrag Volumen Tracking
CREATE TABLE framework_volume_tracking (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    framework_contract_id BIGINT NOT NULL,
    contract_id BIGINT,
    volume_used DECIMAL(15,2) NOT NULL,
    volume_date DATE NOT NULL,
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_framework (framework_contract_id),
    INDEX idx_contract (contract_id),
    CONSTRAINT fk_volume_framework 
        FOREIGN KEY (framework_contract_id) 
        REFERENCES framework_contracts(id) 
        ON DELETE CASCADE,
    CONSTRAINT fk_volume_contract 
        FOREIGN KEY (contract_id) 
        REFERENCES contracts(id) 
        ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

