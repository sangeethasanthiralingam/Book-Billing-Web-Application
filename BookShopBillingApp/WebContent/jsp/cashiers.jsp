<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="header.jspf" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${systemName != null ? systemName : 'Set System Name in Settings'} - Cashiers</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <div class="container">
        <div class="header">
            <h1>All Cashiers - Sales Overview</h1>
        </div>
        <% if (request.getAttribute("error") != null) { %>
            <div class="error-message">
                <%= request.getAttribute("error") %>
            </div>
        <% } %>
        <div class="report-card">
            <div class="report-header">
                <div class="report-title">Cashier Sales Summary</div>
            </div>
            <div class="report-body">
                <table class="transaction-table">
                    <thead>
                        <tr>
                            <th>Full Name</th>
                            <th>Username</th>
                            <th>Email</th>
                            <th>Bill Count</th>
                            <th>Total Sales</th>
                        </tr>
                    </thead>
                    <tbody>
                        <% java.util.List cashierStats = (java.util.List)request.getAttribute("cashierStats");
                           if (cashierStats != null && !cashierStats.isEmpty()) {
                               for (Object obj : cashierStats) {
                                   java.util.Map stats = (java.util.Map)obj;
                                   model.User cashier = (model.User)stats.get("cashier");
                        %>
                        <tr>
                            <td><%= cashier.getFullName() %></td>
                            <td><%= cashier.getUsername() %></td>
                            <td><%= cashier.getEmail() %></td>
                            <td><%= stats.get("billCount") %></td>
                            <td>$<%= String.format("%.2f", stats.get("totalSales")) %></td>
                        </tr>
                        <%       }
                           } else { %>
                        <tr><td colspan="5" class="no-data">No cashiers found.</td></tr>
                        <% } %>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
</body>
</html> 