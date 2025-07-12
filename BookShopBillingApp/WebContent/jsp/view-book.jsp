<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page import="model.Book" %>
<%@ include file="header.jspf" %>
<%
    Book book = (Book) request.getAttribute("book");
    if (book == null) {
        response.sendRedirect(request.getContextPath() + "/controller/books");
        return;
    }
    

%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${book.title != null ? book.title : 'Book Details'}</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<div class="container">
    <!-- Header Section -->
    <div class="header">
        <h1>📖 Book Details</h1>
        <p>Complete information about this book in our collection</p>
    </div>

    <!-- Main Book Information Card -->
    <div class="book-detail-card">
        <div class="book-header">
            <div class="book-title-section">
                <h2 class="book-title">${book.title != null ? book.title : 'Untitled'}</h2>
                <p class="book-author">by <span class="author-name">${book.author != null ? book.author : 'Unknown Author'}</span></p>
            </div>
            <div class="book-status">
                <c:choose>
                    <c:when test="${book.quantity > 10}">
                        <span class="status-badge status-available">In Stock</span>
                    </c:when>
                    <c:when test="${book.quantity > 0}">
                        <span class="status-badge status-low">Low Stock</span>
                    </c:when>
                    <c:otherwise>
                        <span class="status-badge status-out">Out of Stock</span>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>

        <div class="book-content">
            <div class="book-info-grid">
                <!-- Left Column - Book Details -->
                <div class="book-details-section">
                    <h3>📋 Book Information</h3>
                    <div class="info-list">
                        <div class="info-item">
                            <span class="info-label">Title</span>
                            <span class="info-value">${book.title != null ? book.title : 'Not available'}</span>
                        </div>
                        <div class="info-item">
                            <span class="info-label">Author</span>
                            <span class="info-value">${book.author != null ? book.author : 'Not available'}</span>
                        </div>
                        <div class="info-item">
                            <span class="info-label">ISBN</span>
                            <span class="info-value isbn">${book.isbn != null ? book.isbn : 'Not available'}</span>
                        </div>
                        <div class="info-item">
                            <span class="info-label">Category</span>
                            <span class="info-value category">
                                <c:choose>
                                    <c:when test="${not empty book.category}">
                                        <span class="category-badge">${book.category}</span>
                                    </c:when>
                                    <c:otherwise>
                                        <span class="text-muted">Not specified</span>
                                    </c:otherwise>
                                </c:choose>
                            </span>
                        </div>
                    </div>
                </div>

                <!-- Right Column - Pricing & Stock -->
                <div class="book-pricing-section">
                    <h3>💰 Pricing & Availability</h3>
                    <div class="pricing-card">
                        <div class="price-display">
                            <span class="currency">LKR</span>
                            <span class="price-amount">${book.price != null ? book.price : '0.00'}</span>
                        </div>
                        <div class="stock-info">
                            <div class="stock-item">
                                <span class="stock-label">Available Copies</span>
                                <span class="stock-quantity ${book.quantity > 10 ? 'stock-high' : book.quantity > 0 ? 'stock-low' : 'stock-out'}">
                                    ${book.quantity}
                                </span>
                            </div>
                            <div class="stock-indicator">
                                <c:choose>
                                    <c:when test="${book.quantity > 10}">
                                        <div class="stock-bar stock-bar-high"></div>
                                    </c:when>
                                    <c:when test="${book.quantity > 0}">
                                        <div class="stock-bar stock-bar-low"></div>
                                    </c:when>
                                    <c:otherwise>
                                        <div class="stock-bar stock-bar-out"></div>
                                    </c:otherwise>
                                </c:choose>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            <!-- Description Section (if available) -->
            <c:if test="${not empty book.publisher}">
                <div class="book-description-section">
                    <h3>📝 Publisher Information</h3>
                    <div class="description-content">
                        <p><strong>Publisher:</strong> ${book.publisher}</p>
                        <c:if test="${not empty book.publicationYear}">
                            <p><strong>Publication Year:</strong> ${book.publicationYear}</p>
                        </c:if>
                        <c:if test="${not empty book.language}">
                            <p><strong>Language:</strong> ${book.language}</p>
                        </c:if>
                    </div>
                </div>
            </c:if>
        </div>

        <!-- Action Buttons -->
        <div class="book-actions">
            <div class="action-buttons action-buttons-flex">
                <a href="${pageContext.request.contextPath}/controller/edit-book?id=${book.id}" 
                   class="btn btn-primary">
                    <span>✏️</span> Edit Book
                </a>
                <a href="${pageContext.request.contextPath}/controller/books" 
                   class="btn btn-secondary">
                    <span>📚</span> Back to Books
                </a>
                <c:if test="${book.quantity > 0}">
                    <a href="${pageContext.request.contextPath}/controller/billing?bookId=${book.id}" 
                       class="btn btn-success">
                        <span>🛒</span> Add to Bill
                    </a>
                </c:if>
                <button onclick="confirmDelete()" class="btn btn-danger">
                    <span>🗑️</span> Delete Book
                </button>
            </div>
        </div>
    </div>

    <!-- Additional Information Cards -->
    <div class="additional-info-grid">
        <!-- Quick Stats Card -->
        <div class="info-card">
            <h3>📊 Quick Stats</h3>
            <div class="stats-list">
                <div class="stat-item">
                    <span class="stat-icon">📅</span>
                    <div class="stat-content">
                        <span class="stat-label">Publication Year</span>
                        <span class="stat-value">${book.publicationYear != null ? book.publicationYear : 'Not available'}</span>
                    </div>
                </div>
                <div class="stat-item">
                    <span class="stat-icon">🌐</span>
                    <div class="stat-content">
                        <span class="stat-label">Language</span>
                        <span class="stat-value">${book.language != null ? book.language : 'Not available'}</span>
                    </div>
                </div>
            </div>
        </div>

        <!-- Stock Management Card -->
        <div class="info-card">
            <h3>📦 Stock Management</h3>
            <div class="stock-management">
                <c:choose>
                    <c:when test="${book.quantity > 10}">
                        <div class="stock-status stock-status-good">
                            <span class="status-icon">✅</span>
                            <div class="status-content">
                                <span class="status-title">Well Stocked</span>
                                <span class="status-desc">Sufficient inventory available</span>
                            </div>
                        </div>
                    </c:when>
                    <c:when test="${book.quantity > 0}">
                        <div class="stock-status stock-status-warning">
                            <span class="status-icon">⚠️</span>
                            <div class="status-content">
                                <span class="status-title">Low Stock</span>
                                <span class="status-desc">Consider reordering soon</span>
                            </div>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <div class="stock-status stock-status-critical">
                            <span class="status-icon">🚨</span>
                            <div class="status-content">
                                <span class="status-title">Out of Stock</span>
                                <span class="status-desc">Immediate reorder required</span>
                            </div>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </div>
</div>

<script>
function confirmDelete() {
    if (confirm('Are you sure you want to delete "${book.title}"? This action cannot be undone.')) {
        window.location.href = '${pageContext.request.contextPath}/controller/delete-book?id=${book.id}';
    }
}

// Add some interactive enhancements
document.addEventListener('DOMContentLoaded', function() {
    // Add hover effects to info items
    const infoItems = document.querySelectorAll('.info-item');
    infoItems.forEach(item => {
        item.addEventListener('mouseenter', function() {
            this.style.transform = 'translateX(5px)';
        });
        item.addEventListener('mouseleave', function() {
            this.style.transform = 'translateX(0)';
        });
    });

    // Add copy functionality to ISBN
    const isbnElement = document.querySelector('.isbn');
    if (isbnElement) {
        isbnElement.style.cursor = 'pointer';
        isbnElement.title = 'Click to copy ISBN';
        isbnElement.addEventListener('click', function() {
            navigator.clipboard.writeText(this.textContent).then(() => {
                const originalText = this.textContent;
                this.textContent = 'Copied!';
                this.style.color = 'var(--color-success)';
                setTimeout(() => {
                    this.textContent = originalText;
                    this.style.color = '';
                }, 2000);
            });
        });
    }
});
</script>
</body>
</html> 