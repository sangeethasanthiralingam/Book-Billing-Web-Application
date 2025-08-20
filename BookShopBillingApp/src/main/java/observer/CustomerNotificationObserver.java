package observer;

/**
 * Observer Pattern: Customer Notification Observer
 * Handles customer notifications for order status changes
 */
public class CustomerNotificationObserver implements OrderObserver {
    private final String customerEmail;
    private final String customerName;
    
    public CustomerNotificationObserver(String customerEmail, String customerName) {
        this.customerEmail = customerEmail;
        this.customerName = customerName;
    }
    
    @Override
    public void update(int orderId, String status, String message) {
        System.out.println("Customer Notification - Order " + orderId + " status: " + status);
        
        // Send email notification to customer
        try {
            String subject = "Order " + orderId + " Status Update";
            String htmlContent = generateEmailContent(orderId, status, message);
            
            util.MailUtil.sendMailHtml(customerEmail, subject, htmlContent);
            System.out.println("Customer notification email sent to: " + customerEmail);
            
        } catch (Exception e) {
            System.err.println("Failed to send customer notification: " + e.getMessage());
        }
    }
    
    @Override
    public String getObserverId() {
        return "CustomerNotification-" + customerEmail;
    }
    
    private String generateEmailContent(int orderId, String status, String message) {
        StringBuilder html = new StringBuilder();
        html.append("<h2>Order Status Update</h2>");
        html.append("<p>Dear " + customerName + ",</p>");
        html.append("<p>Your order <strong>#" + orderId + "</strong> status has been updated to: <strong>" + status + "</strong></p>");
        
        if (message != null && !message.trim().isEmpty()) {
            html.append("<p><strong>Additional Information:</strong> " + message + "</p>");
        }
        
        html.append("<p>Thank you for choosing BookShop!</p>");
        html.append("<p>Best regards,<br>BookShop Team</p>");
        
        return html.toString();
    }
    
    public String getCustomerEmail() {
        return customerEmail;
    }
    
    public String getCustomerName() {
        return customerName;
    }
} 