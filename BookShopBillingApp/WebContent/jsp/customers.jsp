<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ include file="header.jspf" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Customers</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <div class="container">
        <div class="page-header">
            <div>
                <h1 class="page-header-title">📋 Customer Management</h1>
                <p class="page-header-subtitle">Manage customer accounts and view purchase history</p>
            </div>
            <div class="page-header-actions">
                <a href="${pageContext.request.contextPath}/controller/customer-form" class="btn btn-primary">➕ Add Customer</a>
            </div>
        </div>
        <form method="get" action="${pageContext.request.contextPath}/controller/customers" class="filter-bar">
            <div class="filter-fields">
                <div class="form-group">
                    <label for="search">Search</label>
                    <input type="text" id="search" name="search" value="${param.search}" placeholder="Search by name, email, or phone..." />
                </div>
                <div class="form-group">
                    <label for="status">Status</label>
                    <select id="status" name="status">
                        <option value="">All Status</option>
                        <option value="active" ${param.status == 'active' ? 'selected' : ''}>Active</option>
                        <option value="inactive" ${param.status == 'inactive' ? 'selected' : ''}>Inactive</option>
                    </select>
                </div>
                <div class="form-group">
                    <label for="sortBy">Sort By</label>
                    <select id="sortBy" name="sortBy">
                        <option value="name" ${param.sortBy == 'name' ? 'selected' : ''}>Name</option>
                        <option value="email" ${param.sortBy == 'email' ? 'selected' : ''}>Email</option>
                        <option value="bills" ${param.sortBy == 'bills' ? 'selected' : ''}>Bill Count</option>
                        <option value="spent" ${param.sortBy == 'spent' ? 'selected' : ''}>Total Spent</option>
                        <option value="recent" ${param.sortBy == 'recent' ? 'selected' : ''}>Recently Active</option>
                    </select>
                </div>
            </div>
            <div class="filter-actions">
                <button type="submit" class="btn btn-primary" title="Search">🔍</button>
                <a href="${pageContext.request.contextPath}/controller/customers" class="btn btn-secondary" title="Clear">✖️</a>
            </div>
        </form>

    <!-- Results Summary -->
    <c:if test="${not empty param.search || not empty param.status}">
        <div class="info-card">
            <div class="info-content">
                <span class="info-icon">📊</span>
                <div class="info-text">
                    <strong>Search Results:</strong> 
                    <c:choose>
                        <c:when test="${not empty param.search && not empty param.status}">
                            Found ${customerStats.size()} customers matching "${param.search}" with ${param.status} status
                        </c:when>
                        <c:when test="${not empty param.search}">
                            Found ${customerStats.size()} customers matching "${param.search}"
                        </c:when>
                        <c:when test="${not empty param.status}">
                            Showing ${customerStats.size()} customers with ${param.status} status
                        </c:when>
                    </c:choose>
                </div>
            </div>
        </div>
    </c:if>

    <!-- Customer List Table (no report-card/report-body wrappers) -->
    <div class="report-header">
        <div class="report-title">Customer List</div>
        <div class="report-stats">
            <span class="stat-item">
                <span class="stat-icon">👥</span>
                <span class="stat-label">Total:</span>
                <span class="stat-value">${customerStats.size()}</span>
            </span>
            <span class="stat-item">
                <span class="stat-icon">✅</span>
                <span class="stat-label">Active:</span>
                <span class="stat-value">
                    <c:set var="activeCount" value="0" />
                    <c:forEach var="stats" items="${customerStats}">
                        <c:if test="${stats.customer.active}">
                            <c:set var="activeCount" value="${activeCount + 1}" />
                        </c:if>
                    </c:forEach>
                    ${activeCount}
                </span>
            </span>
        </div>
    </div>
    <table class="transaction-table">
        <thead>
            <tr>
                <th>Full Name</th>
                <th>Email</th>
                <th>Phone</th>
                <th>Status</th>
                <th>Bill Count</th>
                <th>Total Spent</th>
                <th>Actions</th>
            </tr>
        </thead>
        <tbody>
            <c:forEach var="stats" items="${customerStats}">
                <c:set var="customer" value="${stats.customer}" />
                <tr>
                    <td>${customer.fullName}</td>
                    <td>${customer.email}</td>
                    <td>${customer.phone}</td>
                    <td>
                        <span class="status-badge ${customer.active ? 'status-active' : 'status-inactive'}">
                            ${customer.active ? 'Active' : 'Inactive'}
                        </span>
                    </td>
                    <td>${stats.billCount}</td>
                    <td>LKR <c:out value="${stats.totalSpent}" /></td>
                    <td class="action-buttons">
                        <a href="${pageContext.request.contextPath}/controller/edit-user?id=${customer.id}" class="edit-btn" title="Edit Customer">✏️</a>
                        <c:choose>
                            <c:when test="${customer.active}">
                                <a href="${pageContext.request.contextPath}/controller/delete-user?id=${customer.id}" class="delete-btn" title="Deactivate Customer" onclick="return confirm('Are you sure you want to deactivate this customer?');">🚫</a>
                            </c:when>
                            <c:otherwise>
                                <a href="${pageContext.request.contextPath}/controller/delete-user?id=${customer.id}&action=activate" class="activate-btn" title="Activate Customer">✅</a>
                            </c:otherwise>
                        </c:choose>
                        <a href="${pageContext.request.contextPath}/controller/view-customer?id=${customer.id}" class="view-btn" title="View Details">👁️</a>
                        <a href="${pageContext.request.contextPath}/controller/customer-purchases?id=${customer.id}" class="btn btn-secondary" title="Purchase History">🧾</a>
                    </td>
                </tr>
            </c:forEach>
        </tbody>
    </table>
    <div class="pagination">
        <c:if test="${totalPages > 1}">
            <c:if test="${page > 1}">
                <a href="${pageContext.request.contextPath}/controller/customers?search=${param.search}&status=${param.status}&sortBy=${param.sortBy}&page=1" class="btn btn-secondary pagination-btn" title="First">⏮</a>
            </c:if>
            <c:if test="${page > 1}">
                <a href="${pageContext.request.contextPath}/controller/customers?search=${param.search}&status=${param.status}&sortBy=${param.sortBy}&page=${page-1}" class="btn btn-secondary pagination-btn" title="Previous">⟨</a>
            </c:if>
            <c:forEach var="i" begin="1" end="${totalPages}">
                <c:if test="${i == page}">
                    <span class="btn btn-primary pagination-btn" style="pointer-events:none;">${i}</span>
                </c:if>
                <c:if test="${i != page}">
                    <a href="${pageContext.request.contextPath}/controller/customers?search=${param.search}&status=${param.status}&sortBy=${param.sortBy}&page=${i}" class="btn btn-secondary pagination-btn">${i}</a>
                </c:if>
            </c:forEach>
            <c:if test="${page < totalPages}">
                <a href="${pageContext.request.contextPath}/controller/customers?search=${param.search}&status=${param.status}&sortBy=${param.sortBy}&page=${page+1}" class="btn btn-secondary pagination-btn" title="Next">⟩</a>
            </c:if>
            <c:if test="${page < totalPages}">
                <a href="${pageContext.request.contextPath}/controller/customers?search=${param.search}&status=${param.status}&sortBy=${param.sortBy}&page=${totalPages}" class="btn btn-secondary pagination-btn" title="Last">⏭</a>
            </c:if>
        </c:if>
    </div>
</div>
</body>
</html> 