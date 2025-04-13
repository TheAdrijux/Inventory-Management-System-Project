# Setup Guide for School Computers

## Prerequisites
1. Java JDK 17
2. MySQL Server
3. Maven

## Database Setup
1. Open MySQL command line or MySQL Workbench
2. Create a new database:
```sql
CREATE DATABASE inventory_management;
```
3. Create a user with necessary permissions:
```sql
CREATE USER 'student'@'localhost' IDENTIFIED BY 'student';
GRANT ALL PRIVILEGES ON inventory_management.* TO 'student'@'localhost';
FLUSH PRIVILEGES;
```
4. Import the database structure and data:
   - Navigate to the `database` folder
   - Run the SQL files in this order:
     1. `schema.sql` (table structure)
     2. `data.sql` (your exported data)

## Running the Application
1. Clone the repository:
```bash
git clone https://github.com/TheAdrijux/Inventory-Management-System-Project.git
cd Inventory-Management-System-Project
```

2. Build the project:
```bash
mvn clean install
```

3. Run the application:
```bash
mvn javafx:run
```

## Troubleshooting
- If you have different MySQL credentials, modify them in:
  `src/main/resources/META-INF/persistence.xml`
- If port 3306 is already in use, modify the port in the same file
- If you see any Java version errors, make sure you're using JDK 17 