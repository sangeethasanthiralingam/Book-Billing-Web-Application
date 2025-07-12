<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ include file="header.jspf" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${systemName != null ? systemName : 'Set System Name in Settings'} - Cashier Leaderboard</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <div class="container">
        <div class="header">
            <h1>🏆 Cashier Leaderboard</h1>
            <p>Top performing cashiers based on sales and performance metrics</p>
        </div>
        
        <% if (request.getAttribute("error") != null) { %>
            <div class="error-message">
                <%= request.getAttribute("error") %>
            </div>
        <% } %>
        
        <!-- Optional: Add filter section for future functionality -->
        <div class="filter-section">
            <div class="filter-row">
                <div class="form-group">
                    <label for="period">Performance Period</label>
                    <select id="period" name="period">
                        <option value="current">Current Month</option>
                        <option value="last">Last Month</option>
                        <option value="quarter">This Quarter</option>
                        <option value="year">This Year</option>
                    </select>
                    <div class="form-text">Select the time period for performance metrics</div>
                </div>
                <div class="form-actions">
                    <button type="button" class="btn btn-primary" onclick="refreshLeaderboard()">Refresh</button>
                    <a href="${pageContext.request.contextPath}/controller/dashboard" class="btn btn-secondary">← Back to Dashboard</a>
                </div>
            </div>
        </div>
        
        <div class="report-card">
            <div class="report-header">
                <div class="report-title">Top Performing Cashiers</div>
                <div class="report-subtitle">Ranked by total sales and performance</div>
            </div>
            <div class="report-body">
                <table class="transaction-table">
                    <thead>
                        <tr>
                            <th>🏆 Rank</th>
                            <th>👤 Full Name</th>
                            <th>🔑 Username</th>
                            <th>📊 Bill Count</th>
                            <th>💰 Total Sales</th>
                            <th>⭐ Feedback Score</th>
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
                        <tr class="${rank <= 3 ? 'top-performer' : ''}">
                            <td>
                                <% if (rank == 1) { %>🥇<% } else if (rank == 2) { %>🥈<% } else if (rank == 3) { %>🥉<% } else { %>#<%= rank %><% } %>
                            </td>
                            <td><strong><%= cashier.getFullName() %></strong></td>
                            <td><%= cashier.getUsername() %></td>
                            <td><%= stats.get("billCount") %></td>
                            <td><strong>$<%= String.format("%.2f", stats.get("totalSales")) %></strong></td>
                            <td><%= stats.get("feedbackScore") %>/5</td>
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
    
    <script>
        function refreshLeaderboard() {
            // Future functionality: refresh leaderboard data
            location.reload();
        }
    </script>
    
    <style>
        /* Special styling for top performers */
        .top-performer {
            background: linear-gradient(135deg, var(--color-bg-light-gray), var(--color-bg-white));
        }
        
        .top-performer:hover {
            background: linear-gradient(135deg, var(--color-bg-info), var(--color-bg-light-gray));
        }
        
        .transaction-table td:first-child {
            text-align: center;
            font-weight: bold;
        }
        
        .transaction-table td:nth-child(4),
        .transaction-table td:nth-child(5) {
            text-align: center;
        }
        
        .transaction-table td:last-child {
            text-align: center;
        }
    </style>
</body>
</html> 