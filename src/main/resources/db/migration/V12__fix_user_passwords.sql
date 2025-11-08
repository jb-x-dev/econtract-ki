-- ============================================================================
-- eContract KI - Fix User Passwords with Correct BCrypt Hashes
-- Version: V12
-- Description: Update admin and demo user passwords with correct BCrypt hashes
-- Author: jb-x Development Team
-- Date: 2025-11-08
-- PostgreSQL Compatible
-- ============================================================================

-- Update admin user password with correct BCrypt hash for 'admin123'
UPDATE users 
SET password_hash = '$2b$12$jwEn8LE2XNegZOjx1fHzIO.CV9X7X/mro2Sz1J7L6VBfT7t19cVDK',
    updated_at = CURRENT_TIMESTAMP
WHERE username = 'admin';

-- Update demo user password with correct BCrypt hash for 'demo123'
UPDATE users 
SET password_hash = '$2b$12$FpIROysA/hJujdU/SvuJT.4xl2o5F7/T662jhTiXuDC0ygxNwIiRq',
    updated_at = CURRENT_TIMESTAMP
WHERE username = 'demo';

-- Verify users exist and are active
DO $$
DECLARE
    admin_count INTEGER;
    demo_count INTEGER;
BEGIN
    SELECT COUNT(*) INTO admin_count FROM users WHERE username = 'admin' AND active = TRUE;
    SELECT COUNT(*) INTO demo_count FROM users WHERE username = 'demo' AND active = TRUE;
    
    IF admin_count = 0 THEN
        RAISE NOTICE 'WARNING: Admin user not found or inactive!';
    ELSE
        RAISE NOTICE 'Admin user password updated successfully';
    END IF;
    
    IF demo_count = 0 THEN
        RAISE NOTICE 'WARNING: Demo user not found or inactive!';
    ELSE
        RAISE NOTICE 'Demo user password updated successfully';
    END IF;
END $$;

-- ============================================================================
-- MIGRATION COMPLETE
-- Password Hashes Updated:
-- - admin: admin123 -> $2b$12$jwEn8LE2XNegZOjx1fHzIO.CV9X7X/mro2Sz1J7L6VBfT7t19cVDK
-- - demo:  demo123  -> $2b$12$FpIROysA/hJujdU/SvuJT.4xl2o5F7/T662jhTiXuDC0ygxNwIiRq
-- ============================================================================
