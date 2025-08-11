<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ include file="header.jspf" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <title>Online Billing System Pahana Edu - Create Bill</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <div class="container">
        <% if (request.getAttribute("error") != null) { %>
            <div class="error-message">
                <%= request.getAttribute("error") %>
            </div>
        <% } %>
        
        <% if (request.getAttribute("success") != null) { %>
            <div class="success-message">
                <%= request.getAttribute("success") %>
            </div>
        <% } %>
        
        <div class="billing-container">
            <!-- Customer Selection Section -->
            <div class="main-content">
                <h2 class="section-title">Create New Bill</h2>
                
                <!-- Customer Search/Creation -->
                <div class="customer-section">
                    <h3>Customer Information</h3>
                    <div class="customer-search">
                <div class="form-group">
                            <label for="customerSearch">Search Customer</label>
                            <div class="search-input-group">
                                <input type="text" id="customerSearch" placeholder="Search by name, phone, or account number">
                                <button type="button" class="btn btn-primary" onclick="searchCustomer()">🔍 Search</button>
                                <button type="button" class="btn btn-secondary" onclick="showNewCustomerForm()">➕ New Customer</button>
                            </div>
                        </div>
                        
                        <!-- Customer Search Results -->
                        <div id="customerSearchResults" class="search-results" style="display: none;">
                            <h4>Search Results:</h4>
                            <div id="customerResultsList"></div>
                </div>
                
                        <!-- New Customer Form -->
                        <div id="newCustomerForm" class="form-card" style="display: none;">
                            <h4>Create New Customer</h4>
                            <form id="customerForm" onsubmit="createCustomer(event)">
                                <div class="form-group">
                                    <label for="newCustomerName">Full Name *</label>
                                    <input type="text" id="newCustomerName" name="fullName" required>
                                </div>
                                <div class="form-group">
                                    <label for="newCustomerEmail">Email *</label>
                                    <input type="email" id="newCustomerEmail" name="email" required>
                                </div>
                                <div class="form-group">
                                    <label for="newCustomerPhone">Phone *</label>
                                    <input type="tel" id="newCustomerPhone" name="phone" required>
                                </div>
                <div class="form-group">
                                    <label for="newCustomerAddress">Address</label>
                                    <textarea id="newCustomerAddress" name="address" rows="2"></textarea>
                                </div>
                                <div class="form-actions">
                                    <button type="submit" class="btn btn-primary">Create Customer</button>
                                    <button type="button" class="btn btn-secondary" onclick="hideNewCustomerForm()">Cancel</button>
                                </div>
                            </form>
                        </div>
                        
                        <!-- Selected Customer Display -->
                        <div id="selectedCustomer" class="selected-customer" style="display: none;">
                            <h4>Selected Customer:</h4>
                            <div class="customer-info">
                                <p><strong>Name:</strong> <span id="selectedCustomerName"></span></p>
                                <p><strong>Phone:</strong> <span id="selectedCustomerPhone"></span></p>
                                <p><strong>Email:</strong> <span id="selectedCustomerEmail"></span></p>
                                <p><strong>Account:</strong> <span id="selectedCustomerAccount"></span></p>
                            </div>
                            <button type="button" class="btn btn-secondary" onclick="clearSelectedCustomer()">Change Customer</button>
                        </div>
                    </div>
                </div>
                
                <!-- Book Search and Selection -->
                <div class="book-section">
                    <h3>Add Books to Bill</h3>
                <div class="book-search">
                        <div class="form-group">
                            <label for="bookSearch">Search Books</label>
                            <div class="search-input-group">
                                <input type="text" id="bookSearch" placeholder="Search by title, author, or ISBN">
                                <button type="button" class="btn btn-primary" onclick="searchBooks()">🔍 Search</button>
                                <button type="button" class="btn btn-secondary" onclick="showAllBooks()">Show All</button>
                            </div>
                </div>
                
                        <!-- Book Search Results -->
                        <div id="bookSearchResults" class="search-results">
                            <h4>Available Books:</h4>
                            <div id="bookResultsList">
                                <!-- Initial books from server (will be replaced by dynamic search results) -->
                                <% if (request.getAttribute("books") != null && !((java.util.List)request.getAttribute("books")).isEmpty()) { %>
                                    <% for (model.Book book : (java.util.List<model.Book>)request.getAttribute("books")) { %>
                                        <div class="book-item" data-book-id="${book.id}" data-book-title="${book.title}" data-book-price="${book.price}" data-book-stock="${book.quantity}">
                                            <div class="book-info">
                                                <div class="book-title">${book.title}</div>
                                                <div class="book-details">By ${book.author} | ISBN: ${book.isbn} | Price: $${String.format("%.2f", book.price)} | Stock: ${book.quantity}</div>
                                            </div>
                                            <button type="button" class="btn btn-primary" onclick="addToCart(${book.id}, '${book.title}', ${book.price}, ${book.quantity})">Add to Cart</button>
                                        </div>
                                    <% } %>
                                <% } else { %>
                                    <div class="no-books">
                                        <p>No books available. Please add some books first.</p>
                                    </div>
                                <% } %>
                            </div>
                        </div>
                        </div>
                    </div>
                </div>
                
                <!-- Cart Items -->
                <div class="cart-section">
                    <h3>Cart Items</h3>
                    <div id="cartItems" class="cart-items">
                        <div class="no-items">
                            <p>No items in cart. Search and add books above.</p>
                        </div>
                    </div>
                </div>
            </div>
            
            <!-- Bill Summary Sidebar -->
            <div class="sidebar">
                <h3 class="section-title">Bill Summary</h3>
                
                <div class="bill-summary">
                    <div class="summary-row">
                        <span>Subtotal:</span>
                        <span id="subtotal">$0.00</span>
                    </div>
                    <div class="summary-row">
                        <span>Discount:</span>
                        <span id="discount">-$0.00</span>
                    </div>
                    <div class="summary-row">
                        <span>Tax (10%):</span>
                        <span id="tax">$0.00</span>
                    </div>
                    <div class="summary-row">
                        <span>Delivery:</span>
                        <span id="delivery">$0.00</span>
                    </div>
                    <div class="summary-row total">
                        <span>Total:</span>
                        <span id="total">$0.00</span>
                    </div>
                    
                    <!-- Payment Options -->
                    <div class="payment-section" style="margin-top: 20px;">
                        <h4>Payment Method:</h4>
                        <select id="paymentMethod" class="form-control" style="margin-bottom: 10px;">
                            <option value="CASH">Cash</option>
                            <option value="CARD">Card</option>
                            <option value="UPI">UPI</option>
                        </select>
                        
                        <button type="button" class="btn btn-success" onclick="generateBill()" style="width: 100%; padding: 12px; font-size: 16px; font-weight: bold;">
                            🧾 Generate Bill
                        </button>
                    </div>
                </div>
            </div>
        </div>
    </div>
    
    <script src="${pageContext.request.contextPath}/js/billing.js"></script>
</body>
</html>