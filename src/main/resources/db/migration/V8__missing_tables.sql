-- ============================================================================
-- eContract KI - Missing Tables Migration
-- Version: V8
-- Description: Add missing tables for ContractParticipant and QuickFilter entities
-- Author: jb-x Development Team
-- Date: 2025-11-08
-- PostgreSQL Compatible
-- ============================================================================

-- Create ENUM type for participant type
CREATE TYPE participant_type AS ENUM ('RESPONSIBLE', 'INFOUSER', 'EXTERNAL_INFOUSER');

-- ============================================================================
-- 1. CONTRACT PARTICIPANTS (Vertragsbeteiligte)
-- ============================================================================

CREATE TABLE IF NOT EXISTS contract_participants (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    contract_id BIGINT NOT NULL,
    participant_type participant_type NOT NULL,
    user_id BIGINT,
    external_name VARCHAR(255),
    external_email VARCHAR(255),
    external_phone VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_cp_contract FOREIGN KEY (contract_id) REFERENCES contracts(id) ON DELETE CASCADE,
    CONSTRAINT fk_cp_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL
);

CREATE INDEX idx_contract_participants_contract_id ON contract_participants(contract_id);
CREATE INDEX idx_contract_participants_user_id ON contract_participants(user_id);
CREATE INDEX idx_contract_participants_type ON contract_participants(participant_type);

-- ============================================================================
-- 2. QUICK FILTERS (Schnellfilter)
-- ============================================================================

CREATE TABLE IF NOT EXISTS quick_filters (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    user_id BIGINT NOT NULL,
    filter_name VARCHAR(255) NOT NULL,
    filter_criteria TEXT NOT NULL,
    is_default BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_qf_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE INDEX idx_quick_filters_user_id ON quick_filters(user_id);
CREATE INDEX idx_quick_filters_is_default ON quick_filters(is_default);

-- ============================================================================
-- MIGRATION COMPLETE
-- ============================================================================
