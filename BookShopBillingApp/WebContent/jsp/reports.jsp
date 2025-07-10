<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="header.jspf" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <title>Online Billing System Pahana Edu - Reports</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <div class="container">
        <% if (request.getAttribute("error") != null) { %>
            <div class="error-message">
                <%= request.getAttribute("error") %>
            </div>
        <% } %>
        
        <div class="header">
            <h1>Reports & Analytics</h1>
        </div>
        
        <div class="filter-section">
            <h3 style="margin-bottom: 1rem;">Filter Reports</h3>
            <div class="filter-row">
                <div class="form-group">
                    <label>Date From</label>
                    <input type="date" value="2024-01-01">
                </div>
                <div class="form-group">
                    <label>Date To</label>
                    <input type="date" value="2024-01-31">
                </div>
                <div class="form-group">
                    <label>Report Type</label>
                    <select>
                        <option>Sales Report</option>
                        <option>Inventory Report</option>
                        <option>Customer Report</option>
                        <option>Revenue Report</option>
                    </select>
                </div>
                <button class="generate-btn">Generate Report</button>
            </div>
        </div>
        
        <div class="reports-grid">
            <div class="report-card">
                <div class="report-header">
                    <div class="report-title">📊 Sales Summary</div>
                    <div class="report-subtitle">January 2024</div>
                </div>
                <div class="report-body">
                    <div class="metric">
                        <span class="metric-label">Total Sales</span>
                        <span class="metric-value">$${totalBills != null ? String.format("%.2f", totalBills * 50.82) : "0.00"}</span>
                    </div>
                    <div class="metric">
                        <span class="metric-label">Total Bills</span>
                        <span class="metric-value">${totalBills != null ? totalBills : 0}</span>
                    </div>
                    <div class="metric">
                        <span class="metric-label">Total Books</span>
                        <span class="metric-value">${totalBooks != null ? totalBooks : 0}</span>
                    </div>
                    <div class="metric">
                        <span class="metric-label">Average Bill Value</span>
                        <span class="metric-value">$${totalBills != null && totalBills > 0 ? String.format("%.2f", (totalBills * 50.82) / totalBills) : "0.00"}</span>
                    </div>
                </div>
            </div>
            
            <div class="report-card">
                <div class="report-header">
                    <div class="report-title">📚 Top Selling Books</div>
                    <div class="report-subtitle">This Month</div>
                </div>
                <div class="report-body">
                    <% if (request.getAttribute("allBooks") != null && !((java.util.List)request.getAttribute("allBooks")).isEmpty()) { %>
                        <% 
                        java.util.List<model.Book> books = (java.util.List<model.Book>)request.getAttribute("allBooks");
                        int count = 0;
                        for (model.Book book : books) {
                            if (count >= 4) break;
                        %>
                    <div class="metric">
                            <span class="metric-label">${book.title}</span>
                            <span class="metric-value">${book.quantity} copies</span>
                    </div>
                        <% 
                            count++;
                        } 
                        %>
                    <% } else { %>
                        <div class="no-data">
                            <p>No books data available</p>
                    </div>
                    <% } %>
                </div>
            </div>
            
            <div class="report-card">
                <div class="report-header">
                    <div class="report-title">💰 Revenue Analysis</div>
                    <div class="report-subtitle">Monthly Trend</div>
                </div>
                <div class="report-body">
                    <div class="chart-placeholder">
                        Chart visualization would go here
                    </div>
                    <div class="metric">
                        <span class="metric-label">Highest Revenue Day</span>
                        <span class="metric-value">Jan 15 - $1,250</span>
                    </div>
                    <div class="metric">
                        <span class="metric-label">Lowest Revenue Day</span>
                        <span class="metric-value">Jan 8 - $320</span>
                    </div>
                </div>
            </div>
            
            <div class="report-card">
                <div class="report-header">
                    <div class="report-title">📦 Inventory Status</div>
                    <div class="report-subtitle">Current Stock</div>
                </div>
                <div class="report-body">
                    <div class="metric">
                        <span class="metric-label">Total Books</span>
                        <span class="metric-value">${totalBooks != null ? totalBooks : 0}</span>
                    </div>
                    <div class="metric">
                        <span class="metric-label">Low Stock Items</span>
                        <span class="metric-value negative">${lowStockBooks != null ? lowStockBooks : 0}</span>
                    </div>
                    <div class="metric">
                        <span class="metric-label">Total Bills</span>
                        <span class="metric-value">${totalBills != null ? totalBills : 0}</span>
                    </div>
                    <div class="metric">
                        <span class="metric-label">Average Stock Level</span>
                        <span class="metric-value">${totalBooks != null && totalBooks > 0 ? String.format("%.1f", totalBooks / 4.0) : "0.0"}</span>
                    </div>
                </div>
            </div>
        </div>
        
        <div class="export-section">
            <h3 style="margin-bottom: 1rem;">Export Reports</h3>
            <div class="export-buttons">
                <button class="export-btn export-pdf">📄 Export as PDF</button>
                <button class="export-btn export-excel">📊 Export to Excel</button>
                <button class="export-btn export-csv">📋 Export as CSV</button>
            </div>
        </div>
    </div>
</body>
</html> 