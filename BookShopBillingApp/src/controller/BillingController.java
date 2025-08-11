package controller;

import java.io.IOException;
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
                sendJsonError(response, "Missing required data");
                return;
            }

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

            // Create bill
            Bill bill = new Bill();
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
            BillDAO billDAO = new BillDAO();
            boolean success = billDAO.saveBill(bill);

            if (success) {
                String json = "{\"success\":true,\"billId\":" + bill.getId() + ",\"billNumber\":\"" + 
                             bill.getBillNumber() + "\"}";
                sendJsonResponse(response, json);
            } else {
                sendJsonError(response, "Failed to save bill");
            }

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            sendJsonError(response, e.getMessage());
        }
    }
    
    // Helper methods
    
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
} 