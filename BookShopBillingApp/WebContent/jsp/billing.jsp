<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="header.jspf" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <title>Online Billing System Pahana Edu - Create Bill</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
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