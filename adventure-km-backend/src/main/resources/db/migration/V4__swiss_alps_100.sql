-- ============================================================
-- V4: Swiss Alps 100 — 161km, 9650 D+
-- ============================================================

INSERT INTO adventures (id, user_id, title, date, content, type, difficulty, gpx_path, status)
VALUES (4, 1, 'Swiss Alps 100 — 161 km autour des Alpes suisses',
        '2025-08-10',
        '## Swiss Alps 100

Le Swiss Alps 100 est l''un des trails les plus exigeants d''Europe. 161 km et 9650 m de dénivelé positif à travers les Alpes suisses, entre Davos et le Piz Bernina.

### Parcours

Départ de Davos, traversée du Val Bregaglia, passages par des cols mythiques au-dessus de 2700m, nuits en haute montagne. Un itinéraire sauvage, loin des foules, avec des sentiers souvent peu balisés.

### Points forts

- **Col de la Maloja (1815m)** : premier grand passage, vue sur les lacs engadinois
- **Passo del Lunghin (2645m)** : point de partage des eaux entre Mer du Nord, Adriatique et Mer Noire
- **Nuit en bivouac** : étoiles au-dessus du Piz Platta, températures proches de 0°C
- **Arrivée à Vals** : village thermal, soulagement et fierté mêlés

### Conditions

Météo changeante avec orage électrique au passage du Piz Bernina. Terrain technique, exigence d''autonomie maximale. Ravitaillements espacés de 25-30 km.

### Leçons

L''altitude et le froid nocturne consomment énormément d''énergie. Minimum 2 couches thermiques en plus du prévu. Ne jamais négliger les bâtons en haute montagne.

> *"La montagne se mérite. Chaque mètre de dénivelé est une victoire arrachée."*',
        'ULTRA', 5, 'gpx/4.gpx', 'PUBLISHED');

INSERT INTO adventure_stats (adventure_id, distance_km, elevation_gain_m, elevation_loss_m, duration_minutes, max_altitude_m, min_altitude_m)
VALUES (4, 161.30, 9650, 9650, 2040, 2778, 1053);

-- Update user levels: add Swiss Alps 100 to totals
-- total_km = 323 + 161.30 = 484.30
-- total_elevation_m = 22000 + 9650 = 31650
-- adventure_count = 4
-- rpg_score = (484 * 1) + (31650 / 100 * 2) + (4 * 50) = 484 + 633 + 200 = 1317
-- level = floor(sqrt(1317 / 10)) = floor(11.48) = 11
UPDATE user_levels
SET total_km          = 484.30,
    total_elevation_m = 31650,
    adventure_count   = 4,
    rpg_score         = 1317,
    level             = 11
WHERE user_id = 1;

-- Reset sequence to avoid collision with seed IDs
ALTER TABLE adventures ALTER COLUMN id RESTART WITH 100;
ALTER TABLE adventure_stats ALTER COLUMN id RESTART WITH 100;
