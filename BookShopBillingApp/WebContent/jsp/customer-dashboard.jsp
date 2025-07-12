<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="header.jspf" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${systemName != null ? systemName : 'Set System Name in Settings'} - My Dashboard</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <div class="container">
        <div class="header">
            <h1>My Dashboard</h1>
        </div>
        <% if (request.getAttribute("error") != null) { %>
            <div class="error-message">
                <%= request.getAttribute("error") %>
            </div>
        <% } %>
        <div class="form-card">
            <div class="form-header">
                <h2>Profile</h2>
            </div>
            <% model.User customer = (model.User)request.getAttribute("customer"); %>
            <div class="form-group">
                <label>Full Name:</label>
                <span><%= customer.getFullName() %></span>
            </div>
            <div class="form-group">
                <label>Email:</label>
                <span><%= customer.getEmail() %></span>
            </div>
            <div class="form-group">
                <label>Phone:</label>
                <span><%= customer.getPhone() %></span>
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
            <h2>My Purchases</h2>
            <% java.util.List bills = (java.util.List)request.getAttribute("bills");
               if (bills != null && !bills.isEmpty()) { %>
            <table class="transaction-table">
                <thead>
                    <tr>
                        <th>Bill #</th>
                        <th>Date</th>
                        <th>Total</th>
                        <th>Status</th>
                    </tr>
                </thead>
                <tbody>
                    <% for (Object obj : bills) {
                        model.Bill bill = (model.Bill)obj; %>
                    <tr>
                        <td><%= bill.getBillNumber() %></td>
                        <td><%= bill.getBillDate() %></td>
                        <td>$<%= String.format("%.2f", bill.getTotal()) %></td>
                        <td><%= bill.getStatus() %></td>
                    </tr>
                    <% } %>
                </tbody>
            </table>
            <% } else { %>
            <div class="no-data">
                <p>You have not made any purchases yet.</p>
            </div>
            <% } %>
        </div>
    </div>
</body>
</html> 