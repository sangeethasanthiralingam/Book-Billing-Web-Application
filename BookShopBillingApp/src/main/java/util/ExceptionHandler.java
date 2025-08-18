package util;

import org.json.JSONObject;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.Logger;
import java.util.logging.Level;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.ServletException;
import java.io.IOException;
import java.sql.SQLException;

/**
 * Comprehensive exception handling utility for centralized error management
 */
public class ExceptionHandler {
    
    private static final Logger LOGGER = Logger.getLogger(ExceptionHandler.class.getName());
    
    // Custom exception classes
    public static class ValidationException extends Exception {
        public ValidationException(String message) {
            super(message);
        }
        
        public ValidationException(String message, Throwable cause) {
            super(message, cause);
        }
    }
    
    public static class DatabaseException extends Exception {
        public DatabaseException(String message) {
            super(message);
        }
        
        public DatabaseException(String message, Throwable cause) {
            super(message, cause);
        }
    }
    
    public static class AuthenticationException extends Exception {
        public AuthenticationException(String message) {
            super(message);
        }
        
        public AuthenticationException(String message, Throwable cause) {
            super(message, cause);
        }
    }
    
    public static class AuthorizationException extends Exception {
        public AuthorizationException(String message) {
            super(message);
        }
        
        public AuthorizationException(String message, Throwable cause) {
            super(message, cause);
        }
    }
    
    public static class BusinessLogicException extends Exception {
        public BusinessLogicException(String message) {
            super(message);
        }
        
        public BusinessLogicException(String message, Throwable cause) {
            super(message, cause);
        }
    }
    
    /**
     * Handle exceptions and provide appropriate response
     */
    public static void handleException(Exception e, HttpServletRequest request, 
                                     HttpServletResponse response) throws ServletException, IOException {
        
        String errorMessage;
        int statusCode;
        String errorType;
        
        // Determine exception type and appropriate response
        if (e instanceof ValidationException) {
            errorMessage = e.getMessage();
            statusCode = HttpServletResponse.SC_BAD_REQUEST;
            errorType = "VALIDATION_ERROR";
            LOGGER.log(Level.WARNING, "Validation error: " + errorMessage, e);
            
        } else if (e instanceof AuthenticationException) {
            errorMessage = "Authentication failed. Please login again.";
            statusCode = HttpServletResponse.SC_UNAUTHORIZED;
            errorType = "AUTHENTICATION_ERROR";
            LOGGER.log(Level.WARNING, "Authentication error: " + e.getMessage(), e);
            
        } else if (e instanceof AuthorizationException) {
            errorMessage = "Access denied. Insufficient permissions.";
            statusCode = HttpServletResponse.SC_FORBIDDEN;
            errorType = "AUTHORIZATION_ERROR";
            LOGGER.log(Level.WARNING, "Authorization error: " + e.getMessage(), e);
            
        } else if (e instanceof DatabaseException || e instanceof SQLException) {
            errorMessage = "Database operation failed. Please try again.";
            statusCode = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
            errorType = "DATABASE_ERROR";
            LOGGER.log(Level.SEVERE, "Database error: " + e.getMessage(), e);
            
        } else if (e instanceof BusinessLogicException) {
            errorMessage = e.getMessage();
            statusCode = HttpServletResponse.SC_BAD_REQUEST;
            errorType = "BUSINESS_LOGIC_ERROR";
            LOGGER.log(Level.WARNING, "Business logic error: " + errorMessage, e);
            
        } else {
            errorMessage = "An unexpected error occurred. Please try again.";
            statusCode = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
            errorType = "INTERNAL_SERVER_ERROR";
            LOGGER.log(Level.SEVERE, "Unexpected error: " + e.getMessage(), e);
        }
        
        // Check if this is an AJAX request
        String contentType = request.getContentType();
        String acceptHeader = request.getHeader("Accept");
        boolean isAjaxRequest = "XMLHttpRequest".equals(request.getHeader("X-Requested-With")) ||
                               (acceptHeader != null && acceptHeader.contains("application/json")) ||
                               (contentType != null && contentType.contains("application/json"));
        
        if (isAjaxRequest) {
            handleAjaxException(response, errorMessage, statusCode, errorType, e);
        } else {
            handleWebException(request, response, errorMessage, statusCode, errorType, e);
        }
    }
    
    /**
     * Handle AJAX exception with JSON response
     */
    private static void handleAjaxException(HttpServletResponse response, String errorMessage, 
                                          int statusCode, String errorType, Exception e) throws IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(statusCode);
        
        JSONObject errorResponse = new JSONObject();
        errorResponse.put("success", false);
        errorResponse.put("error", true);
        errorResponse.put("message", errorMessage);
        errorResponse.put("type", errorType);
        errorResponse.put("timestamp", System.currentTimeMillis());
        
        // Include stack trace in development mode
        if (isDevelopmentMode()) {
            errorResponse.put("stackTrace", getStackTrace(e));
        }
        
        PrintWriter out = response.getWriter();
        out.print(errorResponse.toString());
        out.flush();
        out.close();
    }
    
    /**
     * Handle web exception with JSP error page
     */
    private static void handleWebException(HttpServletRequest request, HttpServletResponse response, 
                                         String errorMessage, int statusCode, String errorType, 
                                         Exception e) throws ServletException, IOException {
        
        // Set error attributes for JSP page
        request.setAttribute("errorMessage", errorMessage);
        request.setAttribute("errorType", errorType);
        request.setAttribute("statusCode", statusCode);
        request.setAttribute("timestamp", new java.util.Date());
        
        if (isDevelopmentMode()) {
            request.setAttribute("stackTrace", getStackTrace(e));
            request.setAttribute("exception", e);
        }
        
        // Forward to error page
        response.setStatus(statusCode);
        request.getRequestDispatcher("/jsp/error.jsp").forward(request, response);
    }
    
    /**
     * Log exception with context information
     */
    public static void logException(Exception e, String context) {
        logException(e, context, null);
    }
    
    /**
     * Log exception with context and user information
     */
    public static void logException(Exception e, String context, String userId) {
        String logMessage = String.format("Context: %s", context);
        if (userId != null) {
            logMessage += String.format(", User: %s", userId);
        }
        
        Level logLevel = Level.SEVERE;
        if (e instanceof ValidationException || e instanceof BusinessLogicException) {
            logLevel = Level.WARNING;
        } else if (e instanceof AuthenticationException || e instanceof AuthorizationException) {
            logLevel = Level.WARNING;
        }
        
        LOGGER.log(logLevel, logMessage, e);
    }
    
    /**
     * Create error response JSON
     */
    public static JSONObject createErrorResponse(String message) {
        return createErrorResponse(message, "ERROR", null);
    }
    
    /**
     * Create error response JSON with type
     */
    public static JSONObject createErrorResponse(String message, String type) {
        return createErrorResponse(message, type, null);
    }
    
    /**
     * Create detailed error response JSON
     */
    public static JSONObject createErrorResponse(String message, String type, Exception e) {
        JSONObject response = new JSONObject();
        response.put("success", false);
        response.put("error", true);
        response.put("message", message);
        response.put("type", type);
        response.put("timestamp", System.currentTimeMillis());
        
        if (e != null && isDevelopmentMode()) {
            response.put("stackTrace", getStackTrace(e));
        }
        
        return response;
    }
    
    /**
     * Create success response JSON
     */
    public static JSONObject createSuccessResponse(String message) {
        return createSuccessResponse(message, null);
    }
    
    /**
     * Create success response JSON with data
     */
    public static JSONObject createSuccessResponse(String message, Object data) {
        JSONObject response = new JSONObject();
        response.put("success", true);
        response.put("error", false);
        response.put("message", message);
        response.put("timestamp", System.currentTimeMillis());
        
        if (data != null) {
            response.put("data", data);
        }
        
        return response;
    }
    
    /**
     * Get stack trace as string
     */
    public static String getStackTrace(Exception e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        return sw.toString();
    }
    
    /**
     * Check if running in development mode
     */
    private static boolean isDevelopmentMode() {
        // Check system property or environment variable
        String environment = System.getProperty("app.environment", "development");
        return "development".equalsIgnoreCase(environment) || "dev".equalsIgnoreCase(environment);
    }
    
    /**
     * Validate and throw ValidationException if invalid
     */
    public static void validateRequired(String value, String fieldName) throws ValidationException {
        if (value == null || value.trim().isEmpty()) {
            throw new ValidationException(fieldName + " is required");
        }
    }
    
    /**
     * Validate email and throw ValidationException if invalid
     */
    public static void validateEmail(String email) throws ValidationException {
        if (!SecurityUtil.isValidEmail(email)) {
            throw new ValidationException("Invalid email format");
        }
    }
    
    /**
     * Validate positive integer and throw ValidationException if invalid
     */
    public static void validatePositiveInteger(String value, String fieldName) throws ValidationException {
        if (!ValidationUtil.isValidPositiveInteger(value)) {
            throw new ValidationException(fieldName + " must be a positive number");
        }
    }
    
    /**
     * Validate positive decimal and throw ValidationException if invalid
     */
    public static void validatePositiveDecimal(String value, String fieldName) throws ValidationException {
        if (!ValidationUtil.isValidPositiveDecimal(value)) {
            throw new ValidationException(fieldName + " must be a positive decimal number");
        }
    }
    
    /**
     * Handle database exceptions
     */
    public static void handleDatabaseException(SQLException e, String operation) throws DatabaseException {
        String message = String.format("Database error during %s: %s", operation, e.getMessage());
        LOGGER.log(Level.SEVERE, message, e);
        
        // Don't expose internal database errors to users
        throw new DatabaseException("Database operation failed. Please try again.");
    }
    
    /**
     * Handle authentication failure
     */
    public static void handleAuthenticationFailure(String username) throws AuthenticationException {
        String message = "Authentication failed for user: " + username;
        LOGGER.log(Level.WARNING, message);
        throw new AuthenticationException("Invalid username or password");
    }
    
    /**
     * Handle authorization failure
     */
    public static void handleAuthorizationFailure(String username, String resource) throws AuthorizationException {
        String message = String.format("Access denied for user %s to resource %s", username, resource);
        LOGGER.log(Level.WARNING, message);
        throw new AuthorizationException("Access denied. Insufficient permissions.");
    }
    
    /**
     * Wrap and rethrow checked exceptions as runtime exceptions
     */
    public static RuntimeException wrapException(Exception e) {
        if (e instanceof RuntimeException) {
            return (RuntimeException) e;
        }
        return new RuntimeException(e.getMessage(), e);
    }
    
    /**
     * Safe execution with exception handling
     */
    public static <T> T safeExecute(SafeSupplier<T> supplier, T defaultValue) {
        try {
            return supplier.get();
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Safe execution failed, returning default value", e);
            return defaultValue;
        }
    }
    
    /**
     * Safe execution without return value
     */
    public static void safeExecute(SafeRunnable runnable) {
        try {
            runnable.run();
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Safe execution failed", e);
        }
    }
    
    // Functional interfaces for safe execution
    @FunctionalInterface
    public interface SafeSupplier<T> {
        T get() throws Exception;
    }
    
    @FunctionalInterface
    public interface SafeRunnable {
        void run() throws Exception;
    }
    
    /**
     * Format error message for user display
     */
    public static String formatUserErrorMessage(String technicalMessage) {
        // Convert technical messages to user-friendly ones
        if (technicalMessage.contains("duplicate") || technicalMessage.contains("unique")) {
            return "This record already exists. Please use different values.";
        }
        if (technicalMessage.contains("foreign key") || technicalMessage.contains("constraint")) {
            return "Cannot perform this operation due to related data constraints.";
        }
        if (technicalMessage.contains("connection") || technicalMessage.contains("timeout")) {
            return "Service temporarily unavailable. Please try again.";
        }
        
        // Return generic message for unknown technical errors
        return "An error occurred. Please try again or contact support if the problem persists.";
    }
}
