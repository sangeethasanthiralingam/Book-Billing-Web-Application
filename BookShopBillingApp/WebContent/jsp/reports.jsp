<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>BookShop Billing - Reports</title>
    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }
        
        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            background: #f5f7fa;
        }
        
        .navbar {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            padding: 1rem 2rem;
            display: flex;
            justify-content: space-between;
            align-items: center;
            box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
        }
        
        .logo {
            font-size: 1.5rem;
            font-weight: bold;
        }
        
        .nav-links {
            display: flex;
            gap: 2rem;
        }
        
        .nav-links a {
            color: white;
            text-decoration: none;
            padding: 0.5rem 1rem;
            border-radius: 5px;
            transition: background-color 0.3s ease;
        }
        
        .nav-links a:hover {
            background-color: rgba(255, 255, 255, 0.1);
        }
        
        .container {
            max-width: 1200px;
            margin: 2rem auto;
            padding: 0 2rem;
        }
        
        .header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 2rem;
        }
        
        .filter-section {
            background: white;
            padding: 1.5rem;
            border-radius: 10px;
            box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
            margin-bottom: 2rem;
        }
        
        .filter-row {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
            gap: 1rem;
            align-items: end;
        }
        
        .form-group {
            display: flex;
            flex-direction: column;
        }
        
        .form-group label {
            margin-bottom: 0.5rem;
            color: #333;
            font-weight: 500;
        }
        
        .form-group input, .form-group select {
            padding: 0.75rem;
            border: 2px solid #e1e5e9;
            border-radius: 8px;
            font-size: 1rem;
        }
        
        .generate-btn {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            border: none;
            padding: 0.75rem 1.5rem;
            border-radius: 8px;
            cursor: pointer;
            font-weight: 500;
        }
        
        .reports-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(400px, 1fr));
            gap: 2rem;
        }
        
        .report-card {
            background: white;
            border-radius: 10px;
            box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
            overflow: hidden;
        }
        
        .report-header {
            background: #f8f9fa;
            padding: 1.5rem;
            border-bottom: 1px solid #e1e5e9;
        }
        
        .report-title {
            font-size: 1.2rem;
            color: #333;
            margin-bottom: 0.5rem;
        }
        
        .report-subtitle {
            color: #666;
            font-size: 0.9rem;
        }
        
        .report-body {
            padding: 1.5rem;
        }
        
        .metric {
            display: flex;
            justify-content: space-between;
            align-items: center;
            padding: 1rem 0;
            border-bottom: 1px solid #e1e5e9;
        }
        
        .metric:last-child {
            border-bottom: none;
        }
        
        .metric-label {
            color: #666;
        }
        
        .metric-value {
            font-weight: bold;
            color: #333;
        }
        
        .metric-value.positive {
            color: #28a745;
        }
        
        .metric-value.negative {
            color: #dc3545;
        }
        
        .chart-placeholder {
            height: 200px;
            background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%);
            border-radius: 8px;
            display: flex;
            align-items: center;
            justify-content: center;
            color: #666;
            font-style: italic;
        }
        
        .export-section {
            background: white;
            padding: 1.5rem;
            border-radius: 10px;
            box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
            margin-top: 2rem;
        }
        
        .export-buttons {
            display: flex;
            gap: 1rem;
            flex-wrap: wrap;
        }
        
        .export-btn {
            padding: 0.75rem 1.5rem;
            border: none;
            border-radius: 8px;
            cursor: pointer;
            font-weight: 500;
        }
        
        .export-btn.pdf {
            background: #dc3545;
            color: white;
        }
        
        .export-btn.excel {
            background: #28a745;
            color: white;
        }
        
        .error-message {
            background: #fee;
            color: #c33;
            padding: 1rem;
            border-radius: 5px;
            margin-bottom: 1rem;
            border: 1px solid #fcc;
        }
        
        .no-data {
            text-align: center;
            padding: 2rem;
            color: #666;
        }
        
        .export-pdf {
            background: #dc3545;
            color: white;
        }
        
        .export-excel {
            background: #28a745;
            color: white;
        }
        
        .export-csv {
            background: #17a2b8;
            color: white;
        }
    </style>
</head>
<body>
    <nav class="navbar">
        <div class="logo">📚 BookShop Billing</div>
        <div class="nav-links">
            <a href="${pageContext.request.contextPath}/controller/dashboard">Dashboard</a>
            <a href="${pageContext.request.contextPath}/controller/books">Books</a>
            <a href="${pageContext.request.contextPath}/controller/billing">Billing</a>
            <a href="${pageContext.request.contextPath}/controller/reports">Reports</a>
        </div>
    </nav>
    
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