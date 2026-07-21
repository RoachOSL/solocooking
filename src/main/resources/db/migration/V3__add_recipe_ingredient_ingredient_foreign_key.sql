ALTER TABLE recipe_ingredient
    ADD CONSTRAINT fk_recipe_ingredient_ingredient
        FOREIGN KEY (ingredient_id) REFERENCES ingredient (id);

CREATE INDEX idx_recipe_ingredient_ingredient_id
    ON recipe_ingredient (ingredient_id);
