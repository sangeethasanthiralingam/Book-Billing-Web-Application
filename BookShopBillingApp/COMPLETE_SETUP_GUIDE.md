# 📚 BookShop Billing Application - Complete Setup Guide

## Overview
This guide will help you set up the complete BookShop Billing Application with MySQL database and Apache Tomcat deployment.

## Prerequisites
- Java 17 or higher
- MySQL Server 8.0 or higher
- Apache Tomcat 11.0.9
- PowerShell (Windows)

## Your Configuration
- **Database**: MySQL at `127.0.0.1:3306`
- **Database Name**: `bookshop`
- **Database User**: `root`
- **Database Password**: `root@1234`
- **Tomcat Path**: `D:\BIT\Tomcat\apache-tomcat-11.0.9-windows-x64\apache-tomcat-11.0.9`

## Step-by-Step Setup

### Step 1: Database Setup

#### Option A: Automated Setup (Recommended)
```powershell
# Navigate to your project directory
cd "D:\BIT\java workspace\Book Billing Web Application\bookshop-billing"

# Run the database setup script
.\setup-database.ps1
```

#### Option B: Manual Setup
If you prefer to set up the database manually:

1. **Start MySQL Server**
2. **Connect to MySQL**:
   ```bash
   mysql -h 127.0.0.1 -P 3306 -u root -p
   ```
3. **Execute the SQL script**:
   ```sql
   source database-setup.sql;
   ```

### Step 2: Build and Deploy Application

#### Option A: One-Command Deployment (Recommended)
```powershell
# Build and deploy with Tomcat auto-start
.\quick-deploy.ps1 -StartTomcat
```

#### Option B: Step-by-Step Deployment
```powershell
# Step 1: Build the web application
.\build-web.ps1

# Step 2: Deploy to Tomcat
.\deploy-tomcat.ps1 -StartTomcat
```

### Step 3: Access Your Application

Once deployed and Tomcat is running, access your application at:
- **Main Application**: http://localhost:8080/bookshop-billing
- **Login Page**: http://localhost:8080/bookshop-billing/login.jsp

## Sample Login Credentials

| Role | Username | Password | Access Level |
|------|----------|----------|--------------|
| **Admin** | `admin` | `admin123` | Full access to all features |
| **Cashier** | `cashier1` | `cashier123` | Can process bills and manage inventory |
| **Customer** | `customer1` | `customer123` | Can view bills and reports |

## Application Features

### Available Pages
1. **Login Page** (`/login.jsp`) - User authentication
2. **Dashboard** (`/dashboard.jsp`) - Main overview and navigation
3. **Books Management** (`/books.jsp`) - Add, edit, delete books
4. **Billing** (`/billing.jsp`) - Create and process bills
5. **Invoice** (`/invoice.jsp`) - View and print invoices
6. **Reports** (`/reports.jsp`) - Sales and inventory reports

### Database Tables
- **users** - User accounts and authentication
- **books** - Book inventory and details
- **bills** - Bill headers and metadata
- **bill_items** - Individual items in bills

## Troubleshooting

### Database Issues

#### MySQL Connection Failed
```powershell
# Check if MySQL is running
netstat -an | findstr :3306

# Test connection manually
mysql -h 127.0.0.1 -P 3306 -u root -p
```

#### Database Setup Failed
1. Ensure MySQL server is running
2. Verify user has CREATE, INSERT, UPDATE privileges
3. Check MySQL error logs
4. Try running the setup script with full MySQL path:
   ```powershell
   .\setup-database.ps1 -MySQLPath "C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe"
   ```

### Tomcat Issues

#### Port 8080 Already in Use
```powershell
# Find process using port 8080
netstat -ano | findstr :8080

# Kill the process (replace PID with actual process ID)
taskkill /PID <PID> /F
```

#### Application Not Loading
1. Check Tomcat logs: `D:\BIT\Tomcat\apache-tomcat-11.0.9-windows-x64\apache-tomcat-11.0.9\logs\catalina.out`
2. Verify WAR file was deployed correctly
3. Ensure all dependencies are included

### Build Issues

#### Compilation Errors
1. Ensure Java 17+ is installed and in PATH
2. Check that all source files are in the correct packages
3. Verify classpath includes all required dependencies

#### Missing Dependencies
The application includes MySQL connector in the build process. If you encounter missing dependency errors:
1. Download `mysql-connector-java-8.0.33.jar`
2. Place it in the `lib` directory
3. Rebuild the application

## Development Workflow

### Making Changes
1. **Edit Source Files**: Modify Java classes in `src/` directory
2. **Edit JSP Pages**: Modify JSP files in `WebContent/jsp/` directory
3. **Rebuild**: Run `.\build-web.ps1`
4. **Redeploy**: Run `.\deploy-tomcat.ps1 -Clean -StartTomcat`

### Database Changes
1. **Modify Schema**: Edit `database-setup.sql`
2. **Reapply**: Run `.\setup-database.ps1`
3. **Update DAO Classes**: Modify corresponding DAO classes if needed

## File Structure

```
BookShopBillingApp/
├── src/                          # Java source files
│   ├── model/                    # Data models
│   ├── dao/                      # Data Access Objects
│   ├── service/                  # Business logic
│   ├── controller/               # Servlet controllers
│   ├── util/                     # Utilities (DBConnection, etc.)
│   ├── factory/                  # Factory pattern implementations
│   ├── builder/                  # Builder pattern implementations
│   └── demo/                     # Demo and test classes
├── WebContent/                   # Web application content
│   ├── jsp/                      # JSP pages
│   └── WEB-INF/                  # Web configuration
├── lib/                          # External libraries
├── dist/                         # Build output (WAR files)
├── database-setup.sql            # Database schema and sample data
├── setup-database.ps1            # Database setup script
├── build-web.ps1                 # Web application build script
├── deploy-tomcat.ps1             # Tomcat deployment script
├── quick-deploy.ps1              # One-command deployment script
└── README.md                     # Project documentation
```

## Performance Optimization

### Database Optimization
- Indexes are automatically created on frequently queried columns
- Use connection pooling for better performance
- Consider query optimization for large datasets

### Application Optimization
- Enable JSP compilation caching
- Use appropriate session management
- Implement proper error handling

## Security Considerations

### Database Security
- Change default passwords
- Use dedicated database user with minimal privileges
- Enable SSL for database connections in production

### Application Security
- Implement proper input validation
- Use prepared statements to prevent SQL injection
- Implement proper session management
- Use HTTPS in production

## Additional Configuration: JSTL 3.x and Tomcat 11

### JSTL 3.x Setup (Required for Tomcat 11/Jakarta EE 10)

1. **Download JSTL 3.x JARs:**
   - [`jakarta.servlet.jsp.jstl-3.0.0.jar`](https://repo1.maven.org/maven2/jakarta/servlet/jsp/jstl/jakarta.servlet.jsp.jstl/3.0.0/jakarta.servlet.jsp.jstl-3.0.0.jar)
   - [`jakarta.servlet.jsp.jstl-api-3.0.0.jar`](https://repo1.maven.org/maven2/jakarta/servlet/jsp/jstl/jakarta.servlet.jsp.jstl-api/3.0.0/jakarta.servlet.jsp.jstl-api-3.0.0.jar)
2. **Copy both JARs to:** `WebContent/WEB-INF/lib/`
3. **Remove any old JSTL 2.x JARs** (e.g., `jakarta.servlet.jsp.jstl-2.0.0.jar`).
4. **Use the correct taglib URI in JSPs:**
   ```jsp
   <%@ taglib uri="jakarta.tags.core" prefix="c" %>
   ```
5. **If you update or remove JARs:**
   - Stop Tomcat
   - Delete the exploded app directory and any old WAR from `TOMCAT_HOME/webapps/bookshop-billing`
   - Redeploy the new WAR
   - Start Tomcat

### Troubleshooting JSTL and Deployment

- **500 Internal Server Error:**
  - Check Tomcat logs for stack traces.
  - Ensure JSTL 3.x JARs are present and no old versions remain.
  - Clean Tomcat's `webapps` directory if you change dependencies.
- **JSTL taglib not found:**
  - Use the correct URI: `jakarta.tags.core` for JSTL 3.x.
  - Ensure JARs are in `WEB-INF/lib/`.
- **JDBC driver/thread warnings:**
  - Add a `ServletContextListener` to clean up JDBC drivers and MySQL threads on shutdown.

### Prerequisites (Updated)
- Java 17 or higher
- Apache Tomcat 11.x (Jakarta EE 10)
- MySQL 8.0 or higher
- JSTL 3.x JARs (see above)

## Support

If you encounter issues:
1. Check the troubleshooting section above
2. Review application and Tomcat logs
3. Verify all prerequisites are met
4. Ensure proper file permissions

## Quick Reference Commands

```powershell
# Complete setup (database + deployment)
.\setup-database.ps1
.\quick-deploy.ps1 -StartTomcat

# Rebuild and redeploy
.\build-web.ps1
.\deploy-tomcat.ps1 -Clean -StartTomcat

# Start/Stop Tomcat manually
cd "D:\BIT\Tomcat\apache-tomcat-11.0.9-windows-x64\apache-tomcat-11.0.9\bin"
.\startup.bat
.\shutdown.bat

# Check application status
netstat -an | findstr :8080
```

---

**Note**: This guide is specifically configured for your environment. If you change any paths or configurations, update the corresponding scripts and documentation accordingly. 