package state;

/**
 * State Pattern: Cancelled State
 * Represents a cancelled order
 */
public class CancelledState implements OrderState {
    
    @Override
    public void processOrder(OrderContext context) {
        System.out.println("Cannot process cancelled order: " + context.getOrder().getBillNumber());
    }
    
    @Override
    public void cancelOrder(OrderContext context) {
        System.out.println("Order is already cancelled: " + context.getOrder().getBillNumber());
    }
    
    @Override
    public void completeOrder(OrderContext context) {
        System.out.println("Cannot complete cancelled order: " + context.getOrder().getBillNumber());
    }
    
    @Override
    public String getStateName() {
        return "CANCELLED";
    }
    
    @Override
    public String getStateDescription() {
        return "Order has been cancelled";
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