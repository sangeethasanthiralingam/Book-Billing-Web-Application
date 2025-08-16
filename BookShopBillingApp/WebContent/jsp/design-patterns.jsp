<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%
    // Simple authentication check
    if (session.getAttribute("user") == null) {
        response.sendRedirect(request.getContextPath() + "/jsp/login.jsp");
        return;
    }
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Design Patterns Demo - BookShop Billing</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <style>
        .pattern-section {
            background: white;
            margin: 20px 0;
            padding: 20px;
            border-radius: 8px;
            box-shadow: 0 2px 4px rgba(0,0,0,0.1);
        }
        .pattern-title {
            color: #2c3e50;
            border-bottom: 2px solid #3498db;
            padding-bottom: 10px;
            margin-bottom: 15px;
        }
        .demo-button {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            border: none;
            padding: 10px 20px;
            border-radius: 5px;
            cursor: pointer;
            margin: 5px;
            transition: transform 0.2s;
        }
        .demo-button:hover {
            transform: translateY(-2px);
        }
        .result-area {
            background: #f8f9fa;
            border: 1px solid #dee2e6;
            border-radius: 4px;
            padding: 15px;
            margin-top: 10px;
            font-family: monospace;
            white-space: pre-wrap;
            max-height: 300px;
            overflow-y: auto;
        }
        .pattern-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(400px, 1fr));
            gap: 20px;
        }
    </style>
</head>
<body>
    <div class="container">
        <h1>🎯 Design Patterns Demonstration</h1>
        <p>This page demonstrates all the design patterns implemented in the BookShop Billing System.</p>
        <div style="background: #e8f5e8; padding: 10px; border-radius: 5px; margin-bottom: 20px;">
            <strong>Note:</strong> You are logged in as <strong>${sessionScope.user}</strong>. 
            All pattern operations will be executed with your current session.
        </div>
        
        <div class="pattern-grid">
            <!-- Command Pattern -->
            <div class="pattern-section">
                <h2 class="pattern-title">🔧 Command Pattern</h2>
                <p>Encapsulates requests as objects, allowing for undo operations and command queuing.</p>
                <button class="demo-button" onclick="demoCommand('history')">View Command History</button>
                <button class="demo-button" onclick="demoCommand('undo')">Undo Last Command</button>
                <div id="command-result" class="result-area" style="display:none;"></div>
            </div>
            
            <!-- Observer Pattern -->
            <div class="pattern-section">
                <h2 class="pattern-title">👁️ Observer Pattern</h2>
                <p>Notifies multiple observers when order status changes occur.</p>
                <button class="demo-button" onclick="demoOrderState('process')">Process Order</button>
                <button class="demo-button" onclick="demoOrderState('complete')">Complete Order</button>
                <div id="observer-result" class="result-area" style="display:none;"></div>
            </div>
            
            <!-- State Pattern -->
            <div class="pattern-section">
                <h2 class="pattern-title">🔄 State Pattern</h2>
                <p>Manages order state transitions (Pending → Processing → Completed).</p>
                <input type="number" id="billId" placeholder="Enter Bill ID" style="padding: 8px; margin: 5px;">
                <button class="demo-button" onclick="demoOrderState('cancel')">Cancel Order</button>
                <div id="state-result" class="result-area" style="display:none;"></div>
            </div>
            
            <!-- Template Pattern -->
            <div class="pattern-section">
                <h2 class="pattern-title">📊 Template Pattern</h2>
                <p>Defines the skeleton of report generation algorithm.</p>
                <select id="reportType" style="padding: 8px; margin: 5px;">
                    <option value="SALES">Sales Report</option>
                    <option value="DAILY_SALES">Daily Sales</option>
                    <option value="MONTHLY_SALES">Monthly Sales</option>
                </select>
                <button class="demo-button" onclick="generateReport()">Generate Report</button>
                <div id="template-result" class="result-area" style="display:none;"></div>
            </div>
            
            <!-- Decorator Pattern -->
            <div class="pattern-section">
                <h2 class="pattern-title">🎨 Decorator Pattern</h2>
                <p>Enhances books with premium features and discounts dynamically.</p>
                <button class="demo-button" onclick="showDecoratorInfo()">Show Decorator Info</button>
                <div id="decorator-result" class="result-area" style="display:none;">
                    <strong>Decorator Pattern in Action:</strong>
                    • Premium books (>$50) get premium decoration with enhanced features
                    • Regular books get discount decoration (5% off)
                    • Applied automatically during billing process
                    • Enhances book pricing and presentation dynamically
                </div>
            </div>
            
            <!-- Visitor Pattern -->
            <div class="pattern-section">
                <h2 class="pattern-title">🚶 Visitor Pattern</h2>
                <p>Visits different book types to generate sales analytics.</p>
                <button class="demo-button" onclick="showVisitorInfo()">Show Visitor Info</button>
                <div id="visitor-result" class="result-area" style="display:none;">
                    <strong>Visitor Pattern in Action:</strong>
                    • SalesReportVisitor visits each book during billing
                    • Collects analytics data from regular and premium books
                    • Calculates revenue and sales statistics
                    • Generates comprehensive sales reports
                </div>
            </div>
        </div>
        
        <!-- Complete Demo -->
        <div class="pattern-section" style="margin-top: 30px;">
            <h2 class="pattern-title">🎯 Complete Pattern Demonstration</h2>
            <p>Run a comprehensive demonstration of all design patterns working together.</p>
            <button class="demo-button" onclick="runCompleteDemo()" style="font-size: 16px; padding: 15px 30px;">
                🚀 Run Complete Demo
            </button>
            <div id="complete-result" class="result-area" style="display:none;"></div>
        </div>
    </div>

    <script>
        function demoCommand(action) {
            fetch('${pageContext.request.contextPath}/controller/command-history?action=' + action)
                .then(response => {
                    if (!response.ok) {
                        throw new Error('HTTP ' + response.status);
                    }
                    return response.text();
                })
                .then(text => {
                    let data;
                    try {
                        data = JSON.parse(text);
                    } catch (e) {
                        throw new Error('Invalid JSON response - likely authentication required');
                    }
                    
                    const resultDiv = document.getElementById('command-result');
                    resultDiv.style.display = 'block';
                    
                    if (action === 'history') {
                        let result = 'COMMAND HISTORY:\n';
                        if (data.commands && data.commands.length > 0) {
                            data.commands.forEach((cmd, index) => {
                                result += `${index + 1}. ${cmd.type}: ${cmd.description}\n`;
                            });
                        } else {
                            result += 'No commands in history yet.\n';
                        }
                        resultDiv.textContent = result;
                    } else {
                        resultDiv.textContent = `UNDO RESULT: ${data.message}`;
                    }
                })
                .catch(error => {
                    document.getElementById('command-result').textContent = 'Error: ' + error.message;
                    document.getElementById('command-result').style.display = 'block';
                });
        }
        
        function demoOrderState(action) {
            const billId = document.getElementById('billId').value || '1';
            
            fetch(`${pageContext.request.contextPath}/controller/order-state?billId=${billId}&action=${action}`)
                .then(response => {
                    if (!response.ok) {
                        throw new Error('HTTP ' + response.status);
                    }
                    return response.text();
                })
                .then(text => {
                    let data;
                    try {
                        data = JSON.parse(text);
                    } catch (e) {
                        throw new Error('Invalid JSON response - likely authentication required');
                    }
                    
                    const resultDiv = action === 'process' || action === 'complete' ? 
                                    document.getElementById('observer-result') : 
                                    document.getElementById('state-result');
                    resultDiv.style.display = 'block';
                    
                    if (data.success) {
                        resultDiv.textContent = `ORDER STATE CHANGED:\nBill ID: ${billId}\nNew State: ${data.newState}\nAction: ${action.toUpperCase()}`;
                    } else {
                        resultDiv.textContent = `Error: ${data.message || 'Unknown error'}`;
                    }
                })
                .catch(error => {
                    const resultDiv = action === 'process' || action === 'complete' ? 
                                    document.getElementById('observer-result') : 
                                    document.getElementById('state-result');
                    resultDiv.textContent = 'Error: ' + error.message;
                    resultDiv.style.display = 'block';
                });
        }
        
        function generateReport() {
            const reportType = document.getElementById('reportType').value;
            
            fetch(`${pageContext.request.contextPath}/controller/generate-report?reportType=${reportType}`)
                .then(response => {
                    if (!response.ok) {
                        throw new Error('HTTP ' + response.status + ' - Authentication may be required');
                    }
                    return response.text();
                })
                .then(data => {
                    const resultDiv = document.getElementById('template-result');
                    resultDiv.style.display = 'block';
                    
                    if (data.includes('<html') || data.includes('<!DOCTYPE')) {
                        resultDiv.textContent = 'Error: Received HTML instead of report - likely authentication required';
                    } else {
                        resultDiv.textContent = data;
                    }
                })
                .catch(error => {
                    document.getElementById('template-result').textContent = 'Error: ' + error.message;
                    document.getElementById('template-result').style.display = 'block';
                });
        }
        
        function showDecoratorInfo() {
            const resultDiv = document.getElementById('decorator-result');
            resultDiv.style.display = resultDiv.style.display === 'none' ? 'block' : 'none';
        }
        
        function showVisitorInfo() {
            const resultDiv = document.getElementById('visitor-result');
            resultDiv.style.display = resultDiv.style.display === 'none' ? 'block' : 'none';
        }
        
        function runCompleteDemo() {
            const resultDiv = document.getElementById('complete-result');
            resultDiv.style.display = 'block';
            resultDiv.textContent = 'Running complete demonstration...\n';
            
            fetch('${pageContext.request.contextPath}/controller/pattern-demo')
                .then(response => response.text())
                .then(data => {
                    resultDiv.textContent = data;
                })
                .catch(error => {
                    resultDiv.textContent = 'Error running complete demo: ' + error.message;
                });
        }
    </script>
</body>
</html>