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
            response.sendRedirect(request.getContextPath() + "/jsp/auth/login.jsp");
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
                case "add-user":
                    handleAddUser(request, response);
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
                case "cashiers":
                    handleCashiers(request, response);
                    break;
                case "customer-dashboard":
                    handleCustomerDashboard(request, response);
                    break;
                case "leaderboard":
                    handleCashierLeaderboard(request, response);
                    break;
                case "customer-profile":
                    handleCustomerProfile(request, response);
                    break;
                case "users":
                    handleUsers(request, response);
                    break;
                case "edit-user":
                    handleEditUser(request, response);
                    break;
                case "delete-user":
                    handleDeleteUser(request, response);
                    break;
                case "customer-reset-password":
                    handleCustomerResetPassword(request, response);
                    break;
                case "add-book":
                    handleAddBook(request, response);
                    break;
                case "edit-book":
                    handleEditBook(request, response);
                    break;
                case "delete-book":
                    handleDeleteBook(request, response);
                    break;
                case "view-book":
                    handleViewBook(request, response);
                    break;
                default:
                    response.sendRedirect(request.getContextPath() + "/jsp/admin/dashboard.jsp");
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "An error occurred: " + e.getMessage());
            request.getRequestDispatcher("/jsp/common/error.jsp").forward(request, response);
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
                if ("CUSTOMER".equals(user.getRole())) {
                    response.sendRedirect(request.getContextPath() + "/controller/customer-dashboard");
                } else {
                    response.sendRedirect(request.getContextPath() + "/controller/dashboard");
                }
            } else {
                request.setAttribute("error", "Invalid username or password");
                request.getRequestDispatcher("/jsp/auth/login.jsp").forward(request, response);
            }
        } else {
            request.getRequestDispatcher("/jsp/auth/login.jsp").forward(request, response);
        }
    }
    
    private void handleLogout(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        response.sendRedirect(request.getContextPath() + "/jsp/auth/login.jsp");
    }
    
    private void handleDashboard(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        try {
            BookDAO bookDAO = new BookDAO();
            BillDAO billDAO = new BillDAO();
            HttpSession session = request.getSession(false);
            String userRole = (session != null) ? (String) session.getAttribute("userRole") : null;
            Integer userId = (session != null) ? (Integer) session.getAttribute("userId") : null;
            
            if ("CASHIER".equals(userRole) && userId != null) {
                // Cashier-specific dashboard
                int todayBills = billDAO.getTodayBillsCountByCashier(userId);
                double todaySales = billDAO.getTodaySalesTotalByCashier(userId);
                List<Bill> recentBills = billDAO.getRecentBillsByCashier(userId, 5);
                request.setAttribute("todayBills", todayBills);
                request.setAttribute("todaySales", todaySales);
                request.setAttribute("recentBills", recentBills);
                // Optionally, show only relevant stats for cashier
            } else {
                // Admin/other roles: show global stats
                int totalBooks = bookDAO.getTotalBooksCount();
                int lowStockBooks = bookDAO.getLowStockBooksCount();
                int todayBills = billDAO.getTodayBillsCount();
                double todaySales = billDAO.getTodaySalesTotal();
                int todayCustomers = billDAO.getTodayCustomersCount();
                List<Bill> recentBills = billDAO.getRecentBills(5);
                request.setAttribute("totalBooks", totalBooks);
                request.setAttribute("lowStockBooks", lowStockBooks);
                request.setAttribute("todayBills", todayBills);
                request.setAttribute("todaySales", todaySales);
                request.setAttribute("todayCustomers", todayCustomers);
                request.setAttribute("recentBills", recentBills);
            }
            request.setAttribute("systemName", ConfigurationService.getInstance().getSystemName());
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Error loading dashboard data: " + e.getMessage());
        }
        request.getRequestDispatcher("/jsp/admin/dashboard.jsp").forward(request, response);
    }
    
    private void handleBooks(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        try {
            BookDAO bookDAO = new BookDAO();
            String search = request.getParameter("search");
            java.util.List<Book> books = bookDAO.getAllBooks();
            java.util.List<Book> filtered = new java.util.ArrayList<>();
            for (Book book : books) {
                boolean matches = true;
                if (search != null && !search.trim().isEmpty()) {
                    String s = search.trim().toLowerCase();
                    matches &= book.getTitle().toLowerCase().contains(s)
                        || book.getAuthor().toLowerCase().contains(s)
                        || (book.getCategory() != null && book.getCategory().toLowerCase().contains(s));
                }
                if (matches) filtered.add(book);
            }
            int page = 1;
            int pageSize = 10;
            String pageParam = request.getParameter("page");
            if (pageParam != null) {
                try { page = Integer.parseInt(pageParam); } catch (Exception ignored) {}
                if (page < 1) page = 1;
            }
            int totalBooks = filtered.size();
            int totalPages = (int)Math.ceil((double)totalBooks / pageSize);
            int fromIndex = (page - 1) * pageSize;
            int toIndex = Math.min(fromIndex + pageSize, totalBooks);
            java.util.List<Book> pagedBooks = filtered.subList(fromIndex < totalBooks ? fromIndex : 0, toIndex);
            request.setAttribute("books", pagedBooks);
            request.setAttribute("search", search);
            request.setAttribute("page", page);
            request.setAttribute("totalPages", totalPages);
            request.setAttribute("totalBooks", totalBooks);
            request.setAttribute("systemName", service.ConfigurationService.getInstance().getSystemName());
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Error loading books: " + e.getMessage());
        }
        request.getRequestDispatcher("/jsp/admin/books.jsp").forward(request, response);
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
        request.getRequestDispatcher("/jsp/admin/billing.jsp").forward(request, response);
    }
    
    private void handleInvoice(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        // TODO: Generate invoice
        request.getRequestDispatcher("/jsp/admin/invoice.jsp").forward(request, response);
    }
    
    private void handleReports(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        try {
            BillDAO billDAO = new BillDAO();
            BookDAO bookDAO = new BookDAO();
            HttpSession session = request.getSession(false);
            String userRole = (session != null) ? (String) session.getAttribute("userRole") : null;
            Integer userId = (session != null) ? (Integer) session.getAttribute("userId") : null;
            
            if ("CASHIER".equals(userRole) && userId != null) {
                // Cashier-specific reports
                List<Bill> allBills = billDAO.getBillsByCashier(userId);
                int totalBills = allBills.size();
                double totalSales = billDAO.getTotalSalesByCashier(userId);
                List<Bill> recentBills = billDAO.getRecentBillsByCashier(userId, 5);
                request.setAttribute("allBills", allBills);
                request.setAttribute("totalBills", totalBills);
                request.setAttribute("totalSales", totalSales);
                request.setAttribute("recentBills", recentBills);
                // Optionally, add more cashier-specific metrics
            } else {
                // Admin/other roles: show global reports
                List<Bill> allBills = billDAO.getAllBills();
                List<Book> allBooks = bookDAO.getAllBooks();
                request.setAttribute("allBills", allBills);
                request.setAttribute("allBooks", allBooks);
                request.setAttribute("totalBills", allBills.size());
                request.setAttribute("totalBooks", allBooks.size());
                request.setAttribute("lowStockBooks", bookDAO.getLowStockBooksCount());
            }
            request.setAttribute("systemName", ConfigurationService.getInstance().getSystemName());
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Error loading reports: " + e.getMessage());
        }
        request.getRequestDispatcher("/jsp/admin/reports.jsp").forward(request, response);
    }
    
    private void handleCustomers(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        try {
            dao.UserDAO userDAO = new dao.UserDAO();
            dao.BillDAO billDAO = new dao.BillDAO();
            java.util.List<model.User> customers = userDAO.getUsersByRole("CUSTOMER");
            java.util.List<java.util.Map<String, Object>> customerStats = new java.util.ArrayList<>();
            for (model.User customer : customers) {
                java.util.Map<String, Object> stats = new java.util.HashMap<>();
                stats.put("customer", customer);
                java.util.List<model.Bill> bills = billDAO.getBillsByCustomer(customer.getId());
                stats.put("billCount", bills.size());
                double totalSpent = 0.0;
                for (model.Bill bill : bills) totalSpent += bill.getTotal();
                stats.put("totalSpent", totalSpent);
                customerStats.add(stats);
            }
            request.setAttribute("customerStats", customerStats);
            request.setAttribute("systemName", service.ConfigurationService.getInstance().getSystemName());
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Error loading customers: " + e.getMessage());
        }
        request.getRequestDispatcher("/jsp/admin/customers.jsp").forward(request, response);
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
        request.getRequestDispatcher("/jsp/admin/customer-form.jsp").forward(request, response);
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
        request.getRequestDispatcher("/jsp/admin/customer-details.jsp").forward(request, response);
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
        request.getRequestDispatcher("/jsp/admin/help.jsp").forward(request, response);
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
        request.getRequestDispatcher("/jsp/admin/system-config.jsp").forward(request, response);
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

    private void handleAddUser(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        String role = (session != null) ? (String) session.getAttribute("userRole") : null;
        if (!"ADMIN".equals(role)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Only admins can add users.");
            return;
        }
        if ("POST".equalsIgnoreCase(request.getMethod())) {
            // Process form submission
            String username = request.getParameter("username");
            String password = request.getParameter("password");
            String email = request.getParameter("email");
            String fullName = request.getParameter("fullName");
            String phone = request.getParameter("phone");
            String userRole = request.getParameter("role");
            if (username != null && password != null && email != null && fullName != null && userRole != null) {
                User user = new User();
                user.setUsername(username);
                user.setPassword(password);
                user.setEmail(email);
                user.setFullName(fullName);
                user.setPhone(phone);
                user.setRole(userRole);
                UserDAO userDAO = new UserDAO();
                boolean success = userDAO.addUser(user);
                if (success) {
                    request.setAttribute("success", "User created successfully.");
                } else {
                    request.setAttribute("error", "Failed to create user.");
                }
            } else {
                request.setAttribute("error", "All fields except phone are required.");
            }
        }
        request.getRequestDispatcher("/jsp/admin/add-user.jsp").forward(request, response);
    }

    private void handleCashiers(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        String userRole = (session != null) ? (String) session.getAttribute("userRole") : null;
        if (!"ADMIN".equals(userRole)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Only admins can view all cashiers.");
            return;
        }
        try {
            dao.UserDAO userDAO = new dao.UserDAO();
            dao.BillDAO billDAO = new dao.BillDAO();
            java.util.List<model.User> cashiers = userDAO.getUsersByRole("CASHIER");
            java.util.List<java.util.Map<String, Object>> cashierStats = new java.util.ArrayList<>();
            for (model.User cashier : cashiers) {
                java.util.Map<String, Object> stats = new java.util.HashMap<>();
                stats.put("cashier", cashier);
                int billCount = billDAO.getBillsByCashier(cashier.getId()).size();
                double totalSales = billDAO.getTotalSalesByCashier(cashier.getId());
                stats.put("billCount", billCount);
                stats.put("totalSales", totalSales);
                cashierStats.add(stats);
            }
            request.setAttribute("cashierStats", cashierStats);
            request.setAttribute("systemName", service.ConfigurationService.getInstance().getSystemName());
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Error loading cashiers: " + e.getMessage());
        }
        request.getRequestDispatcher("/jsp/admin/cashiers.jsp").forward(request, response);
    }

    private void handleCustomerDashboard(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        String userRole = (session != null) ? (String) session.getAttribute("userRole") : null;
        Integer userId = (session != null) ? (Integer) session.getAttribute("userId") : null;
        if (!"CUSTOMER".equals(userRole) || userId == null) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Only customers can view their dashboard.");
            return;
        }
        try {
            dao.UserDAO userDAO = new dao.UserDAO();
            dao.BillDAO billDAO = new dao.BillDAO();
            model.User customer = userDAO.getUserById(userId);
            java.util.List<model.Bill> bills = billDAO.getBillsByCustomer(userId);
            double totalSpent = 0.0;
            for (model.Bill bill : bills) {
                totalSpent += bill.getTotal();
            }
            request.setAttribute("customer", customer);
            request.setAttribute("bills", bills);
            request.setAttribute("totalSpent", totalSpent);
            request.setAttribute("billCount", bills.size());
            request.setAttribute("systemName", service.ConfigurationService.getInstance().getSystemName());
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Error loading customer dashboard: " + e.getMessage());
        }
        request.getRequestDispatcher("/jsp/customer/customer-dashboard.jsp").forward(request, response);
    }

    private void handleCashierLeaderboard(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        String userRole = (session != null) ? (String) session.getAttribute("userRole") : null;
        if (!"ADMIN".equals(userRole)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Only admins can view the leaderboard.");
            return;
        }
        try {
            dao.UserDAO userDAO = new dao.UserDAO();
            dao.BillDAO billDAO = new dao.BillDAO();
            java.util.List<model.User> cashiers = userDAO.getUsersByRole("CASHIER");
            java.util.List<java.util.Map<String, Object>> leaderboard = new java.util.ArrayList<>();
            for (model.User cashier : cashiers) {
                java.util.Map<String, Object> stats = new java.util.HashMap<>();
                stats.put("cashier", cashier);
                int billCount = billDAO.getBillsByCashier(cashier.getId()).size();
                double totalSales = billDAO.getTotalSalesByCashier(cashier.getId());
                // Placeholder for feedback score (implement feedback aggregation later)
                double feedbackScore = 0.0;
                stats.put("billCount", billCount);
                stats.put("totalSales", totalSales);
                stats.put("feedbackScore", feedbackScore);
                leaderboard.add(stats);
            }
            // Sort leaderboard by totalSales descending
            leaderboard.sort((a, b) -> Double.compare((double)b.get("totalSales"), (double)a.get("totalSales")));
            request.setAttribute("leaderboard", leaderboard);
            request.setAttribute("systemName", service.ConfigurationService.getInstance().getSystemName());
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Error loading leaderboard: " + e.getMessage());
        }
        request.getRequestDispatcher("/jsp/admin/leaderboard.jsp").forward(request, response);
    }

    private void handleCustomerProfile(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        String userRole = (session != null) ? (String) session.getAttribute("userRole") : null;
        Integer userId = (session != null) ? (Integer) session.getAttribute("userId") : null;
        if (!"CUSTOMER".equals(userRole) || userId == null) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Only customers can view their profile.");
            return;
        }
        dao.UserDAO userDAO = new dao.UserDAO();
        if ("POST".equalsIgnoreCase(request.getMethod())) {
            String fullName = request.getParameter("fullName");
            String email = request.getParameter("email");
            String phone = request.getParameter("phone");
            String address = request.getParameter("address");
            model.User customer = userDAO.getUserById(userId);
            if (customer != null) {
                customer.setFullName(fullName);
                customer.setEmail(email);
                customer.setPhone(phone);
                customer.setAddress(address);
                boolean updated = userDAO.updateUser(customer);
                if (updated) {
                    request.setAttribute("success", "Profile updated successfully.");
                } else {
                    request.setAttribute("error", "Failed to update profile.");
                }
            }
        }
        model.User customer = userDAO.getUserById(userId);
        request.setAttribute("customer", customer);
        request.setAttribute("systemName", service.ConfigurationService.getInstance().getSystemName());
        request.getRequestDispatcher("/jsp/customer/customer-profile.jsp").forward(request, response);
    }

    private void handleUsers(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        String userRole = (session != null) ? (String) session.getAttribute("userRole") : null;
        if (!"ADMIN".equals(userRole)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Only admins can manage users.");
            return;
        }
        dao.UserDAO userDAO = new dao.UserDAO();
        String search = request.getParameter("search");
        String roleFilter = request.getParameter("roleFilter");
        java.util.List<model.User> users = userDAO.getAllUsers();
        java.util.List<model.User> filtered = new java.util.ArrayList<>();
        for (model.User user : users) {
            boolean matches = true;
            if (search != null && !search.trim().isEmpty()) {
                String s = search.trim().toLowerCase();
                matches &= user.getUsername().toLowerCase().contains(s)
                        || user.getEmail().toLowerCase().contains(s)
                        || user.getFullName().toLowerCase().contains(s);
            }
            if (roleFilter != null && !roleFilter.isEmpty() && !"ALL".equals(roleFilter)) {
                matches &= user.getRole().equalsIgnoreCase(roleFilter);
            }
            if (matches) filtered.add(user);
        }
        int page = 1;
        int pageSize = 10;
        String pageParam = request.getParameter("page");
        if (pageParam != null) {
            try { page = Integer.parseInt(pageParam); } catch (Exception ignored) {}
            if (page < 1) page = 1;
        }
        int totalUsers = filtered.size();
        int totalPages = (int)Math.ceil((double)totalUsers / pageSize);
        int fromIndex = (page - 1) * pageSize;
        int toIndex = Math.min(fromIndex + pageSize, totalUsers);
        java.util.List<model.User> pagedUsers = filtered.subList(fromIndex < totalUsers ? fromIndex : 0, toIndex);
        request.setAttribute("users", pagedUsers);
        request.setAttribute("search", search);
        request.setAttribute("roleFilter", roleFilter);
        request.setAttribute("page", page);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("totalUsers", totalUsers);
        request.getRequestDispatcher("/jsp/admin/users.jsp").forward(request, response);
    }

    private void handleEditUser(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        String userRole = (session != null) ? (String) session.getAttribute("userRole") : null;
        if (!"ADMIN".equals(userRole)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Only admins can edit users.");
            return;
        }
        dao.UserDAO userDAO = new dao.UserDAO();
        String idParam = request.getParameter("id");
        if (idParam == null) {
            response.sendRedirect(request.getContextPath() + "/controller/users");
            return;
        }
        int userId = Integer.parseInt(idParam);
        if ("POST".equalsIgnoreCase(request.getMethod())) {
            String fullName = request.getParameter("fullName");
            String email = request.getParameter("email");
            String phone = request.getParameter("phone");
            String address = request.getParameter("address");
            String role = request.getParameter("role");
            model.User user = userDAO.getUserById(userId);
            if (user != null) {
                user.setFullName(fullName);
                user.setEmail(email);
                user.setPhone(phone);
                user.setAddress(address);
                user.setRole(role);
                boolean updated = userDAO.updateUser(user);
                if (updated) {
                    request.setAttribute("success", "User updated successfully.");
                } else {
                    request.setAttribute("error", "Failed to update user.");
                }
            }
        }
        model.User user = userDAO.getUserById(userId);
        request.setAttribute("user", user);
        request.setAttribute("systemName", service.ConfigurationService.getInstance().getSystemName());
        request.getRequestDispatcher("/jsp/admin/edit-user.jsp").forward(request, response);
    }

    private void handleDeleteUser(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        String userRole = (session != null) ? (String) session.getAttribute("userRole") : null;
        if (!"ADMIN".equals(userRole)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Only admins can delete users.");
            return;
        }
        dao.UserDAO userDAO = new dao.UserDAO();
        String idParam = request.getParameter("id");
        if (idParam != null) {
            int userId = Integer.parseInt(idParam);
            userDAO.deleteUser(userId);
        }
        response.sendRedirect(request.getContextPath() + "/controller/users");
    }

    private void handleCustomerResetPassword(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        String userRole = (session != null) ? (String) session.getAttribute("userRole") : null;
        Integer userId = (session != null) ? (Integer) session.getAttribute("userId") : null;
        if (!"CUSTOMER".equals(userRole) || userId == null) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Only customers can reset their password.");
            return;
        }
        dao.UserDAO userDAO = new dao.UserDAO();
        if ("POST".equalsIgnoreCase(request.getMethod())) {
            String currentPassword = request.getParameter("currentPassword");
            String newPassword = request.getParameter("newPassword");
            String confirmPassword = request.getParameter("confirmPassword");
            model.User customer = userDAO.getUserById(userId);
            if (!customer.getPassword().equals(currentPassword)) {
                request.setAttribute("error", "Current password is incorrect.");
            } else if (!newPassword.equals(confirmPassword)) {
                request.setAttribute("error", "New passwords do not match.");
            } else {
                customer.setPassword(newPassword);
                userDAO.updateUser(customer);
                request.setAttribute("success", "Password updated successfully.");
                // Stay on the same page, do not redirect
            }
        }
        request.getRequestDispatcher("/jsp/customer/customer-reset-password.jsp").forward(request, response);
    }

    private void handleAddBook(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!"ADMIN".equals(request.getSession().getAttribute("userRole"))) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Only admins can add books.");
            return;
        }
        if ("POST".equalsIgnoreCase(request.getMethod())) {
            String title = request.getParameter("title");
            String author = request.getParameter("author");
            String isbn = request.getParameter("isbn");
            String priceStr = request.getParameter("price");
            String quantityStr = request.getParameter("quantity");
            String category = request.getParameter("category");
            double price = 0.0;
            int quantity = 0;
            try { price = Double.parseDouble(priceStr); } catch (Exception ignored) {}
            try { quantity = Integer.parseInt(quantityStr); } catch (Exception ignored) {}
            model.Book book = new model.Book();
            book.setTitle(title);
            book.setAuthor(author);
            book.setIsbn(isbn);
            book.setPrice(price);
            book.setQuantity(quantity);
            book.setCategory(category);
            dao.BookDAO bookDAO = new dao.BookDAO();
            boolean success = bookDAO.addBook(book);
            if (success) {
                request.setAttribute("success", "Book added successfully.");
            } else {
                request.setAttribute("error", "Failed to add book.");
            }
        }
        request.getRequestDispatcher("/jsp/admin/add-book.jsp").forward(request, response);
    }

    private void handleEditBook(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!"ADMIN".equals(request.getSession().getAttribute("userRole"))) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Only admins can edit books.");
            return;
        }
        dao.BookDAO bookDAO = new dao.BookDAO();
        String idParam = request.getParameter("id");
        if (idParam == null) {
            response.sendRedirect(request.getContextPath() + "/controller/books");
            return;
        }
        int bookId = Integer.parseInt(idParam);
        if ("POST".equalsIgnoreCase(request.getMethod())) {
            String title = request.getParameter("title");
            String author = request.getParameter("author");
            String isbn = request.getParameter("isbn");
            String priceStr = request.getParameter("price");
            String quantityStr = request.getParameter("quantity");
            String category = request.getParameter("category");
            double price = 0.0;
            int quantity = 0;
            try { price = Double.parseDouble(priceStr); } catch (Exception ignored) {}
            try { quantity = Integer.parseInt(quantityStr); } catch (Exception ignored) {}
            model.Book book = bookDAO.getBookById(bookId);
            if (book != null) {
                book.setTitle(title);
                book.setAuthor(author);
                book.setIsbn(isbn);
                book.setPrice(price);
                book.setQuantity(quantity);
                book.setCategory(category);
                boolean updated = bookDAO.updateBook(book);
                if (updated) {
                    request.setAttribute("success", "Book updated successfully.");
                } else {
                    request.setAttribute("error", "Failed to update book.");
                }
            }
        }
        model.Book book = bookDAO.getBookById(bookId);
        request.setAttribute("book", book);
        request.getRequestDispatcher("/jsp/admin/edit-book.jsp").forward(request, response);
    }

    private void handleDeleteBook(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!"ADMIN".equals(request.getSession().getAttribute("userRole"))) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Only admins can delete books.");
            return;
        }
        dao.BookDAO bookDAO = new dao.BookDAO();
        String idParam = request.getParameter("id");
        if (idParam != null) {
            int bookId = Integer.parseInt(idParam);
            bookDAO.deleteBook(bookId);
        }
        response.sendRedirect(request.getContextPath() + "/controller/books");
    }

    private void handleViewBook(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        dao.BookDAO bookDAO = new dao.BookDAO();
        String idParam = request.getParameter("id");
        if (idParam == null) {
            response.sendRedirect(request.getContextPath() + "/controller/books");
            return;
        }
        int bookId = Integer.parseInt(idParam);
        model.Book book = bookDAO.getBookById(bookId);
        request.setAttribute("book", book);
        request.getRequestDispatcher("/jsp/admin/view-book.jsp").forward(request, response);
    }
} 