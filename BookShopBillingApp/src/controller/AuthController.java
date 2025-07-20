package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import dao.UserDAO;
import model.User;

/**
 * Controller for authentication-related operations
 */
public class AuthController extends BaseController {
    
    /**
     * Handle user login
     */
    public void handleLogin(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        if (username == null || password == null) {
            request.getRequestDispatcher(LOGIN_PAGE).forward(request, response);
            return;
        }

        try {
            UserDAO userDAO = new UserDAO();
            User user = userDAO.authenticateUser(username, password);

            if (user != null) {
                HttpSession session = request.getSession();
                session.setAttribute("user", user.getFullName());
                session.setAttribute("userId", user.getId());
                session.setAttribute("userRole", user.getRole());

                // Redirect based on user role
                if ("CUSTOMER".equals(user.getRole())) {
                    response.sendRedirect(request.getContextPath() + "/controller/customer-dashboard");
                } else {
                    response.sendRedirect(request.getContextPath() + "/controller/dashboard");
                }
            } else {
                request.setAttribute("error", "Invalid username or password");
                request.getRequestDispatcher(LOGIN_PAGE).forward(request, response);
            }
        } catch (Exception e) {
            handleException(request, response, e, "login");
        }
    }
    
    /**
     * Handle user logout
     */
    public void handleLogout(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        response.sendRedirect(request.getContextPath() + LOGIN_PAGE);
    }
    
    /**
     * Handle user registration
     */
    public void handleRegister(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        if (!"POST".equalsIgnoreCase(request.getMethod())) {
            request.getRequestDispatcher("/jsp/register.jsp").forward(request, response);
            return;
        }

        // Get registration parameters
        String username = request.getParameter("username");
        String email = request.getParameter("email");
        String fullName = request.getParameter("fullName");
        String phone = request.getParameter("phone");
        String address = request.getParameter("address");
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");

        // Validation
        if (!validateRequiredParams(request, "username", "email", "fullName", "phone", 
                                   "address", "password", "confirmPassword")) {
            request.setAttribute("error", "All fields are required.");
            request.getRequestDispatcher("/jsp/register.jsp").forward(request, response);
            return;
        }

        if (!password.equals(confirmPassword)) {
            request.setAttribute("error", "Passwords do not match.");
            request.getRequestDispatcher("/jsp/register.jsp").forward(request, response);
            return;
        }

        if (password.length() < 6) {
            request.setAttribute("error", "Password must be at least 6 characters long.");
            request.getRequestDispatcher("/jsp/register.jsp").forward(request, response);
            return;
        }

        try {
            UserDAO userDAO = new UserDAO();

            // Check if username already exists
            if (userDAO.getUserByUsername(username) != null) {
                request.setAttribute("error", "Username already exists. Please choose a different username.");
                request.getRequestDispatcher("/jsp/register.jsp").forward(request, response);
                return;
            }

            // Check if email already exists
            if (userDAO.getUserByEmail(email) != null) {
                request.setAttribute("error", "Email already registered. Please use a different email or login.");
                request.getRequestDispatcher("/jsp/register.jsp").forward(request, response);
                return;
            }

            // Create new customer
            User newCustomer = new User();
            newCustomer.setUsername(username);
            newCustomer.setPassword(password);
            newCustomer.setEmail(email);
            newCustomer.setFullName(fullName);
            newCustomer.setPhone(phone);
            newCustomer.setAddress(address);
            newCustomer.setRole("CUSTOMER");
            newCustomer.setAccountNumber("ACC-" + System.currentTimeMillis());
            newCustomer.setUnitsConsumed(0);
            newCustomer.setActive(true);

            boolean success = userDAO.addUser(newCustomer);

            if (success) {
                request.setAttribute("success", "Registration successful! You can now login with your username and password.");
                request.getRequestDispatcher(LOGIN_PAGE).forward(request, response);
            } else {
                request.setAttribute("error", "Registration failed. Please try again.");
                request.getRequestDispatcher("/jsp/register.jsp").forward(request, response);
            }

        } catch (Exception e) {
            handleException(request, response, e, "registration");
        }
    }
} 