package state;

/**
 * State Pattern: Billing State
 * Represents the billing process after approval
 */
public class BillingState implements OrderState {
    
    @Override
    public void processOrder(OrderContext context) {
        System.out.println("Billing completed for collection request: " + context.getOrder().getBillNumber());
        context.setState(new CompletedState());
    }
    
    @Override
    public void cancelOrder(OrderContext context) {
        System.out.println("Cannot cancel during billing: " + context.getOrder().getBillNumber());
    }
    
    @Override
    public void completeOrder(OrderContext context) {
        System.out.println("Completing billing process: " + context.getOrder().getBillNumber());
        context.setState(new CompletedState());
    }
    
    @Override
    public String getStateName() {
        return "BILLING";
    }
    
    @Override
    public String getStateDescription() {
        return "Billing process in progress";
    }
    
    @Override
    public boolean canProcess() {
        return true; // Can complete billing
    }
    
    @Override
    public boolean canCancel() {
        return false; // Cannot cancel during billing
    }
    
    @Override
    public boolean canComplete() {
        return true; // Can complete billing
    }
} 