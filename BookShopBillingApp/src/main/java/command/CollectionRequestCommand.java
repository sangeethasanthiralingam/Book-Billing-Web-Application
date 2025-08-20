package command;

import model.Bill;
import observer.OrderManager;
import state.OrderContext;
import java.util.Date;

/**
 * Command Pattern: Collection Request Command
 * Handles collection request creation and admin notification
 */
public class CollectionRequestCommand implements OrderCommand {
    private final Bill collectionRequest;
    private final String customerNote;
    private boolean executed = false;
    private int requestId = -1;
    
    public CollectionRequestCommand(Bill collectionRequest, String customerNote) {
        this.collectionRequest = collectionRequest;
        this.customerNote = customerNote;
    }
    
    @Override
    public boolean execute() {
        try {
            System.out.println("Executing CollectionRequestCommand for customer: " + 
                              collectionRequest.getCustomer().getFullName());
            
            // Set request date
            collectionRequest.setBillDate(new Date());
            collectionRequest.setStatus("COLLECTION_REQUEST");
            
            // Create order context with collection request state
            OrderContext orderContext = new OrderContext(collectionRequest);
            
            // Register observers for notifications
            OrderManager orderManager = OrderManager.getInstance();
            
            // Register customer notification observer
            if (collectionRequest.getCustomer() != null) {
                observer.CustomerNotificationObserver customerObserver = 
                    new observer.CustomerNotificationObserver(
                        collectionRequest.getCustomer().getEmail(),
                        collectionRequest.getCustomer().getFullName()
                    );
                orderManager.registerObserver(customerObserver);
            }
            
            // Register inventory observer
            observer.InventoryObserver inventoryObserver = new observer.InventoryObserver();
            orderManager.registerObserver(inventoryObserver);
            
            // Notify observers about collection request
            orderManager.updateOrderStatus(
                collectionRequest.getId(), 
                "COLLECTION_REQUEST", 
                "Collection request submitted: " + customerNote
            );
            
            executed = true;
            requestId = collectionRequest.getId();
            
            System.out.println("Collection request created successfully with ID: " + requestId);
            return true;
            
        } catch (Exception e) {
            System.err.println("Error executing CollectionRequestCommand: " + e.getMessage());
            return false;
        }
    }
    
    @Override
    public boolean undo() {
        if (!executed || requestId == -1) {
            System.err.println("Cannot undo: Command not executed or no request ID available");
            return false;
        }
        
        try {
            System.out.println("Undoing CollectionRequestCommand for request ID: " + requestId);
            
            // Notify observers about cancellation
            OrderManager.getInstance().updateOrderStatus(
                requestId, "CANCELLED", "Collection request was cancelled by customer"
            );
            
            executed = false;
            return true;
            
        } catch (Exception e) {
            System.err.println("Error undoing CollectionRequestCommand: " + e.getMessage());
            return false;
        }
    }
    
    @Override
    public String getDescription() {
        return "Collection request for customer: " + 
               (collectionRequest.getCustomer() != null ? collectionRequest.getCustomer().getFullName() : "Unknown");
    }
    
    @Override
    public String getCommandType() {
        return "COLLECTION_REQUEST";
    }
    
    public Bill getCollectionRequest() {
        return collectionRequest;
    }
    
    public String getCustomerNote() {
        return customerNote;
    }
    
    public int getRequestId() {
        return requestId;
    }
    
    public boolean isExecuted() {
        return executed;
    }
} 