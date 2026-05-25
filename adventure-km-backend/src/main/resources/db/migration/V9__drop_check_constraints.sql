ALTER TABLE adventures DROP CONSTRAINT IF EXISTS chk_adventures_type;
ALTER TABLE adventures DROP CONSTRAINT IF EXISTS chk_adventures_status;
ALTER TABLE adventures DROP CONSTRAINT IF EXISTS chk_adventures_difficulty;
ALTER TABLE users DROP CONSTRAINT IF EXISTS chk_users_role;
