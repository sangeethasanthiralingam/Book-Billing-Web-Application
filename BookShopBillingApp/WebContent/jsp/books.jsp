<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="header.jspf" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Online Billing System Pahana Edu - Books Management</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    
    <div class="container">
        <% if (request.getAttribute("error") != null) { %>
            <div class="error-message">
                <%= request.getAttribute("error") %>
            </div>
        <% } %>
        
        <div class="header">
            <h1>Books Management</h1>
            <button class="add-book-btn">+ Add New Book</button>
        </div>
        
        <div class="books-table">
            <div class="table-header">
                <div class="table-row">
                    <div>Title</div>
                    <div>Author</div>
                    <div>ISBN</div>
                    <div>Price</div>
                    <div>Stock</div>
                    <div>Category</div>
                    <div>Actions</div>
                </div>
            </div>
            
            <% if (request.getAttribute("books") != null && !((java.util.List)request.getAttribute("books")).isEmpty()) { %>
                <% for (model.Book book : (java.util.List<model.Book>)request.getAttribute("books")) { %>
            <div class="table-row">
                    <div>${book.title}</div>
                    <div>${book.author}</div>
                    <div>${book.isbn}</div>
                    <div>$${String.format("%.2f", book.price)}</div>
                    <div class="${book.quantity <= 5 ? 'stock-low' : 'stock-ok'}">${book.quantity}</div>
                    <div>${book.category}</div>
                <div class="action-buttons">
                        <button class="edit-btn" onclick="editBook(${book.id})">Edit</button>
                        <button class="delete-btn" onclick="deleteBook(${book.id})">Delete</button>
            </div>
                </div>
                <% } %>
            <% } else { %>
                <div class="no-books">
                    <p>No books found. Add some books to get started!</p>
                </div>
            <% } %>
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