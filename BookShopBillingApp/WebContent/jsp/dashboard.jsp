<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ include file="header.jspf" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${systemName != null ? systemName : 'Set System Name in Settings'} - Dashboard</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/compact-requests.css">
</head>
<body>
    <div class="container">
        <% if (request.getAttribute("error") != null) { %>
            <div class="error-message">
                <%= request.getAttribute("error") %>
            </div>
        <% } %>
        
        <div class="welcome-section">
            <h1 class="section-title"><span class="section-icon">📚</span>Welcome to BookShop Billing System</h1>
            <p>Manage your bookstore operations efficiently with our comprehensive billing solution.</p>
        </div>
        
        <div class="stats-grid">
            <% if (session.getAttribute("userRole") != null && session.getAttribute("userRole").equals("CASHIER")) { %>
                <div class="stat-card sales">
                    <h3><span class="stat-icon">&#36;</span> Today's Sales</h3>
                    <div class="stat-number">$${todaySales != null ? String.format("%.2f", todaySales) : "0.00"}</div>
                    <p>Revenue generated</p>
                </div>
                <div class="stat-card bills">
                    <h3><span class="stat-icon">&#128179;</span> Bills Generated</h3>
                    <div class="stat-number">${todayBills != null ? todayBills : 0}</div>
                    <p>Today's transactions</p>
                </div>
            <% } else { %>
                <div class="stat-card books">
                    <h3><span class="stat-icon">&#128214;</span> Total Books</h3>
                    <div class="stat-number">${totalBooks != null ? totalBooks : 0}</div>
                    <p>Available in inventory</p>
                </div>
                <div class="stat-card sales">
                    <h3><span class="stat-icon">&#36;</span> Today's Sales</h3>
                    <div class="stat-number">$${todaySales != null ? String.format("%.2f", todaySales) : "0.00"}</div>
                    <p>Revenue generated</p>
                </div>
                <div class="stat-card bills">
                    <h3><span class="stat-icon">&#128179;</span> Bills Generated</h3>
                    <div class="stat-number">${todayBills != null ? todayBills : 0}</div>
                    <p>Today's transactions</p>
                </div>
                <div class="stat-card customers">
                    <h3><span class="stat-icon">&#128101;</span> Customers</h3>
                    <div class="stat-number">${todayCustomers != null ? todayCustomers : 0}</div>
                    <p>Served today</p>
                </div>
            <% } %>
        </div>
        
        <div class="quick-actions">
            <h2 class="section-title"><span class="section-icon">⚡</span>Quick Actions</h2>
            <div class="action-buttons">
                <a href="${pageContext.request.contextPath}/controller/billing" class="action-btn btn-primary" title="Create a new bill for a customer">
                    <span class="action-icon">🛒</span> Create New Bill
                </a>
                <a href="${pageContext.request.contextPath}/controller/books" class="action-btn btn-secondary" title="View and manage all books in inventory">
                    <span class="action-icon">📚</span> Manage Books
                </a>
                <a href="${pageContext.request.contextPath}/controller/reports" class="action-btn btn-secondary" title="View sales and billing reports">
                    <span class="action-icon">📊</span> View Reports
                </a>
                <a href="${pageContext.request.contextPath}/controller/invoice" class="action-btn btn-danger" title="Generate and print invoices for customers">
                    <span class="action-icon">🧾</span> Generate Invoice
                </a>
                <a href="${pageContext.request.contextPath}/jsp/design-patterns.jsp" class="action-btn btn-success" title="Demonstrate all design patterns in action">
                    <span class="action-icon">🎯</span> Design Patterns Demo
                </a>
            </div>
        </div>
        
        <% if (request.getAttribute("recentBills") != null && !((java.util.List)request.getAttribute("recentBills")).isEmpty()) { %>
        <div class="recent-transactions">
            <h2 class="section-title"><span class="section-icon">🧾</span>Recent Transactions</h2>
            <table class="transaction-table">
                <thead>
                    <tr>
                        <th>Bill #</th>
                        <th>Customer</th>
                        <th>Date</th>
                        <th>Total</th>
                        <th>Payment</th>
                        <th>Status</th>
                        <th>Action</th>
                    </tr>
                </thead>
                <tbody>
                    <% for (model.Bill bill : (java.util.List<model.Bill>)request.getAttribute("recentBills")) { %>
                    <tr>
                        <td><strong><%= bill.getBillNumber() %></strong></td>
                        <td>
                            <% if (bill.getCustomer() != null) { %>
                                <%= bill.getCustomer().getFullName() %>
                                <% if (bill.getCustomer().getPhone() != null) { %>
                                    <br><small><%= bill.getCustomer().getPhone() %></small>
                                <% } %>
                            <% } else { %>
                                <em>N/A</em>
                            <% } %>
                        </td>
                        <td><%= new java.text.SimpleDateFormat("MMM dd, yyyy HH:mm").format(bill.getBillDate()) %></td>
                        <td><strong>$<%= String.format("%.2f", bill.getTotal()) %></strong></td>
                        <td><%= bill.getPaymentMethod() %></td>
                        <td>
                            <span class="status-badge status-<%= bill.getStatus().toLowerCase() %>">
                                <%= bill.getStatus() %>
                            </span>
                        </td>
                        <td>
                            <a href="${pageContext.request.contextPath}/controller/invoice?billId=<%= bill.getId() %>" 
                               class="btn-small btn-primary" target="_blank" title="View Invoice">
                                📄 Invoice
                            </a>
                        </td>
                    </tr>
                    <% } %>
                </tbody>
            </table>
            <div class="table-footer">
                <p><em>Showing last 5 transactions</em></p>
            </div>
        </div>
        <% } else { %>
        <div class="recent-transactions">
            <h2 class="section-title"><span class="section-icon">🧾</span>Recent Transactions</h2>
            <div class="no-data">
                <p>No recent transactions found.</p>
                <a href="${pageContext.request.contextPath}/controller/billing" class="btn btn-primary">
                    Create Your First Bill
                </a>
            </div>
        </div>
        <% } %>
        
        <!-- DEBUG: User Role: <%= session.getAttribute("userRole") %> -->
        <!-- DEBUG: Collection Requests: <%= request.getAttribute("collectionRequests") %> -->
        <!-- DEBUG: Collection Requests Size: <%= request.getAttribute("collectionRequests") != null ? ((java.util.List)request.getAttribute("collectionRequests")).size() : "null" %> -->
        <% if (session.getAttribute("userRole") != null && session.getAttribute("userRole").equals("ADMIN") && request.getAttribute("collectionRequests") != null && !((java.util.List)request.getAttribute("collectionRequests")).isEmpty()) { %>
        <div class="collection-requests">
            <div class="requests-header">
                <h2 class="section-title"><span class="section-icon">📚</span>Collection Requests (<%= ((java.util.List)request.getAttribute("collectionRequests")).size() %>)</h2>
                <div class="requests-controls">
                    <button class="toggle-view-btn" onclick="toggleRequestsView()">📋 Compact View</button>
                </div>
            </div>
            
            <div class="requests-container" id="requestsContainer">
                <% for (model.Bill collectionRequest : (java.util.List<model.Bill>)request.getAttribute("collectionRequests")) { %>
                <div class="request-card-compact">
                    <div class="request-info">
                        <div class="customer-summary">
                            <h4><%= collectionRequest.getCustomer().getFullName() %></h4>
                            <span class="request-meta"><%= new java.text.SimpleDateFormat("MMM dd").format(collectionRequest.getBillDate()) %> • <%= collectionRequest.getItems().size() %> books • LKR <%= String.format("%.2f", collectionRequest.getTotal()) %></span>
                        </div>
                        <div class="request-actions-compact">
                            <button class="view-details-btn" onclick="toggleDetails(<%= collectionRequest.getId() %>)">👁️</button>
                            <a href="${pageContext.request.contextPath}/controller/billing?collectionId=<%= collectionRequest.getId() %>" 
                               class="pay-btn">💳 Pay</a>
                        </div>
                    </div>
                    
                    <div class="request-details" id="details-<%= collectionRequest.getId() %>" style="display:none;">
                        <div class="contact-info">
                            <span>📧 <%= collectionRequest.getCustomer().getEmail() %></span>
                            <span>📞 <%= collectionRequest.getCustomer().getPhone() %></span>
                        </div>
                        <div class="books-summary">
                            <% for (model.BillItem item : collectionRequest.getItems()) { %>
                            <div class="book-line">
                                <span class="book-name"><%= item.getBook().getTitle() %></span>
                                <span class="book-price">LKR <%= String.format("%.2f", item.getUnitPrice()) %></span>
                            </div>
                            <% } %>
                        </div>
                    </div>
                </div>
                <% } %>
            </div>
        </div>
        <% } %>
    </div>

<script>
function toggleDetails(requestId) {
    const details = document.getElementById('details-' + requestId);
    if (details.style.display === 'none') {
        details.style.display = 'block';
    } else {
        details.style.display = 'none';
    }
}

function toggleRequestsView() {
    const container = document.getElementById('requestsContainer');
    const btn = document.querySelector('.toggle-view-btn');
    
    if (container.style.maxHeight === '400px' || !container.style.maxHeight) {
        container.style.maxHeight = 'none';
        btn.textContent = '📋 Compact View';
    } else {
        container.style.maxHeight = '400px';
        btn.textContent = '📋 Full View';
    }
}
</script>
</body>
</html> 