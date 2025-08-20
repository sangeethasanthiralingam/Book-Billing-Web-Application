package controller;

import java.io.IOException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import jakarta.mail.MessagingException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * Main front controller servlet that routes requests to appropriate handlers
 */
@WebServlet("/controller/*")
public class FrontControllerServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    
    // Controller instances
    private final AuthController authController = new AuthController();
    private final BookController bookController = new BookController();
    private final CustomerController customerController = new CustomerController();
    private final UserController userController = new UserController();
    private final BillingController billingController = new BillingController();
    private final DashboardController dashboardController = new DashboardController();
    private final ReportController reportController = new ReportController();
    private final ConfigController configController = new ConfigController();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @SuppressWarnings("CallToPrintStackTrace")
    private void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String pathInfo = request.getPathInfo();
        String action = pathInfo != null ? pathInfo.substring(1) : "";
        
        System.out.println("[FrontController] Request URI: " + request.getRequestURI());
        System.out.println("[FrontController] Path Info: " + pathInfo);
        System.out.println("[FrontController] Action: " + action);
        System.out.println("[FrontController] Method: " + request.getMethod());

        // Check authentication (except for login and register)
        if (!action.equals("login") && !action.equals("register") && !isAuthenticated(request)) {
            System.out.println("[FrontController] User not authenticated, redirecting to login");
            response.sendRedirect(request.getContextPath() + "/jsp/login.jsp");
            return;
        }
        
        System.out.println("[FrontController] User authenticated, proceeding with routing");

        try {
            routeRequest(action, request, response);
        } catch (ServletException | IOException e) {
            e.printStackTrace();
            request.setAttribute("error", "An error occurred: " + e.getMessage());
            request.getRequestDispatcher("/jsp/error.jsp").forward(request, response);
        }
    }

    private void routeRequest(String action, HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        System.out.println("[FrontController] Routing action: '" + action + "'");
        // Authentication routes
        switch (action) {
            case "login" -> authController.handleLogin(request, response);
            case "logout" -> authController.handleLogout(request, response);
            case "register" -> authController.handleRegister(request, response);
            case "books" -> bookController.handleBooks(request, response);
            case "search-books" -> bookController.handleSearchBooks(request, response);
            case "add-book" -> bookController.handleAddBook(request, response);
            case "edit-book" -> bookController.handleEditBook(request, response);
            case "delete-book" -> bookController.handleDeleteBook(request, response);
            case "view-book" -> bookController.handleViewBook(request, response);
            case "store" -> bookController.handleStore(request, response);
            case "customers" -> customerController.handleCustomers(request, response);
            case "customer-form" -> customerController.handleCustomerForm(request, response);
            case "customer-details" -> customerController.handleCustomerDetails(request, response);
            case "delete-customer" -> customerController.handleDeleteCustomer(request, response);
            case "customer-dashboard" -> customerController.handleCustomerDashboard(request, response);
            case "customer-profile" -> customerController.handleCustomerProfile(request, response);
            case "customer-purchases" -> customerController.handleCustomerPurchases(request, response);
            case "customer-reset-password" -> customerController.handleCustomerResetPassword(request, response);
            case "view-customer" -> customerController.handleViewCustomer(request, response);
            case "save-customer" -> customerController.handleSaveCustomer(request, response);
            case "search-customers" -> customerController.handleSearchCustomers(request, response);
            case "create-customer" -> customerController.handleCreateCustomer(request, response);
            case "generate-account-number" -> customerController.handleGenerateAccountNumber(request, response);
            case "customer-store" -> response.sendRedirect(request.getContextPath() + "/controller/store");
            case "customer-place-order" -> customerController.handleCustomerPlaceOrder(request, response);
            case "customer-order-confirmation" -> customerController.handleCustomerOrderConfirmation(request, response);
            case "get-book-details" -> customerController.handleGetBookDetails(request, response);
            case "add-user" -> userController.handleAddUser(request, response);
            case "users" -> userController.handleUsers(request, response);
            case "edit-user" -> userController.handleEditUser(request, response);
            case "delete-user" -> userController.handleDeleteUser(request, response);
            case "cashiers" -> userController.handleCashiers(request, response);
            case "leaderboard" -> userController.handleCashierLeaderboard(request, response);
            case "billing" -> billingController.handleBilling(request, response);
            case "invoice" -> billingController.handleInvoice(request, response);
            case "generate-bill" -> billingController.handleGenerateBill(request, response);
            case "search-books-billing" -> billingController.handleSearchBooks(request, response);
            case "search-customers-billing" -> {
                System.out.println("[FrontController] Matched search-customers-billing route");
                billingController.handleSearchCustomers(request, response);
            }
            case "pattern-demo" -> billingController.handlePatternDemo(request, response);
            case "generate-report" -> billingController.handleGenerateReport(request, response);
            case "order-state" -> billingController.handleOrderState(request, response);
            case "command-history" -> billingController.handleCommandHistory(request, response);
            case "dashboard" -> dashboardController.handleDashboard(request, response);
            case "reports" -> reportController.handleReports(request, response);
            case "pattern-reports" -> billingController.handleGenerateReport(request, response);
            case "system-config" -> configController.handleSystemConfig(request, response);
            case "update-configs" -> configController.handleUpdateConfigs(request, response);
            case "help" -> handleHelp(request, response);
            case "send-collection" -> handleSendCollection(request, response);
            default -> {
                System.out.println("[FrontController] No route matched, using default route");
                handleDefaultRoute(request, response);
            }
        }
        // Book routes
        // Customer routes
        // User management routes (Admin only)
        // Billing routes
        // Design Pattern routes - NEW FUNCTIONALITY
        // Dashboard routes
        // Report routes
        // Configuration routes
        // Utility routes
        // Default route
            }

    private boolean isAuthenticated(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        return session != null && session.getAttribute("user") != null;
    }

    private void handleHelp(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setAttribute("systemName", "BookShop Billing System");
        request.getRequestDispatcher("/jsp/help.jsp").forward(request, response);
    }

    @SuppressWarnings("CallToPrintStackTrace")
    private void handleSendCollection(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            // Read JSON from request body
            java.io.BufferedReader reader = request.getReader();
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            String jsonData = sb.toString();
            
            // Parse JSON
            JSONObject obj = new JSONObject(jsonData);
            JSONArray books = obj.getJSONArray("books");
            String note = obj.optString("note", "");
            
            // Get customer info from session
            HttpSession session = request.getSession(false);
            String customerName = session != null ? (String) session.getAttribute("user") : "Unknown";
            String customerEmail = "";
            Integer userId = session != null ? (Integer) session.getAttribute("userId") : null;
            
            if (userId != null) {
                dao.UserDAO userDAO = new dao.UserDAO();
                model.User user = userDAO.getUserById(userId);
                if (user != null) {
                    customerEmail = user.getEmail();
                }
            }
            
            // Load admin email from config
            String adminEmail = service.ConfigurationService.getInstance().getAdminEmail();
            
            // Format HTML email
            StringBuilder html = new StringBuilder();
            html.append("<h3>Book Collection Request from Customer Portal</h3>");
            html.append("<p><b>Customer:</b> ").append(customerName).append(" (").append(customerEmail).append(")</p>");
            if (!note.isEmpty()) {
                html.append("<p><b>Note:</b> ").append(note).append("</p>");
            }
            html.append("<table border='1' cellpadding='6' style='border-collapse:collapse;'>");
            html.append("<tr><th>Title</th><th>Author</th><th>Price</th></tr>");
            
            for (int i = 0; i < books.length(); i++) {
                JSONObject book = books.getJSONObject(i);
                html.append("<tr><td>").append(book.getString("title")).append("</td><td>").append(book.getString("author")).append("</td><td>LKR ")
                .append(book.get("price")).append("</td></tr>");
            }
            html.append("</table>");
            
            // Send email to admin (when email dependencies are properly configured)
            // For now, log the collection request with detailed information
            System.out.println("=== BOOK COLLECTION REQUEST ===");
            System.out.println("Customer: " + customerName + " (" + customerEmail + ")");
            System.out.println("Admin Email: " + adminEmail);
            System.out.println("Books Requested: " + books.length());
            System.out.println("Note: " + (note.isEmpty() ? "None" : note));
            System.out.println("HTML Content: " + html.toString());
            System.out.println("================================");
            
            try {
                if (adminEmail != null && !adminEmail.isEmpty() && !"your-email@gmail.com".equals(adminEmail)) {
                    util.MailUtil.sendMailHtml(adminEmail, "Book Collection Request from " + customerName, html.toString());
                    System.out.println("Email sent successfully to admin: " + adminEmail);
                } else {
                    System.out.println("Admin email not configured. Please configure SMTP settings in system config.");
                }
            } catch (MessagingException emailException) {
                System.err.println("Failed to send email: " + emailException.getMessage());
                emailException.printStackTrace();
            }
            
            response.setContentType("application/json");
            response.getWriter().write("{\"success\":true,\"message\":\"Collection request logged successfully.\"}");
            
        } catch (IOException | JSONException e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.setContentType("application/json");
            response.getWriter().write("{\"success\":false,\"message\":\"Failed to send collection: " + 
                                      e.getMessage().replace("\"", "\\\"") + "\"}");
        }
    }

    private void handleDefaultRoute(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        String userRole = (session != null) ? (String) session.getAttribute("userRole") : null;
        
        if ("CUSTOMER".equals(userRole)) {
            response.sendRedirect(request.getContextPath() + "/controller/customer-dashboard");
        } else {
            response.sendRedirect(request.getContextPath() + "/controller/dashboard");
        }
    }
}