package state;

/**
 * State Pattern: Approved State
 * Represents an approved collection request ready for billing
 */
public class ApprovedState implements OrderState {
    
    @Override
    public void processOrder(OrderContext context) {
        System.out.println("Processing approved collection request for billing: " + context.getOrder().getBillNumber());
        context.setState(new BillingState());
    }
    
    @Override
    public void cancelOrder(OrderContext context) {
        System.out.println("Cannot cancel approved request: " + context.getOrder().getBillNumber());
    }
    
    @Override
    public void completeOrder(OrderContext context) {
        System.out.println("Cannot complete without billing: " + context.getOrder().getBillNumber());
    }
    
    @Override
    public String getStateName() {
        return "APPROVED";
    }
    
    @Override
    public String getStateDescription() {
        return "Collection request approved, ready for billing";
    }
    
    @Override
    public boolean canProcess() {
        return true; // Can proceed to billing
    }
    
    @Override
    public boolean canCancel() {
        return false; // Cannot cancel after approval
    }
    
    @Override
    public boolean canComplete() {
        return false; // Must go through billing first
    }
} 