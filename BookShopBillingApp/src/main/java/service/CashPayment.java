package service;

/**
 * Strategy Pattern: Cash Payment Implementation
 * Handles cash payment processing
 */
public class CashPayment implements PaymentStrategy {
    
    @Override
    public boolean processPayment(double amount) {
        // Cash payment should be positive
        if (amount <= 0) {
            System.out.println("Invalid cash payment amount: " + amount);
            return false;
        }
        System.out.println("Processing cash payment of $" + amount);
        return true;
    }
    
    @Override
    public String getPaymentMethod() {
        return "CASH";
    }
    
    @Override
    public String getPaymentDescription() {
        return "Cash Payment";
    }
    
    @Override
    public String getPaymentDetails() {
        return "Cash Payment - No additional details required";
    }
}