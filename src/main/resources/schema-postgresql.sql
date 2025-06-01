-- PostgreSQL schema for target database
\c target_db;

DROP TABLE IF EXISTS processed_orders;

CREATE TABLE processed_orders (
                                  id BIGSERIAL PRIMARY KEY,
                                  original_order_id BIGINT NOT NULL,
                                  customer_id BIGINT NOT NULL,
                                  product_name VARCHAR(255) NOT NULL,
                                  quantity INTEGER NOT NULL,
                                  unit_price DECIMAL(10,2) NOT NULL,
                                  total_amount DECIMAL(10,2) NOT NULL,
                                  discount_applied DECIMAL(5,2) DEFAULT 0.00,
                                  final_amount DECIMAL(10,2) NOT NULL,
                                  order_date TIMESTAMP NOT NULL,
                                  processed_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                  status VARCHAR(50) DEFAULT 'PROCESSED'
);

CREATE INDEX idx_processed_orders_customer_id ON processed_orders(customer_id);
CREATE INDEX idx_processed_orders_status ON processed_orders(status);
