package command;

import dao.BillDAO;
import model.Bill;
import java.util.Date;

/**
 * Command Pattern: Create Order Command
 * Handles order creation with undo capability
 */
public class CreateOrderCommand implements OrderCommand {
    private final Bill bill;
    private final BillDAO billDAO;
    private boolean executed = false;
    private int createdBillId = -1;
    
    public CreateOrderCommand(Bill bill) {
        this.bill = bill;
        this.billDAO = new BillDAO();
    }
    
    @Override
    public boolean execute() {
        try {
            System.out.println("Executing CreateOrderCommand for bill: " + bill.getBillNumber());
            
            // Set bill date if not already set
            if (bill.getBillDate() == null) {
                bill.setBillDate(new Date());
            }
            
            // Save bill to database
            boolean success = billDAO.saveBill(bill);
            
            if (success) {
                createdBillId = bill.getId();
                executed = true;
                System.out.println("Order created successfully with ID: " + createdBillId);
                
                // Notify observers about order creation
                observer.OrderManager.getInstance().updateOrderStatus(
                    createdBillId, "CREATED", "Order has been created successfully"
                );
                
                return true;
            } else {
                System.err.println("Failed to create order");
                return false;
            }
            
        } catch (Exception e) {
            System.err.println("Error executing CreateOrderCommand: " + e.getMessage());
            return false;
        }
    }
    
    @Override
    public boolean undo() {
        if (!executed || createdBillId == -1) {
            System.err.println("Cannot undo: Command not executed or no bill ID available");
            return false;
        }
        
        try {
            System.out.println("Undoing CreateOrderCommand for bill ID: " + createdBillId);
            
            // In a real implementation, you would have a delete method in BillDAO
            // For now, we'll just mark it as cancelled
            observer.OrderManager.getInstance().updateOrderStatus(
                createdBillId, "CANCELLED", "Order creation was undone"
            );
            
            executed = false;
            return true;
            
        } catch (Exception e) {
            System.err.println("Error undoing CreateOrderCommand: " + e.getMessage());
            return false;
        }
    }
    
    @Override
    public String getDescription() {
        return "Create new order for customer: " + 
               (bill.getCustomer() != null ? bill.getCustomer().getFullName() : "Unknown");
    }
    
    @Override
    public String getCommandType() {
        return "CREATE_ORDER";
    }
    
    public Bill getBill() {
        return bill;
    }
    
    public int getCreatedBillId() {
        return createdBillId;
    }
    
    public boolean isExecuted() {
        return executed;
    }
} 