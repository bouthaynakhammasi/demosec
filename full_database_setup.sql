-- ============================================================================
-- SCRIPT COMPLET DE CRÉATION DE LA BASE DE DONNÉES
-- Pharmacy Order Management System
-- ============================================================================

-- Désactiver les vérifications de clés étrangères pendant la création
SET FOREIGN_KEY_CHECKS=0;
SET AUTOCOMMIT=0;

-- ============================================================================
-- 1. SUPPRIMER LA BASE EXISTANTE
-- ============================================================================
DROP DATABASE IF EXISTS demospringsecurity;

-- ============================================================================
-- 2. CRÉER LA BASE DE DONNÉES
-- ============================================================================
CREATE DATABASE demospringsecurity
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

-- Utiliser la base de données
USE demospringsecurity;

-- ============================================================================
-- 3. CRÉER LES TABLES (DANS LE BON ORDRE)
-- ============================================================================

-- TABLE 1: USERS (table de base)
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

-- TABLE 2: PHARMACIES
CREATE TABLE pharmacies (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    address VARCHAR(255),
    location_lat DOUBLE,
    location_lng DOUBLE,
    phone_number VARCHAR(20),
    email VARCHAR(255)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- TABLE 3: PRODUCTS
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

-- TABLE 4: PHARMACY_STOCKS
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

-- TABLE 5: MEDICAL_RECORDS
CREATE TABLE medical_records (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    patient_id BIGINT NOT NULL UNIQUE,
    FOREIGN KEY (patient_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- TABLE 6: CONSULTATIONS
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

-- TABLE 7: PRESCRIPTIONS
CREATE TABLE prescriptions (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    consultation_id BIGINT NOT NULL,
    date_created DATE NOT NULL,
    FOREIGN KEY (consultation_id) REFERENCES consultations(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- TABLE 8: PRESCRIPTION_ITEMS
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

-- TABLE 9: PHARMACY_ORDERS (TABLE PRINCIPALE)
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

-- TABLE 10: ORDER_ITEMS
CREATE TABLE order_items (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    order_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    quantity INTEGER NOT NULL,
    price DECIMAL(12, 2) NOT NULL,
    FOREIGN KEY (order_id) REFERENCES pharmacy_orders(id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- TABLE 11: ORDER_TRACKING
CREATE TABLE order_tracking (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    order_id BIGINT NOT NULL,
    status ENUM('PENDING','REVIEWING','VALIDATED','PAYMENT_PENDING','PAID','AWAITING_CHOICE','ASSIGNING','ASSIGNED','RESERVED','READY_FOR_PICKUP','DELIVERY_REQUESTED','OUT_FOR_DELIVERY','PICKED_UP','DELIVERED','CANCELLED') NOT NULL,
    changed_by VARCHAR(255) NOT NULL,
    changed_at DATETIME NOT NULL,
    note VARCHAR(255),
    FOREIGN KEY (order_id) REFERENCES pharmacy_orders(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- TABLE 12: PAYMENTS
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

-- TABLE 13: DELIVERIES
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

-- TABLE 14: NOTIFICATIONS
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

-- TABLE 15: PRESCRIPTION_NOTES
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
-- 4. CRÉER LES INDEX
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
-- 5. INSÉRER LES UTILISATEURS
-- ============================================================================
INSERT INTO users (id, full_name, email, password, role, phone, birth_date, enabled) VALUES
(1, 'Admin System', 'admin@pharmacy.com', '$2a$10$DoxEqDjCp5h5kLDCR5z3X.8KTdJNV.ZUZe/ZKuV5vPP.f0Nm2YpYe', 'ADMIN', '0612345678', '1990-01-15', TRUE),
(2, 'Dr. Mohamed Aziz', 'doctor@pharmacy.com', '$2a$10$DoxEqDjCp5h5kLDCR5z3X.8KTdJNV.ZUZe/ZKuV5vPP.f0Nm2YpYe', 'DOCTOR', '0622334455', '1985-03-20', TRUE),
(3, 'Patient Jean Dupont', 'jean.dupont@patient.com', '$2a$10$DoxEqDjCp5h5kLDCR5z3X.8KTdJNV.ZUZe/ZKuV5vPP.f0Nm2YpYe', 'PATIENT', '0633445566', '1992-07-10', TRUE),
(4, 'Patient Marie Martin', 'marie.martin@patient.com', '$2a$10$DoxEqDjCp5h5kLDCR5z3X.8KTdJNV.ZUZe/ZKuV5vPP.f0Nm2YpYe', 'PATIENT', '0644556677', '1988-11-25', TRUE),
(5, 'Pharmacien Ahmed Hassan', 'ahmed.hassan@pharmacy.com', '$2a$10$DoxEqDjCp5h5kLDCR5z3X.8KTdJNV.ZUZe/ZKuV5vPP.f0Nm2YpYe', 'PHARMACIST', '0655667788', '1987-05-12', TRUE),
(6, 'Pharmacien Fatima Zahra', 'fatima.zahra@pharmacy.com', '$2a$10$DoxEqDjCp5h5kLDCR5z3X.8KTdJNV.ZUZe/ZKuV5vPP.f0Nm2YpYe', 'PHARMACIST', '0666778899', '1991-09-18', TRUE);

-- ============================================================================
-- 6. INSÉRER LES PHARMACIES
-- ============================================================================
INSERT INTO pharmacies (id, name, address, location_lat, location_lng, phone_number, email) VALUES
(1, 'Pharmacie Centrale Casablanca', '123 Boulevard Mohammed V, Casablanca', 33.5731, -7.5898, '0522123456', 'central@pharmacy.com'),
(2, 'Pharmacie Maroc Fez', '456 Avenue Hassan II, Fez', 34.0333, -5.0000, '0535234567', 'fez@pharmacy.com'),
(3, 'Pharmacie Santé Plus Rabat', '789 Rue Moulay Hassan, Rabat', 34.0209, -6.8416, '0537345678', 'rabat@pharmacy.com');

-- ============================================================================
-- 7. INSÉRER LES PRODUITS
-- ============================================================================
INSERT INTO products (id, name, description, category, brand, manufacturer, barcode, type, unit) VALUES
(1, 'Paracétamol 500mg', 'Analgésique et antipyrétique', 'Analgésiques', 'Doliprane', 'Sanofi', '3400930040144', 'Comprimé', 'Boîte de 16'),
(2, 'Amoxicilline 500mg', 'Antibiotique bêta-lactamine', 'Antibiotiques', 'Clamoxyl', 'GSK', '3400930020033', 'Gélule', 'Boîte de 16'),
(3, 'Ibuprofène 400mg', 'Anti-inflammatoire non stéroïdien', 'Anti-inflammatoires', 'Nurofen', 'Reckitt', '3400930040151', 'Comprimé', 'Boîte de 20'),
(4, 'Vitamine C 1000mg', 'Complément vitaminique', 'Vitamines', 'Cébion', 'Merck', '3400930050222', 'Comprimé', 'Boîte de 20'),
(5, 'Oméprazole 20mg', 'Inhibiteur de la pompe à protons', 'Gastroentérologie', 'Mopral', 'AstraZeneca', '3400930060333', 'Gélule', 'Boîte de 14'),
(6, 'Metformine 500mg', 'Antidiabétique oral', 'Endocrinologie', 'Glucophage', 'Merck', '3400930070444', 'Comprimé', 'Boîte de 30'),
(7, 'Losartan 50mg', 'Antihypertenseur', 'Cardiologie', 'Cozaar', 'MSD', '3400930080555', 'Comprimé', 'Boîte de 30'),
(8, 'Atorvastatine 20mg', 'Hypolipidémiant', 'Cardiologie', 'Tahor', 'Pfizer', '3400930090666', 'Comprimé', 'Boîte de 30');

-- ============================================================================
-- 8. INSÉRER LES STOCKS DE PHARMACIE
-- ============================================================================
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

-- ============================================================================
-- 9. INSÉRER LES DOSSIERS MÉDICAUX
-- ============================================================================
INSERT INTO medical_records (id, patient_id) VALUES
(1, 3),
(2, 4);

-- ============================================================================
-- 10. INSÉRER LES CONSULTATIONS
-- ============================================================================
INSERT INTO consultations (id, medical_record_id, doctor_id, date, observations, notes) VALUES
(1, 1, 2, '2026-03-10 09:30:00', 'Patient avec symptômes de grippe', 'Fièvre, toux, fatigue'),
(2, 2, 2, '2026-03-12 14:00:00', 'Trouble du sommeil avec anxiété', 'Recommande une consultation psychologique'),
(3, 1, 2, '2026-03-15 10:15:00', 'Hypertension artérielle', 'Tension artérielle: 160/95 mmHg');

-- ============================================================================
-- 11. INSÉRER LES PRESCRIPTIONS
-- ============================================================================
INSERT INTO prescriptions (id, consultation_id, date_created) VALUES
(1, 1, '2026-03-10'),
(2, 2, '2026-03-12'),
(3, 3, '2026-03-15');

-- ============================================================================
-- 12. INSÉRER LES ARTICLES DE PRESCRIPTION
-- ============================================================================
INSERT INTO prescription_items (prescription_id, product_id, quantity, dosage, duration) VALUES
(1, 1, 1, '500mg', '5 jours'),
(1, 2, 1, '500mg', '7 jours'),
(2, 4, 1, '1000mg', '30 jours'),
(3, 7, 1, '50mg', '90 jours'),
(3, 6, 1, '500mg', '90 jours');

-- ============================================================================
-- 13. INSÉRER LES COMMANDES DE PHARMACIE
-- ============================================================================
INSERT INTO pharmacy_orders (id, patient_id, pharmacy_id, prescription_id, status, total_price, delivery_address, delivery_type, scheduled_delivery_date, pharmacist_note, created_at, updated_at) VALUES
(1, 3, 1, 1, 'PAID', 14.50, '100 Rue du Patient, Casablanca', 'HOME_DELIVERY', '2026-03-20', 'Prendre avec de la nourriture', '2026-03-16 08:00:00', '2026-03-16 10:30:00'),
(2, 4, 2, 2, 'VALIDATED', 8.50, '200 Rue Marie, Fez', 'PICKUP', '2026-03-21', NULL, '2026-03-17 09:15:00', '2026-03-17 11:00:00'),
(3, 3, 1, 3, 'PENDING', 48.00, '100 Rue du Patient, Casablanca', 'HOME_DELIVERY', '2026-03-22', NULL, '2026-03-18 07:30:00', '2026-03-18 07:30:00');

-- ============================================================================
-- 14. INSÉRER LES ARTICLES DE COMMANDE
-- ============================================================================
INSERT INTO order_items (order_id, product_id, quantity, price) VALUES
(1, 1, 2, 5.00),
(1, 2, 1, 5.00),
(1, 4, 1, 4.50),
(2, 4, 1, 8.50),
(3, 7, 2, 30.00),
(3, 6, 2, 9.00),
(3, 8, 1, 9.00);

-- ============================================================================
-- 15. INSÉRER LE SUIVI DES COMMANDES
-- ============================================================================
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

-- ============================================================================
-- 16. INSÉRER LES PAIEMENTS
-- ============================================================================
INSERT INTO payments (order_id, method, status, amount, transaction_id, created_at, paid_at) VALUES
(1, 'BANK_CARD', 'COMPLETED', 14.50, 'TXN20260316001', '2026-03-16 08:00:00', '2026-03-16 10:30:00'),
(2, 'CASH_ON_DELIVERY', 'PENDING', 8.50, NULL, '2026-03-17 09:15:00', NULL),
(3, 'STRIPE', 'PENDING', 48.00, NULL, '2026-03-18 07:30:00', NULL);

-- ============================================================================
-- 17. INSÉRER LES LIVRAISONS
-- ============================================================================
INSERT INTO deliveries (order_id, agency_name, external_tracking_id, tracking_url, status, current_lat, current_lng, estimated_arrival, requested_at, delivered_at) VALUES
(1, 'COLISSIMO', 'COL20260316001', 'https://tracking.colissimo.fr/COL20260316001', 'IN_TRANSIT', 33.5750, -7.5890, '2026-03-20 15:00:00', '2026-03-16 11:00:00', NULL);

-- ============================================================================
-- 18. INSÉRER LES NOTIFICATIONS
-- ============================================================================
INSERT INTO notifications (recipient_id, order_id, title, message, type, is_read, created_at) VALUES
(3, 1, 'Commande confirmée', 'Votre commande #1 a été confirmée et payée', 'ORDER_VALIDATED', TRUE, '2026-03-16 10:30:00'),
(3, 1, 'Livraison en cours', 'Votre commande est en cours de livraison', 'OUT_FOR_DELIVERY', FALSE, '2026-03-19 08:00:00'),
(4, 2, 'Commande validée', 'Votre commande #2 est prête pour le retrait', 'ORDER_VALIDATED', FALSE, '2026-03-17 11:00:00'),
(3, 3, 'Commande reçue', 'Votre commande #3 a été reçue et est en cours de traitement', 'ORDER_CREATED', FALSE, '2026-03-18 07:35:00');

-- ============================================================================
-- 19. INSÉRER LES NOTES DE PRESCRIPTION
-- ============================================================================
INSERT INTO prescription_notes (order_id, pharmacist_id, medication_name, dosage, duration, comment) VALUES
(1, 5, 'Paracétamol', '500mg', '5 jours', 'À prendre toutes les 4-6 heures si besoin'),
(1, 5, 'Amoxicilline', '500mg', '7 jours', 'Terminer le traitement complet même en cas d\'amélioration'),
(2, 6, 'Vitamine C', '1000mg', '30 jours', 'À prendre le matin avec un verre d\'eau');

-- ============================================================================
-- 20. RÉACTIVER LES VÉRIFICATIONS DE CLÉS ÉTRANGÈRES
-- ============================================================================
COMMIT;
SET FOREIGN_KEY_CHECKS=1;
SET AUTOCOMMIT=1;

-- ============================================================================
-- 21. VÉRIFICATIONS FINALES
-- ============================================================================
SELECT '✓ Base de données créée avec succès!' as Status;

SELECT
    CONCAT('✓ Table: ', TABLE_NAME, ' - ', TABLE_ROWS, ' lignes') as Vérification
FROM information_schema.tables
WHERE table_schema = 'demospringsecurity'
ORDER BY TABLE_NAME;

-- Afficher un résumé
SELECT '=== RÉSUMÉ DES DONNÉES ===' as Résumé
UNION ALL
SELECT CONCAT('Utilisateurs: ', COUNT(*)) FROM users
UNION ALL
SELECT CONCAT('Pharmacies: ', COUNT(*)) FROM pharmacies
UNION ALL
SELECT CONCAT('Produits: ', COUNT(*)) FROM products
UNION ALL
SELECT CONCAT('Stocks: ', COUNT(*)) FROM pharmacy_stocks
UNION ALL
SELECT CONCAT('Commandes: ', COUNT(*)) FROM pharmacy_orders
UNION ALL
SELECT CONCAT('Paiements: ', COUNT(*)) FROM payments
UNION ALL
SELECT CONCAT('Livraisons: ', COUNT(*)) FROM deliveries
UNION ALL
SELECT CONCAT('Notifications: ', COUNT(*)) FROM notifications;

