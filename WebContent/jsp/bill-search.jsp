<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ include file="header.jspf" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Online Billing System Pahana Edu - Bill Search</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <div class="container">
        <div class="page-header">
            <div>
                <h1 class="page-header-title">🔍 Bill Search & Management</h1>
                <p class="page-header-subtitle">Search and manage all billing transactions</p>
            </div>
            <div class="page-header-actions">
                <a href="${pageContext.request.contextPath}/controller/billing" class="btn btn-primary">➕ Create New Bill</a>
            </div>
        </div>
        
        <!-- Advanced Search Form -->
        <form method="get" action="${pageContext.request.contextPath}/controller/bill-search" class="filter-bar">
            <div class="filter-fields">
                <div class="form-group">
                    <label for="search">Search</label>
                    <input type="text" id="search" name="search" value="${search}" 
                           placeholder="Bill number, customer name..." />
                </div>
                <div class="form-group">
                    <label for="status">Status</label>
                    <select id="status" name="status">
                        <option value="">All Status</option>
                        <option value="PENDING" ${status == 'PENDING' ? 'selected' : ''}>Pending</option>
                        <option value="PAID" ${status == 'PAID' ? 'selected' : ''}>Paid</option>
                        <option value="CANCELLED" ${status == 'CANCELLED' ? 'selected' : ''}>Cancelled</option>
                    </select>
                </div>
                <div class="form-group">
                    <label for="paymentMethod">Payment Method</label>
                    <select id="paymentMethod" name="paymentMethod">
                        <option value="">All Methods</option>
                        <option value="CASH" ${paymentMethod == 'CASH' ? 'selected' : ''}>Cash</option>
                        <option value="CARD" ${paymentMethod == 'CARD' ? 'selected' : ''}>Card</option>
                        <option value="UPI" ${paymentMethod == 'UPI' ? 'selected' : ''}>UPI</option>
                    </select>
                </div>
                <div class="form-group">
                    <label for="dateFrom">Date From</label>
                    <input type="date" id="dateFrom" name="dateFrom" value="${dateFrom}" />
                </div>
                <div class="form-group">
                    <label for="dateTo">Date To</label>
                    <input type="date" id="dateTo" name="dateTo" value="${dateTo}" />
                </div>
            </div>
            <div class="filter-actions">
                <button type="submit" class="btn btn-primary" title="Search">🔍</button>
                <a href="${pageContext.request.contextPath}/controller/bill-search" class="btn btn-secondary" title="Clear">✖️</a>
            </div>
        </form>
        
        <!-- Results Summary -->
        <c:if test="${not empty search || not empty status || not empty paymentMethod || not empty dateFrom || not empty dateTo}">
            <div class="info-card">
                <div class="info-content">
                    <span class="info-icon">📊</span>
                    <div class="info-text">
                        <strong>Search Results:</strong> 
                        Found ${bills.size()} bills matching your criteria
                    </div>
                </div>
            </div>
        </c:if>
        
        <!-- Bills Table -->
        <div class="report-card">
            <div class="report-header">
                <div class="report-title">📋 Bills List</div>
                <div class="report-stats">
                    <span class="stat-item">
                        <span class="stat-icon">📄</span>
                        <span class="stat-label">Total:</span>
                        <span class="stat-value">${bills.size()}</span>
                    </span>
                </div>
            </div>
            <div class="report-body">
                <c:choose>
                    <c:when test="${not empty bills}">
                        <table class="transaction-table">
                            <thead>
                                <tr>
                                    <th>Bill Number</th>
                                    <th>Date</th>
                                    <th>Customer</th>
                                    <th>Cashier</th>
                                    <th>Total</th>
                                    <th>Payment</th>
                                    <th>Status</th>
                                    <th>Actions</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="bill" items="${bills}">
                                    <tr>
                                        <td><strong>${bill.billNumber}</strong></td>
                                        <td>${bill.billDate}</td>
                                        <td>${bill.customer.fullName}</td>
                                        <td>${bill.cashier.fullName}</td>
                                        <td><strong>LKR ${String.format("%.2f", bill.total)}</strong></td>
                                        <td>
                                            <span class="payment-badge payment-${bill.paymentMethod.toLowerCase()}">
                                                ${bill.paymentMethod}
                                            </span>
                                        </td>
                                        <td>
                                            <span class="status-badge status-${bill.status.toLowerCase()}">
                                                ${bill.status}
                                            </span>
                                        </td>
                                        <td class="action-buttons">
                                            <a href="${pageContext.request.contextPath}/controller/invoice?billId=${bill.id}" 
                                               class="view-btn" title="View Invoice">👁️</a>
                                            <c:if test="${bill.status != 'CANCELLED'}">
                                                <button onclick="cancelBill(${bill.id})" 
                                                        class="delete-btn" title="Cancel Bill">❌</button>
                                            </c:if>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </c:when>
                    <c:otherwise>
                        <div class="no-data">
                            <h3>📋 No Bills Found</h3>
                            <p>No bills match your search criteria. Try adjusting your filters or create a new bill.</p>
                            <div class="action-buttons action-buttons-centered">
                                <a href="${pageContext.request.contextPath}/controller/billing" class="btn btn-primary">
                                    ➕ Create New Bill
                                </a>
                                <a href="${pageContext.request.contextPath}/controller/bill-search" class="btn btn-secondary">
                                    🔄 Clear Filters
                                </a>
                            </div>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </div>
    
    <script>
        function cancelBill(billId) {
            if (confirm('Are you sure you want to cancel this bill? This action cannot be undone.')) {
                fetch('${pageContext.request.contextPath}/controller/cancel-bill?billId=' + billId, {
                    method: 'POST',
                    headers: {'Content-Type': 'application/json'}
                })
                .then(response => response.json())
                .then(data => {
                    if (data.success) {
                        showNotification('Bill cancelled successfully!', 'success');
                        setTimeout(() => location.reload(), 1000);
                    } else {
                        showNotification('Error cancelling bill: ' + data.message, 'error');
                    }
                })
                .catch(error => {
                    console.error('Error:', error);
                    showNotification('Error cancelling bill', 'error');
                });
            }
        }
        
        // Notification system
        function showNotification(message, type = 'info') {
            const notification = document.createElement('div');
            notification.className = `notification notification-${type}`;
            notification.innerHTML = `
                <span class="notification-icon">
                    ${type === 'success' ? '✅' : type === 'error' ? '❌' : type === 'warning' ? '⚠️' : 'ℹ️'}
                </span>
                <span class="notification-message">${message}</span>
            `;
            notification.style.cssText = `
                position: fixed;
                top: 20px;
                right: 20px;
                padding: 12px 20px;
                border-radius: 8px;
                color: white;
                font-weight: 500;
                z-index: 10000;
                animation: slideIn 0.3s ease;
                display: flex;
                align-items: center;
                gap: 8px;
                max-width: 400px;
                box-shadow: 0 4px 12px rgba(0,0,0,0.15);
            `;
            
            switch (type) {
                case 'success':
                    notification.style.backgroundColor = '#28a745';
                    break;
                case 'error':
                    notification.style.backgroundColor = '#dc3545';
                    break;
                case 'warning':
                    notification.style.backgroundColor = '#ffc107';
                    notification.style.color = '#212529';
                    break;
                default:
                    notification.style.backgroundColor = '#17a2b8';
            }
            
            document.body.appendChild(notification);
            
            setTimeout(() => {
                notification.style.animation = 'slideOut 0.3s ease';
                setTimeout(() => {
                    if (notification.parentNode) {
                        notification.parentNode.removeChild(notification);
                    }
                }, 300);
            }, 4000);
        }
        
        // Add CSS animations
        const style = document.createElement('style');
        style.textContent = `
            @keyframes slideIn {
                from { transform: translateX(100%); opacity: 0; }
                to { transform: translateX(0); opacity: 1; }
            }
            @keyframes slideOut {
                from { transform: translateX(0); opacity: 1; }
                to { transform: translateX(100%); opacity: 0; }
            }
            
            .payment-badge {
                padding: 0.25rem 0.5rem;
                border-radius: 4px;
                font-size: 0.8rem;
                font-weight: 600;
                text-transform: uppercase;
            }
            
            .payment-cash {
                background: var(--color-bg-success);
                color: var(--color-success);
            }
            
            .payment-card {
                background: var(--color-bg-info);
                color: var(--color-teal);
            }
            
            .payment-upi {
                background: var(--color-bg-error);
                color: var(--color-saffron);
            }
            
            .status-paid {
                background: var(--color-bg-success);
                color: var(--color-success);
            }
            
            .status-pending {
                background: var(--color-bg-info);
                color: var(--color-saffron);
            }
            
            .status-cancelled {
                background: var(--color-bg-error);
                color: var(--color-error);
            }
        `;
        document.head.appendChild(style);
    </script>
</body>
</html>