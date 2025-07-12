<%@ include file="header.jspf" %>
<%@ page import="model.Book" %>
<%
    Book book = (Book) request.getAttribute("book");
%>
<div class="container">
    <div class="form-card">
        <h2>Edit Book</h2>
        <c:if test="${not empty error}">
            <div class="error-message">${error}</div>
        </c:if>
        <c:if test="${not empty success}">
            <div class="success-message">${success}</div>
        </c:if>
        <form method="post">
            <div class="form-group">
                <label for="title">Title</label>
                <input type="text" id="title" name="title" class="input-field" value="<%= book.getTitle() %>" required />
            </div>
            <div class="form-group">
                <label for="author">Author</label>
                <input type="text" id="author" name="author" class="input-field" value="<%= book.getAuthor() %>" required />
            </div>
            <div class="form-group">
                <label for="isbn">ISBN</label>
                <input type="text" id="isbn" name="isbn" class="input-field" value="<%= book.getIsbn() %>" required />
            </div>
            <div class="form-group">
                <label for="price">Price</label>
                <input type="number" step="0.01" id="price" name="price" class="input-field" value="<%= book.getPrice() %>" required />
            </div>
            <div class="form-group">
                <label for="quantity">Stock Quantity</label>
                <input type="number" id="quantity" name="quantity" class="input-field" value="<%= book.getQuantity() %>" required />
            </div>
            <div class="form-group">
                <label for="category">Category</label>
                <input type="text" id="category" name="category" class="input-field" value="<%= book.getCategory() != null ? book.getCategory() : "" %>" />
            </div>
            <div class="form-actions">
                <button type="submit" class="btn btn-primary">Update Book</button>
            </div>
        </form>
    </div>
</div> 