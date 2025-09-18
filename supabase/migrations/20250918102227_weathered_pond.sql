-- Insertion de données de test
-- Villes
INSERT INTO cities (name, country) VALUES 
('Yaoundé', 'Cameroun'),
('Douala', 'Cameroun'),
('Bamenda', 'Cameroun'),
('Bafoussam', 'Cameroun')
ON CONFLICT (name) DO NOTHING;

-- Premier utilisateur admin (mot de passe: admin123)
INSERT INTO users (email, password, first_name, last_name, phone, role, is_active, created_at, updated_at) 
VALUES (
    'admin@realestate.com', 
    '$2a$10$rQ5gV8XqZ3aZ3aZ3aZ3aZ3.Z3aZ3aZ3aZ3aZ3aZ3aZ3aZ3aZ3aZ3a', 
    'Admin', 
    'System', 
    '+237123456789', 
    'ADMIN', 
    true, 
    NOW(), 
    NOW()
) ON CONFLICT (email) DO NOTHING;

-- Utilisateur propriétaire (mot de passe: owner123)
INSERT INTO users (email, password, first_name, last_name, phone, role, is_active, created_at, updated_at) 
VALUES (
    'owner@realestate.com', 
    '$2a$10$tS6gW9YrA4bA4bA4bA4bA4.A4bA4bA4bA4bA4bA4bA4bA4bA4bA4b', 
    'Jean', 
    'Propriétaire', 
    '+237987654321', 
    'OWNER', 
    true, 
    NOW(), 
    NOW()
) ON CONFLICT (email) DO NOTHING;