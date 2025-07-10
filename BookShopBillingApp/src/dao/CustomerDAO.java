package dao;

import model.Customer;
import util.DBConnection;
import service.ConfigurationService;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CustomerDAO {
    private DBConnection dbConnection;
    
    public CustomerDAO() {
        this.dbConnection = DBConnection.getInstance();
    }
    
    public List<Customer> getAllCustomers() {
        List<Customer> customers = new ArrayList<>();
        String query = "SELECT * FROM customers WHERE is_active = 1 ORDER BY name";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                Customer customer = new Customer();
                customer.setId(rs.getInt("id"));
                customer.setAccountNumber(rs.getString("account_number"));
                customer.setName(rs.getString("name"));
                customer.setAddress(rs.getString("address"));
                customer.setTelephone(rs.getString("telephone"));
                customer.setUnitsConsumed(rs.getInt("units_consumed"));
                customer.setActive(rs.getBoolean("is_active"));
                customers.add(customer);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Database error: " + e.getMessage());
        }
        return customers;
    }
    
    public Customer getCustomerById(int id) {
        String query = "SELECT * FROM customers WHERE id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                Customer customer = new Customer();
                customer.setId(rs.getInt("id"));
                customer.setAccountNumber(rs.getString("account_number"));
                customer.setName(rs.getString("name"));
                customer.setAddress(rs.getString("address"));
                customer.setTelephone(rs.getString("telephone"));
                customer.setUnitsConsumed(rs.getInt("units_consumed"));
                customer.setActive(rs.getBoolean("is_active"));
                return customer;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Database error: " + e.getMessage());
        }
        return null;
    }
    
    public Customer getCustomerByAccountNumber(String accountNumber) {
        String query = "SELECT * FROM customers WHERE account_number = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, accountNumber);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                Customer customer = new Customer();
                customer.setId(rs.getInt("id"));
                customer.setAccountNumber(rs.getString("account_number"));
                customer.setName(rs.getString("name"));
                customer.setAddress(rs.getString("address"));
                customer.setTelephone(rs.getString("telephone"));
                customer.setUnitsConsumed(rs.getInt("units_consumed"));
                customer.setActive(rs.getBoolean("is_active"));
                return customer;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Database error: " + e.getMessage());
        }
        return null;
    }
    
    public boolean addCustomer(Customer customer) {
        String query = "INSERT INTO customers (account_number, name, address, telephone, units_consumed, is_active) VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, customer.getAccountNumber());
            stmt.setString(2, customer.getName());
            stmt.setString(3, customer.getAddress());
            stmt.setString(4, customer.getTelephone());
            stmt.setInt(5, customer.getUnitsConsumed());
            stmt.setBoolean(6, customer.isActive());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Database error: " + e.getMessage());
        }
    }
    
    public boolean updateCustomer(Customer customer) {
        String query = "UPDATE customers SET account_number=?, name=?, address=?, telephone=?, units_consumed=?, is_active=? WHERE id=?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, customer.getAccountNumber());
            stmt.setString(2, customer.getName());
            stmt.setString(3, customer.getAddress());
            stmt.setString(4, customer.getTelephone());
            stmt.setInt(5, customer.getUnitsConsumed());
            stmt.setBoolean(6, customer.isActive());
            stmt.setInt(7, customer.getId());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Database error: " + e.getMessage());
        }
    }
    
    public boolean deleteCustomer(int id) {
        String query = "UPDATE customers SET is_active = 0 WHERE id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Database error: " + e.getMessage());
        }
    }
    
    public boolean updateUnitsConsumed(int customerId, int units) {
        String query = "UPDATE customers SET units_consumed = units_consumed + ? WHERE id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, units);
            stmt.setInt(2, customerId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Database error: " + e.getMessage());
        }
    }
    
    public String generateAccountNumber() {
        ConfigurationService configService = ConfigurationService.getInstance();
        String prefix = configService.getAccountPrefix();
        int length = configService.getAccountLength();
        
        String query = "SELECT MAX(CAST(SUBSTRING(account_number, " + (prefix.length() + 1) + ") AS UNSIGNED)) as max_num FROM customers WHERE account_number LIKE ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, prefix + "%");
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                int maxNum = rs.getInt("max_num");
                return prefix + String.format("%0" + length + "d", maxNum + 1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return prefix + String.format("%0" + length + "d", 1);
    }
} 