-- MySQL schema for source database
CREATE DATABASE IF NOT EXISTS source_db;
USE source_db;

DROP TABLE IF EXISTS orders;

CREATE TABLE orders (
                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                        customer_id BIGINT NOT NULL,
                        product_name VARCHAR(255) NOT NULL,
                        quantity INT NOT NULL,
                        unit_price DECIMAL(10,2) NOT NULL,
                        order_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                        status VARCHAR(50) DEFAULT 'PENDING'
);

-- Insert sample data
INSERT INTO orders (customer_id, product_name, quantity, unit_price, status) VALUES
                                                                                 (1, 'Laptop Computer', 1, 999.99, 'PENDING'),
                                                                                 (2, 'Wireless Mouse', 2, 25.50, 'PENDING'),
                                                                                 (3, 'USB Keyboard', 1, 45.00, 'PENDING'),
                                                                                 (4, 'Monitor 24 inch', 2, 299.99, 'PENDING'),
                                                                                 (5, 'Desk Chair', 1, 150.00, 'PENDING'),
                                                                                 (6, 'Webcam HD', 1, 89.99, 'PENDING'),
                                                                                 (7, 'Headphones', 3, 79.99, 'PENDING'),
                                                                                 (8, 'Smartphone', 1, 699.99, 'PENDING'),
                                                                                 (9, 'Tablet', 1, 399.99, 'PENDING'),
                                                                                 (10, 'Printer', 1, 199.99, 'PENDING');
