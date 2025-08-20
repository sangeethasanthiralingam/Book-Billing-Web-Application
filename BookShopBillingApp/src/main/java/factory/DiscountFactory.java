package  factory;
/**
 * Factory Pattern: Discount Factory
 * Creates different types of discount objects based on type and parameters
 */
public class DiscountFactory {
    
    /**
     * Create discount based on type and value
     * @param type the discount type (PERCENTAGE, FIXED)
     * @param value the discount value
     * @return Discount object
     * @throws IllegalArgumentException if discount type is unknown
     */
    public static Discount createDiscount(String type, double value) {
        if (type == null || type.trim().isEmpty()) {
            throw new IllegalArgumentException("Discount type cannot be null or empty");
        }
        
        switch (type.toUpperCase()) {
            case "PERCENTAGE" -> {
                return new PercentageDiscount(value);
            }
            case "FIXED" -> {
                return new FixedDiscount(value);
            }
            default -> throw new IllegalArgumentException("Unknown discount type: " + type);
        }
    }
    
    /**
     * Create percentage discount
     * @param percentage the percentage value (0-100)
     * @return PercentageDiscount object
     */
    public static Discount createPercentageDiscount(double percentage) {
        if (percentage < 0 || percentage > 100) {
            throw new IllegalArgumentException("Percentage must be between 0 and 100");
        }
        return new PercentageDiscount(percentage);
    }
    
    /**
     * Create fixed amount discount
     * @param amount the fixed discount amount
     * @return FixedDiscount object
     */
    public static Discount createFixedDiscount(double amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Fixed discount amount cannot be negative");
        }
        return new FixedDiscount(amount);
    }
    
    /**
     * Create no discount (null object pattern)
     * @return Discount object that returns 0 discount
     */
    public static Discount createNoDiscount() {
        return new Discount() {
            @Override
            public double calculateDiscount(double amount) {
                return 0.0;
            }
            
            @Override
            public String getDiscountType() {
                return "NONE";
            }
        };
    }
} 