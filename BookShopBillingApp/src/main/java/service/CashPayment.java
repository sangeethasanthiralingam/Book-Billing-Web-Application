package service;

/**
 * Strategy Pattern: Cash Payment Implementation
 * Handles cash payment processing
 */
public class CashPayment implements PaymentStrategy {
    
    @Override
    public boolean processPayment(double amount) {
        // In a real application, this would integrate with cash register systems
        System.out.println("Processing cash payment of $" + amount);
        return true; // Simulate successful payment
    }
    
    @Override
    public String getPaymentMethod() {
        return "CASH";
    }
    
    @Override
    public String getPaymentDescription() {
        return "Cash Payment";
    }
} 