-- ============================================================================
-- Données d'exemple pour Pharmacy Order Management
-- ============================================================================

-- ============================================================================
-- INSERT UTILISATEURS
-- ============================================================================
INSERT INTO users (full_name, email, password, role, phone, birth_date, enabled) VALUES
('Admin System', 'admin@pharmacy.com', '$2a$10$DoxEqDjCp5h5kLDCR5z3X.8KTdJNV.ZUZe/ZKuV5vPP.f0Nm2YpYe', 'ADMIN', '0612345678', '1990-01-15', TRUE),
('Dr. Mohamed Aziz', 'doctor@pharmacy.com', '$2a$10$DoxEqDjCp5h5kLDCR5z3X.8KTdJNV.ZUZe/ZKuV5vPP.f0Nm2YpYe', 'DOCTOR', '0622334455', '1985-03-20', TRUE),
('Patient Jean Dupont', 'jean.dupont@patient.com', '$2a$10$DoxEqDjCp5h5kLDCR5z3X.8KTdJNV.ZUZe/ZKuV5vPP.f0Nm2YpYe', 'PATIENT', '0633445566', '1992-07-10', TRUE),
('Patient Marie Martin', 'marie.martin@patient.com', '$2a$10$DoxEqDjCp5h5kLDCR5z3X.8KTdJNV.ZUZe/ZKuV5vPP.f0Nm2YpYe', 'PATIENT', '0644556677', '1988-11-25', TRUE),
('Pharmacien Ahmed Hassan', 'ahmed.hassan@pharmacy.com', '$2a$10$DoxEqDjCp5h5kLDCR5z3X.8KTdJNV.ZUZe/ZKuV5vPP.f0Nm2YpYe', 'PHARMACIST', '0655667788', '1987-05-12', TRUE),
('Pharmacien Fatima Zahra', 'fatima.zahra@pharmacy.com', '$2a$10$DoxEqDjCp5h5kLDCR5z3X.8KTdJNV.ZUZe/ZKuV5vPP.f0Nm2YpYe', 'PHARMACIST', '0666778899', '1991-09-18', TRUE);

INSERT INTO pharmacies (name, address, location_lat, location_lng, phone_number, email) VALUES
('Pharmacie Centrale Casablanca', '123 Boulevard Mohammed V, Casablanca', 33.5731, -7.5898, '0522123456', 'central@pharmacy.com'),
('Pharmacie Maroc Fez', '456 Avenue Hassan II, Fez', 34.0333, -5.0000, '0535234567', 'fez@pharmacy.com'),
('Pharmacie Santé Plus Rabat', '789 Rue Moulay Hassan, Rabat', 34.0209, -6.8416, '0537345678', 'rabat@pharmacy.com');

INSERT INTO patients (id, gender, blood_type) VALUES
(3, 'MALE', 'A_POS'),
(4, 'FEMALE', 'O_NEG');

INSERT INTO pharmacists (id, pharmacy_id) VALUES
(5, 1),
(6, 2);

INSERT INTO products (name, description, category, brand, manufacturer, barcode, type, unit) VALUES
('Paracétamol 500mg', 'Analgésique et antipyrétique', 'Analgésiques', 'Doliprane', 'Sanofi', '3400930040144', 'Comprimé', 'Boîte de 16'),
('Amoxicilline 500mg', 'Antibiotique bêta-lactamine', 'Antibiotiques', 'Clamoxyl', 'GSK', '3400930020033', 'Gélule', 'Boîte de 16'),
('Ibuprofène 400mg', 'Anti-inflammatoire non stéroïdien', 'Anti-inflammatoires', 'Nurofen', 'Reckitt', '3400930040151', 'Comprimé', 'Boîte de 20'),
('Vitamine C 1000mg', 'Complément vitaminique', 'Vitamines', 'Cébion', 'Merck', '3400930050222', 'Comprimé', 'Boîte de 20'),
('Oméprazole 20mg', 'Inhibiteur de la pompe à protons', 'Gastroentérologie', 'Mopral', 'AstraZeneca', '3400930060333', 'Gélule', 'Boîte de 14'),
('Metformine 500mg', 'Antidiabétique oral', 'Endocrinologie', 'Glucophage', 'Merck', '3400930070444', 'Comprimé', 'Boîte de 30'),
('Losartan 50mg', 'Antihypertenseur', 'Cardiologie', 'Cozaar', 'MSD', '3400930080555', 'Comprimé', 'Boîte de 30'),
('Atorvastatine 20mg', 'Hypolipidémiant', 'Cardiologie', 'Tahor', 'Pfizer', '3400930090666', 'Comprimé', 'Boîte de 30');

INSERT INTO pharmacy_stocks (pharmacy_id, product_id, total_quantity, unit_price, min_quantity_threshold) VALUES
(1, 1, 500, 2.50, 50), (1, 2, 300, 5.00, 30), (1, 3, 400, 3.75, 40), (1, 4, 200, 8.50, 20),
(1, 5, 150, 12.00, 15), (1, 6, 100, 4.50, 10), (1, 7, 120, 15.00, 12), (1, 8, 80, 18.00, 8),
(2, 1, 300, 2.50, 30), (2, 2, 250, 5.00, 25), (2, 3, 350, 3.75, 35),
(3, 1, 400, 2.50, 40), (3, 2, 200, 5.00, 20), (3, 4, 250, 8.50, 25);

INSERT INTO medical_records (patient_id) VALUES (3), (4);

INSERT INTO consultations (medical_record_id, doctor_id, date, observations, notes) VALUES
(1, 2, '2026-03-10 09:30:00', 'Patient avec symptômes de grippe', 'Fièvre, toux, fatigue'),
(2, 2, '2026-03-12 14:00:00', 'Trouble du sommeil avec anxiété', 'Recommande une consultation psychologique'),
(1, 2, '2026-03-15 10:15:00', 'Hypertension artérielle', 'Tension artérielle: 160/95 mmHg');

INSERT INTO prescriptions (consultation_id, date_created) VALUES (1, '2026-03-10'), (2, '2026-03-12'), (3, '2026-03-15');

INSERT INTO prescription_items (prescription_id, product_id, quantity, dosage, duration) VALUES
(1, 1, 1, '500mg', '5 jours'), (1, 2, 1, '500mg', '7 jours'), (2, 4, 1, '1000mg', '30 jours'),
(3, 7, 1, '50mg', '90 jours'), (3, 6, 1, '500mg', '90 jours');

INSERT INTO pharmacy_orders (patient_id, pharmacy_id, prescription_id, status, total_price, delivery_address, delivery_type, scheduled_delivery_date, created_at, updated_at) VALUES
(3, 1, 1, 'PAID', 14.50, '100 Rue du Patient, Casablanca', 'HOME_DELIVERY', '2026-03-20', '2026-03-16 08:00:00', '2026-03-16 10:30:00'),
(4, 2, 2, 'VALIDATED', 8.50, '200 Rue Marie, Fez', 'PICKUP', '2026-03-21', '2026-03-17 09:15:00', '2026-03-17 11:00:00'),
(3, 1, 3, 'PENDING', 48.00, '100 Rue du Patient, Casablanca', 'HOME_DELIVERY', '2026-03-22', '2026-03-18 07:30:00', '2026-03-18 07:30:00');

INSERT INTO order_items (order_id, product_id, quantity, price) VALUES
(1, 1, 2, 5.00), (1, 2, 1, 5.00), (1, 4, 1, 4.50), (2, 4, 1, 8.50),
(3, 7, 2, 30.00), (3, 6, 2, 9.00), (3, 8, 1, 9.00);

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

INSERT INTO payments (order_id, method, status, amount, transaction_id, created_at, paid_at) VALUES
(1, 'BANK_CARD', 'COMPLETED', 14.50, 'TXN20260316001', '2026-03-16 08:00:00', '2026-03-16 10:30:00'),
(2, 'CASH_ON_DELIVERY', 'PENDING', 8.50, NULL, '2026-03-17 09:15:00', NULL),
(3, 'STRIPE', 'PENDING', 48.00, NULL, '2026-03-18 07:30:00', NULL);

INSERT INTO deliveries (order_id, agency_name, external_tracking_id, tracking_url, status, current_lat, current_lng, estimated_arrival, requested_at, delivered_at) VALUES
(1, 'COLISSIMO', 'COL20260316001', 'https://tracking.colissimo.fr/COL20260316001', 'IN_TRANSIT', 33.5750, -7.5890, '2026-03-20 15:00:00', '2026-03-16 11:00:00', NULL);

INSERT INTO notifications (recipient_id, order_id, title, message, type, is_read, created_at) VALUES
(3, 1, 'Commande confirmée', 'Votre commande #1 a été confirmée et payée', 'ORDER_VALIDATED', TRUE, '2026-03-16 10:30:00'),
(3, 1, 'Livraison en cours', 'Votre commande est en cours de livraison', 'OUT_FOR_DELIVERY', FALSE, '2026-03-19 08:00:00'),
(4, 2, 'Commande validée', 'Votre commande #2 est prête pour le retrait', 'ORDER_VALIDATED', FALSE, '2026-03-17 11:00:00'),
(3, 3, 'Commande reçue', 'Votre commande #3 a été reçue et est en cours de traitement', 'ORDER_CREATED', FALSE, '2026-03-18 07:35:00');

INSERT INTO prescription_notes (order_id, pharmacist_id, medication_name, dosage, duration, comment) VALUES
(1, 5, 'Paracétamol', '500mg', '5 jours', 'À prendre toutes les 4-6 heures si besoin'),
(1, 5, 'Amoxicilline', '500mg', '7 jours', 'Terminer le traitement complet même en cas d\'amélioration'),
(2, 6, 'Vitamine C', '1000mg', '30 jours', 'À prendre le matin avec un verre d\'eau');

-- Delivery Agencies (Simulation)
DELETE FROM delivery_agents;
DELETE FROM delivery_agencies;

INSERT INTO delivery_agencies (id, name, phone_number, city) VALUES (1, 'Aramex Tunisia', '+216 71 000 000', 'Tunis');
INSERT INTO delivery_agencies (id, name, phone_number, city) VALUES (2, 'Intigo', '+216 71 111 111', 'Tunis');
INSERT INTO delivery_agencies (id, name, phone_number, city) VALUES (3, 'Rapid-Poste', '+216 1820', 'Tunis');

-- Delivery Agents
INSERT INTO delivery_agents (id, name, phone_number, vehicle_type, status, agency_id) VALUES (1, 'Ahmed Livreur', '+216 99 123 456', 'MOTORCYCLE', 'AVAILABLE', 1);
INSERT INTO delivery_agents (id, name, phone_number, vehicle_type, status, agency_id) VALUES (2, 'Mohamed Aramex', '+216 98 765 432', 'CAR', 'AVAILABLE', 1);
INSERT INTO delivery_agents (id, name, phone_number, vehicle_type, status, agency_id) VALUES (3, 'Sami Intigo', '+216 97 111 222', 'MOTORCYCLE', 'AVAILABLE', 2);
INSERT INTO delivery_agents (id, name, phone_number, vehicle_type, status, agency_id) VALUES (4, 'Postier Tunis', '+216 71 333 444', 'BICYCLE', 'AVAILABLE', 3);


