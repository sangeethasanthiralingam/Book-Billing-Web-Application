# 📚 Online Billing System Pahana Edu

A comprehensive Java web application for managing billing operations for Pahana Edu, built using JSP, Servlets, and various design patterns.

## 🏗️ Architecture Overview

This application follows the **Model-View-Controller (MVC)** architecture pattern and implements several design patterns for maintainable and scalable code.

### Project Structure

```
BookShopBillingApp/
├── src/
│   ├── controller/             # Servlets (Controllers)
│   │   └── FrontControllerServlet.java
│   ├── dao/                   # Data Access Objects
│   │   ├── BookDAO.java
│   │   ├── UserDAO.java
│   │   └── BillDAO.java
│   ├── model/                 # Java Beans / Models
│   │   ├── Book.java
│   │   ├── User.java
│   │   ├── Bill.java
│   │   └── BillItem.java
│   ├── service/              # Business Logic & Strategy Pattern
│   │   ├── PaymentService.java
│   │   ├── PaymentStrategy.java
│   │   ├── CashPayment.java
│   │   ├── CardPayment.java
│   │   └── UpiPayment.java
│   ├── util/                   # Utility Classes
│   │   └── DBConnection.java   # Singleton Pattern
│   ├── factory/              # Factory Pattern
│   │   ├── DiscountFactory.java
│   │   ├── Discount.java
│   │   ├── PercentageDiscount.java
│   │   └── FixedDiscount.java
│   ├── builder/             # Builder Pattern
│   │   └── BillBuilder.java
│   └── demo/                # Demo and Testing
│       ├── Main.java
│       └── StandaloneDemo.java
├── WebContent/
│   ├── jsp/
│   │   ├── login.jsp
│   │   ├── dashboard.jsp
│   │   ├── books.jsp
│   │   ├── billing.jsp
│   │   ├── invoice.jsp
│   │   └── reports.jsp
│   └── WEB-INF/
│       └── web.xml
└── README.md
```

## 🎯 Design Patterns Implemented

| Pattern | Purpose | Implementation |
|---------|---------|----------------|
| **Singleton** | Database Connection | `util/DBConnection.java` |
| **Strategy** | Payment Methods | `service/PaymentStrategy.java` + implementations |
| **Factory** | Discount Types | `factory/DiscountFactory.java` + implementations |
| **Builder** | Bill Construction | `builder/BillBuilder.java` |
| **MVC** | Application Architecture | Model: `model/`, View: `jsp/`, Controller: `controller/` |
| **DAO** | Data Access | `dao/BookDAO.java`, `dao/UserDAO.java`, `dao/BillDAO.java` |

### 1. **Singleton Pattern** - Database Connection
- **File**: `util/DBConnection.java`
- **Purpose**: Ensures only one database connection instance exists
- **Usage**: `DBConnection.getInstance()`

### 2. **Strategy Pattern** - Payment Methods
- **Files**: `service/PaymentStrategy.java`, `service/PaymentService.java`
- **Implementations**: `CashPayment.java`, `CardPayment.java`, `UpiPayment.java`
- **Purpose**: Allows switching between different payment methods at runtime

### 3. **Factory Pattern** - Discount Types
- **Files**: `factory/DiscountFactory.java`, `factory/Discount.java`
- **Implementations**: `PercentageDiscount.java`, `FixedDiscount.java`
- **Purpose**: Creates discount objects based on type and parameters

### 4. **Builder Pattern** - Bill Construction
- **File**: `builder/BillBuilder.java`
- **Purpose**: Constructs complex Bill objects step by step
- **Usage**: `BillBuilder.createNewBill().withCustomer(user).addItem(item).build()`

### 5. **MVC Pattern** - Application Architecture
- **Model**: `model/` package (Book, User, Bill, BillItem)
- **View**: `WebContent/jsp/` package (JSP pages)
- **Controller**: `controller/` package (Servlets)

### 6. **DAO Pattern** - Data Access
- **Files**: `dao/BookDAO.java`, `dao/UserDAO.java`, `dao/BillDAO.java`
- **Purpose**: Separates data access logic from business logic

## 🚀 Features

### Core Functionality
- **User Authentication**: Login/logout with role-based access
- **Book Management**: Add, edit, delete, and search books
- **Billing System**: Create bills with multiple items and payment methods
- **Invoice Generation**: Generate and print invoices
- **Reports**: Sales, inventory, and revenue reports

### Payment Methods
- 💵 **Cash Payment**
- 💳 **Card Payment** (VISA, MasterCard, etc.)
- 📱 **UPI Payment**

### Discount Types
- **Percentage Discount**: Apply percentage-based discounts
- **Fixed Discount**: Apply fixed amount discounts

### User Roles
- **ADMIN**: Full system access
- **CASHIER**: Billing and inventory access
- **CUSTOMER**: View and purchase books

## 🛠️ Technology Stack

- **Backend**: Java, JSP, Servlets
- **Database**: MySQL
- **Frontend**: HTML5, CSS3, JavaScript
- **Server**: Apache Tomcat 11.x (Jakarta EE 10)
- **Build Tool**: Maven (recommended)
- **JSTL**: Jakarta Standard Tag Library 3.x (for Jakarta EE 9+)

## 📋 Prerequisites

- Java JDK 17 or higher
- Apache Tomcat 11.x (Jakarta EE 10)
- MySQL 8.0 or higher
- Maven (optional)
- JSTL 3.x JARs (see below)

## ⚙️ JSTL Configuration (Tomcat 11/Jakarta EE 10)

1. **Download JSTL 3.x JARs:**
   - [`jakarta.servlet.jsp.jstl-3.0.0.jar`](https://repo1.maven.org/maven2/jakarta/servlet/jsp/jstl/jakarta.servlet.jsp.jstl/3.0.0/jakarta.servlet.jsp.jstl-3.0.0.jar)
   - [`jakarta.servlet.jsp.jstl-api-3.0.0.jar`](https://repo1.maven.org/maven2/jakarta/servlet/jsp/jstl/jakarta.servlet.jsp.jstl-api/3.0.0/jakarta.servlet.jsp.jstl-api-3.0.0.jar)
2. **Place both JARs in:** `WebContent/WEB-INF/lib/`
3. **Remove any old JSTL 2.x JARs** (e.g., `jakarta.servlet.jsp.jstl-2.0.0.jar`).
4. **Taglib URI in JSPs:**
   ```jsp
   <%@ taglib uri="jakarta.tags.core" prefix="c" %>
   ```
5. **If you update or remove JARs:**
   - Stop Tomcat
   - Delete the exploded app directory and any old WAR from `TOMCAT_HOME/webapps/bookshop-billing`
   - Redeploy the new WAR
   - Start Tomcat

## 🛠️ Troubleshooting

- **500 Internal Server Error:**
  - Check Tomcat logs for stack traces.
  - Ensure JSTL 3.x JARs are present and no old versions remain.
  - Clean Tomcat's `webapps` directory if you change dependencies.
- **JSTL taglib not found:**
  - Use the correct URI: `jakarta.tags.core` for JSTL 3.x.
  - Ensure JARs are in `WEB-INF/lib/`.
- **JDBC driver/thread warnings:**
  - Add a `ServletContextListener` to clean up JDBC drivers and MySQL threads on shutdown (see below for example).

## 📋 Prerequisites

- Java JDK 17 or higher
- Apache Tomcat 11.x (Jakarta EE 10)
- MySQL 8.0 or higher
- Maven (optional)

## 🗄️ Database Setup

### 1. Create Database
```sql
CREATE DATABASE bookshop;
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
    phone VARCHAR(20)
);

-- Books table
CREATE TABLE books (
    id INT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(255) NOT NULL,
    author VARCHAR(255),
    isbn VARCHAR(20) UNIQUE,
    price DECIMAL(10,2),
    quantity INT DEFAULT 0,
    category VARCHAR(100)
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
('The Hobbit', 'J.R.R. Tolkien', '978-0547928241', 16.99, 8, 'Fantasy');
```

## 🚀 Installation & Setup

### 1. Clone the Repository
```bash
git clone <repository-url>
cd BookShopBillingApp
```

### 2. Configure Database
- Update database credentials in `src/util/DBConnection.java`
- Run the SQL scripts above to create database and tables

### 3. Build and Deploy
```bash
# If using Maven
mvn clean package

# Deploy to Tomcat
# Copy the WAR file to Tomcat's webapps directory
```

### 4. Access Application
- Start Tomcat server
- Navigate to: `http://localhost:8080/bookshop-billing`
- Login with demo credentials:
  - Username: `admin`
  - Password: `admin123`

## 🧪 Testing

### Quick Demo (Core Classes Only)
For a quick demonstration without servlet dependencies:

```bash
# Build core classes
.\build-core.bat

# Run the demo
.\run-demo.bat
```

### Full Web Application
For the complete web application with servlets:

```bash
# Using Maven (recommended)
mvn clean compile
mvn package

# Deploy to Tomcat
# Copy target/bookshop-billing-1.0.0.war to Tomcat webapps directory
```

### Manual Compilation
```bash
# Compile core classes
javac -d bin src\model\*.java
javac -d bin -cp bin src\dao\*.java
javac -d bin -cp bin src\service\*.java
javac -d bin -cp bin src\factory\*.java
javac -d bin -cp bin src\builder\*.java
javac -d bin -cp bin src\util\*.java
javac -d bin -cp bin src\demo\StandaloneDemo.java

# Run demo
java -cp bin demo.StandaloneDemo
```

This will demonstrate:
- Database connection
- Model classes
- Payment strategy pattern
- Factory pattern for discounts
- Builder pattern for bills
- DAO classes

## 🎨 UI Features

### Modern Design
- Responsive layout with CSS Grid and Flexbox
- Beautiful gradient backgrounds
- Smooth animations and transitions
- Mobile-friendly interface

### User Experience
- Intuitive navigation
- Real-time form validation
- Interactive elements with hover effects
- Professional invoice layout

## 🔧 Configuration

### Database Configuration
Edit `src/util/DBConnection.java`:
```java
private static final String URL = "jdbc:mysql://localhost:3306/bookshop";
private static final String USERNAME = "your_username";
private static final String PASSWORD = "your_password";
```

### Application Settings
- Session timeout: 30 minutes (configurable in `web.xml`)
- Default tax rate: 8.5%
- Default discount: 10%

## 📊 Reports Available

1. **Sales Summary**: Total sales, bill count, average bill value
2. **Top Selling Books**: Best-selling books with quantities
3. **Revenue Analysis**: Daily/monthly revenue trends
4. **Inventory Status**: Stock levels and low stock alerts

## 🔒 Security Features

- Session-based authentication
- Role-based access control
- SQL injection prevention (PreparedStatements)
- Input validation and sanitization

## 🚀 Future Enhancements

- [ ] Real-time inventory updates
- [ ] Email notifications
- [ ] Barcode scanning integration
- [ ] Advanced analytics dashboard
- [ ] Mobile app development
- [ ] Multi-language support
- [ ] Cloud deployment support

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests if applicable
5. Submit a pull request

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 👥 Authors

- **Sangeetha Santhiralingam** - Initial work

## 🙏 Acknowledgments

- Design patterns inspiration from Gang of Four
- UI design inspiration from modern web applications
- Database design best practices

---

**Note**: This is a demonstration application showcasing various design patterns and best practices in Java web development. For production use, additional security measures, error handling, and testing should be implemented. 