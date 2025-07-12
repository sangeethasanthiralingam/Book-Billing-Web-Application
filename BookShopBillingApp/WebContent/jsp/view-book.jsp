<%@ include file="header.jspf" %>
<%@ page import="model.Book" %>
<%
    Book book = (Book) request.getAttribute("book");
%>
<div class="container">
    <div class="form-card" style="max-width:500px;margin:2rem auto;">
        <h2>Book Details</h2>
        <table style="width:100%;margin-bottom:1.5rem;">
            <tr><th style="text-align:left;width:40%;">Title:</th><td><%= book.getTitle() %></td></tr>
            <tr><th style="text-align:left;">Author:</th><td><%= book.getAuthor() %></td></tr>
            <tr><th style="text-align:left;">ISBN:</th><td><%= book.getIsbn() %></td></tr>
            <tr><th style="text-align:left;">Price:</th><td>$<%= String.format("%.2f", book.getPrice()) %></td></tr>
            <tr><th style="text-align:left;">Stock Quantity:</th><td><%= book.getQuantity() %></td></tr>
            <tr><th style="text-align:left;">Category:</th><td><%= book.getCategory() != null ? book.getCategory() : "-" %></td></tr>
        </table>
        <a href="${pageContext.request.contextPath}/controller/books" class="btn btn-secondary">Back to Books</a>
    </div>
</div> 