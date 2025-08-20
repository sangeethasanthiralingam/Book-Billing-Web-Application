package observer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Observer Pattern: Order Manager Implementation
 * Manages order observers and notifies them of order status changes
 */
public class OrderManager implements OrderSubject {
    private final List<OrderObserver> observers;
    private static OrderManager instance;
    
    /**
     * Private constructor for Singleton pattern
     */
    private OrderManager() {
        // Use thread-safe list for concurrent access
        this.observers = new CopyOnWriteArrayList<>();
    }
    
    /**
     * Get singleton instance
     * @return OrderManager instance
     */
    public static synchronized OrderManager getInstance() {
        if (instance == null) {
            instance = new OrderManager();
        }
        return instance;
    }
    
    @Override
    public void registerObserver(OrderObserver observer) {
        if (observer != null && !observers.contains(observer)) {
            observers.add(observer);
            System.out.println("Observer registered: " + observer.getObserverId());
        }
    }
    
    @Override
    public void removeObserver(OrderObserver observer) {
        if (observer != null) {
            observers.remove(observer);
            System.out.println("Observer removed: " + observer.getObserverId());
        }
    }
    
    @Override
    public void notifyObservers(int orderId, String status, String message) {
        System.out.println("Notifying " + observers.size() + " observers of order " + orderId + " status: " + status);
        
        for (OrderObserver observer : observers) {
            try {
                observer.update(orderId, status, message);
            } catch (Exception e) {
                System.err.println("Error notifying observer " + observer.getObserverId() + ": " + e.getMessage());
            }
        }
    }
    
    @Override
    public List<OrderObserver> getObservers() {
        return new ArrayList<>(observers);
    }
    
    /**
     * Update order status and notify all observers
     * @param orderId the order ID
     * @param status the new status
     * @param message additional message
     */
    public void updateOrderStatus(int orderId, String status, String message) {
        System.out.println("Order " + orderId + " status updated to: " + status);
        notifyObservers(orderId, status, message);
    }
} 