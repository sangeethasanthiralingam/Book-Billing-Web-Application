package state;

/**
 * State Pattern: Processing State
 * Represents an order that is being processed
 */
public class ProcessingState implements OrderState {
    
    @Override
    public void processOrder(OrderContext context) {
        System.out.println("Order is already being processed: " + context.getOrder().getBillNumber());
    }
    
    @Override
    public void cancelOrder(OrderContext context) {
        System.out.println("Cancelling order in PROCESSING state: " + context.getOrder().getBillNumber());
        context.setState(new CancelledState());
    }
    
    @Override
    public void completeOrder(OrderContext context) {
        System.out.println("Completing order in PROCESSING state: " + context.getOrder().getBillNumber());
        context.setState(new CompletedState());
    }
    
    @Override
    public String getStateName() {
        return "PROCESSING";
    }
    
    @Override
    public String getStateDescription() {
        return "Order is currently being processed";
    }
    
    @Override
    public boolean canProcess() {
        return false;
    }
    
    @Override
    public boolean canCancel() {
        return true;
    }
    
    @Override
    public boolean canComplete() {
        return true;
    }
} 