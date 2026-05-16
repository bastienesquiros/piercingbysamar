-- ============================================================
-- V2 - Seed données de développement (profil dev uniquement)
-- ============================================================

-- ── Categories ──────────────────────────────────────────────
INSERT INTO categories (id, parent_id, name, slug, description, active) VALUES
    -- Parents
    (1,  NULL, 'Oreille',  'oreille',  'Piercings pour l''oreille',        true),
    (2,  NULL, 'Nez',      'nez',      'Piercings pour le nez',            true),
    (3,  NULL, 'Lèvres',   'labret',   'Piercings pour les lèvres',        true),
    (4,  NULL, 'Nombril',  'nombril',  'Piercings pour le nombril',        true),
    -- Sous-catégories Oreille
    (5,  1, 'Lobe',    'lobe',    'Piercings pour le lobe',           true),
    (6,  1, 'Hélix',   'helix',   'Piercings pour l''hélix',          true),
    (7,  1, 'Conch',   'conch',   'Piercings pour le conch',          true),
    (8,  1, 'Daith',   'daith',   'Piercings pour le daith',          true),
    (9,  1, 'Tragus',  'tragus',  'Piercings pour le tragus',         true),
    -- Sous-catégories Nez
    (10, 2, 'Narine',  'narine',  'Piercings pour la narine',         true),
    (11, 2, 'Septum',  'septum',  'Piercings pour le septum',         true);

SELECT setval('categories_id_seq', 11);

-- ── Tags ─────────────────────────────────────────────────────
INSERT INTO tags (id, name, slug) VALUES
    (1, 'Bestseller',    'bestseller'),
    (2, 'Nouveauté',     'nouveaute'),
    (3, 'Titane',        'titane'),
    (4, 'Or',            'or'),
    (5, 'Argent',        'argent'),
    (6, 'Nickel Free',   'nickel-free'),
    (7, 'Serti',         'serti'),
    (8, 'Minimaliste',   'minimaliste');

SELECT setval('tags_id_seq', 8);

-- ── Users ────────────────────────────────────────────────────
-- Mot de passe : Admin1234!
INSERT INTO users (id, email, password_hash, role) VALUES
    (1, 'admin@piercingbysamar.com', '$2b$12$ThX.lVfZRQ/2LLqsLG9cve3Mbwk6NroUBVzHe6QT4QnVspdDNnn42', 'ADMIN');

SELECT setval('users_id_seq', 1);

-- ── Products ─────────────────────────────────────────────────
INSERT INTO products (id, category_id, name, slug, description, material, nickel_free, meta_title, meta_description, active) VALUES
    (1, 6, 'Anneau Segment Clicker Titanium',
        'anneau-segment-clicker-titanium',
        'Anneau segment clicker en titane ASTM F136. Idéal pour l''hélix, le daith ou le conch. Ouverture facile par pression.',
        'TITANIUM', true,
        'Anneau Segment Clicker Titanium | Piercing by Samar',
        'Anneau segment clicker en titane ASTM F136, nickel free. Disponible en gold et silver.',
        true),
    (2, 9, 'Labret Flat Back Titanium',
        'labret-flat-back-titanium',
        'Labret à dos plat en titane ASTM F136. Confortable pour le tragus, le lobe ou le labret. Filetage interne.',
        'TITANIUM', true,
        'Labret Flat Back Titanium | Piercing by Samar',
        'Labret dos plat titane ASTM F136, filetage interne. Nickel free, disponible en plusieurs tailles.',
        true),
    (3, 11, 'Anneau Clicker Serti Zirconiums',
        'anneau-clicker-serti-zirconiums',
        'Anneau clicker serti de zirconiums en titane ASTM F136. Élégant et résistant, parfait pour le septum.',
        'TITANIUM', true,
        'Anneau Clicker Serti Zirconiums Septum | Piercing by Samar',
        'Anneau clicker septum en titane, serti de zirconiums. Hypoallergénique, nickel free.',
        true),
    (4, 10, 'Nostril Stud Titanium',
        'nostril-stud-titanium',
        'Stud narine en titane ASTM F136 avec embout boule. Léger, discret et hypoallergénique.',
        'TITANIUM', true,
        'Nostril Stud Titanium | Piercing by Samar',
        'Stud narine en titane ASTM F136. Nickel free, disponible en gold et silver.',
        true),
    (5, 4, 'Banana Bell Nombril Acier',
        'banana-bell-nombril-acier',
        'Banana bell pour nombril en acier chirurgical 316L. Pendentif étoile serti. Résistant et élégant.',
        'STEEL', false,
        'Banana Bell Nombril Acier Chirurgical | Piercing by Samar',
        'Banana bell nombril en acier chirurgical 316L, pendentif étoile. Disponible en silver et gold.',
        true),
    (6, 7, 'Anneau Conch Titanium Pave',
        'anneau-conch-titanium-pave',
        'Anneau pour conch en titane pavé de zirconiums. Effet maximaliste et sophistiqué.',
        'TITANIUM', true,
        'Anneau Conch Pavé Zirconiums | Piercing by Samar',
        'Anneau conch en titane ASTM F136, pavé de zirconiums. Nickel free.',
        true);

SELECT setval('products_id_seq', 6);

-- ── Product Variants ─────────────────────────────────────────
-- price_cents = centimes de Dirham Marocain (MAD)
INSERT INTO product_variants (id, product_id, sku, size, color, price_cents, stock, active) VALUES
    -- Anneau Segment Clicker Titanium (produit 1)
    (1,  1, 'ASC-TI-6-GOLD',   '6mm',  'Gold',   22000, 15, true),
    (2,  1, 'ASC-TI-6-SIL',    '6mm',  'Silver', 22000, 20, true),
    (3,  1, 'ASC-TI-8-GOLD',   '8mm',  'Gold',   25000, 10, true),
    (4,  1, 'ASC-TI-8-SIL',    '8mm',  'Silver', 25000, 12, true),
    (5,  1, 'ASC-TI-10-GOLD',  '10mm', 'Gold',   28000, 5,  true),
    -- Labret Flat Back (produit 2)
    (6,  2, 'LFB-TI-6-GOLD',   '6mm',  'Gold',   18000, 25, true),
    (7,  2, 'LFB-TI-6-SIL',    '6mm',  'Silver', 18000, 30, true),
    (8,  2, 'LFB-TI-8-GOLD',   '8mm',  'Gold',   20000, 18, true),
    (9,  2, 'LFB-TI-8-SIL',    '8mm',  'Silver', 20000, 22, true),
    (10, 2, 'LFB-TI-10-GOLD',  '10mm', 'Gold',   23000, 8,  true),
    (11, 2, 'LFB-TI-10-SIL',   '10mm', 'Silver', 23000, 10, true),
    -- Anneau Clicker Serti Septum (produit 3)
    (12, 3, 'ACS-TI-8-GOLD',   '8mm',  'Gold',   32000, 8,  true),
    (13, 3, 'ACS-TI-10-GOLD',  '10mm', 'Gold',   35000, 6,  true),
    (14, 3, 'ACS-TI-8-SIL',    '8mm',  'Silver', 32000, 9,  true),
    -- Nostril Stud (produit 4)
    (15, 4, 'NS-TI-6-GOLD',    '6mm',  'Gold',   15000, 30, true),
    (16, 4, 'NS-TI-6-SIL',     '6mm',  'Silver', 15000, 35, true),
    (17, 4, 'NS-TI-8-GOLD',    '8mm',  'Gold',   17000, 20, true),
    -- Banana Bell Nombril (produit 5)
    (18, 5, 'BBN-ST-10-SIL',   '10mm', 'Silver', 18000, 15, true),
    (19, 5, 'BBN-ST-10-GOLD',  '10mm', 'Gold',   18000, 12, true),
    (20, 5, 'BBN-ST-12-SIL',   '12mm', 'Silver', 20000, 8,  true),
    -- Anneau Conch Pavé (produit 6)
    (21, 6, 'ACP-TI-8-GOLD',   '8mm',  'Gold',   39000, 5,  true),
    (22, 6, 'ACP-TI-10-GOLD',  '10mm', 'Gold',   43000, 3,  true),
    (23, 6, 'ACP-TI-8-SIL',    '8mm',  'Silver', 39000, 4,  true),
    -- Variante en rupture pour tester le comportement
    (24, 2, 'LFB-TI-12-GOLD',  '12mm', 'Gold',   26000, 0,  true);

SELECT setval('product_variants_id_seq', 24);

-- ── Product Tags ─────────────────────────────────────────────
INSERT INTO product_tags (product_id, tag_id) VALUES
    (1, 1), (1, 3), (1, 6),         -- Anneau segment : bestseller, titane, nickel free
    (2, 3), (2, 6), (2, 8),         -- Labret : titane, nickel free, minimaliste
    (3, 1), (3, 3), (3, 6), (3, 7), -- Clicker septum : bestseller, titane, nickel free, serti
    (4, 2), (4, 3), (4, 6), (4, 8), -- Nostril : nouveauté, titane, nickel free, minimaliste
    (5, 1), (5, 7),                  -- Banana bell : bestseller, serti
    (6, 2), (6, 3), (6, 6), (6, 7); -- Conch pavé : nouveauté, titane, nickel free, serti

-- ── Orders ───────────────────────────────────────────────────
INSERT INTO orders (id, reference, order_type, status, customer_email, customer_name, customer_phone,
                    shipping_address, shipping_city, shipping_postal_code, shipping_country,
                    subtotal_cents, shipping_cost_cents, total_cents, currency,
                    stripe_session_id, stripe_payment_intent_id, notes) VALUES
    -- Commande livrée (shipping France)
    (1, 'PBS-2026-0001', 'SHIPPING', 'DELIVERED',
        'camille.dupont@gmail.com', 'Camille Dupont', '+33612345678',
        '12 rue des Lilas', 'Paris', '75011', 'FR',
        42000, 5000, 47000, 'MAD',
        'cs_test_aaa111', 'pi_test_aaa111', NULL),
    -- Commande payée en cours d''expédition (shipping Belgique)
    (2, 'PBS-2026-0002', 'SHIPPING', 'SHIPPED',
        'lea.martin@hotmail.be', 'Léa Martin', '+32471234567',
        '5 avenue du Roi', 'Bruxelles', '1060', 'BE',
        74000, 7500, 81500, 'MAD',
        'cs_test_bbb222', 'pi_test_bbb222', NULL),
    -- Commande en attente paiement Stripe
    (3, 'PBS-2026-0003', 'SHIPPING', 'PENDING',
        'sofia.ali@gmail.com', 'Sofia Ali', NULL,
        '8 Calle Mayor', 'Madrid', '28013', 'ES',
        32000, 9500, 41500, 'MAD',
        'cs_test_ccc333', NULL, NULL),
    -- Click & Collect prête à récupérer
    (4, 'PBS-2026-0004', 'CLICK_COLLECT', 'READY',
        'yasmine.benali@gmail.com', 'Yasmine Benali', '+212612345678',
        NULL, NULL, NULL, NULL,
        51000, 0, 51000, 'MAD',
        NULL, NULL, 'Préférence paiement cash'),
    -- Click & Collect récupérée
    (5, 'PBS-2026-0005', 'CLICK_COLLECT', 'COLLECTED',
        'nour.hassan@gmail.com', 'Nour Hassan', '+212698765432',
        NULL, NULL, NULL, NULL,
        15000, 0, 15000, 'MAD',
        NULL, NULL, NULL),
    -- Commande annulée
    (6, 'PBS-2026-0006', 'SHIPPING', 'CANCELLED',
        'test.cancel@gmail.com', 'Test Cancel', NULL,
        '1 rue Test', 'Lyon', '69001', 'FR',
        22000, 5000, 27000, 'MAD',
        'cs_test_ddd444', NULL, 'Annulée par le client');

SELECT setval('orders_id_seq', 6);

-- ── Order Items ──────────────────────────────────────────────
INSERT INTO order_items (order_id, product_variant_id, snapshot_product_name, snapshot_variant_label, unit_price_cents, quantity, total_cents) VALUES
    -- Commande 1 (subtotal 42000 MAD) : 1x Anneau Segment Gold 6mm + 1x Labret Silver 8mm
    (1, 1,  'Anneau Segment Clicker Titanium', '6mm - Gold',   22000, 1, 22000),
    (1, 9,  'Labret Flat Back Titanium',       '8mm - Silver', 20000, 1, 20000),
    -- Commande 2 (subtotal 74000 MAD) : 1x Clicker Septum Gold 10mm + 1x Anneau Conch Gold 8mm
    (2, 13, 'Anneau Clicker Serti Zirconiums', '10mm - Gold',  35000, 1, 35000),
    (2, 21, 'Anneau Conch Titanium Pavé',      '8mm - Gold',   39000, 1, 39000),
    -- Commande 3 (subtotal 32000 MAD) : 1x Clicker Septum Silver 8mm
    (3, 14, 'Anneau Clicker Serti Zirconiums', '8mm - Silver', 32000, 1, 32000),
    -- Commande 4 (subtotal 51000 MAD) : 1x Labret Gold 6mm + 1x Nostril Gold 6mm + 1x Banana Bell Silver
    (4, 6,  'Labret Flat Back Titanium',       '6mm - Gold',   18000, 1, 18000),
    (4, 15, 'Nostril Stud Titanium',           '6mm - Gold',   15000, 1, 15000),
    (4, 18, 'Banana Bell Nombril Acier',       '10mm - Silver',18000, 1, 18000),
    -- Commande 5 (subtotal 15000 MAD) : 1x Nostril Stud Silver 6mm
    (5, 16, 'Nostril Stud Titanium',           '6mm - Silver', 15000, 1, 15000),
    -- Commande 6 (subtotal 22000 MAD) : 1x Anneau Segment Gold 6mm (annulée)
    (6, 1,  'Anneau Segment Clicker Titanium', '6mm - Gold',   22000, 1, 22000);
