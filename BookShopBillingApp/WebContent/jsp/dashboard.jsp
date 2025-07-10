<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>BookShop Billing - Dashboard</title>
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
        
        .user-info {
            display: flex;
            align-items: center;
            gap: 1rem;
        }
        
        .logout-btn {
            background: rgba(255, 255, 255, 0.2);
            border: none;
            color: white;
            padding: 0.5rem 1rem;
            border-radius: 5px;
            cursor: pointer;
            text-decoration: none;
        }
        
        .container {
            max-width: 1200px;
            margin: 2rem auto;
            padding: 0 2rem;
        }
        
        .welcome-section {
            background: white;
            padding: 2rem;
            border-radius: 10px;
            box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
            margin-bottom: 2rem;
        }
        
        .stats-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
            gap: 1.5rem;
            margin-bottom: 2rem;
        }
        
        .stat-card {
            background: white;
            padding: 1.5rem;
            border-radius: 10px;
            box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
            text-align: center;
        }
        
        .stat-card h3 {
            color: #333;
            margin-bottom: 0.5rem;
        }
        
        .stat-number {
            font-size: 2rem;
            font-weight: bold;
            color: #667eea;
        }
        
        .quick-actions {
            background: white;
            padding: 2rem;
            border-radius: 10px;
            box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
        }
        
        .quick-actions h2 {
            margin-bottom: 1.5rem;
            color: #333;
        }
        
        .action-buttons {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
            gap: 1rem;
        }
        
        .action-btn {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            border: none;
            padding: 1rem;
            border-radius: 8px;
            cursor: pointer;
            text-decoration: none;
            text-align: center;
            font-weight: 500;
            transition: transform 0.2s ease;
        }
        
        .action-btn:hover {
            transform: translateY(-2px);
        }
        
        .recent-transactions {
            background: white;
            padding: 2rem;
            border-radius: 10px;
            box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
            margin-top: 2rem;
        }
        
        .recent-transactions h2 {
            margin-bottom: 1.5rem;
            color: #333;
        }
        
        .transaction-table {
            width: 100%;
            border-collapse: collapse;
            margin-top: 1rem;
        }
        
        .transaction-table th,
        .transaction-table td {
            padding: 0.75rem;
            text-align: left;
            border-bottom: 1px solid #eee;
        }
        
        .transaction-table th {
            background-color: #f8f9fa;
            font-weight: 600;
            color: #333;
        }
        
        .transaction-table tr:hover {
            background-color: #f8f9fa;
        }
        
        .error-message {
            background: #fee;
            color: #c33;
            padding: 1rem;
            border-radius: 5px;
            margin-bottom: 1rem;
            border: 1px solid #fcc;
        }
    </style>
</head>
<body>
    <nav class="navbar">
        <div class="logo">📚 BookShop Billing</div>
        <div class="nav-links">
            <a href="${pageContext.request.contextPath}/controller/dashboard">Dashboard</a>
            <a href="${pageContext.request.contextPath}/controller/books">Books</a>
            <a href="${pageContext.request.contextPath}/controller/billing">Billing</a>
            <a href="${pageContext.request.contextPath}/controller/reports">Reports</a>
        </div>
        <div class="user-info">
            <span>Welcome, <%= session.getAttribute("user") != null ? session.getAttribute("user") : "User" %></span>
            <a href="${pageContext.request.contextPath}/controller/logout" class="logout-btn">Logout</a>
        </div>
    </nav>
    
    <div class="container">
        <% if (request.getAttribute("error") != null) { %>
            <div class="error-message">
                <%= request.getAttribute("error") %>
            </div>
        <% } %>
        
        <div class="welcome-section">
            <h1>Welcome to BookShop Billing System</h1>
            <p>Manage your bookstore operations efficiently with our comprehensive billing solution.</p>
        </div>
        
        <div class="stats-grid">
            <div class="stat-card">
                <h3>Total Books</h3>
                <div class="stat-number">${totalBooks != null ? totalBooks : 0}</div>
                <p>Available in inventory</p>
            </div>
            <div class="stat-card">
                <h3>Today's Sales</h3>
                <div class="stat-number">$${todaySales != null ? String.format("%.2f", todaySales) : "0.00"}</div>
                <p>Revenue generated</p>
            </div>
            <div class="stat-card">
                <h3>Bills Generated</h3>
                <div class="stat-number">${todayBills != null ? todayBills : 0}</div>
                <p>Today's transactions</p>
            </div>
            <div class="stat-card">
                <h3>Customers</h3>
                <div class="stat-number">${todayCustomers != null ? todayCustomers : 0}</div>
                <p>Served today</p>
            </div>
        </div>
        
        <div class="quick-actions">
            <h2>Quick Actions</h2>
            <div class="action-buttons">
                <a href="${pageContext.request.contextPath}/controller/billing" class="action-btn">
                    🛒 Create New Bill
                </a>
                <a href="${pageContext.request.contextPath}/controller/books" class="action-btn">
                    📚 Manage Books
                </a>
                <a href="${pageContext.request.contextPath}/controller/reports" class="action-btn">
                    📊 View Reports
                </a>
                <a href="${pageContext.request.contextPath}/controller/invoice" class="action-btn">
                    🧾 Generate Invoice
                </a>
            </div>
        </div>
        
        <% if (request.getAttribute("recentBills") != null && !((java.util.List)request.getAttribute("recentBills")).isEmpty()) { %>
        <div class="recent-transactions">
            <h2>Recent Transactions</h2>
            <table class="transaction-table">
                <thead>
                    <tr>
                        <th>Bill #</th>
                        <th>Date</th>
                        <th>Total</th>
                        <th>Payment Method</th>
                        <th>Status</th>
                    </tr>
                </thead>
                <tbody>
                    <% for (model.Bill bill : (java.util.List<model.Bill>)request.getAttribute("recentBills")) { %>
                    <tr>
                        <td>${bill.billNumber}</td>
                        <td>${bill.billDate}</td>
                        <td>$${String.format("%.2f", bill.total)}</td>
                        <td>${bill.paymentMethod}</td>
                        <td>${bill.status}</td>
                    </tr>
                    <% } %>
                </tbody>
            </table>
        </div>
        <% } %>
    </div>
</body>
</html> 