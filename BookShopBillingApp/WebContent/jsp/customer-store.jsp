<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.functions" prefix="fn" %>
<%@ include file="header.jspf" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${systemName != null ? systemName : 'Pahana Edu'} - Book Store</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/store.css">
</head>
<body>
<div class="container">
    <!-- Store Header with Cart -->
    <div class="store-header">
        <div>
            <h1 class="store-title">📚 Pahana Edu Book Store</h1>
            <p class="store-subtitle">Discover your next favorite book</p>
        </div>
        <div class="cart-section">
            <button class="cart-toggle-btn" onclick="toggleCart()">
                🛒 Cart (<span id="cartCount">0</span>)
            </button>
        </div>
    </div>

    <!-- Search and Filter Bar -->
    <form method="get" action="${pageContext.request.contextPath}/controller/customer-store" class="store-search-bar">
        <input type="text" name="search" value="${param.search}" placeholder="Search books by title, author, or ISBN..." />
        <select name="category">
            <option value="">All Categories</option>
            <c:forEach var="cat" items="${categories}">
                <option value="${cat}" ${param.category == cat ? 'selected' : ''}>${cat}</option>
            </c:forEach>
        </select>
        <select name="priceRange">
            <option value="">All Prices</option>
            <option value="0-500" ${param.priceRange == '0-500' ? 'selected' : ''}>Under LKR 500</option>
            <option value="500-1000" ${param.priceRange == '500-1000' ? 'selected' : ''}>LKR 500 - 1000</option>
            <option value="1000-2000" ${param.priceRange == '1000-2000' ? 'selected' : ''}>LKR 1000 - 2000</option>
            <option value="2000+" ${param.priceRange == '2000+' ? 'selected' : ''}>Over LKR 2000</option>
        </select>
        <button type="submit">🔍 Search</button>
        <a href="${pageContext.request.contextPath}/controller/customer-store" class="btn btn-secondary">Clear</a>
    </form>

    <!-- Results Summary -->
    <c:if test="${not empty param.search || not empty param.category || not empty param.priceRange}">
        <div class="search-summary">
            <p>Found <strong>${books.size()}</strong> books matching your criteria</p>
        </div>
    </c:if>

    <!-- Book Grid -->
    <c:choose>
        <c:when test="${not empty books}">
            <div class="book-grid">
                <c:forEach var="book" items="${books}">
                    <div class="book-card" data-book-id="${book.id}">
                        <div class="book-image">
                            <c:choose>
                                <c:when test="${not empty book.coverImage}">
                                    <img src="${book.coverImage}" alt="${book.title}" class="book-cover" />
                                </c:when>
                                <c:otherwise>
                                    <div class="book-cover-placeholder">
                                        <span class="book-icon">📖</span>
                                    </div>
                                </c:otherwise>
                            </c:choose>
                            <c:if test="${book.quantity <= 5 && book.quantity > 0}">
                                <span class="stock-badge low-stock">Low Stock</span>
                            </c:if>
                            <c:if test="${book.quantity <= 0}">
                                <span class="stock-badge out-of-stock">Out of Stock</span>
                            </c:if>
                        </div>
                        
                        <div class="book-info">
                            <h3 class="book-title">${book.title}</h3>
                            <p class="book-author">by ${book.author}</p>
                            <c:if test="${not empty book.category}">
                                <span class="category-badge">${book.category}</span>
                            </c:if>
                            <div class="book-price">LKR ${String.format("%.2f", book.price)}</div>
                            <div class="book-stock">
                                <c:choose>
                                    <c:when test="${book.quantity > 10}">
                                        <span class="stock-status in-stock">✅ In Stock</span>
                                    </c:when>
                                    <c:when test="${book.quantity > 0}">
                                        <span class="stock-status low-stock">⚠️ Only ${book.quantity} left</span>
                                    </c:when>
                                    <c:otherwise>
                                        <span class="stock-status out-of-stock">❌ Out of Stock</span>
                                    </c:otherwise>
                                </c:choose>
                            </div>
                        </div>
                        
                        <div class="book-actions">
                            <button class="btn-view-details" onclick="viewBookDetails(${book.id})">
                                👁️ View Details
                            </button>
                            <c:choose>
                                <c:when test="${book.quantity > 0}">
                                    <button class="btn-add-cart" 
                                            onclick="addToCart(${book.id}, '${fn:escapeXml(book.title)}', ${book.price}, ${book.quantity})"
                                            data-book-id="${book.id}">
                                        🛒 Add to Cart
                                    </button>
                                </c:when>
                                <c:otherwise>
                                    <button class="btn-add-cart" disabled>
                                        📵 Out of Stock
                                    </button>
                                </c:otherwise>
                            </c:choose>
                        </div>
                    </div>
                </c:forEach>
            </div>
        </c:when>
        <c:otherwise>
            <div class="no-books">
                <div class="no-books-content">
                    <span class="no-books-icon">📚</span>
                    <h3>No Books Found</h3>
                    <p>We couldn't find any books matching your criteria.</p>
                    <a href="${pageContext.request.contextPath}/controller/customer-store" class="btn btn-primary">
                        View All Books
                    </a>
                </div>
            </div>
        </c:otherwise>
    </c:choose>

    <!-- Shopping Cart Sidebar -->
    <div id="cartSidebar" class="cart-sidebar">
        <div class="cart-header">
            <h3>🛒 Shopping Cart</h3>
            <button class="cart-close" onclick="toggleCart()">&times;</button>
        </div>
        
        <div id="cartItems" class="cart-items">
            <div class="empty-cart">
                <span class="empty-cart-icon">🛒</span>
                <p>Your cart is empty</p>
                <p class="empty-cart-subtitle">Add some books to get started!</p>
            </div>
        </div>
        
        <div id="cartSummary" class="cart-summary" style="display: none;">
            <div class="summary-row">
                <span>Subtotal:</span>
                <span id="cartSubtotal">LKR 0.00</span>
            </div>
            <div class="summary-row">
                <span>Tax (10%):</span>
                <span id="cartTax">LKR 0.00</span>
            </div>
            <div class="summary-row total">
                <span>Total:</span>
                <span id="cartTotal">LKR 0.00</span>
            </div>
        </div>
        
        <div id="cartActions" class="cart-actions" style="display: none;">
            <button class="btn-checkout" onclick="proceedToCheckout()">
                💳 Proceed to Checkout
            </button>
            <button class="btn-clear-cart" onclick="clearCart()">
                🗑️ Clear Cart
            </button>
        </div>
    </div>

    <!-- Book Details Modal -->
    <div id="bookModal" class="book-modal" style="display: none;">
        <div class="book-modal-content">
            <div class="modal-header">
                <h3 id="modalBookTitle">Book Details</h3>
                <button class="modal-close" onclick="closeBookModal()">&times;</button>
            </div>
            <div class="modal-body">
                <div id="modalBookDetails"></div>
            </div>
            <div class="modal-actions">
                <button id="modalAddToCart" class="btn btn-primary" onclick="addToCartFromModal()">
                    🛒 Add to Cart
                </button>
                <button class="btn btn-secondary" onclick="closeBookModal()">
                    Close
                </button>
            </div>
        </div>
    </div>

    <!-- Checkout Modal -->
    <div id="checkoutModal" class="checkout-modal" style="display: none;">
        <div class="checkout-modal-content">
            <div class="modal-header">
                <h3>💳 Checkout</h3>
                <button class="modal-close" onclick="closeCheckoutModal()">&times;</button>
            </div>
            <div class="modal-body">
                <div class="checkout-summary">
                    <h4>Order Summary</h4>
                    <div id="checkoutItems"></div>
                    <div class="checkout-totals">
                        <div class="total-row">
                            <span>Subtotal:</span>
                            <span id="checkoutSubtotal">LKR 0.00</span>
                        </div>
                        <div class="total-row">
                            <span>Tax (10%):</span>
                            <span id="checkoutTax">LKR 0.00</span>
                        </div>
                        <div class="total-row final">
                            <span>Total:</span>
                            <span id="checkoutTotal">LKR 0.00</span>
                        </div>
                    </div>
                </div>
                
                <div class="payment-section">
                    <h4>Payment Method</h4>
                    <div class="payment-options">
                        <label class="payment-option">
                            <input type="radio" name="paymentMethod" value="CASH" checked>
                            <span class="payment-icon">💵</span>
                            <span>Cash Payment</span>
                        </label>
                        <label class="payment-option">
                            <input type="radio" name="paymentMethod" value="CARD">
                            <span class="payment-icon">💳</span>
                            <span>Card Payment</span>
                        </label>
                        <label class="payment-option">
                            <input type="radio" name="paymentMethod" value="UPI">
                            <span class="payment-icon">📱</span>
                            <span>UPI Payment</span>
                        </label>
                    </div>
                </div>
            </div>
            <div class="modal-actions">
                <button class="btn-place-order" onclick="placeOrder()">
                    ✅ Place Order
                </button>
                <button class="btn btn-secondary" onclick="closeCheckoutModal()">
                    Cancel
                </button>
            </div>
        </div>
    </div>
</div>

<script>
// Shopping Cart Management
let cart = JSON.parse(localStorage.getItem('customerCart') || '[]');
let currentBookModal = null;

// Initialize page
document.addEventListener('DOMContentLoaded', function() {
    updateCartDisplay();
    updateCartCount();
});

// Cart Functions
function addToCart(bookId, title, price, stock) {
    const existingItem = cart.find(item => item.id === bookId);
    
    if (existingItem) {
        if (existingItem.quantity < stock) {
            existingItem.quantity++;
            showNotification(`Updated ${title} quantity to ${existingItem.quantity}`, 'success');
        } else {
            showNotification('Cannot add more. Stock limit reached.', 'warning');
            return;
        }
    } else {
        cart.push({
            id: bookId,
            title: title,
            price: parseFloat(price),
            quantity: 1,
            stock: stock
        });
        showNotification(`Added ${title} to cart`, 'success');
    }
    
    saveCart();
    updateCartDisplay();
    updateCartCount();
    
    // Animate the add to cart button
    const button = document.querySelector(`[data-book-id="${bookId}"]`);
    if (button) {
        button.style.transform = 'scale(0.95)';
        setTimeout(() => {
            button.style.transform = 'scale(1)';
        }, 150);
    }
}

function removeFromCart(bookId) {
    const item = cart.find(item => item.id === bookId);
    if (item) {
        cart = cart.filter(item => item.id !== bookId);
        showNotification(`Removed ${item.title} from cart`, 'info');
        saveCart();
        updateCartDisplay();
        updateCartCount();
    }
}

function updateCartQuantity(bookId, newQuantity) {
    const item = cart.find(item => item.id === bookId);
    if (item) {
        if (newQuantity <= 0) {
            removeFromCart(bookId);
        } else if (newQuantity <= item.stock) {
            item.quantity = newQuantity;
            saveCart();
            updateCartDisplay();
            updateCartCount();
        } else {
            showNotification('Quantity exceeds available stock', 'warning');
        }
    }
}

function clearCart() {
    if (confirm('Are you sure you want to clear your cart?')) {
        cart = [];
        saveCart();
        updateCartDisplay();
        updateCartCount();
        showNotification('Cart cleared', 'info');
    }
}

function saveCart() {
    localStorage.setItem('customerCart', JSON.stringify(cart));
}

function updateCartCount() {
    const totalItems = cart.reduce((sum, item) => sum + item.quantity, 0);
    document.getElementById('cartCount').textContent = totalItems;
}

function updateCartDisplay() {
    const cartItemsDiv = document.getElementById('cartItems');
    const cartSummaryDiv = document.getElementById('cartSummary');
    const cartActionsDiv = document.getElementById('cartActions');
    
    if (cart.length === 0) {
        cartItemsDiv.innerHTML = `
            <div class="empty-cart">
                <span class="empty-cart-icon">🛒</span>
                <p>Your cart is empty</p>
                <p class="empty-cart-subtitle">Add some books to get started!</p>
            </div>
        `;
        cartSummaryDiv.style.display = 'none';
        cartActionsDiv.style.display = 'none';
    } else {
        let cartHTML = '';
        let subtotal = 0;
        
        cart.forEach(item => {
            const itemTotal = item.price * item.quantity;
            subtotal += itemTotal;
            
            cartHTML += `
                <div class="cart-item">
                    <div class="cart-item-info">
                        <h4 class="cart-item-title">${item.title}</h4>
                        <p class="cart-item-price">LKR ${item.price.toFixed(2)} each</p>
                    </div>
                    <div class="cart-item-controls">
                        <div class="quantity-controls">
                            <button class="qty-btn" onclick="updateCartQuantity(${item.id}, ${item.quantity - 1})">-</button>
                            <span class="quantity">${item.quantity}</span>
                            <button class="qty-btn" onclick="updateCartQuantity(${item.id}, ${item.quantity + 1})">+</button>
                        </div>
                        <div class="cart-item-total">LKR ${itemTotal.toFixed(2)}</div>
                        <button class="remove-item-btn" onclick="removeFromCart(${item.id})">🗑️</button>
                    </div>
                </div>
            `;
        });
        
        cartItemsDiv.innerHTML = cartHTML;
        
        const tax = subtotal * 0.10;
        const total = subtotal + tax;
        
        document.getElementById('cartSubtotal').textContent = `LKR ${subtotal.toFixed(2)}`;
        document.getElementById('cartTax').textContent = `LKR ${tax.toFixed(2)}`;
        document.getElementById('cartTotal').textContent = `LKR ${total.toFixed(2)}`;
        
        cartSummaryDiv.style.display = 'block';
        cartActionsDiv.style.display = 'block';
    }
}

function toggleCart() {
    const cartSidebar = document.getElementById('cartSidebar');
    cartSidebar.classList.toggle('open');
}

// Book Details Modal
function viewBookDetails(bookId) {
    fetch(`${window.location.origin}${window.location.pathname.replace(/\/jsp\/.*/, '')}/controller/get-book-details?id=${bookId}`)
        .then(response => response.json())
        .then(book => {
            currentBookModal = book;
            displayBookModal(book);
        })
        .catch(error => {
            console.error('Error fetching book details:', error);
            showNotification('Error loading book details', 'error');
        });
}

function displayBookModal(book) {
    document.getElementById('modalBookTitle').textContent = book.title;
    
    const detailsHTML = `
        <div class="book-modal-details">
            <div class="book-modal-image">
                ${book.coverImage ? 
                    `<img src="${book.coverImage}" alt="${book.title}" />` : 
                    '<div class="book-cover-placeholder"><span class="book-icon">📖</span></div>'
                }
            </div>
            <div class="book-modal-info">
                <h4>${book.title}</h4>
                <p><strong>Author:</strong> ${book.author}</p>
                <p><strong>ISBN:</strong> ${book.isbn || 'Not available'}</p>
                <p><strong>Category:</strong> ${book.category || 'Not specified'}</p>
                <p><strong>Publisher:</strong> ${book.publisher || 'Not specified'}</p>
                <p><strong>Language:</strong> ${book.language || 'English'}</p>
                <p><strong>Price:</strong> LKR ${book.price.toFixed(2)}</p>
                <p><strong>Available:</strong> ${book.quantity} copies</p>
            </div>
        </div>
    `;
    
    document.getElementById('modalBookDetails').innerHTML = detailsHTML;
    
    const addToCartBtn = document.getElementById('modalAddToCart');
    if (book.quantity > 0) {
        addToCartBtn.disabled = false;
        addToCartBtn.onclick = () => addToCartFromModal();
    } else {
        addToCartBtn.disabled = true;
        addToCartBtn.textContent = '📵 Out of Stock';
    }
    
    document.getElementById('bookModal').style.display = 'flex';
}

function addToCartFromModal() {
    if (currentBookModal) {
        addToCart(currentBookModal.id, currentBookModal.title, currentBookModal.price, currentBookModal.quantity);
        closeBookModal();
    }
}

function closeBookModal() {
    document.getElementById('bookModal').style.display = 'none';
    currentBookModal = null;
}

// Checkout Process
function proceedToCheckout() {
    if (cart.length === 0) {
        showNotification('Your cart is empty', 'warning');
        return;
    }
    
    updateCheckoutModal();
    document.getElementById('checkoutModal').style.display = 'flex';
    toggleCart(); // Close cart sidebar
}

function updateCheckoutModal() {
    let itemsHTML = '';
    let subtotal = 0;
    
    cart.forEach(item => {
        const itemTotal = item.price * item.quantity;
        subtotal += itemTotal;
        
        itemsHTML += `
            <div class="checkout-item">
                <span class="checkout-item-name">${item.title}</span>
                <span class="checkout-item-qty">x${item.quantity}</span>
                <span class="checkout-item-total">LKR ${itemTotal.toFixed(2)}</span>
            </div>
        `;
    });
    
    document.getElementById('checkoutItems').innerHTML = itemsHTML;
    
    const tax = subtotal * 0.10;
    const total = subtotal + tax;
    
    document.getElementById('checkoutSubtotal').textContent = `LKR ${subtotal.toFixed(2)}`;
    document.getElementById('checkoutTax').textContent = `LKR ${tax.toFixed(2)}`;
    document.getElementById('checkoutTotal').textContent = `LKR ${total.toFixed(2)}`;
}

function placeOrder() {
    if (cart.length === 0) {
        showNotification('Your cart is empty', 'error');
        return;
    }
    
    const paymentMethod = document.querySelector('input[name="paymentMethod"]:checked').value;
    
    const orderData = {
        customerId: ${sessionScope.userId},
        items: cart.map(item => ({
            bookId: item.id,
            quantity: item.quantity,
            price: item.price
        })),
        paymentMethod: paymentMethod,
        isDelivery: false,
        deliveryAddress: ''
    };
    
    // Show loading state
    const placeOrderBtn = document.querySelector('.btn-place-order');
    placeOrderBtn.disabled = true;
    placeOrderBtn.textContent = '⏳ Processing Order...';
    
    fetch('${pageContext.request.contextPath}/controller/customer-place-order', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(orderData)
    })
    .then(response => response.json())
    .then(data => {
        if (data.success) {
            showNotification(`Order placed successfully! Order #${data.billNumber}`, 'success');
            
            // Clear cart
            cart = [];
            saveCart();
            updateCartDisplay();
            updateCartCount();
            
            closeCheckoutModal();
            
            // Redirect to order confirmation
            setTimeout(() => {
                window.location.href = `${pageContext.request.contextPath}/controller/customer-order-confirmation?billId=${data.billId}`;
            }, 2000);
        } else {
            showNotification('Error placing order: ' + data.message, 'error');
        }
    })
    .catch(error => {
        console.error('Error:', error);
        showNotification('Error placing order. Please try again.', 'error');
    })
    .finally(() => {
        placeOrderBtn.disabled = false;
        placeOrderBtn.textContent = '✅ Place Order';
    });
}

function closeCheckoutModal() {
    document.getElementById('checkoutModal').style.display = 'none';
}

// Notification System
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

// Add CSS animations and styles
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
    
    .cart-sidebar {
        position: fixed;
        top: 0;
        right: -400px;
        width: 400px;
        height: 100vh;
        background: white;
        box-shadow: -2px 0 10px rgba(0,0,0,0.1);
        z-index: 1000;
        transition: right 0.3s ease;
        display: flex;
        flex-direction: column;
    }
    
    .cart-sidebar.open {
        right: 0;
    }
    
    .cart-header {
        padding: 1rem;
        border-bottom: 1px solid #eee;
        display: flex;
        justify-content: space-between;
        align-items: center;
        background: var(--color-teal);
        color: white;
    }
    
    .cart-close {
        background: none;
        border: none;
        color: white;
        font-size: 1.5rem;
        cursor: pointer;
    }
    
    .cart-items {
        flex: 1;
        overflow-y: auto;
        padding: 1rem;
    }
    
    .cart-item {
        display: flex;
        justify-content: space-between;
        align-items: center;
        padding: 1rem;
        border: 1px solid #eee;
        border-radius: 8px;
        margin-bottom: 0.5rem;
    }
    
    .cart-item-controls {
        display: flex;
        align-items: center;
        gap: 1rem;
    }
    
    .quantity-controls {
        display: flex;
        align-items: center;
        gap: 0.5rem;
    }
    
    .qty-btn {
        background: var(--color-teal);
        color: white;
        border: none;
        border-radius: 50%;
        width: 25px;
        height: 25px;
        cursor: pointer;
        font-size: 0.8rem;
    }
    
    .cart-summary {
        padding: 1rem;
        border-top: 1px solid #eee;
        background: #f8f9fa;
    }
    
    .summary-row {
        display: flex;
        justify-content: space-between;
        margin-bottom: 0.5rem;
    }
    
    .summary-row.total {
        font-weight: bold;
        font-size: 1.1rem;
        border-top: 1px solid #ddd;
        padding-top: 0.5rem;
        margin-top: 0.5rem;
    }
    
    .cart-actions {
        padding: 1rem;
        display: flex;
        flex-direction: column;
        gap: 0.5rem;
    }
    
    .btn-checkout {
        background: var(--color-success);
        color: white;
        border: none;
        padding: 0.75rem;
        border-radius: 8px;
        font-weight: 600;
        cursor: pointer;
    }
    
    .btn-clear-cart {
        background: var(--color-error);
        color: white;
        border: none;
        padding: 0.5rem;
        border-radius: 8px;
        cursor: pointer;
    }
    
    .book-modal, .checkout-modal {
        position: fixed;
        top: 0;
        left: 0;
        width: 100vw;
        height: 100vh;
        background: rgba(0,0,0,0.5);
        display: flex;
        align-items: center;
        justify-content: center;
        z-index: 2000;
    }
    
    .book-modal-content, .checkout-modal-content {
        background: white;
        border-radius: 12px;
        max-width: 600px;
        width: 90%;
        max-height: 80vh;
        overflow-y: auto;
    }
    
    .modal-header {
        padding: 1rem;
        border-bottom: 1px solid #eee;
        display: flex;
        justify-content: space-between;
        align-items: center;
        background: var(--color-teal);
        color: white;
        border-radius: 12px 12px 0 0;
    }
    
    .modal-close {
        background: none;
        border: none;
        color: white;
        font-size: 1.5rem;
        cursor: pointer;
    }
    
    .modal-body {
        padding: 1.5rem;
    }
    
    .modal-actions {
        padding: 1rem;
        border-top: 1px solid #eee;
        display: flex;
        gap: 1rem;
        justify-content: flex-end;
    }
    
    .book-modal-details {
        display: grid;
        grid-template-columns: 150px 1fr;
        gap: 1.5rem;
    }
    
    .book-modal-image img, .book-cover-placeholder {
        width: 100%;
        height: 200px;
        object-fit: cover;
        border-radius: 8px;
        background: #f8f9fa;
        display: flex;
        align-items: center;
        justify-content: center;
    }
    
    .payment-options {
        display: flex;
        flex-direction: column;
        gap: 0.5rem;
    }
    
    .payment-option {
        display: flex;
        align-items: center;
        padding: 0.75rem;
        border: 2px solid #eee;
        border-radius: 8px;
        cursor: pointer;
        transition: all 0.3s ease;
    }
    
    .payment-option:hover {
        border-color: var(--color-teal);
        background: #f8f9fa;
    }
    
    .payment-option input[type="radio"] {
        margin-right: 0.5rem;
    }
    
    .payment-icon {
        margin-right: 0.5rem;
        font-size: 1.2rem;
    }
    
    .checkout-item {
        display: flex;
        justify-content: space-between;
        padding: 0.5rem 0;
        border-bottom: 1px solid #eee;
    }
    
    .checkout-item:last-child {
        border-bottom: none;
    }
    
    .btn-place-order {
        background: var(--color-success);
        color: white;
        border: none;
        padding: 0.75rem 1.5rem;
        border-radius: 8px;
        font-weight: 600;
        cursor: pointer;
        font-size: 1rem;
    }
    
    .search-summary {
        background: #f8f9fa;
        padding: 1rem;
        border-radius: 8px;
        margin-bottom: 1.5rem;
        text-align: center;
    }
    
    .empty-cart {
        text-align: center;
        padding: 2rem;
        color: #6c757d;
    }
    
    .empty-cart-icon {
        font-size: 3rem;
        display: block;
        margin-bottom: 1rem;
    }
    
    .empty-cart-subtitle {
        font-size: 0.9rem;
        margin-top: 0.5rem;
    }
    
    @media (max-width: 768px) {
        .cart-sidebar {
            width: 100vw;
            right: -100vw;
        }
        
        .book-modal-details {
            grid-template-columns: 1fr;
            text-align: center;
        }
        
        .book-modal-image {
            justify-self: center;
        }
    }
`;
document.head.appendChild(style);
</script>
</body>
</html>