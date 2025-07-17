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
        <div class="page-header">
            <div>
                <h1 class="page-header-title">Books Management</h1>
            </div>
            <div class="page-header-actions">
                <a href="${pageContext.request.contextPath}/controller/add-book" class="btn btn-primary">+ Add New Book</a>
            </div>
        </div>
        <form method="get" action="${pageContext.request.contextPath}/controller/books" class="filter-bar">
            <div class="filter-fields">
                <div class="form-group">
                    <label for="search">Search Books</label>
                    <input type="text" id="search" name="search" value="${search}" placeholder="Search by title, author, or category" />
                </div>
            </div>
            <div class="filter-actions">
                <button type="submit" class="btn btn-primary" title="Search">🔍</button>
                <a href="${pageContext.request.contextPath}/controller/books" class="btn btn-secondary" title="Clear">✖️</a>
            </div>
        </form>
        <div class="report-header">
            <div class="report-title">All Books</div>
        </div>
        <table class="transaction-table">
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
                    <tr>
                        <td>${book.id}</td>
                        <td>${book.title}</td>
                        <td>${book.author}</td>
                        <td>${book.isbn}</td>
                        <td>$${String.format("%.2f", book.price)}</td>
                        <td class="${book.quantity <= 5 ? 'stock-low' : 'stock-ok'}">${book.quantity}</td>
                        <td>${book.category}</td>
                        <td class="action-buttons">
                            <a href="${pageContext.request.contextPath}/controller/edit-book?id=${book.id}" class="edit-btn" title="Edit" aria-label="Edit">✏️</a>
                            <a href="${pageContext.request.contextPath}/controller/delete-book?id=${book.id}" class="delete-btn" title="Delete" aria-label="Delete" onclick="return confirm('Are you sure you want to delete this book?');">🗑️</a>
                            <a href="${pageContext.request.contextPath}/controller/view-book?id=${book.id}" class="view-btn" title="View" aria-label="View">👁️</a>
                        </td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
        <div class="pagination">
            <c:if test="${totalPages > 1}">
                <!-- First page button -->
                <c:if test="${page > 1}">
                    <a href="${pageContext.request.contextPath}/controller/books?search=${search}&page=1" class="btn btn-secondary pagination-btn" title="First">⏮</a>
                </c:if>
                <!-- Previous page button -->
                <c:if test="${page > 1}">
                    <a href="${pageContext.request.contextPath}/controller/books?search=${search}&page=${page-1}" class="btn btn-secondary pagination-btn" title="Previous">⟨</a>
                </c:if>
                <!-- Page numbers -->
                <c:forEach var="i" begin="1" end="${totalPages}">
                    <c:if test="${i == page}">
                        <span class="btn btn-primary pagination-btn" style="pointer-events:none;">${i}</span>
                    </c:if>
                    <c:if test="${i != page}">
                        <a href="${pageContext.request.contextPath}/controller/books?search=${search}&page=${i}" class="btn btn-secondary pagination-btn">${i}</a>
                    </c:if>
                </c:forEach>
                <!-- Next page button -->
                <c:if test="${page < totalPages}">
                    <a href="${pageContext.request.contextPath}/controller/books?search=${search}&page=${page+1}" class="btn btn-secondary pagination-btn" title="Next">⟩</a>
                </c:if>
                <!-- Last page button -->
                <c:if test="${page < totalPages}">
                    <a href="${pageContext.request.contextPath}/controller/books?search=${search}&page=${totalPages}" class="btn btn-secondary pagination-btn" title="Last">⏭</a>
                </c:if>
            </c:if>
        </div>
    </div>
    
    <script>
        function editBook(bookId) {
            // TODO: Implement edit functionality
            alert('Edit book with ID: ' + bookId);
        }
        
        function deleteBook(bookId) {
            if (confirm('Are you sure you want to delete this book?')) {
                // TODO: Implement delete functionality
                alert('Delete book with ID: ' + bookId);
            }
        }
    </script>
</body>
</html> 