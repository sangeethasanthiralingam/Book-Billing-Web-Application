<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ include file="header.jspf" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Online Billing System Pahana Edu - Invoice</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <style>
        .invoice-container {
            max-width: 800px;
            margin: 0 auto;
            background: white;
            padding: 2rem;
            border-radius: 12px;
            box-shadow: 0 4px 20px rgba(0,0,0,0.1);
        }
        
        .invoice-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 2rem;
            padding-bottom: 1rem;
            border-bottom: 2px solid var(--color-teal);
        }
        
        .invoice-title {
            font-size: 2rem;
            font-weight: 700;
            color: var(--color-teal);
        }
        
        .invoice-number {
            font-size: 1.2rem;
            font-weight: 600;
            color: var(--color-text-secondary);
        }
        
        .invoice-info {
            display: grid;
            grid-template-columns: 1fr 1fr;
            gap: 2rem;
            margin-bottom: 2rem;
        }
        
        .info-section h3 {
            color: var(--color-teal);
            margin-bottom: 1rem;
            font-size: 1.1rem;
        }
        
        .info-row {
            display: flex;
            margin-bottom: 0.5rem;
        }
        
        .info-label {
            font-weight: 600;
            min-width: 80px;
            color: var(--color-text-secondary);
        }
        
        .info-value {
            color: var(--color-text-primary);
        }
        
        .items-table {
            width: 100%;
            border-collapse: collapse;
            margin-bottom: 2rem;
        }
        
        .items-table th,
        .items-table td {
            padding: 1rem;
            text-align: left;
            border-bottom: 1px solid var(--color-border-light);
        }
        
        .items-table th {
            background: var(--color-bg-light-gray);
            font-weight: 600;
            color: var(--color-teal);
        }
        
        .items-table tbody tr:hover {
            background: var(--color-bg-light-gray);
        }
        
        .total-section {
            background: var(--color-bg-light-gray);
            padding: 1.5rem;
            border-radius: 8px;
            margin-bottom: 2rem;
        }
        
        .total-row {
            display: flex;
            justify-content: space-between;
            margin-bottom: 0.5rem;
            font-size: 1rem;
        }
        
        .total-row.final {
            border-top: 2px solid var(--color-teal);
            padding-top: 0.5rem;
            margin-top: 0.5rem;
            font-weight: 700;
            font-size: 1.2rem;
            color: var(--color-teal);
        }
        
        .actions {
            display: flex;
            gap: 1rem;
            justify-content: center;
            flex-wrap: wrap;
        }
        
        .action-btn {
            padding: 0.75rem 1.5rem;
            border: none;
            border-radius: 8px;
            font-weight: 600;
            cursor: pointer;
            text-decoration: none;
            display: inline-flex;
            align-items: center;
            gap: 0.5rem;
            transition: all 0.3s ease;
        }
        
        .print-btn {
            background: var(--color-success);
            color: white;
        }
        
        .print-btn:hover {
            background: var(--color-success-dark);
            transform: translateY(-2px);
        }
        
        .download-btn {
            background: var(--color-saffron);
            color: white;
        }
        
        .download-btn:hover {
            background: var(--color-saffron-dark);
            transform: translateY(-2px);
        }
        
        .back-btn {
            background: var(--color-text-secondary);
            color: white;
        }
        
        .back-btn:hover {
            background: var(--color-text-primary);
            transform: translateY(-2px);
        }
        
        @media print {
            .actions {
                display: none;
            }
            
            .invoice-container {
                box-shadow: none;
                padding: 1rem;
            }
            
            body {
                background: white;
            }
        }
        
        @media (max-width: 768px) {
            .invoice-info {
                grid-template-columns: 1fr;
                gap: 1rem;
            }
            
            .invoice-header {
                flex-direction: column;
                gap: 1rem;
                text-align: center;
            }
            
            .actions {
                flex-direction: column;
            }
        }
    </style>
</head>
<body>
    <div class="container">
        <c:choose>
            <c:when test="${not empty bill}">
                <div class="invoice-container">
                    <div class="invoice-header">
                        <div class="invoice-title">📚 Pahana Edu BookShop</div>
                        <div class="invoice-number">Invoice #${bill.billNumber}</div>
                    </div>
                    
                    <div class="invoice-info">
                        <div class="info-section">
                            <h3>👤 Bill To</h3>
                            <div class="info-row">
                                <span class="info-label">Name:</span>
                                <span class="info-value">${customer.fullName}</span>
                            </div>
                            <div class="info-row">
                                <span class="info-label">Phone:</span>
                                <span class="info-value">${customer.phone != null ? customer.phone : 'N/A'}</span>
                            </div>
                            <div class="info-row">
                                <span class="info-label">Email:</span>
                                <span class="info-value">${customer.email}</span>
                            </div>
                            <c:if test="${customer.accountNumber != null}">
                                <div class="info-row">
                                    <span class="info-label">Account:</span>
                                    <span class="info-value">${customer.accountNumber}</span>
                                </div>
                            </c:if>
                        </div>
                        
                        <div class="info-section">
                            <h3>🧾 Bill Details</h3>
                            <div class="info-row">
                                <span class="info-label">Date:</span>
                                <span class="info-value">${bill.billDate}</span>
                            </div>
                            <div class="info-row">
                                <span class="info-label">Cashier:</span>
                                <span class="info-value">${cashier.fullName}</span>
                            </div>
                            <div class="info-row">
                                <span class="info-label">Payment:</span>
                                <span class="info-value">${bill.paymentMethod}</span>
                            </div>
                            <div class="info-row">
                                <span class="info-label">Status:</span>
                                <span class="info-value">${bill.status}</span>
                            </div>
                        </div>
                    </div>
                    
                    <c:if test="${not empty billItems}">
                        <table class="items-table">
                            <thead>
                                <tr>
                                    <th>📖 Book Title</th>
                                    <th>👤 Author</th>
                                    <th>📊 Qty</th>
                                    <th>💰 Unit Price</th>
                                    <th>💵 Total</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="item" items="${billItems}">
                                    <tr>
                                        <td>${item.book.title}</td>
                                        <td>${item.book.author}</td>
                                        <td>${item.quantity}</td>
                                        <td>LKR ${String.format("%.2f", item.unitPrice)}</td>
                                        <td>LKR ${String.format("%.2f", item.total)}</td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </c:if>
                    
                    <div class="total-section">
                        <div class="total-row">
                            <span>Subtotal:</span>
                            <span>LKR ${String.format("%.2f", bill.subtotal)}</span>
                        </div>
                        <c:if test="${bill.discount > 0}">
                            <div class="total-row">
                                <span>Discount:</span>
                                <span>-LKR ${String.format("%.2f", bill.discount)}</span>
                            </div>
                        </c:if>
                        <div class="total-row">
                            <span>Tax (10%):</span>
                            <span>LKR ${String.format("%.2f", bill.tax)}</span>
                        </div>
                        <c:if test="${bill.delivery}">
                            <div class="total-row">
                                <span>Delivery Charge:</span>
                                <span>LKR ${String.format("%.2f", bill.deliveryCharge)}</span>
                            </div>
                        </c:if>
                        <div class="total-row final">
                            <span>Total Amount:</span>
                            <span>LKR ${String.format("%.2f", bill.total)}</span>
                        </div>
                    </div>
                    
                    <c:if test="${bill.delivery and not empty bill.deliveryAddress}">
                        <div class="delivery-info">
                            <h4>🚚 Delivery Information</h4>
                            <p><strong>Address:</strong> ${bill.deliveryAddress}</p>
                        </div>
                    </c:if>
                    
                    <div class="actions">
                        <button class="action-btn print-btn" onclick="window.print()">
                            🖨️ Print Invoice
                        </button>
                        <a href="${pageContext.request.contextPath}/controller/billing" class="action-btn back-btn">
                            ← Back to Billing
                        </a>
                        <c:if test="${bill.status != 'CANCELLED'}">
                            <button class="action-btn" style="background: var(--color-error); color: white;" 
                                    onclick="cancelBill(${bill.id})">
                                ❌ Cancel Bill
                            </button>
                        </c:if>
                    </div>
                </div>
            </c:when>
            <c:otherwise>
                <div class="form-card">
                    <div class="error-message">
                        <h3>❌ Invoice Not Found</h3>
                        <p>The requested invoice could not be found or does not exist.</p>
                        <div class="form-actions">
                            <a href="${pageContext.request.contextPath}/controller/billing" class="btn btn-primary">
                                ← Back to Billing
                            </a>
                        </div>
                    </div>
                </div>
            </c:otherwise>
        </c:choose>
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
                        alert('Bill cancelled successfully!');
                        location.reload();
                    } else {
                        alert('Error cancelling bill: ' + data.message);
                    }
                })
                .catch(error => {
                    console.error('Error:', error);
                    alert('Error cancelling bill');
                });
            }
        }
        
        // Auto-print functionality
        const urlParams = new URLSearchParams(window.location.search);
        if (urlParams.get('print') === 'true') {
            setTimeout(() => {
                window.print();
            }, 1000);
        }
    </script>
</body>
</html>