-- V2: migrate category and product IDs from BIGINT to UUIDs (BINARY(16))

-- Step 1: add temporary UUID columns, populate them, and copy every product -> category link
ALTER TABLE category ADD COLUMN new_id BINARY(16);
UPDATE category SET new_id = UUID_TO_BIN(UUID());

ALTER TABLE product ADD COLUMN new_id BINARY(16), ADD COLUMN new_category_id BINARY(16);
UPDATE product SET new_id = UUID_TO_BIN(UUID());
UPDATE product p JOIN category c ON c.id = p.category_id SET p.new_category_id = c.new_id;

-- Step 2: drop the old foreign key, strip AUTO_INCREMENT, and drop old numeric columns
ALTER TABLE product DROP FOREIGN KEY FK1mtsbur82frn64de7balymq9s;
ALTER TABLE product MODIFY id BIGINT NOT NULL;
ALTER TABLE product DROP PRIMARY KEY, DROP COLUMN id, DROP COLUMN category_id;
ALTER TABLE category MODIFY id BIGINT NOT NULL;
ALTER TABLE category DROP PRIMARY KEY, DROP COLUMN id;

-- Step 3: rename UUID columns to final names and re-establish primary & foreign keys
ALTER TABLE category CHANGE new_id id BINARY(16) NOT NULL, ADD PRIMARY KEY (id);
ALTER TABLE product CHANGE new_id id BINARY(16) NOT NULL, CHANGE new_category_id category_id BINARY(16), ADD PRIMARY KEY (id);
ALTER TABLE product ADD CONSTRAINT fk_product_category FOREIGN KEY (category_id) REFERENCES category (id);