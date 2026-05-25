-- Remove UTOBI seed adventure (id=2) and recalculate user levels
DELETE FROM adventure_equipment WHERE adventure_id = 2;
DELETE FROM adventure_stats WHERE adventure_id = 2;
DELETE FROM adventures WHERE id = 2;

-- Recalculate totals: GR54 (176km, 12000 D+) + UT4M (42km, 2500 D+) + Swiss Alps 100 (161.30km, 9650 D+)
UPDATE user_levels
SET total_km          = 379.30,
    total_elevation_m = 24150,
    adventure_count   = 3,
    rpg_score         = 1012,
    level             = 10
WHERE user_id = 1;
