<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ include file="header.jspf" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <title>Online Billing System Pahana Edu - Customer Form</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    
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
        
        <div class="form-card form-card-wide">
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
                    <div class="form-text">Account number is automatically generated</div>
                </div>
                
                <div class="form-group">
                    <label for="name">Full Name *</label>
                    <input type="text" id="name" name="name" 
                           value="${customer != null ? customer.name : ''}" 
                           required>
                </div>
                
                <div class="form-group">
                    <label for="address">Address *</label>
                    <textarea id="address" name="address" required rows="3">${customer != null ? customer.address : ''}</textarea>
                </div>
                
                <div class="form-group">
                    <label for="telephone">Telephone *</label>
                    <input type="tel" id="telephone" name="telephone" 
                           value="${customer != null ? customer.telephone : ''}" 
                           pattern="[0-9+\-\s()]{10,15}" 
                           title="Please enter a valid phone number"
                           required>
                    <div class="form-text">Please enter a valid phone number</div>
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
        var isNewCustomer = <%= request.getAttribute("customer") == null ? "true" : "false" %>;
        if (isNewCustomer) {
            window.onload = function() {
                fetch('<%= request.getContextPath() %>/controller/generate-account-number')
                    .then(function(response) {
                        return response.text();
                    })
                    .then(function(accountNumber) {
                        document.getElementById('accountNumber').value = accountNumber;
                    })
                    .catch(function(error) {
                        console.error('Error generating account number:', error);
                    });
            };
        }
    </script>
</body>
</html> 