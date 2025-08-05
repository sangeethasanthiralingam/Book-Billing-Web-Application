package model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import memento.BillMemento;

public class Bill implements Serializable {
    private int id;
    private String billNumber;
    private Date billDate;
    private User customer;  // Changed from Customer to User
    private User cashier;
    private List<BillItem> items;
    private double subtotal;
    private double discount;
    private double tax;
    private double total;
    private String paymentMethod;
    private String status; // PENDING, PAID, CANCELLED
    private String notes;
    private String deliveryAddress;
    private double deliveryCharge;
    private boolean isDelivery;
    private int unitsConsumed;
    
    public Bill() {
        this.items = new ArrayList<>();
        this.billDate = new Date();
        this.isDelivery = false;
        this.deliveryCharge = 0.0;
    }
    
    public Bill(int id, String billNumber, User customer, User cashier) {  // Changed parameter type
        this();
        this.id = id;
        this.billNumber = billNumber;
        this.customer = customer;
        this.cashier = cashier;
    }
    
    // Copy constructor for Memento pattern
    public Bill(Bill other) {
        this.id = other.id;
        this.billNumber = other.billNumber;
        this.customer = other.customer; // Consider deep copy if mutable
        this.cashier = other.cashier;   // Consider deep copy if mutable
        this.items = new ArrayList<>(other.items); // Deep copy if needed
        this.subtotal = other.subtotal;
        this.discount = other.discount;
        this.tax = other.tax;
        this.total = other.total;
        this.status = other.status;
        this.paymentMethod = other.paymentMethod;
        this.billDate = other.billDate != null ? new Date(other.billDate.getTime()) : null;
        // Copy other fields as needed
    }
    
    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getBillNumber() { return billNumber; }
    public void setBillNumber(String billNumber) { this.billNumber = billNumber; }
    
    public Date getBillDate() { return billDate; }
    public void setBillDate(Date billDate) { this.billDate = billDate; }
    
    public User getCustomer() { return customer; }  // Changed return type
    public void setCustomer(User customer) { this.customer = customer; }  // Changed parameter type
    
    public User getCashier() { return cashier; }
    public void setCashier(User cashier) { this.cashier = cashier; }
    
    public List<BillItem> getItems() { return items; }
    public void setItems(List<BillItem> items) { this.items = items; }
    
    public double getSubtotal() { return subtotal; }
    public void setSubtotal(double subtotal) { this.subtotal = subtotal; }
    
    public double getDiscount() { return discount; }
    public void setDiscount(double discount) { this.discount = discount; }
    
    public double getTax() { return tax; }
    public void setTax(double tax) { this.tax = tax; }
    
    public double getTotal() { return total; }
    public void setTotal(double total) { this.total = total; }
    
    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    
    public String getDeliveryAddress() { return deliveryAddress; }
    public void setDeliveryAddress(String deliveryAddress) { this.deliveryAddress = deliveryAddress; }
    
    public double getDeliveryCharge() { return deliveryCharge; }
    public void setDeliveryCharge(double deliveryCharge) { this.deliveryCharge = deliveryCharge; }
    
    public boolean isDelivery() { return isDelivery; }
    public void setDelivery(boolean delivery) { this.isDelivery = delivery; }
    
    public int getUnitsConsumed() { return unitsConsumed; }
    public void setUnitsConsumed(int unitsConsumed) { this.unitsConsumed = unitsConsumed; }
    
    // Helper methods
    public void addItem(BillItem item) {
        this.items.add(item);
        calculateTotals();
    }
    
    public void removeItem(BillItem item) {
        this.items.remove(item);
        calculateTotals();
    }
    
    public void calculateTotals() {
        this.subtotal = 0;
        for (BillItem item : items) {
            this.subtotal += item.getTotal();
        }
        this.total = this.subtotal - this.discount + this.tax + this.deliveryCharge;
    }

    public BillMemento saveToMemento() {
        return new BillMemento(this);
    }

    public void restoreFromMemento(BillMemento memento) {
        Bill state = memento.getSavedState();
        this.id = state.id;
        this.billNumber = state.billNumber;
        this.customer = state.customer;
        this.cashier = state.cashier;
        this.items = new ArrayList<>(state.items);
        this.subtotal = state.subtotal;
        this.discount = state.discount;
        this.tax = state.tax;
        this.total = state.total;
        this.status = state.status;
        this.paymentMethod = state.paymentMethod;
        this.billDate = state.billDate != null ? new Date(state.billDate.getTime()) : null;
        // Restore other fields as needed
    }
    
    @Override
    public String toString() {
        return "Bill{" +
                "id=" + id +
                ", billNumber='" + billNumber + '\'' +
                ", billDate=" + billDate +
                ", customer=" + customer +
                ", subtotal=" + subtotal +
                ", discount=" + discount +
                ", tax=" + tax +
                ", total=" + total +
                ", paymentMethod='" + paymentMethod + '\'' +
                ", status='" + status + '\'' +
                ", notes='" + notes + '\'' +
                ", deliveryAddress='" + deliveryAddress + '\'' +
                ", deliveryCharge=" + deliveryCharge +
                ", isDelivery=" + isDelivery +
                '}';
    }
} 