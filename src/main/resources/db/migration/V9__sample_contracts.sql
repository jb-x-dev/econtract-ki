-- ============================================================================
-- eContract KI - Sample Contracts Migration
-- Version: V9
-- Description: Insert 100 realistic sample contracts for demonstration
-- Author: jb-x Development Team
-- Date: 2025-11-08
-- PostgreSQL Compatible
-- ============================================================================

-- Note: All contracts are assigned to admin user (id=1)

INSERT INTO contracts (contract_number, title, contract_type, status, partner_name, 
    start_date, end_date, notice_period_days, auto_renewal, contract_value, currency, 
    department, owner_user_id, created_by, created_at, updated_at) 
VALUES ('VTR-2024-0001', 'NDA mit Scrum Masters GmbH', 'NDA', 'APPROVED', 'Scrum Masters GmbH', 
    '2024-08-20', '2025-11-04', 30, TRUE, 154103.00, 'EUR', 
    'HR', 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO contracts (contract_number, title, contract_type, status, partner_name, 
    start_date, end_date, notice_period_days, auto_renewal, contract_value, currency, 
    department, owner_user_id, created_by, created_at, updated_at) 
VALUES ('VTR-2024-0002', 'Projektvertrag mit API Gateway Services', 'Projektvertrag', 'DRAFT', 'API Gateway Services', 
    '2024-05-19', '2026-12-04', 60, TRUE, 217191.00, 'EUR', 
    'Einkauf', 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO contracts (contract_number, title, contract_type, status, partner_name, 
    start_date, end_date, notice_period_days, auto_renewal, contract_value, currency, 
    department, owner_user_id, created_by, created_at, updated_at) 
VALUES ('VTR-2024-0003', 'Projektvertrag mit Container Solutions', 'Projektvertrag', 'APPROVED', 'Container Solutions', 
    '2024-08-27', '2027-03-15', 60, TRUE, 433068.00, 'EUR', 
    'Finanzen', 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO contracts (contract_number, title, contract_type, status, partner_name, 
    start_date, end_date, notice_period_days, auto_renewal, contract_value, currency, 
    department, owner_user_id, created_by, created_at, updated_at) 
VALUES ('VTR-2024-0004', 'Projektvertrag mit Future Tech GmbH', 'Projektvertrag', 'ACTIVE', 'Future Tech GmbH', 
    '2024-06-28', '2025-05-01', 90, TRUE, 403814.00, 'EUR', 
    'Einkauf', 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO contracts (contract_number, title, contract_type, status, partner_name, 
    start_date, end_date, notice_period_days, auto_renewal, contract_value, currency, 
    department, owner_user_id, created_by, created_at, updated_at) 
VALUES ('VTR-2024-0005', 'Lieferantenvertrag mit System Integration', 'Lieferantenvertrag', 'IN_APPROVAL', 'System Integration', 
    '2024-01-09', '2024-10-01', 60, TRUE, 378449.00, 'EUR', 
    'Marketing', 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO contracts (contract_number, title, contract_type, status, partner_name, 
    start_date, end_date, notice_period_days, auto_renewal, contract_value, currency, 
    department, owner_user_id, created_by, created_at, updated_at) 
VALUES ('VTR-2024-0006', 'Mietvertrag mit Agile Coaching', 'Mietvertrag', 'EXPIRED', 'Agile Coaching', 
    '2024-02-16', '2026-04-18', 30, FALSE, 346770.00, 'EUR', 
    'Produktion', 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO contracts (contract_number, title, contract_type, status, partner_name, 
    start_date, end_date, notice_period_days, auto_renewal, contract_value, currency, 
    department, owner_user_id, created_by, created_at, updated_at) 
VALUES ('VTR-2024-0007', 'Wartungsvertrag mit Enterprise Solutions', 'Wartungsvertrag', 'APPROVED', 'Enterprise Solutions', 
    '2024-10-11', '2025-09-05', 90, FALSE, 285041.00, 'EUR', 
    'Produktion', 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO contracts (contract_number, title, contract_type, status, partner_name, 
    start_date, end_date, notice_period_days, auto_renewal, contract_value, currency, 
    department, owner_user_id, created_by, created_at, updated_at) 
VALUES ('VTR-2024-0008', 'Wartungsvertrag mit Quantum Computing', 'Wartungsvertrag', 'APPROVED', 'Quantum Computing', 
    '2024-01-01', '2026-04-22', 60, TRUE, 194148.00, 'EUR', 
    'Produktion', 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO contracts (contract_number, title, contract_type, status, partner_name, 
    start_date, end_date, notice_period_days, auto_renewal, contract_value, currency, 
    department, owner_user_id, created_by, created_at, updated_at) 
VALUES ('VTR-2024-0009', 'Lieferantenvertrag mit Backup Solutions', 'Lieferantenvertrag', 'IN_APPROVAL', 'Backup Solutions', 
    '2024-09-12', '2026-11-17', 60, FALSE, 122997.00, 'EUR', 
    'IT', 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO contracts (contract_number, title, contract_type, status, partner_name, 
    start_date, end_date, notice_period_days, auto_renewal, contract_value, currency, 
    department, owner_user_id, created_by, created_at, updated_at) 
VALUES ('VTR-2024-0010', 'Dienstleistungsvertrag mit Quality Assurance AG', 'Dienstleistungsvertrag', 'APPROVED', 'Quality Assurance AG', 
    '2024-03-23', '2025-11-30', 30, FALSE, 338303.00, 'EUR', 
    'Einkauf', 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO contracts (contract_number, title, contract_type, status, partner_name, 
    start_date, end_date, notice_period_days, auto_renewal, contract_value, currency, 
    department, owner_user_id, created_by, created_at, updated_at) 
VALUES ('VTR-2024-0011', 'Lieferantenvertrag mit Frontend Experts', 'Lieferantenvertrag', 'IN_APPROVAL', 'Frontend Experts', 
    '2024-05-27', '2027-04-20', 90, FALSE, 178363.00, 'EUR', 
    'Einkauf', 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO contracts (contract_number, title, contract_type, status, partner_name, 
    start_date, end_date, notice_period_days, auto_renewal, contract_value, currency, 
    department, owner_user_id, created_by, created_at, updated_at) 
VALUES ('VTR-2024-0012', 'Rahmenvertrag mit Business Intelligence', 'Rahmenvertrag', 'DRAFT', 'Business Intelligence', 
    '2024-06-01', '2025-09-26', 60, TRUE, 370384.00, 'EUR', 
    'HR', 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO contracts (contract_number, title, contract_type, status, partner_name, 
    start_date, end_date, notice_period_days, auto_renewal, contract_value, currency, 
    department, owner_user_id, created_by, created_at, updated_at) 
VALUES ('VTR-2024-0013', 'Lizenzvertrag mit Mobile Apps AG', 'Lizenzvertrag', 'IN_NEGOTIATION', 'Mobile Apps AG', 
    '2024-01-30', '2025-06-22', 90, TRUE, 276117.00, 'EUR', 
    'Logistik', 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO contracts (contract_number, title, contract_type, status, partner_name, 
    start_date, end_date, notice_period_days, auto_renewal, contract_value, currency, 
    department, owner_user_id, created_by, created_at, updated_at) 
VALUES ('VTR-2024-0014', 'Rahmenvertrag mit API Gateway Services', 'Rahmenvertrag', 'IN_NEGOTIATION', 'API Gateway Services', 
    '2024-08-02', '2026-08-01', 60, TRUE, 351141.00, 'EUR', 
    'Finanzen', 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO contracts (contract_number, title, contract_type, status, partner_name, 
    start_date, end_date, notice_period_days, auto_renewal, contract_value, currency, 
    department, owner_user_id, created_by, created_at, updated_at) 
VALUES ('VTR-2024-0015', 'Beratungsvertrag mit DevOps Automation', 'Beratungsvertrag', 'TERMINATED', 'DevOps Automation', 
    '2024-01-11', '2026-11-11', 30, TRUE, 216013.00, 'EUR', 
    'Produktion', 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO contracts (contract_number, title, contract_type, status, partner_name, 
    start_date, end_date, notice_period_days, auto_renewal, contract_value, currency, 
    department, owner_user_id, created_by, created_at, updated_at) 
VALUES ('VTR-2024-0016', 'Beratungsvertrag mit Smart Business AG', 'Beratungsvertrag', 'DRAFT', 'Smart Business AG', 
    '2024-08-17', '2026-07-25', 90, FALSE, 81854.00, 'EUR', 
    'Marketing', 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO contracts (contract_number, title, contract_type, status, partner_name, 
    start_date, end_date, notice_period_days, auto_renewal, contract_value, currency, 
    department, owner_user_id, created_by, created_at, updated_at) 
VALUES ('VTR-2024-0017', 'NDA mit Backend Systems AG', 'NDA', 'IN_NEGOTIATION', 'Backend Systems AG', 
    '2024-09-07', '2026-12-26', 90, FALSE, 70954.00, 'EUR', 
    'Produktion', 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO contracts (contract_number, title, contract_type, status, partner_name, 
    start_date, end_date, notice_period_days, auto_renewal, contract_value, currency, 
    department, owner_user_id, created_by, created_at, updated_at) 
VALUES ('VTR-2024-0018', 'Rahmenvertrag mit IT Support Services', 'Rahmenvertrag', 'DRAFT', 'IT Support Services', 
    '2024-05-25', '2026-03-27', 30, FALSE, 99627.00, 'EUR', 
    'Produktion', 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO contracts (contract_number, title, contract_type, status, partner_name, 
    start_date, end_date, notice_period_days, auto_renewal, contract_value, currency, 
    department, owner_user_id, created_by, created_at, updated_at) 
VALUES ('VTR-2024-0019', 'Rahmenvertrag mit IT Support Services', 'Rahmenvertrag', 'IN_NEGOTIATION', 'IT Support Services', 
    '2024-01-31', '2025-02-07', 90, TRUE, 489691.00, 'EUR', 
    'HR', 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO contracts (contract_number, title, contract_type, status, partner_name, 
    start_date, end_date, notice_period_days, auto_renewal, contract_value, currency, 
    department, owner_user_id, created_by, created_at, updated_at) 
VALUES ('VTR-2024-0020', 'Beratungsvertrag mit Network Infrastructure', 'Beratungsvertrag', 'IN_NEGOTIATION', 'Network Infrastructure', 
    '2024-01-13', '2024-09-29', 60, TRUE, 55122.00, 'EUR', 
    'Logistik', 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO contracts (contract_number, title, contract_type, status, partner_name, 
    start_date, end_date, notice_period_days, auto_renewal, contract_value, currency, 
    department, owner_user_id, created_by, created_at, updated_at) 
VALUES ('VTR-2024-0021', 'NDA mit Container Solutions', 'NDA', 'TERMINATED', 'Container Solutions', 
    '2024-06-28', '2026-09-20', 30, TRUE, 187657.00, 'EUR', 
    'Produktion', 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO contracts (contract_number, title, contract_type, status, partner_name, 
    start_date, end_date, notice_period_days, auto_renewal, contract_value, currency, 
    department, owner_user_id, created_by, created_at, updated_at) 
VALUES ('VTR-2024-0022', 'Lizenzvertrag mit Business Intelligence', 'Lizenzvertrag', 'TERMINATED', 'Business Intelligence', 
    '2024-08-23', '2027-07-06', 60, TRUE, 338998.00, 'EUR', 
    'Logistik', 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO contracts (contract_number, title, contract_type, status, partner_name, 
    start_date, end_date, notice_period_days, auto_renewal, contract_value, currency, 
    department, owner_user_id, created_by, created_at, updated_at) 
VALUES ('VTR-2024-0023', 'Lieferantenvertrag mit UX Design Studio', 'Lieferantenvertrag', 'ACTIVE', 'UX Design Studio', 
    '2024-09-07', '2025-08-17', 90, FALSE, 53747.00, 'EUR', 
    'Einkauf', 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO contracts (contract_number, title, contract_type, status, partner_name, 
    start_date, end_date, notice_period_days, auto_renewal, contract_value, currency, 
    department, owner_user_id, created_by, created_at, updated_at) 
VALUES ('VTR-2024-0024', 'Beratungsvertrag mit Backend Systems AG', 'Beratungsvertrag', 'APPROVED', 'Backend Systems AG', 
    '2024-02-06', '2026-07-19', 30, TRUE, 96266.00, 'EUR', 
    'Logistik', 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO contracts (contract_number, title, contract_type, status, partner_name, 
    start_date, end_date, notice_period_days, auto_renewal, contract_value, currency, 
    department, owner_user_id, created_by, created_at, updated_at) 
VALUES ('VTR-2024-0025', 'Lizenzvertrag mit Big Data Analytics', 'Lizenzvertrag', 'IN_APPROVAL', 'Big Data Analytics', 
    '2024-05-15', '2027-02-02', 30, TRUE, 144808.00, 'EUR', 
    'Vertrieb', 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO contracts (contract_number, title, contract_type, status, partner_name, 
    start_date, end_date, notice_period_days, auto_renewal, contract_value, currency, 
    department, owner_user_id, created_by, created_at, updated_at) 
VALUES ('VTR-2024-0026', 'SLA mit Web Development Co', 'SLA', 'IN_APPROVAL', 'Web Development Co', 
    '2024-03-25', '2025-04-26', 90, FALSE, 496335.00, 'EUR', 
    'Einkauf', 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO contracts (contract_number, title, contract_type, status, partner_name, 
    start_date, end_date, notice_period_days, auto_renewal, contract_value, currency, 
    department, owner_user_id, created_by, created_at, updated_at) 
VALUES ('VTR-2024-0027', 'Projektvertrag mit Digital Solutions AG', 'Projektvertrag', 'TERMINATED', 'Digital Solutions AG', 
    '2024-07-30', '2027-07-24', 60, FALSE, 266059.00, 'EUR', 
    'Vertrieb', 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO contracts (contract_number, title, contract_type, status, partner_name, 
    start_date, end_date, notice_period_days, auto_renewal, contract_value, currency, 
    department, owner_user_id, created_by, created_at, updated_at) 
VALUES ('VTR-2024-0028', 'Projektvertrag mit Web Development Co', 'Projektvertrag', 'IN_APPROVAL', 'Web Development Co', 
    '2024-04-01', '2025-08-12', 60, FALSE, 190678.00, 'EUR', 
    'IT', 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO contracts (contract_number, title, contract_type, status, partner_name, 
    start_date, end_date, notice_period_days, auto_renewal, contract_value, currency, 
    department, owner_user_id, created_by, created_at, updated_at) 
VALUES ('VTR-2024-0029', 'Arbeitsvertrag mit Backup Solutions', 'Arbeitsvertrag', 'IN_APPROVAL', 'Backup Solutions', 
    '2024-10-16', '2026-02-20', 90, TRUE, 161881.00, 'EUR', 
    'Marketing', 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO contracts (contract_number, title, contract_type, status, partner_name, 
    start_date, end_date, notice_period_days, auto_renewal, contract_value, currency, 
    department, owner_user_id, created_by, created_at, updated_at) 
VALUES ('VTR-2024-0030', 'Kundenvertrag mit CI/CD Pipeline GmbH', 'Kundenvertrag', 'TERMINATED', 'CI/CD Pipeline GmbH', 
    '2024-10-08', '2026-09-21', 60, FALSE, 336061.00, 'EUR', 
    'Produktion', 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO contracts (contract_number, title, contract_type, status, partner_name, 
    start_date, end_date, notice_period_days, auto_renewal, contract_value, currency, 
    department, owner_user_id, created_by, created_at, updated_at) 
VALUES ('VTR-2024-0031', 'Arbeitsvertrag mit Security Auditing', 'Arbeitsvertrag', 'IN_NEGOTIATION', 'Security Auditing', 
    '2024-07-25', '2026-01-26', 60, TRUE, 147151.00, 'EUR', 
    'IT', 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO contracts (contract_number, title, contract_type, status, partner_name, 
    start_date, end_date, notice_period_days, auto_renewal, contract_value, currency, 
    department, owner_user_id, created_by, created_at, updated_at) 
VALUES ('VTR-2024-0032', 'Kundenvertrag mit Software Factory', 'Kundenvertrag', 'DRAFT', 'Software Factory', 
    '2024-04-02', '2025-12-07', 90, FALSE, 357987.00, 'EUR', 
    'Vertrieb', 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO contracts (contract_number, title, contract_type, status, partner_name, 
    start_date, end_date, notice_period_days, auto_renewal, contract_value, currency, 
    department, owner_user_id, created_by, created_at, updated_at) 
VALUES ('VTR-2024-0033', 'Lizenzvertrag mit Disaster Recovery Ltd', 'Lizenzvertrag', 'APPROVED', 'Disaster Recovery Ltd', 
    '2024-03-24', '2026-07-06', 60, FALSE, 319385.00, 'EUR', 
    'Finanzen', 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO contracts (contract_number, title, contract_type, status, partner_name, 
    start_date, end_date, notice_period_days, auto_renewal, contract_value, currency, 
    department, owner_user_id, created_by, created_at, updated_at) 
VALUES ('VTR-2024-0034', 'Mietvertrag mit Mobile Apps AG', 'Mietvertrag', 'DRAFT', 'Mobile Apps AG', 
    '2024-01-20', '2024-10-29', 90, FALSE, 494265.00, 'EUR', 
    'HR', 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO contracts (contract_number, title, contract_type, status, partner_name, 
    start_date, end_date, notice_period_days, auto_renewal, contract_value, currency, 
    department, owner_user_id, created_by, created_at, updated_at) 
VALUES ('VTR-2024-0035', 'Beratungsvertrag mit UI Development', 'Beratungsvertrag', 'TERMINATED', 'UI Development', 
    '2024-08-28', '2027-04-01', 30, FALSE, 269452.00, 'EUR', 
    'Produktion', 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO contracts (contract_number, title, contract_type, status, partner_name, 
    start_date, end_date, notice_period_days, auto_renewal, contract_value, currency, 
    department, owner_user_id, created_by, created_at, updated_at) 
VALUES ('VTR-2024-0036', 'Lieferantenvertrag mit UI Development', 'Lieferantenvertrag', 'ACTIVE', 'UI Development', 
    '2024-04-05', '2026-09-04', 30, FALSE, 244048.00, 'EUR', 
    'Vertrieb', 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO contracts (contract_number, title, contract_type, status, partner_name, 
    start_date, end_date, notice_period_days, auto_renewal, contract_value, currency, 
    department, owner_user_id, created_by, created_at, updated_at) 
VALUES ('VTR-2024-0037', 'Wartungsvertrag mit CI/CD Pipeline GmbH', 'Wartungsvertrag', 'IN_APPROVAL', 'CI/CD Pipeline GmbH', 
    '2024-09-11', '2025-11-27', 60, FALSE, 238929.00, 'EUR', 
    'Produktion', 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO contracts (contract_number, title, contract_type, status, partner_name, 
    start_date, end_date, notice_period_days, auto_renewal, contract_value, currency, 
    department, owner_user_id, created_by, created_at, updated_at) 
VALUES ('VTR-2024-0038', 'SLA mit Product Management', 'SLA', 'IN_APPROVAL', 'Product Management', 
    '2024-02-20', '2025-03-07', 90, FALSE, 362645.00, 'EUR', 
    'Produktion', 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO contracts (contract_number, title, contract_type, status, partner_name, 
    start_date, end_date, notice_period_days, auto_renewal, contract_value, currency, 
    department, owner_user_id, created_by, created_at, updated_at) 
VALUES ('VTR-2024-0039', 'Wartungsvertrag mit Blockchain Ventures', 'Wartungsvertrag', 'ACTIVE', 'Blockchain Ventures', 
    '2024-02-04', '2025-01-11', 90, TRUE, 249327.00, 'EUR', 
    'IT', 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO contracts (contract_number, title, contract_type, status, partner_name, 
    start_date, end_date, notice_period_days, auto_renewal, contract_value, currency, 
    department, owner_user_id, created_by, created_at, updated_at) 
VALUES ('VTR-2024-0040', 'SLA mit System Integration', 'SLA', 'IN_NEGOTIATION', 'System Integration', 
    '2024-10-16', '2025-05-17', 90, FALSE, 379990.00, 'EUR', 
    'Finanzen', 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO contracts (contract_number, title, contract_type, status, partner_name, 
    start_date, end_date, notice_period_days, auto_renewal, contract_value, currency, 
    department, owner_user_id, created_by, created_at, updated_at) 
VALUES ('VTR-2024-0041', 'Lizenzvertrag mit Help Desk Pro', 'Lizenzvertrag', 'APPROVED', 'Help Desk Pro', 
    '2024-03-22', '2027-01-27', 90, TRUE, 283949.00, 'EUR', 
    'Logistik', 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO contracts (contract_number, title, contract_type, status, partner_name, 
    start_date, end_date, notice_period_days, auto_renewal, contract_value, currency, 
    department, owner_user_id, created_by, created_at, updated_at) 
VALUES ('VTR-2024-0042', 'Projektvertrag mit Product Management', 'Projektvertrag', 'APPROVED', 'Product Management', 
    '2024-07-05', '2025-01-27', 30, TRUE, 474371.00, 'EUR', 
    'Logistik', 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO contracts (contract_number, title, contract_type, status, partner_name, 
    start_date, end_date, notice_period_days, auto_renewal, contract_value, currency, 
    department, owner_user_id, created_by, created_at, updated_at) 
VALUES ('VTR-2024-0043', 'Arbeitsvertrag mit Future Tech GmbH', 'Arbeitsvertrag', 'IN_NEGOTIATION', 'Future Tech GmbH', 
    '2024-02-13', '2026-01-27', 90, FALSE, 213046.00, 'EUR', 
    'Finanzen', 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO contracts (contract_number, title, contract_type, status, partner_name, 
    start_date, end_date, notice_period_days, auto_renewal, contract_value, currency, 
    department, owner_user_id, created_by, created_at, updated_at) 
VALUES ('VTR-2024-0044', 'Wartungsvertrag mit Container Solutions', 'Wartungsvertrag', 'TERMINATED', 'Container Solutions', 
    '2024-07-20', '2025-12-03', 90, FALSE, 402493.00, 'EUR', 
    'Logistik', 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO contracts (contract_number, title, contract_type, status, partner_name, 
    start_date, end_date, notice_period_days, auto_renewal, contract_value, currency, 
    department, owner_user_id, created_by, created_at, updated_at) 
VALUES ('VTR-2024-0045', 'Kundenvertrag mit Docker Consulting', 'Kundenvertrag', 'EXPIRED', 'Docker Consulting', 
    '2024-09-02', '2025-04-19', 60, TRUE, 267592.00, 'EUR', 
    'Marketing', 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO contracts (contract_number, title, contract_type, status, partner_name, 
    start_date, end_date, notice_period_days, auto_renewal, contract_value, currency, 
    department, owner_user_id, created_by, created_at, updated_at) 
VALUES ('VTR-2024-0046', 'Lieferantenvertrag mit CI/CD Pipeline GmbH', 'Lieferantenvertrag', 'TERMINATED', 'CI/CD Pipeline GmbH', 
    '2024-04-09', '2025-10-10', 30, TRUE, 375454.00, 'EUR', 
    'Logistik', 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO contracts (contract_number, title, contract_type, status, partner_name, 
    start_date, end_date, notice_period_days, auto_renewal, contract_value, currency, 
    department, owner_user_id, created_by, created_at, updated_at) 
VALUES ('VTR-2024-0047', 'Rahmenvertrag mit CI/CD Pipeline GmbH', 'Rahmenvertrag', 'EXPIRED', 'CI/CD Pipeline GmbH', 
    '2024-04-09', '2024-11-29', 60, TRUE, 25302.00, 'EUR', 
    'Produktion', 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO contracts (contract_number, title, contract_type, status, partner_name, 
    start_date, end_date, notice_period_days, auto_renewal, contract_value, currency, 
    department, owner_user_id, created_by, created_at, updated_at) 
VALUES ('VTR-2024-0048', 'Lieferantenvertrag mit Disaster Recovery Ltd', 'Lieferantenvertrag', 'APPROVED', 'Disaster Recovery Ltd', 
    '2024-03-20', '2025-05-26', 60, TRUE, 227730.00, 'EUR', 
    'Logistik', 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO contracts (contract_number, title, contract_type, status, partner_name, 
    start_date, end_date, notice_period_days, auto_renewal, contract_value, currency, 
    department, owner_user_id, created_by, created_at, updated_at) 
VALUES ('VTR-2024-0049', 'Lieferantenvertrag mit Kubernetes Experts', 'Lieferantenvertrag', 'IN_NEGOTIATION', 'Kubernetes Experts', 
    '2024-02-20', '2026-07-15', 90, TRUE, 48259.00, 'EUR', 
    'Produktion', 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO contracts (contract_number, title, contract_type, status, partner_name, 
    start_date, end_date, notice_period_days, auto_renewal, contract_value, currency, 
    department, owner_user_id, created_by, created_at, updated_at) 
VALUES ('VTR-2024-0050', 'Dienstleistungsvertrag mit Agile Coaching', 'Dienstleistungsvertrag', 'ACTIVE', 'Agile Coaching', 
    '2024-05-28', '2027-02-06', 30, FALSE, 491947.00, 'EUR', 
    'IT', 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO contracts (contract_number, title, contract_type, status, partner_name, 
    start_date, end_date, notice_period_days, auto_renewal, contract_value, currency, 
    department, owner_user_id, created_by, created_at, updated_at) 
VALUES ('VTR-2024-0051', 'Beratungsvertrag mit Database Systems', 'Beratungsvertrag', 'TERMINATED', 'Database Systems', 
    '2024-10-24', '2026-10-10', 90, FALSE, 159449.00, 'EUR', 
    'Vertrieb', 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO contracts (contract_number, title, contract_type, status, partner_name, 
    start_date, end_date, notice_period_days, auto_renewal, contract_value, currency, 
    department, owner_user_id, created_by, created_at, updated_at) 
VALUES ('VTR-2024-0052', 'Mietvertrag mit Frontend Experts', 'Mietvertrag', 'IN_APPROVAL', 'Frontend Experts', 
    '2024-09-24', '2025-12-24', 90, FALSE, 148873.00, 'EUR', 
    'HR', 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO contracts (contract_number, title, contract_type, status, partner_name, 
    start_date, end_date, notice_period_days, auto_renewal, contract_value, currency, 
    department, owner_user_id, created_by, created_at, updated_at) 
VALUES ('VTR-2024-0053', 'SLA mit Test Automation', 'SLA', 'DRAFT', 'Test Automation', 
    '2024-04-04', '2024-12-09', 30, FALSE, 20119.00, 'EUR', 
    'HR', 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO contracts (contract_number, title, contract_type, status, partner_name, 
    start_date, end_date, notice_period_days, auto_renewal, contract_value, currency, 
    department, owner_user_id, created_by, created_at, updated_at) 
VALUES ('VTR-2024-0054', 'SLA mit Container Solutions', 'SLA', 'APPROVED', 'Container Solutions', 
    '2024-10-04', '2025-11-04', 30, FALSE, 300380.00, 'EUR', 
    'Finanzen', 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO contracts (contract_number, title, contract_type, status, partner_name, 
    start_date, end_date, notice_period_days, auto_renewal, contract_value, currency, 
    department, owner_user_id, created_by, created_at, updated_at) 
VALUES ('VTR-2024-0055', 'Wartungsvertrag mit Penetration Testing Ltd', 'Wartungsvertrag', 'DRAFT', 'Penetration Testing Ltd', 
    '2024-04-19', '2025-08-21', 30, TRUE, 228264.00, 'EUR', 
    'IT', 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO contracts (contract_number, title, contract_type, status, partner_name, 
    start_date, end_date, notice_period_days, auto_renewal, contract_value, currency, 
    department, owner_user_id, created_by, created_at, updated_at) 
VALUES ('VTR-2024-0056', 'Kundenvertrag mit Cyber Security Pro', 'Kundenvertrag', 'APPROVED', 'Cyber Security Pro', 
    '2024-01-27', '2026-04-15', 30, TRUE, 423020.00, 'EUR', 
    'IT', 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO contracts (contract_number, title, contract_type, status, partner_name, 
    start_date, end_date, notice_period_days, auto_renewal, contract_value, currency, 
    department, owner_user_id, created_by, created_at, updated_at) 
VALUES ('VTR-2024-0057', 'Lieferantenvertrag mit Global IT Services', 'Lieferantenvertrag', 'ACTIVE', 'Global IT Services', 
    '2024-03-25', '2026-03-20', 90, FALSE, 388267.00, 'EUR', 
    'Marketing', 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO contracts (contract_number, title, contract_type, status, partner_name, 
    start_date, end_date, notice_period_days, auto_renewal, contract_value, currency, 
    department, owner_user_id, created_by, created_at, updated_at) 
VALUES ('VTR-2024-0058', 'Arbeitsvertrag mit Disaster Recovery Ltd', 'Arbeitsvertrag', 'TERMINATED', 'Disaster Recovery Ltd', 
    '2024-06-06', '2026-03-05', 90, FALSE, 301807.00, 'EUR', 
    'Einkauf', 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO contracts (contract_number, title, contract_type, status, partner_name, 
    start_date, end_date, notice_period_days, auto_renewal, contract_value, currency, 
    department, owner_user_id, created_by, created_at, updated_at) 
VALUES ('VTR-2024-0059', 'Mietvertrag mit Data Analytics GmbH', 'Mietvertrag', 'APPROVED', 'Data Analytics GmbH', 
    '2024-03-09', '2026-05-20', 90, FALSE, 494821.00, 'EUR', 
    'Finanzen', 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO contracts (contract_number, title, contract_type, status, partner_name, 
    start_date, end_date, notice_period_days, auto_renewal, contract_value, currency, 
    department, owner_user_id, created_by, created_at, updated_at) 
VALUES ('VTR-2024-0060', 'Wartungsvertrag mit ERP Consulting', 'Wartungsvertrag', 'DRAFT', 'ERP Consulting', 
    '2024-10-17', '2026-11-28', 60, FALSE, 339851.00, 'EUR', 
    'HR', 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO contracts (contract_number, title, contract_type, status, partner_name, 
    start_date, end_date, notice_period_days, auto_renewal, contract_value, currency, 
    department, owner_user_id, created_by, created_at, updated_at) 
VALUES ('VTR-2024-0061', 'Arbeitsvertrag mit Business Intelligence', 'Arbeitsvertrag', 'ACTIVE', 'Business Intelligence', 
    '2024-02-16', '2026-05-15', 90, TRUE, 47530.00, 'EUR', 
    'Logistik', 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO contracts (contract_number, title, contract_type, status, partner_name, 
    start_date, end_date, notice_period_days, auto_renewal, contract_value, currency, 
    department, owner_user_id, created_by, created_at, updated_at) 
VALUES ('VTR-2024-0062', 'Beratungsvertrag mit Penetration Testing Ltd', 'Beratungsvertrag', 'DRAFT', 'Penetration Testing Ltd', 
    '2024-10-11', '2026-03-13', 90, TRUE, 477044.00, 'EUR', 
    'Marketing', 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO contracts (contract_number, title, contract_type, status, partner_name, 
    start_date, end_date, notice_period_days, auto_renewal, contract_value, currency, 
    department, owner_user_id, created_by, created_at, updated_at) 
VALUES ('VTR-2024-0063', 'Arbeitsvertrag mit UI Development', 'Arbeitsvertrag', 'IN_NEGOTIATION', 'UI Development', 
    '2024-10-12', '2026-08-14', 60, FALSE, 285066.00, 'EUR', 
    'Vertrieb', 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO contracts (contract_number, title, contract_type, status, partner_name, 
    start_date, end_date, notice_period_days, auto_renewal, contract_value, currency, 
    department, owner_user_id, created_by, created_at, updated_at) 
VALUES ('VTR-2024-0064', 'Wartungsvertrag mit Software Factory', 'Wartungsvertrag', 'DRAFT', 'Software Factory', 
    '2024-04-07', '2026-05-04', 60, TRUE, 432852.00, 'EUR', 
    'IT', 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO contracts (contract_number, title, contract_type, status, partner_name, 
    start_date, end_date, notice_period_days, auto_renewal, contract_value, currency, 
    department, owner_user_id, created_by, created_at, updated_at) 
VALUES ('VTR-2024-0065', 'Wartungsvertrag mit Frontend Experts', 'Wartungsvertrag', 'ACTIVE', 'Frontend Experts', 
    '2024-06-15', '2025-09-05', 90, FALSE, 130460.00, 'EUR', 
    'Produktion', 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO contracts (contract_number, title, contract_type, status, partner_name, 
    start_date, end_date, notice_period_days, auto_renewal, contract_value, currency, 
    department, owner_user_id, created_by, created_at, updated_at) 
VALUES ('VTR-2024-0066', 'Beratungsvertrag mit Penetration Testing Ltd', 'Beratungsvertrag', 'TERMINATED', 'Penetration Testing Ltd', 
    '2024-01-21', '2025-03-27', 90, TRUE, 334958.00, 'EUR', 
    'IT', 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO contracts (contract_number, title, contract_type, status, partner_name, 
    start_date, end_date, notice_period_days, auto_renewal, contract_value, currency, 
    department, owner_user_id, created_by, created_at, updated_at) 
VALUES ('VTR-2024-0067', 'NDA mit Database Systems', 'NDA', 'DRAFT', 'Database Systems', 
    '2024-07-05', '2026-03-15', 30, TRUE, 196714.00, 'EUR', 
    'Produktion', 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO contracts (contract_number, title, contract_type, status, partner_name, 
    start_date, end_date, notice_period_days, auto_renewal, contract_value, currency, 
    department, owner_user_id, created_by, created_at, updated_at) 
VALUES ('VTR-2024-0068', 'Arbeitsvertrag mit Disaster Recovery Ltd', 'Arbeitsvertrag', 'EXPIRED', 'Disaster Recovery Ltd', 
    '2024-04-08', '2026-09-10', 30, TRUE, 140900.00, 'EUR', 
    'Logistik', 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO contracts (contract_number, title, contract_type, status, partner_name, 
    start_date, end_date, notice_period_days, auto_renewal, contract_value, currency, 
    department, owner_user_id, created_by, created_at, updated_at) 
VALUES ('VTR-2024-0069', 'Dienstleistungsvertrag mit Data Analytics GmbH', 'Dienstleistungsvertrag', 'ACTIVE', 'Data Analytics GmbH', 
    '2024-02-02', '2025-08-25', 30, TRUE, 38857.00, 'EUR', 
    'IT', 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO contracts (contract_number, title, contract_type, status, partner_name, 
    start_date, end_date, notice_period_days, auto_renewal, contract_value, currency, 
    department, owner_user_id, created_by, created_at, updated_at) 
VALUES ('VTR-2024-0070', 'Arbeitsvertrag mit Software Factory', 'Arbeitsvertrag', 'APPROVED', 'Software Factory', 
    '2024-09-03', '2026-11-15', 90, TRUE, 274584.00, 'EUR', 
    'Finanzen', 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO contracts (contract_number, title, contract_type, status, partner_name, 
    start_date, end_date, notice_period_days, auto_renewal, contract_value, currency, 
    department, owner_user_id, created_by, created_at, updated_at) 
VALUES ('VTR-2024-0071', 'Mietvertrag mit Scrum Masters GmbH', 'Mietvertrag', 'ACTIVE', 'Scrum Masters GmbH', 
    '2024-07-28', '2027-06-21', 90, TRUE, 111483.00, 'EUR', 
    'HR', 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO contracts (contract_number, title, contract_type, status, partner_name, 
    start_date, end_date, notice_period_days, auto_renewal, contract_value, currency, 
    department, owner_user_id, created_by, created_at, updated_at) 
VALUES ('VTR-2024-0072', 'Arbeitsvertrag mit Quantum Computing', 'Arbeitsvertrag', 'APPROVED', 'Quantum Computing', 
    '2024-01-24', '2026-10-19', 30, FALSE, 139309.00, 'EUR', 
    'Produktion', 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO contracts (contract_number, title, contract_type, status, partner_name, 
    start_date, end_date, notice_period_days, auto_renewal, contract_value, currency, 
    department, owner_user_id, created_by, created_at, updated_at) 
VALUES ('VTR-2024-0073', 'Lizenzvertrag mit Web Development Co', 'Lizenzvertrag', 'EXPIRED', 'Web Development Co', 
    '2024-07-12', '2026-12-09', 90, FALSE, 394853.00, 'EUR', 
    'Vertrieb', 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO contracts (contract_number, title, contract_type, status, partner_name, 
    start_date, end_date, notice_period_days, auto_renewal, contract_value, currency, 
    department, owner_user_id, created_by, created_at, updated_at) 
VALUES ('VTR-2024-0074', 'Beratungsvertrag mit Container Solutions', 'Beratungsvertrag', 'IN_NEGOTIATION', 'Container Solutions', 
    '2024-10-05', '2025-04-15', 60, FALSE, 30544.00, 'EUR', 
    'Logistik', 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO contracts (contract_number, title, contract_type, status, partner_name, 
    start_date, end_date, notice_period_days, auto_renewal, contract_value, currency, 
    department, owner_user_id, created_by, created_at, updated_at) 
VALUES ('VTR-2024-0075', 'Kundenvertrag mit Test Automation', 'Kundenvertrag', 'DRAFT', 'Test Automation', 
    '2024-03-11', '2025-06-22', 30, FALSE, 167578.00, 'EUR', 
    'Logistik', 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO contracts (contract_number, title, contract_type, status, partner_name, 
    start_date, end_date, notice_period_days, auto_renewal, contract_value, currency, 
    department, owner_user_id, created_by, created_at, updated_at) 
VALUES ('VTR-2024-0076', 'Lieferantenvertrag mit Docker Consulting', 'Lieferantenvertrag', 'ACTIVE', 'Docker Consulting', 
    '2024-03-10', '2026-09-17', 90, TRUE, 232345.00, 'EUR', 
    'Marketing', 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO contracts (contract_number, title, contract_type, status, partner_name, 
    start_date, end_date, notice_period_days, auto_renewal, contract_value, currency, 
    department, owner_user_id, created_by, created_at, updated_at) 
VALUES ('VTR-2024-0077', 'NDA mit Global IT Services', 'NDA', 'EXPIRED', 'Global IT Services', 
    '2024-06-29', '2025-02-10', 30, TRUE, 345747.00, 'EUR', 
    'Marketing', 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO contracts (contract_number, title, contract_type, status, partner_name, 
    start_date, end_date, notice_period_days, auto_renewal, contract_value, currency, 
    department, owner_user_id, created_by, created_at, updated_at) 
VALUES ('VTR-2024-0078', 'Beratungsvertrag mit Scrum Masters GmbH', 'Beratungsvertrag', 'TERMINATED', 'Scrum Masters GmbH', 
    '2024-03-27', '2026-04-11', 90, TRUE, 469370.00, 'EUR', 
    'Produktion', 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO contracts (contract_number, title, contract_type, status, partner_name, 
    start_date, end_date, notice_period_days, auto_renewal, contract_value, currency, 
    department, owner_user_id, created_by, created_at, updated_at) 
VALUES ('VTR-2024-0079', 'Dienstleistungsvertrag mit Mobile Apps AG', 'Dienstleistungsvertrag', 'IN_APPROVAL', 'Mobile Apps AG', 
    '2024-01-21', '2025-01-13', 90, TRUE, 273344.00, 'EUR', 
    'Marketing', 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO contracts (contract_number, title, contract_type, status, partner_name, 
    start_date, end_date, notice_period_days, auto_renewal, contract_value, currency, 
    department, owner_user_id, created_by, created_at, updated_at) 
VALUES ('VTR-2024-0080', 'SLA mit Security Auditing', 'SLA', 'EXPIRED', 'Security Auditing', 
    '2024-02-02', '2026-10-17', 30, TRUE, 413962.00, 'EUR', 
    'Finanzen', 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO contracts (contract_number, title, contract_type, status, partner_name, 
    start_date, end_date, notice_period_days, auto_renewal, contract_value, currency, 
    department, owner_user_id, created_by, created_at, updated_at) 
VALUES ('VTR-2024-0081', 'Dienstleistungsvertrag mit ERP Consulting', 'Dienstleistungsvertrag', 'IN_APPROVAL', 'ERP Consulting', 
    '2024-08-01', '2026-07-16', 60, TRUE, 92935.00, 'EUR', 
    'Produktion', 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO contracts (contract_number, title, contract_type, status, partner_name, 
    start_date, end_date, notice_period_days, auto_renewal, contract_value, currency, 
    department, owner_user_id, created_by, created_at, updated_at) 
VALUES ('VTR-2024-0082', 'Rahmenvertrag mit IoT Solutions GmbH', 'Rahmenvertrag', 'ACTIVE', 'IoT Solutions GmbH', 
    '2024-02-04', '2027-01-18', 60, FALSE, 102461.00, 'EUR', 
    'Logistik', 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO contracts (contract_number, title, contract_type, status, partner_name, 
    start_date, end_date, notice_period_days, auto_renewal, contract_value, currency, 
    department, owner_user_id, created_by, created_at, updated_at) 
VALUES ('VTR-2024-0083', 'Rahmenvertrag mit Help Desk Pro', 'Rahmenvertrag', 'IN_NEGOTIATION', 'Help Desk Pro', 
    '2024-01-07', '2026-04-30', 90, TRUE, 255118.00, 'EUR', 
    'Produktion', 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO contracts (contract_number, title, contract_type, status, partner_name, 
    start_date, end_date, notice_period_days, auto_renewal, contract_value, currency, 
    department, owner_user_id, created_by, created_at, updated_at) 
VALUES ('VTR-2024-0084', 'Projektvertrag mit UX Design Studio', 'Projektvertrag', 'DRAFT', 'UX Design Studio', 
    '2024-07-23', '2025-10-09', 30, TRUE, 320208.00, 'EUR', 
    'Finanzen', 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO contracts (contract_number, title, contract_type, status, partner_name, 
    start_date, end_date, notice_period_days, auto_renewal, contract_value, currency, 
    department, owner_user_id, created_by, created_at, updated_at) 
VALUES ('VTR-2024-0085', 'Beratungsvertrag mit Product Management', 'Beratungsvertrag', 'EXPIRED', 'Product Management', 
    '2024-04-27', '2025-01-24', 30, FALSE, 37164.00, 'EUR', 
    'Marketing', 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO contracts (contract_number, title, contract_type, status, partner_name, 
    start_date, end_date, notice_period_days, auto_renewal, contract_value, currency, 
    department, owner_user_id, created_by, created_at, updated_at) 
VALUES ('VTR-2024-0086', 'Kundenvertrag mit Network Infrastructure', 'Kundenvertrag', 'EXPIRED', 'Network Infrastructure', 
    '2024-04-21', '2026-10-31', 90, TRUE, 310311.00, 'EUR', 
    'IT', 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO contracts (contract_number, title, contract_type, status, partner_name, 
    start_date, end_date, notice_period_days, auto_renewal, contract_value, currency, 
    department, owner_user_id, created_by, created_at, updated_at) 
VALUES ('VTR-2024-0087', 'Rahmenvertrag mit Network Infrastructure', 'Rahmenvertrag', 'IN_APPROVAL', 'Network Infrastructure', 
    '2024-09-18', '2027-03-24', 30, FALSE, 331520.00, 'EUR', 
    'IT', 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO contracts (contract_number, title, contract_type, status, partner_name, 
    start_date, end_date, notice_period_days, auto_renewal, contract_value, currency, 
    department, owner_user_id, created_by, created_at, updated_at) 
VALUES ('VTR-2024-0088', 'Dienstleistungsvertrag mit Help Desk Pro', 'Dienstleistungsvertrag', 'DRAFT', 'Help Desk Pro', 
    '2024-06-13', '2026-07-13', 90, FALSE, 465365.00, 'EUR', 
    'Marketing', 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO contracts (contract_number, title, contract_type, status, partner_name, 
    start_date, end_date, notice_period_days, auto_renewal, contract_value, currency, 
    department, owner_user_id, created_by, created_at, updated_at) 
VALUES ('VTR-2024-0089', 'Rahmenvertrag mit Global IT Services', 'Rahmenvertrag', 'EXPIRED', 'Global IT Services', 
    '2024-08-25', '2026-05-21', 90, FALSE, 276695.00, 'EUR', 
    'Logistik', 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO contracts (contract_number, title, contract_type, status, partner_name, 
    start_date, end_date, notice_period_days, auto_renewal, contract_value, currency, 
    department, owner_user_id, created_by, created_at, updated_at) 
VALUES ('VTR-2024-0090', 'Wartungsvertrag mit Backend Systems AG', 'Wartungsvertrag', 'DRAFT', 'Backend Systems AG', 
    '2024-09-04', '2027-05-21', 60, FALSE, 222166.00, 'EUR', 
    'Einkauf', 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO contracts (contract_number, title, contract_type, status, partner_name, 
    start_date, end_date, notice_period_days, auto_renewal, contract_value, currency, 
    department, owner_user_id, created_by, created_at, updated_at) 
VALUES ('VTR-2024-0091', 'Kundenvertrag mit Web Development Co', 'Kundenvertrag', 'APPROVED', 'Web Development Co', 
    '2024-07-22', '2025-10-29', 90, FALSE, 239108.00, 'EUR', 
    'IT', 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO contracts (contract_number, title, contract_type, status, partner_name, 
    start_date, end_date, notice_period_days, auto_renewal, contract_value, currency, 
    department, owner_user_id, created_by, created_at, updated_at) 
VALUES ('VTR-2024-0092', 'SLA mit API Gateway Services', 'SLA', 'DRAFT', 'API Gateway Services', 
    '2024-03-17', '2025-01-14', 60, FALSE, 397761.00, 'EUR', 
    'HR', 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO contracts (contract_number, title, contract_type, status, partner_name, 
    start_date, end_date, notice_period_days, auto_renewal, contract_value, currency, 
    department, owner_user_id, created_by, created_at, updated_at) 
VALUES ('VTR-2024-0093', 'Beratungsvertrag mit Big Data Analytics', 'Beratungsvertrag', 'DRAFT', 'Big Data Analytics', 
    '2024-08-21', '2027-01-09', 60, TRUE, 369284.00, 'EUR', 
    'Produktion', 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO contracts (contract_number, title, contract_type, status, partner_name, 
    start_date, end_date, notice_period_days, auto_renewal, contract_value, currency, 
    department, owner_user_id, created_by, created_at, updated_at) 
VALUES ('VTR-2024-0094', 'Rahmenvertrag mit Disaster Recovery Ltd', 'Rahmenvertrag', 'ACTIVE', 'Disaster Recovery Ltd', 
    '2024-07-30', '2025-11-09', 90, TRUE, 388345.00, 'EUR', 
    'Marketing', 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO contracts (contract_number, title, contract_type, status, partner_name, 
    start_date, end_date, notice_period_days, auto_renewal, contract_value, currency, 
    department, owner_user_id, created_by, created_at, updated_at) 
VALUES ('VTR-2024-0095', 'Rahmenvertrag mit Product Management', 'Rahmenvertrag', 'TERMINATED', 'Product Management', 
    '2024-07-03', '2027-05-19', 90, TRUE, 419279.00, 'EUR', 
    'Einkauf', 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO contracts (contract_number, title, contract_type, status, partner_name, 
    start_date, end_date, notice_period_days, auto_renewal, contract_value, currency, 
    department, owner_user_id, created_by, created_at, updated_at) 
VALUES ('VTR-2024-0096', 'Lieferantenvertrag mit Network Infrastructure', 'Lieferantenvertrag', 'IN_APPROVAL', 'Network Infrastructure', 
    '2024-03-14', '2025-08-24', 60, TRUE, 125277.00, 'EUR', 
    'Marketing', 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO contracts (contract_number, title, contract_type, status, partner_name, 
    start_date, end_date, notice_period_days, auto_renewal, contract_value, currency, 
    department, owner_user_id, created_by, created_at, updated_at) 
VALUES ('VTR-2024-0097', 'Rahmenvertrag mit Container Solutions', 'Rahmenvertrag', 'IN_NEGOTIATION', 'Container Solutions', 
    '2024-01-26', '2024-12-08', 60, TRUE, 443658.00, 'EUR', 
    'Marketing', 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO contracts (contract_number, title, contract_type, status, partner_name, 
    start_date, end_date, notice_period_days, auto_renewal, contract_value, currency, 
    department, owner_user_id, created_by, created_at, updated_at) 
VALUES ('VTR-2024-0098', 'Mietvertrag mit Mobile Apps AG', 'Mietvertrag', 'DRAFT', 'Mobile Apps AG', 
    '2024-01-06', '2026-05-30', 90, FALSE, 328243.00, 'EUR', 
    'Produktion', 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO contracts (contract_number, title, contract_type, status, partner_name, 
    start_date, end_date, notice_period_days, auto_renewal, contract_value, currency, 
    department, owner_user_id, created_by, created_at, updated_at) 
VALUES ('VTR-2024-0099', 'Wartungsvertrag mit AI Research Lab', 'Wartungsvertrag', 'IN_NEGOTIATION', 'AI Research Lab', 
    '2024-10-08', '2026-07-16', 30, FALSE, 214890.00, 'EUR', 
    'IT', 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO contracts (contract_number, title, contract_type, status, partner_name, 
    start_date, end_date, notice_period_days, auto_renewal, contract_value, currency, 
    department, owner_user_id, created_by, created_at, updated_at) 
VALUES ('VTR-2024-0100', 'Beratungsvertrag mit System Integration', 'Beratungsvertrag', 'DRAFT', 'System Integration', 
    '2024-05-27', '2026-07-26', 30, FALSE, 339920.00, 'EUR', 
    'IT', 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- ============================================================================
-- MIGRATION COMPLETE - 100 sample contracts inserted
-- ============================================================================
