package service;

import dao.SystemConfigDAO;
import model.SystemConfig;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import util.Constants;

public class ConfigurationService {
    private static ConfigurationService instance;
    private SystemConfigDAO configDAO;
    private Map<String, String> configCache;
    private long lastCacheUpdate;
    private static final long CACHE_DURATION = 300000; // 5 minutes
    
    private ConfigurationService() {
        this.configDAO = new SystemConfigDAO();
        this.configCache = new HashMap<>();
        this.lastCacheUpdate = 0;
        initializeConfigs();
    }
    
    public static ConfigurationService getInstance() {
        if (instance == null) {
            instance = new ConfigurationService();
        }
        return instance;
    }
    
    private void initializeConfigs() {
        try {
            configDAO.initializeDefaultConfigs();
            refreshCache();
        } catch (Exception e) {
            e.printStackTrace();
            // Use default values if database is not available
            setDefaultConfigs();
        }
    }
    
    private void setDefaultConfigs() {
        configCache.put("UNIT_RATE", "2.50");
        configCache.put("TAX_RATE", "0.10");
        configCache.put("DELIVERY_CHARGE", "5.00");
        configCache.put("DISCOUNT_LEVEL_1_THRESHOLD", "20");
        configCache.put("DISCOUNT_LEVEL_1_PERCENT", "0.05");
        configCache.put("DISCOUNT_LEVEL_2_THRESHOLD", "50");
        configCache.put("DISCOUNT_LEVEL_2_PERCENT", "0.10");
        configCache.put("DISCOUNT_LEVEL_3_THRESHOLD", "100");
        configCache.put("DISCOUNT_LEVEL_3_PERCENT", "0.15");
        configCache.put("COMPANY_NAME", "Online Billing System Pahana Edu");
        configCache.put("COMPANY_ADDRESS", "Colombo City, Sri Lanka");
        configCache.put("COMPANY_PHONE", "+94 11 1234567");
        configCache.put("COMPANY_EMAIL", "info@pahanaedu.com");
        configCache.put("ACCOUNT_PREFIX", "ACC-");
        configCache.put("ACCOUNT_LENGTH", "6");
        configCache.put("LOW_STOCK_THRESHOLD", "5");
        configCache.put("AUTO_RESTOCK_ENABLED", "false");
    }
    
    private void refreshCache() {
        try {
            configCache = configDAO.getConfigMap();
            lastCacheUpdate = System.currentTimeMillis();
        } catch (Exception e) {
            e.printStackTrace();
            setDefaultConfigs();
        }
    }
    
    private void checkCacheExpiry() {
        if (System.currentTimeMillis() - lastCacheUpdate > CACHE_DURATION) {
            refreshCache();
        }
    }
    
    public String getConfigValue(String key) {
        checkCacheExpiry();
        return configCache.getOrDefault(key, "");
    }
    
    public double getDoubleConfig(String key) {
        try {
            return Double.parseDouble(getConfigValue(key));
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }
    
    public int getIntConfig(String key) {
        try {
            return Integer.parseInt(getConfigValue(key));
        } catch (NumberFormatException e) {
            return 0;
        }
    }
    
    public boolean getBooleanConfig(String key) {
        String value = getConfigValue(key);
        return "true".equalsIgnoreCase(value) || "1".equals(value);
    }
    
    // Billing Configuration Getters
    public double getUnitRate() {
        return getDoubleConfig("UNIT_RATE");
    }
    
    public double getTaxRate() {
        return getDoubleConfig("TAX_RATE");
    }
    
    public double getDeliveryCharge() {
        return getDoubleConfig("DELIVERY_CHARGE");
    }
    
    // Discount Configuration Getters
    public int getDiscountLevel1Threshold() {
        return getIntConfig("DISCOUNT_LEVEL_1_THRESHOLD");
    }
    
    public double getDiscountLevel1Percent() {
        return getDoubleConfig("DISCOUNT_LEVEL_1_PERCENT");
    }
    
    public int getDiscountLevel2Threshold() {
        return getIntConfig("DISCOUNT_LEVEL_2_THRESHOLD");
    }
    
    public double getDiscountLevel2Percent() {
        return getDoubleConfig("DISCOUNT_LEVEL_2_PERCENT");
    }
    
    public int getDiscountLevel3Threshold() {
        return getIntConfig("DISCOUNT_LEVEL_3_THRESHOLD");
    }
    
    public double getDiscountLevel3Percent() {
        return getDoubleConfig("DISCOUNT_LEVEL_3_PERCENT");
    }
    
    // System Configuration Getters
    public String getCompanyName() {
        return getConfigValue("COMPANY_NAME");
    }
    
    public String getCompanyAddress() {
        return getConfigValue("COMPANY_ADDRESS");
    }
    
    public String getCompanyPhone() {
        return getConfigValue("COMPANY_PHONE");
    }
    
    public String getCompanyEmail() {
        return getConfigValue("COMPANY_EMAIL");
    }
    
    public String getSystemName() {
        String name = getConfigValue(Constants.CONFIG_KEY_SYSTEM_NAME);
        if (name == null || name.trim().isEmpty()) {
            return "Set System Name in Settings";
        }
        return name;
    }
    
    // Account Configuration Getters
    public String getAccountPrefix() {
        return getConfigValue("ACCOUNT_PREFIX");
    }
    
    public int getAccountLength() {
        return getIntConfig("ACCOUNT_LENGTH");
    }
    
    // Inventory Configuration Getters
    public int getLowStockThreshold() {
        return getIntConfig("LOW_STOCK_THRESHOLD");
    }
    
    public boolean isAutoRestockEnabled() {
        return getBooleanConfig("AUTO_RESTOCK_ENABLED");
    }
    
    // Configuration Management
    public List<SystemConfig> getAllConfigs() {
        return configDAO.getAllConfigs();
    }
    
    public List<SystemConfig> getConfigsByCategory(String category) {
        return configDAO.getConfigsByCategory(category);
    }
    
    public boolean updateConfig(SystemConfig config) {
        boolean success = configDAO.updateConfig(config);
        if (success) {
            refreshCache();
        }
        return success;
    }
    
    public boolean addConfig(SystemConfig config) {
        boolean success = configDAO.addConfig(config);
        if (success) {
            refreshCache();
        }
        return success;
    }
    
    public boolean deleteConfig(String configKey) {
        boolean success = configDAO.deleteConfig(configKey);
        if (success) {
            refreshCache();
        }
        return success;
    }
    
    public void forceRefresh() {
        refreshCache();
    }

    public Map<String, String> getConfigMap() {
        checkCacheExpiry();
        return new HashMap<>(configCache);
    }

    public String getSmtpHost() { return getPropertyOrDefault("smtp.host", "smtp.gmail.com"); }
    public String getSmtpPort() { return getPropertyOrDefault("smtp.port", "587"); }
    public String getSmtpUsername() { return getPropertyOrDefault("smtp.username", "your-email@gmail.com"); }
    public String getSmtpPassword() { return getPropertyOrDefault("smtp.password", "your-app-password"); }
    public String getSmtpFrom() { return getPropertyOrDefault("smtp.from", getSmtpUsername()); }
    public String getAdminEmail() { return getPropertyOrDefault("admin.email", "your-email@gmail.com"); }

    private String getPropertyOrDefault(String key, String defaultValue) {
        String value = getConfigValue(key);
        if (value == null || value.trim().isEmpty()) {
            return defaultValue;
        }
        return value;
    }
} 