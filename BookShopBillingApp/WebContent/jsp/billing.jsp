<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <title>Online Billing System Pahana Edu - Create Bill</title>
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
        
        .billing-container {
            display: grid;
            grid-template-columns: 2fr 1fr;
            gap: 2rem;
        }
        
        .main-content, .sidebar {
            background: white;
            padding: 2rem;
            border-radius: 10px;
            box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
        }
        
        .section-title {
            margin-bottom: 1.5rem;
            color: #333;
            font-size: 1.5rem;
        }
        
        .form-group {
            margin-bottom: 1rem;
        }
        
        .form-group label {
            display: block;
            margin-bottom: 0.5rem;
            color: #333;
            font-weight: 500;
        }
        
        .form-group input, .form-group select {
            width: 100%;
            padding: 0.75rem;
            border: 2px solid #e1e5e9;
            border-radius: 8px;
            font-size: 1rem;
            transition: border-color 0.3s ease;
        }
        
        .form-group input:focus, .form-group select:focus {
            outline: none;
            border-color: #667eea;
        }
        
        .book-search {
            display: flex;
            gap: 1rem;
            margin-bottom: 2rem;
        }
        
        .book-search input {
            flex: 1;
        }
        
        .search-btn {
            background: #667eea;
            color: white;
            border: none;
            padding: 0.75rem 1.5rem;
            border-radius: 8px;
            cursor: pointer;
            font-weight: 500;
        }
        
        .book-list {
            max-height: 400px;
            overflow-y: auto;
            border: 1px solid #e1e5e9;
            border-radius: 8px;
        }
        
        .book-item {
            padding: 1rem;
            border-bottom: 1px solid #e1e5e9;
            cursor: pointer;
            transition: background-color 0.3s ease;
        }
        
        .book-item:hover {
            background-color: #f8f9fa;
        }
        
        .book-item:last-child {
            border-bottom: none;
        }
        
        .book-title {
            font-weight: 600;
            color: #333;
        }
        
        .book-details {
            color: #666;
            font-size: 0.9rem;
            margin-top: 0.25rem;
        }
        
        .cart-items {
            margin-bottom: 2rem;
        }
        
        .cart-item {
            display: flex;
            justify-content: space-between;
            align-items: center;
            padding: 1rem;
            border: 1px solid #e1e5e9;
            border-radius: 8px;
            margin-bottom: 1rem;
        }
        
        .cart-item-info {
            flex: 1;
        }
        
        .cart-item-title {
            font-weight: 600;
            color: #333;
        }
        
        .cart-item-price {
            color: #667eea;
            font-weight: 500;
        }
        
        .quantity-controls {
            display: flex;
            align-items: center;
            gap: 0.5rem;
        }
        
        .quantity-btn {
            background: #667eea;
            color: white;
            border: none;
            width: 30px;
            height: 30px;
            border-radius: 50%;
            cursor: pointer;
            font-weight: bold;
        }
        
        .remove-btn {
            background: #dc3545;
            color: white;
            border: none;
            padding: 0.5rem 1rem;
            border-radius: 5px;
            cursor: pointer;
            font-size: 0.9rem;
        }
        
        .error-message {
            background: #fee;
            color: #c33;
            padding: 1rem;
            border-radius: 5px;
            margin-bottom: 1rem;
            border: 1px solid #fcc;
        }
        
        .no-books {
            text-align: center;
            padding: 2rem;
            color: #666;
        }
        
        .bill-summary {
            background: #f8f9fa;
            padding: 1.5rem;
            border-radius: 8px;
            margin-bottom: 2rem;
        }
        
        .summary-row {
            display: flex;
            justify-content: space-between;
            margin-bottom: 0.5rem;
        }
        
        .summary-row.total {
            font-weight: bold;
            font-size: 1.2rem;
            color: #333;
            border-top: 2px solid #e1e5e9;
            padding-top: 1rem;
            margin-top: 1rem;
        }
        
        .payment-section {
            margin-bottom: 2rem;
        }
        
        .payment-methods {
            display: grid;
            grid-template-columns: repeat(3, 1fr);
            gap: 1rem;
            margin-bottom: 1rem;
        }
        
        .payment-method {
            padding: 1rem;
            border: 2px solid #e1e5e9;
            border-radius: 8px;
            text-align: center;
            cursor: pointer;
            transition: border-color 0.3s ease;
        }
        
        .payment-method.selected {
            border-color: #667eea;
            background-color: #f0f4ff;
        }
        
        .generate-bill-btn {
            width: 100%;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            border: none;
            padding: 1rem;
            border-radius: 8px;
            font-size: 1.1rem;
            font-weight: 600;
            cursor: pointer;
            transition: transform 0.2s ease;
        }
        
        .generate-bill-btn:hover {
            transform: translateY(-2px);
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
        <% if (request.getAttribute("error") != null) { %>
            <div class="error-message">
                <%= request.getAttribute("error") %>
            </div>
        <% } %>
        
        <div class="billing-container">
            <div class="main-content">
                <h2 class="section-title">Create New Bill</h2>
                
                <div class="form-group">
                    <label for="customerName">Customer Name</label>
                    <input type="text" id="customerName" name="customerName" placeholder="Enter customer name">
                </div>
                
                <div class="form-group">
                    <label for="customerPhone">Customer Phone</label>
                    <input type="tel" id="customerPhone" name="customerPhone" placeholder="Enter phone number">
                </div>
                
                <h3 class="section-title">Add Books</h3>
                
                <div class="book-search">
                    <input type="text" placeholder="Search books by title, author, or ISBN">
                    <button class="search-btn">Search</button>
                </div>
                
                <div class="book-list">
                    <% if (request.getAttribute("books") != null && !((java.util.List)request.getAttribute("books")).isEmpty()) { %>
                        <% for (model.Book book : (java.util.List<model.Book>)request.getAttribute("books")) { %>
                        <div class="book-item" onclick="addToCart(${book.id}, '${book.title}', ${book.price}, ${book.quantity})">
                            <div class="book-title">${book.title}</div>
                            <div class="book-details">By ${book.author} | ISBN: ${book.isbn} | Price: $${String.format("%.2f", book.price)} | Stock: ${book.quantity}</div>
                    </div>
                        <% } %>
                    <% } else { %>
                        <div class="no-books">
                            <p>No books available. Please add some books first.</p>
                    </div>
                    <% } %>
                </div>
                
                <h3 class="section-title">Cart Items</h3>
                <div class="cart-items">
                    <div class="cart-item">
                        <div class="cart-item-info">
                            <div class="cart-item-title">The Great Gatsby</div>
                            <div class="cart-item-price">$12.99</div>
                        </div>
                        <div class="quantity-controls">
                            <button class="quantity-btn">-</button>
                            <span>2</span>
                            <button class="quantity-btn">+</button>
                        </div>
                        <button class="remove-btn">Remove</button>
                    </div>
                </div>
            </div>
            
            <div class="sidebar">
                <h3 class="section-title">Bill Summary</h3>
                
                <div class="bill-summary">
                    <div class="summary-row">
                        <span>Subtotal:</span>
                        <span>$25.98</span>
                    </div>
                    <div class="summary-row">
                        <span>Discount:</span>
                        <span>-$2.60</span>
                    </div>
                    <div class="summary-row">
                        <span>Tax (10%):</span>
                        <span>$2.34</span>
                    </div>
                    <div class="summary-row total">
                        <span>Total:</span>
                        <span>$25.72</span>
                    </div>
                </div>
                
                <div class="payment-section">
                    <h4>Payment Method</h4>
                    <div class="payment-methods">
                        <div class="payment-method selected">
                            <div>💳</div>
                            <div>Card</div>
                        </div>
                        <div class="payment-method">
                            <div>💵</div>
                            <div>Cash</div>
                        </div>
                        <div class="payment-method">
                            <div>📱</div>
                            <div>UPI</div>
                        </div>
                    </div>
                </div>
                
                <button class="generate-bill-btn">Generate Bill</button>
            </div>
        </div>
    </div>
    
    <script>
        function addToCart(bookId, title, price, stock) {
            // TODO: Implement add to cart functionality
            alert('Added to cart: ' + title + ' - $' + price.toFixed(2));
        }
        
        function updateQuantity(bookId, change) {
            // TODO: Implement quantity update
            console.log('Update quantity for book ' + bookId + ' by ' + change);
        }
        
        function removeFromCart(bookId) {
            // TODO: Implement remove from cart
            console.log('Remove book ' + bookId + ' from cart');
        }
    </script>
</body>
</html> 