CREATE TABLE recipe (
  id          UUID PRIMARY KEY,
  name        VARCHAR(255) NOT NULL,
  image_url   TEXT,
  description TEXT
);