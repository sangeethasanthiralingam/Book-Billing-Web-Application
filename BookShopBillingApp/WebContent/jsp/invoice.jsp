<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ include file="header.jspf" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <title>Online Billing System Pahana Edu - Invoice</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    
    <div class="container">
        <div class="invoice">
            <div class="invoice-header">
                <div class="invoice-title">📚 ${systemName != null ? systemName : 'BookShop Billing System'}</div>
                <div class="invoice-number">Invoice #${bill != null ? bill.billNumber : 'N/A'}</div>
            </div>
            
            <div class="invoice-body">
                <% if (request.getAttribute("bill") != null) { %>
                <div class="invoice-info">
                    <div class="info-section">
                        <h3>Bill To</h3>
                        <div class="info-row">
                            <span class="info-label">Name:</span>
                            <span class="info-value">${bill.customer != null ? bill.customer.fullName : 'N/A'}</span>
                        </div>
                        <div class="info-row">
                            <span class="info-label">Phone:</span>
                            <span class="info-value">${bill.customer != null && bill.customer.phone != null ? bill.customer.phone : 'N/A'}</span>
                        </div>
                        <div class="info-row">
                            <span class="info-label">Email:</span>
                            <span class="info-value">${bill.customer != null && bill.customer.email != null ? bill.customer.email : 'N/A'}</span>
                        </div>
                    </div>
                    
                    <div class="info-section">
                        <h3>Bill Details</h3>
                        <div class="info-row">
                            <span class="info-label">Date:</span>
                            <span class="info-value">${bill.billDate != null ? bill.billDate : 'N/A'}</span>
                        </div>
                        <div class="info-row">
                            <span class="info-label">Payment:</span>
                            <span class="info-value">${bill.paymentMethod != null ? bill.paymentMethod : 'N/A'}</span>
                        </div>
                        <div class="info-row">
                            <span class="info-label">Cashier:</span>
                            <span class="info-value">${bill.cashier != null ? bill.cashier.fullName : 'N/A'}</span>
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
                        <% if (request.getAttribute("billItems") != null && !((java.util.List)request.getAttribute("billItems")).isEmpty()) { %>
                            <% for (model.BillItem item : (java.util.List<model.BillItem>)request.getAttribute("billItems")) { %>
                            <tr>
                                <td><%= item.getBook() != null ? item.getBook().getTitle() : "N/A" %></td>
                                <td><%= item.getBook() != null ? item.getBook().getAuthor() : "N/A" %></td>
                                <td><%= item.getQuantity() %></td>
                                <td>$<%= String.format("%.2f", item.getUnitPrice()) %></td>
                                <td>$<%= String.format("%.2f", item.getTotal()) %></td>
                            </tr>
                            <% } %>
                        <% } else { %>
                            <tr>
                                <td colspan="5" style="text-align: center; color: #999;">No items found</td>
                            </tr>
                        <% } %>
                    </tbody>
                </table>
                
                <div class="total-section">
                    <div class="total-row">
                        <span>Subtotal:</span>
                        <span>$${String.format("%.2f", bill.subtotal)}</span>
                    </div>
                    <div class="total-row">
                        <span>Discount:</span>
                        <span>-$${String.format("%.2f", bill.discount)}</span>
                    </div>
                    <div class="total-row">
                        <span>Tax:</span>
                        <span>$${String.format("%.2f", bill.tax)}</span>
                    </div>
                    <div class="total-row final">
                        <span>Total Amount:</span>
                        <span>$${String.format("%.2f", bill.total)}</span>
                    </div>
                </div>
                <% } else { %>
                <div class="no-data">
                    <p>No bill data found.</p>
                </div>
                <% } %>
                
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