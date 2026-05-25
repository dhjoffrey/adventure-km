-- Populate avg_altitude_m for existing seed adventures
-- Values are estimates based on min/max and typical trail profiles

-- GR54 — Tour de l'Oisans (min 720m, max 3100m) : high alpine loop
UPDATE adventure_stats SET avg_altitude_m = 1860 WHERE adventure_id = 1;

-- UT4M 40 — Vercors (min 210m, max 1700m) : lower massif
UPDATE adventure_stats SET avg_altitude_m = 950 WHERE adventure_id = 3;

-- Swiss Alps 100 (min 1053m, max 2778m) : sustained high terrain
UPDATE adventure_stats SET avg_altitude_m = 1900 WHERE adventure_id = 4;
