-- Add more clients
INSERT INTO clients (name, email, phone, address) VALUES 
('Computer Store', 'sales@computerstore.com', '555-0201', '789 Tech Street'),
('Local School', 'office@school.edu', '555-0202', '456 Education Ave'),
('City Hospital', 'supply@hospital.org', '555-0203', '123 Health Blvd'),
('Game Shop', 'info@gameshop.com', '555-0204', '321 Gaming Lane'),
('Music Studio', 'contact@musicstudio.com', '555-0205', '654 Audio Way');

-- Add more products with existing categories
INSERT INTO products (name, price, quantity, category_id) VALUES 
-- Electronics (1)
('Dell Monitor 27"', 249.99, 10, 1),
('Apple Magic Mouse', 79.99, 15, 1),
('Mechanical Keyboard', 129.99, 20, 1),
('Wireless Headphones', 89.99, 25, 1),

-- Office Supplies (2)
('Premium Notebook', 14.99, 100, 2),
('Desk Calendar 2024', 9.99, 150, 2),
('Color Markers Set', 12.99, 80, 2),
('Document Holder', 19.99, 45, 2),

-- Furniture (3)
('Ergonomic Chair', 249.99, 8, 3),
('L-Shaped Desk', 199.99, 5, 3),
('Monitor Riser', 34.99, 20, 3),
('Cable Management', 24.99, 30, 3),

-- Books (4)
('JavaScript Guide', 44.99, 25, 4),
('Database Design', 39.99, 20, 4),
('Cloud Computing', 49.99, 15, 4),
('AI Fundamentals', 54.99, 12, 4),

-- Computer Parts (5)
('16GB RAM Kit', 89.99, 20, 5),
('2TB NVMe SSD', 159.99, 15, 5),
('RTX 4060 GPU', 399.99, 5, 5),
('750W PSU Gold', 89.99, 10, 5);

-- Add some orders
INSERT INTO orders (client_id, order_date, total_amount) VALUES 
(1, CURRENT_DATE - INTERVAL 1 DAY, 529.97),
(2, CURRENT_DATE - INTERVAL 2 DAY, 349.98),
(3, CURRENT_DATE - INTERVAL 3 DAY, 899.97);

-- Add order items (adjust product_id as needed based on your actual IDs)
INSERT INTO order_items (order_id, product_id, quantity, unit_price, total_price) VALUES 
((SELECT MAX(id)-2 FROM orders), (SELECT id FROM products WHERE name = 'Dell Monitor 27"'), 2, 249.99, 499.98),
((SELECT MAX(id)-1 FROM orders), (SELECT id FROM products WHERE name = 'Ergonomic Chair'), 1, 249.99, 249.99),
((SELECT MAX(id) FROM orders), (SELECT id FROM products WHERE name = 'RTX 4060 GPU'), 2, 399.99, 799.98); 