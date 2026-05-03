-- ============================================================================
-- MIGRATION: Fix pharmacy_orders status column
-- Purpose: Convert ENUM to VARCHAR and add missing REJECTED status
-- Date: 2026-03-29
-- ============================================================================

USE demospringsecurity;

-- Step 1: Back up the current data
SELECT * INTO OUTFILE '/tmp/pharmacy_orders_backup.sql'
    FROM pharmacy_orders;

-- Step 2: Modify the status column to VARCHAR(20)
-- This change is BREAKING for existing ENUM data, so we'll use a safe approach:
ALTER TABLE pharmacy_orders 
MODIFY COLUMN status VARCHAR(20) NOT NULL DEFAULT 'PENDING';

-- Step 3: Do the same for order_tracking table (also has status column)
ALTER TABLE order_tracking 
MODIFY COLUMN status VARCHAR(20) NOT NULL;

-- Step 4: Verify the changes
SHOW CREATE TABLE pharmacy_orders;
SHOW CREATE TABLE order_tracking;

-- ============================================================================
-- Verification Query
-- ============================================================================
SELECT 
    'pharmacy_orders' as table_name,
    COLUMN_TYPE as column_type,
    IS_NULLABLE as nullable
FROM information_schema.COLUMNS
WHERE TABLE_SCHEMA = 'demospringsecurity' 
AND TABLE_NAME = 'pharmacy_orders' 
AND COLUMN_NAME = 'status'
UNION ALL
SELECT 
    'order_tracking' as table_name,
    COLUMN_TYPE as column_type,
    IS_NULLABLE as nullable
FROM information_schema.COLUMNS
WHERE TABLE_SCHEMA = 'demospringsecurity' 
AND TABLE_NAME = 'order_tracking' 
AND COLUMN_NAME = 'status';
