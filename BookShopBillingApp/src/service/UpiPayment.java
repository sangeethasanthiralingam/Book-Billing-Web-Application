package service;

public class UpiPayment implements PaymentStrategy {
    private String upiId;
    
    public UpiPayment(String upiId) {
        this.upiId = upiId;
    }
    
    @Override
    public boolean processPayment(double amount) {
        // In a real application, this would integrate with UPI payment systems
        System.out.println("Processing UPI payment of $" + amount + " to " + upiId);
        return true; // Simulate successful payment
    }
    
    @Override
    public String getPaymentMethod() {
        return "UPI";
    }
    
    public String getUpiId() {
        return upiId;
    }
} 