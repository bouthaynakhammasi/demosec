-- Add Home Care Services
INSERT INTO home_care_services (name, description, price, category, icon_url, duration_minutes, active) VALUES
('General Nursing', 'Professional nursing care at home', 50.0, 'Nursing', 'nursing-icon', 60, TRUE),
('Physiotherapy', 'Home physiotherapy sessions', 70.0, 'Therapy', 'physio-icon', 45, TRUE);

-- Create a Provider User
INSERT INTO users (full_name, email, password, role, phone, birth_date, enabled) VALUES
('John Provider', 'provider@test.com', '$2a$10$DoxEqDjCp5h5kLDCR5z3X.8KTdJNV.ZUZe/ZKuV5vPP.f0Nm2YpYe', 'HOME_CARE_PROVIDER', '0699887766', '1980-05-15', TRUE);

-- Link User to ServiceProvider profile
INSERT INTO service_providers (user_id, bio, verified, average_rating, total_reviews) VALUES
((SELECT id FROM users WHERE email = 'provider@test.com'), 'Expert nurse with 10 years experience', TRUE, 5.0, 1);

-- Link Provider to Specialties (Services)
INSERT INTO service_provider_specialties (service_provider_id, home_care_service_id) VALUES
((SELECT id FROM service_providers WHERE user_id = (SELECT id FROM users WHERE email = 'provider@test.com')), 1),
((SELECT id FROM service_providers WHERE user_id = (SELECT id FROM users WHERE email = 'provider@test.com')), 2);

-- Add Availability Rules for all days
INSERT INTO provider_availabilities (provider_id, day_of_week, start_time, end_time, available) VALUES
((SELECT id FROM service_providers WHERE user_id = (SELECT id FROM users WHERE email = 'provider@test.com')), 'MONDAY', '08:00:00', '18:00:00', TRUE),
((SELECT id FROM service_providers WHERE user_id = (SELECT id FROM users WHERE email = 'provider@test.com')), 'TUESDAY', '08:00:00', '18:00:00', TRUE),
((SELECT id FROM service_providers WHERE user_id = (SELECT id FROM users WHERE email = 'provider@test.com')), 'WEDNESDAY', '08:00:00', '18:00:00', TRUE),
((SELECT id FROM service_providers WHERE user_id = (SELECT id FROM users WHERE email = 'provider@test.com')), 'THURSDAY', '08:00:00', '18:00:00', TRUE),
((SELECT id FROM service_providers WHERE user_id = (SELECT id FROM users WHERE email = 'provider@test.com')), 'FRIDAY', '08:00:00', '18:00:00', TRUE),
((SELECT id FROM service_providers WHERE user_id = (SELECT id FROM users WHERE email = 'provider@test.com')), 'SATURDAY', '09:00:00', '13:00:00', TRUE),
((SELECT id FROM service_providers WHERE user_id = (SELECT id FROM users WHERE email = 'provider@test.com')), 'SUNDAY', '09:00:00', '13:00:00', TRUE);
