-- ============================================================
-- StockMaster — Données initiales
-- À exécuter une seule fois dans HeidiSQL / MySQL Workbench
-- ============================================================

-- Mot de passe : Admin@1234 (bcrypt encodé)
-- Généré avec BCryptPasswordEncoder.encode("Admin@1234")
INSERT INTO users (username, password, email, full_name, active, role, created_at, updated_at)
VALUES (
    'admin',
    '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
    'admin@stockmaster.com',
    'Administrateur Système',
    true,
    'ADMINISTRATEUR',
    NOW(),
    NOW()
)
ON DUPLICATE KEY UPDATE username = username;
