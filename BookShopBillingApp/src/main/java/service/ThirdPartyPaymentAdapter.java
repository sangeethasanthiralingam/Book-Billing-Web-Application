package service;

public class ThirdPartyPaymentAdapter implements PaymentStrategy {
    private ThirdPartyPayment thirdPartyPayment = new ThirdPartyPayment();

    @Override
    public boolean processPayment(double amount) {
        return thirdPartyPayment.makePayment(amount);
    }

    @Override
    public String getPaymentMethod() {
        return "THIRD_PARTY";
    }

    @Override
    public String getPaymentDescription() {
        return "Third Party Payment";
    }
} 