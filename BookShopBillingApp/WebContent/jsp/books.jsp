<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>BookShop Billing - Books Management</title>
    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }
        
        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            background: #f5f7fa;
        }
        
        .navbar {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            padding: 1rem 2rem;
            display: flex;
            justify-content: space-between;
            align-items: center;
            box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
        }
        
        .logo {
            font-size: 1.5rem;
            font-weight: bold;
        }
        
        .nav-links {
            display: flex;
            gap: 2rem;
        }
        
        .nav-links a {
            color: white;
            text-decoration: none;
            padding: 0.5rem 1rem;
            border-radius: 5px;
            transition: background-color 0.3s ease;
        }
        
        .nav-links a:hover {
            background-color: rgba(255, 255, 255, 0.1);
        }
        
        .container {
            max-width: 1200px;
            margin: 2rem auto;
            padding: 0 2rem;
        }
        
        .header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 2rem;
        }
        
        .add-book-btn {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            border: none;
            padding: 0.75rem 1.5rem;
            border-radius: 8px;
            cursor: pointer;
            font-weight: 500;
        }
        
        .books-table {
            background: white;
            border-radius: 10px;
            box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
            overflow: hidden;
        }
        
        .table-header {
            background: #f8f9fa;
            padding: 1rem;
            border-bottom: 1px solid #e1e5e9;
        }
        
        .table-row {
            display: grid;
            grid-template-columns: 1fr 1fr 1fr 1fr 1fr 1fr auto;
            gap: 1rem;
            padding: 1rem;
            border-bottom: 1px solid #e1e5e9;
            align-items: center;
        }
        
        .table-row:last-child {
            border-bottom: none;
        }
        
        .table-row:hover {
            background-color: #f8f9fa;
        }
        
        .action-buttons {
            display: flex;
            gap: 0.5rem;
        }
        
        .edit-btn, .delete-btn {
            padding: 0.5rem 1rem;
            border: none;
            border-radius: 5px;
            cursor: pointer;
            font-size: 0.9rem;
        }
        
        .edit-btn {
            background: #28a745;
            color: white;
        }
        
        .delete-btn {
            background: #dc3545;
            color: white;
        }
        
        .stock-low {
            color: #dc3545;
            font-weight: bold;
        }
        
        .stock-ok {
            color: #28a745;
        }
        
        .error-message {
            background: #fee;
            color: #c33;
            padding: 1rem;
            border-radius: 5px;
            margin-bottom: 1rem;
            border: 1px solid #fcc;
        }
        
        .no-books {
            text-align: center;
            padding: 2rem;
            color: #666;
        }
    </style>
</head>
<body>
    <nav class="navbar">
        <div class="logo">📚 BookShop Billing</div>
        <div class="nav-links">
            <a href="${pageContext.request.contextPath}/controller/dashboard">Dashboard</a>
            <a href="${pageContext.request.contextPath}/controller/books">Books</a>
            <a href="${pageContext.request.contextPath}/controller/billing">Billing</a>
            <a href="${pageContext.request.contextPath}/controller/reports">Reports</a>
        </div>
    </nav>
    
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