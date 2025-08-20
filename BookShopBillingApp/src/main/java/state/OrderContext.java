package state;

import model.Bill;
import java.util.Date;

/**
 * State Pattern: Order Context
 * Maintains reference to current state and manages state transitions
 */
public class OrderContext {
    private OrderState currentState;
    private Bill order;
    private Date lastStateChange;
    
    public OrderContext(Bill order) {
        this.order = order;
        this.currentState = new CollectionRequestState();
        this.lastStateChange = new Date();
    }
    
    /**
     * Set the current state
     * @param state the new state
     */
    public void setState(OrderState state) {
        if (state != null) {
            System.out.println("Order " + order.getBillNumber() + " state changed from " + 
                              currentState.getStateName() + " to " + state.getStateName());
            this.currentState = state;
            this.lastStateChange = new Date();
        }
    }
    
    /**
     * Get current state
     * @return current state
     */
    public OrderState getCurrentState() {
        return currentState;
    }
    
    /**
     * Process the order
     */
    public void processOrder() {
        currentState.processOrder(this);
    }
    
    /**
     * Cancel the order
     */
    public void cancelOrder() {
        currentState.cancelOrder(this);
    }
    
    /**
     * Complete the order
     */
    public void completeOrder() {
        currentState.completeOrder(this);
    }
    
    /**
     * Get the order
     * @return the order
     */
    public Bill getOrder() {
        return order;
    }
    
    /**
     * Get last state change time
     * @return last state change time
     */
    public Date getLastStateChange() {
        return lastStateChange;
    }
    
    /**
     * Get current state name
     * @return current state name
     */
    public String getCurrentStateName() {
        return currentState.getStateName();
    }
    
    /**
     * Check if order can be processed
     * @return true if order can be processed, false otherwise
     */
    public boolean canProcess() {
        return currentState.canProcess();
    }
    
    /**
     * Check if order can be cancelled
     * @return true if order can be cancelled, false otherwise
     */
    public boolean canCancel() {
        return currentState.canCancel();
    }
    
    /**
     * Check if order can be completed
     * @return true if order can be completed, false otherwise
     */
    public boolean canComplete() {
        return currentState.canComplete();
    }
} 