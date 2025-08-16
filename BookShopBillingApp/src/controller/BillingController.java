package controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import dao.BillDAO;
import dao.BookDAO;
import dao.UserDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.Bill;
import model.BillItem;
import model.Book;
import model.User;

// Design Pattern Imports
import command.*;
import observer.*;
import state.*;
import decorator.*;
import template.*;
import visitor.*;

/**
 * Enhanced Controller for billing operations using multiple design patterns
 * Integrates: Command, Observer, State, Decorator, Template, and Visitor patterns
 */
public class BillingController extends BaseController {
    
    // Design Pattern Components
    private final OrderInvoker orderInvoker;
    private final OrderManager orderManager;
    
    public BillingController() {
        this.orderInvoker = new OrderInvoker();
        this.orderManager = OrderManager.getInstance();
        
        // Register observers
        orderManager.registerObserver(new InventoryObserver());
        orderManager.registerObserver(new CustomerNotificationObserver("admin@bookshop.com", "Admin"));
    }
    
    /**
     * Handle billing page
     */
    public void handleBilling(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        try {
            BookDAO bookDAO = new BookDAO();
            UserDAO userDAO = new UserDAO();
            BillDAO billDAO = new BillDAO();
            
            List<Book> books = bookDAO.getAllBooks();
            List<User> customers = userDAO.getUsersByRole("CUSTOMER");
            
            // Check if this is from a collection request
            String collectionId = request.getParameter("collectionId");
            if (collectionId != null && !collectionId.isEmpty()) {
                try {
                    int billId = Integer.parseInt(collectionId);
                    Bill collectionRequest = billDAO.getBillById(billId);
                    if (collectionRequest != null) {
                        request.setAttribute("collectionRequest", collectionRequest);
                        request.setAttribute("preSelectedCustomer", collectionRequest.getCustomer());
                        request.setAttribute("preSelectedBooks", collectionRequest.getItems());
                    }
                } catch (NumberFormatException e) {
                    // Invalid collection ID, ignore
                }
            }
            
            request.setAttribute("books", books);
            request.setAttribute("customers", customers);
            addCommonAttributes(request);
            
        } catch (Exception e) {
            handleException(request, response, e, "loading billing data");
        }
        request.getRequestDispatcher("/jsp/billing.jsp").forward(request, response);
    }
    
    /**
     * Handle invoice generation
     */
    public void handleInvoice(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        try {
            String billIdStr = request.getParameter("billId");
            if (billIdStr == null || billIdStr.trim().isEmpty()) {
                request.setAttribute("error", "Bill ID is required");
                request.getRequestDispatcher("/jsp/error.jsp").forward(request, response);
                return;
            }

            int billId = Integer.parseInt(billIdStr);
            BillDAO billDAO = new BillDAO();
            Bill bill = billDAO.getBillById(billId);
            
            if (bill == null) {
                request.setAttribute("error", "Bill not found");
                request.getRequestDispatcher("/jsp/error.jsp").forward(request, response);
                return;
            }

            // Get bill items
            List<BillItem> billItems = billDAO.getBillItems(billId);
            
            // Set attributes for invoice display
            request.setAttribute("bill", bill);
            request.setAttribute("billItems", billItems);
            request.setAttribute("invoiceNumber", bill.getBillNumber());
            request.setAttribute("invoiceDate", bill.getBillDate());
            request.setAttribute("customer", bill.getCustomer());
            request.setAttribute("cashier", bill.getCashier());
            
            addCommonAttributes(request);
            
        } catch (NumberFormatException e) {
            request.setAttribute("error", "Invalid Bill ID format");
            request.getRequestDispatcher("/jsp/error.jsp").forward(request, response);
            return;
        } catch (Exception e) {
            handleException(request, response, e, "generating invoice");
            return;
        }
        
        request.getRequestDispatcher("/jsp/invoice.jsp").forward(request, response);
    }
    
    /**
     * Handle bill generation (AJAX)
     */
    public void handleGenerateBill(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        System.out.println("[BillingController] handleGenerateBill called");
        try {
            // Read JSON from request body
            java.io.BufferedReader reader = request.getReader();
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            String jsonData = sb.toString();
            System.out.println("[BillingController] Received JSON: " + jsonData);

            // Extract bill data
            String customerIdStr = extractJsonValue(jsonData, "customerId");
            String paymentMethod = extractJsonValue(jsonData, "paymentMethod");
            String isDeliveryStr = extractJsonValue(jsonData, "isDelivery");
            String deliveryAddress = extractJsonValue(jsonData, "deliveryAddress");
            
            System.out.println("[BillingController] customerId: '" + customerIdStr + "'");
            System.out.println("[BillingController] paymentMethod: '" + paymentMethod + "'");
            System.out.println("[BillingController] isDelivery: '" + isDeliveryStr + "'");
            System.out.println("[BillingController] deliveryAddress: '" + deliveryAddress + "'");

            if (customerIdStr == null || customerIdStr.trim().isEmpty()) {
                System.out.println("[BillingController] Missing or empty customerId");
                sendJsonError(response, "Missing required data: customerId");
                return;
            }
            
            if (paymentMethod == null || paymentMethod.trim().isEmpty()) {
                System.out.println("[BillingController] Missing or empty paymentMethod");
                sendJsonError(response, "Missing required data: paymentMethod");
                return;
            }
            
            // Extract and parse items array
            List<CartItem> cartItems = parseItemsFromJson(jsonData);
            if (cartItems == null || cartItems.isEmpty()) {
                System.out.println("[BillingController] No items found in cart");
                sendJsonError(response, "No items found in cart");
                return;
            }
            System.out.println("[BillingController] Found " + cartItems.size() + " items in cart");

            int customerId = Integer.parseInt(customerIdStr);
            boolean isDelivery = "true".equals(isDeliveryStr);

            // Get customer
            UserDAO userDAO = new UserDAO();
            User customer = userDAO.getUserById(customerId);
            if (customer == null) {
                sendJsonError(response, "Customer not found");
                return;
            }

            // Get current user (cashier)
            HttpSession session = request.getSession(false);
            Integer cashierId = (session != null) ? (Integer) session.getAttribute("userId") : null;
            if (cashierId == null) {
                sendJsonError(response, "User session not found");
                return;
            }

            User cashier = userDAO.getUserById(cashierId);
            if (cashier == null) {
                sendJsonError(response, "Cashier not found");
                return;
            }

            // Calculate totals from actual cart items
            double subtotal = 0.0;
            for (CartItem item : cartItems) {
                subtotal += item.price * item.quantity;
            }
            
            double discount = subtotal > 100 ? subtotal * 0.05 : 0;
            double tax = (subtotal - discount) * 0.10;
            double deliveryCharge = isDelivery ? 5.0 : 0.0;
            double total = subtotal - discount + tax + deliveryCharge;
            
            System.out.println("[BillingController] Calculated totals - Subtotal: " + subtotal + ", Total: " + total);

            // Create bill
            Bill bill = new Bill();
            bill.setBillNumber("BILL-" + System.currentTimeMillis());
            bill.setCustomer(customer);
            bill.setCashier(cashier);
            bill.setPaymentMethod(paymentMethod);
            bill.setDelivery(isDelivery);
            bill.setDeliveryAddress(deliveryAddress != null ? deliveryAddress : "");
            bill.setStatus("PAID");
            bill.setSubtotal(subtotal);
            bill.setDiscount(discount);
            bill.setTax(tax);
            bill.setTotal(total);
            bill.setDeliveryCharge(deliveryCharge);

            // Save bill without items first to get the bill ID
            BillDAO billDAO = new BillDAO();
            boolean success = saveBillWithItems(bill, cartItems, billDAO);

            if (success) {
                String json = "{\"success\":true,\"billId\":" + bill.getId() + ",\"billNumber\":\"" + 
                             bill.getBillNumber() + "\"}";
                sendJsonResponse(response, json);
                System.out.println("[BillingController] Bill generated successfully: " + bill.getBillNumber());
            } else {
                sendJsonError(response, "Failed to save bill");
            }

        } catch (Exception e) {
            System.out.println("[BillingController] Error generating bill: " + e.getMessage());
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            sendJsonError(response, "Error generating bill: " + e.getMessage());
        }
    }
    
    // Helper methods
    
    /**
     * Handle customer search (AJAX)
     */
    public void handleSearchCustomers(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        System.out.println("[BillingController] handleSearchCustomers called");
        try {
            String searchTerm = request.getParameter("searchTerm");
            if (searchTerm == null) {
                searchTerm = "";
            }
            System.out.println("[BillingController] Search term: " + searchTerm);
            
            UserDAO userDAO = new UserDAO();
            List<User> customers;
            
            if (searchTerm.trim().isEmpty()) {
                System.out.println("[BillingController] Getting all customers");
                customers = userDAO.getUsersByRole("CUSTOMER");
            } else {
                System.out.println("[BillingController] Searching users with term: " + searchTerm);
                customers = userDAO.searchUsers(searchTerm);
                // Filter only customers
                customers.removeIf(user -> !"CUSTOMER".equals(user.getRole()));
            }
            System.out.println("[BillingController] Found " + customers.size() + " customers");
            
            // Convert to JSON manually
            StringBuilder json = new StringBuilder("[");
            for (int i = 0; i < customers.size(); i++) {
                User customer = customers.get(i);
                if (i > 0) json.append(",");
                json.append("{");
                json.append("\"id\":" + customer.getId() + ",");
                json.append("\"fullName\":\"" + escapeJson(customer.getFullName()) + "\",");
                json.append("\"phone\":\"" + escapeJson(customer.getPhone()) + "\",");
                json.append("\"email\":\"" + escapeJson(customer.getEmail()) + "\",");
                json.append("\"accountNumber\":\"" + escapeJson(customer.getAccountNumber()) + "\"");
                json.append("}");
            }
            json.append("]");
            
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(json.toString());
            
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            sendJsonError(response, "Error searching customers: " + e.getMessage());
        }
    }
    
    /**
     * Handle book search (AJAX)
     */
    public void handleSearchBooks(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            String searchTerm = request.getParameter("searchTerm");
            if (searchTerm == null) {
                searchTerm = "";
            }
            
            BookDAO bookDAO = new BookDAO();
            List<Book> books;
            
            if (searchTerm.trim().isEmpty()) {
                books = bookDAO.getAllBooks();
            } else {
                books = bookDAO.searchBooks(searchTerm);
            }
            
            // Convert to JSON manually
            StringBuilder json = new StringBuilder("[");
            for (int i = 0; i < books.size(); i++) {
                Book book = books.get(i);
                if (i > 0) json.append(",");
                json.append("{");
                json.append("\"id\":" + book.getId() + ",");
                json.append("\"title\":\"" + escapeJson(book.getTitle()) + "\",");
                json.append("\"author\":\"" + escapeJson(book.getAuthor()) + "\",");
                json.append("\"isbn\":\"" + escapeJson(book.getIsbn()) + "\",");
                json.append("\"price\":" + book.getPrice() + ",");
                json.append("\"quantity\":" + book.getQuantity());
                json.append("}");
            }
            json.append("]");
            
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(json.toString());
            
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            sendJsonError(response, "Error searching books: " + e.getMessage());
        }
    }
    
    private String extractJsonValue(String json, String key) {
        try {
            // Try string value first
            String stringPattern = "\"" + key + "\"\\s*:\\s*\"([^\"]*)\"";
            java.util.regex.Pattern p = java.util.regex.Pattern.compile(stringPattern);
            java.util.regex.Matcher m = p.matcher(json);
            if (m.find()) {
                return m.group(1);
            }
            
            // Try numeric value (without quotes)
            String numericPattern = "\"" + key + "\"\\s*:\\s*([0-9]+)";
            p = java.util.regex.Pattern.compile(numericPattern);
            m = p.matcher(json);
            if (m.find()) {
                return m.group(1);
            }
        } catch (Exception e) {
            System.out.println("[BillingController] Error extracting JSON value for key '" + key + "': " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
    
    private String escapeJson(String str) {
        if (str == null) return "";
        return str.replace("\\", "\\\\")
                  .replace("\"", "\\\"")
                  .replace("\n", "\\n")
                  .replace("\r", "\\r")
                  .replace("\t", "\\t");
    }
    
    /**
     * Parse items array from JSON data
     */
    private List<CartItem> parseItemsFromJson(String jsonData) {
        List<CartItem> items = new ArrayList<>();
        try {
            // Extract the items array from the JSON
            String itemsPattern = "\"items\"\\s*:\\s*\\[(.*?)\\]";
            java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(itemsPattern, java.util.regex.Pattern.DOTALL);
            java.util.regex.Matcher matcher = pattern.matcher(jsonData);
            
            if (matcher.find()) {
                String itemsArrayContent = matcher.group(1);
                System.out.println("[BillingController] Items array content: " + itemsArrayContent);
                
                // Split by object boundaries (}",{" pattern)
                String[] itemStrings = itemsArrayContent.split("\\}\\s*,\\s*\\{");
                
                for (String itemStr : itemStrings) {
                    // Clean up the item string
                    itemStr = itemStr.trim();
                    if (!itemStr.startsWith("{")) itemStr = "{" + itemStr;
                    if (!itemStr.endsWith("}")) itemStr = itemStr + "}";
                    
                    System.out.println("[BillingController] Parsing item: " + itemStr);
                    
                    // Extract individual fields
                    String bookIdStr = extractJsonNumberValue(itemStr, "bookId");
                    String quantityStr = extractJsonNumberValue(itemStr, "quantity");
                    String priceStr = extractJsonNumberValue(itemStr, "price");
                    
                    if (bookIdStr != null && quantityStr != null && priceStr != null) {
                        CartItem item = new CartItem();
                        item.bookId = Integer.parseInt(bookIdStr);
                        item.quantity = Integer.parseInt(quantityStr);
                        item.price = Double.parseDouble(priceStr);
                        items.add(item);
                        System.out.println("[BillingController] Added item - BookId: " + item.bookId + 
                                         ", Qty: " + item.quantity + ", Price: " + item.price);
                    }
                }
            } else {
                System.out.println("[BillingController] No items array found in JSON");
            }
        } catch (Exception e) {
            System.out.println("[BillingController] Error parsing items from JSON: " + e.getMessage());
            e.printStackTrace();
        }
        return items;
    }
    
    /**
     * Extract numeric value from JSON string
     */
    private String extractJsonNumberValue(String json, String key) {
        try {
            String pattern = "\"" + key + "\"\\s*:\\s*([0-9.]+)";
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
    
    /**
     * Save bill with items (custom method to avoid duplicate items issue)
     */
    private boolean saveBillWithItems(Bill bill, List<CartItem> cartItems, BillDAO billDAO) {
        try {
            // Create a temporary bill with empty items list to avoid the automatic item saving
            Bill tempBill = new Bill();
            tempBill.setBillNumber(bill.getBillNumber());
            tempBill.setCustomer(bill.getCustomer());
            tempBill.setCashier(bill.getCashier());
            tempBill.setPaymentMethod(bill.getPaymentMethod());
            tempBill.setDelivery(bill.isDelivery());
            tempBill.setDeliveryAddress(bill.getDeliveryAddress());
            tempBill.setStatus(bill.getStatus());
            tempBill.setSubtotal(bill.getSubtotal());
            tempBill.setDiscount(bill.getDiscount());
            tempBill.setTax(bill.getTax());
            tempBill.setTotal(bill.getTotal());
            tempBill.setDeliveryCharge(bill.getDeliveryCharge());
            tempBill.setItems(new ArrayList<>()); // Empty list to prevent automatic saving
            
            // Save the bill without items (use existing schema)
            String query = "INSERT INTO bills (bill_number, bill_date, customer_id, cashier_id, subtotal, discount, tax, total, payment_method, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            
            java.sql.Connection conn = util.DBConnection.getInstance().getConnection();
            java.sql.PreparedStatement stmt = conn.prepareStatement(query, java.sql.Statement.RETURN_GENERATED_KEYS);
            
            stmt.setString(1, bill.getBillNumber());
            stmt.setTimestamp(2, new java.sql.Timestamp(System.currentTimeMillis()));
            stmt.setInt(3, bill.getCustomer().getId());
            stmt.setInt(4, bill.getCashier().getId());
            stmt.setDouble(5, bill.getSubtotal());
            stmt.setDouble(6, bill.getDiscount());
            stmt.setDouble(7, bill.getTax());
            stmt.setDouble(8, bill.getTotal());
            stmt.setString(9, bill.getPaymentMethod());
            stmt.setString(10, bill.getStatus());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                java.sql.ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    int billId = rs.getInt(1);
                    bill.setId(billId);
                    
                    // Now save the bill items
                    String itemQuery = "INSERT INTO bill_items (bill_id, book_id, quantity, unit_price, total) VALUES (?, ?, ?, ?, ?)";
                    java.sql.PreparedStatement itemStmt = conn.prepareStatement(itemQuery);
                    
                    for (CartItem cartItem : cartItems) {
                        itemStmt.setInt(1, billId);
                        itemStmt.setInt(2, cartItem.bookId);
                        itemStmt.setInt(3, cartItem.quantity);
                        itemStmt.setDouble(4, cartItem.price);
                        itemStmt.setDouble(5, cartItem.price * cartItem.quantity);
                        itemStmt.addBatch();
                    }
                    
                    int[] itemResults = itemStmt.executeBatch();
                    itemStmt.close();
                    
                    // Check if all items were saved
                    for (int result : itemResults) {
                        if (result <= 0) {
                            rs.close();
                            stmt.close();
                            conn.close();
                            return false;
                        }
                    }
                    
                    rs.close();
                    stmt.close();
                    conn.close();
                    return true;
                }
                stmt.close();
            }
            conn.close();
        } catch (Exception e) {
            System.out.println("[BillingController] Error saving bill with items: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    
    /**
     * Apply Decorator Pattern to enhance books with premium features
     */
    private List<CartItem> applyBookDecorators(List<CartItem> cartItems) {
        List<CartItem> enhancedItems = new ArrayList<>();
        
        try {
            BookDAO bookDAO = new BookDAO();
            
            for (CartItem item : cartItems) {
                Book book = bookDAO.getBookById(item.bookId);
                if (book != null) {
                    // Apply premium decoration for high-value books
                    if (book.getPrice() > 50.0) {
                        PremiumBookDecorator premiumBook = new PremiumBookDecorator(book, "Hardcover Edition");
                        premiumBook.applyDecoration();
                        
                        // Update item price with premium pricing
                        CartItem enhancedItem = new CartItem();
                        enhancedItem.bookId = item.bookId;
                        enhancedItem.quantity = item.quantity;
                        enhancedItem.price = premiumBook.getDecoratedPrice();
                        enhancedItems.add(enhancedItem);
                        
                        System.out.println("Applied premium decoration to book: " + book.getTitle());
                    } else {
                        // Apply discount decoration for regular books
                        DiscountBookDecorator discountBook = new DiscountBookDecorator(book, 5.0);
                        discountBook.applyDecoration();
                        
                        CartItem enhancedItem = new CartItem();
                        enhancedItem.bookId = item.bookId;
                        enhancedItem.quantity = item.quantity;
                        enhancedItem.price = discountBook.getDecoratedPrice();
                        enhancedItems.add(enhancedItem);
                        
                        System.out.println("Applied discount decoration to book: " + book.getTitle());
                    }
                } else {
                    enhancedItems.add(item); // Keep original if book not found
                }
            }
        } catch (Exception e) {
            System.err.println("Error applying decorators: " + e.getMessage());
            return cartItems; // Return original items if decoration fails
        }
        
        return enhancedItems;
    }
    
    /**
     * Generate sales report using Template Pattern
     */
    public void handleGenerateReport(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            String reportType = request.getParameter("reportType");
            if (reportType == null) reportType = "SALES";
            
            // Use Template Pattern for report generation
            SalesReportTemplate reportTemplate = new SalesReportTemplate();
            String report = reportTemplate.generateReport(reportType);
            
            response.setContentType("text/plain");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(report);
            
            System.out.println("Generated report using Template Pattern: " + reportType);
            
        } catch (Exception e) {
            sendJsonError(response, "Error generating report: " + e.getMessage());
        }
    }
    
    /**
     * Handle order state management
     */
    public void handleOrderState(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            String billIdStr = request.getParameter("billId");
            String action = request.getParameter("action");
            
            if (billIdStr == null || action == null) {
                sendJsonError(response, "Missing billId or action parameter");
                return;
            }
            
            int billId = Integer.parseInt(billIdStr);
            BillDAO billDAO = new BillDAO();
            Bill bill = billDAO.getBillById(billId);
            
            if (bill == null) {
                sendJsonError(response, "Bill not found");
                return;
            }
            
            // Use State Pattern for order management
            OrderContext orderContext = new OrderContext(bill);
            
            switch (action.toLowerCase()) {
                case "process":
                    orderContext.processOrder();
                    break;
                case "cancel":
                    orderContext.cancelOrder();
                    break;
                case "complete":
                    orderContext.completeOrder();
                    break;
                default:
                    sendJsonError(response, "Invalid action: " + action);
                    return;
            }
            
            // Notify observers
            orderManager.updateOrderStatus(billId, orderContext.getCurrentStateName(), 
                                         "Order state changed to: " + orderContext.getCurrentStateName());
            
            String json = "{\"success\":true,\"newState\":\"" + orderContext.getCurrentStateName() + "\"}";
            sendJsonResponse(response, json);
            
        } catch (Exception e) {
            sendJsonError(response, "Error managing order state: " + e.getMessage());
        }
    }
    
    /**
     * Handle command history operations
     */
    public void handleCommandHistory(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            String action = request.getParameter("action");
            
            if ("undo".equals(action)) {
                boolean success = orderInvoker.undoLastCommand();
                String json = "{\"success\":" + success + ",\"message\":\"" + 
                             (success ? "Command undone successfully" : "No commands to undo") + "\"}";
                sendJsonResponse(response, json);
            } else if ("history".equals(action)) {
                List<OrderCommand> history = orderInvoker.getCommandHistory();
                StringBuilder json = new StringBuilder("{\"commands\":[");
                
                for (int i = 0; i < history.size(); i++) {
                    if (i > 0) json.append(",");
                    OrderCommand cmd = history.get(i);
                    json.append("{\"type\":\"" + cmd.getCommandType() + "\",\"description\":\"" + 
                               escapeJson(cmd.getDescription()) + "\"}");
                }
                
                json.append("]}");
                sendJsonResponse(response, json.toString());
            } else {
                sendJsonError(response, "Invalid action: " + action);
            }
            
        } catch (Exception e) {
            sendJsonError(response, "Error handling command history: " + e.getMessage());
        }
    }
    
    /**
     * Demonstrate all design patterns in action
     */
    public void handlePatternDemo(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            StringBuilder demo = new StringBuilder();
            demo.append("=== DESIGN PATTERNS DEMONSTRATION ===\n\n");
            
            // 1. Singleton Pattern (already in use)
            demo.append("1. SINGLETON PATTERN:\n");
            demo.append("   - OrderManager instance: ").append(OrderManager.getInstance().hashCode()).append("\n");
            demo.append("   - Same instance check: ").append(OrderManager.getInstance() == OrderManager.getInstance()).append("\n\n");
            
            // 2. Command Pattern
            demo.append("2. COMMAND PATTERN:\n");
            demo.append("   - Commands in history: ").append(orderInvoker.getHistorySize()).append("\n");
            demo.append("   - Pending commands: ").append(orderInvoker.getPendingCount()).append("\n\n");
            
            // 3. Observer Pattern
            demo.append("3. OBSERVER PATTERN:\n");
            demo.append("   - Registered observers: ").append(orderManager.getObservers().size()).append("\n");
            for (OrderObserver observer : orderManager.getObservers()) {
                demo.append("   - Observer: ").append(observer.getObserverId()).append("\n");
            }
            demo.append("\n");
            
            // 4. State Pattern Demo
            demo.append("4. STATE PATTERN:\n");
            Bill dummyBill = new Bill();
            dummyBill.setBillNumber("DEMO-" + System.currentTimeMillis());
            OrderContext demoContext = new OrderContext(dummyBill);
            demo.append("   - Initial state: ").append(demoContext.getCurrentStateName()).append("\n");
            demoContext.processOrder();
            demo.append("   - After processing: ").append(demoContext.getCurrentStateName()).append("\n\n");
            
            // 5. Decorator Pattern Demo
            demo.append("5. DECORATOR PATTERN:\n");
            Book demoBook = new Book();
            demoBook.setTitle("Demo Book");
            demoBook.setPrice(25.99);
            PremiumBookDecorator premiumDemo = new PremiumBookDecorator(demoBook, "Demo Premium");
            demo.append("   - Original price: $").append(demoBook.getPrice()).append("\n");
            demo.append("   - Decorated price: $").append(premiumDemo.getDecoratedPrice()).append("\n\n");
            
            // 6. Template Pattern Demo
            demo.append("6. TEMPLATE PATTERN:\n");
            SalesReportTemplate templateDemo = new SalesReportTemplate();
            demo.append("   - Report type: ").append(templateDemo.getReportType()).append("\n");
            demo.append("   - Supported types: ").append(String.join(", ", templateDemo.getSupportedReportTypes())).append("\n\n");
            
            // 7. Visitor Pattern Demo
            demo.append("7. VISITOR PATTERN:\n");
            SalesReportVisitor visitorDemo = new SalesReportVisitor();
            visitorDemo.visit(demoBook);
            demo.append("   - Visitor applied to demo book\n\n");
            
            demo.append("=== ALL PATTERNS SUCCESSFULLY DEMONSTRATED ===\n");
            
            response.setContentType("text/plain");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(demo.toString());
            
        } catch (Exception e) {
            sendJsonError(response, "Error in pattern demonstration: " + e.getMessage());
        }
    }
    
    /**
     * Inner class to represent cart items
     */
    private static class CartItem {
        public int bookId;
        public int quantity;
        public double price;
    }
}
