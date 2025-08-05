package state;

/**
 * State Pattern: Order State Interface
 * Defines the contract for different order states
 */
public interface OrderState {
    /**
     * Process the order in current state
     * @param context the order context
     */
    void processOrder(OrderContext context);
    
    /**
     * Cancel the order
     * @param context the order context
     */
    void cancelOrder(OrderContext context);
    
    /**
     * Complete the order
     * @param context the order context
     */
    void completeOrder(OrderContext context);
    
    /**
     * Get state name
     * @return the name of the current state
     */
    String getStateName();
    
    /**
     * Get state description
     * @return description of what this state represents
     */
    String getStateDescription();
    
    /**
     * Check if order can be processed in this state
     * @return true if order can be processed, false otherwise
     */
    boolean canProcess();
    
    /**
     * Check if order can be cancelled in this state
     * @return true if order can be cancelled, false otherwise
     */
    boolean canCancel();
    
    /**
     * Check if order can be completed in this state
     * @return true if order can be completed, false otherwise
     */
    boolean canComplete();
} 