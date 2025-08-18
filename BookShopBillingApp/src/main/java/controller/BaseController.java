package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import service.*;

/**
 * Base controller class providing common functionality for all controllers
 */
public abstract class BaseController {
    
    protected static final String ERROR_PAGE = "/jsp/error.jsp";
    protected static final String LOGIN_PAGE = "/jsp/login.jsp";
    
    /**
     * Check if user is authenticated
     */
    protected boolean isAuthenticated(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        return session != null && session.getAttribute("user") != null;
    }
    
    /**
     * Get current user role from session
     */
    protected String getUserRole(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        return (session != null) ? (String) session.getAttribute("userRole") : null;
    }
    
    /**
     * Get current user ID from session
     */
    protected Integer getUserId(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        return (session != null) ? (Integer) session.getAttribute("userId") : null;
    }
    
    /**
     * Check if user has required role
     */
    protected boolean hasRole(HttpServletRequest request, String requiredRole) {
        String userRole = getUserRole(request);
        return requiredRole.equals(userRole);
    }
    
    /**
     * Redirect to login page
     */
    protected void redirectToLogin(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        response.sendRedirect(request.getContextPath() + LOGIN_PAGE);
    }
    
    /**
     * Forward to error page with message
     */
    protected void forwardToError(HttpServletRequest request, HttpServletResponse response, 
                                 String errorMessage) throws ServletException, IOException {
        request.setAttribute("error", errorMessage);
        request.getRequestDispatcher(ERROR_PAGE).forward(request, response);
    }
    
    /**
     * Add common attributes to request
     */
    protected void addCommonAttributes(HttpServletRequest request) {
        try {
            request.setAttribute("systemName", ConfigurationService.getInstance().getSystemName());
        } catch (Exception e) {
            request.setAttribute("systemName", "BookShop Billing System");
        }
    }
    
    /**
     * Handle exceptions and forward to error page
     */
    protected void handleException(HttpServletRequest request, HttpServletResponse response, 
                                  Exception e, String operation) throws ServletException, IOException {
        e.printStackTrace();
        String errorMessage = "Error during " + operation + ": " + e.getMessage();
        forwardToError(request, response, errorMessage);
    }
    
    /**
     * Send JSON response
     */
    protected void sendJsonResponse(HttpServletResponse response, String json) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(json);
    }
    
    /**
     * Send JSON error response
     */
    protected void sendJsonError(HttpServletResponse response, String message) throws IOException {
        String json = "{\"success\":false,\"message\":\"" + escapeJson(message) + "\"}";
        sendJsonResponse(response, json);
    }
    
    /**
     * Send JSON success response
     */
    protected void sendJsonSuccess(HttpServletResponse response, String message) throws IOException {
        String json = "{\"success\":true,\"message\":\"" + escapeJson(message) + "\"}";
        sendJsonResponse(response, json);
    }
    
    /**
     * Escape JSON string
     */
    private String escapeJson(String str) {
        if (str == null) return "";
        return str.replace("\\", "\\\\")
                  .replace("\"", "\\\"")
                  .replace("\n", "\\n")
                  .replace("\r", "\\r")
                  .replace("\t", "\\t");
    }
    
    /**
     * Validate required parameters
     */
    protected boolean validateRequiredParams(HttpServletRequest request, String... params) {
        for (String param : params) {
            String value = request.getParameter(param);
            if (value == null || value.trim().isEmpty()) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * Get parameter with default value
     */
    protected String getParameter(HttpServletRequest request, String name, String defaultValue) {
        String value = request.getParameter(name);
        return (value != null && !value.trim().isEmpty()) ? value.trim() : defaultValue;
    }
    
    /**
     * Get integer parameter with default value
     */
    protected int getIntParameter(HttpServletRequest request, String name, int defaultValue) {
        String value = request.getParameter(name);
        if (value != null && !value.trim().isEmpty()) {
            try {
                return Integer.parseInt(value.trim());
            } catch (NumberFormatException e) {
                return defaultValue;
            }
        }
        return defaultValue;
    }
    
    /**
     * Get double parameter with default value
     */
    protected double getDoubleParameter(HttpServletRequest request, String name, double defaultValue) {
        String value = request.getParameter(name);
        if (value != null && !value.trim().isEmpty()) {
            try {
                return Double.parseDouble(value.trim());
            } catch (NumberFormatException e) {
                return defaultValue;
            }
        }
        return defaultValue;
    }
} 