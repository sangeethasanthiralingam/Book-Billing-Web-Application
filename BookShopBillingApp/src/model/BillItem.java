package model;

import java.io.Serializable;

public class BillItem implements Serializable {
    private int id;
    private int billId; // Add billId field
    private int bookId; // Add bookId field
    private Book book;
    private int quantity;
    private double unitPrice;
    private double price; // Add price field
    private double total;
    private double discountPercent;
    private String notes;
    
    public BillItem() {
        this.discountPercent = 0.0;
    }
    
    public BillItem(int id, Book book, int quantity, double unitPrice) {
        this();
        this.id = id;
        this.book = book;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        calculateTotal();
    }
    
    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public Book getBook() { return book; }
    public void setBook(Book book) { this.book = book; }
    
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { 
        this.quantity = quantity; 
        calculateTotal();
    }
    
    public double getUnitPrice() { return unitPrice; }
    public void setUnitPrice(double unitPrice) { 
        this.unitPrice = unitPrice; 
        calculateTotal();
    }
    
    public double getTotal() { return total; }
    public void setTotal(double total) { this.total = total; }
    
    public double getDiscountPercent() { return discountPercent; }
    public void setDiscountPercent(double discountPercent) { 
        this.discountPercent = discountPercent; 
        calculateTotal();
    }
    
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    
    // New getter/setter methods
    public int getBillId() { return billId; }
    public void setBillId(int billId) { this.billId = billId; }
    
    public int getBookId() { return bookId; }
    public void setBookId(int bookId) { this.bookId = bookId; }
    
    public double getPrice() { return price; }
    public void setPrice(double price) { 
        this.price = price;
        this.unitPrice = price; // Keep unitPrice in sync
        calculateTotal();
    }
    
    // Helper method to calculate total with discount
    private void calculateTotal() {
        double subtotal = quantity * unitPrice;
        double discountAmount = subtotal * (discountPercent / 100.0);
        this.total = subtotal - discountAmount;
    }
    
    @Override
    public String toString() {
        return "BillItem{" +
                "id=" + id +
                ", book=" + book.getTitle() +
                ", quantity=" + quantity +
                ", unitPrice=" + unitPrice +
                ", total=" + total +
                ", discountPercent=" + discountPercent +
                ", notes='" + notes + '\'' +
                '}';
    }
} 