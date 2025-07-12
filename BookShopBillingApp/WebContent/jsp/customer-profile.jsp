<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ include file="header.jspf" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${systemName != null ? systemName : 'Set System Name in Settings'} - My Profile</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <div class="container">
        <div class="header">
            <h1>👤 My Profile Information</h1>
            <p>View your complete account details and personal information.</p>
        </div>
        <% if (request.getAttribute("success") != null) { %>
            <div class="success-message">
                <%= request.getAttribute("success") %>
            </div>
        <% } %>
        <% if (request.getAttribute("error") != null) { %>
            <div class="error-message">
                <%= request.getAttribute("error") %>
            </div>
        <% } %>
        <% model.User customer = (model.User)request.getAttribute("customer"); %>
        <div class="form-card">
            <div class="form-header">
                <h2>📋 Personal Information</h2>
            </div>
            <div class="profile-grid">
                <div class="profile-item">
                    <label><strong>👤 Full Name:</strong></label>
                    <span><%= customer.getFullName() %></span>
                </div>
                <div class="profile-item">
                    <label><strong>📧 Email Address:</strong></label>
                    <span><%= customer.getEmail() %></span>
                </div>
                <div class="profile-item">
                    <label><strong>📞 Phone Number:</strong></label>
                    <span><%= customer.getPhone() != null ? customer.getPhone() : "Not provided" %></span>
                </div>
                <div class="profile-item">
                    <label><strong>🏠 Address:</strong></label>
                    <span><%= customer.getAddress() != null ? customer.getAddress() : "Not provided" %></span>
                </div>
                <div class="profile-item">
                    <label><strong>🆔 Account Number:</strong></label>
                    <span><%= customer.getAccountNumber() != null ? customer.getAccountNumber() : "Not assigned" %></span>
                </div>
                <div class="profile-item">
                    <label><strong>📅 Member Since:</strong></label>
                    <span><%= customer.getCreatedAt() != null ? customer.getCreatedAt().toString().substring(0, 10) : "Unknown" %></span>
                </div>
                <div class="profile-item">
                    <label><strong>📊 Total Purchases:</strong></label>
                    <span><%= customer.getUnitsConsumed() %> items</span>
                </div>
                <div class="profile-item">
                    <label><strong>✅ Account Status:</strong></label>
                    <span><%= customer.isActive() ? "Active" : "Inactive" %></span>
                </div>
            </div>
        </div>
        
        <div class="form-card" style="margin-top: 2rem;">
            <div class="form-header">
                <h2>🔧 Account Actions</h2>
            </div>
            <div class="action-buttons" style="display: flex; gap: 1rem; flex-wrap: wrap;">
                <a href="${pageContext.request.contextPath}/controller/customer-purchases?id=${sessionScope.userId}" class="btn btn-primary">
                    📚 View Purchase History
                </a>
                <a href="${pageContext.request.contextPath}/controller/customer-reset-password" class="btn btn-secondary">
                    🔐 Change Password
                </a>
                <a href="${pageContext.request.contextPath}/controller/customer-dashboard" class="btn btn-secondary">
                    🏠 Back to Dashboard
                </a>
            </div>
        </div>
    </div>
</body>
</html> 