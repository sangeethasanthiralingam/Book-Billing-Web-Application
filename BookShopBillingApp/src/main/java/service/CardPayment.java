package service;

/**
 * Strategy Pattern: Card Payment Implementation Handles credit/debit card payment processing
 */
public class CardPayment implements PaymentStrategy {
    private String cardNumber;
    private String cardType; // VISA, MASTERCARD, etc.

    public CardPayment(String cardNumber, String cardType) {
        this.cardNumber = cardNumber;
        this.cardType = cardType;
    }

    @Override
    public String getPaymentDetails() {
        if (!isValidCardNumber()) {
            return "Card Payment - Invalid card number";
        }
        return "Card Payment - Details processed securely";
    }

    @Override
    public boolean processPayment(double amount) {
        // Validate input parameters
        if (amount <= 0) {
            System.out.println("Invalid amount: $" + amount);
            return false;
        }
        
        if (!isValidCardNumber()) {
            System.out.println("Invalid card number provided");
            return false;
        }
        
        // In a real application, this would integrate with payment gateways
        String maskedCardNumber = getMaskedCardNumber();
        System.out.println("Processing " + cardType + " payment of $" + amount
                + " with card ending in " + maskedCardNumber);
        return true; // Simulate successful payment
    }

    @Override
    public String getPaymentMethod() {
        return "CARD";
    }

    @Override
    public String getPaymentDescription() {
        return cardType + " Card Payment";
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public String getCardType() {
        return cardType;
    }
    
    /**
     * Validates if the card number is valid
     * @return true if card number is valid, false otherwise
     */
    private boolean isValidCardNumber() {
        return cardNumber != null && 
               !cardNumber.trim().isEmpty() && 
               cardNumber.length() >= 4 &&
               cardNumber.matches("\\d+"); // Only digits
    }
    
    /**
     * Gets the last 4 digits of the card number safely
     * @return last 4 digits or the entire number if less than 4 digits
     */
    private String getMaskedCardNumber() {
        if (cardNumber == null || cardNumber.length() < 4) {
            return "****";
        }
        return cardNumber.substring(cardNumber.length() - 4);
    }
}