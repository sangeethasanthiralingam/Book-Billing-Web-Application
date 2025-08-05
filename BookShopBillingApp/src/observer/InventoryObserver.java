package observer;

import dao.BookDAO;
import model.Book;

/**
 * Observer Pattern: Inventory Observer
 * Handles inventory updates when orders are processed
 */
public class InventoryObserver implements OrderObserver {
    private final BookDAO bookDAO;
    
    public InventoryObserver() {
        this.bookDAO = new BookDAO();
    }
    
    @Override
    public void update(int orderId, String status, String message) {
        System.out.println("Inventory Observer - Order " + orderId + " status: " + status);
        
        // Update inventory based on order status
        if ("CONFIRMED".equals(status)) {
            updateInventoryForOrder(orderId);
        } else if ("CANCELLED".equals(status)) {
            restoreInventoryForOrder(orderId);
        }
    }
    
    @Override
    public String getObserverId() {
        return "InventoryObserver";
    }
    
    private void updateInventoryForOrder(int orderId) {
        try {
            // In a real implementation, this would update inventory based on order items
            System.out.println("Updating inventory for confirmed order: " + orderId);
            
            // Example: Update book quantities
            // This is a simplified example - in reality, you'd get order items from database
            updateBookQuantity(1, 1); // Reduce quantity of book ID 1 by 1
            
        } catch (Exception e) {
            System.err.println("Error updating inventory for order " + orderId + ": " + e.getMessage());
        }
    }
    
    private void restoreInventoryForOrder(int orderId) {
        try {
            System.out.println("Restoring inventory for cancelled order: " + orderId);
            
            // Example: Restore book quantities
            updateBookQuantity(1, -1); // Increase quantity of book ID 1 by 1
            
        } catch (Exception e) {
            System.err.println("Error restoring inventory for order " + orderId + ": " + e.getMessage());
        }
    }
    
    private void updateBookQuantity(int bookId, int quantityChange) {
        try {
            Book book = bookDAO.getBookById(bookId);
            if (book != null) {
                int newQuantity = book.getQuantity() + quantityChange;
                if (newQuantity >= 0) {
                    // Update book quantity in database
                    System.out.println("Updated book " + bookId + " quantity: " + book.getQuantity() + " -> " + newQuantity);
                } else {
                    System.err.println("Cannot reduce book " + bookId + " quantity below 0");
                }
            }
        } catch (Exception e) {
            System.err.println("Error updating book quantity: " + e.getMessage());
        }
    }
} 