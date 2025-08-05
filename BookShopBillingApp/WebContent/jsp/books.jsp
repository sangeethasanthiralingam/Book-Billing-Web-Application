<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ include file="header.jspf" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Online Billing System Pahana Edu - Books Management</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    
    <div class="container">
        <c:if test="${not empty error}">
            <div class="error-message">${error}</div>
        </c:if>
        <div class="page-header" style="display: flex; justify-content: space-between; align-items: center;">
            <h1 class="section-title"><span class="section-icon">📚</span>Books Management</h1>
            <a href="${pageContext.request.contextPath}/controller/add-book" class="button btn-primary">+ Add New Book</a>
        </div>
        <form method="get" action="${pageContext.request.contextPath}/controller/books" class="filter-bar" style="display: flex; gap: 1em; align-items: flex-end;">
            <div class="form-group">
                <label for="search">Search Books</label>
                <input type="text" id="search" name="search" value="${search}" placeholder="Search by title, author, or category" class="input" />
            </div>
            <button type="submit" class="button btn-primary" title="Search">🔍</button>
            <a href="${pageContext.request.contextPath}/controller/books" class="button btn-secondary" title="Clear">✖️</a>
        </form>
        <div class="report-header">
            <div class="report-title section-title"><span class="section-icon">📚</span>All Books</div>
        </div>
        <table class="transaction-table" style="border-radius:8px; overflow:hidden;">
            <thead>
                <tr>
                    <th>ID</th>
                    <th>Title</th>
                    <th>Author</th>
                    <th>ISBN</th>
                    <th>Price</th>
                    <th>Stock</th>
                    <th>Category</th>
                    <th>Actions</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="book" items="${books}">
                    <tr style="transition: background 0.2s;">
                        <td>${book.id}</td>
                        <td>${book.title}</td>
                        <td>${book.author}</td>
                        <td>${book.isbn}</td>
                        <td>$${String.format("%.2f", book.price)}</td>
                        <td class="${book.quantity <= 5 ? 'text-danger' : 'text-success'}">${book.quantity}</td>
                        <td>${book.category}</td>
                        <td class="action-buttons">
                            <a href="${pageContext.request.contextPath}/controller/edit-book?id=${book.id}" class="button btn-secondary" title="Edit" aria-label="Edit">✏️</a>
                            <a href="${pageContext.request.contextPath}/controller/delete-book?id=${book.id}" class="button btn-danger" title="Delete" aria-label="Delete" onclick="return confirm('Are you sure you want to delete this book?');">🗑️</a>
                            <a href="${pageContext.request.contextPath}/controller/view-book?id=${book.id}" class="button btn-primary" title="View" aria-label="View">👁️</a>
                        </td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
        <div class="pagination">
            <c:if test="${totalPages > 1}">
                <c:if test="${page > 1}">
                    <a href="${pageContext.request.contextPath}/controller/books?search=${search}&page=1" class="button btn-secondary pagination-btn" title="First">⏮</a>
                </c:if>
                <c:if test="${page > 1}">
                    <a href="${pageContext.request.contextPath}/controller/books?search=${search}&page=${page-1}" class="button btn-secondary pagination-btn" title="Previous">⟨</a>
                </c:if>
                <c:forEach var="i" begin="1" end="${totalPages}">
                    <c:if test="${i == page}">
                        <span class="button btn-primary pagination-btn" style="pointer-events:none;">${i}</span>
                    </c:if>
                    <c:if test="${i != page}">
                        <a href="${pageContext.request.contextPath}/controller/books?search=${search}&page=${i}" class="button btn-secondary pagination-btn">${i}</a>
                    </c:if>
                </c:forEach>
                <c:if test="${page < totalPages}">
                    <a href="${pageContext.request.contextPath}/controller/books?search=${search}&page=${page+1}" class="button btn-secondary pagination-btn" title="Next">⟩</a>
                </c:if>
                <c:if test="${page < totalPages}">
                    <a href="${pageContext.request.contextPath}/controller/books?search=${search}&page=${totalPages}" class="button btn-secondary pagination-btn" title="Last">⏭</a>
                </c:if>
            </c:if>
        </div>
    </div>
    
    <script>
        function editBook(bookId) {
            // Redirect to edit page
            window.location.href = '${pageContext.request.contextPath}/controller/edit-book?id=' + bookId;
        }
        
        function deleteBook(bookId) {
            if (confirm('Are you sure you want to delete this book? This action cannot be undone.')) {
                // Make AJAX call to delete book
                fetch('${pageContext.request.contextPath}/controller/delete-book?id=' + bookId, {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                    }
                })
                .then(response => response.json())
                .then(data => {
                    if (data.success) {
                        showNotification('Book deleted successfully!', 'success');
                        // Reload page after a short delay
                        setTimeout(() => {
                            window.location.reload();
                        }, 1000);
                    } else {
                        showNotification('Failed to delete book: ' + data.message, 'error');
                    }
                })
                .catch(error => {
                    console.error('Error:', error);
                    showNotification('Error deleting book', 'error');
                });
            }
        }
        
        // Notification system
        function showNotification(message, type = 'info') {
            const notification = document.createElement('div');
            notification.className = `notification notification-${type}`;
            notification.textContent = message;
            notification.style.cssText = `
                position: fixed;
                top: 20px;
                right: 20px;
                padding: 12px 20px;
                border-radius: 5px;
                color: white;
                font-weight: bold;
                z-index: 10000;
                animation: slideIn 0.3s ease;
            `;
            
            if (type === 'success') {
                notification.style.backgroundColor = '#28a745';
            } else if (type === 'error') {
                notification.style.backgroundColor = '#dc3545';
            } else {
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
            }, 3000);
        }
        
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
        `;
        document.head.appendChild(style);
    </script>
</body>
</html> 