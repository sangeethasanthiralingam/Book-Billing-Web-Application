<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <title>Online Billing System Pahana Edu - Customer Form</title>
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
        
        <% if (request.getAttribute("success") != null) { %>
            <div class="success-message">
                <%= request.getAttribute("success") %>
            </div>
        <% } %>
        
        <div class="form-card">
            <div class="form-header">
                <h1>${customer != null ? 'Edit Customer' : 'Add New Customer'}</h1>
                <p>${customer != null ? 'Update customer information' : 'Create a new customer account'}</p>
            </div>
            
            <form action="${pageContext.request.contextPath}/controller/save-customer" method="post">
                <% if (customer != null) { %>
                    <input type="hidden" name="id" value="${customer.id}">
                <% } %>
                
                <div class="form-group">
                    <label for="accountNumber">Account Number</label>
                    <input type="text" id="accountNumber" name="accountNumber" 
                           value="${customer != null ? customer.accountNumber : ''}" 
                           class="account-number" readonly>
                </div>
                
                <div class="form-group">
                    <label for="name">Full Name *</label>
                    <input type="text" id="name" name="name" 
                           value="${customer != null ? customer.name : ''}" 
                           required>
                </div>
                
                <div class="form-group">
                    <label for="address">Address *</label>
                    <textarea id="address" name="address" required>${customer != null ? customer.address : ''}</textarea>
                </div>
                
                <div class="form-group">
                    <label for="telephone">Telephone *</label>
                    <input type="tel" id="telephone" name="telephone" 
                           value="${customer != null ? customer.telephone : ''}" 
                           required>
                </div>
                
                <div class="form-actions">
                    <button type="submit" class="btn btn-primary">
                        ${customer != null ? 'Update Customer' : 'Add Customer'}
                    </button>
                    <a href="${pageContext.request.contextPath}/controller/customers" class="btn btn-secondary">
                        Cancel
                    </a>
                </div>
            </form>
        </div>
    </div>
    
    <script>
        // Auto-generate account number for new customers
        <% if (request.getAttribute("customer") == null) { %>
        window.onload = function() {
            fetch('${pageContext.request.contextPath}/controller/generate-account-number')
                .then(response => response.text())
                .then(accountNumber => {
                    document.getElementById('accountNumber').value = accountNumber;
                })
                .catch(error => {
                    console.error('Error generating account number:', error);
                });
        };
        <% } %>
    </script>
</body>
</html> 