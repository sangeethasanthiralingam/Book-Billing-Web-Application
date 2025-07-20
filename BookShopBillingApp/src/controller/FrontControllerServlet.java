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
import model.Book;
import model.Bill;
import model.BillItem;
import model.User;
import java.util.List;
import service.ConfigurationService;
import java.util.Map;
import util.MailUtil;
import org.json.JSONArray;
import org.json.JSONObject;

@WebServlet("/controller/*")
public class FrontControllerServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        System.out.println("DEBUG: doGet method called");
        System.out.println("DEBUG: Request URI: " + request.getRequestURI());
        System.out.println("DEBUG: Request URL: " + request.getRequestURL());
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

        System.out.println("DEBUG: PathInfo: " + pathInfo);
        System.out.println("DEBUG: Action: " + action);
        System.out.println("DEBUG: Request URI: " + request.getRequestURI());
        System.out.println("DEBUG: Request URL: " + request.getRequestURL());
        System.out.println("DEBUG: Query String: " + request.getQueryString());

        // Check if user is authenticated (except for login and register)
        System.out.println("DEBUG: Checking authentication for action: " + action);
        if (!action.equals("login") && !action.equals("register") && !isAuthenticated(request)) {
            System.out.println("DEBUG: User not authenticated, redirecting to login");
            response.sendRedirect(request.getContextPath() + "/jsp/login.jsp");
            return;
        }
        System.out.println("DEBUG: User is authenticated or action doesn't require authentication");

        try {
            System.out.println("DEBUG: About to switch on action: " + action);
            System.out.println("DEBUG: Action length: " + action.length());
            System.out.println("DEBUG: Action bytes: " + java.util.Arrays.toString(action.getBytes()));
            System.out.println("DEBUG: Starting action handling");
            // Use if-else instead of switch to avoid compilation issues
            if ("login".equals(action)) {
                System.out.println("DEBUG: About to call handleLogin method");
                handleLogin(request, response);
                System.out.println("DEBUG: handleLogin method completed");
            } else if ("logout".equals(action)) {
                handleLogout(request, response);
            } else if ("add-user".equals(action)) {
                handleAddUser(request, response);
            } else if ("dashboard".equals(action)) {
                handleDashboard(request, response);
            } else if ("books".equals(action)) {
                handleBooks(request, response);
            } else if ("billing".equals(action)) {
                handleBilling(request, response);
            } else if ("invoice".equals(action)) {
                handleInvoice(request, response);
            } else if ("reports".equals(action)) {
                handleReports(request, response);
            } else if ("customers".equals(action)) {
                System.out.println("DEBUG: Matched customers case");
                handleCustomers(request, response);
            } else if ("customer-form".equals(action)) {
                handleCustomerForm(request, response);
            } else if ("customer-details".equals(action)) {
                handleCustomerDetails(request, response);
            } else if ("delete-customer".equals(action)) {
                handleDeleteCustomer(request, response);
            } else if ("help".equals(action)) {
                handleHelp(request, response);
            } else if ("system-config".equals(action)) {
                handleSystemConfig(request, response);
            } else if ("update-configs".equals(action)) {
                handleUpdateConfigs(request, response);
            } else if ("cashiers".equals(action)) {
                handleCashiers(request, response);
            } else if ("customer-dashboard".equals(action)) {
                handleCustomerDashboard(request, response);
            } else if ("leaderboard".equals(action)) {
                handleCashierLeaderboard(request, response);
            } else if ("customer-profile".equals(action)) {
                handleCustomerProfile(request, response);
            } else if ("users".equals(action)) {
                handleUsers(request, response);
            } else if ("edit-user".equals(action)) {
                handleEditUser(request, response);
            } else if ("delete-user".equals(action)) {
                handleDeleteUser(request, response);
            } else if ("customer-reset-password".equals(action)) {
                handleCustomerResetPassword(request, response);
            } else if ("add-book".equals(action)) {
                handleAddBook(request, response);
            } else if ("edit-book".equals(action)) {
                handleEditBook(request, response);
            } else if ("delete-book".equals(action)) {
                handleDeleteBook(request, response);
            } else if ("view-book".equals(action)) {
                System.out.println("DEBUG: Matched view-book case");
                handleViewBook(request, response);
            } else if ("view-customer".equals(action)) {
                System.out.println("DEBUG: Matched view-customer case");
                handleViewCustomer(request, response);
            } else if ("customer-purchases".equals(action)) {
                System.out.println("DEBUG: Executing customer-purchases case");
                handleCustomerPurchases(request, response);
            } else if ("register".equals(action)) {
                System.out.println("DEBUG: Executing register case");
                handleRegister(request, response);
            } else if ("search-books".equals(action)) {
                System.out.println("DEBUG: Executing search-books case");
                handleSearchBooks(request, response);
            } else if ("save-customer".equals(action)) {
                System.out.println("DEBUG: Executing save-customer case");
                handleSaveCustomer(request, response);
            } else if ("search-customers".equals(action)) {
                System.out.println("DEBUG: Executing search-customers case");
                handleSearchCustomers(request, response);
            } else if ("create-customer".equals(action)) {
                System.out.println("DEBUG: Executing create-customer case");
                handleCreateCustomer(request, response);
            } else if ("generate-bill".equals(action)) {
                System.out.println("DEBUG: Executing generate-bill case");
                handleGenerateBill(request, response);
            } else if ("send-collection".equals(action)) {
                handleSendCollection(request, response);
            } else if ("store".equals(action)) {
                handleStore(request, response);
            } else {
                System.out.println("DEBUG: Executing default case, redirecting to dashboard");
                HttpSession session = request.getSession(false);
                String userRole = (session != null) ? (String) session.getAttribute("userRole") : null;
                if ("CUSTOMER".equals(userRole)) {
                    response.sendRedirect(request.getContextPath() + "/controller/customer-dashboard");
                } else {
                    response.sendRedirect(request.getContextPath() + "/controller/dashboard");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "An error occurred: " + e.getMessage());
            request.getRequestDispatcher("/jsp/error.jsp").forward(request, response);
        }
    }

    private boolean isAuthenticated(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        System.out.println("DEBUG: Session: " + session);
        if (session != null) {
            System.out.println("DEBUG: User attribute: " + session.getAttribute("user"));
            System.out.println("DEBUG: User role: " + session.getAttribute("userRole"));
        }
        return session != null && session.getAttribute("user") != null;
    }

    private void handleLogin(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        System.out.println("DEBUG: handleLogin method called");
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        System.out.println("DEBUG: Username: " + username);
        System.out.println("DEBUG: Password: " + (password != null ? "***" : "null"));

        if (username != null && password != null) {
            try {
                System.out.println("DEBUG: About to create UserDAO");
                UserDAO userDAO = new UserDAO();
                System.out.println("DEBUG: About to call authenticateUser");
                User user = userDAO.authenticateUser(username, password);
                System.out.println("DEBUG: authenticateUser returned: " + (user != null ? "User object" : "null"));

                if (user != null) {
                    System.out.println("DEBUG: User authenticated successfully");
                    System.out.println("DEBUG: User role: " + user.getRole());
                    HttpSession session = request.getSession();
                    session.setAttribute("user", user.getFullName());
                    session.setAttribute("userId", user.getId());
                    session.setAttribute("userRole", user.getRole());

                    if ("CUSTOMER".equals(user.getRole())) {
                        System.out.println("DEBUG: Redirecting customer to customer-dashboard");
                        response.sendRedirect(request.getContextPath() + "/controller/customer-dashboard");
                    } else {
                        System.out.println("DEBUG: Redirecting to dashboard");
                        response.sendRedirect(request.getContextPath() + "/controller/dashboard");
                    }
                } else {
                    System.out.println("DEBUG: Authentication failed");
                    request.setAttribute("error", "Invalid username or password");
                    request.getRequestDispatcher("/jsp/login.jsp").forward(request, response);
                }
            } catch (Exception e) {
                System.out.println("DEBUG: Exception in handleLogin: " + e.getMessage());
                e.printStackTrace();
                request.setAttribute("error", "Login error: " + e.getMessage());
                request.getRequestDispatcher("/jsp/login.jsp").forward(request, response);
            }
        } else {
            System.out.println("DEBUG: Username or password is null");
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
        request.getRequestDispatcher("/jsp/dashboard.jsp").forward(request, response);
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
                if (matches)
                    filtered.add(book);
            }
            int page = 1;
            int pageSize = 10;
            String pageParam = request.getParameter("page");
            if (pageParam != null) {
                try {
                    page = Integer.parseInt(pageParam);
                } catch (Exception ignored) {
                }
                if (page < 1)
                    page = 1;
            }
            int totalBooks = filtered.size();
            int totalPages = (int) Math.ceil((double) totalBooks / pageSize);
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
        request.getRequestDispatcher("/jsp/books.jsp").forward(request, response);
    }

    private void handleBilling(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            BookDAO bookDAO = new BookDAO();
            dao.UserDAO userDAO = new dao.UserDAO();
            List<Book> books = bookDAO.getAllBooks();
            List<model.User> customers = userDAO.getUsersByRole("CUSTOMER");
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
        request.getRequestDispatcher("/jsp/reports.jsp").forward(request, response);
    }

    private void handleCustomers(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        System.out.println("DEBUG: handleCustomers method called");
        try {
            dao.UserDAO userDAO = new dao.UserDAO();
            dao.BillDAO billDAO = new dao.BillDAO();

            // Get search and filter parameters
            String searchTerm = request.getParameter("search");
            String statusFilter = request.getParameter("status");
            String sortBy = request.getParameter("sortBy");

            System.out.println("DEBUG: Search term: " + searchTerm);
            System.out.println("DEBUG: Status filter: " + statusFilter);
            System.out.println("DEBUG: Sort by: " + sortBy);

            // Get all customers
            java.util.List<model.User> allCustomers = userDAO.getUsersByRole("CUSTOMER");
            System.out.println("DEBUG: Found " + allCustomers.size() + " total customers");

            // Apply filters
            java.util.List<model.User> filteredCustomers = new java.util.ArrayList<>();
            for (model.User customer : allCustomers) {
                boolean matches = true;

                // Search filter
                if (searchTerm != null && !searchTerm.trim().isEmpty()) {
                    String term = searchTerm.trim().toLowerCase();
                    matches &= (customer.getFullName() != null && customer.getFullName().toLowerCase().contains(term))
                            ||
                            (customer.getEmail() != null && customer.getEmail().toLowerCase().contains(term)) ||
                            (customer.getPhone() != null && customer.getPhone().toLowerCase().contains(term));
                }

                // Status filter
                if (statusFilter != null && !statusFilter.trim().isEmpty()) {
                    if ("active".equals(statusFilter)) {
                        matches &= customer.isActive();
                    } else if ("inactive".equals(statusFilter)) {
                        matches &= !customer.isActive();
                    }
                }

                if (matches) {
                    filteredCustomers.add(customer);
                }
            }

            System.out.println("DEBUG: After filtering: " + filteredCustomers.size() + " customers");

            // Build customer stats
            java.util.List<java.util.Map<String, Object>> customerStats = new java.util.ArrayList<>();
            for (model.User customer : filteredCustomers) {
                java.util.Map<String, Object> stats = new java.util.HashMap<>();
                stats.put("customer", customer);
                java.util.List<model.Bill> bills = billDAO.getBillsByCustomer(customer.getId());
                stats.put("billCount", bills.size());
                double totalSpent = 0.0;
                for (model.Bill bill : bills)
                    totalSpent += bill.getTotal();
                stats.put("totalSpent", totalSpent);
                customerStats.add(stats);
            }

            // Apply sorting
            if (sortBy != null && !sortBy.trim().isEmpty()) {
                switch (sortBy) {
                    case "name":
                        customerStats.sort((a, b) -> {
                            model.User userA = (model.User) a.get("customer");
                            model.User userB = (model.User) b.get("customer");
                            return userA.getFullName().compareToIgnoreCase(userB.getFullName());
                        });
                        break;
                    case "email":
                        customerStats.sort((a, b) -> {
                            model.User userA = (model.User) a.get("customer");
                            model.User userB = (model.User) b.get("customer");
                            return userA.getEmail().compareToIgnoreCase(userB.getEmail());
                        });
                        break;
                    case "bills":
                        customerStats.sort(
                                (a, b) -> Integer.compare((Integer) b.get("billCount"), (Integer) a.get("billCount")));
                        break;
                    case "spent":
                        customerStats.sort(
                                (a, b) -> Double.compare((Double) b.get("totalSpent"), (Double) a.get("totalSpent")));
                        break;
                    case "recent":
                        // Sort by most recent bills (if any)
                        customerStats.sort((a, b) -> {
                            java.util.List<model.Bill> billsA = billDAO
                                    .getBillsByCustomer(((model.User) a.get("customer")).getId());
                            java.util.List<model.Bill> billsB = billDAO
                                    .getBillsByCustomer(((model.User) b.get("customer")).getId());

                            if (billsA.isEmpty() && billsB.isEmpty())
                                return 0;
                            if (billsA.isEmpty())
                                return 1;
                            if (billsB.isEmpty())
                                return -1;

                            java.util.Date latestA = billsA.stream().map(model.Bill::getBillDate)
                                    .max(java.util.Date::compareTo).orElse(new java.util.Date(0));
                            java.util.Date latestB = billsB.stream().map(model.Bill::getBillDate)
                                    .max(java.util.Date::compareTo).orElse(new java.util.Date(0));
                            return latestB.compareTo(latestA);
                        });
                        break;
                }
            }

            request.setAttribute("customerStats", customerStats);
            request.setAttribute("systemName", service.ConfigurationService.getInstance().getSystemName());
            System.out.println("DEBUG: About to forward to customers.jsp");
        } catch (Exception e) {
            System.out.println("DEBUG: Exception in handleCustomers: " + e.getMessage());
            e.printStackTrace();
            request.setAttribute("error", "Error loading customers: " + e.getMessage());
        }
        request.getRequestDispatcher("/jsp/customers.jsp").forward(request, response);
    }

    private void handleCustomerForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String customerId = request.getParameter("id");
        User customer = null;

        if (customerId != null && !customerId.isEmpty()) {
            try {
                UserDAO userDAO = new UserDAO();
                customer = userDAO.getUserById(Integer.parseInt(customerId));
                if (customer == null) {
                    request.setAttribute("error", "Customer not found");
                }
            } catch (Exception e) {
                e.printStackTrace();
                request.setAttribute("error", "Error loading customer: " + e.getMessage());
            }
        }

        // Always set the customer attribute, even if null
        request.setAttribute("customer", customer);
        request.setAttribute("systemName", ConfigurationService.getInstance().getSystemName());
        request.getRequestDispatcher("/jsp/customer-form.jsp").forward(request, response);
    }

    private void handleCustomerDetails(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String customerId = request.getParameter("id");
        if (customerId != null && !customerId.isEmpty()) {
            try {
                UserDAO userDAO = new UserDAO();
                User customer = userDAO.getUserById(Integer.parseInt(customerId));
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
                UserDAO userDAO = new UserDAO();
                User customer = userDAO.getUserById(Integer.parseInt(customerId));
                if (customer != null) {
                    customer.setActive(false);
                    boolean updated = userDAO.updateUser(customer);
                    if (updated) {
                        response.sendRedirect(request.getContextPath()
                                + "/controller/customers?message=Customer deactivated successfully");
                    } else {
                        response.sendRedirect(
                                request.getContextPath() + "/controller/customers?error=Failed to deactivate customer");
                    }
                } else {
                    response.sendRedirect(request.getContextPath() + "/controller/customers?error=Customer not found");
                }
            } catch (Exception e) {
                e.printStackTrace();
                response.sendRedirect(request.getContextPath()
                        + "/controller/customers?error=Error deactivating customer: " + e.getMessage());
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

    private void handleAddUser(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
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

                // Only allow operational employees (ADMIN and CASHIER)
                if ("CUSTOMER".equals(userRole)) {
                    request.setAttribute("error",
                            "Customers cannot be created through user management. Use customer registration instead.");
                    request.getRequestDispatcher("/jsp/add-user.jsp").forward(request, response);
                    return;
                }

                // Set operational employee specific values (no customer fields)
                user.setAddress(""); // Empty for operational employees
                user.setAccountNumber(null); // No account number for employees
                user.setUnitsConsumed(0); // No units consumed for employees
                user.setActive(true);

                dao.UserDAO userDAO = new dao.UserDAO();
                boolean success = userDAO.addUser(user);
                if (success) {
                    request.setAttribute("success", "Employee created successfully.");
                } else {
                    request.setAttribute("error", "Failed to create employee.");
                }
            } else {
                request.setAttribute("error", "All fields except phone are required.");
            }
        }
        request.getRequestDispatcher("/jsp/add-user.jsp").forward(request, response);
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
        request.getRequestDispatcher("/jsp/cashiers.jsp").forward(request, response);
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
        request.getRequestDispatcher("/jsp/customer-dashboard.jsp").forward(request, response);
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
            leaderboard.sort((a, b) -> Double.compare((double) b.get("totalSales"), (double) a.get("totalSales")));
            request.setAttribute("leaderboard", leaderboard);
            request.setAttribute("systemName", service.ConfigurationService.getInstance().getSystemName());
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Error loading leaderboard: " + e.getMessage());
        }
        request.getRequestDispatcher("/jsp/leaderboard.jsp").forward(request, response);
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
        request.getRequestDispatcher("/jsp/customer-profile.jsp").forward(request, response);
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

        // Only get operational employees (ADMIN and CASHIER), exclude CUSTOMER
        java.util.List<model.User> allUsers = userDAO.getAllUsers();
        java.util.List<model.User> operationalUsers = new java.util.ArrayList<>();

        // Filter out customers, only keep operational employees
        for (model.User user : allUsers) {
            if (!"CUSTOMER".equals(user.getRole())) {
                operationalUsers.add(user);
            }
        }

        java.util.List<model.User> filtered = new java.util.ArrayList<>();
        for (model.User user : operationalUsers) {
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
            if (matches)
                filtered.add(user);
        }
        int page = 1;
        int pageSize = 10;
        String pageParam = request.getParameter("page");
        if (pageParam != null) {
            try {
                page = Integer.parseInt(pageParam);
            } catch (Exception ignored) {
            }
            if (page < 1)
                page = 1;
        }
        int totalUsers = filtered.size();
        int totalPages = (int) Math.ceil((double) totalUsers / pageSize);
        int fromIndex = (page - 1) * pageSize;
        int toIndex = Math.min(fromIndex + pageSize, totalUsers);
        java.util.List<model.User> pagedUsers = filtered.subList(fromIndex < totalUsers ? fromIndex : 0, toIndex);
        request.setAttribute("users", pagedUsers);
        request.setAttribute("search", search);
        request.setAttribute("roleFilter", roleFilter);
        request.setAttribute("page", page);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("totalUsers", totalUsers);
        request.getRequestDispatcher("/jsp/users.jsp").forward(request, response);
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
                // Prevent changing users to customers
                if ("CUSTOMER".equals(role)) {
                    request.setAttribute("error", "Users cannot be changed to customers through user management.");
                    request.setAttribute("user", user);
                    request.setAttribute("systemName", service.ConfigurationService.getInstance().getSystemName());
                    request.getRequestDispatcher("/jsp/edit-user.jsp").forward(request, response);
                    return;
                }

                user.setFullName(fullName);
                user.setEmail(email);
                user.setPhone(phone);
                user.setAddress(address);
                user.setRole(role);

                // Ensure customer-specific fields are not set for operational employees
                if (!"CUSTOMER".equals(role)) {
                    user.setAccountNumber(null);
                    user.setUnitsConsumed(0);
                }

                boolean updated = userDAO.updateUser(user);
                if (updated) {
                    request.setAttribute("success", "Employee updated successfully.");
                } else {
                    request.setAttribute("error", "Failed to update employee.");
                }
            }
        }
        model.User user = userDAO.getUserById(userId);
        request.setAttribute("user", user);
        request.setAttribute("systemName", service.ConfigurationService.getInstance().getSystemName());
        request.getRequestDispatcher("/jsp/edit-user.jsp").forward(request, response);
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
        String action = request.getParameter("action");

        if (idParam != null) {
            int userId = Integer.parseInt(idParam);

            if ("activate".equals(action)) {
                // Activate the user
                model.User user = userDAO.getUserById(userId);
                if (user != null) {
                    user.setActive(true);
                    userDAO.updateUser(user);
                }
                response.sendRedirect(request.getContextPath() + "/controller/customers");
            } else {
                // Delete/deactivate the user
                userDAO.deleteUser(userId);
                response.sendRedirect(request.getContextPath() + "/controller/users");
            }
        } else {
            response.sendRedirect(request.getContextPath() + "/controller/users");
        }
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
        request.getRequestDispatcher("/jsp/customer-reset-password.jsp").forward(request, response);
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
            String publisher = request.getParameter("publisher");
            String publicationYearStr = request.getParameter("publicationYear");
            String language = request.getParameter("language");

            double price = 0.0;
            int quantity = 0;
            Integer publicationYear = null;

            try {
                price = Double.parseDouble(priceStr);
            } catch (Exception ignored) {
            }
            try {
                quantity = Integer.parseInt(quantityStr);
            } catch (Exception ignored) {
            }
            try {
                publicationYear = Integer.parseInt(publicationYearStr);
            } catch (Exception ignored) {
            }

            model.Book book = new model.Book();
            book.setTitle(title);
            book.setAuthor(author);
            book.setIsbn(isbn);
            book.setPrice(price);
            book.setQuantity(quantity);
            book.setCategory(category);
            book.setPublisher(publisher);
            book.setPublicationYear(publicationYear);
            book.setLanguage(language != null ? language : "English");
            dao.BookDAO bookDAO = new dao.BookDAO();
            boolean success = bookDAO.addBook(book);
            if (success) {
                request.setAttribute("success", "Book added successfully.");
            } else {
                request.setAttribute("error", "Failed to add book.");
            }
        }
        request.getRequestDispatcher("/jsp/add-book.jsp").forward(request, response);
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
            String publisher = request.getParameter("publisher");
            String publicationYearStr = request.getParameter("publicationYear");
            String language = request.getParameter("language");

            double price = 0.0;
            int quantity = 0;
            Integer publicationYear = null;

            try {
                price = Double.parseDouble(priceStr);
            } catch (Exception ignored) {
            }
            try {
                quantity = Integer.parseInt(quantityStr);
            } catch (Exception ignored) {
            }
            try {
                publicationYear = Integer.parseInt(publicationYearStr);
            } catch (Exception ignored) {
            }

            model.Book book = bookDAO.getBookById(bookId);
            if (book != null) {
                book.setTitle(title);
                book.setAuthor(author);
                book.setIsbn(isbn);
                book.setPrice(price);
                book.setQuantity(quantity);
                book.setCategory(category);
                book.setPublisher(publisher);
                book.setPublicationYear(publicationYear);
                book.setLanguage(language != null ? language : "English");
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
        request.getRequestDispatcher("/jsp/edit-book.jsp").forward(request, response);
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
        request.getRequestDispatcher("/jsp/view-book.jsp").forward(request, response);
    }

    private void handleCustomerPurchases(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String customerIdParam = request.getParameter("id");
        System.out.println("DEBUG: Customer ID parameter: " + customerIdParam);

        if (customerIdParam == null || customerIdParam.isEmpty()) {
            System.out.println("DEBUG: Customer ID is null or empty, redirecting to customers");
            response.sendRedirect(request.getContextPath() + "/controller/customers");
            return;
        }

        try {
            int customerId = Integer.parseInt(customerIdParam);
            System.out.println("DEBUG: Parsed customer ID: " + customerId);

            dao.UserDAO userDAO = new dao.UserDAO();
            dao.BillDAO billDAO = new dao.BillDAO();

            // Get customer details
            System.out.println("DEBUG: About to call userDAO.getUserById(" + customerId + ")");
            model.User customer = userDAO.getUserById(customerId);
            System.out.println("DEBUG: Customer found: " + (customer != null ? customer.getFullName() : "null"));

            if (customer == null) {
                System.out.println("DEBUG: Customer not found, showing error page");
                request.setAttribute("error", "Customer not found");
                request.getRequestDispatcher("/jsp/error.jsp").forward(request, response);
                return;
            }

            // Get all bills for this customer with items
            System.out.println("DEBUG: About to call billDAO.getBillsByCustomer(" + customerId + ")");
            java.util.List<model.Bill> bills = billDAO.getBillsByCustomer(customerId);
            System.out.println("DEBUG: Found " + bills.size() + " bills for customer");

            // Get detailed purchase statistics
            java.util.Map<String, Object> purchaseStats = billDAO.getCustomerPurchaseStats(customerId);
            System.out.println("DEBUG: Purchase stats: " + purchaseStats);

            // Calculate total statistics
            double totalSpent = 0.0;
            int totalItems = 0;
            java.util.Map<String, Integer> bookPurchaseCount = new java.util.HashMap<>();

            for (model.Bill bill : bills) {
                totalSpent += bill.getTotal();
                // Get bill items for detailed analysis
                java.util.List<model.BillItem> items = billDAO.getBillItems(bill.getId());
                bill.setItems(items);

                for (model.BillItem item : items) {
                    totalItems += item.getQuantity();
                    String bookTitle = item.getBook().getTitle();
                    bookPurchaseCount.put(bookTitle,
                            bookPurchaseCount.getOrDefault(bookTitle, 0) + item.getQuantity());
                }
            }

            // Sort bills by date (most recent first)
            bills.sort((b1, b2) -> b2.getBillDate().compareTo(b1.getBillDate()));

            request.setAttribute("customer", customer);
            request.setAttribute("bills", bills);
            request.setAttribute("totalSpent", totalSpent);
            request.setAttribute("totalItems", totalItems);
            request.setAttribute("billCount", bills.size());
            request.setAttribute("bookPurchaseCount", bookPurchaseCount);
            request.setAttribute("purchaseStats", purchaseStats);
            request.setAttribute("systemName", service.ConfigurationService.getInstance().getSystemName());

            System.out.println("DEBUG: About to forward to customer-purchases.jsp");

        } catch (Exception e) {
            System.out.println("DEBUG: Exception in handleCustomerPurchases: " + e.getMessage());
            e.printStackTrace();
            request.setAttribute("error", "Error loading customer purchases: " + e.getMessage());
            // Don't redirect, just show the error page
            request.getRequestDispatcher("/jsp/error.jsp").forward(request, response);
            return;
        }

        System.out.println("DEBUG: Forwarding to customer-purchases.jsp");
        request.getRequestDispatcher("/jsp/customer-purchases.jsp").forward(request, response);
    }

    private void handleViewCustomer(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        System.out.println("DEBUG: handleViewCustomer method called");
        String userIdParam = request.getParameter("id");
        System.out.println("DEBUG: View Customer - User ID parameter: " + userIdParam);

        if (userIdParam == null || userIdParam.isEmpty()) {
            System.out.println("DEBUG: User ID is null or empty, redirecting to customers");
            response.sendRedirect(request.getContextPath() + "/controller/customers");
            return;
        }

        try {
            int userId = Integer.parseInt(userIdParam);
            System.out.println("DEBUG: Parsed user ID: " + userId);

            dao.UserDAO userDAO = new dao.UserDAO();
            dao.BillDAO billDAO = new dao.BillDAO();

            // Get customer details (User with role CUSTOMER)
            System.out.println("DEBUG: About to call userDAO.getUserById(" + userId + ")");
            model.User customer = userDAO.getUserById(userId);
            System.out.println("DEBUG: Customer found: " + (customer != null ? customer.getFullName() : "null"));

            if (customer == null) {
                System.out.println("DEBUG: Customer not found, showing error page");
                request.setAttribute("error", "Customer not found");
                request.getRequestDispatcher("/jsp/error.jsp").forward(request, response);
                return;
            }

            // Verify this is actually a customer
            if (!"CUSTOMER".equals(customer.getRole())) {
                System.out.println("DEBUG: User is not a customer, showing error page");
                request.setAttribute("error", "User is not a customer");
                request.getRequestDispatcher("/jsp/error.jsp").forward(request, response);
                return;
            }

            // Get customer's bills for statistics
            java.util.List<model.Bill> bills = billDAO.getBillsByCustomer(userId);
            double totalSpent = 0.0;
            for (model.Bill bill : bills) {
                totalSpent += bill.getTotal();
            }

            request.setAttribute("user", customer);
            request.setAttribute("bills", bills);
            request.setAttribute("totalSpent", totalSpent);
            request.setAttribute("billCount", bills.size());
            request.setAttribute("systemName", service.ConfigurationService.getInstance().getSystemName());

            System.out.println("DEBUG: Forwarding to view-user.jsp");

        } catch (Exception e) {
            System.out.println("DEBUG: Exception in handleViewCustomer: " + e.getMessage());
            e.printStackTrace();
            request.setAttribute("error", "Error loading customer details: " + e.getMessage());
            request.getRequestDispatcher("/jsp/error.jsp").forward(request, response);
            return;
        }

        request.getRequestDispatcher("/jsp/view-user.jsp").forward(request, response);
    }

    private void handleRegister(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if ("POST".equalsIgnoreCase(request.getMethod())) {
            // Process registration form
            String username = request.getParameter("username");
            String email = request.getParameter("email");
            String fullName = request.getParameter("fullName");
            String phone = request.getParameter("phone");
            String address = request.getParameter("address");
            String password = request.getParameter("password");
            String confirmPassword = request.getParameter("confirmPassword");

            // Validation
            if (username == null || email == null || fullName == null || phone == null ||
                    address == null || password == null || confirmPassword == null) {
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
                System.out.println("DEBUG: Starting customer registration process");
                dao.UserDAO userDAO = new dao.UserDAO();

                System.out.println("DEBUG: Registration data - Username: " + username + ", Email: " + email
                        + ", FullName: " + fullName);

                // Check if username already exists
                System.out.println("DEBUG: Checking if username exists: " + username);
                if (userDAO.getUserByUsername(username) != null) {
                    System.out.println("DEBUG: Username already exists");
                    request.setAttribute("error", "Username already exists. Please choose a different username.");
                    request.getRequestDispatcher("/jsp/register.jsp").forward(request, response);
                    return;
                }
                System.out.println("DEBUG: Username is available");

                // Check if email already exists
                System.out.println("DEBUG: Checking if email exists: " + email);
                if (userDAO.getUserByEmail(email) != null) {
                    System.out.println("DEBUG: Email already exists");
                    request.setAttribute("error", "Email already registered. Please use a different email or login.");
                    request.getRequestDispatcher("/jsp/register.jsp").forward(request, response);
                    return;
                }
                System.out.println("DEBUG: Email is available");

                // Create new customer user
                System.out.println("DEBUG: Creating new customer user object");
                model.User newCustomer = new model.User();
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

                System.out.println("DEBUG: Customer object created: " + newCustomer.toString());
                System.out.println("DEBUG: About to call userDAO.addUser()");

                boolean success = userDAO.addUser(newCustomer);
                System.out.println("DEBUG: addUser() returned: " + success);

                if (success) {
                    System.out.println("DEBUG: Registration successful, redirecting to login");
                    request.setAttribute("success",
                            "Registration successful! You can now login with your username and password.");
                    request.getRequestDispatcher("/jsp/login.jsp").forward(request, response);
                } else {
                    System.out.println("DEBUG: Registration failed, showing error");
                    request.setAttribute("error", "Registration failed. Please try again.");
                    request.getRequestDispatcher("/jsp/register.jsp").forward(request, response);
                }

            } catch (Exception e) {
                System.out.println("DEBUG: Exception during registration: " + e.getMessage());
                e.printStackTrace();
                request.setAttribute("error", "Registration error: " + e.getMessage());
                request.getRequestDispatcher("/jsp/register.jsp").forward(request, response);
            }
        } else {
            // GET request - show registration form
            request.getRequestDispatcher("/jsp/register.jsp").forward(request, response);
        }
    }

    private void handleSearchBooks(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            dao.BookDAO bookDAO = new dao.BookDAO();
            String searchTerm = request.getParameter("search");
            String category = request.getParameter("category");
            String language = request.getParameter("language");
            String publisher = request.getParameter("publisher");
            String minPriceStr = request.getParameter("minPrice");
            String maxPriceStr = request.getParameter("maxPrice");

            double minPrice = 0.0;
            double maxPrice = Double.MAX_VALUE;

            try {
                minPrice = Double.parseDouble(minPriceStr);
            } catch (Exception ignored) {
            }
            try {
                maxPrice = Double.parseDouble(maxPriceStr);
            } catch (Exception ignored) {
            }

            // Get all books and filter based on criteria
            java.util.List<Book> allBooks = bookDAO.getAllBooks();
            java.util.List<Book> filteredBooks = new java.util.ArrayList<>();

            for (Book book : allBooks) {
                boolean matches = true;

                // Search term filter
                if (searchTerm != null && !searchTerm.trim().isEmpty()) {
                    String term = searchTerm.trim().toLowerCase();
                    matches &= (book.getTitle() != null && book.getTitle().toLowerCase().contains(term)) ||
                            (book.getAuthor() != null && book.getAuthor().toLowerCase().contains(term)) ||
                            (book.getIsbn() != null && book.getIsbn().toLowerCase().contains(term));
                }

                // Category filter
                if (category != null && !category.trim().isEmpty() && !"ALL".equals(category)) {
                    matches &= category.equals(book.getCategory());
                }

                // Language filter
                if (language != null && !language.trim().isEmpty() && !"ALL".equals(language)) {
                    matches &= language.equals(book.getLanguage());
                }

                // Publisher filter
                if (publisher != null && !publisher.trim().isEmpty()) {
                    matches &= (book.getPublisher() != null
                            && book.getPublisher().toLowerCase().contains(publisher.toLowerCase()));
                }

                // Price range filter
                if (book.getPrice() < minPrice || book.getPrice() > maxPrice) {
                    matches = false;
                }

                if (matches) {
                    filteredBooks.add(book);
                }
            }

            request.setAttribute("books", filteredBooks);
            request.setAttribute("search", searchTerm);
            request.setAttribute("category", category);
            request.setAttribute("language", language);
            request.setAttribute("publisher", publisher);
            request.setAttribute("minPrice", minPriceStr);
            request.setAttribute("maxPrice", maxPriceStr);
            request.setAttribute("totalBooks", filteredBooks.size());

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Error performing search: " + e.getMessage());
        }

        request.getRequestDispatcher("/jsp/books.jsp").forward(request, response);
    }

    private void handleSaveCustomer(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            String idParam = request.getParameter("id");
            String username = request.getParameter("username");
            String email = request.getParameter("email");
            String fullName = request.getParameter("fullName");
            String phone = request.getParameter("phone");
            String address = request.getParameter("address");
            String password = request.getParameter("password");

            // Validation
            if (username == null || email == null || fullName == null || phone == null ||
                    address == null || password == null) {
                request.setAttribute("error", "All fields are required.");
                request.getRequestDispatcher("/jsp/customer-form.jsp").forward(request, response);
                return;
            }

            if (password.length() < 6) {
                request.setAttribute("error", "Password must be at least 6 characters long.");
                request.getRequestDispatcher("/jsp/customer-form.jsp").forward(request, response);
                return;
            }

            dao.UserDAO userDAO = new dao.UserDAO();
            model.User customer;

            if (idParam != null && !idParam.isEmpty()) {
                // Update existing customer
                int customerId = Integer.parseInt(idParam);
                customer = userDAO.getUserById(customerId);
                if (customer == null) {
                    request.setAttribute("error", "Customer not found.");
                    request.getRequestDispatcher("/jsp/customer-form.jsp").forward(request, response);
                    return;
                }

                // Check if username is being changed and if it already exists
                if (!username.equals(customer.getUsername())) {
                    if (userDAO.getUserByUsername(username) != null) {
                        request.setAttribute("error", "Username already exists. Please choose a different username.");
                        request.setAttribute("customer", customer);
                        request.getRequestDispatcher("/jsp/customer-form.jsp").forward(request, response);
                        return;
                    }
                }

                // Check if email is being changed and if it already exists
                if (!email.equals(customer.getEmail())) {
                    if (userDAO.getUserByEmail(email) != null) {
                        request.setAttribute("error", "Email already registered. Please use a different email.");
                        request.setAttribute("customer", customer);
                        request.getRequestDispatcher("/jsp/customer-form.jsp").forward(request, response);
                        return;
                    }
                }

                customer.setUsername(username);
                customer.setEmail(email);
                customer.setFullName(fullName);
                customer.setPhone(phone);
                customer.setAddress(address);
                customer.setPassword(password);

                boolean updated = userDAO.updateUser(customer);
                if (updated) {
                    response.sendRedirect(
                            request.getContextPath() + "/controller/customers?message=Customer updated successfully");
                } else {
                    request.setAttribute("error", "Failed to update customer.");
                    request.setAttribute("customer", customer);
                    request.getRequestDispatcher("/jsp/customer-form.jsp").forward(request, response);
                }
            } else {
                // Create new customer
                // Check if username already exists
                if (userDAO.getUserByUsername(username) != null) {
                    request.setAttribute("error", "Username already exists. Please choose a different username.");
                    request.getRequestDispatcher("/jsp/customer-form.jsp").forward(request, response);
                    return;
                }

                // Check if email already exists
                if (userDAO.getUserByEmail(email) != null) {
                    request.setAttribute("error", "Email already registered. Please use a different email.");
                    request.getRequestDispatcher("/jsp/customer-form.jsp").forward(request, response);
                    return;
                }

                customer = new model.User();
                customer.setUsername(username);
                customer.setPassword(password);
                customer.setEmail(email);
                customer.setFullName(fullName);
                customer.setPhone(phone);
                customer.setAddress(address);
                customer.setRole("CUSTOMER");
                customer.setAccountNumber("ACC-" + System.currentTimeMillis());
                customer.setUnitsConsumed(0);
                customer.setActive(true);

                boolean added = userDAO.addUser(customer);
                if (added) {
                    response.sendRedirect(
                            request.getContextPath() + "/controller/customers?message=Customer created successfully");
                } else {
                    request.setAttribute("error", "Failed to create customer.");
                    request.getRequestDispatcher("/jsp/customer-form.jsp").forward(request, response);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Error saving customer: " + e.getMessage());
            request.getRequestDispatcher("/jsp/customer-form.jsp").forward(request, response);
        }
    }

    private void handleSearchCustomers(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            String searchTerm = request.getParameter("term");
            if (searchTerm == null || searchTerm.trim().isEmpty()) {
                response.setContentType("application/json");
                response.getWriter().write("[]");
                return;
            }

            dao.UserDAO userDAO = new dao.UserDAO();
            java.util.List<model.User> allCustomers = userDAO.getUsersByRole("CUSTOMER");
            java.util.List<model.User> matchingCustomers = new java.util.ArrayList<>();

            String term = searchTerm.trim().toLowerCase();
            for (model.User customer : allCustomers) {
                if ((customer.getFullName() != null && customer.getFullName().toLowerCase().contains(term)) ||
                        (customer.getEmail() != null && customer.getEmail().toLowerCase().contains(term)) ||
                        (customer.getPhone() != null && customer.getPhone().toLowerCase().contains(term)) ||
                        (customer.getAccountNumber() != null
                                && customer.getAccountNumber().toLowerCase().contains(term))) {
                    matchingCustomers.add(customer);
                }
            }

            // Convert to JSON
            StringBuilder json = new StringBuilder("[");
            for (int i = 0; i < matchingCustomers.size(); i++) {
                model.User customer = matchingCustomers.get(i);
                json.append("{");
                json.append("\"id\":").append(customer.getId()).append(",");
                json.append("\"fullName\":\"")
                        .append(customer.getFullName() != null ? customer.getFullName().replace("\"", "\\\"") : "")
                        .append("\",");
                json.append("\"email\":\"")
                        .append(customer.getEmail() != null ? customer.getEmail().replace("\"", "\\\"") : "")
                        .append("\",");
                json.append("\"phone\":\"")
                        .append(customer.getPhone() != null ? customer.getPhone().replace("\"", "\\\"") : "")
                        .append("\",");
                json.append("\"accountNumber\":\"").append(
                        customer.getAccountNumber() != null ? customer.getAccountNumber().replace("\"", "\\\"") : "")
                        .append("\"");
                json.append("}");
                if (i < matchingCustomers.size() - 1) {
                    json.append(",");
                }
            }
            json.append("]");

            response.setContentType("application/json");
            response.getWriter().write(json.toString());

        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\":\"" + e.getMessage().replace("\"", "\\\"") + "\"}");
        }
    }

    private void handleCreateCustomer(HttpServletRequest request, HttpServletResponse response)
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

            // Simple JSON parsing (in production, use a proper JSON library)
            String fullName = extractJsonValue(jsonData, "fullName");
            String email = extractJsonValue(jsonData, "email");
            String phone = extractJsonValue(jsonData, "phone");
            String address = extractJsonValue(jsonData, "address");

            // Validation
            if (fullName == null || email == null || phone == null) {
                response.setContentType("application/json");
                response.getWriter().write("{\"success\":false,\"message\":\"Required fields missing\"}");
                return;
            }

            dao.UserDAO userDAO = new dao.UserDAO();

            // Check if email already exists
            if (userDAO.getUserByEmail(email) != null) {
                response.setContentType("application/json");
                response.getWriter().write("{\"success\":false,\"message\":\"Email already registered\"}");
                return;
            }

            // Create new customer
            model.User customer = new model.User();
            customer.setUsername(email); // Use email as username
            customer.setPassword("password123"); // Default password
            customer.setEmail(email);
            customer.setFullName(fullName);
            customer.setPhone(phone);
            customer.setAddress(address != null ? address : "");
            customer.setRole("CUSTOMER");
            customer.setAccountNumber("ACC-" + System.currentTimeMillis());
            customer.setUnitsConsumed(0);
            customer.setActive(true);

            boolean success = userDAO.addUser(customer);

            if (success) {
                response.setContentType("application/json");
                response.getWriter().write("{\"success\":true,\"customer\":{" +
                        "\"id\":" + customer.getId() + "," +
                        "\"fullName\":\"" + customer.getFullName().replace("\"", "\\\"") + "\"," +
                        "\"email\":\"" + customer.getEmail().replace("\"", "\\\"") + "\"," +
                        "\"phone\":\"" + customer.getPhone().replace("\"", "\\\"") + "\"," +
                        "\"accountNumber\":\"" + customer.getAccountNumber().replace("\"", "\\\"") + "\"" +
                        "}}");
            } else {
                response.setContentType("application/json");
                response.getWriter().write("{\"success\":false,\"message\":\"Failed to create customer\"}");
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.setContentType("application/json");
            response.getWriter()
                    .write("{\"success\":false,\"message\":\"" + e.getMessage().replace("\"", "\\\"") + "\"}");
        }
    }

    private void handleGenerateBill(HttpServletRequest request, HttpServletResponse response)
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

            // Extract bill data
            String customerIdStr = extractJsonValue(jsonData, "customerId");
            String paymentMethod = extractJsonValue(jsonData, "paymentMethod");
            String isDeliveryStr = extractJsonValue(jsonData, "isDelivery");
            String deliveryAddress = extractJsonValue(jsonData, "deliveryAddress");

            if (customerIdStr == null || paymentMethod == null) {
                response.setContentType("application/json");
                response.getWriter().write("{\"success\":false,\"message\":\"Missing required data\"}");
                return;
            }

            int customerId = Integer.parseInt(customerIdStr);
            boolean isDelivery = "true".equals(isDeliveryStr);

            // Get customer
            dao.UserDAO userDAO = new dao.UserDAO();
            model.User customer = userDAO.getUserById(customerId);
            if (customer == null) {
                response.setContentType("application/json");
                response.getWriter().write("{\"success\":false,\"message\":\"Customer not found\"}");
                return;
            }

            // Get current user (cashier)
            HttpSession session = request.getSession(false);
            Integer cashierId = (session != null) ? (Integer) session.getAttribute("userId") : null;
            if (cashierId == null) {
                response.setContentType("application/json");
                response.getWriter().write("{\"success\":false,\"message\":\"User session not found\"}");
                return;
            }

            model.User cashier = userDAO.getUserById(cashierId);
            if (cashier == null) {
                response.setContentType("application/json");
                response.getWriter().write("{\"success\":false,\"message\":\"Cashier not found\"}");
                return;
            }

            // Parse items from JSON (simplified - in production, use proper JSON parsing)
            java.util.List<model.BillItem> items = new java.util.ArrayList<>();
            // For now, we'll create a simple bill with basic data
            // In a real implementation, you'd parse the items array from JSON

            // Create bill
            model.Bill bill = new model.Bill();
            bill.setBillNumber("BILL-" + System.currentTimeMillis());
            bill.setCustomer(customer);
            bill.setCashier(cashier);
            bill.setPaymentMethod(paymentMethod);
            bill.setDelivery(isDelivery);
            bill.setDeliveryAddress(deliveryAddress != null ? deliveryAddress : "");
            bill.setStatus("PAID");

            // Calculate totals (simplified)
            double subtotal = 100.0; // This should be calculated from actual items
            double discount = subtotal > 100 ? subtotal * 0.05 : 0;
            double tax = (subtotal - discount) * 0.10;
            double deliveryCharge = isDelivery ? 5.0 : 0.0;
            double total = subtotal - discount + tax + deliveryCharge;

            bill.setSubtotal(subtotal);
            bill.setDiscount(discount);
            bill.setTax(tax);
            bill.setTotal(total);
            bill.setDeliveryCharge(deliveryCharge);

            // Save bill
            dao.BillDAO billDAO = new dao.BillDAO();
            boolean success = billDAO.saveBill(bill);

            if (success) {
                response.setContentType("application/json");
                response.getWriter().write("{\"success\":true,\"billId\":" + bill.getId() + ",\"billNumber\":\""
                        + bill.getBillNumber() + "\"}");
            } else {
                response.setContentType("application/json");
                response.getWriter().write("{\"success\":false,\"message\":\"Failed to save bill\"}");
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.setContentType("application/json");
            response.getWriter()
                    .write("{\"success\":false,\"message\":\"" + e.getMessage().replace("\"", "\\\"") + "\"}");
        }
    }

    private String extractJsonValue(String json, String key) {
        try {
            String pattern = "\"" + key + "\"\\s*:\\s*\"([^\"]*)\"";
            java.util.regex.Pattern p = java.util.regex.Pattern.compile(pattern);
            java.util.regex.Matcher m = p.matcher(json);
            if (m.find()) {
                return m.group(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void handleSendCollection(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            StringBuilder sb = new StringBuilder();
            String line;
            java.io.BufferedReader reader = request.getReader();
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            String jsonData = sb.toString();
            // Parse JSON: {books: [...], note: "..."}
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
                if (user != null)
                    customerEmail = user.getEmail();
            }
            // Load admin email from config
            String adminEmail = service.ConfigurationService.getInstance().getAdminEmail();
            // Format HTML email
            StringBuilder html = new StringBuilder();
            html.append("<h3>Book Collection Request from Customer Portal</h3>");
            html.append("<p><b>Customer:</b> " + customerName + " (" + customerEmail + ")</p>");
            if (!note.isEmpty())
                html.append("<p><b>Note:</b> " + note + "</p>");
            html.append(
                    "<table border='1' cellpadding='6' style='border-collapse:collapse;'><tr><th>Title</th><th>Author</th><th>Price</th></tr>");
            for (int i = 0; i < books.length(); i++) {
                JSONObject book = books.getJSONObject(i);
                html.append("<tr><td>" + book.getString("title") + "</td><td>" + book.getString("author")
                        + "</td><td>LKR " + book.get("price") + "</td></tr>");
            }
            html.append("</table>");
            util.MailUtil.sendMailHtml(adminEmail, "Book Collection Request", html.toString());
            // (Optional) Save collection in DB (stub)
            response.setContentType("application/json");
            response.getWriter().write("{\"success\":true,\"message\":\"Collection sent to admin.\"}");
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.setContentType("application/json");
            response.getWriter().write("{\"success\":false,\"message\":\"Failed to send collection: "
                    + e.getMessage().replace("\"", "\\\"") + "\"}");
        }
    }

    private void handleStore(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            BookDAO bookDAO = new BookDAO();
            List<Book> books = bookDAO.getAllBooks();
            // Collect unique categories
            java.util.Set<String> categories = new java.util.HashSet<>();
            for (Book book : books) {
                if (book.getCategory() != null && !book.getCategory().trim().isEmpty()) {
                    categories.add(book.getCategory());
                }
            }
            request.setAttribute("books", books);
            request.setAttribute("categories", categories);
            request.setAttribute("systemName", service.ConfigurationService.getInstance().getSystemName());
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Error loading store: " + e.getMessage());
        }
        request.getRequestDispatcher("/jsp/store.jsp").forward(request, response);
    }
}