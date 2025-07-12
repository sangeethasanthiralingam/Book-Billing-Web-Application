<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ include file="header.jspf" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <title>${systemName != null ? systemName : 'Set System Name in Settings'} - Dashboard</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
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
            <% if (session.getAttribute("userRole") != null && session.getAttribute("userRole").equals("CASHIER")) { %>
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
            <% } else { %>
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
            <% } %>
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