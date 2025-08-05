package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;
import java.util.Date;
import dao.BookDAO;
import dao.UserDAO;
import dao.BillDAO;
import model.Book;
import model.User;
import model.Bill;
import model.BillItem;
import builder.BillBuilder;
import service.BillCalculationService;
import org.json.JSONObject;
import org.json.JSONArray;

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
            
            // Get all available books
            List<Book> books = bookDAO.getAllBooks();
            
            // Get all customers for selection
            List<User> customers = userDAO.getUsersByRole("CUSTOMER");
            
            request.setAttribute("books", books);
            request.setAttribute("customers", customers);
            addCommonAttributes(request);
            
        } catch (Exception e) {
            handleException(request, response, e, "loading billing data");
            return;
        }
        request.getRequestDispatcher("/jsp/billing.jsp").forward(request, response);
    }
    
    /**
     * Handle invoice generation and display
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
            UserDAO userDAO = new UserDAO();
            
            Bill bill = billDAO.getBillById(billId);
            
            if (bill == null) {
                request.setAttribute("error", "Bill not found with ID: " + billId);
                request.getRequestDispatcher("/jsp/error.jsp").forward(request, response);
                return;
            }

            // Get bill items with book details
            List<BillItem> billItems = billDAO.getBillItems(billId);
            
            // Get customer and cashier details
            User customer = userDAO.getUserById(bill.getCustomer().getId());
            User cashier = userDAO.getUserById(bill.getCashier().getId());
            
            // Set attributes for invoice display
            request.setAttribute("bill", bill);
            request.setAttribute("billItems", billItems);
            request.setAttribute("customer", customer);
            request.setAttribute("cashier", cashier);
            request.setAttribute("invoiceNumber", bill.getBillNumber());
            request.setAttribute("invoiceDate", bill.getBillDate());
            
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
        try {
            // Read JSON from request body
            java.io.BufferedReader reader = request.getReader();
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            String jsonData = sb.toString();

            if (jsonData.trim().isEmpty()) {
                sendJsonError(response, "No data received");
                return;
            }

            // Parse JSON data
            JSONObject billData = new JSONObject(jsonData);
            
            int customerId = billData.getInt("customerId");
            String paymentMethod = billData.getString("paymentMethod");
            boolean isDelivery = billData.optBoolean("isDelivery", false);
            String deliveryAddress = billData.optString("deliveryAddress", "");
            JSONArray itemsArray = billData.getJSONArray("items");

            // Validate data
            if (itemsArray.length() == 0) {
                sendJsonError(response, "No items in cart");
                return;
            }

            // Get customer and cashier
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

            // Create bill items
            BookDAO bookDAO = new BookDAO();
            List<BillItem> billItems = new java.util.ArrayList<>();
            double subtotal = 0.0;
            int totalUnits = 0;

            for (int i = 0; i < itemsArray.length(); i++) {
                JSONObject itemData = itemsArray.getJSONObject(i);
                int bookId = itemData.getInt("bookId");
                int quantity = itemData.getInt("quantity");
                double price = itemData.getDouble("price");

                // Validate book availability
                Book book = bookDAO.getBookById(bookId);
                if (book == null) {
                    sendJsonError(response, "Book not found: " + bookId);
                    return;
                }

                if (book.getQuantity() < quantity) {
                    sendJsonError(response, "Insufficient stock for book: " + book.getTitle());
                    return;
                }

                // Create bill item
                BillItem billItem = new BillItem();
                billItem.setBook(book);
                billItem.setQuantity(quantity);
                billItem.setUnitPrice(price);
                billItem.setTotal(quantity * price);
                
                billItems.add(billItem);
                subtotal += billItem.getTotal();
                totalUnits += quantity;
            }

            // Calculate bill totals
            double discount = subtotal > 100 ? subtotal * 0.05 : 0; // 5% discount for orders over LKR 100
            double tax = (subtotal - discount) * 0.10; // 10% tax
            double deliveryCharge = isDelivery ? 5.0 : 0.0;
            double total = subtotal - discount + tax + deliveryCharge;

            // Create bill using Builder pattern
            String billNumber = BillCalculationService.generateBillNumber();
            Bill bill = BillBuilder.createNewBill(billNumber, customer, cashier)
                    .withSubtotal(subtotal)
                    .withDiscount(discount)
                    .withTax(tax)
                    .withTotal(total)
                    .withPaymentMethod(paymentMethod)
                    .withStatus("PAID")
                    .build();

            // Set additional properties
            bill.setDelivery(isDelivery);
            bill.setDeliveryAddress(deliveryAddress);
            bill.setDeliveryCharge(deliveryCharge);
            bill.setUnitsConsumed(totalUnits);
            bill.setItems(billItems);

            // Save bill to database
            BillDAO billDAO = new BillDAO();
            boolean success = billDAO.saveBill(bill);

            if (success) {
                // Update book quantities
                for (BillItem item : billItems) {
                    bookDAO.updateQuantity(item.getBook().getId(), item.getQuantity());
                }

                // Update customer units consumed
                customer.setUnitsConsumed(customer.getUnitsConsumed() + totalUnits);
                userDAO.updateUser(customer);

                // Send success response
                JSONObject response_data = new JSONObject();
                response_data.put("success", true);
                response_data.put("billId", bill.getId());
                response_data.put("billNumber", bill.getBillNumber());
                response_data.put("total", total);
                response_data.put("message", "Bill generated successfully");
                
                sendJsonResponse(response, response_data.toString());
            } else {
                sendJsonError(response, "Failed to save bill to database");
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            sendJsonError(response, "Error generating bill: " + e.getMessage());
        }
    }
    
    /**
     * Handle quick billing for walk-in customers
     */
    public void handleQuickBill(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        if (!"POST".equalsIgnoreCase(request.getMethod())) {
            response.sendRedirect(request.getContextPath() + "/controller/billing");
            return;
        }
        
        try {
            // Get parameters
            String customerName = getParameter(request, "customerName", "Walk-in Customer");
            String customerPhone = getParameter(request, "customerPhone", "");
            String paymentMethod = getParameter(request, "paymentMethod", "CASH");
            
            // Get current user (cashier)
            HttpSession session = request.getSession(false);
            Integer cashierId = (session != null) ? (Integer) session.getAttribute("userId") : null;
            if (cashierId == null) {
                request.setAttribute("error", "User session not found");
                request.getRequestDispatcher("/jsp/billing.jsp").forward(request, response);
                return;
            }

            UserDAO userDAO = new UserDAO();
            User cashier = userDAO.getUserById(cashierId);
            
            // Create temporary customer for walk-in
            User walkInCustomer = new User();
            walkInCustomer.setId(0); // Special ID for walk-in customers
            walkInCustomer.setFullName(customerName);
            walkInCustomer.setPhone(customerPhone);
            walkInCustomer.setEmail("walkin@bookshop.com");
            walkInCustomer.setRole("CUSTOMER");

            // Create simple bill
            String billNumber = BillCalculationService.generateBillNumber();
            Bill bill = BillBuilder.createNewBill(billNumber, walkInCustomer, cashier)
                    .withPaymentMethod(paymentMethod)
                    .withStatus("PAID")
                    .build();

            request.setAttribute("success", "Quick bill created: " + billNumber);
            
        } catch (Exception e) {
            handleException(request, response, e, "creating quick bill");
            return;
        }
        
        // Redirect back to billing page
        response.sendRedirect(request.getContextPath() + "/controller/billing");
    }
    
    /**
     * Handle bill search and filtering
     */
    public void handleBillSearch(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        try {
            String searchTerm = getParameter(request, "search", "");
            String status = getParameter(request, "status", "");
            String paymentMethod = getParameter(request, "paymentMethod", "");
            String dateFrom = getParameter(request, "dateFrom", "");
            String dateTo = getParameter(request, "dateTo", "");
            
            BillDAO billDAO = new BillDAO();
            List<Bill> bills = billDAO.getAllBills();
            
            // Apply filters
            bills = filterBills(bills, searchTerm, status, paymentMethod, dateFrom, dateTo);
            
            request.setAttribute("bills", bills);
            request.setAttribute("search", searchTerm);
            request.setAttribute("status", status);
            request.setAttribute("paymentMethod", paymentMethod);
            request.setAttribute("dateFrom", dateFrom);
            request.setAttribute("dateTo", dateTo);
            addCommonAttributes(request);
            
        } catch (Exception e) {
            handleException(request, response, e, "searching bills");
            return;
        }
        
        request.getRequestDispatcher("/jsp/bill-search.jsp").forward(request, response);
    }
    
    /**
     * Handle bill cancellation
     */
    public void handleCancelBill(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String billIdStr = request.getParameter("billId");
        if (billIdStr == null || billIdStr.trim().isEmpty()) {
            sendJsonError(response, "Bill ID is required");
            return;
        }
        
        try {
            int billId = Integer.parseInt(billIdStr);
            BillDAO billDAO = new BillDAO();
            BookDAO bookDAO = new BookDAO();
            
            Bill bill = billDAO.getBillById(billId);
            if (bill == null) {
                sendJsonError(response, "Bill not found");
                return;
            }
            
            if ("CANCELLED".equals(bill.getStatus())) {
                sendJsonError(response, "Bill is already cancelled");
                return;
            }
            
            // Update bill status
            bill.setStatus("CANCELLED");
            boolean updated = billDAO.updateBillStatus(billId, "CANCELLED");
            
            if (updated) {
                // Restore book quantities
                List<BillItem> items = billDAO.getBillItems(billId);
                for (BillItem item : items) {
                    Book book = item.getBook();
                    book.setQuantity(book.getQuantity() + item.getQuantity());
                    bookDAO.updateBook(book);
                }
                
                sendJsonSuccess(response, "Bill cancelled successfully");
            } else {
                sendJsonError(response, "Failed to cancel bill");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            sendJsonError(response, "Error cancelling bill: " + e.getMessage());
        }
    }
    
    // Helper methods
    
    private List<Bill> filterBills(List<Bill> bills, String searchTerm, String status, 
                                  String paymentMethod, String dateFrom, String dateTo) {
        return bills.stream()
                .filter(bill -> {
                    // Search term filter
                    if (searchTerm != null && !searchTerm.trim().isEmpty()) {
                        String term = searchTerm.toLowerCase();
                        return bill.getBillNumber().toLowerCase().contains(term) ||
                               (bill.getCustomer() != null && 
                                bill.getCustomer().getFullName().toLowerCase().contains(term));
                    }
                    return true;
                })
                .filter(bill -> {
                    // Status filter
                    if (status != null && !status.trim().isEmpty()) {
                        return status.equals(bill.getStatus());
                    }
                    return true;
                })
                .filter(bill -> {
                    // Payment method filter
                    if (paymentMethod != null && !paymentMethod.trim().isEmpty()) {
                        return paymentMethod.equals(bill.getPaymentMethod());
                    }
                    return true;
                })
                .collect(java.util.stream.Collectors.toList());
    }
}