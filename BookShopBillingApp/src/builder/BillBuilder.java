package builder;

import model.Bill;
import model.User;
import model.BillItem;
import model.Customer;
import java.util.Date;

public class BillBuilder {
    private Bill bill;
    
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
    
    public BillBuilder withCustomer(Customer customer) {
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
    
    public Bill build() {
        return bill;
    }
    
    // Convenience methods
    public static BillBuilder createNewBill() {
        return new BillBuilder()
                .withBillDate(new Date())
                .withStatus("PENDING");
    }
    
    public static BillBuilder createNewBill(String billNumber, Customer customer, User cashier) {
        return createNewBill()
                .withBillNumber(billNumber)
                .withCustomer(customer)
                .withCashier(cashier);
    }
} 