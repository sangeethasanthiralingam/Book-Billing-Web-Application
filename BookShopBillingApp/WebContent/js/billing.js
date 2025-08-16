// billing.js

// Initialize interactive features when page loads
document.addEventListener('DOMContentLoaded', function() {
    initializeInteractiveFeatures();
});

function initializeInteractiveFeatures() {
    // Real-time customer search
    const customerSearchInput = document.getElementById('customerSearch');
    if (customerSearchInput) {
        let searchTimeout;
        customerSearchInput.addEventListener('input', function() {
            clearTimeout(searchTimeout);
            const searchTerm = this.value.trim();
            
            if (searchTerm.length >= 2) {
                searchTimeout = setTimeout(() => {
                    searchCustomer(true); // true = real-time search
                }, 500);
            } else if (searchTerm.length === 0) {
                hideCustomerSearchResults();
            }
        });
    }
    
    // Real-time book search
    const bookSearchInput = document.getElementById('bookSearch');
    if (bookSearchInput) {
        let searchTimeout;
        bookSearchInput.addEventListener('input', function() {
            clearTimeout(searchTimeout);
            const searchTerm = this.value.trim();
            
            if (searchTerm.length >= 2) {
                searchTimeout = setTimeout(() => {
                    searchBooks(true); // true = real-time search
                }, 500);
            } else if (searchTerm.length === 0) {
                showAllBooks(); // Show all books when search is cleared
            }
        });
    }
    
    // Add Enter key support for searches
    customerSearchInput?.addEventListener('keypress', function(e) {
        if (e.key === 'Enter') {
            e.preventDefault();
            searchCustomer();
        }
    });
    
    bookSearchInput?.addEventListener('keypress', function(e) {
        if (e.key === 'Enter') {
            e.preventDefault();
            searchBooks();
        }
    });
    
    // Initialize bill summary
    updateBillSummary();
    
    // Add loading animation styles
    addLoadingStyles();
}

function addLoadingStyles() {
    const style = document.createElement('style');
    style.textContent = `
        .loading {
            opacity: 0.6;
            pointer-events: none;
            position: relative;
        }
        .loading::after {
            content: '';
            position: absolute;
            top: 50%;
            left: 50%;
            width: 20px;
            height: 20px;
            margin: -10px 0 0 -10px;
            border: 2px solid #f3f3f3;
            border-top: 2px solid #3498db;
            border-radius: 50%;
            animation: spin 1s linear infinite;
            z-index: 1000;
        }
        @keyframes spin {
            0% { transform: rotate(0deg); }
            100% { transform: rotate(360deg); }
        }
        .fade-in {
            animation: fadeIn 0.3s ease-in;
        }
        @keyframes fadeIn {
            from { opacity: 0; transform: translateY(-10px); }
            to { opacity: 1; transform: translateY(0); }
        }
        .cart-item {
            transition: all 0.3s ease;
            border-left: 3px solid transparent;
        }
        .cart-item:hover {
            background-color: #f8f9fa;
            border-left-color: #007bff;
        }
        .book-item {
            transition: all 0.2s ease;
            border-radius: 8px;
            padding: 12px;
            margin-bottom: 10px;
            border: 1px solid #e9ecef;
        }
        .book-item:hover {
            box-shadow: 0 2px 8px rgba(0,123,255,0.15);
            transform: translateY(-1px);
        }
        .customer-item {
            transition: all 0.2s ease;
            border-radius: 6px;
            padding: 10px;
            margin-bottom: 8px;
            border: 1px solid #e9ecef;
        }
        .customer-item:hover {
            background-color: #f8f9fa;
            border-color: #007bff;
        }
        .btn {
            transition: all 0.2s ease;
        }
        .btn:hover {
            transform: translateY(-1px);
            box-shadow: 0 2px 4px rgba(0,0,0,0.1);
        }
        .success-message {
            background-color: #d4edda;
            border: 1px solid #c3e6cb;
            color: #155724;
            padding: 10px;
            border-radius: 5px;
            margin: 10px 0;
            animation: fadeIn 0.5s ease-in;
        }
        .error-message {
            background-color: #f8d7da;
            border: 1px solid #f5c6cb;
            color: #721c24;
            padding: 10px;
            border-radius: 5px;
            margin: 10px 0;
            animation: fadeIn 0.5s ease-in;
        }
    `;
    document.head.appendChild(style);
}

function showLoading(element) {
    if (element) {
        element.classList.add('loading');
    }
}

function hideLoading(element) {
    if (element) {
        element.classList.remove('loading');
    }
}

function showMessage(message, type = 'success') {
    const container = document.querySelector('.container');
    const messageDiv = document.createElement('div');
    messageDiv.className = `${type}-message fade-in`;
    messageDiv.textContent = message;
    
    container.insertBefore(messageDiv, container.firstChild);
    
    // Auto remove after 5 seconds
    setTimeout(() => {
        messageDiv.remove();
    }, 5000);
}

function searchCustomer(isRealTime = false) {
    const searchInput = document.getElementById('customerSearch').value;
    console.log('Searching for customers with term:', searchInput);
    
    fetch(`/bookshop-billing/controller/search-customers-billing?searchTerm=${encodeURIComponent(searchInput)}`)
        .then(response => {
            console.log('Response status:', response.status);
            console.log('Response headers:', response.headers);
            
            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }
            
            return response.text().then(text => {
                console.log('Raw response:', text);
                try {
                    return JSON.parse(text);
                } catch (e) {
                    console.error('JSON parse error:', e);
                    throw new Error('Invalid JSON response: ' + text);
                }
            });
        })
        .then(data => {
            console.log('Parsed data:', data);
            const resultsContainer = document.getElementById('customerResultsList');
            const searchResultsDiv = document.getElementById('customerSearchResults');
            resultsContainer.innerHTML = ''; // Clear previous results
            
            if (!data || data.length === 0) {
                resultsContainer.innerHTML = '<p>No customers found.</p>';
                searchResultsDiv.style.display = 'block'; // Show the search results container
                return;
            }
            
            // Show the search results container
            searchResultsDiv.style.display = 'block';

            data.forEach(customer => {
                const customerDiv = document.createElement('div');
                customerDiv.classList.add('customer-item');
                customerDiv.innerHTML = `<p>${customer.fullName} (${customer.phone})</p>`;
                customerDiv.dataset.customerId = customer.id; // Store customer ID for further actions

                // Optionally add a button or functionality to select the customer
                const selectButton = document.createElement('button');
                selectButton.textContent = 'Select';
                selectButton.onclick = function() {
                    // Handle selecting the customer (e.g., display customer details)
                    selectCustomer(customer.id);
                };
                customerDiv.appendChild(selectButton);
                resultsContainer.appendChild(customerDiv);
            });
        })
        .catch(error => {
            console.error('Fetch error:', error);
            const resultsContainer = document.getElementById('customerResultsList');
            resultsContainer.innerHTML = '<p style="color: red;">Error searching customers: ' + error.message + '</p>';
        });
}

function searchBooks(isRealTime = false) {
    const searchInput = document.getElementById('bookSearch').value;
    const resultsContainer = document.getElementById('bookResultsList');
    
    if (!isRealTime) {
        showLoading(resultsContainer);
    }
    
    fetch(`/bookshop-billing/controller/search-books-billing?searchTerm=${encodeURIComponent(searchInput)}`)
        .then(response => response.json())
        .then(data => {
            hideLoading(resultsContainer);
            displayBookResults(data);
            if (!isRealTime && data.length > 0) {
                showMessage(`Found ${data.length} book(s)`, 'success');
            }
        })
        .catch(error => {
            hideLoading(resultsContainer);
            console.error('Error:', error);
            showMessage('Error searching books', 'error');
        });
}

// Global variables to store selected customer and cart items
let selectedCustomerData = null;
let cartItems = [];

function selectCustomer(customerId) {
    console.log('Selecting customer with ID:', customerId);
    
    // Find customer data from the last search results
    fetch(`/bookshop-billing/controller/search-customers-billing?searchTerm=`)
        .then(response => response.json())
        .then(customers => {
            console.log('Available customers:', customers);
            const customer = customers.find(c => c.id == customerId); // Use == for type coercion
            if (customer) {
                selectedCustomerData = customer;
                console.log('Selected customer data:', selectedCustomerData);
                displaySelectedCustomer(customer);
                hideCustomerSearchResults();
            } else {
                console.error('Customer not found with ID:', customerId);
                showMessage('Customer not found', 'error');
            }
        })
        .catch(error => {
            console.error('Error fetching customer:', error);
            showMessage('Error selecting customer', 'error');
        });
}

function displaySelectedCustomer(customer) {
    // Hide search results and show selected customer
    document.getElementById('customerSearchResults').style.display = 'none';
    
    // Show selected customer info
    const selectedCustomerDiv = document.getElementById('selectedCustomer');
    document.getElementById('selectedCustomerName').textContent = customer.fullName;
    document.getElementById('selectedCustomerPhone').textContent = customer.phone;
    document.getElementById('selectedCustomerEmail').textContent = customer.email;
    document.getElementById('selectedCustomerAccount').textContent = customer.accountNumber;
    
    selectedCustomerDiv.style.display = 'block';
    selectedCustomerDiv.classList.add('fade-in');
    
    showMessage(`Selected customer: ${customer.fullName}`, 'success');
    
    // Clear search input
    document.getElementById('customerSearch').value = '';
}

function clearSelectedCustomer() {
    selectedCustomerData = null;
    document.getElementById('selectedCustomer').style.display = 'none';
    document.getElementById('customerSearch').value = '';
}

function showNewCustomerForm() {
    document.getElementById('newCustomerForm').style.display = 'block';
}

function hideNewCustomerForm() {
    document.getElementById('newCustomerForm').style.display = 'none';
    document.getElementById('customerForm').reset();
}

function hideCustomerSearchResults() {
    document.getElementById('customerSearchResults').style.display = 'none';
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
    
    fetch('/bookshop-billing/controller/create-customer', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(customerData)
    })
    .then(response => response.json())
    .then(data => {
        if (data.success) {
            // Select the newly created customer
            selectedCustomerData = data.customer;
            displaySelectedCustomer(data.customer);
            hideNewCustomerForm();
            alert('Customer created successfully!');
        } else {
            alert('Error creating customer: ' + data.message);
        }
    })
    .catch(error => {
        console.error('Error:', error);
        alert('Error creating customer');
    });
}

function addToCart(bookId, bookTitle, bookPrice, bookStock) {
    // Check if book is already in cart
    const existingItem = cartItems.find(item => item.id === bookId);
    
    if (existingItem) {
        // Increase quantity if stock allows
        if (existingItem.quantity < bookStock) {
            existingItem.quantity += 1;
            showMessage(`Updated ${bookTitle} quantity to ${existingItem.quantity}`, 'success');
        } else {
            showMessage('Cannot add more. Stock limit reached.', 'error');
            return;
        }
    } else {
        // Add new item to cart
        cartItems.push({
            id: bookId,
            title: bookTitle,
            price: parseFloat(bookPrice),
            quantity: 1,
            stock: bookStock
        });
        showMessage(`Added ${bookTitle} to cart`, 'success');
    }
    
    updateCartDisplay();
    updateBillSummary();
    
    // Animate the cart section
    const cartSection = document.querySelector('.cart-section');
    if (cartSection) {
        cartSection.classList.add('fade-in');
        setTimeout(() => cartSection.classList.remove('fade-in'), 300);
    }
}

function removeFromCart(bookId) {
    const item = cartItems.find(item => item.id === bookId);
    if (item) {
        cartItems = cartItems.filter(item => item.id !== bookId);
        showMessage(`Removed ${item.title} from cart`, 'success');
        updateCartDisplay();
        updateBillSummary();
    }
}

function updateCartQuantity(bookId, newQuantity) {
    const item = cartItems.find(item => item.id === bookId);
    if (item) {
        if (newQuantity <= 0) {
            removeFromCart(bookId);
        } else if (newQuantity <= item.stock) {
            item.quantity = newQuantity;
            updateCartDisplay();
            updateBillSummary();
        } else {
            showMessage('Quantity exceeds stock limit', 'error');
        }
    }
}

function updateCartDisplay() {
    const cartContainer = document.getElementById('cartItems');
    
    if (cartItems.length === 0) {
        cartContainer.innerHTML = '<div class="no-items"><p>No items in cart. Search and add books above.</p></div>';
        return;
    }
    
    let cartHTML = '';
    cartItems.forEach(item => {
        cartHTML += `
            <div class="cart-item">
                <div class="item-info">
                    <h4>${item.title}</h4>
                    <p>Price: $${item.price.toFixed(2)} each</p>
                </div>
                <div class="quantity-controls">
                    <button type="button" onclick="updateCartQuantity(${item.id}, ${item.quantity - 1})">-</button>
                    <span class="quantity">${item.quantity}</span>
                    <button type="button" onclick="updateCartQuantity(${item.id}, ${item.quantity + 1})">+</button>
                </div>
                <div class="item-total">
                    <p>Total: $${(item.price * item.quantity).toFixed(2)}</p>
                    <button type="button" class="remove-btn" onclick="removeFromCart(${item.id})">Remove</button>
                </div>
            </div>
        `;
    });
    
    cartContainer.innerHTML = cartHTML;
}

function updateBillSummary() {
    const subtotal = cartItems.reduce((sum, item) => sum + (item.price * item.quantity), 0);
    const discount = subtotal > 100 ? subtotal * 0.05 : 0;
    const tax = (subtotal - discount) * 0.10;
    const delivery = 0; // Set based on delivery option
    const total = subtotal - discount + tax + delivery;
    
    document.getElementById('subtotal').textContent = '$' + subtotal.toFixed(2);
    document.getElementById('discount').textContent = '-$' + discount.toFixed(2);
    document.getElementById('tax').textContent = '$' + tax.toFixed(2);
    document.getElementById('delivery').textContent = '$' + delivery.toFixed(2);
    document.getElementById('total').textContent = '$' + total.toFixed(2);
}

function showAllBooks() {
    fetch('/bookshop-billing/controller/search-books-billing?searchTerm=')
        .then(response => response.json())
        .then(books => {
            displayBookResults(books);
        })
        .catch(error => console.error('Error fetching books:', error));
}

function displayBookResults(books) {
    const resultsContainer = document.getElementById('bookResultsList');
    const searchContainer = document.getElementById('bookSearchResults');
    
    if (!resultsContainer) {
        console.error('Book results container not found!');
        return;
    }
    
    // Clear the dynamic results container
    resultsContainer.innerHTML = '';
    
    if (books.length === 0) {
        resultsContainer.innerHTML = '<p>No books found.</p>';
        return;
    }
    
    // Build HTML for search results
    let html = '';
    books.forEach(book => {
        html += `
            <div class="book-item">
                <div class="book-info">
                    <div class="book-title">${book.title}</div>
                    <div class="book-details">By ${book.author} | ISBN: ${book.isbn || 'N/A'} | Price: $${book.price.toFixed(2)} | Stock: ${book.quantity}</div>
                </div>
                <button type="button" class="btn btn-primary" onclick="addToCart(${book.id}, '${escapeHtml(book.title)}', ${book.price}, ${book.quantity})">Add to Cart</button>
            </div>
        `;
    });
    
    resultsContainer.innerHTML = html;
}

function escapeHtml(text) {
    const div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
}

function generateBill() {
    console.log('=== GENERATE BILL DEBUG ===');
    console.log('selectedCustomerData:', selectedCustomerData);
    console.log('cartItems:', cartItems);
    console.log('paymentMethod element:', document.getElementById('paymentMethod'));
    
    if (!selectedCustomerData) {
        console.log('ERROR: No customer selected');
        showMessage('Please select a customer first.', 'error');
        return;
    }
    
    if (cartItems.length === 0) {
        console.log('ERROR: No items in cart');
        showMessage('Please add items to cart first.', 'error');
        return;
    }
    
    const paymentMethod = document.getElementById('paymentMethod').value;
    console.log('paymentMethod value:', paymentMethod);
    
    if (!paymentMethod) {
        console.log('ERROR: No payment method selected');
        showMessage('Please select a payment method.', 'error');
        return;
    }
    
    const generateButton = document.querySelector('.btn-success');
    
    // Show loading state
    generateButton.disabled = true;
    generateButton.textContent = '🔄 Generating Bill...';
    showLoading(generateButton.parentElement);
    
    const billData = {
        customerId: String(selectedCustomerData.id),
        paymentMethod: paymentMethod,
        isDelivery: "false",
        deliveryAddress: "",
        items: cartItems.map(item => ({
            bookId: item.id,
            quantity: item.quantity,
            price: item.price
        }))
    };
    
    console.log('Sending bill data:', JSON.stringify(billData, null, 2));
    
    fetch('/bookshop-billing/controller/generate-bill', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(billData)
    })
    .then(response => response.json())
    .then(data => {
        // Restore button state
        generateButton.disabled = false;
        generateButton.textContent = '🧾 Generate Bill';
        hideLoading(generateButton.parentElement);
        
        if (data.success) {
            showMessage(`Bill generated successfully! Bill Number: ${data.billNumber}`, 'success');
            
            // Clear cart and reset form with animation
            cartItems = [];
            selectedCustomerData = null;
            updateCartDisplay();
            updateBillSummary();
            clearSelectedCustomer();
            
            // Auto-open invoice after 2 seconds
            setTimeout(() => {
                if (confirm('Would you like to view the invoice?')) {
                    window.open(`/bookshop-billing/controller/invoice?billId=${data.billId}`, '_blank');
                }
            }, 1000);
        } else {
            showMessage('Error generating bill: ' + data.message, 'error');
        }
    })
    .catch(error => {
        // Restore button state
        generateButton.disabled = false;
        generateButton.textContent = '🧾 Generate Bill';
        hideLoading(generateButton.parentElement);
        
        console.error('Error:', error);
        showMessage('Error generating bill: ' + error.message, 'error');
    });
}
