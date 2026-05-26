-- ======================================================================
-- Migration: Synchroniser les colonnes ENUM avec les entités Hibernate
-- ======================================================================
-- Raison: Hibernate génère automatiquement les DDL ALTER TABLE pour les colonnes ENUM,
-- mais parfois les modifications ne s'appliquent pas (ex: lors du drop/recreate de colonnes)
-- Cette migration synchronise les énumérations avec les tables

-- 1. Notifications table - synchroniser la colonne type
ALTER TABLE notifications
  MODIFY COLUMN type enum (
    'ACCOUNT_ACTIVATED',
    'DELIVERED',
    'DELIVERY_ASSIGNED',
    'DELIVERY_CHOICE_REQUIRED',
    'DELIVERY_PICKED_UP',
    'HOMECARE_COMPLETED',
    'HOMECARE_IN_PROGRESS',
    'HOMECARE_REQUEST_ACCEPTED',
    'NEW_HOMECARE_REQUEST',
    'NO_DRIVER_AVAILABLE',
    'ORDER_CANCELLED',
    'ORDER_CREATED',
    'ORDER_REJECTED',
    'ORDER_VALIDATED',
    'OUT_FOR_DELIVERY',
    'PAYMENT_CONFIRMED',
    'REG_REQ'
  ) NOT NULL;

-- 2. Order Tracking table - synchroniser la colonne status
ALTER TABLE order_tracking
  MODIFY COLUMN status enum (
    'ASSIGNED',
    'ASSIGNING',
    'AWAITING_CHOICE',
    'CANCELLED',
    'DELIVERED',
    'DELIVERY_REQUESTED',
    'OUT_FOR_DELIVERY',
    'PAID',
    'PAYMENT_PENDING',
    'PENDING',
    'PICKED_UP',
    'READY_FOR_PICKUP',
    'REJECTED',
    'RESERVED',
    'REVIEWING',
    'VALIDATED'
  ) NOT NULL;

-- 3. Pharmacy Orders table - synchroniser la colonne status
ALTER TABLE pharmacy_orders
  MODIFY COLUMN status enum (
    'ASSIGNED',
    'ASSIGNING',
    'AWAITING_CHOICE',
    'CANCELLED',
    'DELIVERED',
    'DELIVERY_REQUESTED',
    'OUT_FOR_DELIVERY',
    'PAID',
    'PAYMENT_PENDING',
    'PENDING',
    'PICKED_UP',
    'READY_FOR_PICKUP',
    'REJECTED',
    'RESERVED',
    'REVIEWING',
    'VALIDATED'
  ) NOT NULL;

