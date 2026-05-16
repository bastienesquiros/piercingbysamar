-- Make size and color required on product_variants
UPDATE product_variants SET size = 'N/A' WHERE size IS NULL;
UPDATE product_variants SET color = 'N/A' WHERE color IS NULL;

ALTER TABLE product_variants
    ALTER COLUMN size SET NOT NULL,
    ALTER COLUMN color SET NOT NULL;
