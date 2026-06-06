-- ── Indexes composite pour les filtres produits ────────────────
-- Requêtes findWithFilters : WHERE active = true AND category_id = ?
CREATE INDEX IF NOT EXISTS idx_products_active_category ON products(active, category_id);
-- Requête findByMaterial : WHERE active = true AND material = ?
CREATE INDEX IF NOT EXISTS idx_products_active_material ON products(active, material);

-- ── Variants : filtre par product_id + active ──────────────────
-- EXISTS (SELECT v FROM product_variants v WHERE v.product_id = p.id AND v.active = true)
CREATE INDEX IF NOT EXISTS idx_variants_product_active ON product_variants(product_id, active);

-- ── Commandes : filtre status + date pour les stats ───────────
-- WHERE status IN (...) AND created_at BETWEEN ? AND ?
CREATE INDEX IF NOT EXISTS idx_orders_status_created ON orders(status, created_at);

-- ── Images : filtre par variant_id ────────────────────────────
-- Utilisé pour récupérer les images d'une variante spécifique
CREATE INDEX IF NOT EXISTS idx_images_variant ON product_images(variant_id);

-- ── FAQ : position pour le tri ─────────────────────────────────
CREATE INDEX IF NOT EXISTS idx_faq_position ON faq_items(position);
