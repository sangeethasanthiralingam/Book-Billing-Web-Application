package observer;

/**
 * Observer Pattern: Order Observer Interface
 * Defines the contract for observers that need to be notified of order events
 */
public interface OrderObserver {
    /**
     * Update method called when order status changes
     * @param orderId the ID of the order
     * @param status the new status
     * @param message additional information about the status change
     */
    void update(int orderId, String status, String message);
    
    /**
     * Get observer identifier
     * @return unique identifier for this observer
     */
    String getObserverId();
} 