package state;

/**
 * State Pattern: Admin Review State
 * Represents admin reviewing the collection request
 */
public class AdminReviewState implements OrderState {
    
    @Override
    public void processOrder(OrderContext context) {
        System.out.println("Admin approved collection request: " + context.getOrder().getBillNumber());
        context.setState(new ApprovedState());
    }
    
    @Override
    public void cancelOrder(OrderContext context) {
        System.out.println("Admin rejected collection request: " + context.getOrder().getBillNumber());
        context.setState(new RejectedState());
    }
    
    @Override
    public void completeOrder(OrderContext context) {
        System.out.println("Cannot complete while under admin review: " + context.getOrder().getBillNumber());
    }
    
    @Override
    public String getStateName() {
        return "ADMIN_REVIEW";
    }
    
    @Override
    public String getStateDescription() {
        return "Collection request under admin review";
    }
    
    @Override
    public boolean canProcess() {
        return true; // Admin can approve
    }
    
    @Override
    public boolean canCancel() {
        return true; // Admin can reject
    }
    
    @Override
    public boolean canComplete() {
        return false; // Cannot complete during review
    }
} 