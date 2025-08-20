package state;

/**
 * State Pattern: Pending State
 * Represents the initial state of an order
 */
public class PendingState implements OrderState {
    
    @Override
    public void processOrder(OrderContext context) {
        System.out.println("Processing order in PENDING state: " + context.getOrder().getBillNumber());
        context.setState(new ProcessingState());
    }
    
    @Override
    public void cancelOrder(OrderContext context) {
        System.out.println("Cancelling order in PENDING state: " + context.getOrder().getBillNumber());
        context.setState(new CancelledState());
    }
    
    @Override
    public void completeOrder(OrderContext context) {
        System.out.println("Cannot complete order in PENDING state: " + context.getOrder().getBillNumber());
    }
    
    @Override
    public String getStateName() {
        return "PENDING";
    }
    
    @Override
    public String getStateDescription() {
        return "Order is pending and waiting to be processed";
    }
    
    @Override
    public boolean canProcess() {
        return true;
    }
    
    @Override
    public boolean canCancel() {
        return true;
    }
    
    @Override
    public boolean canComplete() {
        return false;
    }
} 