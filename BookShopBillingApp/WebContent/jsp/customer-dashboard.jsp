<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ include file="header.jspf" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${systemName != null ? systemName : 'Set System Name in Settings'} - My Dashboard</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <div class="container">
        <div class="header">
            <h1>Welcome, ${sessionScope.user}! 👋</h1>
            <p>This is your personal dashboard where you can view your profile, purchase history, and account information.</p>
        </div>
        <% if (request.getAttribute("error") != null) { %>
            <div class="error-message">
                <%= request.getAttribute("error") %>
            </div>
        <% } %>
        <div class="form-card">
            <div class="form-header">
                <h2>📋 My Profile Information</h2>
            </div>
            <% model.User customer = (model.User)request.getAttribute("customer"); %>
            <div class="profile-grid">
                <div class="profile-item">
                    <label><strong>Full Name:</strong></label>
                    <span><%= customer.getFullName() %></span>
                </div>
                <div class="profile-item">
                    <label><strong>Email:</strong></label>
                    <span><%= customer.getEmail() %></span>
                </div>
                <div class="profile-item">
                    <label><strong>Phone:</strong></label>
                    <span><%= customer.getPhone() %></span>
                </div>
                <div class="profile-item">
                    <label><strong>Account Number:</strong></label>
                    <span><%= customer.getAccountNumber() != null ? customer.getAccountNumber() : "N/A" %></span>
                </div>
                <div class="profile-item">
                    <label><strong>Address:</strong></label>
                    <span><%= customer.getAddress() != null ? customer.getAddress() : "N/A" %></span>
                </div>
                <div class="profile-item">
                    <label><strong>Member Since:</strong></label>
                    <span><%= customer.getCreatedAt() != null ? customer.getCreatedAt().toString().substring(0, 10) : "N/A" %></span>
                </div>
            </div>
        </div>
        <div class="stats-grid" style="margin-top:2rem;">
            <div class="stat-card">
                <h3>Total Spent</h3>
                <div class="stat-number">$${totalSpent != null ? String.format("%.2f", totalSpent) : "0.00"}</div>
            </div>
            <div class="stat-card">
                <h3>Bills</h3>
                <div class="stat-number">${billCount != null ? billCount : 0}</div>
            </div>
        </div>
        <div class="recent-transactions" style="margin-top:2rem;">
            <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px;">
                <h2>📚 My Recent Purchases</h2>
                <a href="${pageContext.request.contextPath}/controller/customer-purchases?id=${sessionScope.userId}" class="btn btn-primary" style="text-decoration: none; padding: 10px 20px; background: #007bff; color: white; border-radius: 5px;">
                    View All Purchases
                </a>
            </div>
            <% java.util.List bills = (java.util.List)request.getAttribute("bills");
               if (bills != null && !bills.isEmpty()) { %>
            <table class="transaction-table">
                <thead>
                    <tr>
                        <th>📖 Book Name</th>
                        <th>📅 Purchase Date</th>
                    </tr>
                </thead>
                <tbody>
                    <% for (Object obj : bills) {
                        model.Bill bill = (model.Bill)obj;
                        // Get bill items to show book names
                        dao.BillDAO billDAO = new dao.BillDAO();
                        java.util.List<model.BillItem> items = billDAO.getBillItems(bill.getId());
                        if (items != null && !items.isEmpty()) {
                            for (model.BillItem item : items) { %>
                    <tr>
                        <td><%= item.getBook().getTitle() %></td>
                        <td><%= bill.getBillDate().toString().substring(0, 10) %></td>
                    </tr>
                    <%      }
                        }
                    } %>
                </tbody>
            </table>
            <% } else { %>
            <div class="no-data">
                <p>📚 You have not made any purchases yet. Start shopping to see your book history here!</p>
            </div>
            <% } %>
        </div>
    </div>
</body>
</html> 