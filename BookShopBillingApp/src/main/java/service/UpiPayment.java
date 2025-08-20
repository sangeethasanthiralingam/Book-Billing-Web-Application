package service;

/**
 * Strategy Pattern: UPI Payment Implementation
 * Handles UPI (Unified Payment Interface) payment processing
 */
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
public String getPaymentDetails() {
    return "UPI Payment - Digital transaction completed";
}
    @Override
    public String getPaymentMethod() {
        return "UPI";
    }
    
    @Override
    public String getPaymentDescription() {
        return "UPI Payment to " + upiId;
    }
    
    public String getUpiId() {
        return upiId;
    }
} 