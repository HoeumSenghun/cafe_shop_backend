
INSERT INTO products (name, description, price, category, image_url, is_available, is_deleted, created_at, updated_at)
VALUES
  (
    'Espresso',
    'Single shot, rich and bold',
    2.50,
    'Coffee',
    'https://example.com/images/espresso.jpg',
    true,
    false,
    now(),
    now()
  ),
  (
    'Americano',
    'Espresso with hot water',
    3.00,
    'Coffee',
    'https://example.com/images/americano.jpg',
    true,
    false,
    now(),
    now()
  ),
  (
    'Cappuccino',
    'Espresso with steamed milk and foam',
    3.75,
    'Coffee',
    'https://example.com/images/cappuccino.jpg',
    true,
    false,
    now(),
    now()
  ),
  (
    'Latte',
    'Espresso with steamed milk',
    4.00,
    'Coffee',
    'https://example.com/images/latte.jpg',
    true,
    false,
    now(),
    now()
  ),
  (
    'Iced Coffee',
    'Cold brew over ice',
    4.25,
    'Coffee',
    'https://example.com/images/iced-coffee.jpg',
    true,
    false,
    now(),
    now()
  ),
  (
    'Green Tea',
    'Hot jasmine green tea',
    2.75,
    'Tea',
    'https://example.com/images/green-tea.jpg',
    true,
    false,
    now(),
    now()
  ),
  (
    'Mango Smoothie',
    'Fresh mango blended with yogurt',
    4.50,
    'Smoothie',
    'https://example.com/images/mango-smoothie.jpg',
    true,
    false,
    now(),
    now()
  ),
  (
    'Croissant',
    'Buttery baked croissant',
    2.80,
    'Pastry',
    'https://example.com/images/croissant.jpg',
    true,
    false,
    now(),
    now()
  ),
  (
    'Chocolate Muffin',
    'Double chocolate chip muffin',
    3.20,
    'Pastry',
    'https://example.com/images/chocolate-muffin.jpg',
    true,
    false,
    now(),
    now()
  ),
  (
    'Club Sandwich',
    'Chicken, lettuce, tomato, mayo',
    6.50,
    'Food',
    'https://example.com/images/club-sandwich.jpg',
    true,
    false,
    now(),
    now()
  );

-- Verify
SELECT id, name, price, category, is_available FROM products WHERE is_deleted = false ORDER BY id;
