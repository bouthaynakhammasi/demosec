-- ============================================================================
-- Base de données complète pour Pharmacy Order Management
-- ============================================================================

-- Créer la base de données
DROP DATABASE IF EXISTS demospringsecurity;
CREATE DATABASE demospringsecurity CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE demospringsecurity;

-- ============================================================================
-- TABLES DE BASE
-- ============================================================================

-- Table des utilisateurs (base pour toutes les entités)
CREATE TABLE users (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    full_name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role ENUM('ADMIN','DOCTOR','CLINIC','PHARMACIST','LABORATORY_STAFF','NUTRITIONIST','VISITOR','PATIENT','HOME_CARE_PROVIDER') NOT NULL DEFAULT 'VISITOR',
    phone VARCHAR(20),
    birth_date DATE,
    enabled BOOLEAN DEFAULT TRUE,
    dtype VARCHAR(31),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Table des pharmacies
CREATE TABLE pharmacies (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    address VARCHAR(255),
    location_lat DOUBLE,
    location_lng DOUBLE,
    phone_number VARCHAR(20),
    email VARCHAR(255)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Table des produits
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

-- Table des stocks de pharmacie
CREATE TABLE pharmacy_stocks (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    pharmacy_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    total_quantity INTEGER NOT NULL,
    unit_price DECIMAL(12, 2) NOT NULL,
    min_quantity_threshold INTEGER,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (pharmacy_id) REFERENCES pharmacies(id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE,
    UNIQUE KEY uk_pharmacy_product (pharmacy_id, product_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Table des consultations (pour prescriptions)
CREATE TABLE consultations (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    patient_id BIGINT NOT NULL,
    doctor_id BIGINT NOT NULL,
    consultation_date DATETIME NOT NULL,
    diagnosis TEXT,
    notes TEXT,
    FOREIGN KEY (patient_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (doctor_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Table des prescriptions
CREATE TABLE prescriptions (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    consultation_id BIGINT NOT NULL,
    date_created DATE NOT NULL,
    FOREIGN KEY (consultation_id) REFERENCES consultations(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Table des articles de prescription
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
-- TABLE PRINCIPALE: PHARMACY_ORDERS
-- ============================================================================

CREATE TABLE pharmacy_orders (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    patient_id BIGINT NOT NULL,
    pharmacy_id BIGINT NOT NULL,
    prescription_id BIGINT,
    status ENUM('PENDING','REVIEWING','VALIDATED','PAYMENT_PENDING','PAID','AWAITING_CHOICE','ASSIGNING','ASSIGNED','RESERVED','READY_FOR_PICKUP','DELIVERY_REQUESTED','OUT_FOR_DELIVERY','PICKED_UP','DELIVERED','CANCELLED') NOT NULL DEFAULT 'PENDING',
    total_price DECIMAL(12, 2) NOT NULL,
    delivery_address VARCHAR(255) NOT NULL,
    delivery_type ENUM('PICKUP','HOME_DELIVERY'),
    scheduled_delivery_date DATE,
    pharmacist_note VARCHAR(255),
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (patient_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (pharmacy_id) REFERENCES pharmacies(id) ON DELETE CASCADE,
    FOREIGN KEY (prescription_id) REFERENCES prescriptions(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================================
-- TABLES LIÉES AUX COMMANDES
-- ============================================================================

-- Table des articles de commande
CREATE TABLE order_items (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    order_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    quantity INTEGER NOT NULL,
    price DECIMAL(12, 2) NOT NULL,
    FOREIGN KEY (order_id) REFERENCES pharmacy_orders(id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Table de suivi des commandes
CREATE TABLE order_tracking (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    order_id BIGINT NOT NULL,
    status ENUM('PENDING','REVIEWING','VALIDATED','PAYMENT_PENDING','PAID','AWAITING_CHOICE','ASSIGNING','ASSIGNED','RESERVED','READY_FOR_PICKUP','DELIVERY_REQUESTED','OUT_FOR_DELIVERY','PICKED_UP','DELIVERED','CANCELLED') NOT NULL,
    changed_by VARCHAR(255) NOT NULL,
    changed_at DATETIME NOT NULL,
    note VARCHAR(255),
    FOREIGN KEY (order_id) REFERENCES pharmacy_orders(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Table des paiements
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

-- Table des livraisons
CREATE TABLE deliveries (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    order_id BIGINT NOT NULL UNIQUE,
    agency_name VARCHAR(255) NOT NULL,
    external_tracking_id VARCHAR(255),
    tracking_url VARCHAR(255),
    status ENUM('REQUESTED','PICKED_UP','IN_TRANSIT','ASSIGNED','DELIVERED','FAILED') NOT NULL,
    current_lat DOUBLE,
    current_lng DOUBLE,
    estimated_arrival DATETIME,
    requested_at DATETIME NOT NULL,
    delivered_at DATETIME,
    FOREIGN KEY (order_id) REFERENCES pharmacy_orders(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Table des notifications
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

-- Table des notes de prescription
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
-- INDEX POUR OPTIMISER LES REQUÊTES
-- ============================================================================

CREATE INDEX idx_pharmacy_orders_patient ON pharmacy_orders(patient_id);
CREATE INDEX idx_pharmacy_orders_pharmacy ON pharmacy_orders(pharmacy_id);
CREATE INDEX idx_pharmacy_orders_status ON pharmacy_orders(status);
CREATE INDEX idx_order_tracking_order ON order_tracking(order_id);
CREATE INDEX idx_notifications_recipient ON notifications(recipient_id);
CREATE INDEX idx_notifications_order ON notifications(order_id);
CREATE INDEX idx_prescription_notes_order ON prescription_notes(order_id);

-- ============================================================================
-- DONNÉES D'EXEMPLE POUR TESTER
-- ============================================================================

-- Insérer des utilisateurs (Patients, Pharmaciens, Docteurs, Admin)
INSERT INTO users (full_name, email, password, role, phone, birth_date, enabled) VALUES
('Admin System', 'admin@pharmacy.com', '$2a$10$DoxEqDjCp5h5kLDCR5z3X.8KTdJNV.ZUZe/ZKuV5vPP.f0Nm2YpYe', 'ADMIN', '0612345678', '1990-01-15', TRUE),
('Dr. Mohamed Aziz', 'doctor@pharmacy.com', '$2a$10$DoxEqDjCp5h5kLDCR5z3X.8KTdJNV.ZUZe/ZKuV5vPP.f0Nm2YpYe', 'DOCTOR', '0622334455', '1985-03-20', TRUE),
('Patient Jean Dupont', 'jean.dupont@patient.com', '$2a$10$DoxEqDjCp5h5kLDCR5z3X.8KTdJNV.ZUZe/ZKuV5vPP.f0Nm2YpYe', 'PATIENT', '0633445566', '1992-07-10', TRUE),
('Patient Marie Martin', 'marie.martin@patient.com', '$2a$10$DoxEqDjCp5h5kLDCR5z3X.8KTdJNV.ZUZe/ZKuV5vPP.f0Nm2YpYe', 'PATIENT', '0644556677', '1988-11-25', TRUE),
('Pharmacien Ahmed Hassan', 'ahmed.hassan@pharmacy.com', '$2a$10$DoxEqDjCp5h5kLDCR5z3X.8KTdJNV.ZUZe/ZKuV5vPP.f0Nm2YpYe', 'PHARMACIST', '0655667788', '1987-05-12', TRUE),
('Pharmacien Fatima Zahra', 'fatima.zahra@pharmacy.com', '$2a$10$DoxEqDjCp5h5kLDCR5z3X.8KTdJNV.ZUZe/ZKuV5vPP.f0Nm2YpYe', 'PHARMACIST', '0666778899', '1991-09-18', TRUE);

-- Insérer des pharmacies
INSERT INTO pharmacies (name, address, location_lat, location_lng, phone_number, email) VALUES
('Pharmacie Centrale Casablanca', '123 Boulevard Mohammed V, Casablanca', 33.5731, -7.5898, '0522123456', 'central@pharmacy.com'),
('Pharmacie Maroc Fez', '456 Avenue Hassan II, Fez', 34.0333, -5.0000, '0535234567', 'fez@pharmacy.com'),
('Pharmacie Santé Plus Rabat', '789 Rue Moulay Hassan, Rabat', 34.0209, -6.8416, '0537345678', 'rabat@pharmacy.com');

-- Insérer des produits (médicaments)
INSERT INTO products (name, description, category, brand, manufacturer, barcode, type, unit) VALUES
('Paracétamol 500mg', 'Analgésique et antipyrétique', 'Analgésiques', 'Doliprane', 'Sanofi', '3400930040144', 'Comprimé', 'Boîte de 16'),
('Amoxicilline 500mg', 'Antibiotique bêta-lactamine', 'Antibiotiques', 'Clamoxyl', 'GSK', '3400930020033', 'Gélule', 'Boîte de 16'),
('Ibuprofène 400mg', 'Anti-inflammatoire non stéroïdien', 'Anti-inflammatoires', 'Nurofen', 'Reckitt', '3400930040151', 'Comprimé', 'Boîte de 20'),
('Vitamine C 1000mg', 'Complément vitaminique', 'Vitamines', 'Cébion', 'Merck', '3400930050222', 'Comprimé', 'Boîte de 20'),
('Oméprazole 20mg', 'Inhibiteur de la pompe à protons', 'Gastroentérologie', 'Mopral', 'AstraZeneca', '3400930060333', 'Gélule', 'Boîte de 14'),
('Metformine 500mg', 'Antidiabétique oral', 'Endocrinologie', 'Glucophage', 'Merck', '3400930070444', 'Comprimé', 'Boîte de 30'),
('Losartan 50mg', 'Antihypertenseur', 'Cardiologie', 'Cozaar', 'MSD', '3400930080555', 'Comprimé', 'Boîte de 30'),
('Atorvastatine 20mg', 'Hypolipidémiant', 'Cardiologie', 'Tahor', 'Pfizer', '3400930090666', 'Comprimé', 'Boîte de 30');

-- Insérer des stocks de pharmacie
INSERT INTO pharmacy_stocks (pharmacy_id, product_id, total_quantity, unit_price, min_quantity_threshold) VALUES
(1, 1, 500, 2.50, 50),
(1, 2, 300, 5.00, 30),
(1, 3, 400, 3.75, 40),
(1, 4, 200, 8.50, 20),
(1, 5, 150, 12.00, 15),
(1, 6, 100, 4.50, 10),
(1, 7, 120, 15.00, 12),
(1, 8, 80, 18.00, 8),
(2, 1, 300, 2.50, 30),
(2, 2, 250, 5.00, 25),
(2, 3, 350, 3.75, 35),
(3, 1, 400, 2.50, 40),
(3, 2, 200, 5.00, 20),
(3, 4, 250, 8.50, 25);

-- Insérer des consultations
INSERT INTO consultations (patient_id, doctor_id, consultation_date, diagnosis, notes) VALUES
(3, 2, '2026-03-10 09:30:00', 'Grippe saisonnière', 'Patient avec symptômes de grippe: fièvre, toux, fatigue'),
(4, 2, '2026-03-12 14:00:00', 'Trouble du sommeil avec anxiété', 'Recommande une consultation psychologique'),
(3, 2, '2026-03-15 10:15:00', 'Hypertension artérielle', 'Tension artérielle: 160/95 mmHg');

-- Insérer des prescriptions
INSERT INTO prescriptions (consultation_id, date_created) VALUES
(1, '2026-03-10'),
(2, '2026-03-12'),
(3, '2026-03-15');

-- Insérer des articles de prescription
INSERT INTO prescription_items (prescription_id, product_id, quantity, dosage, duration) VALUES
(1, 1, 1, '500mg', '5 jours'),
(1, 2, 1, '500mg', '7 jours'),
(2, 4, 1, '1000mg', '30 jours'),
(3, 7, 1, '50mg', '90 jours'),
(3, 6, 1, '500mg', '90 jours');

-- ============================================================================
-- DONNÉES D'EXEMPLE POUR COMMANDES DE PHARMACIE
-- ============================================================================

-- Créer des commandes de pharmacie
INSERT INTO pharmacy_orders (patient_id, pharmacy_id, prescription_id, status, total_price, delivery_address, delivery_type, scheduled_delivery_date, pharmacist_note, created_at, updated_at) VALUES
(3, 1, 1, 'PAID', 14.50, '100 Rue du Patient, Casablanca', 'HOME_DELIVERY', '2026-03-20', 'Prendre avec de la nourriture', '2026-03-16 08:00:00', '2026-03-16 10:30:00'),
(4, 2, 2, 'VALIDATED', 8.50, '200 Rue Marie, Fez', 'PICKUP', '2026-03-21', NULL, '2026-03-17 09:15:00', '2026-03-17 11:00:00'),
(3, 1, 3, 'PENDING', 48.00, '100 Rue du Patient, Casablanca', 'HOME_DELIVERY', '2026-03-22', NULL, '2026-03-18 07:30:00', '2026-03-18 07:30:00');

-- Insérer les articles de commande
INSERT INTO order_items (order_id, product_id, quantity, price) VALUES
(1, 1, 2, 5.00),
(1, 2, 1, 5.00),
(1, 4, 1, 4.50),
(2, 4, 1, 8.50),
(3, 7, 2, 30.00),
(3, 6, 2, 9.00),
(3, 8, 1, 9.00);

-- Insérer le suivi des commandes
INSERT INTO order_tracking (order_id, status, changed_by, changed_at, note) VALUES
(1, 'PENDING', 'SYSTEM', '2026-03-16 08:00:00', 'Commande créée'),
(1, 'REVIEWING', 'PHARMACIST_AHMED', '2026-03-16 08:15:00', 'Vérification de la prescription'),
(1, 'VALIDATED', 'PHARMACIST_AHMED', '2026-03-16 08:30:00', 'Prescription validée'),
(1, 'PAYMENT_PENDING', 'SYSTEM', '2026-03-16 08:45:00', 'Attente du paiement'),
(1, 'PAID', 'SYSTEM', '2026-03-16 10:30:00', 'Paiement reçu'),
(2, 'PENDING', 'SYSTEM', '2026-03-17 09:15:00', 'Commande créée'),
(2, 'REVIEWING', 'PHARMACIST_FATIMA', '2026-03-17 09:30:00', 'Vérification en cours'),
(2, 'VALIDATED', 'PHARMACIST_FATIMA', '2026-03-17 11:00:00', 'Prescription validée'),
(3, 'PENDING', 'SYSTEM', '2026-03-18 07:30:00', 'Commande créée');

-- Insérer les paiements
INSERT INTO payments (order_id, method, status, amount, transaction_id, created_at, paid_at) VALUES
(1, 'BANK_CARD', 'COMPLETED', 14.50, 'TXN20260316001', '2026-03-16 08:00:00', '2026-03-16 10:30:00'),
(2, 'CASH_ON_DELIVERY', 'PENDING', 8.50, NULL, '2026-03-17 09:15:00', NULL),
(3, 'STRIPE', 'PENDING', 48.00, NULL, '2026-03-18 07:30:00', NULL);

-- Insérer les livraisons
INSERT INTO deliveries (order_id, agency_name, external_tracking_id, tracking_url, status, current_lat, current_lng, estimated_arrival, requested_at, delivered_at) VALUES
(1, 'COLISSIMO', 'COL20260316001', 'https://tracking.colissimo.fr/COL20260316001', 'IN_TRANSIT', 33.5750, -7.5890, '2026-03-20 15:00:00', '2026-03-16 11:00:00', NULL);

-- Insérer les notifications
INSERT INTO notifications (recipient_id, order_id, title, message, type, is_read, created_at) VALUES
(3, 1, 'Commande confirmée', 'Votre commande #1 a été confirmée et payée', 'ORDER_VALIDATED', TRUE, '2026-03-16 10:30:00'),
(3, 1, 'Livraison en cours', 'Votre commande est en cours de livraison', 'OUT_FOR_DELIVERY', FALSE, '2026-03-19 08:00:00'),
(4, 2, 'Commande validée', 'Votre commande #2 est prête pour le retrait', 'ORDER_VALIDATED', FALSE, '2026-03-17 11:00:00'),
(3, 3, 'Commande reçue', 'Votre commande #3 a été reçue et est en cours de traitement', 'ORDER_CREATED', FALSE, '2026-03-18 07:35:00');

-- Insérer les notes de prescription
INSERT INTO prescription_notes (order_id, pharmacist_id, medication_name, dosage, duration, comment) VALUES
(1, 5, 'Paracétamol', '500mg', '5 jours', 'À prendre toutes les 4-6 heures si besoin'),
(1, 5, 'Amoxicilline', '500mg', '7 jours', 'Terminer le traitement complet même en cas d\'amélioration'),
(2, 6, 'Vitamine C', '1000mg', '30 jours', 'À prendre le matin avec un verre d\'eau');

-- ============================================================================
-- VÉRIFICATIONS FINALES
-- ============================================================================

-- Vérifier les tables créées
SHOW TABLES;

-- Vérifier les données
SELECT COUNT(*) as total_users FROM users;
SELECT COUNT(*) as total_pharmacies FROM pharmacies;
SELECT COUNT(*) as total_products FROM products;
SELECT COUNT(*) as total_orders FROM pharmacy_orders;
SELECT COUNT(*) as total_payments FROM payments;
SELECT COUNT(*) as total_deliveries FROM deliveries;
SELECT COUNT(*) as total_notifications FROM notifications;

-- Afficher un exemple de commande complète
SELECT
    po.id as order_id,
    u.full_name as patient_name,
    p.name as pharmacy_name,
    po.total_price,
    po.status,
    po.created_at
FROM pharmacy_orders po
JOIN users u ON po.patient_id = u.id
JOIN pharmacies p ON po.pharmacy_id = p.id
ORDER BY po.created_at DESC;

