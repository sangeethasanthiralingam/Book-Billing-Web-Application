package util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.regex.Pattern;

/**
 * Security utility class for password hashing, validation, and security functions
 */
public class SecurityUtil {
    
    private static final String SALT = "BookShop2024$ecureApp";
    private static final String ALGORITHM = "SHA-256";
    
    // Password validation patterns
    private static final Pattern EMAIL_PATTERN = 
        Pattern.compile("^[A-Za-z0-9+_.-]+@([A-Za-z0-9.-]+\\.[A-Za-z]{2,})$");
    
    private static final Pattern PHONE_PATTERN = 
        Pattern.compile("^[+]?[0-9]{10,15}$");
    
    private static final Pattern STRONG_PASSWORD_PATTERN = 
        Pattern.compile("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$");
    
    /**
     * Hash password using SHA-256 with salt
     */
    public static String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance(ALGORITHM);
            md.update(SALT.getBytes());
            byte[] hashedPassword = md.digest(password.getBytes());
            return Base64.getEncoder().encodeToString(hashedPassword);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error hashing password", e);
        }
    }
    
    /**
     * Verify password against hashed password
     */
    public static boolean verifyPassword(String password, String hashedPassword) {
        try {
            String hashedInput = hashPassword(password);
            return hashedInput.equals(hashedPassword);
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Generate secure random password
     */
    public static String generateSecurePassword(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789@#$%^&+=";
        SecureRandom random = new SecureRandom();
        StringBuilder password = new StringBuilder();
        
        for (int i = 0; i < length; i++) {
            password.append(chars.charAt(random.nextInt(chars.length())));
        }
        
        return password.toString();
    }
    
    /**
     * Validate email format
     */
    public static boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }
    
    /**
     * Validate phone number format
     */
    public static boolean isValidPhoneNumber(String phone) {
        return phone != null && PHONE_PATTERN.matcher(phone).matches();
    }
    
    /**
     * Validate strong password
     */
    public static boolean isStrongPassword(String password) {
        return password != null && STRONG_PASSWORD_PATTERN.matcher(password).matches();
    }
    
    /**
     * Sanitize input to prevent XSS
     */
    public static String sanitizeInput(String input) {
        if (input == null) return null;
        
        return input.replace("<", "&lt;")
                   .replace(">", "&gt;")
                   .replace("\"", "&quot;")
                   .replace("'", "&#x27;")
                   .replace("/", "&#x2F;")
                   .replace("&", "&amp;");
    }
    
    /**
     * Validate input length
     */
    public static boolean isValidLength(String input, int minLength, int maxLength) {
        if (input == null) return false;
        int length = input.trim().length();
        return length >= minLength && length <= maxLength;
    }
    
    /**
     * Generate session token
     */
    public static String generateSessionToken() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[32];
        random.nextBytes(bytes);
        return Base64.getEncoder().encodeToString(bytes);
    }
    
    /**
     * Validate username (alphanumeric and underscore only)
     */
    public static boolean isValidUsername(String username) {
        if (username == null) return false;
        return username.matches("^[a-zA-Z0-9_]{3,20}$");
    }
    
    /**
     * Check if string contains SQL injection patterns
     */
    public static boolean containsSQLInjection(String input) {
        if (input == null) return false;
        
        String[] sqlKeywords = {
            "SELECT", "INSERT", "UPDATE", "DELETE", "DROP", "CREATE", 
            "ALTER", "EXEC", "EXECUTE", "UNION", "SCRIPT", "--", "/*", "*/"
        };
        
        String upperInput = input.toUpperCase();
        for (String keyword : sqlKeywords) {
            if (upperInput.contains(keyword)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Generate account number with prefix
     */
    public static String generateAccountNumber(String prefix, int sequenceNumber) {
        return prefix + String.format("%08d", sequenceNumber);
    }
    
    /**
     * Mask sensitive data (like credit card numbers)
     */
    public static String maskSensitiveData(String data, int visibleChars) {
        if (data == null || data.length() <= visibleChars) {
            return data;
        }
        
        String visible = data.substring(data.length() - visibleChars);
        String masked = "*".repeat(data.length() - visibleChars);
        return masked + visible;
    }
    
    /**
     * Generate secure bill number
     */
    public static String generateSecureBillNumber() {
        String timestamp = String.valueOf(System.currentTimeMillis());
        SecureRandom random = new SecureRandom();
        int randomNum = random.nextInt(9999);
        return "BILL" + timestamp.substring(timestamp.length() - 6) + String.format("%04d", randomNum);
    }
}
