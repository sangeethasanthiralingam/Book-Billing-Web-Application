# 📚 BookShop Billing Application - Complete Setup Guide

## 🎯 Overview

This guide provides complete setup instructions for the BookShop Billing Application after the recent controller refactoring. The application now features a modular controller architecture with Jakarta EE 10 compatibility.

## 🏗️ Architecture Overview

### New Controller Structure
```
src/controller/
├── FrontControllerServlet.java    # Main router (293 lines)
├── BaseController.java            # Abstract base class
├── AuthController.java            # Authentication
├── BookController.java            # Book management
├── CustomerController.java        # Customer management
├── UserController.java            # User management
├── BillingController.java         # Billing operations
├── DashboardController.java       # Dashboard display
├── ReportController.java          # Reporting
└── ConfigController.java          # System configuration
```

### Key Improvements
- ✅ **Modular Architecture**: Split monolithic servlet into specialized controllers
- ✅ **Single Responsibility**: Each controller handles specific domain
- ✅ **Better Maintainability**: Easier to locate and modify functionality
- ✅ **Modern Dependencies**: Jakarta EE 10, Java 17, Maven support
- ✅ **Improved Testing**: Individual controllers can be unit tested

## 📋 Prerequisites

### Required Software
- **Java JDK 17** or higher
- **Apache Tomcat 11.x** (Jakarta EE 10 compatible)
- **MySQL 8.0** or higher
- **Maven 3.6+** (recommended) or manual JAR management

### System Requirements
- **RAM**: 4GB minimum, 8GB recommended
- **Disk Space**: 2GB free space
- **OS**: Windows 10+, macOS 10.15+, or Linux

## 🗄️ Database Setup

### 1. Create Database
```sql
CREATE DATABASE bookshop CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE bookshop;
```

### 2. Create Tables
```sql
-- Users table
CREATE TABLE users (
    id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(100),
    role ENUM('ADMIN', 'CASHIER', 'CUSTOMER') DEFAULT 'CUSTOMER',
    full_name VARCHAR(100),
    phone VARCHAR(20),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Books table
CREATE TABLE books (
    id INT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(255) NOT NULL,
    author VARCHAR(255),
    isbn VARCHAR(20) UNIQUE,
    price DECIMAL(10,2),
    quantity INT DEFAULT 0,
    category VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Bills table
CREATE TABLE bills (
    id INT PRIMARY KEY AUTO_INCREMENT,
    bill_number VARCHAR(50) UNIQUE NOT NULL,
    bill_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    customer_id INT,
    cashier_id INT,
    subtotal DECIMAL(10,2),
    discount DECIMAL(10,2) DEFAULT 0,
    tax DECIMAL(10,2) DEFAULT 0,
    total DECIMAL(10,2),
    payment_method VARCHAR(50),
    status ENUM('PENDING', 'PAID', 'CANCELLED') DEFAULT 'PENDING',
    FOREIGN KEY (customer_id) REFERENCES users(id),
    FOREIGN KEY (cashier_id) REFERENCES users(id)
);

-- Bill items table
CREATE TABLE bill_items (
    id INT PRIMARY KEY AUTO_INCREMENT,
    bill_id INT,
    book_id INT,
    quantity INT,
    unit_price DECIMAL(10,2),
    total DECIMAL(10,2),
    FOREIGN KEY (bill_id) REFERENCES bills(id),
    FOREIGN KEY (book_id) REFERENCES books(id)
);

-- System configuration table
CREATE TABLE system_config (
    id INT PRIMARY KEY AUTO_INCREMENT,
    config_key VARCHAR(100) UNIQUE NOT NULL,
    config_value TEXT,
    description VARCHAR(255),
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
```

### 3. Insert Sample Data
```sql
-- Insert sample users
INSERT INTO users (username, password, email, role, full_name, phone) VALUES
('admin', 'admin123', 'admin@bookshop.com', 'ADMIN', 'System Administrator', '555-0001'),
('cashier1', 'cashier123', 'cashier1@bookshop.com', 'CASHIER', 'Sarah Johnson', '555-0002'),
('customer1', 'customer123', 'john.doe@email.com', 'CUSTOMER', 'John Doe', '555-0003');

-- Insert sample books
INSERT INTO books (title, author, isbn, price, quantity, category) VALUES
('The Great Gatsby', 'F. Scott Fitzgerald', '978-0743273565', 12.99, 15, 'Fiction'),
('To Kill a Mockingbird', 'Harper Lee', '978-0446310789', 14.99, 8, 'Fiction'),
('1984', 'George Orwell', '978-0451524935', 11.99, 22, 'Fiction'),
('The Hobbit', 'J.R.R. Tolkien', '978-0547928241', 16.99, 8, 'Fantasy'),
('Pride and Prejudice', 'Jane Austen', '978-0141439518', 9.99, 12, 'Classic');

-- Insert system configuration
INSERT INTO system_config (config_key, config_value, description) VALUES
('tax_rate', '8.5', 'Default tax rate percentage'),
('default_discount', '10', 'Default discount percentage'),
('admin_email', 'admin@bookshop.com', 'Admin email for notifications'),
('smtp_host', 'smtp.gmail.com', 'SMTP server host'),
('smtp_port', '587', 'SMTP server port'),
('smtp_username', 'your-email@gmail.com', 'SMTP username'),
('smtp_password', 'your-app-password', 'SMTP password'),
('smtp_from', 'noreply@bookshop.com', 'From email address');

-- Sample collection request (for testing)
INSERT INTO bills (bill_number, bill_date, customer_id, cashier_id, subtotal, discount, tax, total, payment_method, status) 
VALUES ('COLL-001', NOW(), 3, 1, 27.98, 0, 0, 27.98, 'COLLECTION', 'PENDING');

-- Sample collection items
SET @bill_id = LAST_INSERT_ID();
INSERT INTO bill_items (bill_id, book_id, quantity, unit_price, total) VALUES
(@bill_id, 1, 1, 12.99, 12.99),
(@bill_id, 2, 1, 14.99, 14.99);
```

## ⚙️ Application Configuration

### 1. Database Connection
Edit `src/util/DBConnection.java`:
```java
private static final String URL = "jdbc:mysql://localhost:3306/bookshop?useSSL=false&serverTimezone=UTC";
private static final String USERNAME = "your_username";
private static final String PASSWORD = "your_password";
```

### 2. Maven Configuration (Recommended)
The project includes a `pom.xml` with all necessary dependencies:
- Jakarta Servlet API 6.0.0
- Jakarta JSTL 3.0.1
- org.json 20231013
- MySQL Connector 8.0.33

### 3. Manual JAR Setup (Alternative)
If not using Maven, download these JARs to `lib/` directory:
- `jakarta.servlet-api-6.0.0.jar`
- `jakarta.servlet.jsp.jstl-api-3.0.0.jar`
- `jakarta.servlet.jsp.jstl-3.0.1.jar`
- `json-20231013.jar`
- `mysql-connector-j-8.0.33.jar`

## 🚀 Build and Deployment

### Option 1: Using Maven (Recommended)
```bash
# Clean and compile
mvn clean compile

# Package for deployment
mvn package

# The WAR file will be created in target/bookshop-billing-1.0.0.war
```

### Option 2: Using PowerShell Scripts
```powershell
# Build the application
.\build-web.ps1

# Deploy to Tomcat (with cleanup and restart)
.\deploy-tomcat.ps1 -Clean -StartTomcat
```

### Option 3: Manual Compilation
```bash
# Create directories
mkdir -p bin WebContent/WEB-INF/classes WebContent/WEB-INF/lib

# Compile all classes
javac -d bin -cp "lib/*" src/controller/*.java
javac -d bin -cp "lib/*;bin" src/dao/*.java
javac -d bin -cp "lib/*;bin" src/service/*.java
javac -d bin -cp "lib/*;bin" src/factory/*.java
javac -d bin -cp "lib/*;bin" src/builder/*.java
javac -d bin -cp "lib/*;bin" src/util/*.java

# Copy classes to web deployment
cp -r bin/* WebContent/WEB-INF/classes/

# Copy JARs to web deployment
cp lib/*.jar WebContent/WEB-INF/lib/
```

## 🐱 Tomcat Configuration

### 1. Tomcat Installation
Download and install Apache Tomcat 11.x from [Apache Tomcat](https://tomcat.apache.org/)

### 2. Deploy Application
```bash
# Copy the built application to Tomcat webapps
cp -r WebContent/ $TOMCAT_HOME/webapps/bookshop-billing/

# Or copy the WAR file
cp target/bookshop-billing-1.0.0.war $TOMCAT_HOME/webapps/
```

### 3. Start Tomcat
```bash
cd $TOMCAT_HOME/bin
./startup.sh  # Linux/Mac
startup.bat   # Windows
```

## 🌐 Access Application

### URLs
- **Main Application**: http://localhost:8080/bookshop-billing/
- **Login Page**: http://localhost:8080/bookshop-billing/login.jsp
- **Admin Dashboard**: http://localhost:8080/bookshop-billing/controller/dashboard
- **Customer Store**: http://localhost:8080/bookshop-billing/controller/store
- **Billing System**: http://localhost:8080/bookshop-billing/controller/billing

### Default Credentials
- **Admin**: username=`admin`, password=`admin123`
- **Cashier**: username=`cashier1`, password=`cashier123`
- **Customer**: username=`customer1`, password=`customer123`

## 🧪 Testing

### Web Application Testing
1. Start Tomcat
2. Access http://localhost:8080/bookshop-billing/
3. Login with test credentials
4. Test all major functionalities:
   - Book management
   - Customer management
   - Customer store and collection requests
   - Auto-fill billing from collection requests
   - Billing operations
   - Reports generation

### Collection Requests Workflow Testing
1. **Customer Side**:
   - Login as customer or access `/controller/store`
   - Browse books and add to collection
   - Send collection request to admin

2. **Admin Side**:
   - Login as admin
   - View collection requests on dashboard
   - Click "💳 Pay" to process payment
   - Billing page auto-fills customer and books
   - Complete payment processing

## 🔧 Troubleshooting

### Common Issues

#### 1. Compilation Errors
**Problem**: Java compilation fails
**Solution**: 
- Ensure Java 17 is installed and in PATH
- Check all dependencies are in `lib/` directory
- Verify `sources.txt` includes all controller files

#### 2. Tomcat Startup Issues
**Problem**: Tomcat fails to start
**Solution**:
- Check Tomcat logs at `$TOMCAT_HOME/logs/catalina.out`
- Ensure port 8080 is not in use
- Verify JAR files are in `WEB-INF/lib/`

#### 3. Database Connection Issues
**Problem**: Cannot connect to database
**Solution**:
- Verify MySQL is running
- Check database credentials in `DBConnection.java`
- Ensure database and tables exist

#### 4. JSTL Errors
**Problem**: JSTL tags not working
**Solution**:
- Use correct taglib URI: `jakarta.tags.core`
- Ensure JSTL 3.x JARs are in classpath
- Check JSP files use correct imports

#### 5. Controller Routing Issues
**Problem**: 404 errors for controller routes
**Solution**:
- Verify all controller classes are compiled
- Check `web.xml` servlet mapping
- Ensure URL patterns match controller routes

### Debug Mode
Enable debug logging by adding to `web.xml`:
```xml
<context-param>
    <param-name>debug</param-name>
    <param-value>true</param-value>
</context-param>
```

## 📊 Application Features

### User Roles and Permissions
- **ADMIN**: Full system access, user management, system configuration, collection request management
- **CASHIER**: Billing operations, inventory management, customer management
- **CUSTOMER**: View books, make purchases, view purchase history, create collection requests

### 🆕 New Features: Collection Requests System

#### Customer Store (`/controller/store`)
- **Book Browsing**: Grid layout with book covers, search, and category filtering
- **My Collection**: Personal book collection with localStorage persistence
- **Collection Management**: Add/remove books, view collection sidebar with totals
- **Send to Admin**: Email collection requests with optional notes
- **Real-time Search**: Auto-search functionality with visual feedback

#### Admin Dashboard Enhancement
- **Collection Requests Display**: Compact cards showing customer requests
- **Customer Information**: Name, email, phone, request date
- **Book Details**: Requested books with prices and quantities
- **Total Calculation**: Automatic total value calculation
- **Quick Actions**: Direct "💳 Pay" button for processing
- **Expandable Details**: Click to view full request information

#### Auto-Fill Billing System
- **Seamless Integration**: Collection requests → Billing page workflow
- **Auto-Customer Selection**: Customer automatically selected from request
- **Auto-Book Addition**: All requested books added to cart with quantities
- **Auto-Amount Calculation**: Subtotal, tax, discount, and total pre-calculated
- **Ready for Payment**: Just select payment method and process

#### Collection Request Workflow
1. **Customer**: Browse store → Add books to collection → Send request
2. **System**: Store request with PENDING status and COLLECTION payment method
3. **Admin**: View requests on dashboard → Click "💳 Pay"
4. **Billing**: Auto-filled form → Select payment method → Generate bill
5. **Completion**: Status changes to PAID, request removed from pending list

### Core Functionality
- ✅ User authentication and authorization
- ✅ Book management (CRUD operations)
- ✅ Customer management
- ✅ Customer book store with collection management
- ✅ Collection requests system (Customer → Admin workflow)
- ✅ Auto-fill billing from collection requests
- ✅ Billing and invoice generation
- ✅ Payment processing (Cash, Card, UPI)
- ✅ Discount application (Percentage, Fixed)
- ✅ Reports and analytics
- ✅ System configuration

### Design Patterns Implemented
- **Singleton**: Database connection
- **Strategy**: Payment methods
- **Factory**: Discount types
- **Builder**: Bill construction
- **MVC**: Application architecture
- **DAO**: Data access layer

## 🔒 Security Considerations

### Production Deployment
1. **Change default passwords** immediately
2. **Use HTTPS** in production
3. **Configure proper session timeout**
4. **Implement input validation**
5. **Use connection pooling**
6. **Enable database SSL**
7. **Regular security updates**

### Security Features
- Session-based authentication
- Role-based access control
- SQL injection prevention
- Input validation and sanitization
- CSRF protection (recommended for production)

## 📈 Performance Optimization

### Database Optimization
- Add indexes on frequently queried columns
- Use connection pooling
- Optimize SQL queries
- Regular database maintenance

### Application Optimization
- Enable JSP compilation caching
- Use CDN for static resources
- Implement response caching
- Monitor memory usage

## 🔄 Maintenance

### Regular Tasks
1. **Database backups** (daily)
2. **Log rotation** (weekly)
3. **Security updates** (monthly)
4. **Performance monitoring** (ongoing)
5. **User management** (as needed)

### Monitoring
- Check Tomcat logs regularly
- Monitor database performance
- Track application usage
- Review error logs

## 📞 Support

### Getting Help
1. Check this setup guide
2. Review troubleshooting section
3. Check application logs
4. Verify configuration files
5. Test with sample data

### Useful Commands
```bash
# Check Java version
java -version

# Check Tomcat status
curl http://localhost:8080/

# Check database connection
mysql -u username -p bookshop

# View Tomcat logs
tail -f $TOMCAT_HOME/logs/catalina.out
```

---

## ✅ Setup Complete!

Your BookShop Billing Application is now ready to use with:

### ✨ **Latest Features (Updated)**
- ✅ **Modular Controller Architecture**: Clean, maintainable MVC structure
- ✅ **Customer Store System**: Interactive book browsing with collection management
- ✅ **Collection Requests**: Customer-to-Admin workflow for book requests
- ✅ **Auto-Fill Billing**: Seamless integration from requests to payment processing
- ✅ **Enhanced UI/UX**: Modern, responsive design with animations and notifications
- ✅ **Compact Dashboard**: Space-efficient collection request management
- ✅ **Real-time Features**: Auto-search, live updates, and instant feedback

### 🚀 **Ready for Production**
The application now provides a complete end-to-end solution for:
- Customer book discovery and collection building
- Admin request management and processing
- Streamlined billing and payment workflows
- Professional invoice generation and reporting

### 📞 **Quick Start**
1. Access: http://localhost:8080/bookshop-billing
2. Login as admin (admin/admin123) or customer (customer1/customer123)
3. Test collection requests: `/controller/store` → Add books → Send to admin
4. Process payments: Admin dashboard → Collection requests → 💳 Pay

Your modern BookShop Billing System is now fully operational! 🎉he application provides a solid foundation for a production billing system with modern Java technologies and best practices.

For additional features and customizations, refer to the main README.md file and the controller refactoring summary. 