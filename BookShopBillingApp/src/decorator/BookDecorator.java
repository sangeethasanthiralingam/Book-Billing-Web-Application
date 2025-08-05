package decorator;

import model.Book;

/**
 * Decorator Pattern: Book Decorator Interface
 * Defines the contract for book functionality extensions
 */
public interface BookDecorator {
    /**
     * Get the decorated book
     * @return the book being decorated
     */
    Book getBook();
    
    /**
     * Get book title with decoration
     * @return decorated title
     */
    String getDecoratedTitle();
    
    /**
     * Get book price with decoration
     * @return decorated price
     */
    double getDecoratedPrice();
    
    /**
     * Get book description with decoration
     * @return decorated description
     */
    String getDecoratedDescription();
    
    /**
     * Get decoration type
     * @return type of decoration applied
     */
    String getDecorationType();
    
    /**
     * Apply decoration to book
     */
    void applyDecoration();
} 