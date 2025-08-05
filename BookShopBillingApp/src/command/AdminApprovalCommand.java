package command;

import model.Bill;
import observer.OrderManager;
import state.OrderContext;
import state.ApprovedState;
import state.RejectedState;
import java.util.Date;

/**
 * Command Pattern: Admin Approval Command
 * Handles admin approval or rejection of collection requests
 */
public class AdminApprovalCommand implements OrderCommand {
    private final int requestId;
    private final boolean isApproved;
    private final String adminNote;
    private final String adminName;
    private boolean executed = false;
    
    public AdminApprovalCommand(int requestId, boolean isApproved, String adminNote, String adminName) {
        this.requestId = requestId;
        this.isApproved = isApproved;
        this.adminNote = adminNote;
        this.adminName = adminName;
    }
    
    @Override
    public boolean execute() {
        try {
            System.out.println("Executing AdminApprovalCommand for request ID: " + requestId + 
                              " - Approved: " + isApproved);
            
            // Get the collection request (in real implementation, fetch from database)
            // For now, we'll create a mock bill
            Bill collectionRequest = new Bill();
            collectionRequest.setId(requestId);
            collectionRequest.setBillNumber("REQ-" + requestId);
            
            // Create order context
            OrderContext orderContext = new OrderContext(collectionRequest);
            
            if (isApproved) {
                // Move to approved state
                orderContext.setState(new ApprovedState());
                collectionRequest.setStatus("APPROVED");
                
                System.out.println("Admin " + adminName + " approved collection request " + requestId);
                
                // Notify observers about approval
                OrderManager.getInstance().updateOrderStatus(
                    requestId, "APPROVED", "Approved by " + adminName + ": " + adminNote
                );
                
            } else {
                // Move to rejected state
                orderContext.setState(new RejectedState());
                collectionRequest.setStatus("REJECTED");
                
                System.out.println("Admin " + adminName + " rejected collection request " + requestId);
                
                // Notify observers about rejection
                OrderManager.getInstance().updateOrderStatus(
                    requestId, "REJECTED", "Rejected by " + adminName + ": " + adminNote
                );
            }
            
            executed = true;
            return true;
            
        } catch (Exception e) {
            System.err.println("Error executing AdminApprovalCommand: " + e.getMessage());
            return false;
        }
    }
    
    @Override
    public boolean undo() {
        if (!executed) {
            System.err.println("Cannot undo: Command not executed");
            return false;
        }
        
        try {
            System.out.println("Undoing AdminApprovalCommand for request ID: " + requestId);
            
            // In a real implementation, you would revert the approval/rejection
            // For now, we'll just notify observers
            String undoMessage = isApproved ? "Approval reverted" : "Rejection reverted";
            OrderManager.getInstance().updateOrderStatus(
                requestId, "UNDO", undoMessage + " by " + adminName
            );
            
            executed = false;
            return true;
            
        } catch (Exception e) {
            System.err.println("Error undoing AdminApprovalCommand: " + e.getMessage());
            return false;
        }
    }
    
    @Override
    public String getDescription() {
        return "Admin " + adminName + " " + (isApproved ? "approved" : "rejected") + 
               " collection request " + requestId;
    }
    
    @Override
    public String getCommandType() {
        return "ADMIN_APPROVAL";
    }
    
    public int getRequestId() {
        return requestId;
    }
    
    public boolean isApproved() {
        return isApproved;
    }
    
    public String getAdminNote() {
        return adminNote;
    }
    
    public String getAdminName() {
        return adminName;
    }
    
    public boolean isExecuted() {
        return executed;
    }
} 