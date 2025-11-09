-- V15: Convert contract_status ENUM to VARCHAR
-- Problem: PostgreSQL ENUM contract_status is not compatible with JPA String comparison
-- Solution: Convert status column from ENUM to VARCHAR

-- Step 1: Add temporary column
ALTER TABLE contracts ADD COLUMN status_temp VARCHAR(50);

-- Step 2: Copy data from ENUM to VARCHAR
UPDATE contracts SET status_temp = status::text;

-- Step 3: Drop old ENUM column
ALTER TABLE contracts DROP COLUMN status;

-- Step 4: Rename temporary column to status
ALTER TABLE contracts RENAME COLUMN status_temp TO status;

-- Step 5: Set NOT NULL constraint
ALTER TABLE contracts ALTER COLUMN status SET NOT NULL;

-- Step 6: Set default value
ALTER TABLE contracts ALTER COLUMN status SET DEFAULT 'DRAFT';

-- Step 7: Drop ENUM type
DROP TYPE IF EXISTS contract_status CASCADE;

-- Add index for better query performance
CREATE INDEX IF NOT EXISTS idx_contracts_status ON contracts(status);
