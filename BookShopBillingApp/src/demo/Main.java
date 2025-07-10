package demo;

import model.*;
import dao.*;
import service.*;
import factory.*;
import builder.*;
import util.DBConnection;

import java.util.Date;

public class Main {
    public static void main(String[] args) {
        System.out.println("=== BookShop Billing Application Demo ===\n");
        
        // Test Database Connection
        System.out.println("1. Testing Database Connection:");
        DBConnection dbConnection = DBConnection.getInstance();
        System.out.println("Database connection singleton created successfully.\n");
        
        // Test Model Classes
        System.out.println("2. Testing Model Classes:");
        Book book = new Book(1, "The Great Gatsby", "F. Scott Fitzgerald", "978-0743273565", 12.99, 15, "Fiction");
        User user = new User(1, "john_doe", "password123", "john@email.com", "CUSTOMER", "John Doe", "555-1234");
        System.out.println("Book: " + book.getTitle() + " by " + book.getAuthor());
        System.out.println("User: " + user.getFullName() + " (" + user.getRole() + ")\n");
        
        // Test Payment Strategy Pattern
        System.out.println("3. Testing Payment Strategy Pattern:");
        PaymentService paymentService = new PaymentService(new CashPayment());
        paymentService.processPayment(50.00);
        
        paymentService.setPaymentStrategy(new CardPayment("1234567890123456", "VISA"));
        paymentService.processPayment(75.50);
        
        paymentService.setPaymentStrategy(new UpiPayment("john@upi"));
        paymentService.processPayment(25.00);
        System.out.println();
        
        // Test Factory Pattern for Discounts
        System.out.println("4. Testing Factory Pattern for Discounts:");
        Discount percentageDiscount = DiscountFactory.createPercentageDiscount(10.0);
        Discount fixedDiscount = DiscountFactory.createFixedDiscount(5.0);
        
        double amount = 100.0;
        System.out.println("Original amount: $" + amount);
        System.out.println("Percentage discount (10%): $" + percentageDiscount.calculateDiscount(amount));
        System.out.println("Fixed discount ($5): $" + fixedDiscount.calculateDiscount(amount));
        System.out.println();
        
        // Test Builder Pattern
        System.out.println("5. Testing Builder Pattern:");
        Bill bill = BillBuilder.createNewBill("BILL-2024-001", user, user)
                .addItem(new BillItem(1, book, 2, book.getPrice()))
                .calculateTotals()
                .build();
        
        System.out.println("Bill created: " + bill.getBillNumber());
        System.out.println("Total items: " + bill.getItems().size());
        System.out.println("Subtotal: $" + bill.getSubtotal());
        System.out.println();
        
        // Test DAO Classes (simulated)
        System.out.println("6. Testing DAO Classes:");
        BookDAO bookDAO = new BookDAO();
        UserDAO userDAO = new UserDAO();
        BillDAO billDAO = new BillDAO();
        System.out.println("DAO classes initialized successfully.\n");
        
        System.out.println("=== Demo Completed Successfully ===");
        System.out.println("\nDesign Patterns Implemented:");
        System.out.println("✓ Singleton Pattern (DBConnection)");
        System.out.println("✓ Strategy Pattern (Payment Methods)");
        System.out.println("✓ Factory Pattern (Discount Types)");
        System.out.println("✓ Builder Pattern (Bill Construction)");
        System.out.println("✓ MVC Pattern (Model-View-Controller)");
        System.out.println("✓ DAO Pattern (Data Access Objects)");
    }
} 