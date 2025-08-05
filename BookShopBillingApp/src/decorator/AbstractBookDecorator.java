package decorator;

import model.Book;

/**
 * Decorator Pattern: Abstract Book Decorator
 * Base class for book decorators with common functionality
 */
public abstract class AbstractBookDecorator implements BookDecorator {
    protected final Book book;
    protected boolean decorationApplied = false;
    
    public AbstractBookDecorator(Book book) {
        this.book = book;
    }
    
    @Override
    public Book getBook() {
        return book;
    }
    
    @Override
    public String getDecoratedTitle() {
        if (!decorationApplied) {
            applyDecoration();
        }
        return book.getTitle();
    }
    
    @Override
    public double getDecoratedPrice() {
        if (!decorationApplied) {
            applyDecoration();
        }
        return book.getPrice();
    }
    
    @Override
    public String getDecoratedDescription() {
        if (!decorationApplied) {
            applyDecoration();
        }
        return book.getTitle() + " by " + book.getAuthor() + " (" + book.getCategory() + ")";
    }
    
    @Override
    public abstract void applyDecoration();
    
    @Override
    public abstract String getDecorationType();
    
    /**
     * Check if decoration is applied
     * @return true if decoration is applied, false otherwise
     */
    public boolean isDecorationApplied() {
        return decorationApplied;
    }
    
    /**
     * Remove decoration
     */
    public void removeDecoration() {
        decorationApplied = false;
    }
} 