<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ include file="header.jspf" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${systemName != null ? systemName : 'Pahana Edu'} - Order Confirmation</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <div class="container">
        <div class="confirmation-container">
            <div class="confirmation-header">
                <div class="success-icon">✅</div>
                <h1>Order Placed Successfully!</h1>
                <p>Thank you for your purchase. Your order has been confirmed.</p>
            </div>
            
            <c:if test="${not empty bill}">
                <div class="order-details-card">
                    <div class="order-header">
                        <h2>📋 Order Details</h2>
                        <div class="order-number">Order #${bill.billNumber}</div>
                    </div>
                    
                    <div class="order-info-grid">
                        <div class="order-info-section">
                            <h3>📅 Order Information</h3>
                            <div class="info-list">
                                <div class="info-item">
                                    <span class="info-label">Order Date:</span>
                                    <span class="info-value">${bill.billDate}</span>
                                </div>
                                <div class="info-item">
                                    <span class="info-label">Payment Method:</span>
                                    <span class="info-value">${bill.paymentMethod}</span>
                                </div>
                                <div class="info-item">
                                    <span class="info-label">Status:</span>
                                    <span class="status-badge status-${bill.status.toLowerCase()}">${bill.status}</span>
                                </div>
                            </div>
                        </div>
                        
                        <div class="order-info-section">
                            <h3>👤 Customer Information</h3>
                            <div class="info-list">
                                <div class="info-item">
                                    <span class="info-label">Name:</span>
                                    <span class="info-value">${bill.customer.fullName}</span>
                                </div>
                                <div class="info-item">
                                    <span class="info-label">Email:</span>
                                    <span class="info-value">${bill.customer.email}</span>
                                </div>
                                <div class="info-item">
                                    <span class="info-label">Account:</span>
                                    <span class="info-value">${bill.customer.accountNumber}</span>
                                </div>
                            </div>
                        </div>
                    </div>
                    
                    <c:if test="${not empty billItems}">
                        <div class="order-items-section">
                            <h3>📚 Ordered Books</h3>
                            <div class="order-items-list">
                                <c:forEach var="item" items="${billItems}">
                                    <div class="order-item">
                                        <div class="item-info">
                                            <h4 class="item-title">${item.book.title}</h4>
                                            <p class="item-author">by ${item.book.author}</p>
                                            <p class="item-details">ISBN: ${item.book.isbn}</p>
                                        </div>
                                        <div class="item-quantity">
                                            <span class="quantity-label">Qty:</span>
                                            <span class="quantity-value">${item.quantity}</span>
                                        </div>
                                        <div class="item-pricing">
                                            <div class="unit-price">LKR ${String.format("%.2f", item.unitPrice)} each</div>
                                            <div class="item-total">LKR ${String.format("%.2f", item.total)}</div>
                                        </div>
                                    </div>
                                </c:forEach>
                            </div>
                        </div>
                    </c:if>
                    
                    <div class="order-summary">
                        <h3>💰 Order Summary</h3>
                        <div class="summary-details">
                            <div class="summary-row">
                                <span>Subtotal:</span>
                                <span>LKR ${String.format("%.2f", bill.subtotal)}</span>
                            </div>
                            <c:if test="${bill.discount > 0}">
                                <div class="summary-row">
                                    <span>Discount:</span>
                                    <span class="discount-amount">-LKR ${String.format("%.2f", bill.discount)}</span>
                                </div>
                            </c:if>
                            <div class="summary-row">
                                <span>Tax (10%):</span>
                                <span>LKR ${String.format("%.2f", bill.tax)}</span>
                            </div>
                            <c:if test="${bill.delivery}">
                                <div class="summary-row">
                                    <span>Delivery:</span>
                                    <span>LKR ${String.format("%.2f", bill.deliveryCharge)}</span>
                                </div>
                            </c:if>
                            <div class="summary-row total">
                                <span>Total Amount:</span>
                                <span>LKR ${String.format("%.2f", bill.total)}</span>
                            </div>
                        </div>
                    </div>
                </div>
                
                <div class="confirmation-actions">
                    <a href="${pageContext.request.contextPath}/controller/invoice?billId=${bill.id}" 
                       class="btn btn-primary" target="_blank">
                        🖨️ View Invoice
                    </a>
                    <a href="${pageContext.request.contextPath}/controller/customer-store" 
                       class="btn btn-secondary">
                        🛒 Continue Shopping
                    </a>
                    <a href="${pageContext.request.contextPath}/controller/customer-dashboard" 
                       class="btn btn-secondary">
                        🏠 Back to Dashboard
                    </a>
                </div>
                
                <div class="next-steps">
                    <h3>📋 What's Next?</h3>
                    <div class="steps-list">
                        <div class="step-item">
                            <span class="step-icon">📧</span>
                            <div class="step-content">
                                <h4>Order Confirmation</h4>
                                <p>You'll receive an email confirmation shortly with your order details.</p>
                            </div>
                        </div>
                        <div class="step-item">
                            <span class="step-icon">📦</span>
                            <div class="step-content">
                                <h4>Order Processing</h4>
                                <p>Our team will prepare your books for pickup or delivery.</p>
                            </div>
                        </div>
                        <div class="step-item">
                            <span class="step-icon">🎉</span>
                            <div class="step-content">
                                <h4>Enjoy Reading!</h4>
                                <p>Your books will be ready soon. Happy reading!</p>
                            </div>
                        </div>
                    </div>
                </div>
            </c:if>
        </div>
    </div>
    
    <style>
        .confirmation-container {
            max-width: 800px;
            margin: 0 auto;
        }
        
        .confirmation-header {
            text-align: center;
            padding: 2rem;
            background: linear-gradient(135deg, var(--color-success), var(--color-success-dark));
            color: white;
            border-radius: 12px;
            margin-bottom: 2rem;
        }
        
        .success-icon {
            font-size: 4rem;
            margin-bottom: 1rem;
        }
        
        .confirmation-header h1 {
            font-size: 2.5rem;
            margin-bottom: 0.5rem;
        }
        
        .order-details-card {
            background: white;
            border-radius: 12px;
            box-shadow: 0 4px 20px rgba(0,0,0,0.1);
            padding: 2rem;
            margin-bottom: 2rem;
        }
        
        .order-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 2rem;
            padding-bottom: 1rem;
            border-bottom: 2px solid var(--color-border-light);
        }
        
        .order-number {
            font-size: 1.2rem;
            font-weight: 600;
            color: var(--color-teal);
        }
        
        .order-info-grid {
            display: grid;
            grid-template-columns: 1fr 1fr;
            gap: 2rem;
            margin-bottom: 2rem;
        }
        
        .order-info-section h3 {
            color: var(--color-teal);
            margin-bottom: 1rem;
        }
        
        .info-list {
            display: flex;
            flex-direction: column;
            gap: 0.5rem;
        }
        
        .info-item {
            display: flex;
            justify-content: space-between;
            padding: 0.5rem 0;
        }
        
        .info-label {
            font-weight: 600;
            color: var(--color-text-secondary);
        }
        
        .info-value {
            color: var(--color-text-primary);
        }
        
        .order-items-section {
            margin-bottom: 2rem;
        }
        
        .order-items-list {
            display: flex;
            flex-direction: column;
            gap: 1rem;
        }
        
        .order-item {
            display: grid;
            grid-template-columns: 1fr auto auto;
            gap: 1rem;
            padding: 1rem;
            border: 1px solid var(--color-border-light);
            border-radius: 8px;
            background: var(--color-bg-light-gray);
        }
        
        .item-title {
            font-size: 1.1rem;
            font-weight: 600;
            color: var(--color-text-primary);
            margin-bottom: 0.25rem;
        }
        
        .item-author {
            color: var(--color-text-secondary);
            margin-bottom: 0.25rem;
        }
        
        .item-details {
            font-size: 0.9rem;
            color: var(--color-text-muted);
        }
        
        .item-quantity {
            display: flex;
            flex-direction: column;
            align-items: center;
            justify-content: center;
        }
        
        .quantity-label {
            font-size: 0.8rem;
            color: var(--color-text-muted);
        }
        
        .quantity-value {
            font-size: 1.2rem;
            font-weight: 600;
            color: var(--color-teal);
        }
        
        .item-pricing {
            display: flex;
            flex-direction: column;
            align-items: flex-end;
            justify-content: center;
        }
        
        .unit-price {
            font-size: 0.9rem;
            color: var(--color-text-secondary);
        }
        
        .item-total {
            font-size: 1.1rem;
            font-weight: 600;
            color: var(--color-teal);
        }
        
        .order-summary {
            background: var(--color-bg-light-gray);
            padding: 1.5rem;
            border-radius: 8px;
            margin-bottom: 2rem;
        }
        
        .order-summary h3 {
            color: var(--color-teal);
            margin-bottom: 1rem;
        }
        
        .summary-row {
            display: flex;
            justify-content: space-between;
            margin-bottom: 0.5rem;
            font-size: 1rem;
        }
        
        .summary-row.total {
            border-top: 2px solid var(--color-teal);
            padding-top: 0.5rem;
            margin-top: 0.5rem;
            font-weight: 700;
            font-size: 1.2rem;
            color: var(--color-teal);
        }
        
        .discount-amount {
            color: var(--color-success);
        }
        
        .confirmation-actions {
            display: flex;
            gap: 1rem;
            justify-content: center;
            flex-wrap: wrap;
            margin-bottom: 2rem;
        }
        
        .next-steps {
            background: var(--color-bg-info);
            padding: 2rem;
            border-radius: 12px;
            border-left: 4px solid var(--color-teal);
        }
        
        .next-steps h3 {
            color: var(--color-teal);
            margin-bottom: 1.5rem;
        }
        
        .steps-list {
            display: flex;
            flex-direction: column;
            gap: 1rem;
        }
        
        .step-item {
            display: flex;
            align-items: flex-start;
            gap: 1rem;
        }
        
        .step-icon {
            font-size: 1.5rem;
            margin-top: 0.25rem;
        }
        
        .step-content h4 {
            color: var(--color-text-primary);
            margin-bottom: 0.25rem;
        }
        
        .step-content p {
            color: var(--color-text-secondary);
            font-size: 0.9rem;
        }
        
        @media (max-width: 768px) {
            .order-info-grid {
                grid-template-columns: 1fr;
                gap: 1rem;
            }
            
            .order-item {
                grid-template-columns: 1fr;
                text-align: center;
                gap: 0.5rem;
            }
            
            .confirmation-actions {
                flex-direction: column;
            }
            
            .confirmation-header h1 {
                font-size: 2rem;
            }
        }
    </style>
</body>
</html>