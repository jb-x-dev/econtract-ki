-- ============================================================================
-- eContract KI - Users Table and Admin User Creation
-- Version: V10
-- Description: Create users table and default admin user for login
-- Author: jb-x Development Team
-- Date: 2025-11-08
-- PostgreSQL Compatible
-- ============================================================================

-- ============================================================================
-- 1. CREATE USERS TABLE
-- ============================================================================

CREATE TABLE IF NOT EXISTS users (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    username VARCHAR(100) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    first_name VARCHAR(100),
    last_name VARCHAR(100),
    department VARCHAR(50),
    position VARCHAR(100),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    role VARCHAR(50) NOT NULL DEFAULT 'USER',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_login TIMESTAMP
);

CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_active ON users(active);

-- ============================================================================
-- 2. INSERT ADMIN USER
-- ============================================================================

-- Insert admin user
-- Password: admin123 (BCrypt hash)
INSERT INTO users (username, password_hash, email, first_name, last_name, department, position, active, role, created_at, updated_at)
VALUES (
    'admin',
    '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
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

-- ============================================================================
-- 3. INSERT DEMO USER
-- ============================================================================

-- Insert demo user
-- Password: demo123 (BCrypt hash)
INSERT INTO users (username, password_hash, email, first_name, last_name, department, position, active, role, created_at, updated_at)
VALUES (
    'demo',
    '$2a$10$8K1p/a0dL3.qdkqH0CQMB.Xc2hDlD6qKfPqPqPqPqPqPqPqPqPqPq',
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
