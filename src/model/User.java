package model;

import java.io.Serializable;
import java.util.Date;

public class User implements Serializable {
    private int id;
    private String username;
    private String password;
    private String email;
    private String role; // ADMIN, CASHIER, CUSTOMER
    private String fullName;
    private String phone;
    private String address;
    private String accountNumber;
    private int unitsConsumed;
    private boolean isActive;
    private Date createdAt;
    
    public User() {
        this.isActive = true;
        this.unitsConsumed = 0;
        this.createdAt = new Date();
    }
    
    public User(int id, String username, String password, String email, String role, String fullName) {
        this();
        this.id = id;
        this.username = username;
        this.password = password;
        this.email = email;
        this.role = role;
        this.fullName = fullName;
    }
    
    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    
    public String getAccountNumber() { return accountNumber; }
    public void setAccountNumber(String accountNumber) { this.accountNumber = accountNumber; }
    
    public int getUnitsConsumed() { return unitsConsumed; }
    public void setUnitsConsumed(int unitsConsumed) { this.unitsConsumed = unitsConsumed; }
    
    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }
    
    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
    
    // Helper methods
    public boolean isAdmin() {
        return "ADMIN".equals(role);
    }
    
    public boolean isCashier() {
        return "CASHIER".equals(role);
    }
    
    public boolean isCustomer() {
        return "CUSTOMER".equals(role);
    }
    
    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", role='" + role + '\'' +
                ", fullName='" + fullName + '\'' +
                ", phone='" + phone + '\'' +
                ", accountNumber='" + accountNumber + '\'' +
                ", unitsConsumed=" + unitsConsumed +
                ", isActive=" + isActive +
                ", createdAt=" + createdAt +
                '}';
    }
}