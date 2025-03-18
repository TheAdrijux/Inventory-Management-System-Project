-- Drop the old database if exists
DROP DATABASE IF EXISTS prekiu_valdymo_sistema;
DROP DATABASE IF EXISTS inventory_management;

-- Create fresh database
CREATE DATABASE inventory_management
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

-- Switch to the new database
USE inventory_management;

-- Create tables
CREATE TABLE categories (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE products (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    price DECIMAL(10,2) NOT NULL,
    quantity INT NOT NULL DEFAULT 0,
    category_id BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (category_id) REFERENCES categories(id)
        ON DELETE SET NULL
        ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE clients (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100),
    phone VARCHAR(20),
    address TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE orders (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    client_id BIGINT NOT NULL,
    order_date DATETIME NOT NULL,
    total_amount DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    status ENUM('PENDING', 'PROCESSING', 'COMPLETED', 'CANCELLED') NOT NULL DEFAULT 'PENDING',
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (client_id) REFERENCES clients(id)
        ON DELETE RESTRICT
        ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE order_items (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    quantity INT NOT NULL,
    price_per_unit DECIMAL(10,2) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (order_id) REFERENCES orders(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
    FOREIGN KEY (product_id) REFERENCES products(id)
        ON DELETE RESTRICT
        ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Create indexes for better performance
CREATE INDEX idx_products_category ON products(category_id);
CREATE INDEX idx_orders_client ON orders(client_id);
CREATE INDEX idx_order_items_order ON order_items(order_id);
CREATE INDEX idx_order_items_product ON order_items(product_id);

-- Insert sample data
INSERT INTO categories (name) VALUES
    ('Electronics'),
    ('Office Supplies'),
    ('Furniture');

INSERT INTO products (name, description, price, quantity, category_id) VALUES
    ('Laptop', 'High-performance laptop', 999.99, 10, 1),
    ('Printer', 'Color laser printer', 299.99, 5, 1),
    ('Desk', 'Office desk', 199.99, 3, 3),
    ('Chair', 'Ergonomic office chair', 149.99, 8, 3),
    ('Paper', 'A4 paper ream', 4.99, 100, 2),
    ('Pens', 'Box of blue pens', 2.99, 50, 2);

INSERT INTO clients (name, email, phone) VALUES
    ('John Doe', 'john@example.com', '+1234567890'),
    ('Jane Smith', 'jane@example.com', '+0987654321'); 