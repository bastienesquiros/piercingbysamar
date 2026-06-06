CREATE TABLE faq_items (
    id       BIGSERIAL    PRIMARY KEY,
    question VARCHAR(500) NOT NULL,
    answer   TEXT         NOT NULL,
    position INTEGER      NOT NULL DEFAULT 0,
    active   BOOLEAN      NOT NULL DEFAULT TRUE
);

CREATE INDEX idx_faq_position ON faq_items(position);
