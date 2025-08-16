<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ include file="header.jspf" %>
<%@ taglib uri="jakarta.tags.functions" prefix="fn" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Book Store - Explore & Buy Books</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/store.css">
</head>
<body>
<div class="container">
    <div class="store-header">
        <div class="store-title">🛒 Explore & Buy Books</div>
    </div>
    <form class="store-search-bar">
        <input type="text" name="search" placeholder="Search by title, author, or category..." />
        <select name="category">
            <option value="">All Categories</option>
            <c:forEach var="cat" items="${categories}">
                <option value="${cat}" ${param.category == cat ? 'selected' : ''}>${cat}</option>
            </c:forEach>
        </select>
        <button type="submit">🔍 Search</button>
    </form>
    <c:choose>
        <c:when test="${not empty books}">
            <div class="book-grid">
                <c:forEach var="book" items="${books}">
                    <div class="book-card">
                        <c:choose>
                            <c:when test="${not empty book.coverImage}">
                                <img src="${book.coverImage}" alt="Book Cover" class="book-cover" />
                            </c:when>
                            <c:otherwise>
                                <img src="${pageContext.request.contextPath}/img/book-placeholder.png" alt="Book Cover" class="book-cover" />
                            </c:otherwise>
                        </c:choose>
                        <div class="book-title">${book.title}</div>
                        <div class="book-author">by ${book.author}</div>
                        <div class="book-price">LKR ${book.price}</div>
                        <div class="book-desc">No description available.</div>
                        <button class="add-cart-btn" style="background: var(--color-saffron); margin-top: 0.5rem;"
                            data-book='{"id":"${book.id}","title":"${fn:escapeXml(book.title)}","author":"${fn:escapeXml(book.author)}","price":"${book.price}"}'
                            onclick="addToCollectionFromData(this)">Add to Collection</button>
                    </div>
                </c:forEach>
            </div>
        </c:when>
        <c:otherwise>
            <div class="no-books">No books available.</div>
        </c:otherwise>
    </c:choose>

    <!-- Collection Sidebar/Modal -->
    <div id="collectionSidebar" class="collection-sidebar">
        <div class="collection-header">
            <h3>📚 My Collection</h3>
            <button class="close-btn" onclick="toggleCollectionSidebar()">&times;</button>
        </div>
        <div id="collectionList"></div>
        <textarea id="collectionNote" placeholder="Add a note to the admin (optional)" style="width:100%;margin-top:1rem;"></textarea>
        <button id="sendCollectionBtn" class="add-cart-btn" style="background: var(--color-teal); margin-top: 1rem;" onclick="openCollectionModal()">Send Collection to Admin</button>
        <div id="collectionMsg" style="margin-top:0.7rem;"></div>
    </div>
    <div id="collectionModal" class="collection-modal" style="display:none;">
        <div class="collection-modal-content">
            <h4>Send Collection to Admin?</h4>
            <p>Are you sure you want to send your collection to the admin?</p>
            <button onclick="sendCollection()" class="add-cart-btn" style="background: var(--color-success);">Yes, Send</button>
            <button onclick="closeCollectionModal()" class="add-cart-btn" style="background: var(--color-error);">Cancel</button>
        </div>
    </div>
    <button class="collection-toggle-btn" onclick="toggleCollectionSidebar()">📚 My Collection (<span id="collectionCount">0</span>)</button>
    <style>
    .collection-sidebar { position:fixed; top:0; right:0; width:340px; height:100vh; background:#fff; box-shadow:-2px 0 12px rgba(0,0,0,0.08); z-index:2000; padding:1.2rem; display:flex; flex-direction:column; transition:right 0.3s; }
    .collection-header { display:flex; justify-content:space-between; align-items:center; }
    .close-btn { background:none; border:none; font-size:2rem; cursor:pointer; color:#888; }
    .collection-modal { position:fixed; top:0; left:0; width:100vw; height:100vh; background:rgba(0,0,0,0.3); display:flex; align-items:center; justify-content:center; z-index:3000; }
    .collection-modal-content { background:#fff; padding:2rem; border-radius:10px; text-align:center; }
    .collection-toggle-btn { position:fixed; bottom:2rem; right:2rem; background:var(--color-teal); color:#fff; border:none; border-radius:50px; padding:0.8rem 1.5rem; font-size:1.1rem; z-index:2100; box-shadow:0 2px 8px rgba(0,0,0,0.12); cursor:pointer; }
    @media (max-width:700px) { .collection-sidebar { width:90vw; } }
    </style>
    <script>
    // Implemented addToCart logic with localStorage
    function addToCart(bookId) {
        // Get book data from the button
        const bookButton = document.querySelector(`[data-book*="\"id\":\"${bookId}\""]`);
        if (bookButton) {
            const book = JSON.parse(bookButton.getAttribute('data-book'));
            
            // Add to collection (which serves as cart)
            addToCollectionFromData(bookButton);
            
            // Show success message
            showNotification('Book added to collection!', 'success');
        }
    }
    
    // Notification system
    function showNotification(message, type = 'info') {
        const notification = document.createElement('div');
        notification.className = `notification notification-${type}`;
        notification.textContent = message;
        notification.style.cssText = `
            position: fixed;
            top: 20px;
            right: 20px;
            padding: 12px 20px;
            border-radius: 5px;
            color: white;
            font-weight: bold;
            z-index: 10000;
            animation: slideIn 0.3s ease;
        `;
        
        if (type === 'success') {
            notification.style.backgroundColor = '#28a745';
        } else if (type === 'error') {
            notification.style.backgroundColor = '#dc3545';
        } else {
            notification.style.backgroundColor = '#17a2b8';
        }
        
        document.body.appendChild(notification);
        
        setTimeout(() => {
            notification.style.animation = 'slideOut 0.3s ease';
            setTimeout(() => {
                if (notification.parentNode) {
                    notification.parentNode.removeChild(notification);
                }
            }, 300);
        }, 3000);
    }
    
    // Add CSS animations
    const style = document.createElement('style');
    style.textContent = `
        @keyframes slideIn {
            from { transform: translateX(100%); opacity: 0; }
            to { transform: translateX(0); opacity: 1; }
        }
        @keyframes slideOut {
            from { transform: translateX(0); opacity: 1; }
            to { transform: translateX(100%); opacity: 0; }
        }
    `;
    document.head.appendChild(style);

    // --- Collection logic with localStorage ---
    let collection = JSON.parse(localStorage.getItem('collection') || '[]');
    
    function saveCollection() { 
        localStorage.setItem('collection', JSON.stringify(collection)); 
        updateCollectionDisplay(); 
    }
    
    function addToCollectionFromData(btn) { 
        const book = JSON.parse(btn.getAttribute('data-book')); 
        if (!collection.some(b => b.id == book.id)) { 
            collection.push(book); 
            saveCollection();
            showNotification('Book added to collection!', 'success');
        } else {
            showNotification('Book already in collection!', 'info');
        }
    }
    
    function removeFromCollection(id) { 
        collection = collection.filter(b => b.id != id); 
        saveCollection(); 
        showNotification('Book removed from collection!', 'success');
    }
    function updateCollectionDisplay() {
        const listDiv = document.getElementById('collectionList');
        const countSpan = document.getElementById('collectionCount');
        
        if (countSpan) countSpan.textContent = collection.length;
        if (!listDiv) return;
        
        if (collection.length === 0) {
            listDiv.innerHTML = '<div class="no-books"><p>No books in your collection.</p></div>';
        } else {
            let html = '<ul style="list-style:none;padding:0;">';
            collection.forEach(book => {
                html += `<li style='margin-bottom:0.5rem; padding:0.5rem; border:1px solid #eee; border-radius:5px;'>
                    <div><b>${book.title}</b></div>
                    <div style='font-size:0.9rem; color:#666;'>by ${book.author}</div>
                    <div style='color:var(--color-teal); font-weight:bold;'>LKR ${book.price}</div>
                    <button style='margin-top:0.3rem;background:var(--color-error);color:#fff;border:none;border-radius:3px;padding:0.3rem 0.8rem;cursor:pointer;font-size:0.8rem;' onclick='removeFromCollection("${book.id}")'>Remove</button>
                </li>`;
            });
            html += '</ul>';
            listDiv.innerHTML = html;
        }
    }
    
    function toggleCollectionSidebar() {
        const sidebar = document.getElementById('collectionSidebar');
        if (sidebar.style.right === '-340px' || !sidebar.style.right) { 
            sidebar.style.right = '0px'; 
            updateCollectionDisplay(); 
        } else { 
            sidebar.style.right = '-340px'; 
        }
    }
    function openCollectionModal() { document.getElementById('collectionModal').style.display = 'flex'; }
    function closeCollectionModal() { document.getElementById('collectionModal').style.display = 'none'; }
    function sendCollection() {
        if (collection.length === 0) {
            showNotification('No books in collection to send!', 'error');
            return;
        }
        const btn = document.getElementById('sendCollectionBtn');
        btn.disabled = true;
        document.getElementById('collectionMsg').textContent = 'Sending...';
        const note = document.getElementById('collectionNote').value;
        
        // Get the correct base path
        var basePath = '${pageContext.request.contextPath}';
        if (!basePath) {
            basePath = '/bookshop-billing';
        }
        
        fetch(`${basePath}/controller/send-collection`, {
            method: 'POST',
            headers: {'Content-Type': 'application/json'},
            body: JSON.stringify({books: collection, note: note})
        })
        .then(res => {
            if (!res.ok) {
                throw new Error('Network response was not ok');
            }
            return res.json();
        })
        .then(data => {
            if (data.success) {
                document.getElementById('collectionMsg').textContent = 'Collection sent to admin!';
                showNotification('Collection sent successfully to admin!', 'success');
                collection = [];
                saveCollection();
                document.getElementById('collectionNote').value = '';
                closeCollectionModal();
                setTimeout(() => {
                    document.getElementById('collectionMsg').textContent = '';
                }, 3000);
            } else {
                document.getElementById('collectionMsg').textContent = 'Failed: ' + (data.message || 'Unknown error');
                showNotification('Failed to send collection: ' + (data.message || 'Unknown error'), 'error');
            }
        })
        .catch(err => {
            console.error('Error sending collection:', err);
            document.getElementById('collectionMsg').textContent = 'Error sending collection.';
            showNotification('Error sending collection. Please try again.', 'error');
        })
        .finally(() => { 
            btn.disabled = false; 
        });
    }
    document.addEventListener('DOMContentLoaded', function() { updateCollectionDisplay(); });
    </script>
</body>
</html> 