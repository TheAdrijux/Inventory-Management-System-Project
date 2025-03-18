# Inventory Management System

A JavaFX-based desktop application for managing inventory, including products, categories, orders, and clients.

## Prerequisites

1. Java Development Kit (JDK) 17 or later
   - Download from: https://adoptium.net/
   - Add to system PATH

2. Maven
   - Download from: https://maven.apache.org/download.cgi
   - Add to system PATH

3. MySQL Server (via XAMPP)
   - Make sure MySQL service is running
   - Default port: 3306
   - Default credentials: root (no password)

## Database Setup

1. Start XAMPP Control Panel
2. Start MySQL service
3. Click on "Admin" button next to MySQL to open phpMyAdmin
4. Create a new database named `inventory_management`
5. Import the database schema from `database/schema.sql` (if available)

## Running the Application

### Method 1: Using run.bat (Windows)
1. Double-click `run.bat`
2. Wait for the build to complete
3. The application will start automatically

### Method 2: Manual Build and Run
1. Open terminal/command prompt
2. Navigate to project directory
3. Run: `mvn clean package`
4. Run: `java -jar target/inventory-management-system-1.0-SNAPSHOT.jar`

## Features

- Product Management
  - Add/Edit/Delete products
  - Search and filter by category
  - Track inventory levels

- Category Management
  - Organize products by categories
  - View category statistics

- Order Management
  - Create new orders
  - Add products to orders
  - View order history

- Client Management
  - Manage client information
  - View client order history

## Troubleshooting

1. If you get database connection errors:
   - Make sure MySQL is running in XAMPP
   - Check database name and credentials
   - Verify port 3306 is not blocked

2. If the application won't start:
   - Verify Java 17+ is installed: `java -version`
   - Verify Maven is installed: `mvn -version`
   - Check console output for error messages

## Development

This project uses:
- JavaFX for the user interface
- Hibernate/JPA for database operations
- Maven for dependency management
- MySQL for data storage

The application follows:
- MVC architecture pattern
- Material Design principles
- Best practices for Java development 