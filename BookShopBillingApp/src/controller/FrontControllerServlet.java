package controller;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import dao.BookDAO;
import dao.BillDAO;
import dao.UserDAO;
import dao.CustomerDAO;
import model.Book;
import model.Bill;
import model.User;
import model.Customer;
import java.util.List;
import service.ConfigurationService;
import java.util.Map;

@WebServlet("/controller/*")
public class FrontControllerServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        processRequest(request, response);
    }
    
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        processRequest(request, response);
    }
    
    private void processRequest(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String pathInfo = request.getPathInfo();
        String action = pathInfo != null ? pathInfo.substring(1) : "";
        
        // Check if user is authenticated (except for login)
        if (!action.equals("login") && !isAuthenticated(request)) {
            response.sendRedirect(request.getContextPath() + "/jsp/login.jsp");
            return;
        }
        
        try {
            switch (action) {
                case "login":
                    handleLogin(request, response);
                    break;
                case "logout":
                    handleLogout(request, response);
                    break;
                case "dashboard":
                    handleDashboard(request, response);
                    break;
                case "books":
                    handleBooks(request, response);
                    break;
                case "billing":
                    handleBilling(request, response);
                    break;
                case "invoice":
                    handleInvoice(request, response);
                    break;
                case "reports":
                    handleReports(request, response);
                    break;
                case "customers":
                    handleCustomers(request, response);
                    break;
                case "customer-form":
                    handleCustomerForm(request, response);
                    break;
                case "customer-details":
                    handleCustomerDetails(request, response);
                    break;
                case "delete-customer":
                    handleDeleteCustomer(request, response);
                    break;
                case "help":
                    handleHelp(request, response);
                    break;
                case "system-config":
                    handleSystemConfig(request, response);
                    break;
                case "update-configs":
                    handleUpdateConfigs(request, response);
                    break;
                default:
                    response.sendRedirect(request.getContextPath() + "/jsp/dashboard.jsp");
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "An error occurred: " + e.getMessage());
            request.getRequestDispatcher("/jsp/error.jsp").forward(request, response);
        }
    }
    
    private boolean isAuthenticated(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        return session != null && session.getAttribute("user") != null;
    }
    
    private void handleLogin(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        
        if (username != null && password != null) {
            UserDAO userDAO = new UserDAO();
            User user = userDAO.authenticateUser(username, password);
            
            if (user != null) {
            HttpSession session = request.getSession();
                session.setAttribute("user", user.getFullName());
                session.setAttribute("userId", user.getId());
                session.setAttribute("userRole", user.getRole());
                response.sendRedirect(request.getContextPath() + "/controller/dashboard");
            } else {
                request.setAttribute("error", "Invalid username or password");
                request.getRequestDispatcher("/jsp/login.jsp").forward(request, response);
            }
        } else {
            request.getRequestDispatcher("/jsp/login.jsp").forward(request, response);
        }
    }
    
    private void handleLogout(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        response.sendRedirect(request.getContextPath() + "/jsp/login.jsp");
    }
    
    private void handleDashboard(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        try {
            BookDAO bookDAO = new BookDAO();
            BillDAO billDAO = new BillDAO();
            
            // Load dashboard statistics
            int totalBooks = bookDAO.getTotalBooksCount();
            int lowStockBooks = bookDAO.getLowStockBooksCount();
            int todayBills = billDAO.getTodayBillsCount();
            double todaySales = billDAO.getTodaySalesTotal();
            int todayCustomers = billDAO.getTodayCustomersCount();
            List<Bill> recentBills = billDAO.getRecentBills(5);
            
            // Set attributes for JSP
            request.setAttribute("totalBooks", totalBooks);
            request.setAttribute("lowStockBooks", lowStockBooks);
            request.setAttribute("todayBills", todayBills);
            request.setAttribute("todaySales", todaySales);
            request.setAttribute("todayCustomers", todayCustomers);
            request.setAttribute("recentBills", recentBills);
            request.setAttribute("systemName", ConfigurationService.getInstance().getSystemName());
            
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Error loading dashboard data: " + e.getMessage());
        }
        
        request.getRequestDispatcher("/jsp/dashboard.jsp").forward(request, response);
    }
    
    private void handleBooks(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        try {
            BookDAO bookDAO = new BookDAO();
            List<Book> books = bookDAO.getAllBooks();
            request.setAttribute("books", books);
            request.setAttribute("systemName", ConfigurationService.getInstance().getSystemName());
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Error loading books: " + e.getMessage());
        }
        request.getRequestDispatcher("/jsp/books.jsp").forward(request, response);
    }
    
    private void handleBilling(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        try {
            BookDAO bookDAO = new BookDAO();
            CustomerDAO customerDAO = new CustomerDAO();
            List<Book> books = bookDAO.getAllBooks();
            List<Customer> customers = customerDAO.getAllCustomers();
            request.setAttribute("books", books);
            request.setAttribute("customers", customers);
            request.setAttribute("systemName", ConfigurationService.getInstance().getSystemName());
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Error loading data for billing: " + e.getMessage());
        }
        request.getRequestDispatcher("/jsp/billing.jsp").forward(request, response);
    }
    
    private void handleInvoice(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        // TODO: Generate invoice
        request.getRequestDispatcher("/jsp/invoice.jsp").forward(request, response);
    }
    
    private void handleReports(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        try {
            BillDAO billDAO = new BillDAO();
            BookDAO bookDAO = new BookDAO();
            
            List<Bill> allBills = billDAO.getAllBills();
            List<Book> allBooks = bookDAO.getAllBooks();
            
            request.setAttribute("allBills", allBills);
            request.setAttribute("allBooks", allBooks);
            request.setAttribute("totalBills", allBills.size());
            request.setAttribute("totalBooks", allBooks.size());
            request.setAttribute("lowStockBooks", bookDAO.getLowStockBooksCount());
            request.setAttribute("systemName", ConfigurationService.getInstance().getSystemName());
            
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Error loading reports: " + e.getMessage());
        }
        request.getRequestDispatcher("/jsp/reports.jsp").forward(request, response);
    }
    
    private void handleCustomers(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        try {
            CustomerDAO customerDAO = new CustomerDAO();
            List<Customer> customers = customerDAO.getAllCustomers();
            request.setAttribute("customers", customers);
            request.setAttribute("systemName", ConfigurationService.getInstance().getSystemName());
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Error loading customers: " + e.getMessage());
        }
        request.getRequestDispatcher("/jsp/customers.jsp").forward(request, response);
    }
    
    private void handleCustomerForm(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String customerId = request.getParameter("id");
        if (customerId != null && !customerId.isEmpty()) {
            try {
                CustomerDAO customerDAO = new CustomerDAO();
                Customer customer = customerDAO.getCustomerById(Integer.parseInt(customerId));
                request.setAttribute("customer", customer);
            } catch (Exception e) {
                e.printStackTrace();
                request.setAttribute("error", "Error loading customer: " + e.getMessage());
            }
        }
        request.getRequestDispatcher("/jsp/customer-form.jsp").forward(request, response);
    }
    
    private void handleCustomerDetails(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String customerId = request.getParameter("id");
        if (customerId != null && !customerId.isEmpty()) {
            try {
                CustomerDAO customerDAO = new CustomerDAO();
                Customer customer = customerDAO.getCustomerById(Integer.parseInt(customerId));
                if (customer != null) {
                    request.setAttribute("customer", customer);
                } else {
                    request.setAttribute("error", "Customer not found");
                }
            } catch (Exception e) {
                e.printStackTrace();
                request.setAttribute("error", "Error loading customer details: " + e.getMessage());
            }
        }
        request.getRequestDispatcher("/jsp/customer-details.jsp").forward(request, response);
    }
    
    private void handleDeleteCustomer(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String customerId = request.getParameter("id");
        if (customerId != null && !customerId.isEmpty()) {
            try {
                CustomerDAO customerDAO = new CustomerDAO();
                boolean deleted = customerDAO.deleteCustomer(Integer.parseInt(customerId));
                if (deleted) {
                    response.sendRedirect(request.getContextPath() + "/controller/customers?message=Customer deleted successfully");
                } else {
                    response.sendRedirect(request.getContextPath() + "/controller/customers?error=Failed to delete customer");
                }
            } catch (Exception e) {
                e.printStackTrace();
                response.sendRedirect(request.getContextPath() + "/controller/customers?error=Error deleting customer: " + e.getMessage());
            }
        } else {
            response.sendRedirect(request.getContextPath() + "/controller/customers?error=Invalid customer ID");
        }
    }
    
    private void handleHelp(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        request.setAttribute("systemName", ConfigurationService.getInstance().getSystemName());
        request.getRequestDispatcher("/jsp/help.jsp").forward(request, response);
    }
    
    private void handleSystemConfig(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        try {
            service.ConfigurationService configService = service.ConfigurationService.getInstance();
            Map<String, String> configs = configService.getConfigMap();
            request.setAttribute("configs", configs);
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Error loading system configuration: " + e.getMessage());
        }
        request.getRequestDispatcher("/jsp/system-config.jsp").forward(request, response);
    }
    
    private void handleUpdateConfigs(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        try {
            service.ConfigurationService configService = service.ConfigurationService.getInstance();
            Map<String, String> configs = configService.getConfigMap();
            
            // Update each configuration value
            for (String key : configs.keySet()) {
                String value = request.getParameter(key);
                if (value != null && !value.trim().isEmpty()) {
                    model.SystemConfig config = new model.SystemConfig();
                    config.setConfigKey(key);
                    config.setConfigValue(value.trim());
                    config.setActive(true);
                    
                    // Get existing config for description and category
                    dao.SystemConfigDAO configDAO = new dao.SystemConfigDAO();
                    model.SystemConfig existingConfig = configDAO.getConfigByKey(key);
                    if (existingConfig != null) {
                        config.setDescription(existingConfig.getDescription());
                        config.setCategory(existingConfig.getCategory());
                    }
                    
                    configService.updateConfig(config);
                }
            }
            
            request.setAttribute("success", "System configuration updated successfully!");
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Error updating system configuration: " + e.getMessage());
        }
        
        // Redirect back to system config page
        response.sendRedirect(request.getContextPath() + "/controller/system-config");
    }
} 