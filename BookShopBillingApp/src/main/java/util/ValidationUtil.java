package util;

import org.json.JSONObject;
import org.json.JSONArray;
import java.util.regex.Pattern;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.math.BigDecimal;

/**
 * Comprehensive validation utility class for input validation and data sanitization
 */
public class ValidationUtil {
    
    // Common regex patterns
    private static final Pattern EMAIL_PATTERN = 
        Pattern.compile("^[A-Za-z0-9+_.-]+@([A-Za-z0-9.-]+\\.[A-Za-z]{2,})$");
    
    private static final Pattern PHONE_PATTERN = 
        Pattern.compile("^[+]?[0-9]{10,15}$");
    
    private static final Pattern USERNAME_PATTERN = 
        Pattern.compile("^[a-zA-Z0-9_]{3,20}$");
    
    private static final Pattern STRONG_PASSWORD_PATTERN = 
        Pattern.compile("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$");
    
    private static final Pattern ISBN_PATTERN = 
        Pattern.compile("^(978|979)[0-9]{10}$|^[0-9]{9}[0-9X]$");
    
    private static final Pattern ACCOUNT_NUMBER_PATTERN = 
        Pattern.compile("^ACC[0-9]{8}$");
    
    private static final Pattern BILL_NUMBER_PATTERN = 
        Pattern.compile("^BILL[0-9]{6}[0-9]{4}$");
    
    /**
     * Validation result class
     */
    public static class ValidationResult {
        private boolean valid;
        private List<String> errors;
        
        public ValidationResult() {
            this.valid = true;
            this.errors = new ArrayList<>();
        }
        
        public boolean isValid() { return valid; }
        public List<String> getErrors() { return errors; }
        
        public void addError(String error) {
            this.valid = false;
            this.errors.add(error);
        }
        
        public String getFirstError() {
            return errors.isEmpty() ? null : errors.get(0);
        }
        
        public JSONObject toJSON() {
            JSONObject json = new JSONObject();
            json.put("valid", valid);
            json.put("errors", new JSONArray(errors));
            return json;
        }
    }
    
    // Basic validation methods
    
    /**
     * Check if string is not null and not empty
     */
    public static boolean isNotEmpty(String value) {
        return value != null && !value.trim().isEmpty();
    }
    
    /**
     * Check if string length is within specified range
     */
    public static boolean isValidLength(String value, int minLength, int maxLength) {
        if (value == null) return false;
        int length = value.trim().length();
        return length >= minLength && length <= maxLength;
    }
    
    /**
     * Check if value is a valid integer
     */
    public static boolean isValidInteger(String value) {
        if (!isNotEmpty(value)) return false;
        try {
            Integer.parseInt(value.trim());
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    /**
     * Check if value is a valid positive integer
     */
    public static boolean isValidPositiveInteger(String value) {
        if (!isValidInteger(value)) return false;
        try {
            return Integer.parseInt(value.trim()) > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    /**
     * Check if value is a valid decimal number
     */
    public static boolean isValidDecimal(String value) {
        if (!isNotEmpty(value)) return false;
        try {
            new BigDecimal(value.trim());
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    /**
     * Check if value is a valid positive decimal
     */
    public static boolean isValidPositiveDecimal(String value) {
        if (!isValidDecimal(value)) return false;
        try {
            return new BigDecimal(value.trim()).compareTo(BigDecimal.ZERO) > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    // Specific validation methods
    
    /**
     * Validate email address
     */
    public static boolean isValidEmail(String email) {
        return isNotEmpty(email) && EMAIL_PATTERN.matcher(email.trim()).matches();
    }
    
    /**
     * Validate phone number
     */
    public static boolean isValidPhoneNumber(String phone) {
        return isNotEmpty(phone) && PHONE_PATTERN.matcher(phone.trim()).matches();
    }
    
    /**
     * Validate username
     */
    public static boolean isValidUsername(String username) {
        return isNotEmpty(username) && USERNAME_PATTERN.matcher(username.trim()).matches();
    }
    
    /**
     * Validate strong password
     */
    public static boolean isStrongPassword(String password) {
        return isNotEmpty(password) && STRONG_PASSWORD_PATTERN.matcher(password).matches();
    }
    
    /**
     * Validate ISBN
     */
    public static boolean isValidISBN(String isbn) {
        return isNotEmpty(isbn) && ISBN_PATTERN.matcher(isbn.trim()).matches();
    }
    
    /**
     * Validate account number
     */
    public static boolean isValidAccountNumber(String accountNumber) {
        return isNotEmpty(accountNumber) && ACCOUNT_NUMBER_PATTERN.matcher(accountNumber.trim()).matches();
    }
    
    /**
     * Validate bill number
     */
    public static boolean isValidBillNumber(String billNumber) {
        return isNotEmpty(billNumber) && BILL_NUMBER_PATTERN.matcher(billNumber.trim()).matches();
    }
    
    /**
     * Validate role
     */
    public static boolean isValidRole(String role) {
        return isNotEmpty(role) && 
               ("ADMIN".equals(role) || "CASHIER".equals(role) || "CUSTOMER".equals(role));
    }
    
    // Form validation methods
    
    /**
     * Validate user registration form
     */
    public static ValidationResult validateUserRegistration(Map<String, String> formData) {
        ValidationResult result = new ValidationResult();
        
        // Username validation
        String username = formData.get("username");
        if (!isNotEmpty(username)) {
            result.addError("Username is required");
        } else if (!isValidUsername(username)) {
            result.addError("Username must be 3-20 characters long and contain only letters, numbers, and underscores");
        }
        
        // Password validation
        String password = formData.get("password");
        if (!isNotEmpty(password)) {
            result.addError("Password is required");
        } else if (!isStrongPassword(password)) {
            result.addError("Password must be at least 8 characters long and contain uppercase, lowercase, number, and special character");
        }
        
        // Confirm password validation
        String confirmPassword = formData.get("confirmPassword");
        if (!isNotEmpty(confirmPassword)) {
            result.addError("Password confirmation is required");
        } else if (!password.equals(confirmPassword)) {
            result.addError("Passwords do not match");
        }
        
        // Email validation
        String email = formData.get("email");
        if (!isNotEmpty(email)) {
            result.addError("Email is required");
        } else if (!isValidEmail(email)) {
            result.addError("Invalid email format");
        }
        
        // Full name validation
        String fullName = formData.get("fullName");
        if (!isNotEmpty(fullName)) {
            result.addError("Full name is required");
        } else if (!isValidLength(fullName, 2, 100)) {
            result.addError("Full name must be between 2 and 100 characters");
        }
        
        // Phone validation (optional)
        String phone = formData.get("phone");
        if (isNotEmpty(phone) && !isValidPhoneNumber(phone)) {
            result.addError("Invalid phone number format");
        }
        
        // Role validation
        String role = formData.get("role");
        if (!isNotEmpty(role)) {
            result.addError("Role is required");
        } else if (!isValidRole(role)) {
            result.addError("Invalid role selected");
        }
        
        return result;
    }
    
    /**
     * Validate user update form
     */
    public static ValidationResult validateUserUpdate(Map<String, String> formData) {
        ValidationResult result = new ValidationResult();
        
        // Username validation (required for updates)
        String username = formData.get("username");
        if (!isNotEmpty(username)) {
            result.addError("Username is required");
        } else if (!isValidUsername(username)) {
            result.addError("Username must be 3-20 characters long and contain only letters, numbers, and underscores");
        }
        
        // Password validation (optional for updates)
        String password = formData.get("password");
        if (isNotEmpty(password) && !isStrongPassword(password)) {
            result.addError("Password must be at least 8 characters long and contain uppercase, lowercase, number, and special character");
        }
        
        // Email validation
        String email = formData.get("email");
        if (!isNotEmpty(email)) {
            result.addError("Email is required");
        } else if (!isValidEmail(email)) {
            result.addError("Invalid email format");
        }
        
        // Full name validation
        String fullName = formData.get("fullName");
        if (!isNotEmpty(fullName)) {
            result.addError("Full name is required");
        } else if (!isValidLength(fullName, 2, 100)) {
            result.addError("Full name must be between 2 and 100 characters");
        }
        
        // Phone validation (optional)
        String phone = formData.get("phone");
        if (isNotEmpty(phone) && !isValidPhoneNumber(phone)) {
            result.addError("Invalid phone number format");
        }
        
        // Role validation
        String role = formData.get("role");
        if (!isNotEmpty(role)) {
            result.addError("Role is required");
        } else if (!isValidRole(role)) {
            result.addError("Invalid role selected");
        }
        
        return result;
    }
    
    /**
     * Validate book form
     */
    public static ValidationResult validateBook(Map<String, String> formData) {
        ValidationResult result = new ValidationResult();
        
        // Title validation
        String title = formData.get("title");
        if (!isNotEmpty(title)) {
            result.addError("Book title is required");
        } else if (!isValidLength(title, 1, 255)) {
            result.addError("Book title must be between 1 and 255 characters");
        }
        
        // Author validation
        String author = formData.get("author");
        if (!isNotEmpty(author)) {
            result.addError("Author is required");
        } else if (!isValidLength(author, 1, 255)) {
            result.addError("Author name must be between 1 and 255 characters");
        }
        
        // ISBN validation (optional)
        String isbn = formData.get("isbn");
        if (isNotEmpty(isbn) && !isValidISBN(isbn)) {
            result.addError("Invalid ISBN format. Use 10 or 13 digit ISBN");
        }
        
        // Price validation
        String price = formData.get("price");
        if (!isNotEmpty(price)) {
            result.addError("Price is required");
        } else if (!isValidPositiveDecimal(price)) {
            result.addError("Price must be a positive number");
        }
        
        // Quantity validation
        String quantity = formData.get("quantity");
        if (!isNotEmpty(quantity)) {
            result.addError("Quantity is required");
        } else if (!isValidInteger(quantity)) {
            result.addError("Quantity must be a valid number");
        } else if (Integer.parseInt(quantity) < 0) {
            result.addError("Quantity cannot be negative");
        }
        
        // Category validation (optional)
        String category = formData.get("category");
        if (isNotEmpty(category) && !isValidLength(category, 1, 100)) {
            result.addError("Category must be between 1 and 100 characters");
        }
        
        return result;
    }
    
    /**
     * Validate billing form
     */
    public static ValidationResult validateBilling(Map<String, String> formData) {
        ValidationResult result = new ValidationResult();
        
        // Customer ID validation
        String customerId = formData.get("customerId");
        if (!isNotEmpty(customerId)) {
            result.addError("Customer selection is required");
        } else if (!isValidPositiveInteger(customerId)) {
            result.addError("Invalid customer selected");
        }
        
        // Payment method validation
        String paymentMethod = formData.get("paymentMethod");
        if (!isNotEmpty(paymentMethod)) {
            result.addError("Payment method is required");
        } else if (!isValidPaymentMethod(paymentMethod)) {
            result.addError("Invalid payment method selected");
        }
        
        // Discount validation (optional)
        String discount = formData.get("discount");
        if (isNotEmpty(discount) && !isValidPositiveDecimal(discount)) {
            result.addError("Discount must be a positive number");
        }
        
        return result;
    }
    
    /**
     * Validate payment method
     */
    public static boolean isValidPaymentMethod(String paymentMethod) {
        return isNotEmpty(paymentMethod) && 
               ("CASH".equals(paymentMethod) || "CARD".equals(paymentMethod) || "UPI".equals(paymentMethod));
    }
    
    /**
     * Validate search parameters
     */
    public static ValidationResult validateSearchParameters(Map<String, String> params) {
        ValidationResult result = new ValidationResult();
        
        String query = params.get("q");
        if (isNotEmpty(query)) {
            if (SecurityUtil.containsSQLInjection(query)) {
                result.addError("Invalid characters in search query");
            }
            if (!isValidLength(query, 1, 100)) {
                result.addError("Search query must be between 1 and 100 characters");
            }
        }
        
        return result;
    }
    
    /**
     * Sanitize and validate form data
     */
    public static Map<String, String> sanitizeFormData(Map<String, String> formData) {
        Map<String, String> sanitized = new HashMap<>();
        
        for (Map.Entry<String, String> entry : formData.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            
            if (value != null) {
                // Don't sanitize passwords
                if (key.toLowerCase().contains("password")) {
                    sanitized.put(key, value.trim());
                } else {
                    sanitized.put(key, SecurityUtil.sanitizeInput(value.trim()));
                }
            }
        }
        
        return sanitized;
    }
    
    /**
     * Validate required fields
     */
    public static ValidationResult validateRequiredFields(Map<String, String> formData, String... requiredFields) {
        ValidationResult result = new ValidationResult();
        
        for (String field : requiredFields) {
            if (!isNotEmpty(formData.get(field))) {
                result.addError(capitalizeFirst(field) + " is required");
            }
        }
        
        return result;
    }
    
    /**
     * Validate numeric range
     */
    public static boolean isWithinRange(String value, double min, double max) {
        if (!isValidDecimal(value)) return false;
        try {
            double numValue = Double.parseDouble(value.trim());
            return numValue >= min && numValue <= max;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    /**
     * Validate date format (YYYY-MM-DD)
     */
    public static boolean isValidDate(String date) {
        if (!isNotEmpty(date)) return false;
        return date.matches("^\\d{4}-\\d{2}-\\d{2}$");
    }
    
    // Helper methods
    
    private static String capitalizeFirst(String str) {
        if (str == null || str.isEmpty()) return str;
        return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
    }
    
    /**
     * Convert form data to JSON for easy validation result handling
     */
    public static JSONObject createValidationResponse(ValidationResult result) {
        JSONObject response = new JSONObject();
        response.put("success", result.isValid());
        response.put("valid", result.isValid());
        
        if (!result.isValid()) {
            response.put("errors", new JSONArray(result.getErrors()));
            response.put("message", result.getFirstError());
        }
        
        return response;
    }
    
    /**
     * Validate configuration settings
     */
    public static ValidationResult validateSystemConfig(Map<String, String> configData) {
        ValidationResult result = new ValidationResult();
        
        // Tax rate validation
        String taxRate = configData.get("taxRate");
        if (isNotEmpty(taxRate)) {
            if (!isValidDecimal(taxRate)) {
                result.addError("Tax rate must be a valid decimal number");
            } else if (!isWithinRange(taxRate, 0, 100)) {
                result.addError("Tax rate must be between 0 and 100");
            }
        }
        
        // Default discount validation
        String defaultDiscount = configData.get("defaultDiscount");
        if (isNotEmpty(defaultDiscount)) {
            if (!isValidDecimal(defaultDiscount)) {
                result.addError("Default discount must be a valid decimal number");
            } else if (!isWithinRange(defaultDiscount, 0, 100)) {
                result.addError("Default discount must be between 0 and 100");
            }
        }
        
        return result;
    }
}
