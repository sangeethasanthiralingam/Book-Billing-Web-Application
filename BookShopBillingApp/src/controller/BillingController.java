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

/**
 * Controller for billing operations
 */
public class BillingController extends BaseController {
    
    /**
     * Handle billing page
     */
    public void handleBilling(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        try {
            BookDAO bookDAO = new BookDAO();
            UserDAO userDAO = new UserDAO();
            List<Book> books = bookDAO.getAllBooks();
            List<User> customers = userDAO.getUsersByRole("CUSTOMER");
            
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
            
            System.out.println("[BillingController] customerId: " + customerIdStr);
            System.out.println("[BillingController] paymentMethod: " + paymentMethod);

            if (customerIdStr == null || paymentMethod == null) {
                System.out.println("[BillingController] Missing required data - customerId or paymentMethod null");
                sendJsonError(response, "Missing required data: customerId or paymentMethod");
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
     * Inner class to represent cart items
     */
    private static class CartItem {
        public int bookId;
        public int quantity;
        public double price;
    }
}
