ALTER TABLE ingestreport ADD COLUMN failedrecordscount bigint DEFAULT 0;
UPDATE ingestreport SET failedrecordscount = 0;
