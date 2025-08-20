package state;

/**
 * State Pattern: Completed State
 * Represents a completed order
 */
public class CompletedState implements OrderState {
    
    @Override
    public void processOrder(OrderContext context) {
        System.out.println("Cannot process completed order: " + context.getOrder().getBillNumber());
    }
    
    @Override
    public void cancelOrder(OrderContext context) {
        System.out.println("Cannot cancel completed order: " + context.getOrder().getBillNumber());
    }
    
    @Override
    public void completeOrder(OrderContext context) {
        System.out.println("Order is already completed: " + context.getOrder().getBillNumber());
    }
    
    @Override
    public String getStateName() {
        return "COMPLETED";
    }
    
    @Override
    public String getStateDescription() {
        return "Order has been completed successfully";
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