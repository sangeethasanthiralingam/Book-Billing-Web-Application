package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import service.ConfigurationService;
import dao.SystemConfigDAO;
import model.SystemConfig;

/**
 * Controller for system configuration operations
 */
public class ConfigController extends BaseController {
    
    /**
     * Handle system configuration page
     */
    public void handleSystemConfig(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        try {
            ConfigurationService configService = ConfigurationService.getInstance();
            Map<String, String> configs = configService.getConfigMap();
            
            // Ensure all required configs exist with default values
            if (configs.isEmpty()) {
                SystemConfigDAO configDAO = new SystemConfigDAO();
                configDAO.initializeDefaultConfigs();
                configService.forceRefresh();
                configs = configService.getConfigMap();
            }
            
            request.setAttribute("configs", configs);
            
        } catch (Exception e) {
            handleException(request, response, e, "loading system configuration");
        }
        request.getRequestDispatcher("/jsp/system-config.jsp").forward(request, response);
    }
    
    /**
     * Handle updating system configurations
     */
    public void handleUpdateConfigs(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        try {
            ConfigurationService configService = ConfigurationService.getInstance();
            Map<String, String> configs = configService.getConfigMap();

            // Update each configuration value
            for (String key : configs.keySet()) {
                String value = request.getParameter(key);
                if (value != null && !value.trim().isEmpty()) {
                    SystemConfig config = new SystemConfig();
                    config.setConfigKey(key);
                    config.setConfigValue(value.trim());
                    config.setActive(true);

                    // Get existing config for description and category
                    SystemConfigDAO configDAO = new SystemConfigDAO();
                    SystemConfig existingConfig = configDAO.getConfigByKey(key);
                    if (existingConfig != null) {
                        config.setDescription(existingConfig.getDescription());
                        config.setCategory(existingConfig.getCategory());
                    }

                    configService.updateConfig(config);
                }
            }

            request.setAttribute("success", "System configuration updated successfully!");
            
        } catch (Exception e) {
            handleException(request, response, e, "updating system configuration");
        }

        // Redirect back to system config page
        response.sendRedirect(request.getContextPath() + "/controller/system-config");
    }
} 