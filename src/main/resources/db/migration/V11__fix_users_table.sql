-- ============================================================================
-- eContract KI - Fix Users Table
-- Version: V11
-- Description: Add missing columns to users table if they don't exist
-- Author: jb-x Development Team
-- Date: 2025-11-08
-- PostgreSQL Compatible
-- ============================================================================

-- Add missing columns if they don't exist
DO $$
BEGIN
    -- Add active column if it doesn't exist
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns 
                   WHERE table_name='users' AND column_name='active') THEN
        ALTER TABLE users ADD COLUMN active BOOLEAN NOT NULL DEFAULT TRUE;
    END IF;

    -- Add role column if it doesn't exist
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns 
                   WHERE table_name='users' AND column_name='role') THEN
        ALTER TABLE users ADD COLUMN role VARCHAR(50) NOT NULL DEFAULT 'USER';
    END IF;

    -- Add last_login column if it doesn't exist
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns 
                   WHERE table_name='users' AND column_name='last_login') THEN
        ALTER TABLE users ADD COLUMN last_login TIMESTAMP;
    END IF;
END $$;

-- Create indexes if they don't exist
CREATE INDEX IF NOT EXISTS idx_users_username ON users(username);
CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);
CREATE INDEX IF NOT EXISTS idx_users_active ON users(active);

-- Insert admin user if not exists
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

-- Insert demo user if not exists
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
