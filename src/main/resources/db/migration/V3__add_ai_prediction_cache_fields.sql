ALTER TABLE ai_results
    ADD COLUMN requested_crop_name VARCHAR(50) NULL AFTER original_file_name,
    ADD COLUMN sick_key VARCHAR(100) NULL AFTER sick_name_kor,
    ADD COLUMN prediction_status VARCHAR(30) NOT NULL DEFAULT 'SUCCESS' AFTER confidence;

UPDATE ai_results
SET requested_crop_name = crop_name
WHERE requested_crop_name IS NULL;

UPDATE ai_results
SET prediction_status = 'INFO_NOT_FOUND'
WHERE sick_key IS NULL;

ALTER TABLE ai_results
    MODIFY COLUMN requested_crop_name VARCHAR(50) NOT NULL,
    DROP INDEX uk_ai_results_image_hash,
    ADD CONSTRAINT uk_ai_results_image_hash_crop UNIQUE (image_hash, requested_crop_name);
