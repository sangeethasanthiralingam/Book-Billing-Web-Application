<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="header.jspf" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${systemName != null ? systemName : 'Set System Name in Settings'} - Cashier Leaderboard</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <div class="container">
        <div class="header">
            <h1>Cashier Leaderboard</h1>
        </div>
        <% if (request.getAttribute("error") != null) { %>
            <div class="error-message">
                <%= request.getAttribute("error") %>
            </div>
        <% } %>
        <div class="report-card">
            <div class="report-header">
                <div class="report-title">Top Performing Cashiers</div>
            </div>
            <div class="report-body">
                <table class="transaction-table">
                    <thead>
                        <tr>
                            <th>Rank</th>
                            <th>Full Name</th>
                            <th>Username</th>
                            <th>Bill Count</th>
                            <th>Total Sales</th>
                            <th>Feedback Score</th>
                        </tr>
                    </thead>
                    <tbody>
                        <% java.util.List leaderboard = (java.util.List)request.getAttribute("leaderboard");
                           if (leaderboard != null && !leaderboard.isEmpty()) {
                               int rank = 1;
                               for (Object obj : leaderboard) {
                                   java.util.Map stats = (java.util.Map)obj;
                                   model.User cashier = (model.User)stats.get("cashier");
                        %>
                        <tr>
                            <td><%= rank++ %></td>
                            <td><%= cashier.getFullName() %></td>
                            <td><%= cashier.getUsername() %></td>
                            <td><%= stats.get("billCount") %></td>
                            <td>$<%= String.format("%.2f", stats.get("totalSales")) %></td>
                            <td><%= stats.get("feedbackScore") %></td>
                        </tr>
                        <%       }
                           } else { %>
                        <tr><td colspan="6" class="no-data">No cashiers found.</td></tr>
                        <% } %>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
</body>
</html> 