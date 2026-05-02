-- Base de données de test pour la gestion des commandes de pharmacie (Pharmacy Order Management)
-- Ce script remplit les tables nécessaires avec des données cohérentes pour les tests.

-- Désactiver temporairement les vérifications de clés étrangères pour faciliter l'insertion
SET FOREIGN_KEY_CHECKS = 0;

-- 1. Insertion des Pharmacies
-- Table: pharmacies
INSERT INTO pharmacies (id, name, address, location_lat, location_lng, phone_number, email) VALUES
(1, 'Pharmacie Centrale de Tunis', '123 Avenue de la Liberté, Tunis', 36.8065, 10.1815, '71000111', 'contact@pharmacie-centrale.tn'),
(2, 'Pharmacie du Lac', 'Rue du Lac Biwa, Les Berges du Lac, Tunis', 36.8329, 10.2372, '71222333', 'lac@pharmacie.tn'),
(3, 'Pharmacie de la Gare', '45 Avenue Habib Bourguiba, Tunis', 36.7992, 10.1793, '71444555', 'gare@pharmacie.tn');

-- 2. Insertion des Produits
-- Table: products
INSERT INTO products (id, name, description, image_url, manufacturer, brand, category, type, barcode, unit) VALUES
(1, 'Doliprane 1000mg', 'Paracétamol pour le soulagement de la douleur et de la fièvre', 'https://api.placeholder.com/150', 'Sanofi', 'Doliprane', 'Analgésique', 'MEDICATION', '3400936440266', 'BOX'),
(2, 'Amoxicilline 500mg', 'Antibiotique utilisé pour traiter diverses infections bactériennes', 'https://api.placeholder.com/150', 'Mylan', 'Amoxicilline', 'Antibiotique', 'MEDICATION', '3400936440277', 'BOX'),
(3, 'Advil 400mg', 'Ibuprofène pour le traitement des douleurs inflammatoires', 'https://api.placeholder.com/150', 'Pfizer', 'Advil', 'Anti-inflammatoire', 'MEDICATION', '3400936440288', 'BOX'),
(4, 'Vitamine C 1000mg', 'Complément alimentaire pour booster l''énergie et l''immunité', 'https://api.placeholder.com/150', 'Upsa', 'Vitamine C', 'Complément', 'SUPPLEMENT', '3400936440299', 'TUBE'),
(5, 'Masques Chirurgicaux', 'Boîte de 50 masques de protection faciale', 'https://api.placeholder.com/150', 'HealthCorp', 'SafeMask', 'Hygiène', 'HYGIENE', '3400936440300', 'BOX');

-- 3. Insertion des Utilisateurs (Table de base Users)
-- Table: users (Hérédité JOINED)
-- Mot de passe par défaut: "password" (BCrypt hash)
INSERT INTO users (id, full_name, email, password, role, phone, birth_date, enabled) VALUES
(1, 'Jean Dupont', 'jean.dupont@email.com', '$2a$10$8.UnVuG9HHgffUDAlk8q6Ou5HEMF6vYZPuMAFRAS7ANarLZQunPQu', 'PATIENT', '55111222', '1990-05-15', 1),
(2, 'Sarah Martin', 'sarah.martin@email.com', '$2a$10$8.UnVuG9HHgffUDAlk8q6Ou5HEMF6vYZPuMAFRAS7ANarLZQunPQu', 'PATIENT', '55333444', '1995-10-20', 1),
(3, 'Dr. Ahmed Ben Ali', 'ahmed@pharmacie.tn', '$2a$10$8.UnVuG9HHgffUDAlk8q6Ou5HEMF6vYZPuMAFRAS7ANarLZQunPQu', 'PHARMACIST', '22111222', '1980-01-10', 1),
(4, 'Dr. Leila Mansour', 'leila@pharmacie.tn', '$2a$10$8.UnVuG9HHgffUDAlk8q6Ou5HEMF6vYZPuMAFRAS7ANarLZQunPQu', 'PHARMACIST', '22333444', '1985-06-25', 1),
(5, 'Dr. Marc Durand', 'marc.durand@hopital.com', '$2a$10$8.UnVuG9HHgffUDAlk8q6Ou5HEMF6vYZPuMAFRAS7ANarLZQunPQu', 'DOCTOR', '99111222', '1975-03-30', 1);

-- 4. Insertion des Patients
-- Table: patients
INSERT INTO patients (id, gender, blood_type, emergency_contact_name, emergency_contact_phone) VALUES
(1, 'MALE', 'A_POS', 'Marie Dupont', '55111333'),
(2, 'FEMALE', 'O_NEG', 'Paul Martin', '55333555');

-- 5. Insertion des Pharmaciens
-- Table: pharmacists
INSERT INTO pharmacists (id, pharmacy_id) VALUES
(3, 1),
(4, 2);

-- 6. Insertion des Docteurs
-- Table: doctors
INSERT INTO doctors (id, specialty, license_number, years_of_experience, consultation_fee, consultation_mode) VALUES
(5, 'Généraliste', 'LIC-123456', 15, 50.00, 'IN_PERSON');

-- 7. Insertion des Dossiers Médicaux
-- Table: medical_records
INSERT INTO medical_records (id, patient_id) VALUES
(1, 1),
(2, 2);

-- 8. Insertion des Consultations
-- Table: consultations
INSERT INTO consultations (id, medical_record_id, doctor_id, date, observations) VALUES
(1, 1, 5, NOW(), 'Symptômes de grippe légère, repos recommandé'),
(2, 2, 5, NOW(), 'Douleurs articulaires persistantes');

-- 9. Insertion des Ordonnances (Prescriptions)
-- Table: prescriptions
INSERT INTO prescriptions (id, consultation_id, date) VALUES
(1, 1, CURDATE()),
(2, 2, CURDATE());

-- 10. Insertion des Commandes (Pharmacy Orders)
-- Table: pharmacy_orders
INSERT INTO pharmacy_orders (id, patient_id, pharmacy_id, prescription_id, status, total_price, delivery_address, created_at, delivery_type) VALUES
(1, 1, 1, 1, 'PENDING', 25.50, '123 Avenue de la Liberté, Tunis', NOW(), 'HOME_DELIVERY'),
(2, 2, 1, NULL, 'VALIDATED', 15.00, '45 Avenue Habib Bourguiba, Tunis', NOW(), 'PICKUP'),
(3, 1, 2, 2, 'DELIVERED', 45.00, '123 Avenue de la Liberté, Tunis', DATE_SUB(NOW(), INTERVAL 2 DAY), 'HOME_DELIVERY'),
(4, 2, 3, NULL, 'CANCELLED', 10.00, 'Rue du Lac Biwa, Tunis', DATE_SUB(NOW(), INTERVAL 5 DAY), 'PICKUP');

-- 11. Insertion des Articles de Commande (Order Items)
-- Table: order_items
INSERT INTO order_items (id, order_id, product_id, quantity, price) VALUES
(1, 1, 1, 2, 8.50), -- 2x Doliprane
(2, 1, 4, 1, 8.50), -- 1x Vitamine C
(3, 2, 3, 1, 15.00), -- 1x Advil
(4, 3, 2, 1, 20.00), -- 1x Amoxicilline
(5, 3, 5, 1, 25.00), -- 1x Masques
(6, 4, 1, 1, 10.00); -- 1x Doliprane

-- Réactiver les vérifications de clés étrangères
SET FOREIGN_KEY_CHECKS = 1;

-- Fin du script
