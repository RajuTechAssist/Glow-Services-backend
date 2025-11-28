-- =====================================================
-- GLOW SERVICES DATABASE INITIALIZATION SCRIPT
-- =====================================================

-- 1. CREATE CATEGORIES FIRST
-- =====================================================
INSERT INTO categories (name, slug, description, icon, color, type, active, sort_order, created_at, updated_at)
VALUES 
    ('Bridal Makeup', 'bridal-makeup', 'Complete bridal makeup services for your special day', '👰', 'pink', 'SERVICE', true, 1, NOW(), NOW()),
    ('Hair Styling', 'hair-styling', 'Professional hair styling and treatments', '💇', 'purple', 'SERVICE', true, 2, NOW(), NOW()),
    ('Spa & Wellness', 'spa-wellness', 'Relaxing spa treatments and wellness services', '🧘', 'blue', 'SERVICE', true, 3, NOW(), NOW()),
    ('Facial Treatment', 'facial-treatment', 'Rejuvenating facial treatments', '✨', 'green', 'SERVICE', true, 4, NOW(), NOW()),
    ('Nail Care', 'nail-care', 'Manicure, pedicure and nail art services', '💅', 'red', 'SERVICE', true, 5, NOW(), NOW()),
    ('Skincare Products', 'skincare-products', 'Premium skincare products', '🧴', 'teal', 'PRODUCT', true, 6, NOW(), NOW()),
    ('Makeup Products', 'makeup-products', 'Professional makeup products', '💄', 'pink', 'PRODUCT', true, 7, NOW(), NOW())
ON CONFLICT (slug) DO NOTHING;

-- 2. CREATE SAMPLE SERVICES
-- =====================================================
INSERT INTO services (name, slug, category, price, original_price, duration, rating, reviews, description, long_description, image, gradient, popular, active, savings, created_at, updated_at)
VALUES
    (
        'Complete Bridal Makeup Package',
        'complete-bridal-makeup-package',
        'bridal-makeup',
        15000.00,
        20000.00,
        '3-4 hours',
        4.9,
        156,
        'Comprehensive bridal makeup package with HD makeup, hairstyling, draping, and more',
        'Our Complete Bridal Makeup Package is designed to make you look stunning on your special day. This package includes HD makeup application, professional hairstyling, saree/dupatta draping, false eyelash application, and complete touch-up kit. Our expert makeup artists use only premium, long-lasting products to ensure you look flawless throughout your wedding day.',
        'https://images.unsplash.com/photo-1519741497674-611481863552',
        'from-pink-400 to-rose-600',
        true,
        true,
        5000.00,
        NOW(),
        NOW()
    ),
    (
        'Pre-Bridal Package (7 Days)',
        'pre-bridal-package-7-days',
        'bridal-makeup',
        12000.00,
        15000.00,
        '7 sessions',
        4.8,
        89,
        '7-day pre-bridal treatment including facials, body polishing, hair spa and more',
        'Prepare for your big day with our intensive 7-day pre-bridal package. This comprehensive treatment includes multiple facial sessions, full body polishing, hair spa treatment, manicure, pedicure, and waxing. Each session is customized to your skin and hair type to ensure you achieve that perfect bridal glow.',
        'https://images.unsplash.com/photo-1560750588-73207b1ef5b8',
        'from-purple-400 to-pink-600',
        true,
        true,
        3000.00,
        NOW(),
        NOW()
    ),
    (
        'Engagement Makeup',
        'engagement-makeup',
        'bridal-makeup',
        8000.00,
        10000.00,
        '2-3 hours',
        4.7,
        124,
        'Elegant makeup for your engagement ceremony with hairstyling and draping',
        'Look your best at your engagement ceremony with our specialized engagement makeup service. This package includes airbrush makeup, hairstyling, outfit draping, and accessories arrangement. We create a sophisticated look that photographs beautifully while keeping you comfortable throughout the event.',
        'https://images.unsplash.com/photo-1487412947147-5cebf100ffc2',
        'from-rose-400 to-pink-500',
        false,
        true,
        2000.00,
        NOW(),
        NOW()
    )
ON CONFLICT (slug) DO NOTHING;

-- 3. ADD SERVICE FEATURES
-- =====================================================
INSERT INTO service_features (service_id, feature)
SELECT s.id, f.feature
FROM services s
CROSS JOIN (
    VALUES 
        ('HD Makeup Application'),
        ('Professional Hairstyling'),
        ('Saree/Dupatta Draping'),
        ('False Eyelashes'),
        ('Complete Touch-up Kit'),
        ('Premium Products'),
        ('5-Hour Stay Guarantee'),
        ('Free Trial Available')
) AS f(feature)
WHERE s.slug = 'complete-bridal-makeup-package'
ON CONFLICT DO NOTHING;

-- 4. ADD SERVICE BENEFITS
-- =====================================================
INSERT INTO service_benefits (service_id, benefit)
SELECT s.id, b.benefit
FROM services s
CROSS JOIN (
    VALUES 
        ('Long-lasting makeup that stays flawless'),
        ('Professional expertise with 10+ years experience'),
        ('Premium, skin-friendly products'),
        ('Customized to your skin tone and features'),
        ('Waterproof and sweat-proof formulas'),
        ('Complimentary makeup trial session')
) AS b(benefit)
WHERE s.slug = 'complete-bridal-makeup-package'
ON CONFLICT DO NOTHING;

-- 5. ADD SERVICE INCLUDES
-- =====================================================
INSERT INTO service_includes (service_id, service_item)
SELECT s.id, si.service_item
FROM services s
CROSS JOIN (
    VALUES 
        ('HD Airbrush Makeup'),
        ('Hair Styling & Setting'),
        ('Saree Draping'),
        ('Jewelry Arrangement'),
        ('Touch-up Kit'),
        ('False Lashes'),
        ('Complimentary Trial')
) AS si(service_item)
WHERE s.slug = 'complete-bridal-makeup-package'
ON CONFLICT DO NOTHING;

-- 6. ADD SERVICE GALLERY IMAGES
-- =====================================================
INSERT INTO service_gallery (service_id, image_url)
SELECT s.id, g.image_url
FROM services s
CROSS JOIN (
    VALUES 
        ('https://images.unsplash.com/photo-1519741497674-611481863552'),
        ('https://images.unsplash.com/photo-1487412947147-5cebf100ffc2'),
        ('https://images.unsplash.com/photo-1560750588-73207b1ef5b8'),
        ('https://images.unsplash.com/photo-1595433562696-a8542e0c4b30')
) AS g(image_url)
WHERE s.slug = 'complete-bridal-makeup-package'
ON CONFLICT DO NOTHING;
