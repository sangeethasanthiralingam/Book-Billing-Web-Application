package factory;

public class PercentageDiscount implements Discount {
    private double percentage;
    
    public PercentageDiscount(double percentage) {
        this.percentage = percentage;
    }
    
    @Override
    public double calculateDiscount(double amount) {
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