<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ include file="header.jspf" %>
<%@ taglib uri="jakarta.tags.functions" prefix="fn" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <title>Book Store</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<div class="container">
    <div class="store-header">
        <h1 class="store-title">📚 Pahana Edu Bookstore</h1>
        <p class="store-subtitle">Discover & Collect Your Favorite Books</p>
    </div>
    
    <div class="store-search-bar">
        <div class="search-group">
            <input type="text" id="searchInput" placeholder="🔍 Search by title, author..." oninput="filterBooks()" />
            <select id="categorySelect" onchange="filterBooks()">
                <option value="">All Categories</option>
                <c:forEach var="cat" items="${categories}">
                    <option value="${cat}">${cat}</option>
                </c:forEach>
            </select>
            <button class="clear-btn" onclick="clearFilters()">✖️ Clear</button>
        </div>
    </div>
    
    <div class="book-grid">
        <c:forEach var="book" items="${books}">
            <div class="book-card">
                <img src="${pageContext.request.contextPath}/img/book-placeholder.svg" alt="Book Cover" class="book-cover" />
                <div class="book-title">${book.title}</div>
                <div class="book-author">by ${book.author}</div>
                <div class="book-price">LKR ${book.price}</div>
                <button class="add-cart-btn" 
                    data-book='{"id":"${book.id}","title":"${fn:escapeXml(book.title)}","author":"${fn:escapeXml(book.author)}","price":"${book.price}"}'
                    onclick="addToCollection(this)">Add to Collection</button>
            </div>
        </c:forEach>
    </div>
    
    <button class="collection-toggle-btn" onclick="toggleCollection()">My Collection (<span id="collectionCount">0</span>)</button>
    
    <div id="collectionSidebar" class="collection-sidebar" style="right:-340px;">
        <div class="collection-header">
            <h3>📚 My Collection</h3>
            <button class="close-btn" onclick="toggleCollection()">&times;</button>
        </div>
        <div id="collectionList"></div>
        <textarea id="collectionNote" placeholder="Add a note to admin (optional)" rows="3"></textarea>
        <button class="send-btn" onclick="sendCollection()">📧 Send to Admin</button>
    </div>
</div>

<style>
:root {
    --color-saffron: #f4a261;
    --color-teal: #2a9d8f;
    --color-success: #43a047;
    --color-error: #e53e3e;
    --color-warm: #e76f51;
}

.store-header {
    text-align: center;
    margin: 2rem 0;
    background: linear-gradient(135deg, var(--color-saffron), var(--color-teal));
    padding: 2rem;
    border-radius: 15px;
    color: white;
}

.store-title {
    font-size: 2.5rem;
    margin: 0;
    font-family: 'Playfair Display', serif;
}

.store-subtitle {
    font-size: 1.2rem;
    margin: 0.5rem 0 0 0;
    opacity: 0.9;
}

.store-search-bar {
    margin: 2rem 0;
    background: white;
    padding: 1.5rem;
    border-radius: 10px;
    box-shadow: 0 4px 12px rgba(0,0,0,0.1);
}

.search-group {
    display: flex;
    gap: 1rem;
    align-items: center;
}

.search-group input, .search-group select {
    flex: 1;
    padding: 0.8rem;
    border: 2px solid #e0e0e0;
    border-radius: 8px;
    font-size: 1rem;
}

.search-group input:focus, .search-group select:focus {
    border-color: var(--color-teal);
    outline: none;
}

.clear-btn {
    background: var(--color-error);
    color: white;
    border: none;
    padding: 0.8rem 1.2rem;
    border-radius: 8px;
    cursor: pointer;
    font-weight: bold;
}

.book-grid {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
    gap: 1.5rem;
    margin: 2rem 0;
}

.book-card {
    background: white;
    border-radius: 12px;
    padding: 1.5rem;
    text-align: center;
    box-shadow: 0 4px 12px rgba(0,0,0,0.1);
    transition: transform 0.3s ease, box-shadow 0.3s ease;
}

.book-card:hover {
    transform: translateY(-5px);
    box-shadow: 0 8px 20px rgba(0,0,0,0.15);
}

.book-cover {
    width: 120px;
    height: 150px;
    border-radius: 8px;
    margin-bottom: 1rem;
}

.book-title {
    font-weight: bold;
    font-size: 1.1rem;
    color: #333;
    margin: 0.5rem 0;
}

.book-author {
    color: #666;
    font-style: italic;
    margin: 0.3rem 0;
}

.book-price {
    font-size: 1.2rem;
    font-weight: bold;
    color: var(--color-teal);
    margin: 0.5rem 0;
}

.add-cart-btn {
    background: var(--color-saffron);
    color: white;
    border: none;
    padding: 0.8rem 1.5rem;
    border-radius: 25px;
    cursor: pointer;
    font-weight: bold;
    transition: background 0.3s ease;
}

.add-cart-btn:hover {
    background: var(--color-warm);
}

.collection-sidebar {
    position: fixed;
    top: 0;
    right: -340px;
    width: 340px;
    height: 100vh;
    background: white;
    box-shadow: -2px 0 12px rgba(0,0,0,0.15);
    padding: 1.5rem;
    transition: right 0.3s ease;
    z-index: 1000;
}

.collection-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 1rem;
    padding-bottom: 1rem;
    border-bottom: 2px solid #f0f0f0;
}

.close-btn {
    background: none;
    border: none;
    font-size: 2rem;
    cursor: pointer;
    color: #999;
}

#collectionNote {
    width: 100%;
    padding: 0.8rem;
    border: 2px solid #e0e0e0;
    border-radius: 8px;
    margin: 1rem 0;
    resize: vertical;
}

.send-btn {
    background: var(--color-success);
    color: white;
    border: none;
    padding: 1rem;
    border-radius: 8px;
    cursor: pointer;
    font-weight: bold;
    width: 100%;
}

.collection-toggle-btn {
    position: fixed;
    bottom: 2rem;
    right: 2rem;
    background: var(--color-teal);
    color: white;
    border: none;
    padding: 1rem 1.5rem;
    border-radius: 50px;
    cursor: pointer;
    font-weight: bold;
    box-shadow: 0 4px 12px rgba(0,0,0,0.2);
    z-index: 999;
}

.empty-collection {
    text-align: center;
    color: #999;
    padding: 2rem;
    font-style: italic;
}

.collection-items {
    max-height: 400px;
    overflow-y: auto;
    margin-bottom: 1rem;
}

.collection-item {
    display: flex;
    justify-content: space-between;
    align-items: flex-start;
    padding: 1rem;
    margin-bottom: 0.8rem;
    background: #f8f9fa;
    border-radius: 8px;
    border-left: 4px solid var(--color-teal);
}

.item-info {
    flex: 1;
}

.item-title {
    font-weight: bold;
    color: #333;
    margin-bottom: 0.3rem;
}

.item-author {
    color: #666;
    font-size: 0.9rem;
    margin-bottom: 0.3rem;
}

.item-price {
    color: var(--color-teal);
    font-weight: bold;
    font-size: 1.1rem;
}

.remove-btn {
    background: var(--color-error);
    color: white;
    border: none;
    border-radius: 50%;
    width: 30px;
    height: 30px;
    cursor: pointer;
    font-size: 1rem;
    display: flex;
    align-items: center;
    justify-content: center;
}

.collection-summary {
    border-top: 2px solid var(--color-teal);
    padding-top: 1rem;
    margin-top: 1rem;
}

.summary-row {
    display: flex;
    justify-content: space-between;
    margin-bottom: 0.5rem;
}

.total-row {
    font-weight: bold;
    font-size: 1.1rem;
    color: var(--color-teal);
}

.summary-value {
    font-weight: bold;
}

@keyframes slideIn {
    from { transform: translateX(100%); opacity: 0; }
    to { transform: translateX(0); opacity: 1; }
}

@keyframes slideOut {
    from { transform: translateX(0); opacity: 1; }
    to { transform: translateX(100%); opacity: 0; }
}
</style>

<script>
let collection = [];
let allBooks = [];

function addToCollection(btn) {
    const book = JSON.parse(btn.getAttribute('data-book'));
    if (!collection.some(b => b.id == book.id)) {
        collection.push(book);
        updateCollection();
        showNotification('📚 Book added to collection!', 'success');
    } else {
        showNotification('ℹ️ Book already in collection!', 'info');
    }
}

function removeFromCollection(index) {
    const book = collection[index];
    collection.splice(index, 1);
    updateCollection();
    showNotification(`🗑️ "${book.title}" removed from collection`, 'success');
}

function showNotification(message, type) {
    const notification = document.createElement('div');
    notification.className = `notification ${type}`;
    notification.textContent = message;
    notification.style.cssText = `
        position: fixed; top: 20px; right: 20px; z-index: 2000;
        padding: 1rem 1.5rem; border-radius: 8px; color: white;
        font-weight: bold; animation: slideIn 0.3s ease;
        background: " + (type === 'success' ? 'var(--color-success)' : 'var(--color-teal)') + ";
    `;
    document.body.appendChild(notification);
    setTimeout(() => {
        notification.style.animation = 'slideOut 0.3s ease';
        setTimeout(() => notification.remove(), 300);
    }, 3000);
}

function updateCollection() {
    document.getElementById('collectionCount').textContent = collection.length;
    const list = document.getElementById('collectionList');
    
    if (collection.length === 0) {
        list.innerHTML = '<div class="empty-collection">📚 No books in collection</div>';
        return;
    }
    
    const totalPrice = collection.reduce((sum, book) => sum + parseFloat(book.price), 0);
    
    list.innerHTML = '<div class="collection-items">' +
        collection.map((book, index) => 
            '<div class="collection-item">' +
                '<div class="item-info">' +
                    '<div class="item-title">' + book.title + '</div>' +
                    '<div class="item-author">by ' + book.author + '</div>' +
                    '<div class="item-price">LKR ' + parseFloat(book.price).toFixed(2) + '</div>' +
                '</div>' +
                '<button class="remove-btn" onclick="removeFromCollection(' + index + ')">✖</button>' +
            '</div>'
        ).join('') +
        '</div>' +
        '<div class="collection-summary">' +
            '<div class="summary-row">' +
                '<span>Total Books:</span>' +
                '<span class="summary-value">' + collection.length + '</span>' +
            '</div>' +
            '<div class="summary-row total-row">' +
                '<span>Total Value:</span>' +
                '<span class="summary-value">LKR ' + totalPrice.toFixed(2) + '</span>' +
            '</div>' +
        '</div>';
}

function toggleCollection() {
    const sidebar = document.getElementById('collectionSidebar');
    sidebar.style.right = sidebar.style.right === '0px' ? '-340px' : '0px';
}

function filterBooks() {
    const search = document.getElementById('searchInput').value.toLowerCase();
    const category = document.getElementById('categorySelect').value;
    
    document.querySelectorAll('.book-card').forEach(card => {
        const title = card.querySelector('.book-title').textContent.toLowerCase();
        const author = card.querySelector('.book-author').textContent.toLowerCase();
        
        const matchesSearch = !search || title.includes(search) || author.includes(search);
        const matchesCategory = !category || card.textContent.includes(category);
        
        card.style.display = matchesSearch && matchesCategory ? 'block' : 'none';
    });
}

function clearFilters() {
    document.getElementById('searchInput').value = '';
    document.getElementById('categorySelect').value = '';
    filterBooks();
}

function sendCollection() {
    if (collection.length === 0) {
        alert('No books in collection!');
        return;
    }
    
    const note = document.getElementById('collectionNote').value;
    
    fetch('${pageContext.request.contextPath}/controller/send-collection', {
        method: 'POST',
        headers: {'Content-Type': 'application/json'},
        body: JSON.stringify({books: collection, note: note})
    })
    .then(res => res.json())
    .then(data => {
        if (data.success) {
            alert('✅ Collection sent to admin successfully!');
            collection = [];
            updateCollection();
            document.getElementById('collectionNote').value = '';
            toggleCollection();
        } else {
            alert('❌ Failed to send collection: ' + (data.message || 'Unknown error'));
        }
    })
    .catch(err => {
        alert('❌ Error sending collection. Please try again.');
    });
}
</script>
</body>
</html>