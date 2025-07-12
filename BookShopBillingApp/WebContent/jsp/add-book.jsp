<%@ include file="header.jspf" %>
<div class="container">
    <div class="form-card">
        <h2>Add New Book</h2>
        <c:if test="${not empty error}">
            <div class="error-message">${error}</div>
        </c:if>
        <c:if test="${not empty success}">
            <div class="success-message">${success}</div>
        </c:if>
        <form method="post">
            <div class="form-group">
                <label for="title">Title</label>
                <input type="text" id="title" name="title" class="input-field" required />
            </div>
            <div class="form-group">
                <label for="author">Author</label>
                <input type="text" id="author" name="author" class="input-field" required />
            </div>
            <div class="form-group">
                <label for="isbn">ISBN</label>
                <input type="text" id="isbn" name="isbn" class="input-field" required />
            </div>
            <div class="form-group">
                <label for="price">Price</label>
                <input type="number" step="0.01" id="price" name="price" class="input-field" required />
            </div>
            <div class="form-group">
                <label for="quantity">Stock Quantity</label>
                <input type="number" id="quantity" name="quantity" class="input-field" required />
            </div>
            <div class="form-group">
                <label for="category">Category</label>
                <input type="text" id="category" name="category" class="input-field" />
            </div>
            <div class="form-actions">
                <button type="submit" class="btn btn-primary">Add Book</button>
            </div>
        </form>
    </div>
</div> 