package service;

/**
 * Strategy Pattern: Card Payment Implementation Handles credit/debit card payment processing
 */
public class CardPayment implements PaymentStrategy {
    private String cardNumber;
    private String cardType;

    public CardPayment(String cardNumber, String cardType) {
        this.cardNumber = cardNumber;
        this.cardType = cardType;
    }

    @Override
    public boolean processPayment(double amount) {
        if (amount <= 0) {
            System.out.println("Invalid card payment amount: " + amount);
            return false;
        }
        if (!isValidCard(cardNumber)) {
            System.out.println("Invalid card number provided");
            return false;
        }
        System.out.println("Processing card payment of $" + amount + " using " + cardType);
        return true;
    }

    private boolean isValidCard(String cardNumber) {
        // Simple validation: non-null, 13-19 digits
        return cardNumber != null && cardNumber.matches("\\d{13,19}");
    }

    @Override
    public String getPaymentDetails() {
        return "Card Payment: " + cardType + " ending with " + 
               (cardNumber.length() >= 4 ? cardNumber.substring(cardNumber.length() - 4) : cardNumber);
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
    
}