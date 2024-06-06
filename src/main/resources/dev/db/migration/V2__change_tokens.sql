-- Add refresh_token to tokens table
ALTER TABLE tokens ADD COLUMN `refresh_token` VARCHAR(50) DEFAULT "";

-- Add refresh_expiration_date to tokens table
ALTER TABLE tokens ADD COLUMN  `refresh_expiration_date` datetime;

-- Add is_moible field to tokens table
ALTER TABLE tokens ADD COLUMN  `is_mobile` TINYINT(1) DEFAULT 0;