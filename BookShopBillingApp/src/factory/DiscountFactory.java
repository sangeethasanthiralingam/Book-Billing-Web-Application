package factory;

public class DiscountFactory {
    
    public static Discount createDiscount(String type, double value) {
        switch (type.toUpperCase()) {
            case "PERCENTAGE":
                return new PercentageDiscount(value);
            case "FIXED":
                return new FixedDiscount(value);
            default:
                throw new IllegalArgumentException("Unknown discount type: " + type);
        }
    }
    
    public static Discount createPercentageDiscount(double percentage) {
        return new PercentageDiscount(percentage);
    }
    
    public static Discount createFixedDiscount(double amount) {
        return new FixedDiscount(amount);
    }
    
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