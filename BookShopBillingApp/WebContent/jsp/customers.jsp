<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <title>Online Billing System Pahana Edu - Customer Management</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <nav class="navbar">
        <div class="logo">📚 Online Billing System Pahana Edu</div>
        <div class="nav-links">
            <a href="${pageContext.request.contextPath}/controller/dashboard">Dashboard</a>
            <a href="${pageContext.request.contextPath}/controller/customers">Customers</a>
            <a href="${pageContext.request.contextPath}/controller/books">Books</a>
            <a href="${pageContext.request.contextPath}/controller/billing">Billing</a>
            <a href="${pageContext.request.contextPath}/controller/reports">Reports</a>
        </div>
    </nav>
    
    <div class="container">
        <% if (request.getAttribute("error") != null) { %>
            <div class="error-message">
                <%= request.getAttribute("error") %>
            </div>
        <% } %>
        
        <div class="header">
            <h1>Customer Management</h1>
            <button class="add-customer-btn" onclick="showAddCustomerForm()">+ Add New Customer</button>
        </div>
        
        <div class="customers-table">
            <div class="table-header">
                <div class="table-row">
                    <div>Account Number</div>
                    <div>Name</div>
                    <div>Address</div>
                    <div>Telephone</div>
                    <div>Units Consumed</div>
                    <div>Actions</div>
                </div>
            </div>
            
            <% if (request.getAttribute("customers") != null && !((java.util.List)request.getAttribute("customers")).isEmpty()) { %>
                <% for (model.Customer customer : (java.util.List<model.Customer>)request.getAttribute("customers")) { %>
                <div class="table-row">
                    <div>${customer.accountNumber}</div>
                    <div>${customer.name}</div>
                    <div>${customer.address}</div>
                    <div>${customer.telephone}</div>
                    <div class="units-consumed">${customer.unitsConsumed}</div>
                    <div class="action-buttons">
                        <button class="view-btn" onclick="viewCustomer(${customer.id})">View</button>
                        <button class="edit-btn" onclick="editCustomer(${customer.id})">Edit</button>
                        <button class="delete-btn" onclick="deleteCustomer(${customer.id})">Delete</button>
                    </div>
                </div>
                <% } %>
            <% } else { %>
                <div class="no-customers">
                    <p>No customers found. Add some customers to get started!</p>
                </div>
            <% } %>
        </div>
    </div>
    
    <script>
        function showAddCustomerForm() {
            window.location.href = '${pageContext.request.contextPath}/controller/customer-form';
        }
        
        function viewCustomer(customerId) {
            window.location.href = '${pageContext.request.contextPath}/controller/customer-details?id=' + customerId;
        }
        
        function editCustomer(customerId) {
            window.location.href = '${pageContext.request.contextPath}/controller/customer-form?id=' + customerId;
        }
        
        function deleteCustomer(customerId) {
            if (confirm('Are you sure you want to delete this customer?')) {
                window.location.href = '${pageContext.request.contextPath}/controller/delete-customer?id=' + customerId;
            }
        }
    </script>
</body>
</html> 