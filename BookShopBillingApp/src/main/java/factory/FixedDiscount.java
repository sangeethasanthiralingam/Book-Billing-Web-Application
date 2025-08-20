package factory;

public class FixedDiscount implements Discount {
    private final double fixedAmount;
    
    public FixedDiscount(double fixedAmount) {
        this.fixedAmount = fixedAmount;
    }
    
    @Override
    public double calculateDiscount(double amount) {
        if (fixedAmount < 0) {
            throw new IllegalArgumentException("Fixed discount amount cannot be negative");
        }
        return Math.min(fixedAmount, amount); // Discount cannot exceed the total amount
    }
    
    @Override
    public String getDiscountType() {
        return "FIXED";
    }
    
    public double getFixedAmount() {
        return fixedAmount;
    }
}
