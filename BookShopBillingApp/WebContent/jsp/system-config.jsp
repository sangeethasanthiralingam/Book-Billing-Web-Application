<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ include file="header.jspf" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <title>Online Billing System Pahana Edu - System Configuration</title>
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
        
        <div class="config-section">
            <div class="section-header">
                <h1 class="section-title">System Configuration</h1>
                <button class="btn btn-primary" onclick="saveAllConfigs()">Save All Changes</button>
            </div>
            
            <div class="category-tabs">
                <button class="category-tab active" onclick="showCategory('BILLING')">Billing</button>
                <button class="category-tab" onclick="showCategory('DISCOUNT')">Discounts</button>
                <button class="category-tab" onclick="showCategory('SYSTEM')">Company Info</button>
                <button class="category-tab" onclick="showCategory('ACCOUNT')">Accounts</button>
                <button class="category-tab" onclick="showCategory('INVENTORY')">Inventory</button>
            </div>
            
            <form id="configForm" action="${pageContext.request.contextPath}/controller/update-configs" method="post">
                <!-- Billing Configuration -->
                <div id="BILLING" class="config-category active">
                    <div class="config-grid">
                        <div class="config-item">
                            <div class="config-key">Unit Rate</div>
                            <div class="config-description">Rate per unit consumed for billing calculation</div>
                            <input type="number" step="0.01" class="config-input" name="UNIT_RATE" 
                                   value="${configs.get('UNIT_RATE')}" required>
                        </div>
                        
                        <div class="config-item">
                            <div class="config-key">Tax Rate</div>
                            <div class="config-description">Tax rate as decimal (e.g., 0.10 for 10%)</div>
                            <input type="number" step="0.01" class="config-input" name="TAX_RATE" 
                                   value="${configs.get('TAX_RATE')}" required>
                        </div>
                        
                        <div class="config-item">
                            <div class="config-key">Delivery Charge</div>
                            <div class="config-description">Fixed delivery charge amount</div>
                            <input type="number" step="0.01" class="config-input" name="DELIVERY_CHARGE" 
                                   value="${configs.get('DELIVERY_CHARGE')}" required>
                        </div>
                    </div>
                </div>
                
                <!-- Discount Configuration -->
                <div id="DISCOUNT" class="config-category">
                    <div class="config-grid">
                        <div class="config-item">
                            <div class="config-key">Level 1 Threshold</div>
                            <div class="config-description">Units threshold for first discount level</div>
                            <input type="number" class="config-input" name="DISCOUNT_LEVEL_1_THRESHOLD" 
                                   value="${configs.get('DISCOUNT_LEVEL_1_THRESHOLD')}" required>
                        </div>
                        
                        <div class="config-item">
                            <div class="config-key">Level 1 Percentage</div>
                            <div class="config-description">Discount percentage for level 1 (e.g., 0.05 for 5%)</div>
                            <input type="number" step="0.01" class="config-input" name="DISCOUNT_LEVEL_1_PERCENT" 
                                   value="${configs.get('DISCOUNT_LEVEL_1_PERCENT')}" required>
                        </div>
                        
                        <div class="config-item">
                            <div class="config-key">Level 2 Threshold</div>
                            <div class="config-description">Units threshold for second discount level</div>
                            <input type="number" class="config-input" name="DISCOUNT_LEVEL_2_THRESHOLD" 
                                   value="${configs.get('DISCOUNT_LEVEL_2_THRESHOLD')}" required>
                        </div>
                        
                        <div class="config-item">
                            <div class="config-key">Level 2 Percentage</div>
                            <div class="config-description">Discount percentage for level 2 (e.g., 0.10 for 10%)</div>
                            <input type="number" step="0.01" class="config-input" name="DISCOUNT_LEVEL_2_PERCENT" 
                                   value="${configs.get('DISCOUNT_LEVEL_2_PERCENT')}" required>
                        </div>
                        
                        <div class="config-item">
                            <div class="config-key">Level 3 Threshold</div>
                            <div class="config-description">Units threshold for third discount level</div>
                            <input type="number" class="config-input" name="DISCOUNT_LEVEL_3_THRESHOLD" 
                                   value="${configs.get('DISCOUNT_LEVEL_3_THRESHOLD')}" required>
                        </div>
                        
                        <div class="config-item">
                            <div class="config-key">Level 3 Percentage</div>
                            <div class="config-description">Discount percentage for level 3 (e.g., 0.15 for 15%)</div>
                            <input type="number" step="0.01" class="config-input" name="DISCOUNT_LEVEL_3_PERCENT" 
                                   value="${configs.get('DISCOUNT_LEVEL_3_PERCENT')}" required>
                        </div>
                    </div>
                </div>
                
                <!-- System Configuration -->
                <div id="SYSTEM" class="config-category">
                    <div class="config-grid">
                        <div class="config-item">
                            <div class="config-key">Company Name</div>
                            <div class="config-description">Company name displayed on bills and reports</div>
                            <input type="text" class="config-input" name="COMPANY_NAME" 
                                   value="${configs.get('COMPANY_NAME')}" required>
                        </div>
                        
                        <div class="config-item">
                            <div class="config-key">Company Address</div>
                            <div class="config-description">Company address for bills</div>
                            <input type="text" class="config-input" name="COMPANY_ADDRESS" 
                                   value="${configs.get('COMPANY_ADDRESS')}" required>
                        </div>
                        
                        <div class="config-item">
                            <div class="config-key">Company Phone</div>
                            <div class="config-description">Company phone number</div>
                            <input type="text" class="config-input" name="COMPANY_PHONE" 
                                   value="${configs.get('COMPANY_PHONE')}" required>
                        </div>
                        
                        <div class="config-item">
                            <div class="config-key">Company Email</div>
                            <div class="config-description">Company email address</div>
                            <input type="email" class="config-input" name="COMPANY_EMAIL" 
                                   value="${configs.get('COMPANY_EMAIL')}" required>
                        </div>
                    </div>
                </div>
                
                <!-- Account Configuration -->
                <div id="ACCOUNT" class="config-category">
                    <div class="config-grid">
                        <div class="config-item">
                            <div class="config-key">Account Prefix</div>
                            <div class="config-description">Prefix for customer account numbers</div>
                            <input type="text" class="config-input" name="ACCOUNT_PREFIX" 
                                   value="${configs.get('ACCOUNT_PREFIX')}" required>
                        </div>
                        
                        <div class="config-item">
                            <div class="config-key">Account Length</div>
                            <div class="config-description">Number of digits in account numbers</div>
                            <input type="number" class="config-input" name="ACCOUNT_LENGTH" 
                                   value="${configs.get('ACCOUNT_LENGTH')}" required>
                        </div>
                    </div>
                </div>
                
                <!-- Inventory Configuration -->
                <div id="INVENTORY" class="config-category">
                    <div class="config-grid">
                        <div class="config-item">
                            <div class="config-key">Low Stock Threshold</div>
                            <div class="config-description">Minimum stock level for low stock alerts</div>
                            <input type="number" class="config-input" name="LOW_STOCK_THRESHOLD" 
                                   value="${configs.get('LOW_STOCK_THRESHOLD')}" required>
                        </div>
                        
                        <div class="config-item">
                            <div class="config-key">Auto Restock Enabled</div>
                            <div class="config-description">Enable automatic restock alerts</div>
                            <select class="config-input" name="AUTO_RESTOCK_ENABLED">
                                <option value="true" ${configs.get('AUTO_RESTOCK_ENABLED') == 'true' ? 'selected' : ''}>Yes</option>
                                <option value="false" ${configs.get('AUTO_RESTOCK_ENABLED') == 'false' ? 'selected' : ''}>No</option>
                            </select>
                        </div>
                    </div>
                </div>
            </form>
        </div>
    </div>
    
    <script>
        function showCategory(category) {
            // Hide all categories
            document.querySelectorAll('.config-category').forEach(cat => {
                cat.classList.remove('active');
            });
            
            // Remove active class from all tabs
            document.querySelectorAll('.category-tab').forEach(tab => {
                tab.classList.remove('active');
            });
            
            // Show selected category
            document.getElementById(category).classList.add('active');
            
            // Add active class to clicked tab
            event.target.classList.add('active');
        }
        
        function saveAllConfigs() {
            document.getElementById('configForm').submit();
        }
    </script>
</body>
</html> 