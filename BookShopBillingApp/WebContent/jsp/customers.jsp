<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <title>Online Billing System Pahana Edu - Customer Management</title>
    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }
        
        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            background: #f5f7fa;
        }
        
        .navbar {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            padding: 1rem 2rem;
            display: flex;
            justify-content: space-between;
            align-items: center;
            box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
        }
        
        .logo {
            font-size: 1.5rem;
            font-weight: bold;
        }
        
        .nav-links {
            display: flex;
            gap: 2rem;
        }
        
        .nav-links a {
            color: white;
            text-decoration: none;
            padding: 0.5rem 1rem;
            border-radius: 5px;
            transition: background-color 0.3s ease;
        }
        
        .nav-links a:hover {
            background-color: rgba(255, 255, 255, 0.1);
        }
        
        .container {
            max-width: 1200px;
            margin: 2rem auto;
            padding: 0 2rem;
        }
        
        .header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 2rem;
        }
        
        .add-customer-btn {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            border: none;
            padding: 0.75rem 1.5rem;
            border-radius: 8px;
            cursor: pointer;
            font-weight: 500;
        }
        
        .customers-table {
            background: white;
            border-radius: 10px;
            box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
            overflow: hidden;
        }
        
        .table-header {
            background: #f8f9fa;
            padding: 1rem;
            border-bottom: 1px solid #e1e5e9;
        }
        
        .table-row {
            display: grid;
            grid-template-columns: 1fr 1fr 1fr 1fr 1fr auto;
            gap: 1rem;
            padding: 1rem;
            border-bottom: 1px solid #e1e5e9;
            align-items: center;
        }
        
        .table-row:last-child {
            border-bottom: none;
        }
        
        .table-row:hover {
            background-color: #f8f9fa;
        }
        
        .action-buttons {
            display: flex;
            gap: 0.5rem;
        }
        
        .edit-btn, .delete-btn, .view-btn {
            padding: 0.5rem 1rem;
            border: none;
            border-radius: 5px;
            cursor: pointer;
            font-size: 0.9rem;
        }
        
        .edit-btn {
            background: #28a745;
            color: white;
        }
        
        .delete-btn {
            background: #dc3545;
            color: white;
        }
        
        .view-btn {
            background: #17a2b8;
            color: white;
        }
        
        .error-message {
            background: #fee;
            color: #c33;
            padding: 1rem;
            border-radius: 5px;
            margin-bottom: 1rem;
            border: 1px solid #fcc;
        }
        
        .no-customers {
            text-align: center;
            padding: 2rem;
            color: #666;
        }
        
        .units-consumed {
            font-weight: bold;
            color: #667eea;
        }
    </style>
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