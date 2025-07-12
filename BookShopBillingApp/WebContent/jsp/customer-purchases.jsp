<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ include file="header.jspf" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${systemName != null ? systemName : 'Set System Name in Settings'} - Purchase History</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <div class="container">
        <div class="header">
            <h1>📚 Purchase History</h1>
            <p>Your complete book purchase history and details</p>
        </div>
        
        <% if (request.getAttribute("error") != null) { %>
            <div class="error-message">
                <%= request.getAttribute("error") %>
            </div>
        <% } %>
        
        <% model.User customer = (model.User)request.getAttribute("customer");
           java.util.List bills = (java.util.List)request.getAttribute("bills");
           Double totalSpent = (Double)request.getAttribute("totalSpent");
           Integer billCount = (Integer)request.getAttribute("billCount");
           
           if (customer != null) { %>
        
        <div class="stats-grid" style="margin-bottom: 2rem;">
            <div class="stat-card">
                <h3>Total Purchases</h3>
                <div class="stat-number">${billCount != null ? billCount : 0}</div>
            </div>
            <div class="stat-card">
                <h3>Total Spent</h3>
                <div class="stat-number">$${totalSpent != null ? String.format("%.2f", totalSpent) : "0.00"}</div>
            </div>
        </div>
        
        <% if (bills != null && !bills.isEmpty()) { %>
        <div class="report-card">
            <div class="report-header">
                <div class="report-title">📖 Purchase Details</div>
                <div class="report-actions">
                    <a href="${pageContext.request.contextPath}/controller/customer-dashboard" class="btn btn-secondary">← Back to Dashboard</a>
                </div>
            </div>
            <div class="report-body">
                <table class="transaction-table">
                    <thead>
                        <tr>
                            <th>📖 Book Name</th>
                            <th>📅 Purchase Date</th>
                            <th>💰 Price</th>
                            <th>📊 Quantity</th>
                            <th>💵 Total</th>
                        </tr>
                    </thead>
                    <tbody>
                        <% for (Object obj : bills) {
                            model.Bill bill = (model.Bill)obj;
                            dao.BillDAO billDAO = new dao.BillDAO();
                            java.util.List<model.BillItem> items = billDAO.getBillItems(bill.getId());
                            if (items != null && !items.isEmpty()) {
                                for (model.BillItem item : items) { %>
                        <tr>
                            <td><%= item.getBook().getTitle() %></td>
                            <td><%= bill.getBillDate().toString().substring(0, 10) %></td>
                            <td>$<%= String.format("%.2f", item.getUnitPrice()) %></td>
                            <td><%= item.getQuantity() %></td>
                            <td>$<%= String.format("%.2f", item.getTotal()) %></td>
                        </tr>
                        <%      }
                            }
                        } %>
                    </tbody>
                </table>
            </div>
        </div>
        <% } else { %>
        <div class="no-data">
            <h3>📚 No Purchases Yet</h3>
            <p>You haven't made any purchases yet. Start shopping to see your book history here!</p>
            <a href="${pageContext.request.contextPath}/controller/customer-dashboard" class="btn btn-primary">← Back to Dashboard</a>
        </div>
        <% } %>
        
        <% } else { %>
        <div class="error-message">
            <h3>❌ Error</h3>
            <p>Unable to load customer information.</p>
            <a href="${pageContext.request.contextPath}/controller/customer-dashboard" class="btn btn-primary">← Back to Dashboard</a>
        </div>
        <% } %>
    </div>
</body>
</html> 