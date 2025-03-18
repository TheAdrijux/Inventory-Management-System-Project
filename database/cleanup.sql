-- Disable foreign key checks
SET FOREIGN_KEY_CHECKS = 0;

-- Drop Lithuanian tables
DROP TABLE IF EXISTS uzsakymai;
DROP TABLE IF EXISTS prekes;
DROP TABLE IF EXISTS kategorijos;
DROP TABLE IF EXISTS klientai;

-- Drop English tables
DROP TABLE IF EXISTS order_items;
DROP TABLE IF EXISTS orders;
DROP TABLE IF EXISTS products;
DROP TABLE IF EXISTS categories;
DROP TABLE IF EXISTS clients;

-- Re-enable foreign key checks
SET FOREIGN_KEY_CHECKS = 1; 