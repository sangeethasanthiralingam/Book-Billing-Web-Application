<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ include file="header.jspf" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Add New Book - Book Billing System</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<div class="container">
    <div class="header">
        <h1>Add New Book</h1>
        <p>Enter the details for the new book to add to the inventory</p>
    </div>
    
    <div class="form-card form-card-centered">
        <div class="form-header">
            <h2>Book Information</h2>
            <p class="text-muted">Please fill in all required fields marked with *</p>
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
        
        <form method="post" class="book-form">
            <div class="form-group">
                <label for="title">Book Title *</label>
                <input type="text" id="title" name="title" class="form-control" 
                       placeholder="Enter book title" required />
                <div class="form-text">The complete title of the book</div>
            </div>
            
            <div class="form-group">
                <label for="author">Author *</label>
                <input type="text" id="author" name="author" class="form-control" 
                       placeholder="Enter author name" required />
                <div class="form-text">Full name of the book's author</div>
            </div>
            
            <div class="form-group">
                <label for="isbn">ISBN *</label>
                <input type="text" id="isbn" name="isbn" class="form-control" 
                       placeholder="Enter ISBN (e.g., 978-0-123456-47-2)" required />
                <div class="form-text">International Standard Book Number</div>
            </div>
            
            <div class="form-group">
                <label for="price">Price (LKR) *</label>
                <input type="number" step="0.01" min="0" id="price" name="price" 
                       class="form-control" placeholder="0.00" required />
                <div class="form-text">Price in Sri Lankan Rupees</div>
            </div>
            
            <div class="form-group">
                <label for="quantity">Stock Quantity *</label>
                <input type="number" min="0" id="quantity" name="quantity" 
                       class="form-control" placeholder="0" required />
                <div class="form-text">Number of copies available in stock</div>
            </div>
            
            <div class="form-group">
                <label for="category">Category</label>
                <select id="category" name="category" class="form-control">
                    <option value="">Select a category</option>
                    <option value="Fiction">Fiction</option>
                    <option value="Non-Fiction">Non-Fiction</option>
                    <option value="Science Fiction">Science Fiction</option>
                    <option value="Mystery">Mystery</option>
                    <option value="Romance">Romance</option>
                    <option value="Biography">Biography</option>
                    <option value="History">History</option>
                    <option value="Science">Science</option>
                    <option value="Technology">Technology</option>
                    <option value="Business">Business</option>
                    <option value="Self-Help">Self-Help</option>
                    <option value="Children">Children</option>
                    <option value="Educational">Educational</option>
                    <option value="Other">Other</option>
                </select>
                <div class="form-text">Select the most appropriate category for the book</div>
            </div>
            
            <div class="form-group">
                <label for="publisher">Publisher</label>
                <input type="text" id="publisher" name="publisher" class="form-control"
                       placeholder="Enter publisher name">
                <div class="form-text">Optional: Book publisher or publishing company</div>
            </div>
            
            <div class="form-group">
                <label for="publicationYear">Publication Year</label>
                <input type="number" id="publicationYear" name="publicationYear" class="form-control"
                       min="1900" max="2030" 
                       placeholder="e.g., 2023">
                <div class="form-text">Optional: Year the book was published</div>
            </div>
            
            <div class="form-group">
                <label for="language">Language</label>
                <select id="language" name="language" class="form-control">
                    <option value="English">English</option>
                    <option value="Sinhala">Sinhala</option>
                    <option value="Tamil">Tamil</option>
                    <option value="French">French</option>
                    <option value="German">German</option>
                    <option value="Spanish">Spanish</option>
                    <option value="Other">Other</option>
                </select>
                <div class="form-text">Primary language of the book</div>
            </div>
            
            <div class="form-group">
                <label for="description">Description</label>
                <textarea id="description" name="description" class="form-control" 
                          rows="4" placeholder="Enter a brief description of the book"></textarea>
                <div class="form-text">Optional brief description of the book's content</div>
            </div>
            
            <div class="form-actions form-actions-centered">
                <button type="submit" class="btn btn-primary">
                    <span>📚</span> Add Book
                </button>
                <a href="${pageContext.request.contextPath}/controller/books" class="btn btn-secondary">
                    <span>↩️</span> Back to Books
                </a>
            </div>
        </form>
    </div>
    
    <div class="info-section">
        <div class="info-card">
            <h3>📋 Form Guidelines</h3>
            <ul>
                <li><strong>Title:</strong> Use the complete title as it appears on the book cover</li>
                <li><strong>Author:</strong> Include the full name of the primary author</li>
                <li><strong>ISBN:</strong> Enter the 13-digit ISBN (with or without hyphens)</li>
                <li><strong>Price:</strong> Enter the selling price in Sri Lankan Rupees</li>
                <li><strong>Quantity:</strong> Enter the number of copies available for sale</li>
                <li><strong>Category:</strong> Select the most appropriate category for easy organization</li>
            </ul>
        </div>
    </div>
</div>

<script>
// Form validation and enhancement
document.addEventListener('DOMContentLoaded', function() {
    const form = document.querySelector('.book-form');
    const isbnInput = document.getElementById('isbn');
    const priceInput = document.getElementById('price');
    const quantityInput = document.getElementById('quantity');
    
    // ISBN formatting
    isbnInput.addEventListener('input', function(e) {
        let value = e.target.value.replace(/[^0-9X]/gi, '');
        if (value.length > 13) {
            value = value.substring(0, 13);
        }
        e.target.value = value;
    });
    
    // Price validation
    priceInput.addEventListener('input', function(e) {
        if (e.target.value < 0) {
            e.target.value = 0;
        }
    });
    
    // Quantity validation
    quantityInput.addEventListener('input', function(e) {
        if (e.target.value < 0) {
            e.target.value = 0;
        }
    });
    
    // Form submission enhancement
    form.addEventListener('submit', function(e) {
        const title = document.getElementById('title').value.trim();
        const author = document.getElementById('author').value.trim();
        const isbn = document.getElementById('isbn').value.trim();
        const price = parseFloat(document.getElementById('price').value);
        const quantity = parseInt(document.getElementById('quantity').value);
        
        if (!title || !author || !isbn || price <= 0 || quantity < 0) {
            e.preventDefault();
            alert('Please fill in all required fields correctly.');
            return false;
        }
        
        // Show loading state
        const submitBtn = form.querySelector('button[type="submit"]');
        submitBtn.innerHTML = '<span>⏳</span> Adding Book...';
        submitBtn.disabled = true;
    });
});
</script>
</body>
</html> 