package dao;

import model.SystemConfig;
import util.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SystemConfigDAO {
    private DBConnection dbConnection;
    
    public SystemConfigDAO() {
        this.dbConnection = DBConnection.getInstance();
    }
    
    public List<SystemConfig> getAllConfigs() {
        List<SystemConfig> configs = new ArrayList<>();
        String query = "SELECT * FROM system_configs WHERE is_active = 1 ORDER BY category, config_key";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                SystemConfig config = new SystemConfig();
                config.setId(rs.getInt("id"));
                config.setConfigKey(rs.getString("config_key"));
                config.setConfigValue(rs.getString("config_value"));
                config.setDescription(rs.getString("description"));
                config.setCategory(rs.getString("category"));
                config.setActive(rs.getBoolean("is_active"));
                configs.add(config);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Database error: " + e.getMessage());
        }
        return configs;
    }
    
    public SystemConfig getConfigByKey(String configKey) {
        String query = "SELECT * FROM system_configs WHERE config_key = ? AND is_active = 1";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, configKey);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                SystemConfig config = new SystemConfig();
                config.setId(rs.getInt("id"));
                config.setConfigKey(rs.getString("config_key"));
                config.setConfigValue(rs.getString("config_value"));
                config.setDescription(rs.getString("description"));
                config.setCategory(rs.getString("category"));
                config.setActive(rs.getBoolean("is_active"));
                return config;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Database error: " + e.getMessage());
        }
        return null;
    }
    
    public Map<String, String> getConfigMap() {
        Map<String, String> configMap = new HashMap<>();
        List<SystemConfig> configs = getAllConfigs();
        
        for (SystemConfig config : configs) {
            configMap.put(config.getConfigKey(), config.getConfigValue());
        }
        
        return configMap;
    }
    
    public boolean addConfig(SystemConfig config) {
        String query = "INSERT INTO system_configs (config_key, config_value, description, category, is_active) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, config.getConfigKey());
            stmt.setString(2, config.getConfigValue());
            stmt.setString(3, config.getDescription());
            stmt.setString(4, config.getCategory());
            stmt.setBoolean(5, config.isActive());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Database error: " + e.getMessage());
        }
    }
    
    public boolean updateConfig(SystemConfig config) {
        String query = "UPDATE system_configs SET config_value=?, description=?, category=?, is_active=? WHERE config_key=?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, config.getConfigValue());
            stmt.setString(2, config.getDescription());
            stmt.setString(3, config.getCategory());
            stmt.setBoolean(4, config.isActive());
            stmt.setString(5, config.getConfigKey());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Database error: " + e.getMessage());
        }
    }
    
    public boolean deleteConfig(String configKey) {
        String query = "UPDATE system_configs SET is_active = 0 WHERE config_key = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, configKey);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Database error: " + e.getMessage());
        }
    }
    
    public List<SystemConfig> getConfigsByCategory(String category) {
        List<SystemConfig> configs = new ArrayList<>();
        String query = "SELECT * FROM system_configs WHERE category = ? AND is_active = 1 ORDER BY config_key";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, category);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                SystemConfig config = new SystemConfig();
                config.setId(rs.getInt("id"));
                config.setConfigKey(rs.getString("config_key"));
                config.setConfigValue(rs.getString("config_value"));
                config.setDescription(rs.getString("description"));
                config.setCategory(rs.getString("category"));
                config.setActive(rs.getBoolean("is_active"));
                configs.add(config);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Database error: " + e.getMessage());
        }
        return configs;
    }
    
    public void initializeDefaultConfigs() {
        // Check if configs already exist
        if (getAllConfigs().isEmpty()) {
            addDefaultConfigs();
        }
    }
    
    private void addDefaultConfigs() {
        // Billing Configuration
        addConfig(new SystemConfig("UNIT_RATE", "2.50", "Rate per unit consumed", "BILLING"));
        addConfig(new SystemConfig("TAX_RATE", "0.10", "Tax rate as decimal (10%)", "BILLING"));
        addConfig(new SystemConfig("DELIVERY_CHARGE", "5.00", "Fixed delivery charge", "BILLING"));
        
        // Discount Configuration
        addConfig(new SystemConfig("DISCOUNT_LEVEL_1_THRESHOLD", "20", "Units threshold for 5% discount", "DISCOUNT"));
        addConfig(new SystemConfig("DISCOUNT_LEVEL_1_PERCENT", "0.05", "5% discount rate", "DISCOUNT"));
        addConfig(new SystemConfig("DISCOUNT_LEVEL_2_THRESHOLD", "50", "Units threshold for 10% discount", "DISCOUNT"));
        addConfig(new SystemConfig("DISCOUNT_LEVEL_2_PERCENT", "0.10", "10% discount rate", "DISCOUNT"));
        addConfig(new SystemConfig("DISCOUNT_LEVEL_3_THRESHOLD", "100", "Units threshold for 15% discount", "DISCOUNT"));
        addConfig(new SystemConfig("DISCOUNT_LEVEL_3_PERCENT", "0.15", "15% discount rate", "DISCOUNT"));
        
        // System Configuration
        // Do not add SYSTEM_NAME or COMPANY_NAME by default. Admin must set it from UI.
        addConfig(new SystemConfig("COMPANY_ADDRESS", "Colombo City, Sri Lanka", "Company address", "SYSTEM"));
        addConfig(new SystemConfig("COMPANY_PHONE", "+94 11 1234567", "Company phone number", "SYSTEM"));
        addConfig(new SystemConfig("COMPANY_EMAIL", "info@pahanaedu.com", "Company email", "SYSTEM"));
        
        // Account Configuration
        addConfig(new SystemConfig("ACCOUNT_PREFIX", "ACC-", "Prefix for customer account numbers", "ACCOUNT"));
        addConfig(new SystemConfig("ACCOUNT_LENGTH", "6", "Length of account number digits", "ACCOUNT"));
        
        // Inventory Configuration
        addConfig(new SystemConfig("LOW_STOCK_THRESHOLD", "5", "Minimum stock level for alerts", "INVENTORY"));
        addConfig(new SystemConfig("AUTO_RESTOCK_ENABLED", "false", "Enable automatic restock alerts", "INVENTORY"));
    }
} 