package service;

import model.Bill;
import model.BillItem;
import model.Customer;
import dao.CustomerDAO;

public class BillCalculationService {
    
    private static ConfigurationService configService = ConfigurationService.getInstance();
    
    public static Bill calculateBill(Customer customer, java.util.List<BillItem> items, 
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
    
    private static double calculateDiscount(double subtotal, Customer customer) {
        // Apply discount based on customer's total units consumed
        int totalUnitsConsumed = customer.getUnitsConsumed();
        
        if (totalUnitsConsumed >= configService.getDiscountLevel3Threshold()) {
            return subtotal * configService.getDiscountLevel3Percent(); // Level 3 discount
        } else if (totalUnitsConsumed >= configService.getDiscountLevel2Threshold()) {
            return subtotal * configService.getDiscountLevel2Percent(); // Level 2 discount
        } else if (totalUnitsConsumed >= configService.getDiscountLevel1Threshold()) {
            return subtotal * configService.getDiscountLevel1Percent(); // Level 1 discount
        }
        
        return 0.0; // No discount for new customers
    }
    
    private static void updateCustomerUnits(int customerId, int newUnits) {
        try {
            CustomerDAO customerDAO = new CustomerDAO();
            customerDAO.updateUnitsConsumed(customerId, newUnits);
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
        summary.append("Customer: ").append(bill.getCustomer().getName()).append("\n");
        summary.append("Account: ").append(bill.getCustomer().getAccountNumber()).append("\n");
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