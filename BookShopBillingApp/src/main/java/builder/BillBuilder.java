package builder;

import model.*;
import java.util.*;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Builder Pattern: Bill Builder
 * Constructs complex Bill objects step by step
 */
public class BillBuilder {
    private Bill bill;
    
    /**
     * Initialize a new BillBuilder with a fresh Bill instance
     */
    public BillBuilder() {
        this.bill = new Bill();
    }
    
    public BillBuilder withId(int id) {
        bill.setId(id);
        return this;
    }
    
    public BillBuilder withBillNumber(String billNumber) {
        bill.setBillNumber(billNumber);
        return this;
    }
    
    public BillBuilder withBillDate(Date billDate) {
        bill.setBillDate(billDate);
        return this;
    }
    
    public BillBuilder withCustomer(User customer) {  // Changed from Customer to User
        bill.setCustomer(customer);
        return this;
    }
    
    public BillBuilder withCashier(User cashier) {
        bill.setCashier(cashier);
        return this;
    }
    
    public BillBuilder withSubtotal(double subtotal) {
        bill.setSubtotal(subtotal);
        return this;
    }
    
    public BillBuilder withDiscount(double discount) {
        bill.setDiscount(discount);
        return this;
    }
    
    public BillBuilder withTax(double tax) {
        bill.setTax(tax);
        return this;
    }
    
    public BillBuilder withTotal(double total) {
        bill.setTotal(total);
        return this;
    }
    
    public BillBuilder withPaymentMethod(String paymentMethod) {
        bill.setPaymentMethod(paymentMethod);
        return this;
    }
    
    public BillBuilder withStatus(String status) {
        bill.setStatus(status);
        return this;
    }
    
    public BillBuilder addItem(BillItem item) {
        bill.addItem(item);
        return this;
    }
    
    public BillBuilder calculateTotals() {
        bill.calculateTotals();
        return this;
    }
    
    /**
     * Build and return the final Bill object
     * @return the constructed Bill object
     * @throws IllegalStateException if required fields are missing
     */
    public Bill build() {
        // Validate required fields
        if (bill.getBillNumber() == null || bill.getBillNumber().trim().isEmpty()) {
            throw new IllegalStateException("Bill number is required");
        }
        if (bill.getCustomer() == null) {
            throw new IllegalArgumentException("Customer is required");
        }
        if (bill.getCashier() == null) {
            throw new IllegalStateException("Cashier is required");
        }
        
        return bill;
    }
    
    // Convenience methods
    /**
     * Set default status to PENDING
     * @return this BillBuilder instance
     */
    public BillBuilder withDefaultStatus() {
        bill.setStatus("PENDING");
        return this;
    }
    
    /**
     * Set default payment method to CASH
     * @return this BillBuilder instance
     */
    public BillBuilder withDefaultPaymentMethod() {
        bill.setPaymentMethod("CASH");
        return this;
    }
    
    /**
     * Static convenience method for creating new bills
     * @return new BillBuilder instance
     */
    public static BillBuilder createNewBill() {
        return new BillBuilder()
                .withBillNumber("BILL-" + System.currentTimeMillis())
                .withBillDate(new Date())
                .withDefaultStatus()
                .withDefaultPaymentMethod();
    }
    
    /**
     * Static convenience method for creating new bills with parameters
     * @param billNumber the bill number
     * @param customer the customer
     * @param cashier the cashier
     * @return configured BillBuilder instance
     */
    public static BillBuilder createNewBill(String billNumber, User customer, User cashier) {
        if (billNumber == null || billNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("Bill number cannot be null or empty");
        }
        if (customer == null) {
            throw new IllegalArgumentException("Customer cannot be null");
        }
        if (cashier == null) {
            throw new IllegalArgumentException("Cashier cannot be null");
        }
        
        return new BillBuilder()
                .withBillNumber(billNumber)
                .withCustomer(customer)
                .withCashier(cashier)
                .withBillDate(new Date())
                .withDefaultStatus()
                .withDefaultPaymentMethod();
    }
} 