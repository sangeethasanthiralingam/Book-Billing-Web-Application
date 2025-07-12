<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ include file="header.jspf" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <title>Online Billing System Pahana Edu - Reports</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <div class="container">
        <% if (request.getAttribute("error") != null) { %>
            <div class="error-message">
                <%= request.getAttribute("error") %>
            </div>
        <% } %>
        
        <% if (session.getAttribute("userRole") != null && session.getAttribute("userRole").equals("CASHIER")) { %>
        <div class="header">
            <h1>Cashier Reports & Performance</h1>
        </div>
        <div class="reports-grid">
            <div class="report-card">
                <div class="report-header">
                    <div class="report-title">Today's Sales</div>
                </div>
                <div class="report-body">
                    <div class="metric">
                        <span class="metric-label">Total Sales</span>
                        <span class="metric-value">$${totalSales != null ? String.format("%.2f", totalSales) : "0.00"}</span>
                    </div>
                    <div class="metric">
                        <span class="metric-label">Total Bills</span>
                        <span class="metric-value">${totalBills != null ? totalBills : 0}</span>
                    </div>
                </div>
            </div>
            <div class="report-card">
                <div class="report-header">
                    <div class="report-title">Recent Bills</div>
                </div>
                <div class="report-body">
                    <% if (request.getAttribute("recentBills") != null && !((java.util.List)request.getAttribute("recentBills")).isEmpty()) { %>
                        <table class="transaction-table">
                            <thead>
                                <tr>
                                    <th>Bill #</th>
                                    <th>Date</th>
                                    <th>Total</th>
                                    <th>Payment Method</th>
                                    <th>Status</th>
                                </tr>
                            </thead>
                            <tbody>
                                <% for (model.Bill bill : (java.util.List<model.Bill>)request.getAttribute("recentBills")) { %>
                                <tr>
                                    <td>${bill.billNumber}</td>
                                    <td>${bill.billDate}</td>
                                    <td>$${String.format("%.2f", bill.total)}</td>
                                    <td>${bill.paymentMethod}</td>
                                    <td>${bill.status}</td>
                                </tr>
                                <% } %>
                            </tbody>
                        </table>
                    <% } else { %>
                        <div class="no-data">
                            <p>No recent bills available</p>
                        </div>
                    <% } %>
                </div>
            </div>
            <div class="report-card">
                <div class="report-header">
                    <div class="report-title">Performance Chart</div>
                </div>
                <div class="report-body">
                    <canvas id="cashierPerformanceChart" height="200"></canvas>
                </div>
            </div>
        </div>
        <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
        <% 
            java.util.List<model.Bill> bills = (java.util.List<model.Bill>)request.getAttribute("recentBills");
            StringBuilder billLabels = new StringBuilder("[");
            StringBuilder billTotals = new StringBuilder("[");
            boolean hasBills = false;
            if (bills != null && !bills.isEmpty()) {
                hasBills = true;
                for (int i = 0; i < bills.size(); i++) {
                    model.Bill bill = bills.get(i);
                    billLabels.append('"').append(bill.getBillNumber()).append('"');
                    billTotals.append(bill.getTotal());
                    if (i < bills.size() - 1) {
                        billLabels.append(", ");
                        billTotals.append(", ");
                    }
                }
            }
            billLabels.append("]");
            billTotals.append("]");
        %>
        <script>
        var billLabels = <%= billLabels.toString() %>;
        var billTotals = <%= billTotals.toString() %>;
        var hasBills = <%= hasBills %>;
        if (hasBills) {
            const ctx = document.getElementById('cashierPerformanceChart').getContext('2d');
            new Chart(ctx, {
                type: 'bar',
                data: {
                    labels: billLabels,
                    datasets: [{
                        label: 'Bill Total',
                        data: billTotals,
                        backgroundColor: 'rgba(102, 126, 234, 0.7)'
                    }]
                },
                options: {
                    responsive: true,
                    plugins: {
                        legend: { display: false },
                        title: { display: true, text: 'Last 5 Bills' }
                    }
                }
            });
        }
        </script>
        <% } else { %>
        <div class="header">
            <h1>Reports & Analytics</h1>
        </div>
        
        <div class="filter-section">
            <h3 class="mb-1rem">Filter Reports</h3>
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
            <h3 class="mb-1rem">Export Reports</h3>
            <div class="export-buttons">
                <button class="export-btn export-pdf">📄 Export as PDF</button>
                <button class="export-btn export-excel">📊 Export to Excel</button>
                <button class="export-btn export-csv">📋 Export as CSV</button>
            </div>
        </div>
        <% } %>
    </div>
</body>
</html> 