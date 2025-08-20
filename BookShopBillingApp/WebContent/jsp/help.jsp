<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ include file="header.jspf" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${systemName != null ? systemName : 'Online Billing System Pahana Edu'} - Help</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <div class="container">
        <div class="help-section">
            <h2>📖 System Help & User Guide</h2>
            <p>Welcome to the BookShop Billing System! This guide will help you understand how to use all the features effectively.</p>
            <c:if test="${empty sessionScope.userRole}">
                <div class="login-info">
                    <h4>🔐 Demo Login Information</h4>
                    <p><strong>Admin:</strong> username: admin, password: admin123</p>
                    <p><strong>Cashier:</strong> username: cashier, password: cashier123</p>
                    <p><strong>Customer:</strong> Register a new account or use existing customer credentials</p>
                </div>
            </c:if>
        </div>
        <c:choose>
            <c:when test="${sessionScope.userRole == 'CUSTOMER'}">
                <!-- Customer Help Section -->
                <div class="help-section">
                    <h2>👤 Customer Portal Guide</h2>
                    <div class="feature-grid">
                        <div class="feature-card">
                            <h4>🏠 My Dashboard</h4>
                            <p>View your profile information, account details, and recent purchase summary.</p>
                        </div>
                        <div class="feature-card">
                            <h4>📚 Purchase History</h4>
                            <p>View detailed history of all your book purchases with dates, prices, and quantities.</p>
                        </div>
                        <div class="feature-card">
                            <h4>👤 My Profile</h4>
                            <p>Update your personal information, contact details, and account settings.</p>
                        </div>
                        <div class="feature-card">
                            <h4>🔐 Account Security</h4>
                            <p>Change your password and manage your account security settings.</p>
                        </div>
                    </div>
                </div>
                <div class="help-section">
                    <h2>📋 Customer Step-by-Step Instructions</h2>
                    <h3>1. Accessing Your Dashboard</h3>
                    <ul>
                        <li>Login with your customer credentials</li>
                        <li>You'll be automatically redirected to your personal dashboard</li>
                        <li>View your profile summary and recent purchases</li>
                    </ul>
                    <h3>2. Viewing Your Purchase History</h3>
                    <ul>
                        <li>Click "View All Purchases" button on your dashboard</li>
                        <li>See all books you've purchased with details</li>
                        <li>View purchase dates, prices, and quantities</li>
                        <li>Track your total spending and purchase count</li>
                    </ul>
                    <h3>3. Managing Your Profile</h3>
                    <ul>
                        <li>Click "My Profile" to view your account information</li>
                        <li>View your account number, contact details, and member since date</li>
                        <li>Update your personal information if needed</li>
                        <li>Change your password for security</li>
                    </ul>
                    <h3>4. Account Security</h3>
                    <ul>
                        <li>Use the "Reset Password" feature to change your password</li>
                        <li>Always log out when finished using the system</li>
                        <li>Keep your login credentials secure</li>
                    </ul>
                </div>
                <div class="help-section">
                    <h2>⚠️ Important Notes for Customers</h2>
                    <ul>
                        <li>Your account number is unique and cannot be changed</li>
                        <li>Purchase history shows all your book purchases with detailed information</li>
                        <li>You can only view your own information and purchase history</li>
                        <li>Contact the store staff for any billing or purchase inquiries</li>
                        <li>Your profile information is used for billing purposes</li>
                    </ul>
                </div>
                <div class="help-section">
                    <h2>🔧 Customer Troubleshooting</h2>
                    <h3>Common Issues:</h3>
                    <ul>
                        <li><strong>Can't login:</strong> Check your username and password, ensure caps lock is off</li>
                        <li><strong>No purchase history:</strong> You may not have made any purchases yet</li>
                        <li><strong>Profile not updating:</strong> Contact store staff for assistance</li>
                        <li><strong>Forgot password:</strong> Contact store staff to reset your account</li>
                        <li><strong>Page not loading:</strong> Try refreshing the page or logging out and back in</li>
                    </ul>
                </div>
            </c:when>
            <c:when test="${sessionScope.userRole == 'CASHIER'}">
                <!-- Cashier Help Section -->
                <div class="help-section">
                    <h2>🧾 Cashier Portal Guide</h2>
                    <div class="feature-grid">
                        <div class="feature-card">
                            <h4>🧾 Billing System</h4>
                            <p>Create bills for customers, calculate totals, and generate invoices.</p>
                        </div>
                    </div>
                </div>
                <div class="help-section">
                    <h2>📋 Cashier Step-by-Step Instructions</h2>
                    <h3>1. Creating a Bill</h3>
                    <ul>
                        <li>Go to the Billing section</li>
                        <li>Select a customer and add books to the bill</li>
                        <li>System calculates totals and generates the bill</li>
                        <li>Print or save the invoice for the customer</li>
                    </ul>
                </div>
                <div class="help-section">
                    <h2>⚠️ Important Notes for Cashiers</h2>
                    <ul>
                        <li>Always verify customer details before billing</li>
                        <li>Ensure all sales are recorded accurately</li>
                        <li>Contact admin for technical issues or inventory updates</li>
                    </ul>
                </div>
            </c:when>
            <c:when test="${sessionScope.userRole == 'ADMIN'}">
                <!-- Admin Help Section -->
                <div class="help-section">
                    <h2>🛠️ Admin Portal Guide</h2>
                    <div class="feature-grid">
                        <div class="feature-card">
                            <h4>👥 Customer Management</h4>
                            <p>Add, edit, and delete customers. Manage account numbers and details.</p>
                        </div>
                        <div class="feature-card">
                            <h4>📚 Book Management</h4>
                            <p>Add, edit, and delete books. Manage inventory, prices, and book details.</p>
                        </div>
                        <div class="feature-card">
                            <h4>🧾 Billing System</h4>
                            <p>Oversee all bills, monitor transactions, and resolve billing issues.</p>
                        </div>
                        <div class="feature-card">
                            <h4>📊 Reports & Analytics</h4>
                            <p>View and export sales reports, customer statistics, and analytics.</p>
                        </div>
                        <div class="feature-card">
                            <h4>⚙️ System Configuration</h4>
                            <p>Update system settings, manage users, and configure store information.</p>
                        </div>
                    </div>
                </div>
                <div class="help-section">
                    <h2>📋 Admin Step-by-Step Instructions</h2>
                    <h3>1. Managing Users</h3>
                    <ul>
                        <li>Go to User Management to add or edit users</li>
                        <li>Assign roles and manage access</li>
                    </ul>
                    <h3>2. Managing Books</h3>
                    <ul>
                        <li>Go to Books section to add, edit, or delete books</li>
                        <li>Monitor inventory and update prices</li>
                    </ul>
                    <h3>3. Overseeing Billing</h3>
                    <ul>
                        <li>Monitor all bills and transactions</li>
                        <li>Resolve billing issues and approve refunds if needed</li>
                    </ul>
                    <h3>4. Viewing Reports</h3>
                    <ul>
                        <li>Go to Reports section for analytics and export options</li>
                    </ul>
                    <h3>5. Configuring System</h3>
                    <ul>
                        <li>Go to System Config to update store information and settings</li>
                    </ul>
                </div>
                <div class="help-section">
                    <h2>⚠️ Important Notes for Admins</h2>
                    <ul>
                        <li>Regularly back up system data</li>
                        <li>Monitor user activity and permissions</li>
                        <li>Contact technical support for system issues</li>
                    </ul>
                </div>
            </c:when>
            <c:otherwise>
                <!-- Default: Customer Help Section (as before) -->
                <div class="help-section">
                    <h2>👤 Customer Portal Guide</h2>
                    <div class="feature-grid">
                        <div class="feature-card">
                            <h4>🏠 My Dashboard</h4>
                            <p>View your profile information, account details, and recent purchase summary.</p>
                        </div>
                        <div class="feature-card">
                            <h4>📚 Purchase History</h4>
                            <p>View detailed history of all your book purchases with dates, prices, and quantities.</p>
                        </div>
                        <div class="feature-card">
                            <h4>👤 My Profile</h4>
                            <p>Update your personal information, contact details, and account settings.</p>
                        </div>
                        <div class="feature-card">
                            <h4>🔐 Account Security</h4>
                            <p>Change your password and manage your account security settings.</p>
                        </div>
                    </div>
                </div>
                <div class="help-section">
                    <h2>📋 Customer Step-by-Step Instructions</h2>
                    <h3>1. Accessing Your Dashboard</h3>
                    <ul>
                        <li>Login with your customer credentials</li>
                        <li>You'll be automatically redirected to your personal dashboard</li>
                        <li>View your profile summary and recent purchases</li>
                    </ul>
                    <h3>2. Viewing Your Purchase History</h3>
                    <ul>
                        <li>Click "View All Purchases" button on your dashboard</li>
                        <li>See all books you've purchased with details</li>
                        <li>View purchase dates, prices, and quantities</li>
                        <li>Track your total spending and purchase count</li>
                    </ul>
                    <h3>3. Managing Your Profile</h3>
                    <ul>
                        <li>Click "My Profile" to view your account information</li>
                        <li>View your account number, contact details, and member since date</li>
                        <li>Update your personal information if needed</li>
                        <li>Change your password for security</li>
                    </ul>
                    <h3>4. Account Security</h3>
                    <ul>
                        <li>Use the "Reset Password" feature to change your password</li>
                        <li>Always log out when finished using the system</li>
                        <li>Keep your login credentials secure</li>
                    </ul>
                </div>
                <div class="help-section">
                    <h2>⚠️ Important Notes for Customers</h2>
                    <ul>
                        <li>Your account number is unique and cannot be changed</li>
                        <li>Purchase history shows all your book purchases with detailed information</li>
                        <li>You can only view your own information and purchase history</li>
                        <li>Contact the store staff for any billing or purchase inquiries</li>
                        <li>Your profile information is used for billing purposes</li>
                    </ul>
                </div>
                <div class="help-section">
                    <h2>🔧 Customer Troubleshooting</h2>
                    <h3>Common Issues:</h3>
                    <ul>
                        <li><strong>Can't login:</strong> Check your username and password, ensure caps lock is off</li>
                        <li><strong>No purchase history:</strong> You may not have made any purchases yet</li>
                        <li><strong>Profile not updating:</strong> Contact store staff for assistance</li>
                        <li><strong>Forgot password:</strong> Contact store staff to reset your account</li>
                        <li><strong>Page not loading:</strong> Try refreshing the page or logging out and back in</li>
                    </ul>
                </div>
            </c:otherwise>
        </c:choose>
        <div class="help-section">
            <h2>📞 Contact Information</h2>
            <p>For additional support or questions:</p>
            <ul>
                <li><strong>Store Staff:</strong> Contact the store directly for immediate assistance</li>
                <li><strong>Technical Support:</strong> Contact system administrator for technical issues</li>
                <li><strong>Customer Service:</strong> Available during store hours</li>
            </ul>
        </div>
    </div>
</body>
</html> 