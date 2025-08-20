package service;

public class ThirdPartyPayment {
    public boolean makePayment(double amount) {
        System.out.println("Third-party payment of $" + amount + " processed.");
        return true;
    }
} 