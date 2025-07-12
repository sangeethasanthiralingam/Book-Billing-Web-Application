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
    <div class="header">
        <h1>Customers</h1>
    </div>
    <div class="report-card">
        <div class="report-header">
            <div class="report-title">Customer List</div>
        </div>
        <div class="report-body">
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
                            <td>${customer.active ? 'Active' : 'Inactive'}</td>
                            <td>${stats.billCount}</td>
                            <td>$<c:out value="${stats.totalSpent}" /></td>
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
        </div>
    </div>
</div>
</body>
</html> 