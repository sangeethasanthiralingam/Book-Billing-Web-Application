package factory;

public class FixedDiscount implements Discount {
    private double fixedAmount;
    
    public FixedDiscount(double fixedAmount) {
        this.fixedAmount = fixedAmount;
    }
    
    @Override
    public double calculateDiscount(double amount) {
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