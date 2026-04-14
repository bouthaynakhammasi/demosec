-- ============================================================================
-- PHARMACY ORDER MANAGEMENT - TEST DATA GENERATION SCRIPT
-- ============================================================================
-- This script fills the database with comprehensive test data for the 
-- Pharmacy Order Management system
-- ============================================================================

-- Clear existing data (in correct order to avoid foreign key violations)
-- Uncomment if you want to clear existing data first:
-- SET FOREIGN_KEY_CHECKS=0;
-- TRUNCATE TABLE order_tracking;
-- TRUNCATE TABLE order_items;
-- TRUNCATE TABLE pharmacy_orders;
-- TRUNCATE TABLE prescription_items;
-- TRUNCATE TABLE prescriptions;
-- TRUNCATE TABLE consultations;
-- TRUNCATE TABLE medical_records;
-- TRUNCATE TABLE patients;
-- TRUNCATE TABLE pharmacists;
-- TRUNCATE TABLE pharmacies;
-- TRUNCATE TABLE products;
-- TRUNCATE TABLE users;
-- SET FOREIGN_KEY_CHECKS=1;

-- ============================================================================
-- 1. INSERT BASE USERS (Doctors)
-- ============================================================================
INSERT INTO users (full_name, email, password, role, phone, birth_date, enabled, dtype) VALUES
('Dr. Ahmed Safi', 'ahmed.safi@medecin.ma', '$2a$10$dXJ3SW6G7P50eS3q0/Ptau6sNuJ3xW7JkPLFVgI2Qr.sA7mAlB05a', 'DOCTOR', '+212612345678', '1975-03-15', true, 'User'),
('Dr. Fatima Bennani', 'fatima.bennani@medecin.ma', '$2a$10$dXJ3SW6G7P50eS3q0/Ptau6sNuJ3xW7JkPLFVgI2Qr.sA7mAlB05a', 'DOCTOR', '+212612345679', '1980-07-20', true, 'User');

-- ============================================================================
-- 2. INSERT PATIENTS
-- ============================================================================
INSERT INTO patients (full_name, email, password, role, phone, birth_date, enabled, dtype, gender, blood_type, emergency_contact_name, emergency_contact_phone, chronic_diseases, drug_allergies, hereditary_diseases) VALUES
('Mohamed Hassan', 'hassan.patient@gmail.com', '$2a$10$dXJ3SW6G7P50eS3q0/Ptau6sNuJ3xW7JkPLFVgI2Qr.sA7mAlB05a', 'PATIENT', '+212612345680', '1990-01-15', true, 'Patient', 'MALE', 'O_POS', 'Amina Hassan', '+212612345681', 'Diabetes', 'Penicillin', 'Hypertension'),
('Fatima Al-Rashid', 'fatima.rashid@gmail.com', '$2a$10$dXJ3SW6G7P50eS3q0/Ptau6sNuJ3xW7JkPLFVgI2Qr.sA7mAlB05a', 'PATIENT', '+212612345682', '1985-05-22', true, 'Patient', 'FEMALE', 'A_POS', 'Mohammed Al-Rashid', '+212612345683', 'Asthma', 'Aspirin', NULL),
('Youssef Bennani', 'youssef.bennani@gmail.com', '$2a$10$dXJ3SW6G7P50eS3q0/Ptau6sNuJ3xW7JkPLFVgI2Qr.sA7mAlB05a', 'PATIENT', '+212612345684', '1988-09-10', true, 'Patient', 'MALE', 'B_POS', 'Zainab Bennani', '+212612345685', NULL, 'Sulfalonamides', 'Coronary artery disease'),
('Leila El-Mansouri', 'leila.mansouri@gmail.com', '$2a$10$dXJ3SW6G7P50eS3q0/Ptau6sNuJ3xW7JkPLFVgI2Qr.sA7mAlB05a', 'PATIENT', '+212612345686', '1992-11-30', true, 'Patient', 'FEMALE', 'AB_NEG', 'Hassan El-Mansouri', '+212612345687', 'Thyroid disease', NULL, NULL),
('Karim Mabrouk', 'karim.mabrouk@gmail.com', '$2a$10$dXJ3SW6G7P50eS3q0/Ptau6sNuJ3xW7JkPLFVgI2Qr.sA7mAlB05a', 'PATIENT', '+212612345688', '1995-02-14', true, 'Patient', 'MALE', 'O_NEG', 'Amal Mabrouk', '+212612345689', 'Hypertension', 'NSAIDs', NULL);

-- ============================================================================
-- 3. INSERT PHARMACIES
-- ============================================================================
INSERT INTO pharmacies (name, address, location_lat, location_lng, phone_number, email) VALUES
('Pharmacie Centrale Casablanca', '123 Avenue Hassan II, Casablanca', 33.5731, -7.5898, '+212522123456', 'central@pharmacy.ma'),
('Pharmacie Al-Noor Rabat', '456 Rue Mohammed V, Rabat', 34.0209, -6.8416, '+212537234567', 'alnoor@pharmacy.ma'),
('Pharmacie Al-Shifa Fes', '789 Boulevard Moulay Youssef, Fes', 34.0635, -5.0077, '+212535345678', 'shifa@pharmacy.ma'),
('Pharmacie Essaada Marrakech', '321 Jemaa El Fna Street, Marrakech', 31.6295, -8.0075, '+212524456789', 'essaada@pharmacy.ma'),
('Pharmacie Al-Hana Tangier', '654 Boulevard Mohammed VI, Tangier', 35.7595, -5.8140, '+212539567890', 'alhana@pharmacy.ma');

-- ============================================================================
-- 4. INSERT PHARMACISTS
-- ============================================================================
INSERT INTO pharmacists (full_name, email, password, role, phone, birth_date, enabled, dtype, pharmacy_id) VALUES
('Samir El-Alami', 'samir.alamie@pharmacy.ma', '$2a$10$dXJ3SW6G7P50eS3q0/Ptau6sNuJ3xW7JkPLFVgI2Qr.sA7mAlB05a', 'PHARMACIST', '+212612345690', '1980-04-18', true, 'Pharmacist', 1),
('Hana Boutaleb', 'hana.boutaleb@pharmacy.ma', '$2a$10$dXJ3SW6G7P50eS3q0/Ptau6sNuJ3xW7JkPLFVgI2Qr.sA7mAlB05a', 'PHARMACIST', '+212612345691', '1985-08-25', true, 'Pharmacist', 1),
('Ibrahim Zahra', 'ibrahim.zahra@pharmacy.ma', '$2a$10$dXJ3SW6G7P50eS3q0/Ptau6sNuJ3xW7JkPLFVgI2Qr.sA7mAlB05a', 'PHARMACIST', '+212612345692', '1982-12-08', true, 'Pharmacist', 2),
('Noureddine Caid', 'noureddine.caid@pharmacy.ma', '$2a$10$dXJ3SW6G7P50eS3q0/Ptau6sNuJ3xW7JkPLFVgI2Qr.sA7mAlB05a', 'PHARMACIST', '+212612345693', '1987-06-12', true, 'Pharmacist', 3),
('Hanae Khouni', 'hanae.khouni@pharmacy.ma', '$2a$10$dXJ3SW6G7P50eS3q0/Ptau6sNuJ3xW7JkPLFVgI2Qr.sA7mAlB05a', 'PHARMACIST', '+212612345694', '1989-10-30', true, 'Pharmacist', 4);

-- ============================================================================
-- 5. INSERT PRODUCTS (Medications & Health Products)
-- ============================================================================
INSERT INTO products (name, description, image_url, manufacturer, brand, category, type, barcode, unit) VALUES
('Amoxicillin 500mg', 'Antibiotic capsule', NULL, 'Sanofi', 'Amoxicilline', 'Antibiotics', 'MEDICATION', '9785456221033', 'BOX'),
('Ibuprofen 200mg', 'Pain reliever and anti-inflammatory', NULL, 'Novartis', 'Nurofen', 'Pain Relief', 'MEDICATION', '9785456221034', 'BOX'),
('Paracetamol 500mg', 'Pain reliever and fever reducer', NULL, 'GlaxoSmithKline', 'Doliprane', 'Pain Relief', 'MEDICATION', '9785456221035', 'BOX'),
('Ranitidine 150mg', 'Acid reflux treatment', NULL, 'Astellas', 'Zantac', 'Gastric', 'MEDICATION', '9785456221036', 'BOX'),
('Metformin 1000mg', 'Diabetes management', NULL, 'Merck KGaA', 'Glucophage', 'Endocrinology', 'MEDICATION', '9785456221037', 'BOX'),
('Lisinopril 10mg', 'Blood pressure control', NULL, 'Apitech', 'Zestril', 'Cardiovascular', 'MEDICATION', '9785456221038', 'BOX'),
('Vitamin C 1000mg', 'Immune system supplement', NULL, 'BigChem', 'VitaC Plus', 'Supplements', 'SUPPLEMENT', '9785456221039', 'BOTTLE'),
('Vitamin D3 2000 IU', 'Calcium absorption support', NULL, 'Pharma Lab', 'Vitamin Dream', 'Supplements', 'SUPPLEMENT', '9785456221040', 'BOTTLE'),
('Dexpanthenol Cream 5%', 'Skin healing cream', NULL, 'Bayer', 'Bepanthen', 'Dermatology', 'PARAPHARMA', '9785456221041', 'TUBE'),
('Antiseptic Hand Gel', 'Hand sanitizer 70% alcohol', NULL, 'Hygiene Co', 'SafeHand', 'Hygiene', 'HYGIENE', '9785456221042', 'BOTTLE'),
('Digital Thermometer', 'Quick temperature measurement', NULL, 'TempTech', 'QuickTemp', 'Medical Devices', 'DEVICE', '9785456221043', 'PIECE'),
('Blood Pressure Monitor', 'Automatic BP cuff', NULL, 'CardioHealth', 'CardioMate', 'Medical Devices', 'DEVICE', '9785456221044', 'PIECE'),
('Aspirin 100mg', 'Antiplatelet therapy', NULL, 'Bayer', 'Aspro', 'Cardiovascular', 'MEDICATION', '9785456221045', 'BOX'),
('Cetirizine 10mg', 'Antihistamine for allergies', NULL, 'UCB', 'Piriteze', 'Allergies', 'MEDICATION', '9785456221046', 'BOX'),
('Omeprazole 20mg', 'Proton pump inhibitor', NULL, 'AstraZeneca', 'Prilosec', 'Gastric', 'MEDICATION', '9785456221047', 'BOX');

-- ============================================================================
-- 6. INSERT MEDICAL RECORDS
-- ============================================================================
INSERT INTO medical_records (patient_id) VALUES
(3),  -- Mohamed Hassan
(4),  -- Fatima Al-Rashid
(5),  -- Youssef Bennani
(6),  -- Leila El-Mansouri
(7);  -- Karim Mabrouk

-- ============================================================================
-- 7. INSERT CONSULTATIONS
-- ============================================================================
INSERT INTO consultations (medical_record_id, doctor_id, date, observations, notes) VALUES
(1, 1, '2025-03-10 09:30:00', 'Patient has symptoms of diabetes', 'Need to prescribe medications for blood sugar control'),
(2, 2, '2025-03-11 10:15:00', 'Patient complains of asthma flare-ups', 'Review medication compliance'),
(3, 1, '2025-03-12 14:00:00', 'Routine hypertension check-up', 'Blood pressure is controlled with current medication'),
(4, 2, '2025-03-13 11:30:00', 'Patient has thyroid issues', 'Thyroid function tests needed'),
(5, 1, '2025-03-14 15:45:00', 'Follow-up visit for hypertension', 'Continue current treatment plan');

-- ============================================================================
-- 8. INSERT PRESCRIPTIONS
-- ============================================================================
INSERT INTO prescriptions (consultation_id, date) VALUES
(1, '2025-03-10'),
(2, '2025-03-11'),
(3, '2025-03-12'),
(4, '2025-03-13'),
(5, '2025-03-14');

-- ============================================================================
-- 9. INSERT PRESCRIPTION ITEMS
-- ============================================================================
INSERT INTO prescription_items (prescription_id, medication_name, dosage, frequency, duration) VALUES
(1, 'Metformin', '1000mg', 'Twice daily', '90 days'),
(1, 'Lisinopril', '10mg', 'Once daily', '90 days'),
(2, 'Cetirizine', '10mg', 'Once daily as needed', '30 days'),
(3, 'Aspirin', '100mg', 'Once daily', '90 days'),
(3, 'Omeprazole', '20mg', 'Once daily morning', '60 days'),
(4, 'Ibuprofen', '200mg', 'Three times daily with meals', '14 days'),
(5, 'Lisinopril', '10mg', 'Once daily', '90 days'),
(5, 'Aspirin', '100mg', 'Once daily', '90 days');

-- ============================================================================
-- 10. INSERT PHARMACY ORDERS
-- ============================================================================
INSERT INTO pharmacy_orders (patient_id, pharmacy_id, prescription_id, status, total_price, delivery_address, scheduled_delivery_date, created_at, updated_at, pharmacist_note, delivery_type) VALUES
(3, 1, 1, 'DELIVERED', 450.00, '123 Rue de Fez, Casablanca', '2025-03-15', '2025-03-10 10:00:00', '2025-03-15 16:30:00', 'Order delivered successfully', 'HOME_DELIVERY'),
(4, 1, 2, 'PAID', 320.00, '456 Avenue Hassan II, Casablanca', '2025-03-16', '2025-03-11 11:00:00', '2025-03-12 09:15:00', NULL, 'PICKUP'),
(5, 2, 3, 'OUT_FOR_DELIVERY', 580.50, '789 Boulevard Moulay Youssef, Rabat', '2025-03-17', '2025-03-12 14:30:00', '2025-03-14 10:00:00', 'Package on the way', 'HOME_DELIVERY'),
(6, 3, 4, 'READY_FOR_PICKUP', 275.00, '321 Jemaa El Fna Street, Marrakech', '2025-03-18', '2025-03-13 09:00:00', '2025-03-14 11:30:00', NULL, 'PICKUP'),
(7, 4, 5, 'PENDING', 650.00, '654 Boulevard Mohammed VI, Tangier', NULL, '2025-03-14 16:00:00', '2025-03-14 16:00:00', NULL, 'HOME_DELIVERY'),
(3, 2, NULL, 'REVIEWED', 380.75, '123 Rue de Fez, Casablanca', '2025-03-19', '2025-03-15 10:30:00', '2025-03-15 14:00:00', 'Pharmacist reviewed order', 'HOME_DELIVERY'),
(4, 3, 2, 'AWAITING_CHOICE', 420.00, '456 Avenue Hassan II, Casablanca', NULL, '2025-03-15 13:00:00', '2025-03-15 13:00:00', NULL, 'PICKUP'),
(5, 1, 3, 'CANCELLED', 180.00, '789 Boulevard Moulay Youssef, Rabat', NULL, '2025-03-16 08:00:00', '2025-03-16 12:30:00', 'Cancelled by patient', 'HOME_DELIVERY');

-- ============================================================================
-- 11. INSERT ORDER ITEMS
-- ============================================================================
INSERT INTO order_items (order_id, product_id, quantity, price) VALUES
-- Order 1 (Mohamed Hassan - Delivered)
(1, 5, 2, 150.00),    -- Metformin 1000mg - 2 boxes
(1, 6, 1, 120.00),    -- Lisinopril 10mg - 1 box
(1, 9, 1, 80.00),     -- Dexpanthenol Cream - 1 tube
(1, 7, 2, 100.00),    -- Vitamin C - 2 bottles

-- Order 2 (Fatima Al-Rashid - Paid)
(2, 14, 1, 85.00),    -- Cetirizine 10mg - 1 box
(2, 12, 1, 235.00),   -- Blood Pressure Monitor - 1 piece

-- Order 3 (Youssef Bennani - Out for Delivery)
(3, 13, 1, 45.50),    -- Aspirin 100mg - 1 box
(3, 15, 1, 95.00),    -- Omeprazole 20mg - 1 box
(3, 2, 1, 75.00),     -- Ibuprofen 200mg - 1 box
(3, 10, 2, 120.00),   -- Antiseptic Hand Gel - 2 bottles
(3, 8, 1, 65.00),     -- Vitamin D3 - 1 bottle
(3, 11, 1, 180.00),   -- Digital Thermometer - 1 piece

-- Order 4 (Leila El-Mansouri - Ready for Pickup)
(4, 2, 1, 75.00),     -- Ibuprofen 200mg - 1 box
(4, 7, 2, 100.00),    -- Vitamin C - 2 bottles
(4, 10, 1, 100.00),   -- Antiseptic Hand Gel - 1 bottle

-- Order 5 (Karim Mabrouk - Pending)
(5, 5, 3, 300.00),    -- Metformin 1000mg - 3 boxes
(5, 6, 1, 120.00),    -- Lisinopril 10mg - 1 box
(5, 13, 1, 45.00),    -- Aspirin 100mg - 1 box
(5, 9, 1, 85.00),     -- Dexpanthenol Cream - 1 tube

-- Order 6 (Mohamed Hassan - Reviewed)
(6, 3, 2, 125.00),    -- Paracetamol 500mg - 2 boxes
(6, 8, 1, 65.00),     -- Vitamin D3 - 1 bottle
(6, 10, 2, 100.75),   -- Antiseptic Hand Gel - 2 bottles
(6, 11, 1, 180.00),   -- Digital Thermometer - 1 piece

-- Order 7 (Fatima Al-Rashid - Awaiting Choice)
(7, 1, 1, 95.00),     -- Amoxicillin 500mg - 1 box
(7, 4, 1, 65.00),     -- Ranitidine 150mg - 1 box
(7, 14, 2, 140.00),   -- Cetirizine 10mg - 2 boxes
(7, 15, 1, 120.00),   -- Omeprazole 20mg - 1 box

-- Order 8 (Youssef Bennani - Cancelled)
(8, 2, 1, 75.00),     -- Ibuprofen 200mg - 1 box
(8, 7, 1, 50.00),     -- Vitamin C - 1 bottle
(8, 10, 1, 55.00);    -- Antiseptic Hand Gel - 1 bottle

-- ============================================================================
-- 12. INSERT ORDER TRACKING
-- ============================================================================
INSERT INTO order_tracking (order_id, status, note, changed_by, changed_at) VALUES
-- Order 1: DELIVERED
(1, 'PENDING', 'Order created', 'PATIENT', '2025-03-10 10:00:00'),
(1, 'REVIEWED', 'Pharmacist reviewed prescription', 'PHARMACIST', '2025-03-10 10:30:00'),
(1, 'VALIDATED', 'Order validated', 'PHARMACIST', '2025-03-10 11:00:00'),
(1, 'RESERVED', 'Products reserved', 'PHARMACIST', '2025-03-10 11:30:00'),
(1, 'PAYMENT_PENDING', 'Awaiting payment', 'SYSTEM', '2025-03-10 12:00:00'),
(1, 'PAID', 'Payment received', 'PATIENT', '2025-03-10 14:00:00'),
(1, 'READY_FOR_PICKUP', 'Order packed and ready', 'PHARMACIST', '2025-03-11 09:00:00'),
(1, 'ASSIGNING', 'Assigning delivery personnel', 'SYSTEM', '2025-03-11 10:00:00'),
(1, 'ASSIGNED', 'Delivery personnel assigned', 'SYSTEM', '2025-03-11 10:15:00'),
(1, 'OUT_FOR_DELIVERY', 'Order sent for delivery', 'SYSTEM', '2025-03-15 08:00:00'),
(1, 'DELIVERED', 'Order delivered to patient', 'SYSTEM', '2025-03-15 16:30:00'),

-- Order 2: PAID
(2, 'PENDING', 'Order created', 'PATIENT', '2025-03-11 11:00:00'),
(2, 'REVIEWED', 'Pharmacist reviewed prescription', 'PHARMACIST', '2025-03-11 11:30:00'),
(2, 'VALIDATED', 'Order validated', 'PHARMACIST', '2025-03-11 12:00:00'),
(2, 'RESERVED', 'Products reserved', 'PHARMACIST', '2025-03-11 12:30:00'),
(2, 'PAYMENT_PENDING', 'Awaiting payment', 'SYSTEM', '2025-03-11 13:00:00'),
(2, 'PAID', 'Payment received', 'PATIENT', '2025-03-12 09:15:00'),

-- Order 3: OUT_FOR_DELIVERY
(3, 'PENDING', 'Order created', 'PATIENT', '2025-03-12 14:30:00'),
(3, 'REVIEWED', 'Pharmacist reviewed prescription', 'PHARMACIST', '2025-03-12 15:00:00'),
(3, 'VALIDATED', 'Order validated', 'PHARMACIST', '2025-03-12 15:30:00'),
(3, 'RESERVED', 'Products reserved', 'PHARMACIST', '2025-03-12 16:00:00'),
(3, 'PAYMENT_PENDING', 'Awaiting payment', 'SYSTEM', '2025-03-12 16:30:00'),
(3, 'PAID', 'Payment received', 'PATIENT', '2025-03-13 09:00:00'),
(3, 'READY_FOR_PICKUP', 'Order packed and ready', 'PHARMACIST', '2025-03-13 09:30:00'),
(3, 'OUT_FOR_DELIVERY', 'Order sent for delivery', 'SYSTEM', '2025-03-14 08:00:00'),

-- Order 4: READY_FOR_PICKUP
(4, 'PENDING', 'Order created', 'PATIENT', '2025-03-13 09:00:00'),
(4, 'REVIEWED', 'Pharmacist reviewed prescription', 'PHARMACIST', '2025-03-13 09:30:00'),
(4, 'VALIDATED', 'Order validated', 'PHARMACIST', '2025-03-13 10:00:00'),
(4, 'RESERVED', 'Products reserved', 'PHARMACIST', '2025-03-13 10:30:00'),
(4, 'PAYMENT_PENDING', 'Awaiting payment', 'SYSTEM', '2025-03-13 11:00:00'),
(4, 'PAID', 'Payment received', 'PATIENT', '2025-03-14 11:30:00'),
(4, 'READY_FOR_PICKUP', 'Order packed and ready', 'PHARMACIST', '2025-03-14 11:30:00'),

-- Order 5: PENDING
(5, 'PENDING', 'Order created', 'PATIENT', '2025-03-14 16:00:00'),

-- Order 6: REVIEWED
(6, 'PENDING', 'Order created', 'PATIENT', '2025-03-15 10:30:00'),
(6, 'REVIEWED', 'Pharmacist reviewed prescription', 'PHARMACIST', '2025-03-15 14:00:00'),

-- Order 7: AWAITING_CHOICE
(7, 'PENDING', 'Order created', 'PATIENT', '2025-03-15 13:00:00'),
(7, 'REVIEWED', 'Pharmacist reviewed prescription', 'PHARMACIST', '2025-03-15 13:15:00'),
(7, 'AWAITING_CHOICE', 'Awaiting patient choice on generic/brand', 'PHARMACIST', '2025-03-15 13:30:00'),

-- Order 8: CANCELLED
(8, 'PENDING', 'Order created', 'PATIENT', '2025-03-16 08:00:00'),
(8, 'REVIEWED', 'Pharmacist reviewed prescription', 'PHARMACIST', '2025-03-16 08:30:00'),
(8, 'CANCELLED', 'Cancelled by patient request', 'PATIENT', '2025-03-16 12:30:00');

-- ============================================================================
-- SUMMARY OF TEST DATA CREATED
-- ============================================================================
-- Users:              7 (2 doctors + 5 patients)
-- Patients:           5
-- Pharmacies:         5
-- Pharmacists:        5
-- Products:          15
-- Medical Records:    5
-- Consultations:      5
-- Prescriptions:      5
-- Prescription Items: 8
-- Pharmacy Orders:    8
-- Order Items:       25
-- Order Tracking:    49
-- ============================================================================

-- Verification queries you can run:
-- SELECT COUNT(*) as total_users FROM users;
-- SELECT COUNT(*) as total_patients FROM patients;
-- SELECT COUNT(*) as total_orders FROM pharmacy_orders;
-- SELECT COUNT(*) as total_items FROM order_items;
-- SELECT payment_status, COUNT(*) FROM pharmacy_orders GROUP BY payment_status;

-- ============================================================================
-- END OF TEST DATA SCRIPT
-- ============================================================================
