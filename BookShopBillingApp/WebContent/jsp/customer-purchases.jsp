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
        <c:if test="${sessionScope.userRole != 'CUSTOMER'}">
        <!-- Customer Summary Section -->
        <div class="form-card">
            <div class="form-header">
                <h2>👤 Customer Summary</h2>
            </div>
            <div class="profile-grid profile-grid-spaced">
                <div class="profile-item">
                    <label><strong>👤 Customer:</strong></label>
                    <span><%= customer.getFullName() %></span>
                </div>
                <div class="profile-item">
                    <label><strong>🆔 Account:</strong></label>
                    <span><%= customer.getAccountNumber() != null ? customer.getAccountNumber() : "N/A" %></span>
                </div>
                <div class="profile-item">
                    <label><strong>📧 Email:</strong></label>
                    <span><%= customer.getEmail() %></span>
                </div>
                <div class="profile-item">
                    <label><strong>📅 Member Since:</strong></label>
                    <span><%= customer.getCreatedAt() != null ? customer.getCreatedAt().toString().substring(0, 10) : "N/A" %></span>
                </div>
            </div>
        </div>
        </c:if>
        
        <!-- Purchase Statistics -->
        <div class="stats-grid stats-grid-spaced">
            <div class="stat-card">
                <h3>📚 Total Purchases</h3>
                <div class="stat-number">${billCount != null ? billCount : 0}</div>
                <div class="stat-text">Number of transactions</div>
            </div>
            <div class="stat-card">
                <h3>💰 Total Spent</h3>
                <div class="stat-number">$${totalSpent != null ? String.format("%.2f", totalSpent) : "0.00"}</div>
                <div class="stat-text">Total amount spent</div>
            </div>
            <div class="stat-card">
                <h3>📖 Books Bought</h3>
                <div class="stat-number">${customer.getUnitsConsumed() != null ? customer.getUnitsConsumed() : 0}</div>
                <div class="stat-text">Total books purchased</div>
            </div>
        </div>
        
        <% if (bills != null && !bills.isEmpty()) { %>
        <!-- Purchase Details Table -->
        <div class="report-card">
            <div class="report-header">
                <div class="report-title">📖 Purchase Details</div>
                <div class="report-actions">
                    <a href="${pageContext.request.contextPath}/controller/<c:choose><c:when test='${sessionScope.userRole == "CUSTOMER"}'>customer-dashboard</c:when><c:otherwise>dashboard</c:otherwise></c:choose>" class="btn btn-secondary">
                        ← Back to Dashboard
                    </a>
                </div>
            </div>
            <div class="report-body">
                <table class="transaction-table">
                    <thead>
                        <tr>
                            <th>📖 Book Name</th>
                            <th>📅 Purchase Date</th>
                            <th>💰 Unit Price</th>
                            <th>📊 Quantity</th>
                            <th>💵 Total Amount</th>
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
                            <td><strong><%= item.getBook().getTitle() %></strong></td>
                            <td><%= bill.getBillDate().toString().substring(0, 10) %></td>
                            <td>$<%= String.format("%.2f", item.getUnitPrice()) %></td>
                            <td><%= item.getQuantity() %></td>
                            <td><strong>$<%= String.format("%.2f", item.getTotal()) %></strong></td>
                        </tr>
                        <%      }
                            }
                        } %>
                    </tbody>
                </table>
            </div>
        </div>
        <% } else { %>
        <!-- No Purchases Message -->
        <div class="form-card">
            <div class="no-data">
                <h3>📚 No Purchases Yet</h3>
                <p>You haven't made any purchases yet. Start shopping to see your book history here!</p>
                <div class="action-buttons action-buttons-centered">
                    <c:if test="${sessionScope.userRole == 'CUSTOMER'}">
                        <a href="${pageContext.request.contextPath}/controller/store" class="btn btn-primary">
                        🛒 Start Shopping
                    </a>
                    </c:if>
                    <a href="${pageContext.request.contextPath}/controller/<c:choose><c:when test='${sessionScope.userRole == "CUSTOMER"}'>customer-dashboard</c:when><c:otherwise>dashboard</c:otherwise></c:choose>" class="btn btn-secondary">
                        ← Back to Dashboard
                    </a>
                </div>
            </div>
        </div>
        <% } %>
        
        <% } else { %>
        <!-- Error Message -->
        <div class="form-card">
            <div class="error-message">
                <h3>❌ Error</h3>
                <p>Unable to load customer information.</p>
                <div class="action-buttons action-buttons-centered">
                    <a href="${pageContext.request.contextPath}/controller/customer-dashboard" class="btn btn-primary">
                        ← Back to Dashboard
                    </a>
                </div>
            </div>
        </div>
        <% } %>
    </div>
    
    <style>
        /* Enhanced spacing for purchase history page */
        .stats-grid.stats-grid-spaced {
            gap: 2rem;
            margin: 2rem 0;
        }
        
        .stat-card {
            padding: 2rem;
            text-align: center;
            transition: transform 0.3s ease, box-shadow 0.3s ease;
        }
        
        .stat-card:hover {
            transform: translateY(-5px);
            box-shadow: 0 8px 25px rgba(0, 0, 0, 0.15);
        }
        
        .stat-text {
            color: var(--color-text-muted);
            font-size: 0.9rem;
            margin-top: 0.5rem;
        }
        
        .transaction-table {
            margin-top: 1rem;
        }
        
        .transaction-table th {
            background: var(--color-bg-light-gray);
            padding: 1rem;
            font-weight: 600;
            color: var(--color-teal);
        }
        
        .transaction-table td {
            padding: 1rem;
            border-bottom: 1px solid var(--color-border-input);
        }
        
        .transaction-table tbody tr:hover {
            background: var(--color-bg-light-gray);
        }
        
        .no-data {
            text-align: center;
            padding: 3rem 2rem;
        }
        
        .no-data h3 {
            color: var(--color-teal);
            margin-bottom: 1rem;
        }
        
        .no-data p {
            font-size: 1.1rem;
            color: var(--color-text-muted);
            margin-bottom: 2rem;
        }
        
        .action-buttons-centered {
            justify-content: center;
            gap: 1rem;
        }
        
        .report-actions {
            display: flex;
            gap: 1rem;
            align-items: center;
        }
    </style>
</body>
</html> 