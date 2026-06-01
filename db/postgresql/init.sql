
-- Admin login: admin@gmail.com / Admin12345

-- ========== TABLES ==========

CREATE TABLE IF NOT EXISTS roles (
  id BIGSERIAL PRIMARY KEY,
  name VARCHAR(32) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS users (
  id BIGSERIAL PRIMARY KEY,
  email VARCHAR(120) NOT NULL UNIQUE,
  full_name VARCHAR(120) NOT NULL,
  password_hash TEXT NOT NULL,
  enabled BOOLEAN NOT NULL DEFAULT TRUE,
  created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS user_roles (
  user_id BIGINT NOT NULL,
  role_id BIGINT NOT NULL,
  PRIMARY KEY (user_id, role_id),
  CONSTRAINT fk_user_roles_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
  CONSTRAINT fk_user_roles_role FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE RESTRICT
);

CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);
CREATE INDEX IF NOT EXISTS idx_user_roles_role_id ON user_roles(role_id);

CREATE TABLE IF NOT EXISTS products (
  id BIGSERIAL PRIMARY KEY,
  name VARCHAR(160) NOT NULL,
  description VARCHAR(2000),
  price NUMERIC(12, 2) NOT NULL,
  category VARCHAR(80) NOT NULL,
  image_url VARCHAR(500),
  is_available BOOLEAN NOT NULL DEFAULT TRUE,
  is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
  deleted_at TIMESTAMPTZ,
  created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_products_name ON products(name);
CREATE INDEX IF NOT EXISTS idx_products_category ON products(category);
CREATE INDEX IF NOT EXISTS idx_products_available ON products(is_available);
CREATE INDEX IF NOT EXISTS idx_products_deleted ON products(is_deleted);

CREATE TABLE IF NOT EXISTS orders (
  id BIGSERIAL PRIMARY KEY,
  customer_id BIGINT NOT NULL,
  status VARCHAR(20) NOT NULL,
  total_amount NUMERIC(12, 2) NOT NULL DEFAULT 0,
  created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  CONSTRAINT fk_orders_customer FOREIGN KEY (customer_id) REFERENCES users(id) ON DELETE RESTRICT
);

CREATE INDEX IF NOT EXISTS idx_orders_customer_id ON orders(customer_id);
CREATE INDEX IF NOT EXISTS idx_orders_status ON orders(status);
CREATE INDEX IF NOT EXISTS idx_orders_created_at ON orders(created_at);

CREATE TABLE IF NOT EXISTS order_items (
  id BIGSERIAL PRIMARY KEY,
  order_id BIGINT NOT NULL,
  product_id BIGINT NOT NULL,
  quantity INT NOT NULL CHECK (quantity >= 1),
  unit_price NUMERIC(12, 2) NOT NULL,
  line_total NUMERIC(12, 2) NOT NULL,
  CONSTRAINT fk_order_items_order FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
  CONSTRAINT fk_order_items_product FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE RESTRICT
);

CREATE INDEX IF NOT EXISTS idx_order_items_order_id ON order_items(order_id);
CREATE INDEX IF NOT EXISTS idx_order_items_product_id ON order_items(product_id);

CREATE TABLE IF NOT EXISTS payments (
  id BIGSERIAL PRIMARY KEY,
  order_id BIGINT NOT NULL,
  amount NUMERIC(12, 2) NOT NULL,
  method VARCHAR(10) NOT NULL,
  status VARCHAR(10) NOT NULL,
  created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  CONSTRAINT fk_payments_order FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE RESTRICT
);

CREATE INDEX IF NOT EXISTS idx_payments_order_id ON payments(order_id);
CREATE INDEX IF NOT EXISTS idx_payments_status ON payments(status);

CREATE TABLE IF NOT EXISTS refresh_tokens (
  id BIGSERIAL PRIMARY KEY,
  token VARCHAR(512) NOT NULL UNIQUE,
  user_id BIGINT NOT NULL,
  expires_at TIMESTAMPTZ NOT NULL,
  revoked BOOLEAN NOT NULL DEFAULT FALSE,
  created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  CONSTRAINT fk_refresh_tokens_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_refresh_tokens_token ON refresh_tokens(token);
CREATE INDEX IF NOT EXISTS idx_refresh_tokens_user_id ON refresh_tokens(user_id);

-- ========== SEED DATA ==========

INSERT INTO roles (name) VALUES ('ADMIN'), ('CASHIER'), ('CUSTOMER')
ON CONFLICT (name) DO NOTHING;

INSERT INTO users (email, full_name, password_hash, enabled, created_at, updated_at)
VALUES (
  'admin@gmail.com',
  'Admin',
  '$2a$10$zYIeHx9OsKeCYLcr2YhXwePzXaiSPoK63MRyZRMrA9et3mral2fKu',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id FROM users u, roles r
WHERE u.email = 'admin@gmail.com' AND r.name = 'ADMIN';

INSERT INTO products (name, description, price, category, image_url, is_available, is_deleted, created_at, updated_at)
VALUES
  ('Espresso', 'Single shot, rich and bold', 2.50, 'Coffee', NULL, TRUE, FALSE, NOW(), NOW()),
  ('Americano', 'Espresso with hot water', 3.00, 'Coffee', NULL, TRUE, FALSE, NOW(), NOW()),
  ('Cappuccino', 'Espresso with steamed milk and foam', 3.75, 'Coffee', NULL, TRUE, FALSE, NOW(), NOW()),
  ('Latte', 'Espresso with steamed milk', 4.00, 'Coffee', NULL, TRUE, FALSE, NOW(), NOW()),
  ('Iced Coffee', 'Cold brew over ice', 4.25, 'Coffee', NULL, TRUE, FALSE, NOW(), NOW()),
  ('Green Tea', 'Hot jasmine green tea', 2.75, 'Tea', NULL, TRUE, FALSE, NOW(), NOW()),
  ('Mango Smoothie', 'Fresh mango blended with yogurt', 4.50, 'Smoothie', NULL, TRUE, FALSE, NOW(), NOW()),
  ('Croissant', 'Buttery baked croissant', 2.80, 'Pastry', NULL, TRUE, FALSE, NOW(), NOW()),
  ('Chocolate Muffin', 'Double chocolate chip muffin', 3.20, 'Pastry', NULL, TRUE, FALSE, NOW(), NOW()),
  ('Club Sandwich', 'Chicken, lettuce, tomato, mayo', 6.50, 'Food', NULL, TRUE, FALSE, NOW(), NOW());
