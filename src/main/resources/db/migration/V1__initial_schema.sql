-- eContract KI - Initial Database Schema
-- Version: 1.0.0
-- Date: 2025-10-27
-- PostgreSQL Compatible

-- Create ENUM types first
CREATE TYPE contract_status AS ENUM ('DRAFT', 'IN_NEGOTIATION', 'IN_APPROVAL', 'APPROVED', 'ACTIVE', 'EXPIRED', 'TERMINATED');
CREATE TYPE risk_level AS ENUM ('LOW', 'MEDIUM', 'HIGH');
CREATE TYPE approval_status AS ENUM ('PENDING', 'APPROVED', 'REJECTED', 'SKIPPED');
CREATE TYPE deadline_type AS ENUM ('NOTICE', 'RENEWAL', 'MILESTONE', 'PAYMENT', 'CUSTOM');
CREATE TYPE user_role AS ENUM ('USER', 'MANAGER', 'ADMIN');

-- Verträge (Haupttabelle)
CREATE TABLE contracts (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    contract_number VARCHAR(50) UNIQUE NOT NULL,
    title VARCHAR(500) NOT NULL,
    contract_type VARCHAR(100) NOT NULL,
    status contract_status NOT NULL DEFAULT 'DRAFT',
    partner_name VARCHAR(255) NOT NULL,
    partner_id BIGINT,
    start_date DATE,
    end_date DATE,
    notice_period_days INT,
    auto_renewal BOOLEAN DEFAULT FALSE,
    contract_value DECIMAL(15,2),
    currency VARCHAR(3) DEFAULT 'EUR',
    department VARCHAR(100),
    owner_user_id BIGINT NOT NULL,
    created_by BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_contracts_status ON contracts(status);
CREATE INDEX idx_contracts_end_date ON contracts(end_date);
CREATE INDEX idx_contracts_contract_type ON contracts(contract_type);
CREATE INDEX idx_contracts_owner ON contracts(owner_user_id);
CREATE INDEX idx_contracts_created_at ON contracts(created_at);

-- Vertragsversionen
CREATE TABLE contract_versions (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    contract_id BIGINT NOT NULL,
    version_number INT NOT NULL,
    content TEXT,
    content_html TEXT,
    file_path VARCHAR(500),
    file_size BIGINT,
    mime_type VARCHAR(100),
    created_by BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    comment TEXT,
    FOREIGN KEY (contract_id) REFERENCES contracts(id) ON DELETE CASCADE,
    UNIQUE (contract_id, version_number)
);

CREATE INDEX idx_contract_versions_contract_version ON contract_versions(contract_id, version_number);

-- Vertragsvorlagen
CREATE TABLE contract_templates (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    contract_type VARCHAR(100) NOT NULL,
    content TEXT NOT NULL,
    variables JSON,
    is_active BOOLEAN DEFAULT TRUE,
    version INT DEFAULT 1,
    created_by BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_contract_templates_type_active ON contract_templates(contract_type, is_active);

-- Klauselbibliothek
CREATE TABLE contract_clauses (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    category VARCHAR(100) NOT NULL,
    content TEXT NOT NULL,
    description TEXT,
    risk_level risk_level DEFAULT 'LOW',
    is_mandatory BOOLEAN DEFAULT FALSE,
    applicable_contract_types JSON,
    created_by BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_contract_clauses_category ON contract_clauses(category);
CREATE INDEX idx_contract_clauses_risk_level ON contract_clauses(risk_level);

-- Genehmigungsworkflows
CREATE TABLE approval_workflows (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    contract_type VARCHAR(100),
    trigger_conditions JSON,
    workflow_definition JSON NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_approval_workflows_type_active ON approval_workflows(contract_type, is_active);

-- Genehmigungsschritte (Instanzen)
CREATE TABLE approval_steps (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    contract_id BIGINT NOT NULL,
    workflow_id BIGINT NOT NULL,
    step_number INT NOT NULL,
    step_name VARCHAR(255) NOT NULL,
    approver_user_id BIGINT,
    approver_role VARCHAR(100),
    status approval_status DEFAULT 'PENDING',
    due_date TIMESTAMP,
    approved_at TIMESTAMP NULL,
    comment TEXT,
    FOREIGN KEY (contract_id) REFERENCES contracts(id) ON DELETE CASCADE,
    FOREIGN KEY (workflow_id) REFERENCES approval_workflows(id)
);

CREATE INDEX idx_approval_steps_contract_status ON approval_steps(contract_id, status);
CREATE INDEX idx_approval_steps_approver_status ON approval_steps(approver_user_id, status);
CREATE INDEX idx_approval_steps_due_date ON approval_steps(due_date);

-- Fristen und Termine
CREATE TABLE contract_deadlines (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    contract_id BIGINT NOT NULL,
    deadline_type deadline_type NOT NULL,
    deadline_date DATE NOT NULL,
    description VARCHAR(500),
    notification_days_before INT DEFAULT 30,
    is_completed BOOLEAN DEFAULT FALSE,
    completed_at TIMESTAMP NULL,
    FOREIGN KEY (contract_id) REFERENCES contracts(id) ON DELETE CASCADE
);

CREATE INDEX idx_contract_deadlines_deadline_date ON contract_deadlines(deadline_date, is_completed);
CREATE INDEX idx_contract_deadlines_contract_type ON contract_deadlines(contract_id, deadline_type);

-- Audit Trail
CREATE TABLE contract_audit_log (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    contract_id BIGINT NOT NULL,
    action VARCHAR(100) NOT NULL,
    entity_type VARCHAR(100),
    entity_id BIGINT,
    user_id BIGINT NOT NULL,
    user_name VARCHAR(255),
    old_value TEXT,
    new_value TEXT,
    ip_address VARCHAR(45),
    user_agent VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (contract_id) REFERENCES contracts(id) ON DELETE CASCADE
);

CREATE INDEX idx_contract_audit_log_contract_date ON contract_audit_log(contract_id, created_at);
CREATE INDEX idx_contract_audit_log_user_date ON contract_audit_log(user_id, created_at);
CREATE INDEX idx_contract_audit_log_action ON contract_audit_log(action);

-- Benachrichtigungen
CREATE TABLE notifications (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    user_id BIGINT NOT NULL,
    contract_id BIGINT,
    type VARCHAR(100) NOT NULL,
    title VARCHAR(255) NOT NULL,
    message TEXT,
    is_read BOOLEAN DEFAULT FALSE,
    read_at TIMESTAMP NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_notifications_user_read ON notifications(user_id, is_read, created_at);
CREATE INDEX idx_notifications_contract ON notifications(contract_id);

-- Dokumente und Anhänge
CREATE TABLE contract_attachments (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    contract_id BIGINT NOT NULL,
    file_name VARCHAR(500) NOT NULL,
    file_path VARCHAR(1000) NOT NULL,
    file_size BIGINT,
    mime_type VARCHAR(100),
    category VARCHAR(100),
    uploaded_by BIGINT NOT NULL,
    uploaded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (contract_id) REFERENCES contracts(id) ON DELETE CASCADE
);

CREATE INDEX idx_contract_attachments_contract ON contract_attachments(contract_id);
CREATE INDEX idx_contract_attachments_category ON contract_attachments(category);

-- KI-Analysen
CREATE TABLE contract_ai_analysis (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    contract_id BIGINT NOT NULL,
    analysis_type VARCHAR(100) NOT NULL,
    result JSON NOT NULL,
    confidence_score DECIMAL(5,2),
    analyzed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (contract_id) REFERENCES contracts(id) ON DELETE CASCADE
);

CREATE INDEX idx_contract_ai_analysis_contract_type ON contract_ai_analysis(contract_id, analysis_type);

-- Benutzer (vereinfachte Version für Standalone-Betrieb)
CREATE TABLE users (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    username VARCHAR(100) UNIQUE NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    full_name VARCHAR(255),
    role user_role DEFAULT 'USER',
    department VARCHAR(100),
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_login TIMESTAMP NULL
);

CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_role ON users(role);

-- Initial Admin User (Passwort: admin123)
INSERT INTO users (username, email, password_hash, full_name, role, department) 
VALUES ('admin', 'admin@jb-x.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'System Administrator', 'ADMIN', 'IT');
