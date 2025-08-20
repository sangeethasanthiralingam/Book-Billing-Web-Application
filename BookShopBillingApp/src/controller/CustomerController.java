package controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;

import dao.BillDAO;
import dao.UserDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Bill;
import model.BillItem;
import model.User;

/**
 * Controller for customer-related operations
 */
public class CustomerController extends BaseController {
    
    /**
     * Handle customer listing with search and filters
     */
    public void handleCustomers(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        try {
            UserDAO userDAO = new UserDAO();
            BillDAO billDAO = new BillDAO();

            String searchTerm = getParameter(request, "search", "");
            String statusFilter = getParameter(request, "status", "");
            String sortBy = getParameter(request, "sortBy", "");

            List<User> allCustomers = userDAO.getUsersByRole("CUSTOMER");
            List<User> filteredCustomers = filterCustomers(allCustomers, searchTerm, statusFilter);

            // Build customer stats
            List<Map<String, Object>> customerStats = buildCustomerStats(filteredCustomers, billDAO);

            // Apply sorting
            if (sortBy != null && !sortBy.trim().isEmpty()) {
                sortCustomerStats(customerStats, sortBy, billDAO);
            }

            request.setAttribute("customerStats", customerStats);
            addCommonAttributes(request);
            
        } catch (Exception e) {
            handleException(request, response, e, "loading customers");
        }
        request.getRequestDispatcher("/jsp/customers.jsp").forward(request, response);
    }
    
    /**
     * Handle customer form (add/edit)
     */
    public void handleCustomerForm(HttpServletRequest request, HttpServletResponse response) 
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
            } catch (NumberFormatException e) {
                handleException(request, response, e, "loading customer");
            }
        }

        request.setAttribute("customer", customer);
        addCommonAttributes(request);
        request.getRequestDispatcher("/jsp/customer-form.jsp").forward(request, response);
    }
    
    /**
     * Handle customer details view
     */
    public void handleCustomerDetails(HttpServletRequest request, HttpServletResponse response) 
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
            } catch (NumberFormatException e) {
                handleException(request, response, e, "loading customer details");
            }
        }
        request.getRequestDispatcher("/jsp/customer-details.jsp").forward(request, response);
    }
    
    /**
     * Handle customer deletion (deactivation)
     */
    public void handleDeleteCustomer(HttpServletRequest request, HttpServletResponse response) 
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
                        response.sendRedirect(request.getContextPath() + 
                            "/controller/customers?message=Customer deactivated successfully");
                    } else {
                        response.sendRedirect(request.getContextPath() + 
                            "/controller/customers?error=Failed to deactivate customer");
                    }
                } else {
                    response.sendRedirect(request.getContextPath() + 
                        "/controller/customers?error=Customer not found");
                }
            } catch (IOException | NumberFormatException e) {
                response.sendRedirect(request.getContextPath() + 
                    "/controller/customers?error=Error deactivating customer: " + e.getMessage());
            }
        } else {
            response.sendRedirect(request.getContextPath() + "/controller/customers?error=Invalid customer ID");
        }
    }
    
    /**
     * Handle customer dashboard
     */
    public void handleCustomerDashboard(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String userRole = getUserRole(request);
        Integer userId = getUserId(request);
        
        if (!"CUSTOMER".equals(userRole) || userId == null) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Only customers can view their dashboard.");
            return;
        }
        
        try {
            UserDAO userDAO = new UserDAO();
            BillDAO billDAO = new BillDAO();
            User customer = userDAO.getUserById(userId);
            List<Bill> bills = billDAO.getBillsByCustomer(userId);
            
            double totalSpent = 0.0;
            for (Bill bill : bills) {
                totalSpent += bill.getTotal();
            }
            
            request.setAttribute("customer", customer);
            request.setAttribute("bills", bills);
            request.setAttribute("totalSpent", totalSpent);
            request.setAttribute("billCount", bills.size());
            addCommonAttributes(request);
            
        } catch (Exception e) {
            handleException(request, response, e, "loading customer dashboard");
        }
        request.getRequestDispatcher("/jsp/customer-dashboard.jsp").forward(request, response);
    }
    
    /**
     * Handle customer profile management
     */
    public void handleCustomerProfile(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String userRole = getUserRole(request);
        Integer userId = getUserId(request);
        
        if (!"CUSTOMER".equals(userRole) || userId == null) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Only customers can view their profile.");
            return;
        }
        
        UserDAO userDAO = new UserDAO();
        
        if ("POST".equalsIgnoreCase(request.getMethod())) {
            String fullName = request.getParameter("fullName");
            String email = request.getParameter("email");
            String phone = request.getParameter("phone");
            String address = request.getParameter("address");
            
            User customer = userDAO.getUserById(userId);
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
        
        User customer = userDAO.getUserById(userId);
        request.setAttribute("customer", customer);
        addCommonAttributes(request);
        request.getRequestDispatcher("/jsp/customer-profile.jsp").forward(request, response);
    }
    
    /**
     * Handle customer purchases view
     */
    public void handleCustomerPurchases(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String customerIdParam = request.getParameter("id");

        if (customerIdParam == null || customerIdParam.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/controller/customers");
            return;
        }

        try {
            int customerId = Integer.parseInt(customerIdParam);
            UserDAO userDAO = new UserDAO();
            BillDAO billDAO = new BillDAO();

            User customer = userDAO.getUserById(customerId);
            if (customer == null) {
                request.setAttribute("error", "Customer not found");
                request.getRequestDispatcher("/jsp/error.jsp").forward(request, response);
                return;
            }

            List<Bill> bills = billDAO.getBillsByCustomer(customerId);
            Map<String, Object> purchaseStats = billDAO.getCustomerPurchaseStats(customerId);

            double totalSpent = 0.0;
            int totalItems = 0;
            Map<String, Integer> bookPurchaseCount = new HashMap<>();

            for (Bill bill : bills) {
                totalSpent += bill.getTotal();
                List<BillItem> items = billDAO.getBillItems(bill.getId());
                bill.setItems(items);

                for (BillItem item : items) {
                    totalItems += item.getQuantity();
                    String bookTitle = item.getBook().getTitle();
                    bookPurchaseCount.put(bookTitle, 
                        bookPurchaseCount.getOrDefault(bookTitle, 0) + item.getQuantity());
                }
            }

            bills.sort((b1, b2) -> b2.getBillDate().compareTo(b1.getBillDate()));

            request.setAttribute("customer", customer);
            request.setAttribute("bills", bills);
            request.setAttribute("totalSpent", totalSpent);
            request.setAttribute("totalItems", totalItems);
            request.setAttribute("billCount", bills.size());
            request.setAttribute("bookPurchaseCount", bookPurchaseCount);
            request.setAttribute("purchaseStats", purchaseStats);
            addCommonAttributes(request);

        } catch (ServletException | IOException | NumberFormatException e) {
            handleException(request, response, e, "loading customer purchases");
        }

        request.getRequestDispatcher("/jsp/customer-purchases.jsp").forward(request, response);
    }
    
    /**
     * Handle customer password reset
     */
    public void handleCustomerResetPassword(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String userRole = getUserRole(request);
        Integer userId = getUserId(request);
        
        if (!"CUSTOMER".equals(userRole) || userId == null) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Only customers can reset their password.");
            return;
        }
        
        UserDAO userDAO = new UserDAO();
        
        if ("POST".equalsIgnoreCase(request.getMethod())) {
            String currentPassword = request.getParameter("currentPassword");
            String newPassword = request.getParameter("newPassword");
            String confirmPassword = request.getParameter("confirmPassword");
            
            User customer = userDAO.getUserById(userId);
            if (!customer.getPassword().equals(currentPassword)) {
                request.setAttribute("error", "Current password is incorrect.");
            } else if (!newPassword.equals(confirmPassword)) {
                request.setAttribute("error", "New passwords do not match.");
            } else {
                customer.setPassword(newPassword);
                userDAO.updateUser(customer);
                request.setAttribute("success", "Password updated successfully.");
            }
        }
        
        request.getRequestDispatcher("/jsp/customer-reset-password.jsp").forward(request, response);
    }
    
    /**
     * Handle customer store page
     */
    public void handleCustomerStore(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        try {
            dao.BookDAO bookDAO = new dao.BookDAO();
            String search = getParameter(request, "search", "");
            String category = getParameter(request, "category", "");
            String priceRange = getParameter(request, "priceRange", "");
            
            List<model.Book> books = bookDAO.getAllBooks();
            
            // Apply filters
            books = filterBooksForStore(books, search, category, priceRange);
            
            // Get unique categories
            java.util.Set<String> categories = new java.util.HashSet<>();
            List<model.Book> allBooks = bookDAO.getAllBooks();
            for (model.Book book : allBooks) {
                if (book.getCategory() != null && !book.getCategory().trim().isEmpty()) {
                    categories.add(book.getCategory());
                }
            }
            
            request.setAttribute("books", books);
            request.setAttribute("categories", categories);
            addCommonAttributes(request);
            
            request.getRequestDispatcher("/jsp/store.jsp").forward(request, response);
            
        } catch (ServletException | IOException e) {
            handleException(request, response, e, "loading customer store");
        }
    }
    
    /**
     * Handle customer order placement
     */
    public void handleCustomerPlaceOrder(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String userRole = getUserRole(request);
        Integer userId = getUserId(request);
        
        if (!"CUSTOMER".equals(userRole) || userId == null) {
            sendJsonError(response, "Only customers can place orders");
            return;
        }
        
        try {
            // Read JSON from request body
            java.io.BufferedReader reader = request.getReader();
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            String jsonData = sb.toString();
            
            // Parse order data
            org.json.JSONObject orderData = new org.json.JSONObject(jsonData);
            int customerId = orderData.getInt("customerId");
            String paymentMethod = orderData.getString("paymentMethod");
            boolean isDelivery = orderData.optBoolean("isDelivery", false);
            String deliveryAddress = orderData.optString("deliveryAddress", "");
            org.json.JSONArray itemsArray = orderData.getJSONArray("items");
            
            // Validate customer
            if (customerId != userId) {
                sendJsonError(response, "Invalid customer ID");
                return;
            }
            
            // Get customer and create bill
            dao.UserDAO userDAO = new dao.UserDAO();
            User customer = userDAO.getUserById(customerId);
            
            if (customer == null) {
                sendJsonError(response, "Customer not found");
                return;
            }
            
            // Create bill items and calculate totals
            dao.BookDAO bookDAO = new dao.BookDAO();
            List<model.BillItem> billItems = new ArrayList<>();
            double subtotal = 0.0;
            
            for (int i = 0; i < itemsArray.length(); i++) {
                org.json.JSONObject itemData = itemsArray.getJSONObject(i);
                int bookId = itemData.getInt("bookId");
                int quantity = itemData.getInt("quantity");
                double price = itemData.getDouble("price");
                
                // Validate book and stock
                model.Book book = bookDAO.getBookById(bookId);
                if (book == null) {
                    sendJsonError(response, "Book not found: " + bookId);
                    return;
                }
                
                if (book.getQuantity() < quantity) {
                    sendJsonError(response, "Insufficient stock for: " + book.getTitle());
                    return;
                }
                
                // Create bill item
                model.BillItem billItem = new model.BillItem();
                billItem.setBook(book);
                billItem.setQuantity(quantity);
                billItem.setUnitPrice(price);
                billItem.setTotal(quantity * price);
                
                billItems.add(billItem);
                subtotal += billItem.getTotal();
            }
            
            // Calculate bill totals
            double tax = subtotal * 0.10; // 10% tax
            double total = subtotal + tax;
            
            // Create bill
            model.Bill bill = new model.Bill();
            bill.setBillNumber("ORD-" + System.currentTimeMillis());
            bill.setCustomer(customer);
            bill.setCashier(customer); // Customer is both customer and cashier for self-service
            bill.setSubtotal(subtotal);
            bill.setDiscount(0.0);
            bill.setTax(tax);
            bill.setTotal(total);
            bill.setPaymentMethod(paymentMethod);
            bill.setStatus("PENDING");
            bill.setDelivery(isDelivery);
            bill.setDeliveryAddress(deliveryAddress);
            bill.setItems(billItems);
            
            // Save bill
            dao.BillDAO billDAO = new dao.BillDAO();
            boolean success = billDAO.saveBill(bill);
            
            if (success) {
                // Update book quantities
                for (model.BillItem item : billItems) {
                    bookDAO.updateQuantity(item.getBook().getId(), item.getQuantity());
                }
                
                // Send success response
                org.json.JSONObject response_data = new org.json.JSONObject();
                response_data.put("success", true);
                response_data.put("billId", bill.getId());
                response_data.put("billNumber", bill.getBillNumber());
                response_data.put("total", total);
                response_data.put("message", "Order placed successfully");
                
                sendJsonResponse(response, response_data.toString());
            } else {
                sendJsonError(response, "Failed to place order");
            }
            
        } catch (IOException | JSONException e) {
            sendJsonError(response, "Error placing order: " + e.getMessage());
        }
    }
    
    /**
     * Handle customer order confirmation
     */
    public void handleCustomerOrderConfirmation(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String userRole = getUserRole(request);
        Integer userId = getUserId(request);
        
        if (!"CUSTOMER".equals(userRole) || userId == null) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access denied");
            return;
        }
        
        String billIdStr = request.getParameter("billId");
        if (billIdStr == null || billIdStr.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/controller/customer-dashboard");
            return;
        }
        
        try {
            int billId = Integer.parseInt(billIdStr);
            dao.BillDAO billDAO = new dao.BillDAO();
            model.Bill bill = billDAO.getBillById(billId);
            
            // Verify this bill belongs to the current customer
            if (bill == null || bill.getCustomer().getId() != userId) {
                response.sendRedirect(request.getContextPath() + "/controller/customer-dashboard");
                return;
            }
            
            List<model.BillItem> billItems = billDAO.getBillItems(billId);
            
            request.setAttribute("bill", bill);
            request.setAttribute("billItems", billItems);
            addCommonAttributes(request);
            
        } catch (IOException | NumberFormatException e) {
            handleException(request, response, e, "loading order confirmation");
        }
        
        request.getRequestDispatcher("/jsp/customer-order-confirmation.jsp").forward(request, response);
    }
    
    /**
     * Handle getting book details for modal (AJAX)
     */
    public void handleGetBookDetails(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        try {
            String bookIdStr = request.getParameter("id");
            if (bookIdStr == null || bookIdStr.trim().isEmpty()) {
                sendJsonError(response, "Book ID is required");
                return;
            }
            
            int bookId = Integer.parseInt(bookIdStr);
            dao.BookDAO bookDAO = new dao.BookDAO();
            model.Book book = bookDAO.getBookById(bookId);
            
            if (book == null) {
                sendJsonError(response, "Book not found");
                return;
            }
            
            // Convert book to JSON
            org.json.JSONObject bookJson = new org.json.JSONObject();
            bookJson.put("id", book.getId());
            bookJson.put("title", book.getTitle());
            bookJson.put("author", book.getAuthor());
            bookJson.put("isbn", book.getIsbn());
            bookJson.put("price", book.getPrice());
            bookJson.put("quantity", book.getQuantity());
            bookJson.put("category", book.getCategory());
            bookJson.put("publisher", book.getPublisher());
            bookJson.put("publicationYear", book.getPublicationYear());
            bookJson.put("language", book.getLanguage());
            bookJson.put("coverImage", book.getCoverImage());
            
            sendJsonResponse(response, bookJson.toString());
            
        } catch (IOException | NumberFormatException | JSONException e) {
            sendJsonError(response, "Error fetching book details: " + e.getMessage());
        }
    }
    
    /**
     * Handle viewing a single customer
     */
    public void handleViewCustomer(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String userIdParam = request.getParameter("id");

        if (userIdParam == null || userIdParam.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/controller/customers");
            return;
        }

        try {
            int userId = Integer.parseInt(userIdParam);
            UserDAO userDAO = new UserDAO();
            BillDAO billDAO = new BillDAO();

            User customer = userDAO.getUserById(userId);
            if (customer == null) {
                request.setAttribute("error", "Customer not found");
                request.getRequestDispatcher("/jsp/error.jsp").forward(request, response);
                return;
            }

            if (!"CUSTOMER".equals(customer.getRole())) {
                request.setAttribute("error", "User is not a customer");
                request.getRequestDispatcher("/jsp/error.jsp").forward(request, response);
                return;
            }

            List<Bill> bills = billDAO.getBillsByCustomer(userId);
            double totalSpent = 0.0;
            int totalBooksCount = 0;
            for (Bill bill : bills) {
                totalSpent += bill.getTotal();
                List<BillItem> items = billDAO.getBillItems(bill.getId());
                for (BillItem item : items) {
                    totalBooksCount += item.getQuantity();
                }
            }

            request.setAttribute("user", customer);
            request.setAttribute("bills", bills);
            request.setAttribute("totalSpent", totalSpent);
            request.setAttribute("totalBooksCount", totalBooksCount);
            request.setAttribute("billCount", bills.size());
            addCommonAttributes(request);

        } catch (ServletException | IOException | NumberFormatException e) {
            handleException(request, response, e, "loading customer details");
        }

        request.getRequestDispatcher("/jsp/view-user.jsp").forward(request, response);
    }
    
    /**
     * Handle saving customer (create/update)
     */
    public void handleSaveCustomer(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        try {
            String idParam = request.getParameter("id");
            String username = request.getParameter("username");
            String email = request.getParameter("email");
            String fullName = request.getParameter("fullName");
            String phone = request.getParameter("phone");
            String address = request.getParameter("address");
            String password = request.getParameter("password");

            if (!validateRequiredParams(request, "username", "email", "fullName", "phone", "address", "password")) {
                request.setAttribute("error", "All fields are required.");
                request.getRequestDispatcher("/jsp/customer-form.jsp").forward(request, response);
                return;
            }

            if (password.length() < 6) {
                request.setAttribute("error", "Password must be at least 6 characters long.");
                request.getRequestDispatcher("/jsp/customer-form.jsp").forward(request, response);
                return;
            }

            UserDAO userDAO = new UserDAO();
            User customer;

            if (idParam != null && !idParam.isEmpty()) {
                // Update existing customer
                int customerId = Integer.parseInt(idParam);
                customer = userDAO.getUserById(customerId);
                if (customer == null) {
                    request.setAttribute("error", "Customer not found.");
                    request.getRequestDispatcher("/jsp/customer-form.jsp").forward(request, response);
                    return;
                }

                // Check for duplicate username/email
                if (!username.equals(customer.getUsername()) && userDAO.getUserByUsername(username) != null) {
                    request.setAttribute("error", "Username already exists. Please choose a different username.");
                    request.setAttribute("customer", customer);
                    request.getRequestDispatcher("/jsp/customer-form.jsp").forward(request, response);
                    return;
                }

                if (!email.equals(customer.getEmail()) && userDAO.getUserByEmail(email) != null) {
                    request.setAttribute("error", "Email already registered. Please use a different email.");
                    request.setAttribute("customer", customer);
                    request.getRequestDispatcher("/jsp/customer-form.jsp").forward(request, response);
                    return;
                }

                customer.setUsername(username);
                customer.setEmail(email);
                customer.setFullName(fullName);
                customer.setPhone(phone);
                customer.setAddress(address);
                customer.setPassword(password);

                boolean updated = userDAO.updateUser(customer);
                if (updated) {
                    response.sendRedirect(request.getContextPath() + 
                        "/controller/customers?message=Customer updated successfully");
                } else {
                    request.setAttribute("error", "Failed to update customer.");
                    request.setAttribute("customer", customer);
                    request.getRequestDispatcher("/jsp/customer-form.jsp").forward(request, response);
                }
            } else {
                // Create new customer
                if (userDAO.getUserByUsername(username) != null) {
                    request.setAttribute("error", "Username already exists. Please choose a different username.");
                    request.getRequestDispatcher("/jsp/customer-form.jsp").forward(request, response);
                    return;
                }

                if (userDAO.getUserByEmail(email) != null) {
                    request.setAttribute("error", "Email already registered. Please use a different email.");
                    request.getRequestDispatcher("/jsp/customer-form.jsp").forward(request, response);
                    return;
                }

                customer = new User();
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
                    response.sendRedirect(request.getContextPath() + 
                        "/controller/customers?message=Customer created successfully");
                } else {
                    request.setAttribute("error", "Failed to create customer.");
                    request.getRequestDispatcher("/jsp/customer-form.jsp").forward(request, response);
                }
            }

        } catch (ServletException | IOException | NumberFormatException e) {
            handleException(request, response, e, "saving customer");
        }
    }
    
    /**
     * Handle customer search (AJAX)
     */
    public void handleSearchCustomers(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        try {
            String searchTerm = request.getParameter("term");
            if (searchTerm == null || searchTerm.trim().isEmpty()) {
                sendJsonResponse(response, "[]");
                return;
            }

            UserDAO userDAO = new UserDAO();
            List<User> allCustomers = userDAO.getUsersByRole("CUSTOMER");
            List<User> matchingCustomers = new ArrayList<>();

            String term = searchTerm.trim().toLowerCase();
            for (User customer : allCustomers) {
                if ((customer.getFullName() != null && customer.getFullName().toLowerCase().contains(term)) ||
                        (customer.getEmail() != null && customer.getEmail().toLowerCase().contains(term)) ||
                        (customer.getPhone() != null && customer.getPhone().toLowerCase().contains(term)) ||
                        (customer.getAccountNumber() != null && customer.getAccountNumber().toLowerCase().contains(term))) {
                    matchingCustomers.add(customer);
                }
            }

            // Convert to JSON
            StringBuilder json = new StringBuilder("[");
            for (int i = 0; i < matchingCustomers.size(); i++) {
                User customer = matchingCustomers.get(i);
                json.append("{");
                json.append("\"id\":").append(customer.getId()).append(",");
                json.append("\"fullName\":\"").append(customer.getFullName() != null ? 
                    customer.getFullName().replace("\"", "\\\"") : "").append("\",");
                json.append("\"email\":\"").append(customer.getEmail() != null ? 
                    customer.getEmail().replace("\"", "\\\"") : "").append("\",");
                json.append("\"phone\":\"").append(customer.getPhone() != null ? 
                    customer.getPhone().replace("\"", "\\\"") : "").append("\",");
                json.append("\"accountNumber\":\"").append(customer.getAccountNumber() != null ? 
                    customer.getAccountNumber().replace("\"", "\\\"") : "").append("\"");
                json.append("}");
                if (i < matchingCustomers.size() - 1) {
                    json.append(",");
                }
            }
            json.append("]");

            sendJsonResponse(response, json.toString());

        } catch (IOException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            sendJsonError(response, e.getMessage());
        }
    }
    
    /**
     * Handle creating customer via AJAX
     */
    public void handleCreateCustomer(HttpServletRequest request, HttpServletResponse response) 
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

            // Simple JSON parsing
            String fullName = extractJsonValue(jsonData, "fullName");
            String email = extractJsonValue(jsonData, "email");
            String phone = extractJsonValue(jsonData, "phone");
            String address = extractJsonValue(jsonData, "address");

            if (fullName == null || email == null || phone == null) {
                sendJsonError(response, "Required fields missing");
                return;
            }

            UserDAO userDAO = new UserDAO();

            if (userDAO.getUserByEmail(email) != null) {
                sendJsonError(response, "Email already registered");
                return;
            }

            // Create new customer
            User customer = new User();
            customer.setUsername(email);
            customer.setPassword("password123");
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
                String json = "{\"success\":true,\"customer\":{" +
                        "\"id\":" + customer.getId() + "," +
                        "\"fullName\":\"" + customer.getFullName().replace("\"", "\\\"") + "\"," +
                        "\"email\":\"" + customer.getEmail().replace("\"", "\\\"") + "\"," +
                        "\"phone\":\"" + customer.getPhone().replace("\"", "\\\"") + "\"," +
                        "\"accountNumber\":\"" + customer.getAccountNumber().replace("\"", "\\\"") + "\"" +
                        "}}";
                sendJsonResponse(response, json);
            } else {
                sendJsonError(response, "Failed to create customer");
            }

        } catch (IOException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            sendJsonError(response, e.getMessage());
        }
    }
    
    /**
     * Handle account number generation for AJAX (plain text)
     */
    public void handleGenerateAccountNumber(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        String accountNumber = "ACC-" + System.currentTimeMillis();
        response.setContentType("text/plain");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(accountNumber);
    }
    
    // Helper methods
    
    private List<User> filterCustomers(List<User> customers, String searchTerm, String statusFilter) {
        List<User> filtered = new ArrayList<>();
        for (User customer : customers) {
            boolean matches = true;

            // Search filter
            if (searchTerm != null && !searchTerm.trim().isEmpty()) {
                String term = searchTerm.trim().toLowerCase();
                matches &= (customer.getFullName() != null && customer.getFullName().toLowerCase().contains(term)) ||
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
                filtered.add(customer);
            }
        }
        return filtered;
    }
    
    private List<Map<String, Object>> buildCustomerStats(List<User> customers, BillDAO billDAO) {
        List<Map<String, Object>> customerStats = new ArrayList<>();
        for (User customer : customers) {
            Map<String, Object> stats = new HashMap<>();
            stats.put("customer", customer);
            List<Bill> bills = billDAO.getBillsByCustomer(customer.getId());
            stats.put("billCount", bills.size());
            double totalSpent = 0.0;
            for (Bill bill : bills) {
                totalSpent += bill.getTotal();
            }
            stats.put("totalSpent", totalSpent);
            customerStats.add(stats);
        }
        return customerStats;
    }
    
    private void sortCustomerStats(List<Map<String, Object>> customerStats, String sortBy, BillDAO billDAO) {
        switch (sortBy) {
            case "name" -> customerStats.sort((a, b) -> {
                    User userA = (User) a.get("customer");
                    User userB = (User) b.get("customer");
                    return userA.getFullName().compareToIgnoreCase(userB.getFullName());
                });
            case "email" -> customerStats.sort((a, b) -> {
                    User userA = (User) a.get("customer");
                    User userB = (User) b.get("customer");
                    return userA.getEmail().compareToIgnoreCase(userB.getEmail());
                });
            case "bills" -> customerStats.sort((a, b) -> Integer.compare((Integer) b.get("billCount"), (Integer) a.get("billCount")));
            case "spent" -> customerStats.sort((a, b) -> Double.compare((Double) b.get("totalSpent"), (Double) a.get("totalSpent")));
            case "recent" -> customerStats.sort((a, b) -> {
                    List<Bill> billsA = billDAO.getBillsByCustomer(((User) a.get("customer")).getId());
                    List<Bill> billsB = billDAO.getBillsByCustomer(((User) b.get("customer")).getId());

                    if (billsA.isEmpty() && billsB.isEmpty()) return 0;
                    if (billsA.isEmpty()) return 1;
                    if (billsB.isEmpty()) return -1;

                    java.util.Date latestA = billsA.stream().map(Bill::getBillDate)
                            .max(java.util.Date::compareTo).orElse(new java.util.Date(0));
                    java.util.Date latestB = billsB.stream().map(Bill::getBillDate)
                            .max(java.util.Date::compareTo).orElse(new java.util.Date(0));
                    return latestB.compareTo(latestA);
                });
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
            
        }
        return null;
    }
    
    private List<model.Book> filterBooksForStore(List<model.Book> books, String search, String category, String priceRange) {
        List<model.Book> filtered = new ArrayList<>();
        
        for (model.Book book : books) {
            boolean matches = true;
            
            // Search filter
            if (search != null && !search.trim().isEmpty()) {
                String term = search.trim().toLowerCase();
                matches &= (book.getTitle() != null && book.getTitle().toLowerCase().contains(term)) ||
                          (book.getAuthor() != null && book.getAuthor().toLowerCase().contains(term)) ||
                          (book.getIsbn() != null && book.getIsbn().toLowerCase().contains(term));
            }
            
            // Category filter
            if (category != null && !category.trim().isEmpty()) {
                matches &= category.equals(book.getCategory());
            }
            
            // Price range filter
            if (priceRange != null && !priceRange.trim().isEmpty()) {
                double price = book.getPrice();
                switch (priceRange) {
                    case "0-500" -> matches &= price <= 500;
                    case "500-1000" -> matches &= price > 500 && price <= 1000;
                    case "1000-2000" -> matches &= price > 1000 && price <= 2000;
                    case "2000+" -> matches &= price > 2000;
                }
            }
            
            if (matches) {
                filtered.add(book);
            }
        }
        
        return filtered;
    }
} 