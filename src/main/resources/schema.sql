-- ============================================================================
-- Schema SQL pour Pharmacy Order Management
-- Crée les tables dans le bon ordre sans erreurs de syntaxe
-- ============================================================================

SET FOREIGN_KEY_CHECKS=0;

-- Nettoyer absolument TOUTES les tables possibles pour éviter les conflits d'héritage
DROP TABLE IF EXISTS prescription_notes;
DROP TABLE IF EXISTS notifications;
DROP TABLE IF EXISTS delivery_agents;
DROP TABLE IF EXISTS delivery_agencies;
DROP TABLE IF EXISTS deliveries;
DROP TABLE IF EXISTS payments;
DROP TABLE IF EXISTS order_tracking;
DROP TABLE IF EXISTS order_items;
DROP TABLE IF EXISTS pharmacy_orders;
DROP TABLE IF EXISTS prescription_items;
DROP TABLE IF EXISTS prescriptions;
DROP TABLE IF EXISTS consultations;
DROP TABLE IF EXISTS medical_records;
DROP TABLE IF EXISTS pharmacy_stocks;
DROP TABLE IF EXISTS stock_movements;
DROP TABLE IF EXISTS stock_batches;
DROP TABLE IF EXISTS stock_alerts;
DROP TABLE IF EXISTS products;
DROP TABLE IF EXISTS pharmacists;
DROP TABLE IF EXISTS patients;
DROP TABLE IF EXISTS doctors;
DROP TABLE IF EXISTS clinics;
DROP TABLE IF EXISTS nutritionists;
DROP TABLE IF EXISTS laboratory_staff;
DROP TABLE IF EXISTS home_care_services;
DROP TABLE IF EXISTS service_providers;
DROP TABLE IF EXISTS pharmacies;
DROP TABLE IF EXISTS users;

-- On garde FOREIGN_KEY_CHECKS=0 durant toute la création pour éviter les erreurs d'ordre


-- ============================================================================
-- TABLE 1: USERS
-- ============================================================================
CREATE TABLE users (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    full_name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role ENUM('ADMIN','DOCTOR','CLINIC','PHARMACIST','LABORATORY_STAFF','NUTRITIONIST','VISITOR','PATIENT','HOME_CARE_PROVIDER') NOT NULL DEFAULT 'VISITOR',
    phone VARCHAR(20),
    birth_date DATE,
    enabled BOOLEAN DEFAULT TRUE,
    dtype VARCHAR(31)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE patients (
    id BIGINT PRIMARY KEY,
    gender VARCHAR(20),
    blood_type VARCHAR(10),
    emergency_contact_name VARCHAR(255),
    emergency_contact_phone VARCHAR(20),
    chronic_diseases TEXT,
    drug_allergies TEXT,
    hereditary_diseases TEXT,
    FOREIGN KEY (id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE pharmacists (
    id BIGINT PRIMARY KEY,
    pharmacy_id BIGINT,
    FOREIGN KEY (id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (pharmacy_id) REFERENCES pharmacies(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================================
-- TABLE 2: PHARMACIES
-- ============================================================================
CREATE TABLE pharmacies (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    address VARCHAR(255),
    location_lat DOUBLE,
    location_lng DOUBLE,
    phone_number VARCHAR(20),
    email VARCHAR(255)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================================
-- TABLE 3: PRODUCTS
-- ============================================================================
CREATE TABLE products (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    category VARCHAR(255),
    brand VARCHAR(255),
    manufacturer VARCHAR(255),
    barcode VARCHAR(100),
    image_url VARCHAR(500),
    type VARCHAR(100),
    unit VARCHAR(50)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================================
-- TABLE 4: PHARMACY_STOCKS
-- ============================================================================
CREATE TABLE pharmacy_stocks (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    pharmacy_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    total_quantity INTEGER NOT NULL,
    unit_price DECIMAL(12, 2) NOT NULL,
    min_quantity_threshold INTEGER,
    FOREIGN KEY (pharmacy_id) REFERENCES pharmacies(id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE,
    UNIQUE KEY uk_pharmacy_product (pharmacy_id, product_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================================
-- TABLE 5: MEDICAL_RECORDS
-- ============================================================================
CREATE TABLE medical_records (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    patient_id BIGINT NOT NULL UNIQUE,
    FOREIGN KEY (patient_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================================
-- TABLE 6: CONSULTATIONS
-- ============================================================================
CREATE TABLE consultations (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    medical_record_id BIGINT NOT NULL,
    doctor_id BIGINT NOT NULL,
    date DATETIME NOT NULL,
    observations VARCHAR(500),
    notes TEXT,
    FOREIGN KEY (medical_record_id) REFERENCES medical_records(id) ON DELETE CASCADE,
    FOREIGN KEY (doctor_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================================
-- TABLE 7: PRESCRIPTIONS
-- ============================================================================
CREATE TABLE prescriptions (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    consultation_id BIGINT NOT NULL,
    date_created DATE NOT NULL,
    FOREIGN KEY (consultation_id) REFERENCES consultations(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================================
-- TABLE 8: PRESCRIPTION_ITEMS
-- ============================================================================
CREATE TABLE prescription_items (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    prescription_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    quantity INTEGER NOT NULL,
    dosage VARCHAR(255),
    duration VARCHAR(255),
    FOREIGN KEY (prescription_id) REFERENCES prescriptions(id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================================
-- TABLE 9: PHARMACY_ORDERS
-- ============================================================================
CREATE TABLE pharmacy_orders (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    patient_id BIGINT NOT NULL,
    pharmacy_id BIGINT NOT NULL,
    prescription_id BIGINT,
    status ENUM('PENDING','REVIEWING','VALIDATED','PAYMENT_PENDING','PAID','AWAITING_CHOICE','ASSIGNING','ASSIGNED','RESERVED','READY_FOR_PICKUP','DELIVERY_REQUESTED','OUT_FOR_DELIVERY','PICKED_UP','DELIVERED','CANCELLED','REJECTED') NOT NULL DEFAULT 'PENDING',
    total_price DECIMAL(12, 2) NOT NULL,
    delivery_address VARCHAR(255) NOT NULL,
    delivery_type ENUM('PICKUP','HOME_DELIVERY'),
    scheduled_delivery_date DATE,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (patient_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (pharmacy_id) REFERENCES pharmacies(id) ON DELETE CASCADE,
    FOREIGN KEY (prescription_id) REFERENCES prescriptions(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================================
-- TABLE 10: ORDER_ITEMS
-- ============================================================================
CREATE TABLE order_items (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    order_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    quantity INTEGER NOT NULL,
    price DECIMAL(12, 2) NOT NULL,
    FOREIGN KEY (order_id) REFERENCES pharmacy_orders(id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================================
-- TABLE 11: ORDER_TRACKING
-- ============================================================================
CREATE TABLE order_tracking (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    order_id BIGINT NOT NULL,
    status ENUM('PENDING','REVIEWING','VALIDATED','PAYMENT_PENDING','PAID','AWAITING_CHOICE','ASSIGNING','ASSIGNED','RESERVED','READY_FOR_PICKUP','DELIVERY_REQUESTED','OUT_FOR_DELIVERY','PICKED_UP','DELIVERED','CANCELLED') NOT NULL,
    changed_by VARCHAR(255) NOT NULL,
    changed_at DATETIME NOT NULL,
    note VARCHAR(255),
    FOREIGN KEY (order_id) REFERENCES pharmacy_orders(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================================
-- TABLE 12: PAYMENTS
-- ============================================================================
CREATE TABLE payments (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    order_id BIGINT NOT NULL UNIQUE,
    method ENUM('CASH_ON_DELIVERY','BANK_CARD','STRIPE','D17') NOT NULL,
    status ENUM('PENDING','COMPLETED','FAILED','REFUNDED') NOT NULL,
    amount DECIMAL(12, 2) NOT NULL,
    transaction_id VARCHAR(255),
    created_at DATETIME NOT NULL,
    paid_at DATETIME,
    FOREIGN KEY (order_id) REFERENCES pharmacy_orders(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================================
-- TABLE 13: DELIVERIES
-- ============================================================================
CREATE TABLE delivery_agencies (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    phone_number VARCHAR(50),
    city VARCHAR(255)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE delivery_agents (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    phone_number VARCHAR(50),
    vehicle_type ENUM('MOTORCYCLE','CAR','BICYCLE','VAN') NOT NULL,
    status ENUM('AVAILABLE','BUSY','OFFLINE') NOT NULL,
    agency_id BIGINT,
    FOREIGN KEY (agency_id) REFERENCES delivery_agencies(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE deliveries (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    order_id BIGINT NOT NULL UNIQUE,
    agent_id BIGINT,
    agency_id BIGINT,
    agency_name VARCHAR(255),
    external_tracking_id VARCHAR(255),
    tracking_url VARCHAR(255),
    tracking_number VARCHAR(255),
    courier_name VARCHAR(255),
    courier_phone VARCHAR(50),
    status ENUM('PENDING','REQUESTED','ASSIGNED','PICKED_UP','IN_TRANSIT','OUT_FOR_DELIVERY','DELIVERED','FAILED','CANCELLED') NOT NULL,
    current_lat DOUBLE,
    current_lng DOUBLE,
    estimated_delivery_date DATETIME,
    estimated_arrival DATETIME,
    requested_at DATETIME,
    delivered_at DATETIME,
    created_at DATETIME,
    updated_at DATETIME,
    FOREIGN KEY (order_id) REFERENCES pharmacy_orders(id) ON DELETE CASCADE,
    FOREIGN KEY (agent_id) REFERENCES delivery_agents(id) ON DELETE SET NULL,
    FOREIGN KEY (agency_id) REFERENCES delivery_agencies(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================================
-- TABLE 14: NOTIFICATIONS
-- ============================================================================
CREATE TABLE notifications (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    recipient_id BIGINT NOT NULL,
    order_id BIGINT,
    title VARCHAR(255) NOT NULL,
    message VARCHAR(500) NOT NULL,
    type ENUM('ORDER_CREATED','ORDER_VALIDATED','PAYMENT_CONFIRMED','OUT_FOR_DELIVERY','DELIVERED','DELIVERY_PICKED_UP','DELIVERY_ASSIGNED','DELIVERY_CHOICE_REQUIRED','NO_DRIVER_AVAILABLE','ORDER_CANCELLED') NOT NULL,
    is_read BOOLEAN DEFAULT FALSE,
    created_at DATETIME NOT NULL,
    FOREIGN KEY (recipient_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (order_id) REFERENCES pharmacy_orders(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================================
-- TABLE 15: PRESCRIPTION_NOTES
-- ============================================================================
CREATE TABLE prescription_notes (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    order_id BIGINT NOT NULL,
    pharmacist_id BIGINT NOT NULL,
    medication_name VARCHAR(255) NOT NULL,
    dosage VARCHAR(255),
    duration VARCHAR(255),
    comment VARCHAR(1000),
    FOREIGN KEY (order_id) REFERENCES pharmacy_orders(id) ON DELETE CASCADE,
    FOREIGN KEY (pharmacist_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================================
-- CRÉER LES INDEX
-- ============================================================================
CREATE INDEX idx_pharmacy_orders_patient ON pharmacy_orders(patient_id);
CREATE INDEX idx_pharmacy_orders_pharmacy ON pharmacy_orders(pharmacy_id);
CREATE INDEX idx_pharmacy_orders_status ON pharmacy_orders(status);
CREATE INDEX idx_order_tracking_order ON order_tracking(order_id);
CREATE INDEX idx_notifications_recipient ON notifications(recipient_id);
CREATE INDEX idx_notifications_order ON notifications(order_id);
CREATE INDEX idx_prescription_notes_order ON prescription_notes(order_id);
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_role ON users(role);

-- ============================================================================
-- TABLE 16: HOME CARE MODULE
-- ============================================================================
CREATE TABLE home_care_services (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    price DECIMAL(12, 2) NOT NULL,
    category VARCHAR(255),
    icon_url VARCHAR(500),
    duration_minutes INTEGER,
    active BOOLEAN NOT NULL DEFAULT TRUE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE service_providers (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE,
    certification_document VARCHAR(500),
    verified BOOLEAN NOT NULL DEFAULT FALSE,
    bio TEXT,
    profile_picture_url VARCHAR(500),
    average_rating DOUBLE NOT NULL DEFAULT 0.0,
    total_reviews INTEGER NOT NULL DEFAULT 0,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE service_provider_specialties (
    service_provider_id BIGINT NOT NULL,
    home_care_service_id BIGINT NOT NULL,
    PRIMARY KEY (service_provider_id, home_care_service_id),
    FOREIGN KEY (service_provider_id) REFERENCES service_providers(id) ON DELETE CASCADE,
    FOREIGN KEY (home_care_service_id) REFERENCES home_care_services(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE provider_availabilities (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    provider_id BIGINT NOT NULL,
    day_of_week VARCHAR(20),
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    available BOOLEAN NOT NULL DEFAULT TRUE,
    specific_date DATE,
    FOREIGN KEY (provider_id) REFERENCES service_providers(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE service_requests (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    patient_id BIGINT NOT NULL,
    service_id BIGINT NOT NULL,
    provider_id BIGINT,
    status ENUM('PENDING','ACCEPTED','IN_PROGRESS','COMPLETED','CANCELLED') NOT NULL DEFAULT 'PENDING',
    requested_date_time DATETIME NOT NULL,
    assigned_date_time DATETIME,
    completed_at DATETIME,
    address VARCHAR(500) NOT NULL,
    patient_notes TEXT,
    provider_notes TEXT,
    price DECIMAL(12, 2) NOT NULL,
    created_at DATETIME NOT NULL,
    FOREIGN KEY (patient_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (service_id) REFERENCES home_care_services(id) ON DELETE CASCADE,
    FOREIGN KEY (provider_id) REFERENCES service_providers(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE service_reviews (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    request_id BIGINT NOT NULL UNIQUE,
    patient_id BIGINT NOT NULL,
    provider_id BIGINT NOT NULL,
    rating INTEGER NOT NULL,
    comment TEXT,
    created_at DATETIME NOT NULL,
    FOREIGN KEY (request_id) REFERENCES service_requests(id) ON DELETE CASCADE,
    FOREIGN KEY (patient_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (provider_id) REFERENCES service_providers(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Réactiver les vérifications de clés étrangères à la toute fin
SET FOREIGN_KEY_CHECKS=1;
