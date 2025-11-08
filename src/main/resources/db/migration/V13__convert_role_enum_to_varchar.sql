-- ============================================================================
-- eContract KI - Convert role ENUM to VARCHAR
-- Version: V13
-- Description: Convert users.role from ENUM type to VARCHAR to fix Hibernate compatibility
-- Author: jb-x Development Team
-- Date: 2025-11-08
-- PostgreSQL Compatible
-- ============================================================================

-- Step 1: Alter the role column to VARCHAR
ALTER TABLE users 
    ALTER COLUMN role TYPE VARCHAR(50) USING role::text;

-- Step 2: Drop the user_role ENUM type (if no other tables use it)
DROP TYPE IF EXISTS user_role CASCADE;

-- Step 3: Verify the change
DO $$
DECLARE
    role_count INTEGER;
BEGIN
    SELECT COUNT(*) INTO role_count FROM users WHERE role IS NOT NULL;
    RAISE NOTICE 'Successfully converted % user roles from ENUM to VARCHAR', role_count;
END $$;

-- ============================================================================
-- MIGRATION COMPLETE
-- The role column is now VARCHAR(50) and compatible with JPA String mapping
-- ============================================================================
