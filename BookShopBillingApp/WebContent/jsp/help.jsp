<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <title>Online Billing System Pahana Edu - Help</title>
    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }
        
        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            background: #f5f7fa;
        }
        
        .navbar {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            padding: 1rem 2rem;
            display: flex;
            justify-content: space-between;
            align-items: center;
            box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
        }
        
        .logo {
            font-size: 1.5rem;
            font-weight: bold;
        }
        
        .nav-links {
            display: flex;
            gap: 2rem;
        }
        
        .nav-links a {
            color: white;
            text-decoration: none;
            padding: 0.5rem 1rem;
            border-radius: 5px;
            transition: background-color 0.3s ease;
        }
        
        .nav-links a:hover {
            background-color: rgba(255, 255, 255, 0.1);
        }
        
        .container {
            max-width: 1000px;
            margin: 2rem auto;
            padding: 0 2rem;
        }
        
        .help-section {
            background: white;
            padding: 2rem;
            border-radius: 10px;
            box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
            margin-bottom: 2rem;
        }
        
        .help-section h2 {
            color: #333;
            margin-bottom: 1rem;
            border-bottom: 2px solid #667eea;
            padding-bottom: 0.5rem;
        }
        
        .help-section h3 {
            color: #667eea;
            margin: 1.5rem 0 0.5rem 0;
        }
        
        .help-section p {
            color: #666;
            line-height: 1.6;
            margin-bottom: 1rem;
        }
        
        .help-section ul {
            color: #666;
            line-height: 1.6;
            margin-left: 2rem;
            margin-bottom: 1rem;
        }
        
        .help-section li {
            margin-bottom: 0.5rem;
        }
        
        .feature-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
            gap: 1.5rem;
            margin-top: 1rem;
        }
        
        .feature-card {
            background: #f8f9fa;
            padding: 1.5rem;
            border-radius: 8px;
            border-left: 4px solid #667eea;
        }
        
        .feature-card h4 {
            color: #333;
            margin-bottom: 0.5rem;
        }
        
        .feature-card p {
            color: #666;
            font-size: 0.9rem;
        }
        
        .login-info {
            background: #e3f2fd;
            padding: 1rem;
            border-radius: 8px;
            border-left: 4px solid #2196f3;
            margin: 1rem 0;
        }
        
        .login-info h4 {
            color: #1976d2;
            margin-bottom: 0.5rem;
        }
        
        .login-info p {
            color: #1565c0;
            margin-bottom: 0.25rem;
        }
    </style>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <nav class="navbar">
        <div class="logo">📚 Online Billing System Pahana Edu</div>
        <div class="nav-links">
            <a href="${pageContext.request.contextPath}/controller/dashboard">Dashboard</a>
            <a href="${pageContext.request.contextPath}/controller/customers">Customers</a>
            <a href="${pageContext.request.contextPath}/controller/books">Books</a>
            <a href="${pageContext.request.contextPath}/controller/billing">Billing</a>
            <a href="${pageContext.request.contextPath}/controller/reports">Reports</a>
            <a href="${pageContext.request.contextPath}/controller/help">Help</a>
        </div>
    </nav>
    
    <div class="container">
        <div class="help-section">
            <h2>📖 System Help & User Guide</h2>
            <p>Welcome to the BookShop Billing System! This guide will help you understand how to use all the features effectively.</p>
            
            <div class="login-info">
                <h4>🔐 Login Information</h4>
                <p><strong>Username:</strong> admin</p>
                <p><strong>Password:</strong> admin123</p>
            </div>
        </div>
        
        <div class="help-section">
            <h2>🎯 Main Features</h2>
            <div class="feature-grid">
                <div class="feature-card">
                    <h4>👥 Customer Management</h4>
                    <p>Add new customers with unique account numbers, manage their information, and track units consumed.</p>
                </div>
                <div class="feature-card">
                    <h4>📚 Book Management</h4>
                    <p>Add, edit, and delete books. Manage inventory, prices, and book details.</p>
                </div>
                <div class="feature-card">
                    <h4>🧾 Billing System</h4>
                    <p>Create bills for customers, calculate totals based on units consumed, and generate invoices.</p>
                </div>
                <div class="feature-card">
                    <h4>📊 Reports & Analytics</h4>
                    <p>View sales reports, customer statistics, and business analytics.</p>
                </div>
            </div>
        </div>
        
        <div class="help-section">
            <h2>📋 Step-by-Step Instructions</h2>
            
            <h3>1. User Authentication (Login)</h3>
            <ul>
                <li>Enter your username and password on the login page</li>
                <li>Click "Login" to access the system</li>
                <li>For security, always log out when finished</li>
            </ul>
            
            <h3>2. Adding New Customer Accounts</h3>
            <ul>
                <li>Go to "Customers" section</li>
                <li>Click "+ Add New Customer" button</li>
                <li>Fill in customer details: name, address, telephone</li>
                <li>Account number is automatically generated</li>
                <li>Click "Save" to create the customer</li>
            </ul>
            
            <h3>3. Editing Customer Information</h3>
            <ul>
                <li>Find the customer in the customer list</li>
                <li>Click "Edit" button next to the customer</li>
                <li>Modify the required information</li>
                <li>Click "Update" to save changes</li>
            </ul>
            
            <h3>4. Managing Book Information</h3>
            <ul>
                <li>Go to "Books" section</li>
                <li>Add new books with title, author, ISBN, price, quantity</li>
                <li>Edit existing books by clicking "Edit"</li>
                <li>Delete books by clicking "Delete" (with confirmation)</li>
                <li>Monitor stock levels and update quantities</li>
            </ul>
            
            <h3>5. Displaying Account Details</h3>
            <ul>
                <li>Go to "Customers" section</li>
                <li>Click "View" to see detailed customer information</li>
                <li>View account number, contact details, and units consumed</li>
                <li>Check billing history for each customer</li>
            </ul>
            
            <h3>6. Calculating and Printing Bills</h3>
            <ul>
                <li>Go to "Billing" section</li>
                <li>Select a customer from the list</li>
                <li>Add books to the bill with quantities</li>
                <li>System automatically calculates totals</li>
                <li>Choose payment method (Cash, Card, UPI)</li>
                <li>Generate and print the bill</li>
            </ul>
            
            <h3>7. Viewing Reports</h3>
            <ul>
                <li>Go to "Reports" section</li>
                <li>View sales summaries and analytics</li>
                <li>Check top-selling books</li>
                <li>Monitor inventory status</li>
                <li>Export reports if needed</li>
            </ul>
        </div>
        
        <div class="help-section">
            <h2>⚠️ Important Notes</h2>
            <ul>
                <li>Always save your work before navigating away from forms</li>
                <li>Customer account numbers are unique and auto-generated</li>
                <li>Units consumed are tracked automatically when bills are created</li>
                <li>Book quantities are updated automatically when sold</li>
                <li>Use the search function to quickly find customers or books</li>
                <li>Contact system administrator for technical support</li>
            </ul>
        </div>
        
        <div class="help-section">
            <h2>🔧 Troubleshooting</h2>
            <h3>Common Issues:</h3>
            <ul>
                <li><strong>Login fails:</strong> Check username and password, ensure caps lock is off</li>
                <li><strong>Can't add customer:</strong> Ensure all required fields are filled</li>
                <li><strong>Book not found:</strong> Check if book exists in inventory</li>
                <li><strong>Bill calculation error:</strong> Verify quantities and prices are correct</li>
                <li><strong>System slow:</strong> Try refreshing the page or logging out and back in</li>
            </ul>
        </div>
    </div>
</body>
</html> 