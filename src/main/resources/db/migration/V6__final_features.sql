-- V6: E-Mail-System, Kommunikationsschritte, Zahlungsverfolgung, Export-Funktionen

-- 1. E-Mail-System
CREATE TABLE IF NOT EXISTS email_templates (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    subject VARCHAR(255) NOT NULL,
    body TEXT NOT NULL,
    template_type VARCHAR(50) NOT NULL,
    variables TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_email_template_name (name)
)  ;

CREATE TABLE IF NOT EXISTS email_queue (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    recipient_email VARCHAR(255) NOT NULL,
    recipient_name VARCHAR(255),
    subject VARCHAR(255) NOT NULL,
    body TEXT NOT NULL,
    template_id BIGINT,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    priority INT DEFAULT 5,
    scheduled_at TIMESTAMP NULL,
    sent_at TIMESTAMP NULL,
    error_message TEXT,
    retry_count INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (template_id) REFERENCES email_templates(id),
    INDEX idx_status (status),
    INDEX idx_scheduled_at (scheduled_at)
)  ;

CREATE TABLE IF NOT EXISTS email_settings (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    smtp_host VARCHAR(255) NOT NULL,
    smtp_port INT NOT NULL,
    smtp_username VARCHAR(255),
    smtp_password VARCHAR(255),
    smtp_use_tls BOOLEAN DEFAULT TRUE,
    from_email VARCHAR(255) NOT NULL,
    from_name VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
)  ;

-- 2. Kommunikationsschritte
CREATE TABLE IF NOT EXISTS communication_steps (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    contract_id BIGINT NOT NULL,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    step_type VARCHAR(50) NOT NULL,
    interval_type VARCHAR(20),
    interval_value INT,
    next_execution TIMESTAMP NULL,
    last_execution TIMESTAMP NULL,
    template_id BIGINT,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (contract_id) REFERENCES contracts(id) ON DELETE CASCADE,
    FOREIGN KEY (template_id) REFERENCES email_templates(id),
    INDEX idx_next_execution (next_execution),
    INDEX idx_is_active (is_active)
)  ;

CREATE TABLE IF NOT EXISTS communication_step_participants (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    communication_step_id BIGINT NOT NULL,
    participant_email VARCHAR(255) NOT NULL,
    participant_name VARCHAR(255),
    participant_type VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (communication_step_id) REFERENCES communication_steps(id) ON DELETE CASCADE,
    INDEX idx_comm_step (communication_step_id)
)  ;

CREATE TABLE IF NOT EXISTS communication_history (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    communication_step_id BIGINT NOT NULL,
    executed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(20) NOT NULL,
    recipients_count INT,
    error_message TEXT,
    FOREIGN KEY (communication_step_id) REFERENCES communication_steps(id) ON DELETE CASCADE,
    INDEX idx_executed_at (executed_at)
)  ;

-- 3. Zahlungsverfolgung
CREATE TABLE IF NOT EXISTS payments (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    contract_id BIGINT NOT NULL,
    payment_type VARCHAR(50) NOT NULL,
    amount DECIMAL(15,2) NOT NULL,
    currency VARCHAR(3) DEFAULT 'EUR',
    due_date DATE NOT NULL,
    payment_date DATE,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    description TEXT,
    invoice_number VARCHAR(100),
    department VARCHAR(100),
    cost_center VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (contract_id) REFERENCES contracts(id) ON DELETE CASCADE,
    INDEX idx_due_date (due_date),
    INDEX idx_status (status),
    INDEX idx_department (department)
)  ;

CREATE TABLE IF NOT EXISTS payment_reminders (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    payment_id BIGINT NOT NULL,
    reminder_date DATE NOT NULL,
    reminder_type VARCHAR(50),
    sent BOOLEAN DEFAULT FALSE,
    sent_at TIMESTAMP NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (payment_id) REFERENCES payments(id) ON DELETE CASCADE,
    INDEX idx_reminder_date (reminder_date),
    INDEX idx_sent (sent)
)  ;

-- 4. Export-Funktionen
CREATE TABLE IF NOT EXISTS export_jobs (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    export_type VARCHAR(50) NOT NULL,
    format VARCHAR(10) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    file_path VARCHAR(500),
    parameters TEXT,
    created_by VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    completed_at TIMESTAMP NULL,
    error_message TEXT,
    INDEX idx_status (status),
    INDEX idx_created_at (created_at)
)  ;

-- Standard E-Mail-Vorlagen einfügen
INSERT INTO email_templates (name, subject, body, template_type, variables) VALUES
('contract_expiry_reminder', 'Vertrag läuft bald ab: {{contractTitle}}', 
'Sehr geehrte Damen und Herren,\n\nder Vertrag "{{contractTitle}}" (Vertragsnummer: {{contractNumber}}) läuft am {{expiryDate}} ab.\n\nBitte prüfen Sie, ob eine Verlängerung erforderlich ist.\n\nMit freundlichen Grüßen\nIhr eContract KI Team', 
'REMINDER', 'contractTitle,contractNumber,expiryDate'),

('contract_approval_request', 'Genehmigung erforderlich: {{contractTitle}}', 
'Sehr geehrte Damen und Herren,\n\nder Vertrag "{{contractTitle}}" benötigt Ihre Genehmigung.\n\nBitte prüfen Sie den Vertrag und genehmigen oder lehnen Sie ihn ab.\n\nMit freundlichen Grüßen\nIhr eContract KI Team', 
'APPROVAL', 'contractTitle,contractNumber'),

('payment_reminder', 'Zahlungserinnerung: {{invoiceNumber}}', 
'Sehr geehrte Damen und Herren,\n\ndie Zahlung für Rechnung {{invoiceNumber}} über {{amount}} EUR ist am {{dueDate}} fällig.\n\nBitte veranlassen Sie die Zahlung.\n\nMit freundlichen Grüßen\nIhr eContract KI Team', 
'PAYMENT', 'invoiceNumber,amount,dueDate');

-- Standard E-Mail-Einstellungen (Platzhalter)
INSERT INTO email_settings (smtp_host, smtp_port, smtp_username, from_email, from_name) VALUES
('smtp.example.com', 587, 'noreply@example.com', 'noreply@example.com', 'eContract KI');

