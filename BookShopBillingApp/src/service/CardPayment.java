package service;

public class CardPayment implements PaymentStrategy {
    private String cardNumber;
    private String cardType; // VISA, MASTERCARD, etc.
    
    public CardPayment(String cardNumber, String cardType) {
        this.cardNumber = cardNumber;
        this.cardType = cardType;
    }
    
    @Override
    public boolean processPayment(double amount) {
        // In a real application, this would integrate with payment gateways
        System.out.println("Processing " + cardType + " payment of $" + amount + " with card ending in " + 
                          cardNumber.substring(cardNumber.length() - 4));
        return true; // Simulate successful payment
    }
    
    @Override
    public String getPaymentMethod() {
        return "CARD";
    }
    
    public String getCardNumber() {
        return cardNumber;
    }
    
    public String getCardType() {
        return cardType;
    }
} 