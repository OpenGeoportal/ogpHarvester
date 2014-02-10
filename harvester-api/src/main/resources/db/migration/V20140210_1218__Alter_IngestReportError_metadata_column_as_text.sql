-- Change the length of metadata column in ingestreporterror table in order 
-- to store the original metadata

ALTER TABLE ingestreporterror ALTER COLUMN metadata SET DATA TYPE text;