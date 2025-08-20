package state;

/**
 * State Pattern: Rejected State
 * Represents a rejected collection request
 */
public class RejectedState implements OrderState {
    
    @Override
    public void processOrder(OrderContext context) {
        System.out.println("Cannot process rejected request: " + context.getOrder().getBillNumber());
    }
    
    @Override
    public void cancelOrder(OrderContext context) {
        System.out.println("Request is already rejected: " + context.getOrder().getBillNumber());
    }
    
    @Override
    public void completeOrder(OrderContext context) {
        System.out.println("Cannot complete rejected request: " + context.getOrder().getBillNumber());
    }
    
    @Override
    public String getStateName() {
        return "REJECTED";
    }
    
    @Override
    public String getStateDescription() {
        return "Collection request rejected by admin";
    }
    
    @Override
    public boolean canProcess() {
        return false;
    }
    
    @Override
    public boolean canCancel() {
        return false;
    }
    
    @Override
    public boolean canComplete() {
        return false;
    }
} 