package service;

public class PaymentService {
    private PaymentStrategy paymentStrategy;
    
    public PaymentService(PaymentStrategy paymentStrategy) {
        this.paymentStrategy = paymentStrategy;
    }
    
    public void setPaymentStrategy(PaymentStrategy paymentStrategy) {
        this.paymentStrategy = paymentStrategy;
    }
    
    public boolean processPayment(double amount) {
        if (paymentStrategy == null) {
            throw new IllegalStateException("Payment strategy not set");
        }
        return paymentStrategy.processPayment(amount);
    }
    
    public String getPaymentMethod() {
        return paymentStrategy != null ? paymentStrategy.getPaymentMethod() : "NONE";
    }
    
    // Factory methods for creating payment strategies
    public static PaymentStrategy createCashPayment() {
        return new CashPayment();
    }
    
    public static PaymentStrategy createCardPayment(String cardNumber, String cardType) {
        return new CardPayment(cardNumber, cardType);
    }
    
    public static PaymentStrategy createUpiPayment(String upiId) {
        return new UpiPayment(upiId);
    }
} 