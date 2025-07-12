package model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;

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