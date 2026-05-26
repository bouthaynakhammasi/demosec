-- Fix pharmacy_orders status column
ALTER TABLE pharmacy_orders MODIFY COLUMN status VARCHAR(20) NOT NULL DEFAULT 'PENDING';

-- Fix order_tracking status column  
ALTER TABLE order_tracking MODIFY COLUMN status VARCHAR(20) NOT NULL;

-- Verify the changes
SELECT TABLE_NAME, COLUMN_NAME, COLUMN_TYPE, IS_NULLABLE FROM INFORMATION_SCHEMA.COLUMNS 
WHERE TABLE_NAME IN ('pharmacy_orders', 'order_tracking') AND COLUMN_NAME='status';
