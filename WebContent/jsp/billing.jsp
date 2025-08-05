<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
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
        <div class="header">
            <h1>🛒 Create New Bill</h1>
            <p>Generate bills for customers with multiple books and payment options</p>
        </div>
        
        <c:if test="${not empty error}">
            <div class="error-message">
                <strong>Error:</strong> ${error}
            </div>
        </c:if>
        
        <c:if test="${not empty success}">
            <div class="success-message">
                <strong>Success:</strong> ${success}
            </div>
        </c:if>
        
        <div class="billing-container">
            <!-- Customer Selection Section -->
            <div class="main-content">
                <!-- Customer Search/Creation -->
                <div class="customer-section">
                    <h3>👤 Customer Information</h3>
                    <div class="customer-search">
                        <div class="form-group">
                            <label for="customerSearch">Search Customer</label>
                            <div class="search-input-group">
                                <input type="text" id="customerSearch" placeholder="Search by name, phone, or email...">
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
                                <div class="form-row">
                                    <div class="form-group">
                                        <label for="newCustomerName">Full Name *</label>
                                        <input type="text" id="newCustomerName" name="fullName" required>
                                    </div>
                                    <div class="form-group">
                                        <label for="newCustomerEmail">Email *</label>
                                        <input type="email" id="newCustomerEmail" name="email" required>
                                    </div>
                                </div>
                                <div class="form-row">
                                    <div class="form-group">
                                        <label for="newCustomerPhone">Phone *</label>
                                        <input type="tel" id="newCustomerPhone" name="phone" required>
                                    </div>
                                    <div class="form-group">
                                        <label for="newCustomerAddress">Address</label>
                                        <input type="text" id="newCustomerAddress" name="address">
                                    </div>
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
                    <h3>📚 Add Books to Bill</h3>
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
                                <c:choose>
                                    <c:when test="${not empty books}">
                                        <c:forEach var="book" items="${books}">
                                            <div class="book-item" data-book-id="${book.id}" data-book-title="${book.title}" data-book-price="${book.price}" data-book-stock="${book.quantity}">
                                                <div class="book-info">
                                                    <div class="book-title">${book.title}</div>
                                                    <div class="book-details">By ${book.author} | ISBN: ${book.isbn} | Price: LKR ${String.format("%.2f", book.price)} | Stock: ${book.quantity}</div>
                                                </div>
                                                <button type="button" class="btn btn-primary" onclick="addToCart(${book.id}, '${book.title}', ${book.price}, ${book.quantity})">Add to Cart</button>
                                            </div>
                                        </c:forEach>
                                    </c:when>
                                    <c:otherwise>
                                        <div class="no-books">
                                            <p>No books available. Please add some books first.</p>
                                        </div>
                                    </c:otherwise>
                                </c:choose>
                            </div>
                        </div>
                    </div>
                </div>
                
                <!-- Cart Items -->
                <div class="cart-section">
                    <h3>🛒 Cart Items</h3>
                    <div id="cartItems" class="cart-items">
                        <div class="no-items">
                            <p>No items in cart. Search and add books above.</p>
                        </div>
                    </div>
                </div>
            </div>
            
            <!-- Bill Summary Sidebar -->
            <div class="sidebar">
                <h3 class="section-title">💰 Bill Summary</h3>
                
                <div class="bill-summary">
                    <div class="summary-row">
                        <span>Subtotal:</span>
                        <span id="subtotal">LKR 0.00</span>
                    </div>
                    <div class="summary-row">
                        <span>Discount:</span>
                        <span id="discount">-LKR 0.00</span>
                    </div>
                    <div class="summary-row">
                        <span>Tax (10%):</span>
                        <span id="tax">LKR 0.00</span>
                    </div>
                    <div class="summary-row">
                        <span>Delivery:</span>
                        <span id="delivery">LKR 0.00</span>
                    </div>
                    <div class="summary-row total">
                        <span>Total:</span>
                        <span id="total">LKR 0.00</span>
                    </div>
                </div>
                
                <!-- Delivery Options -->
                <div class="delivery-section">
                    <h4>🚚 Delivery Options</h4>
                    <div class="form-group">
                        <label>
                            <input type="checkbox" id="isDelivery" onchange="updateDelivery()">
                            Home Delivery (+LKR 5.00)
                        </label>
                    </div>
                    <div id="deliveryAddressGroup" style="display: none;">
                        <div class="form-group">
                            <label for="deliveryAddress">Delivery Address</label>
                            <textarea id="deliveryAddress" rows="2" placeholder="Enter delivery address"></textarea>
                        </div>
                    </div>
                </div>
                
                <!-- Payment Method -->
                <div class="payment-section">
                    <h4>💳 Payment Method</h4>
                    <div class="payment-methods">
                        <label class="payment-method">
                            <input type="radio" name="paymentMethod" value="CASH" checked>
                            <div>💵 Cash</div>
                        </label>
                        <label class="payment-method">
                            <input type="radio" name="paymentMethod" value="CARD">
                            <div>💳 Card</div>
                        </label>
                        <label class="payment-method">
                            <input type="radio" name="paymentMethod" value="UPI">
                            <div>📱 UPI</div>
                        </label>
                    </div>
                </div>
                
                <!-- Generate Bill Button -->
                <button id="generateBillBtn" class="generate-bill-btn" onclick="generateBill()" disabled>
                    🧾 Generate Bill
                </button>
            </div>
        </div>
    </div>
    
    <script>
        let selectedCustomer = null;
        let cartItems = [];
        let allBooks = [];
        
        // Initialize books data from server
        <c:if test="${not empty books}">
            allBooks = [
                <c:forEach var="book" items="${books}" varStatus="status">
                    {
                        id: ${book.id},
                        title: '${book.title}'.replace(/'/g, "\\'"),
                        author: '${book.author}'.replace(/'/g, "\\'"),
                        isbn: '${book.isbn}',
                        price: ${book.price},
                        stock: ${book.quantity}
                    }<c:if test="${!status.last}">,</c:if>
                </c:forEach>
            ];
        </c:if>
        
        // Customer Functions
        function searchCustomer() {
            const searchTerm = document.getElementById('customerSearch').value.trim();
            if (!searchTerm) {
                showNotification('Please enter a search term', 'warning');
                return;
            }
            
            fetch('${pageContext.request.contextPath}/controller/search-customers?term=' + encodeURIComponent(searchTerm))
                .then(response => response.json())
                .then(data => {
                    displayCustomerResults(data);
                })
                .catch(error => {
                    console.error('Error searching customers:', error);
                    showNotification('Error searching customers', 'error');
                });
        }
        
        function displayCustomerResults(customers) {
            const resultsDiv = document.getElementById('customerResultsList');
            const resultsContainer = document.getElementById('customerSearchResults');
            
            if (customers.length === 0) {
                resultsDiv.innerHTML = '<p class="no-results">No customers found. Create a new customer.</p>';
            } else {
                let html = '';
                customers.forEach(customer => {
                    html += `
                        <div class="customer-result" onclick="selectCustomer(${customer.id}, '${customer.fullName}', '${customer.phone}', '${customer.email}', '${customer.accountNumber}')">
                            <div class="customer-name">${customer.fullName}</div>
                            <div class="customer-details">${customer.phone} | ${customer.email} | ${customer.accountNumber}</div>
                        </div>
                    `;
                });
                resultsDiv.innerHTML = html;
            }
            
            resultsContainer.style.display = 'block';
        }
        
        function selectCustomer(id, name, phone, email, account) {
            selectedCustomer = {id, name, phone, email, account};
            
            document.getElementById('selectedCustomerName').textContent = name;
            document.getElementById('selectedCustomerPhone').textContent = phone;
            document.getElementById('selectedCustomerEmail').textContent = email;
            document.getElementById('selectedCustomerAccount').textContent = account;
            
            document.getElementById('selectedCustomer').style.display = 'block';
            document.getElementById('customerSearchResults').style.display = 'none';
            document.getElementById('newCustomerForm').style.display = 'none';
            
            updateGenerateBillButton();
            showNotification('Customer selected: ' + name, 'success');
        }
        
        function clearSelectedCustomer() {
            selectedCustomer = null;
            document.getElementById('selectedCustomer').style.display = 'none';
            document.getElementById('customerSearch').value = '';
            updateGenerateBillButton();
        }
        
        function showNewCustomerForm() {
            document.getElementById('newCustomerForm').style.display = 'block';
            document.getElementById('customerSearchResults').style.display = 'none';
        }
        
        function hideNewCustomerForm() {
            document.getElementById('newCustomerForm').style.display = 'none';
        }
        
        function createCustomer(event) {
            event.preventDefault();
            
            const formData = new FormData(event.target);
            const customerData = {
                fullName: formData.get('fullName'),
                email: formData.get('email'),
                phone: formData.get('phone'),
                address: formData.get('address') || ''
            };
            
            // Validate required fields
            if (!customerData.fullName || !customerData.email || !customerData.phone) {
                showNotification('Please fill in all required fields', 'error');
                return;
            }
            
            fetch('${pageContext.request.contextPath}/controller/create-customer', {
                method: 'POST',
                headers: {'Content-Type': 'application/json'},
                body: JSON.stringify(customerData)
            })
            .then(response => response.json())
            .then(data => {
                if (data.success) {
                    selectCustomer(data.customer.id, data.customer.fullName, data.customer.phone, data.customer.email, data.customer.accountNumber);
                    hideNewCustomerForm();
                    event.target.reset();
                    showNotification('Customer created successfully!', 'success');
                } else {
                    showNotification('Error creating customer: ' + data.message, 'error');
                }
            })
            .catch(error => {
                console.error('Error creating customer:', error);
                showNotification('Error creating customer', 'error');
            });
        }
        
        // Book Functions
        function searchBooks() {
            const searchTerm = document.getElementById('bookSearch').value.trim().toLowerCase();
            const filteredBooks = allBooks.filter(book => 
                book.title.toLowerCase().includes(searchTerm) ||
                book.author.toLowerCase().includes(searchTerm) ||
                book.isbn.toLowerCase().includes(searchTerm)
            );
            displayBookResults(filteredBooks);
        }
        
        function showAllBooks() {
            displayBookResults(allBooks);
        }
        
        function displayBookResults(books) {
            const resultsDiv = document.getElementById('bookResultsList');
            
            if (books.length === 0) {
                resultsDiv.innerHTML = '<div class="no-books"><p>No books found.</p></div>';
            } else {
                let html = '';
                books.forEach(book => {
                    const isOutOfStock = book.stock <= 0;
                    html += `
                        <div class="book-item ${isOutOfStock ? 'out-of-stock' : ''}" data-book-id="${book.id}">
                            <div class="book-info">
                                <div class="book-title">${book.title}</div>
                                <div class="book-details">By ${book.author} | ISBN: ${book.isbn} | Price: LKR ${book.price.toFixed(2)} | Stock: ${book.stock}</div>
                            </div>
                            <button type="button" class="btn ${isOutOfStock ? 'btn-disabled' : 'btn-primary'}" 
                                    onclick="addToCart(${book.id}, '${book.title.replace(/'/g, "\\'")}', ${book.price}, ${book.stock})"
                                    ${isOutOfStock ? 'disabled' : ''}>
                                ${isOutOfStock ? 'Out of Stock' : 'Add to Cart'}
                            </button>
                        </div>
                    `;
                });
                resultsDiv.innerHTML = html;
            }
        }
        
        // Cart Functions
        function addToCart(bookId, title, price, stock) {
            if (!selectedCustomer) {
                showNotification('Please select a customer first', 'warning');
                return;
            }
            
            if (stock <= 0) {
                showNotification('Book is out of stock', 'error');
                return;
            }
            
            const existingItem = cartItems.find(item => item.bookId === bookId);
            
            if (existingItem) {
                if (existingItem.quantity < stock) {
                    existingItem.quantity++;
                    existingItem.total = existingItem.quantity * existingItem.price;
                    showNotification('Quantity updated for: ' + title, 'success');
                } else {
                    showNotification('Cannot add more items. Stock limit reached.', 'warning');
                    return;
                }
            } else {
                cartItems.push({
                    bookId: bookId,
                    title: title,
                    price: price,
                    quantity: 1,
                    total: price,
                    stock: stock
                });
                showNotification('Added to cart: ' + title, 'success');
            }
            
            updateCartDisplay();
            updateBillSummary();
        }
        
        function updateCartQuantity(bookId, change) {
            const item = cartItems.find(item => item.bookId === bookId);
            if (item) {
                const newQuantity = item.quantity + change;
                if (newQuantity > 0 && newQuantity <= item.stock) {
                    item.quantity = newQuantity;
                    item.total = item.quantity * item.price;
                    updateCartDisplay();
                    updateBillSummary();
                } else if (newQuantity <= 0) {
                    removeFromCart(bookId);
                } else {
                    showNotification('Cannot add more items. Stock limit reached.', 'warning');
                }
            }
        }
        
        function removeFromCart(bookId) {
            const item = cartItems.find(item => item.bookId === bookId);
            if (item) {
                cartItems = cartItems.filter(item => item.bookId !== bookId);
                updateCartDisplay();
                updateBillSummary();
                showNotification('Removed from cart: ' + item.title, 'info');
            }
        }
        
        function updateCartDisplay() {
            const cartDiv = document.getElementById('cartItems');
            
            if (cartItems.length === 0) {
                cartDiv.innerHTML = '<div class="no-items"><p>No items in cart. Search and add books above.</p></div>';
            } else {
                let html = '';
                cartItems.forEach(item => {
                    html += `
                        <div class="cart-item">
                            <div class="cart-item-info">
                                <div class="cart-item-title">${item.title}</div>
                                <div class="cart-item-price">LKR ${item.price.toFixed(2)} each</div>
                            </div>
                            <div class="quantity-controls">
                                <button class="quantity-btn" onclick="updateCartQuantity(${item.bookId}, -1)">-</button>
                                <span class="quantity-display">${item.quantity}</span>
                                <button class="quantity-btn" onclick="updateCartQuantity(${item.bookId}, 1)">+</button>
                            </div>
                            <div class="cart-item-total">LKR ${item.total.toFixed(2)}</div>
                            <button class="remove-btn" onclick="removeFromCart(${item.bookId})">🗑️</button>
                        </div>
                    `;
                });
                cartDiv.innerHTML = html;
            }
            updateGenerateBillButton();
        }
        
        function updateBillSummary() {
            const subtotal = cartItems.reduce((sum, item) => sum + item.total, 0);
            const discount = subtotal > 100 ? subtotal * 0.05 : 0; // 5% discount for orders over LKR 100
            const tax = (subtotal - discount) * 0.10; // 10% tax
            const delivery = document.getElementById('isDelivery').checked ? 5.00 : 0.00;
            const total = subtotal - discount + tax + delivery;
            
            document.getElementById('subtotal').textContent = 'LKR ' + subtotal.toFixed(2);
            document.getElementById('discount').textContent = '-LKR ' + discount.toFixed(2);
            document.getElementById('tax').textContent = 'LKR ' + tax.toFixed(2);
            document.getElementById('delivery').textContent = 'LKR ' + delivery.toFixed(2);
            document.getElementById('total').textContent = 'LKR ' + total.toFixed(2);
        }
        
        function updateDelivery() {
            const isDelivery = document.getElementById('isDelivery').checked;
            const addressGroup = document.getElementById('deliveryAddressGroup');
            
            if (isDelivery) {
                addressGroup.style.display = 'block';
            } else {
                addressGroup.style.display = 'none';
                document.getElementById('deliveryAddress').value = '';
            }
            
            updateBillSummary();
        }
        
        function updateGenerateBillButton() {
            const btn = document.getElementById('generateBillBtn');
            btn.disabled = !selectedCustomer || cartItems.length === 0;
            
            if (btn.disabled) {
                btn.textContent = '🧾 Generate Bill';
                btn.style.opacity = '0.6';
            } else {
                btn.textContent = '🧾 Generate Bill';
                btn.style.opacity = '1';
            }
        }
        
        function generateBill() {
            if (!selectedCustomer) {
                showNotification('Please select a customer first', 'error');
                return;
            }
            
            if (cartItems.length === 0) {
                showNotification('Please add items to cart first', 'error');
                return;
            }
            
            const paymentMethod = document.querySelector('input[name="paymentMethod"]:checked').value;
            const isDelivery = document.getElementById('isDelivery').checked;
            const deliveryAddress = document.getElementById('deliveryAddress').value;
            
            if (isDelivery && !deliveryAddress.trim()) {
                showNotification('Please enter delivery address', 'error');
                return;
            }
            
            // Prepare bill data
            const billData = {
                customerId: selectedCustomer.id,
                items: cartItems.map(item => ({
                    bookId: item.bookId,
                    quantity: item.quantity,
                    price: item.price
                })),
                paymentMethod: paymentMethod,
                isDelivery: isDelivery,
                deliveryAddress: deliveryAddress
            };
            
            // Show loading state
            const btn = document.getElementById('generateBillBtn');
            btn.disabled = true;
            btn.textContent = '⏳ Generating...';
            
            // Submit bill to server
            fetch('${pageContext.request.contextPath}/controller/generate-bill', {
                method: 'POST',
                headers: {'Content-Type': 'application/json'},
                body: JSON.stringify(billData)
            })
            .then(response => response.json())
            .then(data => {
                if (data.success) {
                    showNotification('Bill generated successfully! Bill Number: ' + data.billNumber, 'success');
                    // Redirect to invoice page
                    setTimeout(() => {
                        window.location.href = '${pageContext.request.contextPath}/controller/invoice?billId=' + data.billId;
                    }, 1500);
                } else {
                    showNotification('Error generating bill: ' + data.message, 'error');
                    btn.disabled = false;
                    btn.textContent = '🧾 Generate Bill';
                }
            })
            .catch(error => {
                console.error('Error generating bill:', error);
                showNotification('Error generating bill. Please try again.', 'error');
                btn.disabled = false;
                btn.textContent = '🧾 Generate Bill';
            });
        }
        
        // Notification system
        function showNotification(message, type = 'info') {
            const notification = document.createElement('div');
            notification.className = `notification notification-${type}`;
            notification.innerHTML = `
                <span class="notification-icon">
                    ${type === 'success' ? '✅' : type === 'error' ? '❌' : type === 'warning' ? '⚠️' : 'ℹ️'}
                </span>
                <span class="notification-message">${message}</span>
            `;
            notification.style.cssText = `
                position: fixed;
                top: 20px;
                right: 20px;
                padding: 12px 20px;
                border-radius: 8px;
                color: white;
                font-weight: 500;
                z-index: 10000;
                animation: slideIn 0.3s ease;
                display: flex;
                align-items: center;
                gap: 8px;
                max-width: 400px;
                box-shadow: 0 4px 12px rgba(0,0,0,0.15);
            `;
            
            switch (type) {
                case 'success':
                    notification.style.backgroundColor = '#28a745';
                    break;
                case 'error':
                    notification.style.backgroundColor = '#dc3545';
                    break;
                case 'warning':
                    notification.style.backgroundColor = '#ffc107';
                    notification.style.color = '#212529';
                    break;
                default:
                    notification.style.backgroundColor = '#17a2b8';
            }
            
            document.body.appendChild(notification);
            
            setTimeout(() => {
                notification.style.animation = 'slideOut 0.3s ease';
                setTimeout(() => {
                    if (notification.parentNode) {
                        notification.parentNode.removeChild(notification);
                    }
                }, 300);
            }, 4000);
        }
        
        // Initialize page
        document.addEventListener('DOMContentLoaded', function() {
            updateGenerateBillButton();
            showAllBooks(); // Show all books initially
            
            // Add keyboard shortcuts
            document.addEventListener('keydown', function(e) {
                if (e.ctrlKey && e.key === 'Enter') {
                    if (!document.getElementById('generateBillBtn').disabled) {
                        generateBill();
                    }
                }
            });
        });
        
        // Add CSS animations
        const style = document.createElement('style');
        style.textContent = `
            @keyframes slideIn {
                from { transform: translateX(100%); opacity: 0; }
                to { transform: translateX(0); opacity: 1; }
            }
            @keyframes slideOut {
                from { transform: translateX(0); opacity: 1; }
                to { transform: translateX(100%); opacity: 0; }
            }
            
            .billing-container {
                display: grid;
                grid-template-columns: 1fr 350px;
                gap: 2rem;
                margin-top: 1rem;
            }
            
            .main-content {
                display: flex;
                flex-direction: column;
                gap: 1.5rem;
            }
            
            .sidebar {
                background: var(--color-bg-white);
                padding: 1.5rem;
                border-radius: 12px;
                box-shadow: 0 2px 10px var(--shadow-light);
                height: fit-content;
                position: sticky;
                top: 20px;
            }
            
            .customer-section, .book-section, .cart-section {
                background: var(--color-bg-white);
                padding: 1.5rem;
                border-radius: 12px;
                box-shadow: 0 2px 10px var(--shadow-light);
            }
            
            .search-input-group {
                display: flex;
                gap: 0.5rem;
                align-items: flex-end;
            }
            
            .search-input-group input {
                flex: 1;
            }
            
            .customer-result {
                padding: 1rem;
                border: 1px solid var(--color-border-light);
                border-radius: 8px;
                margin-bottom: 0.5rem;
                cursor: pointer;
                transition: all 0.3s ease;
            }
            
            .customer-result:hover {
                background: var(--color-bg-light-gray);
                border-color: var(--color-teal);
            }
            
            .customer-name {
                font-weight: 600;
                color: var(--color-text-primary);
                margin-bottom: 0.25rem;
            }
            
            .customer-details {
                font-size: 0.9rem;
                color: var(--color-text-secondary);
            }
            
            .selected-customer {
                background: var(--color-bg-success);
                padding: 1rem;
                border-radius: 8px;
                border: 1px solid var(--color-success);
            }
            
            .customer-info p {
                margin: 0.25rem 0;
                font-size: 0.9rem;
            }
            
            .book-item {
                display: flex;
                justify-content: space-between;
                align-items: center;
                padding: 1rem;
                border: 1px solid var(--color-border-light);
                border-radius: 8px;
                margin-bottom: 0.5rem;
                transition: all 0.3s ease;
            }
            
            .book-item:hover {
                background: var(--color-bg-light-gray);
                border-color: var(--color-teal);
            }
            
            .book-item.out-of-stock {
                opacity: 0.6;
                background: var(--color-bg-error);
            }
            
            .book-title {
                font-weight: 600;
                color: var(--color-text-primary);
                margin-bottom: 0.25rem;
            }
            
            .book-details {
                font-size: 0.9rem;
                color: var(--color-text-secondary);
            }
            
            .cart-item {
                display: flex;
                justify-content: space-between;
                align-items: center;
                padding: 1rem;
                border: 1px solid var(--color-border-light);
                border-radius: 8px;
                margin-bottom: 0.5rem;
                background: var(--color-bg-white);
            }
            
            .cart-item-info {
                flex: 1;
            }
            
            .cart-item-title {
                font-weight: 600;
                color: var(--color-text-primary);
                margin-bottom: 0.25rem;
            }
            
            .cart-item-price {
                font-size: 0.9rem;
                color: var(--color-text-secondary);
            }
            
            .quantity-controls {
                display: flex;
                align-items: center;
                gap: 0.5rem;
                margin: 0 1rem;
            }
            
            .quantity-btn {
                background: var(--color-teal);
                color: white;
                border: none;
                border-radius: 50%;
                width: 30px;
                height: 30px;
                cursor: pointer;
                font-weight: bold;
                transition: background 0.3s ease;
            }
            
            .quantity-btn:hover {
                background: var(--color-teal-dark);
            }
            
            .quantity-display {
                min-width: 30px;
                text-align: center;
                font-weight: 600;
            }
            
            .cart-item-total {
                font-weight: 600;
                color: var(--color-teal);
                margin-right: 1rem;
            }
            
            .remove-btn {
                background: var(--color-error);
                color: white;
                border: none;
                border-radius: 6px;
                padding: 0.5rem;
                cursor: pointer;
                transition: background 0.3s ease;
            }
            
            .remove-btn:hover {
                background: var(--color-error-dark);
            }
            
            .bill-summary {
                background: var(--color-bg-light-gray);
                padding: 1rem;
                border-radius: 8px;
                margin-bottom: 1.5rem;
            }
            
            .summary-row {
                display: flex;
                justify-content: space-between;
                margin-bottom: 0.5rem;
                font-size: 0.9rem;
            }
            
            .summary-row.total {
                border-top: 2px solid var(--color-teal);
                padding-top: 0.5rem;
                margin-top: 0.5rem;
                font-weight: 700;
                font-size: 1.1rem;
                color: var(--color-teal);
            }
            
            .payment-methods {
                display: grid;
                grid-template-columns: 1fr;
                gap: 0.5rem;
                margin-bottom: 1.5rem;
            }
            
            .payment-method {
                display: flex;
                align-items: center;
                padding: 0.75rem;
                border: 2px solid var(--color-border-light);
                border-radius: 8px;
                cursor: pointer;
                transition: all 0.3s ease;
            }
            
            .payment-method:hover {
                border-color: var(--color-teal);
                background: var(--color-bg-light-gray);
            }
            
            .payment-method input[type="radio"] {
                margin-right: 0.5rem;
            }
            
            .payment-method input[type="radio"]:checked + div {
                color: var(--color-teal);
                font-weight: 600;
            }
            
            .generate-bill-btn {
                width: 100%;
                padding: 1rem;
                background: var(--color-success);
                color: white;
                border: none;
                border-radius: 8px;
                font-size: 1rem;
                font-weight: 600;
                cursor: pointer;
                transition: all 0.3s ease;
            }
            
            .generate-bill-btn:hover:not(:disabled) {
                background: var(--color-success-dark);
                transform: translateY(-2px);
                box-shadow: 0 4px 12px rgba(76, 175, 80, 0.3);
            }
            
            .generate-bill-btn:disabled {
                background: var(--color-text-muted);
                cursor: not-allowed;
                transform: none;
                box-shadow: none;
            }
            
            .no-results {
                text-align: center;
                color: var(--color-text-muted);
                font-style: italic;
                padding: 1rem;
            }
            
            .btn-disabled {
                background: var(--color-text-muted) !important;
                cursor: not-allowed !important;
            }
            
            @media (max-width: 1024px) {
                .billing-container {
                    grid-template-columns: 1fr;
                    gap: 1rem;
                }
                
                .sidebar {
                    position: static;
                    order: -1;
                }
            }
            
            @media (max-width: 768px) {
                .search-input-group {
                    flex-direction: column;
                    gap: 0.5rem;
                }
                
                .search-input-group input {
                    width: 100%;
                }
                
                .cart-item {
                    flex-direction: column;
                    gap: 1rem;
                    align-items: stretch;
                }
                
                .quantity-controls {
                    justify-content: center;
                    margin: 0;
                }
            }
        `;
        document.head.appendChild(style);
    </script>
</body>
</html>