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
        
        <!-- Quick Actions Section -->
        <div class="form-card">
            <div class="form-header">
                <h2>⚡ Quick Actions</h2>
            </div>
            <div class="action-buttons action-buttons-flex">
                <a href="${pageContext.request.contextPath}/controller/customer-profile" class="btn btn-primary">
                    👤 View Full Profile
                </a>
                <a href="${pageContext.request.contextPath}/controller/customer-form?id=${sessionScope.userId}" class="btn btn-secondary">
                    ✏️ Edit Info
                </a>
                <a href="${pageContext.request.contextPath}/controller/customer-reset-password" class="btn btn-secondary">
                    🔐 Change Password
                </a>
                <a href="${pageContext.request.contextPath}/controller/customer-store" class="btn btn-success">
                    🛒 Shop Books
                </a>
            </div>
        </div>
        
        <!-- Profile Summary Section -->
        <div class="form-card">
            <div class="form-header">
                <h2>📋 My Profile Summary</h2>
            </div>
            <% model.User customer = (model.User)request.getAttribute("customer"); %>
            <div class="profile-grid profile-grid-spaced">
                <div class="profile-item">
                    <label><strong>👤 Full Name:</strong></label>
                    <span><%= customer.getFullName() %></span>
                </div>
                <div class="profile-item">
                    <label><strong>📧 Email:</strong></label>
                    <span><%= customer.getEmail() %></span>
                </div>
                <div class="profile-item">
                    <label><strong>📞 Phone:</strong></label>
                    <span><%= customer.getPhone() != null ? customer.getPhone() : "Not provided" %></span>
                </div>
                <div class="profile-item">
                    <label><strong>🆔 Account Number:</strong></label>
                    <span><%= customer.getAccountNumber() != null ? customer.getAccountNumber() : "N/A" %></span>
                </div>
                <div class="profile-item">
                    <label><strong>🏠 Address:</strong></label>
                    <span><%= customer.getAddress() != null ? customer.getAddress() : "Not provided" %></span>
                </div>
                <div class="profile-item">
                    <label><strong>📅 Member Since:</strong></label>
                    <span><%= customer.getCreatedAt() != null ? customer.getCreatedAt().toString().substring(0, 10) : "N/A" %></span>
                </div>
            </div>
        </div>
        
        <!-- Statistics Section -->
        <div class="stats-grid stats-grid-spaced">
            <div class="stat-card">
                <h3>💰 Total Spent</h3>
                <div class="stat-number">$${totalSpent != null ? String.format("%.2f", totalSpent) : "0.00"}</div>
                <div class="stat-text">Total amount spent on books</div>
            </div>
            <div class="stat-card">
                <h3>📚 Total Bills</h3>
                <div class="stat-number">${billCount != null ? billCount : 0}</div>
                <div class="stat-text">Number of purchase transactions</div>
            </div>
            <div class="stat-card">
                <h3>📖 Books Purchased</h3>
                <div class="stat-number">${customer.getUnitsConsumed() != null ? customer.getUnitsConsumed() : 0}</div>
                <div class="stat-text">Total books in your collection</div>
            </div>
        </div>
        
        <!-- Recent Transactions Section -->
        <div class="recent-transactions">
            <div class="recent-transactions-header">
                <h2>📚 My Recent Purchases</h2>
                <a href="${pageContext.request.contextPath}/controller/customer-purchases?id=${sessionScope.userId}" class="btn btn-primary">
                    View All Purchases
                </a>
            </div>
            
            <% java.util.List bills = (java.util.List)request.getAttribute("bills");
               if (bills != null && !bills.isEmpty()) { %>
            <div class="report-card">
                <div class="report-body">
                    <table class="transaction-table">
                        <thead>
                            <tr>
                                <th>📖 Book Name</th>
                                <th>📅 Purchase Date</th>
                                <th>💰 Amount</th>
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
                                <td>$${String.format("%.2f", item.getTotal())}</td>
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
                <p>📚 You have not made any purchases yet. Start shopping to see your book history here!</p>
                <div class="action-buttons action-buttons-centered">
                    <c:if test="${sessionScope.userRole == 'CUSTOMER'}">
                        <a href="${pageContext.request.contextPath}/controller/store" class="btn btn-primary">
                            🛒 Start Shopping
                        </a>
                    </c:if>
                </div>
            </div>
            <% } %>
        </div>
    </div>
    
    <style>
        /* Enhanced spacing for dashboard elements */
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
        
        .recent-transactions {
            margin-top: 2rem;
        }
        
        .recent-transactions-header {
            margin-bottom: 1.5rem;
        }
        
        .no-data {
            text-align: center;
            padding: 3rem 2rem;
            background: var(--color-bg-light-gray);
            border-radius: 10px;
            margin: 1rem 0;
        }
        
        .no-data p {
            font-size: 1.1rem;
            color: var(--color-text-muted);
            margin-bottom: 1.5rem;
        }
        
        .action-buttons-centered {
            justify-content: center;
        }
    </style>
</body>
</html> 