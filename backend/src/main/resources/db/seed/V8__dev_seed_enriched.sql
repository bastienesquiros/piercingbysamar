-- ============================================================
-- V8 - Seed enrichi : pagination, photos variantes, stats, FAQ
-- ============================================================

-- ── Category images ──────────────────────────────────────────
UPDATE categories SET image_url = 'https://picsum.photos/seed/cat-oreille/600/400'  WHERE id = 1;
UPDATE categories SET image_url = 'https://picsum.photos/seed/cat-nez/600/400'      WHERE id = 2;
UPDATE categories SET image_url = 'https://picsum.photos/seed/cat-labret/600/400'   WHERE id = 3;
UPDATE categories SET image_url = 'https://picsum.photos/seed/cat-nombril/600/400'  WHERE id = 4;
UPDATE categories SET image_url = 'https://picsum.photos/seed/cat-lobe/600/400'     WHERE id = 5;
UPDATE categories SET image_url = 'https://picsum.photos/seed/cat-helix/600/400'    WHERE id = 6;
UPDATE categories SET image_url = 'https://picsum.photos/seed/cat-conch/600/400'    WHERE id = 7;
UPDATE categories SET image_url = 'https://picsum.photos/seed/cat-daith/600/400'    WHERE id = 8;
UPDATE categories SET image_url = 'https://picsum.photos/seed/cat-tragus/600/400'   WHERE id = 9;
UPDATE categories SET image_url = 'https://picsum.photos/seed/cat-narine/600/400'   WHERE id = 10;
UPDATE categories SET image_url = 'https://picsum.photos/seed/cat-septum/600/400'   WHERE id = 11;


-- ── Photos par variante (produit 1 - Anneau Segment Clicker) ──
-- Variante Gold (ids 1,3,5)
INSERT INTO product_images (product_id, variant_id, r2_url, position, alt_text) VALUES
    (1, 1,  'https://picsum.photos/seed/asc-gold-1/800/800', 0, 'Anneau Segment Clicker Gold 6mm'),
    (1, 1,  'https://picsum.photos/seed/asc-gold-2/800/800', 1, 'Anneau Segment Clicker Gold 6mm - détail'),
    (1, 3,  'https://picsum.photos/seed/asc-gold-3/800/800', 0, 'Anneau Segment Clicker Gold 8mm'),
    -- Variante Silver (ids 2,4)
    (1, 2,  'https://picsum.photos/seed/asc-sil-1/800/800',  0, 'Anneau Segment Clicker Silver 6mm'),
    (1, 4,  'https://picsum.photos/seed/asc-sil-2/800/800',  0, 'Anneau Segment Clicker Silver 8mm'),
    -- Produit 2 - Labret variante Gold
    (2, 6,  'https://picsum.photos/seed/lfb-gold-1/800/800', 0, 'Labret Flat Back Gold 6mm'),
    (2, 8,  'https://picsum.photos/seed/lfb-gold-2/800/800', 0, 'Labret Flat Back Gold 8mm'),
    -- Produit 3 - Septum variantes
    (3, 12, 'https://picsum.photos/seed/acs-gold-8/800/800', 0, 'Anneau Clicker Serti Gold 8mm'),
    (3, 13, 'https://picsum.photos/seed/acs-gold-10/800/800',0, 'Anneau Clicker Serti Gold 10mm');

-- ── Produits supplémentaires (pagination) ────────────────────
INSERT INTO products (id, category_id, name, slug, description, material, nickel_free, meta_title, meta_description, active) VALUES
    (7,  5, 'Ear Stud Lobe Titanium',
        'ear-stud-lobe-titanium',
        'Stud lobe en titane ASTM F136. Léger et discret, parfait comme second ou troisième piercing.',
        'TITANIUM', true,
        'Ear Stud Lobe Titanium | Piercing by Samar', 'Stud lobe en titane ASTM F136. Nickel free.', true),
    (8,  8, 'Anneau Daith Clicker Gold',
        'anneau-daith-clicker-gold',
        'Anneau clicker en titane doré pour le daith. Ouverture fluide, port confortable.',
        'TITANIUM', true,
        'Anneau Daith Clicker Gold | Piercing by Samar', 'Anneau daith clicker doré en titane ASTM F136. Nickel free.', true),
    (9,  6, 'Hélix Stud Zirconium',
        'helix-stud-zirconium',
        'Stud hélix serti d''un zirconium en titane ASTM F136. Discret et élégant.',
        'TITANIUM', true,
        'Hélix Stud Zirconium | Piercing by Samar', 'Stud hélix zirconium titane ASTM F136. Nickel free.', true),
    (10, 3, 'Labret Lip Acier',
        'labret-lip-acier',
        'Labret à bille en acier chirurgical 316L. Confortable pour les lèvres, finition soignée.',
        'STEEL', false,
        'Labret Lip Acier Chirurgical | Piercing by Samar', 'Labret lèvres acier chirurgical 316L.', true),
    (11, 9, 'Tragus Flat Disc Titanium',
        'tragus-flat-disc-titanium',
        'Disc flat en titane pour le tragus. Ultra-plat, ne sort pas de l''oreille. Filetage interne.',
        'TITANIUM', true,
        'Tragus Flat Disc Titanium | Piercing by Samar', 'Disc flat tragus en titane ASTM F136. Nickel free.', true),
    (12, 5, 'Huggie Lobe Or',
        'huggie-lobe-or',
        'Créole huggie en titane anodisé doré. Serre le lobe sans dépasser.',
        'TITANIUM', true,
        'Huggie Lobe Or | Piercing by Samar', 'Créole huggie lobe en titane doré. Nickel free.', true),
    (13, 11, 'Septum Clicker Minimaliste',
        'septum-clicker-minimaliste',
        'Anneau clicker simple en titane pour le septum. Design épuré, fermeture précise.',
        'TITANIUM', true,
        'Septum Clicker Minimaliste | Piercing by Samar', 'Clicker septum minimaliste en titane ASTM F136. Nickel free.', true),
    (14, 10, 'Nostril L-Shape Acier',
        'nostril-l-shape-acier',
        'Stud narine en L en acier chirurgical 316L. Maintien optimal, discret au quotidien.',
        'STEEL', false,
        'Nostril L-Shape Acier | Piercing by Samar', 'Stud narine L-shape acier chirurgical 316L.', true),
    (15, 4, 'Nombril Flex Bioflex',
        'nombril-flex-bioflex',
        'Banana bell en bioflex pour le nombril. Flexible, idéal pour les grossesses ou sports.',
        'BIOFLEX', true,
        'Nombril Flex Bioflex | Piercing by Samar', 'Banana bell nombril en bioflex. Hypoallergénique.', true),
    (16, 7, 'Conch Stud Flat Titanium',
        'conch-stud-flat-titanium',
        'Stud flat back pour le conch en titane ASTM F136. Fin et précis, look minimaliste.',
        'TITANIUM', true,
        'Conch Stud Flat Titanium | Piercing by Samar', 'Stud flat back conch en titane ASTM F136. Nickel free.', true),
    (17, 6, 'Hélix Anneau CBR Acier',
        'helix-anneau-cbr-acier',
        'Anneau à boule captive pour l''hélix en acier chirurgical. Classique et résistant.',
        'STEEL', false,
        'Hélix Anneau CBR Acier | Piercing by Samar', 'Anneau CBR hélix acier chirurgical 316L.', true),
    (18, 5, 'Lobe Opal Stud Titanium',
        'lobe-opal-stud-titanium',
        'Stud lobe avec opale synthétique en titane ASTM F136. Effet irisé unique.',
        'TITANIUM', true,
        'Lobe Opal Stud Titanium | Piercing by Samar', 'Stud lobe opale en titane ASTM F136. Nickel free.', true),
    (19, 2, 'Septum Pincher Acier',
        'septum-pincher-acier',
        'Septum pincher en acier chirurgical avec billes dévissables.',
        'STEEL', false,
        'Septum Pincher Acier | Piercing by Samar', 'Septum pincher acier chirurgical 316L.', true),
    (20, 8, 'Daith Coeur Titanium',
        'daith-coeur-titanium',
        'Anneau clicker en forme de cœur pour le daith en titane anodisé. Original et féminin.',
        'TITANIUM', true,
        'Daith Cœur Titanium | Piercing by Samar', 'Anneau daith cœur en titane ASTM F136. Nickel free.', true),
    (21, 9, 'Tragus Anneau Segment Acier',
        'tragus-anneau-segment-acier',
        'Anneau segment en acier chirurgical pour le tragus. Fermeture discrète.',
        'STEEL', false,
        'Tragus Anneau Segment Acier | Piercing by Samar', 'Anneau segment tragus acier chirurgical 316L.', true),
    (22, 3, 'Vertical Labret Titanium',
        'vertical-labret-titanium',
        'Labret vertical en titane ASTM F136. Deux boules visibles, look statement.',
        'TITANIUM', true,
        'Vertical Labret Titanium | Piercing by Samar', 'Vertical labret en titane ASTM F136. Nickel free.', true);

SELECT setval('products_id_seq', 22);

-- ── Variants des nouveaux produits ───────────────────────────
INSERT INTO product_variants (id, product_id, sku, size, color, price_cents, stock, reserved_stock, active) VALUES
    -- Ear Stud Lobe (7)
    (25, 7, 'ESL-TI-6-GOLD',   '6mm',  'Gold',   16000, 25, 0, true),
    (26, 7, 'ESL-TI-6-SIL',    '6mm',  'Silver', 16000, 30, 0, true),
    (27, 7, 'ESL-TI-8-GOLD',   '8mm',  'Gold',   18000, 15, 0, true),
    -- Daith Clicker Gold (8)
    (28, 8, 'DCG-TI-8-GOLD',   '8mm',  'Gold',   28000, 10, 2, true),
    (29, 8, 'DCG-TI-10-GOLD',  '10mm', 'Gold',   31000, 8,  0, true),
    -- Hélix Stud Zirconium (9)
    (30, 9, 'HSZ-TI-6-GOLD',   '6mm',  'Gold',   19000, 20, 0, true),
    (31, 9, 'HSZ-TI-6-SIL',    '6mm',  'Silver', 19000, 22, 0, true),
    -- Labret Lip Acier (10)
    (32, 10,'LLA-ST-8-SIL',    '8mm',  'Silver', 12000, 40, 0, true),
    (33, 10,'LLA-ST-10-SIL',   '10mm', 'Silver', 13000, 35, 0, true),
    (34, 10,'LLA-ST-8-GOLD',   '8mm',  'Gold',   12000, 20, 0, true),
    -- Tragus Flat Disc (11)
    (35, 11,'TFD-TI-6-GOLD',   '6mm',  'Gold',   20000, 18, 0, true),
    (36, 11,'TFD-TI-6-SIL',    '6mm',  'Silver', 20000, 20, 0, true),
    -- Huggie Lobe Or (12)
    (37, 12,'HLO-TI-8-GOLD',   '8mm',  'Gold',   24000, 12, 3, true),
    (38, 12,'HLO-TI-10-GOLD',  '10mm', 'Gold',   26000, 8,  0, true),
    -- Septum Clicker Minimaliste (13)
    (39, 13,'SCM-TI-8-GOLD',   '8mm',  'Gold',   21000, 15, 0, true),
    (40, 13,'SCM-TI-8-SIL',    '8mm',  'Silver', 21000, 18, 0, true),
    (41, 13,'SCM-TI-10-GOLD',  '10mm', 'Gold',   23000, 10, 0, true),
    -- Nostril L-Shape (14)
    (42, 14,'NLS-ST-6-SIL',    '6mm',  'Silver', 10000, 50, 0, true),
    (43, 14,'NLS-ST-6-GOLD',   '6mm',  'Gold',   10000, 40, 0, true),
    -- Nombril Flex (15)
    (44, 15,'NFB-BF-10-TRANS', '10mm', 'Transparent', 14000, 20, 0, true),
    (45, 15,'NFB-BF-10-ROSE',  '10mm', 'Rose',   14000, 15, 0, true),
    -- Conch Stud Flat (16)
    (46, 16,'CSF-TI-6-GOLD',   '6mm',  'Gold',   19000, 14, 0, true),
    (47, 16,'CSF-TI-6-SIL',    '6mm',  'Silver', 19000, 16, 0, true),
    -- Hélix Anneau CBR (17)
    (48, 17,'HCB-ST-8-SIL',    '8mm',  'Silver', 11000, 35, 0, true),
    (49, 17,'HCB-ST-10-SIL',   '10mm', 'Silver', 12000, 28, 0, true),
    -- Lobe Opal Stud (18)
    (50, 18,'LOS-TI-6-BLUE',   '6mm',  'Bleu',   22000, 10, 0, true),
    (51, 18,'LOS-TI-6-WHITE',  '6mm',  'Blanc',  22000, 12, 0, true),
    -- Septum Pincher (19)
    (52, 19,'SPP-ST-8-SIL',    '8mm',  'Silver', 9000,  40, 0, true),
    (53, 19,'SPP-ST-10-SIL',   '10mm', 'Silver', 10000, 32, 0, true),
    -- Daith Coeur (20)
    (54, 20,'DCO-TI-8-ROSE',   '8mm',  'Rose',   27000, 9,  0, true),
    (55, 20,'DCO-TI-8-GOLD',   '8mm',  'Gold',   27000, 7,  0, true),
    -- Tragus Anneau Segment (21)
    (56, 21,'TAS-ST-8-SIL',    '8mm',  'Silver', 13000, 30, 0, true),
    -- Vertical Labret (22)
    (57, 22,'VLT-TI-8-GOLD',   '8mm',  'Gold',   26000, 8,  0, true),
    (58, 22,'VLT-TI-8-SIL',    '8mm',  'Silver', 26000, 10, 0, true);

SELECT setval('product_variants_id_seq', 58);

-- ── Tags des nouveaux produits ────────────────────────────────
INSERT INTO product_tags (product_id, tag_id) VALUES
    (7,  3),(7,  6),(7,  8),
    (8,  1),(8,  3),(8,  6),(8,  4),
    (9,  3),(9,  6),(9,  7),
    (10, 8),
    (11, 3),(11, 6),(11, 8),
    (12, 1),(12, 3),(12, 6),(12, 4),
    (13, 3),(13, 6),(13, 8),
    (14, 8),
    (15, 6),
    (16, 3),(16, 6),(16, 8),
    (17, 8),
    (18, 2),(18, 3),(18, 6),(18, 7),
    (19, 8),
    (20, 2),(20, 3),(20, 6),
    (21, 8),
    (22, 2),(22, 3),(22, 6);

-- ── Images des nouveaux produits ─────────────────────────────
INSERT INTO product_images (product_id, variant_id, r2_url, position, alt_text) VALUES
    (7,  NULL, 'https://picsum.photos/seed/esl-1/800/800',  0, 'Ear Stud Lobe Titanium - vue principale'),
    (8,  NULL, 'https://picsum.photos/seed/dcg-1/800/800',  0, 'Anneau Daith Clicker Gold'),
    (9,  NULL, 'https://picsum.photos/seed/hsz-1/800/800',  0, 'Hélix Stud Zirconium'),
    (10, NULL, 'https://picsum.photos/seed/lla-1/800/800',  0, 'Labret Lip Acier'),
    (11, NULL, 'https://picsum.photos/seed/tfd-1/800/800',  0, 'Tragus Flat Disc Titanium'),
    (12, NULL, 'https://picsum.photos/seed/hlo-1/800/800',  0, 'Huggie Lobe Or'),
    (13, NULL, 'https://picsum.photos/seed/scm-1/800/800',  0, 'Septum Clicker Minimaliste'),
    (14, NULL, 'https://picsum.photos/seed/nls-1/800/800',  0, 'Nostril L-Shape Acier'),
    (15, NULL, 'https://picsum.photos/seed/nfb-1/800/800',  0, 'Nombril Flex Bioflex'),
    (16, NULL, 'https://picsum.photos/seed/csf-1/800/800',  0, 'Conch Stud Flat Titanium'),
    (17, NULL, 'https://picsum.photos/seed/hcb-1/800/800',  0, 'Hélix Anneau CBR Acier'),
    (18, NULL, 'https://picsum.photos/seed/los-1/800/800',  0, 'Lobe Opal Stud Titanium'),
    (19, NULL, 'https://picsum.photos/seed/spp-1/800/800',  0, 'Septum Pincher Acier'),
    (20, NULL, 'https://picsum.photos/seed/dco-1/800/800',  0, 'Daith Coeur Titanium'),
    (21, NULL, 'https://picsum.photos/seed/tas-1/800/800',  0, 'Tragus Anneau Segment Acier'),
    (22, NULL, 'https://picsum.photos/seed/vlt-1/800/800',  0, 'Vertical Labret Titanium'),
    -- Photos variantes produit 12 (huggie gold)
    (12, 37,   'https://picsum.photos/seed/hlo-gold-8/800/800', 0, 'Huggie Lobe Gold 8mm'),
    (12, 38,   'https://picsum.photos/seed/hlo-gold-10/800/800',0, 'Huggie Lobe Gold 10mm'),
    -- Photos variantes produit 20 (daith cœur)
    (20, 54,   'https://picsum.photos/seed/dco-rose/800/800',   0, 'Daith Cœur Rose'),
    (20, 55,   'https://picsum.photos/seed/dco-gold/800/800',   0, 'Daith Cœur Gold');

-- ── Commandes supplémentaires pour les stats ─────────────────
-- Ordres répartis sur ~30 jours pour avoir un graphique intéressant
INSERT INTO orders (id, reference, order_type, status, customer_email, customer_name, customer_phone,
                    subtotal_cents, shipping_cost_cents, total_cents, currency, created_at) VALUES
    (7,  'PBS-2026-0007', 'CLICK_COLLECT', 'COLLECTED', 'sara.idrissi@gmail.com', 'Sara Idrissi', '+212611111111', 47000, 0, 47000, 'MAD', NOW() - INTERVAL '28 days'),
    (8,  'PBS-2026-0008', 'CLICK_COLLECT', 'COLLECTED', 'fatima.z@gmail.com',     'Fatima Z.',     '+212622222222', 21000, 0, 21000, 'MAD', NOW() - INTERVAL '25 days'),
    (9,  'PBS-2026-0009', 'CLICK_COLLECT', 'COLLECTED', 'layla.h@gmail.com',      'Layla H.',      '+212633333333', 32000, 0, 32000, 'MAD', NOW() - INTERVAL '22 days'),
    (10, 'PBS-2026-0010', 'CLICK_COLLECT', 'COLLECTED', 'amira.k@gmail.com',      'Amira K.',      '+212644444444', 28000, 0, 28000, 'MAD', NOW() - INTERVAL '20 days'),
    (11, 'PBS-2026-0011', 'CLICK_COLLECT', 'COLLECTED', 'rim.t@gmail.com',        'Rim T.',         '+212655555555', 55000, 0, 55000, 'MAD', NOW() - INTERVAL '17 days'),
    (12, 'PBS-2026-0012', 'CLICK_COLLECT', 'COLLECTED', 'hind.b@gmail.com',       'Hind B.',        '+212666666666', 39000, 0, 39000, 'MAD', NOW() - INTERVAL '15 days'),
    (13, 'PBS-2026-0013', 'CLICK_COLLECT', 'COLLECTED', 'soukaina.m@gmail.com',   'Soukaina M.',    '+212677777777', 43000, 0, 43000, 'MAD', NOW() - INTERVAL '12 days'),
    (14, 'PBS-2026-0014', 'CLICK_COLLECT', 'COLLECTED', 'kenza.a@gmail.com',      'Kenza A.',       '+212688888888', 16000, 0, 16000, 'MAD', NOW() - INTERVAL '10 days'),
    (15, 'PBS-2026-0015', 'CLICK_COLLECT', 'COLLECTED', 'malika.o@gmail.com',     'Malika O.',      '+212699999999', 62000, 0, 62000, 'MAD', NOW() - INTERVAL '8 days'),
    (16, 'PBS-2026-0016', 'CLICK_COLLECT', 'READY',     'nadia.ch@gmail.com',     'Nadia Ch.',      '+212700000001', 24000, 0, 24000, 'MAD', NOW() - INTERVAL '3 days'),
    (17, 'PBS-2026-0017', 'CLICK_COLLECT', 'READY',     'zineb.el@gmail.com',     'Zineb El.',      '+212700000002', 51000, 0, 51000, 'MAD', NOW() - INTERVAL '2 days'),
    (18, 'PBS-2026-0018', 'CLICK_COLLECT', 'CLICK_COLLECT_PENDING', 'amal.f@gmail.com', 'Amal F.', '+212700000003', 27000, 0, 27000, 'MAD', NOW() - INTERVAL '1 day'),
    (19, 'PBS-2026-0019', 'CLICK_COLLECT', 'CLICK_COLLECT_PENDING', 'houda.g@gmail.com','Houda G.', '+212700000004', 35000, 0, 35000, 'MAD', NOW());

SELECT setval('orders_id_seq', 19);

-- Items pour les nouvelles commandes
INSERT INTO order_items (order_id, product_variant_id, snapshot_product_name, snapshot_variant_label, unit_price_cents, quantity, total_cents) VALUES
    (7,  37,  'Huggie Lobe Or',             '8mm - Gold',      24000, 1, 24000),
    (7,  25,  'Ear Stud Lobe Titanium',     '6mm - Gold',      16000, 1, 16000),
    (7,  26,  'Ear Stud Lobe Titanium',     '6mm - Silver',    16000, 1, 16000),
    (8,  39,  'Septum Clicker Minimaliste', '8mm - Gold',      21000, 1, 21000),
    (9,  12,  'Anneau Clicker Serti Zirconiums','8mm - Gold',  32000, 1, 32000),
    (10, 30,  'Hélix Stud Zirconium',       '6mm - Gold',      19000, 1, 19000),
    (10, 26,  'Ear Stud Lobe Titanium',     '6mm - Silver',    16000, 1, 16000),
    (11, 37,  'Huggie Lobe Or',             '8mm - Gold',      24000, 1, 24000),
    (11, 13,  'Anneau Clicker Serti Zirconiums','10mm - Gold', 35000, 1, 35000),
    (12, 21,  'Anneau Conch Titanium Pavé', '8mm - Gold',      39000, 1, 39000),
    (13, 3,   'Anneau Segment Clicker Titanium','8mm - Gold',  25000, 1, 25000),
    (13, 35,  'Tragus Flat Disc Titanium',  '6mm - Gold',      20000, 1, 20000),
    (14, 16,  'Nostril Stud Titanium',      '6mm - Silver',    15000, 1, 15000),
    (15, 37,  'Huggie Lobe Or',             '8mm - Gold',      24000, 1, 24000),
    (15, 54,  'Daith Coeur Titanium',       '8mm - Rose',      27000, 1, 27000),
    (15, 50,  'Lobe Opal Stud Titanium',    '6mm - Bleu',      22000, 1, 22000),
    (16, 25,  'Ear Stud Lobe Titanium',     '6mm - Gold',      16000, 1, 16000),
    (16, 31,  'Hélix Stud Zirconium',       '6mm - Silver',    19000, 1, 19000),
    (17, 37,  'Huggie Lobe Or',             '8mm - Gold',      24000, 1, 24000),
    (17, 12,  'Anneau Clicker Serti Zirconiums','8mm - Gold',  32000, 1, 32000),
    (18, 39,  'Septum Clicker Minimaliste', '8mm - Gold',      21000, 1, 21000),
    (18, 26,  'Ear Stud Lobe Titanium',     '6mm - Silver',    16000, 1, 16000),
    (19, 54,  'Daith Coeur Titanium',       '8mm - Rose',      27000, 1, 27000),
    (19, 30,  'Hélix Stud Zirconium',       '6mm - Gold',      19000, 1, 19000);

-- Corriger les totaux commande 10 (order_item with 0 quantity was placeholder)
DELETE FROM order_items WHERE order_id = 10 AND quantity = 0;
UPDATE orders SET subtotal_cents = 35000, total_cents = 35000 WHERE id = 10;

-- ── FAQ ───────────────────────────────────────────────────────
TRUNCATE TABLE faq_items RESTART IDENTITY CASCADE;
INSERT INTO faq_items (id, question, answer, position, active) VALUES
    (1, 'Quels matériaux utilisez-vous ?',
        'Nous utilisons principalement le titane ASTM F136 et l''acier chirurgical 316L, certifiés biocompatibles et hypoallergéniques. Certains articles sont en bioflex pour plus de flexibilité. Tous nos bijoux Nickel Free sont clairement indiqués.',
        0, true),
    (2, 'Comment fonctionne le Click & Collect ?',
        'Vous passez votre commande en ligne, nous préparons votre commande à notre boutique à Marrakech et vous recevez une notification quand elle est prête. Vous venez la récupérer et payez sur place (cash ou carte).',
        1, true),
    (3, 'Combien de temps faut-il pour que ma commande soit prête ?',
        'En général, votre commande est prête dans la journée ou le lendemain. Vous recevrez un message de confirmation dès qu''elle est disponible.',
        2, true),
    (4, 'Puis-je prendre rendez-vous pour un piercing ?',
        'Oui ! Utilisez notre formulaire de prise de rendez-vous sur le site. Nous vous répondrons via WhatsApp pour confirmer le créneau.',
        3, true),
    (5, 'Comment entretenir mon piercing ?',
        'Nettoyez 2 fois par jour avec une solution saline (spray eau de mer). Évitez de toucher avec des mains non lavées. Ne retirez pas le bijou pendant la cicatrisation (3 à 6 mois selon le placement).',
        4, true),
    (6, 'Puis-je retourner un bijou ?',
        'Les produits non portés peuvent être retournés dans les 7 jours. Pour des raisons d''hygiène, les piercings déjà portés ne sont pas repris. Contactez-nous via WhatsApp pour tout retour.',
        5, true),
    (7, 'Avez-vous des bijoux pour les peaux sensibles ?',
        'Oui, tous nos bijoux en titane ASTM F136 sont Nickel Free et adaptés aux peaux les plus sensibles. Ils sont filtrés sur le catalogue avec le badge "Nickel Free".',
        6, true),
    (8, 'Où êtes-vous situés ?',
        'Notre boutique est à Marrakech, Maroc. Retrouvez l''adresse exacte dans le pied de page du site. Le Click & Collect est exclusivement disponible en boutique.',
        7, true);

SELECT setval('faq_items_id_seq', 8);
