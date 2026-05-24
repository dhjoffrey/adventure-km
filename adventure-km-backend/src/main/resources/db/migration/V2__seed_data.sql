-- ============================================================
-- V2: Seed data — admin user, sample adventures, equipment
-- ============================================================

-- ---- 1. Admin user ----
INSERT INTO users (id, username, email, password_hash, avatar_sprite_id, role)
VALUES (1, 'joffrey', 'dhjoffrey@gmail.com',
        '$2a$10$dXJ3SW6G7P50lGmMQgel6u3dNJzs9tlvGWlSFs/EvNJhOm3Jg.IHi',
        1, 'ADMIN');

-- ---- 2. User levels (initial zeros, updated at the end) ----
INSERT INTO user_levels (user_id, total_km, total_elevation_m, adventure_count, rpg_score, level)
VALUES (1, 0, 0, 0, 0, 0);

-- ---- 3. Sample adventures ----

-- GR54 — Tour de l'Oisans
INSERT INTO adventures (id, user_id, title, date, content, type, difficulty, status)
VALUES (1, 1, 'GR54 — Tour de l''Oisans',
        '2024-07-15',
        '## GR54 — Tour de l''Oisans

Le GR54 est une boucle mythique autour du massif des Écrins. Départ de Bourg-d''Oisans, passage par le col du Lautaret, la Bérarde, Vallouise...

### Points forts
- **Col de la Muzelle** : vue imprenable sur le lac et les sommets environnants
- **La Bérarde** : village isolé au coeur du parc national des Écrins
- **Col du Sellar** : passage technique avec névés en début de saison

### Conditions
Météo globalement favorable avec deux jours de pluie. Sentiers bien balisés, quelques passages exposés nécessitant de l''attention.

> *"La montagne n''est ni juste ni injuste, elle est dangereuse."* — Reinhold Messner',
        'ULTRA', 5, 'PUBLISHED');

-- UTOBI — Ultra Tour du Beaufortain et de l'Italie
INSERT INTO adventures (id, user_id, title, date, content, type, difficulty, status)
VALUES (2, 1, 'UTOBI — Ultra Tour du Beaufortain',
        '2025-08-22',
        '## UTOBI — Ultra Tour du Beaufortain

Premier ultra ! 105 km et 7500 D+ autour du Beaufortain. Une nuit blanche, des étoiles au-dessus du col du Bresson, et une arrivée au lever du soleil.

### Récit
Départ à 18h de Beaufort. Les premiers kilomètres se font en groupe, dans une ambiance de fête. Très vite la nuit tombe et chacun se retrouve seul avec sa frontale.

### Moments clés
- **Col du Bresson (2469m)** : arrivée de nuit, ciel étoilé incroyable
- **Ravitaillement Arêches** : soupe chaude à 3h du matin, moral remonté
- **Dernier col** : crampes terribles, marche forcée jusqu''à l''arrivée

### Leçons
Ne jamais sous-estimer la nutrition. Prévoir 300 kcal/h minimum sur un effort de plus de 15h.

> *"L''ultra, c''est 50% physique et 100% mental."*',
        'RACE', 4, 'PUBLISHED');

-- UT4M — Ultra Tour des 4 Massifs
INSERT INTO adventures (id, user_id, title, date, content, type, difficulty, status)
VALUES (3, 1, 'UT4M — Ultra Tour des 4 Massifs',
        '2025-10-10',
        '## UT4M 40 — Vercors

Le format 40 km du UT4M, concentré sur le massif du Vercors. Un parcours technique avec de beaux singles et une ambiance automnale.

### Parcours
Départ de Grenoble-Bastille, montée vers le col de l''Arc, traversée des crêtes du Vercors, descente technique vers Sassenage.

### Temps forts
- **Crêtes du Vercors** : vue à 360° sur Grenoble, Chartreuse, Belledonne
- **Single track** : descente ludique dans la forêt de Sassenage
- **Ambiance automnale** : couleurs flamboyantes, air frais

### Résultat
Objectif atteint : terminer sous les 6h. Bonne gestion de course, alimentation régulière.

> *"Chaque kilomètre est un pas de plus vers la version de toi que tu veux devenir."*',
        'ULTRA', 5, 'PUBLISHED');

-- ---- 4. Adventure stats ----

-- GR54: 176 km, 12000 D+, 12000 D-, 7 jours (10080 min), max 3100m, min 720m
INSERT INTO adventure_stats (adventure_id, distance_km, elevation_gain_m, elevation_loss_m, duration_minutes, max_altitude_m, min_altitude_m)
VALUES (1, 176.00, 12000, 12000, 10080, 3100, 720);

-- UTOBI: 105 km, 7500 D+, 7500 D-, 24h (1440 min), max 2469m, min 750m
INSERT INTO adventure_stats (adventure_id, distance_km, elevation_gain_m, elevation_loss_m, duration_minutes, max_altitude_m, min_altitude_m)
VALUES (2, 105.00, 7500, 7500, 1440, 2469, 750);

-- UT4M 40: 42 km, 2500 D+, 2500 D-, 350 min, max 1700m, min 210m
INSERT INTO adventure_stats (adventure_id, distance_km, elevation_gain_m, elevation_loss_m, duration_minutes, max_altitude_m, min_altitude_m)
VALUES (3, 42.00, 2500, 2500, 350, 1700, 210);

-- ---- 5. Update user levels with totals ----
-- total_km = 176 + 105 + 42 = 323
-- total_elevation_m = 12000 + 7500 + 2500 = 22000
-- rpg_score = (323 * 1) + (22000 / 100 * 2) + (3 * 50) = 323 + 440 + 150 = 913
-- level = floor(sqrt(913 / 10)) = floor(9.56) = 9
UPDATE user_levels
SET total_km          = 323.00,
    total_elevation_m = 22000,
    adventure_count   = 3,
    rpg_score         = 913,
    level             = 9
WHERE user_id = 1;

-- ---- 6. Equipment items ----
INSERT INTO equipment_items (id, name, category, icon_key, pixel_sprite_key) VALUES
(1,  'Hoka Speedgoat 5',       'SHOES',    'shoe',      'speedgoat'),
(2,  'Salomon S/LAB Ultra 3',  'SHOES',    'shoe',      'slab_ultra'),
(3,  'Salomon ADV Skin 12',    'VEST',     'vest',      'adv_skin'),
(4,  'Salomon ADV Skin 5',     'VEST',     'vest',      'adv_skin_5'),
(5,  'Black Diamond Distance', 'POLES',    'poles',     'bd_distance'),
(6,  'Leki Ultratrail FX.One', 'POLES',    'poles',     'leki_fx'),
(7,  'Buff UV+',               'HAT',      'hat',       'buff_uv'),
(8,  'Salomon XA Cap',         'HAT',      'hat',       'xa_cap'),
(9,  'Julbo Aerospeed',        'GLASSES',  'glasses',   'aerospeed'),
(10, 'Salomon XT 15',          'BACKPACK', 'backpack',  'xt_15'),
(11, 'Coros Apex 2 Pro',       'WATCH',    'watch',     'apex2pro'),
(12, 'Coros Pace 3',           'WATCH',    'watch',     'pace3'),
(13, 'Petzl Nao RL',           'OTHER',    'headlamp',  'nao_rl'),
(14, 'Compressport Full Socks','OTHER',    'socks',     'compressport'),
(15, 'Sea to Summit Nano',     'OTHER',    'poncho',    'nano_poncho');

-- ---- 7. Adventure ↔ Equipment links ----

-- GR54: Speedgoat, ADV Skin 12, BD poles, Buff, Julbo, XT 15, Apex 2 Pro, Nao RL
INSERT INTO adventure_equipment (adventure_id, equipment_id) VALUES
(1, 1), (1, 3), (1, 5), (1, 7), (1, 9), (1, 10), (1, 11), (1, 13);

-- UTOBI: S/LAB Ultra, ADV Skin 12, Leki poles, XA Cap, Julbo, Apex 2 Pro, Nao RL, Compressport
INSERT INTO adventure_equipment (adventure_id, equipment_id) VALUES
(2, 2), (2, 3), (2, 6), (2, 8), (2, 9), (2, 11), (2, 13), (2, 14);

-- UT4M 40: Speedgoat, ADV Skin 5, Buff, Julbo, Pace 3
INSERT INTO adventure_equipment (adventure_id, equipment_id) VALUES
(3, 1), (3, 4), (3, 7), (3, 9), (3, 12);

-- ---- 8. Reset identity sequences ----
-- Explicit IDs were used above; reset so app-generated rows don't collide with seed data
ALTER TABLE users ALTER COLUMN id RESTART WITH 100;
ALTER TABLE adventures ALTER COLUMN id RESTART WITH 100;
ALTER TABLE adventure_stats ALTER COLUMN id RESTART WITH 100;
ALTER TABLE equipment_items ALTER COLUMN id RESTART WITH 100;
