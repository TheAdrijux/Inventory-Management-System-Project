-- Categories
INSERT INTO categories (name) VALUES 
('Electronics'),
('Office Supplies'),
('Furniture'),
('Books'),
('Computer Parts'),
('Networking'),
('Software'),
('Gaming'),
('Audio Equipment'),
('Storage Solutions');

-- Products
INSERT INTO products (name, price, quantity, category_id) VALUES 
-- Electronics
('Samsung Monitor 24"', 199.99, 15, 1),
('Logitech Wireless Mouse', 29.99, 50, 1),
('HP Wireless Keyboard', 39.99, 30, 1),
('USB-C Cable 2m', 12.99, 100, 1),
('Wireless Charger', 24.99, 40, 1),
('Bluetooth Earbuds', 79.99, 25, 1),
('Webcam HD 1080p', 59.99, 20, 1),
('Power Bank 20000mAh', 45.99, 30, 1),

-- Office Supplies
('Printer Paper A4 500pc', 8.99, 200, 2),
('Stapler', 5.99, 75, 2),
('Sticky Notes Pack', 3.99, 150, 2),
('Ballpoint Pens 12pk', 6.99, 100, 2),
('File Folders 25pk', 12.99, 80, 2),
('Desk Organizer', 19.99, 40, 2),
('Paper Clips 100pc', 2.99, 200, 2),
('Whiteboard Markers 4pk', 8.99, 60, 2),

-- Furniture
('Office Chair', 149.99, 10, 3),
('Standing Desk', 299.99, 5, 3),
('Filing Cabinet', 89.99, 15, 3),
('Bookshelf', 79.99, 8, 3),
('Desk Lamp', 34.99, 25, 3),
('Monitor Stand', 29.99, 20, 3),
('Keyboard Tray', 49.99, 15, 3),
('Cable Management Box', 19.99, 30, 3),

-- Books
('Java Programming Guide', 49.99, 20, 4),
('SQL Database Design', 39.99, 15, 4),
('Project Management Basics', 29.99, 25, 4),
('Business Analytics', 44.99, 12, 4),
('Web Development Guide', 54.99, 18, 4),
('Python for Beginners', 34.99, 22, 4),
('Agile Methodology', 45.99, 15, 4),
('Cybersecurity Basics', 49.99, 18, 4),

-- Computer Parts
('8GB RAM DDR4', 49.99, 30, 5),
('1TB SSD Drive', 89.99, 20, 5),
('Gaming Mouse', 59.99, 25, 5),
('Mechanical Keyboard', 79.99, 15, 5),
('CPU Cooling Fan', 29.99, 35, 5),
('Graphics Card RTX 3060', 399.99, 8, 5),
('Power Supply 750W', 89.99, 12, 5),
('Motherboard ATX', 159.99, 10, 5),

-- Networking
('WiFi Router', 79.99, 15, 6),
('Network Switch 8-Port', 29.99, 20, 6),
('Cat6 Cable 5m', 9.99, 50, 6),
('Network Tool Kit', 39.99, 10, 6),
('WiFi Range Extender', 34.99, 25, 6),
('Network Card', 24.99, 30, 6),

-- Software
('Windows 11 Pro License', 199.99, 10, 7),
('Office 365 Business', 149.99, 15, 7),
('Antivirus Premium', 49.99, 20, 7),
('Adobe Creative Cloud', 599.99, 5, 7),
('VPN Service Annual', 89.99, 25, 7),

-- Gaming
('Gaming Headset', 89.99, 20, 8),
('Gaming Chair', 199.99, 8, 8),
('RGB Mouse Pad', 19.99, 30, 8),
('Game Controller', 59.99, 25, 8),
('Streaming Deck', 149.99, 10, 8),

-- Audio Equipment
('USB Microphone', 89.99, 15, 9),
('Desktop Speakers', 69.99, 20, 9),
('Audio Interface', 149.99, 8, 9),
('XLR Cable', 19.99, 30, 9),
('Pop Filter', 14.99, 25, 9),

-- Storage Solutions
('External HDD 2TB', 79.99, 20, 10),
('USB Flash Drive 64GB', 19.99, 50, 10),
('NAS Storage 4-Bay', 399.99, 5, 10),
('Memory Card 128GB', 29.99, 35, 10),
('Cable Organizer Kit', 24.99, 40, 10);

-- Clients
INSERT INTO clients (name, email, phone, address) VALUES 
('John Smith', 'john.smith@email.com', '555-0101', '123 Main St, City'),
('Sarah Johnson', 'sarah.j@email.com', '555-0102', '456 Oak Ave, Town'),
('Tech Solutions Inc', 'contact@techsolutions.com', '555-0103', '789 Business Park'),
('Office Supplies Co', 'orders@officesupplies.com', '555-0104', '321 Commerce St'),
('Local Library', 'library@city.gov', '555-0105', '444 Reading Lane'),
('Gaming Center', 'info@gamingcenter.com', '555-0106', '555 Play Street'),
('School District', 'procurement@school.edu', '555-0107', '777 Education Rd'),
('Medical Center', 'supplies@medical.org', '555-0108', '888 Health Ave'),
('Startup Hub', 'office@startup.com', '555-0109', '999 Innovation Way'),
('Restaurant Chain', 'inventory@restaurant.com', '555-0110', '111 Food Court'),
('Design Studio', 'hello@designstudio.com', '555-0111', '222 Creative Ave'),
('IT Services Ltd', 'support@itservices.com', '555-0112', '333 Tech Boulevard'),
('Local Cafe Chain', 'orders@localcafe.com', '555-0113', '444 Coffee Street'),
('University Bookstore', 'books@university.edu', '555-0114', '555 Campus Drive'),
('Software Solutions', 'info@softsolutions.com', '555-0115', '666 Code Lane');

-- Sample Orders
INSERT INTO orders (client_id, order_date, total_amount) VALUES 
(1, NOW() - INTERVAL 10 DAY, 299.97),
(2, NOW() - INTERVAL 9 DAY, 449.95),
(3, NOW() - INTERVAL 8 DAY, 1299.93),
(4, NOW() - INTERVAL 7 DAY, 89.95),
(5, NOW() - INTERVAL 6 DAY, 159.96),
(6, NOW() - INTERVAL 5 DAY, 899.97),
(7, NOW() - INTERVAL 4 DAY, 249.95),
(8, NOW() - INTERVAL 3 DAY, 599.98),
(9, NOW() - INTERVAL 2 DAY, 149.97),
(10, NOW() - INTERVAL 1 DAY, 799.96);

-- Sample Order Items
INSERT INTO order_items (order_id, product_id, quantity, unit_price, total_price) VALUES 
(1, 1, 1, 199.99, 199.99),
(1, 2, 2, 29.99, 59.98),
(1, 4, 3, 12.99, 38.97),
(2, 11, 3, 149.99, 449.97),
(3, 12, 3, 299.99, 899.97),
(3, 15, 2, 34.99, 69.98),
(4, 7, 15, 5.99, 89.85),
(5, 16, 2, 49.99, 99.98),
(6, 25, 1, 399.99, 399.99),
(6, 31, 2, 79.99, 159.98),
(7, 41, 5, 49.99, 249.95),
(8, 45, 1, 599.99, 599.99),
(9, 51, 2, 74.99, 149.98),
(10, 55, 2, 399.99, 799.98); 