package service;

import model.Bill;
import model.BillItem;
import model.User;
import dao.UserDAO;

public class BillCalculationService {
    
    private static ConfigurationService configService = ConfigurationService.getInstance();
    
    public static Bill calculateBill(User customer, java.util.List<BillItem> items, 
                                   String paymentMethod, boolean isDelivery, String deliveryAddress) {
        Bill bill = new Bill();
        bill.setCustomer(customer);
        bill.setPaymentMethod(paymentMethod);
        bill.setDelivery(isDelivery);
        bill.setDeliveryAddress(deliveryAddress);
        
        // Calculate units consumed from items
        int totalUnits = 0;
        double subtotal = 0.0;
        
        for (BillItem item : items) {
            totalUnits += item.getQuantity();
            subtotal += item.getTotal();
        }
        
        // Add units consumed to customer's total
        bill.setUnitsConsumed(totalUnits);
        
        // Calculate bill components
        double discount = calculateDiscount(subtotal, customer);
        double tax = (subtotal - discount) * configService.getTaxRate();
        double deliveryCharge = isDelivery ? configService.getDeliveryCharge() : 0.0;
        double total = subtotal - discount + tax + deliveryCharge;
        
        // Set bill values
        bill.setSubtotal(subtotal);
        bill.setDiscount(discount);
        bill.setTax(tax);
        bill.setTotal(total);
        bill.setItems(items);
        
        // Update customer's units consumed in database
        updateCustomerUnits(customer.getId(), totalUnits);
        
        return bill;
    }
    
    private static double calculateDiscount(double subtotal, User customer) {
        // Simple discount calculation - can be enhanced based on customer loyalty, etc.
        if (subtotal > 100) {
            return subtotal * 0.05; // 5% discount for orders over $100
        }
        return 0.0;
    }
    
    private static void updateCustomerUnits(int customerId, int units) {
        try {
            dao.UserDAO userDAO = new dao.UserDAO();
            userDAO.updateUnitsConsumed(customerId, units);
        } catch (Exception e) {
            e.printStackTrace();
            // Log error but don't fail the bill calculation
        }
    }
    
    public static String generateBillNumber() {
        // Generate a unique bill number
        long timestamp = System.currentTimeMillis();
        return "BILL-" + timestamp;
    }
    
    public static double calculateUnitCost(int units) {
        return units * configService.getUnitRate();
    }
    
    public static String getBillSummary(Bill bill) {
        StringBuilder summary = new StringBuilder();
        summary.append("Bill Summary:\n");
        summary.append("Customer: ").append(bill.getCustomer().getFullName()).append("\n");
        summary.append("Account Number: ").append(bill.getCustomer().getAccountNumber() != null ? bill.getCustomer().getAccountNumber() : "N/A").append("\n");
        summary.append("Units Consumed: ").append(bill.getUnitsConsumed()).append("\n");
        summary.append("Subtotal: $").append(String.format("%.2f", bill.getSubtotal())).append("\n");
        summary.append("Discount: $").append(String.format("%.2f", bill.getDiscount())).append("\n");
        summary.append("Tax: $").append(String.format("%.2f", bill.getTax())).append("\n");
        if (bill.isDelivery()) {
            summary.append("Delivery Charge: $").append(String.format("%.2f", bill.getDeliveryCharge())).append("\n");
        }
        summary.append("Total: $").append(String.format("%.2f", bill.getTotal())).append("\n");
        summary.append("Payment Method: ").append(bill.getPaymentMethod()).append("\n");
        
        return summary.toString();
    }
} 