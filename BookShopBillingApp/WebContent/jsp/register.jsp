<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Online Billing System Pahana Edu - Customer Registration</title>     
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <div class="container">
        <div class="login-container">
            <div class="logo">📚 Online Billing System</div>
            <div class="subtitle">Pahana Edu - Customer Registration</div>
            
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
            
            <form action="${pageContext.request.contextPath}/controller/register" method="post">
                <div class="form-group">
                    <label for="username">Username *</label>
                    <input type="text" id="username" name="username" required 
                           pattern="[a-zA-Z0-9_]{3,20}" 
                           title="Username must be 3-20 characters, letters, numbers, and underscore only">
                </div>
                
                <div class="form-group">
                    <label for="email">Email *</label>
                    <input type="email" id="email" name="email" required>
                </div>
                
                <div class="form-group">
                    <label for="fullName">Full Name *</label>
                    <input type="text" id="fullName" name="fullName" required>
                </div>
                
                <div class="form-group">
                    <label for="phone">Phone Number *</label>
                    <input type="tel" id="phone" name="phone" required 
                           pattern="[0-9+\-\s()]{10,15}" 
                           title="Please enter a valid phone number">
                </div>
                
                <div class="form-group">
                    <label for="address">Address *</label>
                    <textarea id="address" name="address" required rows="3"></textarea>
                </div>
                
                <div class="form-group">
                    <label for="password">Password *</label>
                    <input type="password" id="password" name="password" required 
                           pattern=".{6,}" 
                           title="Password must be at least 6 characters long">
                </div>
                
                <div class="form-group">
                    <label for="confirmPassword">Confirm Password *</label>
                    <input type="password" id="confirmPassword" name="confirmPassword" required>
                </div>
                
                <button type="submit" class="login-btn">Register</button>
            </form>
            
            <div class="register-section">
                <p>Already have an account? <a href="${pageContext.request.contextPath}/jsp/login.jsp" class="register-link">Login here</a></p>
            </div>
            
            <div class="footer">
                <p>By registering, you agree to our terms and conditions.</p>
            </div>
        </div>
    </div>
    
    <script>
        // Password confirmation validation
        document.getElementById('confirmPassword').addEventListener('input', function() {
            const password = document.getElementById('password').value;
            const confirmPassword = this.value;
            
            if (password !== confirmPassword) {
                this.setCustomValidity('Passwords do not match');
            } else {
                this.setCustomValidity('');
            }
        });
        
        // Real-time password validation
        document.getElementById('password').addEventListener('input', function() {
            const confirmPassword = document.getElementById('confirmPassword');
            if (confirmPassword.value && this.value !== confirmPassword.value) {
                confirmPassword.setCustomValidity('Passwords do not match');
            } else {
                confirmPassword.setCustomValidity('');
            }
        });
    </script>
</body>
</html> 