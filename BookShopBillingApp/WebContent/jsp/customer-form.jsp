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
        <c:if test="${not empty error}">
            <div class="error-message">${error}</div>
        </c:if>
        <c:if test="${not empty success}">
            <div class="success-message">${success}</div>
        </c:if>
        <div class="form-card form-card-wide">
            <div class="form-header">
                <h1>${not empty customer ? 'Edit Customer' : 'Add New Customer'}</h1>
                <p>${not empty customer ? 'Update customer information' : 'Create a new customer account'}</p>
            </div>
            <form action="${pageContext.request.contextPath}/controller/save-customer" method="post">
                <c:if test="${not empty customer}">
                    <input type="hidden" name="id" value="${customer.id}">
                </c:if>
                <div class="form-group">
                    <label for="accountNumber">Account Number</label>
                    <input type="text" id="accountNumber" name="accountNumber"
                           value="${not empty customer ? customer.accountNumber : ''}"
                           class="account-number" readonly>
                    <div class="form-text">Account number is automatically generated</div>
                </div>
                <div class="form-group">
                    <label for="username">Username *</label>
                    <input type="text" id="username" name="username"
                           value="${not empty customer ? customer.username : ''}"
                           required>
                </div>
                <div class="form-group">
                    <label for="email">Email *</label>
                    <input type="email" id="email" name="email"
                           value="${not empty customer ? customer.email : ''}"
                           required>
                </div>
                <div class="form-group">
                    <label for="fullName">Full Name *</label>
                    <input type="text" id="fullName" name="fullName"
                           value="${not empty customer ? customer.fullName : ''}"
                           required>
                </div>
                <div class="form-group">
                    <label for="address">Address *</label>
                    <textarea id="address" name="address" required rows="3">${not empty customer ? customer.address : ''}</textarea>
                </div>
                <div class="form-group">
                    <label for="phone">Telephone *</label>
                    <input type="tel" id="phone" name="phone"
                           value="${not empty customer ? customer.phone : ''}"
                           pattern="[0-9+\-\s()]{10,15}"
                           title="Please enter a valid phone number"
                           required>
                    <div class="form-text">Please enter a valid phone number</div>
                </div>
                <div class="form-group">
                    <label for="password">Password *</label>
                    <input type="password" id="password" name="password"
                           value=""
                           required>
                    <div class="form-text">${not empty customer ? 'Leave blank to keep current password.' : ''}</div>
                </div>
                <div class="form-actions">
                    <button type="submit" class="btn btn-primary">
                        ${not empty customer ? 'Update Customer' : 'Add Customer'}
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
        var isNewCustomer = ${empty customer ? "true" : "false"};
        if (isNewCustomer) {
            window.onload = function() {
                fetch('${pageContext.request.contextPath}/controller/generate-account-number')
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