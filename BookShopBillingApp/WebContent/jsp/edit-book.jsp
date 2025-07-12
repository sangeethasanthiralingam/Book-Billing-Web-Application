<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page import="model.Book" %>
<%@ include file="header.jspf" %>
<%
    Book book = (Book) request.getAttribute("book");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Edit Book - BookShop Billing</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<div class="container">
    <div class="form-card form-card-wide">
        <div class="form-header">
            <h1>Edit Book</h1>
            <p>Update book information and inventory details</p>
        </div>
        
        <c:if test="${not empty error}">
            <div class="error-message">${error}</div>
        </c:if>
        <c:if test="${not empty success}">
            <div class="success-message">${success}</div>
        </c:if>
        
        <form method="post">
            <div class="form-section">
                <h2 class="section-title">Basic Information</h2>
                <div class="form-group">
                    <label for="title">Title *</label>
                    <input type="text" id="title" name="title" value="<%= book.getTitle() %>" required />
                </div>
                <div class="form-group">
                    <label for="author">Author *</label>
                    <input type="text" id="author" name="author" value="<%= book.getAuthor() %>" required />
                </div>
                <div class="form-group">
                    <label for="isbn">ISBN *</label>
                    <input type="text" id="isbn" name="isbn" value="<%= book.getIsbn() %>" required />
                    <div class="form-text">International Standard Book Number</div>
                </div>
                <div class="form-group">
                    <label for="category">Category</label>
                    <input type="text" id="category" name="category" value="<%= book.getCategory() != null ? book.getCategory() : "" %>" />
                    <div class="form-text">Optional: Book category or genre</div>
                </div>
            </div>
            <div class="form-section">
                <h2 class="section-title">Publication Details</h2>
                <div class="form-group">
                    <label for="publisher">Publisher</label>
                    <input type="text" id="publisher" name="publisher" value="<%= book.getPublisher() != null ? book.getPublisher() : "" %>" />
                    <div class="form-text">Optional: Book publisher or publishing company</div>
                </div>
                <div class="form-group">
                    <label for="publicationYear">Publication Year</label>
                    <input type="number" id="publicationYear" name="publicationYear" min="1900" max="2030" value="<%= book.getPublicationYear() != null ? book.getPublicationYear() : "" %>" />
                    <div class="form-text">Optional: Year the book was published</div>
                </div>
                <div class="form-group">
                    <label for="language">Language</label>
                    <select id="language" name="language">
                        <option value="English" <%= "English".equals(book.getLanguage()) ? "selected" : "" %>>English</option>
                        <option value="Sinhala" <%= "Sinhala".equals(book.getLanguage()) ? "selected" : "" %>>Sinhala</option>
                        <option value="Tamil" <%= "Tamil".equals(book.getLanguage()) ? "selected" : "" %>>Tamil</option>
                        <option value="French" <%= "French".equals(book.getLanguage()) ? "selected" : "" %>>French</option>
                        <option value="German" <%= "German".equals(book.getLanguage()) ? "selected" : "" %>>German</option>
                        <option value="Spanish" <%= "Spanish".equals(book.getLanguage()) ? "selected" : "" %>>Spanish</option>
                        <option value="Other" <%= "Other".equals(book.getLanguage()) ? "selected" : "" %>>Other</option>
                    </select>
                    <div class="form-text">Primary language of the book</div>
                </div>
            </div>
            <div class="form-section">
                <h2 class="section-title">Inventory & Pricing</h2>
                <div class="form-group">
                    <label for="price">Price *</label>
                    <input type="number" step="0.01" id="price" name="price" value="<%= book.getPrice() %>" required />
                    <div class="form-text">Price in local currency</div>
                </div>
                <div class="form-group">
                    <label for="quantity">Stock Quantity *</label>
                    <input type="number" id="quantity" name="quantity" value="<%= book.getQuantity() %>" required />
                    <div class="form-text">Current stock level</div>
                </div>
            </div>
            <div class="form-actions">
                <button type="submit" class="btn btn-primary">Update Book</button>
                <a href="${pageContext.request.contextPath}/controller/books" class="btn btn-secondary">Cancel</a>
            </div>
        </form>
    </div>
</div>
</body>
</html> 