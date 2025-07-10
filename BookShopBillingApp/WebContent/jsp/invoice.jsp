<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="header.jspf" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <title>Online Billing System Pahana Edu - Invoice</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <div class="container">
        <div class="invoice">
            <div class="invoice-header">
                <div class="invoice-title">📚 BookShop</div>
                <div class="invoice-number">Invoice #BILL-2024-001</div>
            </div>
            
            <div class="invoice-body">
                <div class="invoice-info">
                    <div class="info-section">
                        <h3>Bill To</h3>
                        <div class="info-row">
                            <span class="info-label">Name:</span>
                            <span class="info-value">John Doe</span>
                        </div>
                        <div class="info-row">
                            <span class="info-label">Phone:</span>
                            <span class="info-value">+1 (555) 123-4567</span>
                        </div>
                        <div class="info-row">
                            <span class="info-label">Email:</span>
                            <span class="info-value">john.doe@email.com</span>
                        </div>
                    </div>
                    
                    <div class="info-section">
                        <h3>Bill Details</h3>
                        <div class="info-row">
                            <span class="info-label">Date:</span>
                            <span class="info-value">January 15, 2024</span>
                        </div>
                        <div class="info-row">
                            <span class="info-label">Time:</span>
                            <span class="info-value">14:30:25</span>
                        </div>
                        <div class="info-row">
                            <span class="info-label">Cashier:</span>
                            <span class="info-value">Sarah Johnson</span>
                        </div>
                    </div>
                </div>
                
                <table class="items-table">
                    <thead>
                        <tr>
                            <th>Item</th>
                            <th>Author</th>
                            <th>Qty</th>
                            <th>Price</th>
                            <th>Total</th>
                        </tr>
                    </thead>
                    <tbody>
                        <tr>
                            <td>The Great Gatsby</td>
                            <td>F. Scott Fitzgerald</td>
                            <td>2</td>
                            <td>$12.99</td>
                            <td>$25.98</td>
                        </tr>
                        <tr>
                            <td>To Kill a Mockingbird</td>
                            <td>Harper Lee</td>
                            <td>1</td>
                            <td>$14.99</td>
                            <td>$14.99</td>
                        </tr>
                    </tbody>
                </table>
                
                <div class="total-section">
                    <div class="total-row">
                        <span>Subtotal:</span>
                        <span>$40.97</span>
                    </div>
                    <div class="total-row">
                        <span>Discount (10%):</span>
                        <span>-$4.10</span>
                    </div>
                    <div class="total-row">
                        <span>Tax (8.5%):</span>
                        <span>$3.13</span>
                    </div>
                    <div class="total-row final">
                        <span>Total Amount:</span>
                        <span>$40.00</span>
                    </div>
                </div>
                
                <div class="actions">
                    <button class="action-btn print-btn" onclick="window.print()">🖨️ Print</button>
                    <a href="#" class="action-btn download-btn">📥 Download PDF</a>
                    <a href="${pageContext.request.contextPath}/controller/billing" class="action-btn back-btn">← Back to Billing</a>
                </div>
            </div>
        </div>
    </div>
</body>
</html> 