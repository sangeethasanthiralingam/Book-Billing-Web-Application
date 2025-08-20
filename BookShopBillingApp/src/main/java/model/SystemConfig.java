package model;

import java.io.Serializable;

public class SystemConfig implements Serializable {
    private int id;
    private String configKey;
    private String configValue;
    private String description;
    private String category;
    private boolean isActive;
    
    public SystemConfig() {
        this.isActive = true;
    }
    
    public SystemConfig(String configKey, String configValue, String description, String category) {
        this();
        this.configKey = configKey;
        this.configValue = configValue;
        this.description = description;
        this.category = category;
    }
    
    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getConfigKey() { return configKey; }
    public void setConfigKey(String configKey) { this.configKey = configKey; }
    
    public String getConfigValue() { return configValue; }
    public void setConfigValue(String configValue) { this.configValue = configValue; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    
    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }
    
    // Helper methods for common config types
    public double getDoubleValue() {
        try {
            return Double.parseDouble(configValue);
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }
    
    public int getIntValue() {
        try {
            return Integer.parseInt(configValue);
        } catch (NumberFormatException e) {
            return 0;
        }
    }
    
    public boolean getBooleanValue() {
        return "true".equalsIgnoreCase(configValue) || "1".equals(configValue);
    }
    
    @Override
    public String toString() {
        return "SystemConfig{" +
                "id=" + id +
                ", configKey='" + configKey + '\'' +
                ", configValue='" + configValue + '\'' +
                ", description='" + description + '\'' +
                ", category='" + category + '\'' +
                ", isActive=" + isActive +
                '}';
    }
} 