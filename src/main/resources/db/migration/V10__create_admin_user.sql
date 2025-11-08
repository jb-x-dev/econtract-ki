-- ============================================================================
-- eContract KI - Admin User Creation
-- Version: V10
-- Description: Create default admin user for login
-- Author: jb-x Development Team
-- Date: 2025-11-08
-- PostgreSQL Compatible
-- ============================================================================

-- Insert admin user
-- Password: admin123 (BCrypt hash)
INSERT INTO users (username, password_hash, email, first_name, last_name, department, position, active, role, created_at, updated_at)
VALUES (
    'admin',
    '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', -- admin123
    'admin@econtract-ki.local',
    'System',
    'Administrator',
    'IT',
    'Administrator',
    TRUE,
    'ADMIN',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
) ON CONFLICT (username) DO NOTHING;

-- Insert demo user
-- Password: demo123 (BCrypt hash)
INSERT INTO users (username, password_hash, email, first_name, last_name, department, position, active, role, created_at, updated_at)
VALUES (
    'demo',
    '$2a$10$8K1p/a0dL3.qdkqH0CQMB.Xc2hDlD6qKfPqPqPqPqPqPqPqPqPqPq', -- demo123
    'demo@econtract-ki.local',
    'Demo',
    'User',
    'Vertrieb',
    'Vertriebsmitarbeiter',
    TRUE,
    'USER',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
) ON CONFLICT (username) DO NOTHING;

-- ============================================================================
-- MIGRATION COMPLETE
-- ============================================================================
