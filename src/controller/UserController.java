package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import dao.UserDAO;
import dao.BillDAO;
import model.User;

/**
 * Controller for user management operations (Admin only)
 */
public class UserController extends BaseController {
    
    /**
     * Handle user listing
     */
    public void handleUsers(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        if (!hasRole(request, "ADMIN")) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Only admins can manage users.");
            return;
        }
        
        try {
            UserDAO userDAO = new UserDAO();
            List<User> users = userDAO.getAllUsers();
            
            request.setAttribute("users", users);
            addCommonAttributes(request);
            
        } catch (Exception e) {
            handleException(request, response, e, "loading users");
        }
        request.getRequestDispatcher("/jsp/users.jsp").forward(request, response);
    }
    
    /**
     * Handle adding a new user
     */
    public void handleAddUser(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        if (!hasRole(request, "ADMIN")) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Only admins can add users.");
            return;
        }
        
        if ("POST".equalsIgnoreCase(request.getMethod())) {
            try {
                User user = createUserFromRequest(request);
                UserDAO userDAO = new UserDAO();
                
                // Check for duplicate username
                if (userDAO.getUserByUsername(user.getUsername()) != null) {
                    request.setAttribute("error", "Username already exists.");
                    request.getRequestDispatcher("/jsp/add-user.jsp").forward(request, response);
                    return;
                }
                
                // Check for duplicate email
                if (userDAO.getUserByEmail(user.getEmail()) != null) {
                    request.setAttribute("error", "Email already registered.");
                    request.getRequestDispatcher("/jsp/add-user.jsp").forward(request, response);
                    return;
                }
                
                boolean success = userDAO.addUser(user);
                
                if (success) {
                    response.sendRedirect(request.getContextPath() + "/controller/users?message=User added successfully");
                } else {
                    request.setAttribute("error", "Failed to add user.");
                    request.getRequestDispatcher("/jsp/add-user.jsp").forward(request, response);
                }
            } catch (Exception e) {
                handleException(request, response, e, "adding user");
            }
        } else {
            request.getRequestDispatcher("/jsp/add-user.jsp").forward(request, response);
        }
    }
    
    /**
     * Handle editing a user
     */
    public void handleEditUser(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        if (!hasRole(request, "ADMIN")) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Only admins can edit users.");
            return;
        }
        
        String userIdParam = request.getParameter("id");
        if (userIdParam == null || userIdParam.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/controller/users");
            return;
        }
        
        try {
            int userId = Integer.parseInt(userIdParam);
            UserDAO userDAO = new UserDAO();
            
            if ("POST".equalsIgnoreCase(request.getMethod())) {
                User user = userDAO.getUserById(userId);
                if (user != null) {
                    updateUserFromRequest(user, request);
                    boolean updated = userDAO.updateUser(user);
                    
                    if (updated) {
                        response.sendRedirect(request.getContextPath() + "/controller/users?message=User updated successfully");
                    } else {
                        request.setAttribute("error", "Failed to update user.");
                        request.setAttribute("user", user);
                        request.getRequestDispatcher("/jsp/edit-user.jsp").forward(request, response);
                    }
                } else {
                    request.setAttribute("error", "User not found.");
                    response.sendRedirect(request.getContextPath() + "/controller/users");
                }
            } else {
                User user = userDAO.getUserById(userId);
                request.setAttribute("user", user);
                request.getRequestDispatcher("/jsp/edit-user.jsp").forward(request, response);
            }
            
        } catch (Exception e) {
            handleException(request, response, e, "editing user");
        }
    }
    
    /**
     * Handle deleting a user
     */
    public void handleDeleteUser(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        if (!hasRole(request, "ADMIN")) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Only admins can delete users.");
            return;
        }
        
        String userIdParam = request.getParameter("id");
        if (userIdParam != null && !userIdParam.isEmpty()) {
            try {
                int userId = Integer.parseInt(userIdParam);
                UserDAO userDAO = new UserDAO();
                
                // Check if it's an activation request
                String action = request.getParameter("action");
                if ("activate".equals(action)) {
                    User user = userDAO.getUserById(userId);
                    if (user != null) {
                        user.setActive(true);
                        userDAO.updateUser(user);
                        response.sendRedirect(request.getContextPath() + "/controller/customers?message=Customer activated successfully");
                    }
                } else {
                    // Deactivate user
                    boolean deleted = userDAO.deleteUser(userId);
                    if (deleted) {
                        response.sendRedirect(request.getContextPath() + "/controller/users?message=User deactivated successfully");
                    } else {
                        response.sendRedirect(request.getContextPath() + "/controller/users?error=Failed to deactivate user");
                    }
                }
            } catch (Exception e) {
                response.sendRedirect(request.getContextPath() + "/controller/users?error=Error processing request: " + e.getMessage());
            }
        } else {
            response.sendRedirect(request.getContextPath() + "/controller/users?error=Invalid user ID");
        }
    }
    
    /**
     * Handle cashier listing and statistics
     */
    public void handleCashiers(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        if (!hasRole(request, "ADMIN")) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Only admins can view cashier statistics.");
            return;
        }
        
        try {
            BillDAO billDAO = new BillDAO();
            List<Map<String, Object>> cashierStats = billDAO.getCashierStats();
            
            request.setAttribute("cashierStats", cashierStats);
            addCommonAttributes(request);
            
        } catch (Exception e) {
            handleException(request, response, e, "loading cashier statistics");
        }
        request.getRequestDispatcher("/jsp/cashiers.jsp").forward(request, response);
    }
    
    /**
     * Handle cashier leaderboard
     */
    public void handleCashierLeaderboard(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        if (!hasRole(request, "ADMIN")) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Only admins can view leaderboard.");
            return;
        }
        
        try {
            BillDAO billDAO = new BillDAO();
            List<Map<String, Object>> leaderboard = billDAO.getCashierStats();
            
            // Sort by total sales (already sorted in DAO)
            request.setAttribute("leaderboard", leaderboard);
            addCommonAttributes(request);
            
        } catch (Exception e) {
            handleException(request, response, e, "loading cashier leaderboard");
        }
        request.getRequestDispatcher("/jsp/leaderboard.jsp").forward(request, response);
    }
    
    // Helper methods
    
    private User createUserFromRequest(HttpServletRequest request) {
        User user = new User();
        user.setUsername(request.getParameter("username"));
        user.setPassword(request.getParameter("password"));
        user.setEmail(request.getParameter("email"));
        user.setRole(request.getParameter("role"));
        user.setFullName(request.getParameter("fullName"));
        user.setPhone(request.getParameter("phone"));
        user.setAddress(request.getParameter("address"));
        user.setAccountNumber(generateAccountNumber());
        user.setUnitsConsumed(0);
        user.setActive(true);
        return user;
    }
    
    private void updateUserFromRequest(User user, HttpServletRequest request) {
        user.setUsername(request.getParameter("username"));
        
        // Only update password if provided
        String password = request.getParameter("password");
        if (password != null && !password.trim().isEmpty()) {
            user.setPassword(password);
        }
        
        user.setEmail(request.getParameter("email"));
        user.setRole(request.getParameter("role"));
        user.setFullName(request.getParameter("fullName"));
        user.setPhone(request.getParameter("phone"));
        user.setAddress(request.getParameter("address"));
        
        // Handle active status
        String activeParam = request.getParameter("active");
        user.setActive(activeParam != null && "true".equals(activeParam));
    }
    
    private String generateAccountNumber() {
        return "ACC-" + System.currentTimeMillis();
    }
}