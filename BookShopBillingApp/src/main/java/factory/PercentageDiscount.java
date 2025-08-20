package factory;

public class PercentageDiscount implements Discount {
    private final double percentage;
    
    public PercentageDiscount(double percentage) {
        this.percentage = percentage;
    }
    
    @Override
    public double calculateDiscount(double amount) {
        if (percentage < 0 || percentage > 100) {
            throw new IllegalArgumentException("Percentage must be between 0 and 100");
        }
        return amount * (percentage / 100.0);
    }
    
    @Override
    public String getDiscountType() {
        return "PERCENTAGE";
    }
    
    public double getPercentage() {
        return percentage;
    }
}
