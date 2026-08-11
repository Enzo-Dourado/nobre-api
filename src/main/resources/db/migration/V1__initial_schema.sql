CREATE TABLE users (
 id BIGSERIAL PRIMARY KEY, name VARCHAR(120) NOT NULL, email VARCHAR(180) UNIQUE NOT NULL,
 password_hash VARCHAR(100) NOT NULL, phone VARCHAR(30), role VARCHAR(20) NOT NULL DEFAULT 'CUSTOMER', created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);
CREATE TABLE products (
 id BIGSERIAL PRIMARY KEY, slug VARCHAR(160) UNIQUE NOT NULL, name VARCHAR(180) NOT NULL,
 category VARCHAR(80) NOT NULL, category_label VARCHAR(100) NOT NULL, price NUMERIC(10,2) NOT NULL CHECK(price>0),
 old_price NUMERIC(10,2), image_url VARCHAR(1000), description TEXT
);
CREATE TABLE product_sizes (product_id BIGINT NOT NULL REFERENCES products(id) ON DELETE CASCADE, size VARCHAR(20) NOT NULL);
CREATE TABLE orders (
 id BIGSERIAL PRIMARY KEY, user_id BIGINT NOT NULL REFERENCES users(id), total NUMERIC(10,2) NOT NULL CHECK(total>=0),
 status VARCHAR(30) NOT NULL DEFAULT 'pago', payment_method VARCHAR(20) NOT NULL CHECK(payment_method IN ('card','pix')),
 shipping_address TEXT NOT NULL, created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);
CREATE INDEX idx_orders_user_created ON orders(user_id, created_at DESC);
CREATE TABLE order_items (
 id BIGSERIAL PRIMARY KEY, order_id BIGINT NOT NULL REFERENCES orders(id) ON DELETE CASCADE,
 product_id BIGINT NOT NULL, product_name VARCHAR(180) NOT NULL, size VARCHAR(20), quantity INTEGER NOT NULL CHECK(quantity>0), price NUMERIC(10,2) NOT NULL CHECK(price>=0)
);
