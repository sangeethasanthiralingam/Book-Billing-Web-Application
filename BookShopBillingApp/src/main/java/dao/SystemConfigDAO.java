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
        System.out.println("[SystemConfigDAO] Checking if default configs need to be initialized...");
        
        // Always try to add default configs (INSERT IGNORE will prevent duplicates)
        try {
            String checkQuery = "SELECT COUNT(*) FROM system_configs";
            try (Connection conn = dbConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(checkQuery);
                 ResultSet rs = stmt.executeQuery()) {
                
                rs.next();
                int count = rs.getInt(1);
                System.out.println("[SystemConfigDAO] Found " + count + " existing configs");
                
                if (count == 0) {
                    System.out.println("[SystemConfigDAO] Adding default configurations...");
                    addDefaultConfigs();
                }
            }
        } catch (SQLException e) {
            System.err.println("[SystemConfigDAO] Error checking configs: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void addDefaultConfigs() {
        System.out.println("[SystemConfigDAO] Adding default configurations to database...");
        
        // Use direct SQL INSERT to ensure configs are added
        String insertQuery = "INSERT IGNORE INTO system_configs (config_key, config_value, description, category) VALUES (?, ?, ?, ?)";
        
        String[][] defaultConfigs = {
            {"UNIT_RATE", "2.50", "Rate per unit consumed for billing calculation", "BILLING"},
            {"TAX_RATE", "0.10", "Tax rate as decimal (e.g., 0.10 for 10%)", "BILLING"},
            {"DELIVERY_CHARGE", "5.00", "Fixed delivery charge amount", "BILLING"},
            {"DISCOUNT_LEVEL_1_THRESHOLD", "20", "Units threshold for first discount level", "DISCOUNT"},
            {"DISCOUNT_LEVEL_1_PERCENT", "0.05", "Discount percentage for level 1 (e.g., 0.05 for 5%)", "DISCOUNT"},
            {"DISCOUNT_LEVEL_2_THRESHOLD", "50", "Units threshold for second discount level", "DISCOUNT"},
            {"DISCOUNT_LEVEL_2_PERCENT", "0.10", "Discount percentage for level 2 (e.g., 0.10 for 10%)", "DISCOUNT"},
            {"DISCOUNT_LEVEL_3_THRESHOLD", "100", "Units threshold for third discount level", "DISCOUNT"},
            {"DISCOUNT_LEVEL_3_PERCENT", "0.15", "Discount percentage for level 3 (e.g., 0.15 for 15%)", "DISCOUNT"},
            {"COMPANY_NAME", "Online Billing System Pahana Edu", "Company name displayed on bills and reports", "SYSTEM"},
            {"COMPANY_ADDRESS", "Colombo City, Sri Lanka", "Company address for bills", "SYSTEM"},
            {"COMPANY_PHONE", "+94 11 1234567", "Company phone number", "SYSTEM"},
            {"COMPANY_EMAIL", "info@pahanaedu.com", "Company email address", "SYSTEM"},
            {"ACCOUNT_PREFIX", "ACC-", "Prefix for customer account numbers", "ACCOUNT"},
            {"ACCOUNT_LENGTH", "6", "Number of digits in account numbers", "ACCOUNT"},
            {"LOW_STOCK_THRESHOLD", "5", "Minimum stock level for low stock alerts", "INVENTORY"},
            {"AUTO_RESTOCK_ENABLED", "false", "Enable automatic restock alerts", "INVENTORY"}
        };
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(insertQuery)) {
            
            for (String[] config : defaultConfigs) {
                stmt.setString(1, config[0]);
                stmt.setString(2, config[1]);
                stmt.setString(3, config[2]);
                stmt.setString(4, config[3]);
                
                int result = stmt.executeUpdate();
                if (result > 0) {
                    System.out.println("[SystemConfigDAO] Added config: " + config[0] + " = " + config[1]);
                }
            }
            
            System.out.println("[SystemConfigDAO] Default configurations added successfully");
            
        } catch (SQLException e) {
            System.err.println("[SystemConfigDAO] Error adding default configs: " + e.getMessage());
            e.printStackTrace();
        }
    }
} 