<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ include file="header.jspf" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>BookShop Billing - Error</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <div class="error-container">
        <div class="error-icon">⚠️</div>
        <h1 class="error-title">Oops! Something went wrong</h1>
        <p class="error-message">
            <% if (request.getAttribute("error") != null) { %>
                <%= request.getAttribute("error") %>
            <% } else { %>
                An unexpected error occurred. Please try again.
            <% } %>
        </p>
        <a href="${pageContext.request.contextPath}/controller/dashboard" class="back-btn">Back to Dashboard</a>
    </div>
    </div>
</body>
</html> 