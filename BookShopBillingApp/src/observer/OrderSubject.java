package observer;

import java.util.List;

/**
 * Observer Pattern: Order Subject Interface
 * Defines the contract for subjects that can be observed for order events
 */
public interface OrderSubject {
    /**
     * Register an observer to receive order notifications
     * @param observer the observer to register
     */
    void registerObserver(OrderObserver observer);
    
    /**
     * Remove an observer from notifications
     * @param observer the observer to remove
     */
    void removeObserver(OrderObserver observer);
    
    /**
     * Notify all registered observers of an order event
     * @param orderId the ID of the order
     * @param status the new status
     * @param message additional information about the status change
     */
    void notifyObservers(int orderId, String status, String message);
    
    /**
     * Get all registered observers
     * @return list of registered observers
     */
    List<OrderObserver> getObservers();
} 