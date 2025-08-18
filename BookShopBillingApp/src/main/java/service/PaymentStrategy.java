package service;

/**
 * Strategy Pattern: Payment Strategy Interface
 * Defines the contract for different payment methods
 */
public interface PaymentStrategy {
    /**
     * Process payment for the given amount
     * @param amount the amount to be paid
     * @return true if payment was successful, false otherwise
     */
    boolean processPayment(double amount);
    
    /**
     * Get the payment method name
     * @return the payment method identifier
     */
    String getPaymentMethod();
    
    /**
     * Get payment method description
     * @return human-readable description of the payment method
     */
    String getPaymentDescription();
} 