-- ============================================================
-- V1 - Schema initial Piercing by Samar
-- ============================================================

-- ── Categories ──────────────────────────────────────────────
CREATE TABLE categories (
    id          BIGSERIAL PRIMARY KEY,
    parent_id   BIGINT REFERENCES categories(id) ON DELETE SET NULL,
    name        VARCHAR(100) NOT NULL,
    slug        VARCHAR(100) NOT NULL UNIQUE,
    description TEXT,
    active      BOOLEAN     NOT NULL DEFAULT true,
    created_at  TIMESTAMP   NOT NULL DEFAULT now(),
    updated_at  TIMESTAMP   NOT NULL DEFAULT now()
);

-- ── Users (admin only - V1) ──────────────────────────────────
CREATE TABLE users (
    id            BIGSERIAL PRIMARY KEY,
    email         VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role          VARCHAR(20)  NOT NULL DEFAULT 'ADMIN',
    created_at    TIMESTAMP    NOT NULL DEFAULT now()
);

-- ── Products ─────────────────────────────────────────────────
CREATE TABLE products (
    id               BIGSERIAL PRIMARY KEY,
    category_id      BIGINT REFERENCES categories(id) ON DELETE SET NULL,
    name             VARCHAR(255) NOT NULL,
    slug             VARCHAR(255) NOT NULL UNIQUE,
    description      TEXT,
    material         VARCHAR(50),  -- TITANIUM | STEEL | BIOFLEX | GOLD
    nickel_free      BOOLEAN      NOT NULL DEFAULT false,
    meta_title       VARCHAR(255),
    meta_description VARCHAR(500),
    active           BOOLEAN      NOT NULL DEFAULT true,
    created_at       TIMESTAMP    NOT NULL DEFAULT now(),
    updated_at       TIMESTAMP    NOT NULL DEFAULT now()
);

-- ── Product Variants ─────────────────────────────────────────
-- Chaque variante = combinaison taille/couleur d'un produit
CREATE TABLE product_variants (
    id         BIGSERIAL PRIMARY KEY,
    product_id BIGINT      NOT NULL REFERENCES products(id) ON DELETE CASCADE,
    sku        VARCHAR(100) NOT NULL UNIQUE,
    size       VARCHAR(50),
    color      VARCHAR(50),
    -- Prix en centimes pour éviter les erreurs float (ex: 1990 = 19.90 EUR)
    price_cents INTEGER     NOT NULL,
    stock       INTEGER     NOT NULL DEFAULT 0,
    active      BOOLEAN     NOT NULL DEFAULT true,
    created_at  TIMESTAMP   NOT NULL DEFAULT now(),
    updated_at  TIMESTAMP   NOT NULL DEFAULT now()
);

-- ── Product Images ───────────────────────────────────────────
CREATE TABLE product_images (
    id         BIGSERIAL PRIMARY KEY,
    product_id BIGINT       NOT NULL REFERENCES products(id) ON DELETE CASCADE,
    variant_id BIGINT       REFERENCES product_variants(id) ON DELETE SET NULL,
    r2_url     VARCHAR(500) NOT NULL,
    position   INTEGER      NOT NULL DEFAULT 0,
    alt_text   VARCHAR(255),
    created_at TIMESTAMP    NOT NULL DEFAULT now()
);

-- ── Tags ─────────────────────────────────────────────────────
CREATE TABLE tags (
    id   BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    slug VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE product_tags (
    product_id BIGINT NOT NULL REFERENCES products(id) ON DELETE CASCADE,
    tag_id     BIGINT NOT NULL REFERENCES tags(id)     ON DELETE CASCADE,
    PRIMARY KEY (product_id, tag_id)
);

-- ── Orders ───────────────────────────────────────────────────
-- Statuts SHIPPING  : PENDING → PAID → SHIPPED → DELIVERED | CANCELLED
-- Statuts C&C       : CLICK_COLLECT_PENDING → READY → COLLECTED | CANCELLED
CREATE TABLE orders (
    id                        BIGSERIAL    PRIMARY KEY,
    reference                 VARCHAR(20)  NOT NULL UNIQUE,  -- PBS-2026-0001
    order_type                VARCHAR(20)  NOT NULL,         -- SHIPPING | CLICK_COLLECT
    status                    VARCHAR(30)  NOT NULL DEFAULT 'PENDING',
    -- Client
    customer_email            VARCHAR(255) NOT NULL,
    customer_name             VARCHAR(255) NOT NULL,
    customer_phone            VARCHAR(50),
    -- Adresse livraison (null si click & collect)
    shipping_address          VARCHAR(255),
    shipping_city             VARCHAR(100),
    shipping_postal_code      VARCHAR(20),
    shipping_country          VARCHAR(2),                       -- ISO 3166-1 alpha-2
    -- Montants en centimes
    subtotal_cents            INTEGER      NOT NULL,
    shipping_cost_cents       INTEGER      NOT NULL DEFAULT 0,
    total_cents               INTEGER      NOT NULL,
    currency                  VARCHAR(3)   NOT NULL DEFAULT 'EUR',
    -- Stripe
    stripe_session_id         VARCHAR(255),
    stripe_payment_intent_id  VARCHAR(255),
    notes                     TEXT,
    created_at                TIMESTAMP    NOT NULL DEFAULT now(),
    updated_at                TIMESTAMP    NOT NULL DEFAULT now()
);

-- ── Order Items ──────────────────────────────────────────────
-- Snapshot au moment de l'achat : le prix/nom peut changer après
CREATE TABLE order_items (
    id                     BIGSERIAL    PRIMARY KEY,
    order_id               BIGINT       NOT NULL REFERENCES orders(id) ON DELETE CASCADE,
    product_variant_id     BIGINT       REFERENCES product_variants(id) ON DELETE SET NULL,
    snapshot_product_name  VARCHAR(255) NOT NULL,
    snapshot_variant_label VARCHAR(255),
    unit_price_cents       INTEGER      NOT NULL,
    quantity               INTEGER      NOT NULL,
    total_cents            INTEGER      NOT NULL
);

-- ── Indexes ──────────────────────────────────────────────────
CREATE INDEX idx_categories_parent    ON categories(parent_id);
CREATE INDEX idx_categories_slug      ON categories(slug);

CREATE INDEX idx_products_category    ON products(category_id);
CREATE INDEX idx_products_slug        ON products(slug);
CREATE INDEX idx_products_active      ON products(active);

CREATE INDEX idx_variants_product     ON product_variants(product_id);
CREATE INDEX idx_variants_sku         ON product_variants(sku);

CREATE INDEX idx_images_product       ON product_images(product_id);

CREATE INDEX idx_orders_reference     ON orders(reference);
CREATE INDEX idx_orders_status        ON orders(status);
CREATE INDEX idx_orders_email         ON orders(customer_email);
CREATE INDEX idx_orders_stripe        ON orders(stripe_payment_intent_id);

CREATE INDEX idx_order_items_order    ON order_items(order_id);
