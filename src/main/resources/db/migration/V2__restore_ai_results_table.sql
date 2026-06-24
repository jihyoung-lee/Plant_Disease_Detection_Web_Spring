CREATE TABLE IF NOT EXISTS ai_results (
    id BIGINT NOT NULL AUTO_INCREMENT,
    image_url VARCHAR(2048) NULL,
    image_hash VARCHAR(64) NOT NULL,
    original_file_name VARCHAR(255) NULL,
    crop_name VARCHAR(50) NOT NULL,
    sick_name_kor VARCHAR(100) NOT NULL,
    confidence DECIMAL(5, 2) NOT NULL,
    created_at DATETIME(6) NOT NULL,
    CONSTRAINT pk_ai_results PRIMARY KEY (id),
    CONSTRAINT uk_ai_results_image_hash UNIQUE (image_hash)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

SET @legacy_disease_name_column_exists = (
    SELECT COUNT(*)
    FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'ai_results'
      AND COLUMN_NAME = 'disease_name_kor'
);

SET @rename_legacy_disease_name_column = IF(
    @legacy_disease_name_column_exists > 0,
    'ALTER TABLE ai_results CHANGE COLUMN disease_name_kor sick_name_kor VARCHAR(100) NOT NULL',
    'SELECT 1'
);

PREPARE rename_legacy_disease_name_column FROM @rename_legacy_disease_name_column;
EXECUTE rename_legacy_disease_name_column;
DEALLOCATE PREPARE rename_legacy_disease_name_column;
