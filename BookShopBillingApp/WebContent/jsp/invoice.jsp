<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <title>Online Billing System Pahana Edu - Invoice</title>
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
            max-width: 800px;
            margin: 2rem auto;
            padding: 0 2rem;
        }
        
        .invoice {
            background: white;
            border-radius: 10px;
            box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
            overflow: hidden;
        }
        
        .invoice-header {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            padding: 2rem;
            text-align: center;
        }
        
        .invoice-title {
            font-size: 2rem;
            margin-bottom: 0.5rem;
        }
        
        .invoice-number {
            font-size: 1.2rem;
            opacity: 0.9;
        }
        
        .invoice-body {
            padding: 2rem;
        }
        
        .invoice-info {
            display: grid;
            grid-template-columns: 1fr 1fr;
            gap: 2rem;
            margin-bottom: 2rem;
        }
        
        .info-section h3 {
            color: #333;
            margin-bottom: 1rem;
            border-bottom: 2px solid #e1e5e9;
            padding-bottom: 0.5rem;
        }
        
        .info-row {
            display: flex;
            justify-content: space-between;
            margin-bottom: 0.5rem;
        }
        
        .info-label {
            font-weight: 500;
            color: #666;
        }
        
        .info-value {
            color: #333;
        }
        
        .items-table {
            width: 100%;
            border-collapse: collapse;
            margin-bottom: 2rem;
        }
        
        .items-table th {
            background: #f8f9fa;
            padding: 1rem;
            text-align: left;
            border-bottom: 2px solid #e1e5e9;
            color: #333;
            font-weight: 600;
        }
        
        .items-table td {
            padding: 1rem;
            border-bottom: 1px solid #e1e5e9;
        }
        
        .items-table tr:last-child td {
            border-bottom: none;
        }
        
        .total-section {
            background: #f8f9fa;
            padding: 1.5rem;
            border-radius: 8px;
        }
        
        .total-row {
            display: flex;
            justify-content: space-between;
            margin-bottom: 0.5rem;
        }
        
        .total-row.final {
            font-weight: bold;
            font-size: 1.2rem;
            color: #333;
            border-top: 2px solid #e1e5e9;
            padding-top: 1rem;
            margin-top: 1rem;
        }
        
        .actions {
            display: flex;
            gap: 1rem;
            justify-content: center;
            margin-top: 2rem;
        }
        
        .action-btn {
            padding: 0.75rem 1.5rem;
            border: none;
            border-radius: 8px;
            cursor: pointer;
            font-weight: 500;
            text-decoration: none;
            display: inline-block;
        }
        
        .print-btn {
            background: #28a745;
            color: white;
        }
        
        .download-btn {
            background: #007bff;
            color: white;
        }
        
        .back-btn {
            background: #6c757d;
            color: white;
        }
    </style>
</head>
<body>
    <nav class="navbar">
        <div class="logo">📚 Online Billing System Pahana Edu</div>
        <div class="nav-links">
            <a href="${pageContext.request.contextPath}/controller/dashboard">Dashboard</a>
            <a href="${pageContext.request.contextPath}/controller/books">Books</a>
            <a href="${pageContext.request.contextPath}/controller/billing">Billing</a>
            <a href="${pageContext.request.contextPath}/controller/reports">Reports</a>
        </div>
    </nav>
    
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