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
                </div>
                
                <!-- Delivery Options -->
                <div class="delivery-section">
                    <h4>Delivery Options</h4>
                    <div class="form-group">
                        <label>
                            <input type="checkbox" id="isDelivery" onchange="updateDelivery()">
                            Home Delivery
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
                    <h4>Payment Method</h4>
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
                    Generate Bill
                </button>
            </div>
        </div>
    </div>
    
    <script>
        let selectedCustomer = null;
        let cartItems = [];
        let allBooks = [];
        
        // Initialize books data
        <% if (request.getAttribute("books") != null) { %>
            allBooks = [
                <% for (model.Book book : (java.util.List<model.Book>)request.getAttribute("books")) { %>
                    {
                        id: ${book.id},
                        title: '${book.title}',
                        author: '${book.author}',
                        isbn: '${book.isbn}',
                        price: ${book.price},
                        stock: ${book.quantity}
                    },
                <% } %>
            ];
        <% } %>
        
        // Customer Functions
        function searchCustomer() {
            const searchTerm = document.getElementById('customerSearch').value.trim();
            if (!searchTerm) {
                alert('Please enter a search term');
                return;
            }
            
            // Simulate AJAX call - in real implementation, this would call the server
            fetch('${pageContext.request.contextPath}/controller/search-customers?term=' + encodeURIComponent(searchTerm))
                .then(response => response.json())
                .then(data => {
                    displayCustomerResults(data);
                })
                .catch(error => {
                    console.error('Error searching customers:', error);
                    // For demo, show some sample results
                    displayCustomerResults([
                        {id: 1, fullName: 'John Doe', phone: '+94 71 1234567', email: 'john@example.com', accountNumber: 'ACC-001'},
                        {id: 2, fullName: 'Jane Smith', phone: '+94 72 2345678', email: 'jane@example.com', accountNumber: 'ACC-002'}
                    ]);
                });
        }
        
        function displayCustomerResults(customers) {
            const resultsDiv = document.getElementById('customerResultsList');
            const resultsContainer = document.getElementById('customerSearchResults');
            
            if (customers.length === 0) {
                resultsDiv.innerHTML = '<p>No customers found. Create a new customer.</p>';
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
        }
        
        function clearSelectedCustomer() {
            selectedCustomer = null;
            document.getElementById('selectedCustomer').style.display = 'none';
            document.getElementById('customerSearch').value = '';
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
                address: formData.get('address')
            };
            
            // Simulate AJAX call to create customer
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
                } else {
                    alert('Error creating customer: ' + data.message);
                }
            })
            .catch(error => {
                console.error('Error creating customer:', error);
                // For demo, simulate success
                selectCustomer(999, customerData.fullName, customerData.phone, customerData.email, 'ACC-999');
                hideNewCustomerForm();
                event.target.reset();
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
                    html += `
                        <div class="book-item" data-book-id="${book.id}" data-book-title="${book.title}" data-book-price="${book.price}" data-book-stock="${book.stock}">
                            <div class="book-info">
                                <div class="book-title">${book.title}</div>
                                <div class="book-details">By ${book.author} | ISBN: ${book.isbn} | Price: $${book.price.toFixed(2)} | Stock: ${book.stock}</div>
                            </div>
                            <button type="button" class="btn btn-primary" onclick="addToCart(${book.id}, '${book.title}', ${book.price}, ${book.stock})">Add to Cart</button>
                        </div>
                    `;
                });
                resultsDiv.innerHTML = html;
            }
        }
        
        // Cart Functions
        function addToCart(bookId, title, price, stock) {
            if (!selectedCustomer) {
                alert('Please select a customer first');
                return;
            }
            
            const existingItem = cartItems.find(item => item.bookId === bookId);
            
            if (existingItem) {
                if (existingItem.quantity < stock) {
                    existingItem.quantity++;
                    existingItem.total = existingItem.quantity * existingItem.price;
                } else {
                    alert('Cannot add more items. Stock limit reached.');
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
                    alert('Cannot add more items. Stock limit reached.');
                }
            }
        }
        
        function removeFromCart(bookId) {
            cartItems = cartItems.filter(item => item.bookId !== bookId);
            updateCartDisplay();
            updateBillSummary();
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
                                <div class="cart-item-price">$${item.price.toFixed(2)} each</div>
                            </div>
                            <div class="quantity-controls">
                                <button class="quantity-btn" onclick="updateCartQuantity(${item.bookId}, -1)">-</button>
                                <span>${item.quantity}</span>
                                <button class="quantity-btn" onclick="updateCartQuantity(${item.bookId}, 1)">+</button>
                            </div>
                            <div class="cart-item-total">$${item.total.toFixed(2)}</div>
                            <button class="remove-btn" onclick="removeFromCart(${item.bookId})">Remove</button>
                        </div>
                    `;
                });
                cartDiv.innerHTML = html;
            }
        }
        
        function updateBillSummary() {
            const subtotal = cartItems.reduce((sum, item) => sum + item.total, 0);
            const discount = subtotal > 100 ? subtotal * 0.05 : 0; // 5% discount for orders over $100
            const tax = (subtotal - discount) * 0.10; // 10% tax
            const delivery = document.getElementById('isDelivery').checked ? 5.00 : 0.00;
            const total = subtotal - discount + tax + delivery;
            
            document.getElementById('subtotal').textContent = '$' + subtotal.toFixed(2);
            document.getElementById('discount').textContent = '-$' + discount.toFixed(2);
            document.getElementById('tax').textContent = '$' + tax.toFixed(2);
            document.getElementById('delivery').textContent = '$' + delivery.toFixed(2);
            document.getElementById('total').textContent = '$' + total.toFixed(2);
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
        }
        
        function generateBill() {
            if (!selectedCustomer) {
                alert('Please select a customer first');
                return;
            }
            
            if (cartItems.length === 0) {
                alert('Please add items to cart first');
                return;
            }
            
            const paymentMethod = document.querySelector('input[name="paymentMethod"]:checked').value;
            const isDelivery = document.getElementById('isDelivery').checked;
            const deliveryAddress = document.getElementById('deliveryAddress').value;
            
            if (isDelivery && !deliveryAddress.trim()) {
                alert('Please enter delivery address');
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
            
            // Submit bill to server
            fetch('${pageContext.request.contextPath}/controller/generate-bill', {
                method: 'POST',
                headers: {'Content-Type': 'application/json'},
                body: JSON.stringify(billData)
            })
            .then(response => response.json())
            .then(data => {
                if (data.success) {
                    alert('Bill generated successfully! Bill Number: ' + data.billNumber);
                    // Redirect to invoice page
                    window.location.href = '${pageContext.request.contextPath}/controller/invoice?id=' + data.billId;
                } else {
                    alert('Error generating bill: ' + data.message);
                }
            })
            .catch(error => {
                console.error('Error generating bill:', error);
                alert('Error generating bill. Please try again.');
            });
        }
        
        // Initialize page
        document.addEventListener('DOMContentLoaded', function() {
            updateGenerateBillButton();
        });
    </script>
</body>
</html>