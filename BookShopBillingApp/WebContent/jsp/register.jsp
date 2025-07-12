<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Pahana Edu - Customer Registration</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/login.css">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet">
</head>
<body class="login-page">
    <div class="login-wrapper">
        <!-- Left Side - Image Section -->
        <div class="login-image-section">
            <div class="image-overlay">
                <div class="brand-content">
                    <div class="brand-logo">
                        <i class="fas fa-user-plus"></i>
                    </div>
                    <h1 class="brand-title">Pahana Edu</h1>
                    <p class="brand-tagline">Join our community of readers</p>
                    <div class="brand-features">
                        <div class="feature-item">
                            <i class="fas fa-check-circle"></i>
                            <span>Easy account creation</span>
                        </div>
                        <div class="feature-item">
                            <i class="fas fa-check-circle"></i>
                            <span>Secure shopping experience</span>
                        </div>
                        <div class="feature-item">
                            <i class="fas fa-check-circle"></i>
                            <span>Access to exclusive deals</span>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <!-- Right Side - Registration Form -->
        <div class="login-form-section">
            <div class="login-form-container register-form-container">
                <div class="form-header">
                    <h2>Create Your Account</h2>
                    <p>Join Pahana Edu and start your reading journey today</p>
                </div>

                <% if (request.getAttribute("error") != null) { %>
                    <div class="error-message">
                        <i class="fas fa-exclamation-circle"></i>
                        <%= request.getAttribute("error") %>
                    </div>
                <% } %>

                <% if (request.getAttribute("success") != null) { %>
                    <div class="success-message">
                        <i class="fas fa-check-circle"></i>
                        <%= request.getAttribute("success") %>
                    </div>
                <% } %>

                <form action="${pageContext.request.contextPath}/controller/register" method="post" class="login-form">
                    <div class="form-row">
                        <div class="form-group">
                            <label for="username">
                                <i class="fas fa-user"></i>
                                Username
                            </label>
                            <input type="text" id="username" name="username" placeholder="Enter your username" required 
                                   pattern="[a-zA-Z0-9_]{3,20}" 
                                   title="Username must be 3-20 characters, letters, numbers, and underscore only">
                        </div>
                        
                        <div class="form-group">
                            <label for="email">
                                <i class="fas fa-envelope"></i>
                                Email
                            </label>
                            <input type="email" id="email" name="email" placeholder="Enter your email" required>
                        </div>
                    </div>

                    <div class="form-row">
                        <div class="form-group">
                            <label for="fullName">
                                <i class="fas fa-id-card"></i>
                                Full Name
                            </label>
                            <input type="text" id="fullName" name="fullName" placeholder="Enter your full name" required>
                        </div>
                        
                        <div class="form-group">
                            <label for="phone">
                                <i class="fas fa-phone"></i>
                                Phone Number
                            </label>
                            <input type="tel" id="phone" name="phone" placeholder="Enter your phone number" required 
                                   pattern="[0-9+\-\s()]{10,15}" 
                                   title="Please enter a valid phone number">
                        </div>
                    </div>

                    <div class="form-group">
                        <label for="address">
                            <i class="fas fa-map-marker-alt"></i>
                            Address
                        </label>
                        <textarea id="address" name="address" placeholder="Enter your full address" required rows="3"></textarea>
                    </div>

                    <div class="form-row">
                        <div class="form-group">
                            <label for="password">
                                <i class="fas fa-lock"></i>
                                Password
                            </label>
                            <div class="password-input-wrapper">
                                <input type="password" id="password" name="password" placeholder="Enter your password" required 
                                       pattern=".{6,}" 
                                       title="Password must be at least 6 characters long">
                                <button type="button" class="password-toggle" onclick="togglePassword('password', 'password-icon')">
                                    <i class="fas fa-eye" id="password-icon"></i>
                                </button>
                            </div>
                        </div>
                        
                        <div class="form-group">
                            <label for="confirmPassword">
                                <i class="fas fa-lock"></i>
                                Confirm Password
                            </label>
                            <div class="password-input-wrapper">
                                <input type="password" id="confirmPassword" name="confirmPassword" placeholder="Confirm your password" required>
                                <button type="button" class="password-toggle" onclick="togglePassword('confirmPassword', 'confirm-password-icon')">
                                    <i class="fas fa-eye" id="confirm-password-icon"></i>
                                </button>
                            </div>
                        </div>
                    </div>

                    <div class="form-options">
                        <label class="checkbox-wrapper">
                            <input type="checkbox" id="terms" name="terms" required>
                            <span class="checkmark"></span>
                            I agree to the <a href="#" class="terms-link">Terms & Conditions</a>
                        </label>
                    </div>

                    <button type="submit" class="login-btn">
                        <i class="fas fa-user-plus"></i>
                        Create Account
                    </button>
                </form>

                <div class="register-section">
                    <p>Already have an account? 
                        <a href="${pageContext.request.contextPath}/jsp/login.jsp" class="register-link">
                            Login here
                        </a>
                    </p>
                </div>

                <div class="registration-benefits">
                    <h4>Why Register?</h4>
                    <div class="benefit-item">
                        <i class="fas fa-shopping-cart"></i>
                        <span>Easy online shopping</span>
                    </div>
                    <div class="benefit-item">
                        <i class="fas fa-history"></i>
                        <span>Order history tracking</span>
                    </div>
                    <div class="benefit-item">
                        <i class="fas fa-gift"></i>
                        <span>Exclusive member discounts</span>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <script>
        function togglePassword(inputId, iconId) {
            const passwordInput = document.getElementById(inputId);
            const passwordIcon = document.getElementById(iconId);
            
            if (passwordInput.type === 'password') {
                passwordInput.type = 'text';
                passwordIcon.classList.remove('fa-eye');
                passwordIcon.classList.add('fa-eye-slash');
            } else {
                passwordInput.type = 'password';
                passwordIcon.classList.remove('fa-eye-slash');
                passwordIcon.classList.add('fa-eye');
            }
        }

        // Password confirmation validation
        document.getElementById('confirmPassword').addEventListener('input', function() {
            const password = document.getElementById('password').value;
            const confirmPassword = this.value;
            
            if (password !== confirmPassword) {
                this.setCustomValidity('Passwords do not match');
                this.style.borderColor = 'var(--color-error)';
            } else {
                this.setCustomValidity('');
                this.style.borderColor = 'var(--color-success)';
            }
        });
        
        // Real-time password validation
        document.getElementById('password').addEventListener('input', function() {
            const confirmPassword = document.getElementById('confirmPassword');
            if (confirmPassword.value && this.value !== confirmPassword.value) {
                confirmPassword.setCustomValidity('Passwords do not match');
                confirmPassword.style.borderColor = 'var(--color-error)';
            } else if (confirmPassword.value) {
                confirmPassword.setCustomValidity('');
                confirmPassword.style.borderColor = 'var(--color-success)';
            }
        });

        // Add focus effects
        document.addEventListener('DOMContentLoaded', function() {
            const inputs = document.querySelectorAll('.form-group input, .form-group textarea');
            
            inputs.forEach(input => {
                input.addEventListener('focus', function() {
                    this.parentElement.classList.add('focused');
                });
                
                input.addEventListener('blur', function() {
                    if (!this.value) {
                        this.parentElement.classList.remove('focused');
                    }
                });
            });
        });
    </script>
</body>
</html> 