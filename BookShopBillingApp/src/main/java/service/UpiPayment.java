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
        if (amount <= 0) {
            System.out.println("Invalid payment amount: " + amount);
            return false;
        }

        if (!isValidUpiId(upiId)) {
            System.out.println("Invalid UPI ID: " + upiId);
            return false;
        }

        // Simulate UPI payment processing
        System.out.println("Processing UPI payment of $" + amount + " to " + upiId);
        return true;
    }

    private boolean isValidUpiId(String upiId) {
        // Simple validation: must contain '@' and non-empty parts
        return upiId != null && upiId.matches(".+@.+");
    }

    @Override
    public String getPaymentDetails() {
        return "UPI Payment to " + upiId;
    }

    @Override
    public String getPaymentMethod() {
        return "UPI";
    }

    @Override
    public String getPaymentDescription() {
        return "Pay via UPI to " + upiId;
    }

    public String getUpiId() {
        return upiId;
    }
}
