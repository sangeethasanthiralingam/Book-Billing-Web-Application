<html>
<head>
    <title>Search Results</title>
</head>
<body>
    <h1>Search Results</h1>
    <a href="/billing.jsp">Back to Billing</a>
    <hr>
    <table>
        <thead>
            <tr>
                <th>Book ID</th>
                <th>Title</th>
                <th>Author</th>
                <th>Price</th>
            </tr>
        </thead>
        <tbody>
        
        <c:forEach items="${searchResults}" var="book">
            <tr>
                <td>${book.id}</td>
                <td>${book.title}</td>
                <td>${book.author}</td>
                <td>${book.price}</td>
            </tr>
        </c:forEach>
        
        </tbody>
    </table>
</body>
</html>