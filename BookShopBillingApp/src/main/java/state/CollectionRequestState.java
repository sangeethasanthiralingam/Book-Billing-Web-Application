package state;

/**
 * State Pattern: Collection Request State
 * Represents a collection request that needs admin approval
 */
public class CollectionRequestState implements OrderState {
    
    @Override
    public void processOrder(OrderContext context) {
        System.out.println("Collection request is being reviewed by admin: " + context.getOrder().getBillNumber());
        context.setState(new AdminReviewState());
    }
    
    @Override
    public void cancelOrder(OrderContext context) {
        System.out.println("Cancelling collection request: " + context.getOrder().getBillNumber());
        context.setState(new CancelledState());
    }
    
    @Override
    public void completeOrder(OrderContext context) {
        System.out.println("Cannot complete collection request without admin approval: " + context.getOrder().getBillNumber());
    }
    
    @Override
    public String getStateName() {
        return "COLLECTION_REQUEST";
    }
    
    @Override
    public String getStateDescription() {
        return "Collection request submitted, waiting for admin approval";
    }
    
    @Override
    public boolean canProcess() {
        return true; // Admin can process/review
    }
    
    @Override
    public boolean canCancel() {
        return true; // Customer can cancel request
    }
    
    @Override
    public boolean canComplete() {
        return false; // Cannot complete without admin approval
    }
} 