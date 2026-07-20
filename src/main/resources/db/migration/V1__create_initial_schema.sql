CREATE TABLE ingredient (
    id UUID NOT NULL,
    name VARCHAR(255) NOT NULL,
    created_at TIMESTAMP(6) WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP(6) WITH TIME ZONE NOT NULL,
    CONSTRAINT pk_ingredient PRIMARY KEY (id),
    CONSTRAINT uq_ingredient_name UNIQUE (name)
);

CREATE TABLE recipe (
    id UUID NOT NULL,
    name VARCHAR(255) NOT NULL,
    image_url VARCHAR(2048) NOT NULL,
    description VARCHAR(5000) NOT NULL,
    created_at TIMESTAMP(6) WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP(6) WITH TIME ZONE NOT NULL,
    CONSTRAINT pk_recipe PRIMARY KEY (id)
);

CREATE TABLE recipe_section (
    id UUID NOT NULL,
    recipe_id UUID NOT NULL,
    name VARCHAR(255) NOT NULL,
    position INTEGER NOT NULL,
    created_at TIMESTAMP(6) WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP(6) WITH TIME ZONE NOT NULL,
    CONSTRAINT pk_recipe_section PRIMARY KEY (id),
    CONSTRAINT fk_recipe_section_recipe
        FOREIGN KEY (recipe_id) REFERENCES recipe (id)
);

CREATE INDEX idx_recipe_section_recipe_id
    ON recipe_section (recipe_id);

CREATE TABLE recipe_ingredient (
    id UUID NOT NULL,
    section_id UUID NOT NULL,
    ingredient_id UUID NOT NULL,
    amount NUMERIC(12, 3) NOT NULL,
    unit VARCHAR(64) NOT NULL,
    note VARCHAR(500),
    position INTEGER NOT NULL,
    created_at TIMESTAMP(6) WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP(6) WITH TIME ZONE NOT NULL,
    CONSTRAINT pk_recipe_ingredient PRIMARY KEY (id),
    CONSTRAINT fk_recipe_ingredient_section
        FOREIGN KEY (section_id) REFERENCES recipe_section (id),
    CONSTRAINT ck_recipe_ingredient_amount_positive CHECK (amount > 0)
);

CREATE INDEX idx_recipe_ingredient_section_id
    ON recipe_ingredient (section_id);
