ALTER TABLE place
    ADD COLUMN s2_cell BIGINT UNSIGNED NOT NULL,
    ADD INDEX (s2_cell);
