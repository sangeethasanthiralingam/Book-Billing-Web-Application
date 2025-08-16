package decorator;

import model.Book;

/**
 * Decorator Pattern: Premium Book Decorator
 * Adds premium features to books
 */
public class PremiumBookDecorator extends AbstractBookDecorator {
    private final String premiumFeature;
    
    public PremiumBookDecorator(Book book, String premiumFeature) {
        super(book);
        this.premiumFeature = premiumFeature;
    }
    
    @Override
    public void applyDecoration() {
        if (!decorationApplied) {
            // Add premium markup to price
            double premiumMarkup = book.getPrice() * 0.15; // 15% premium
            book.setPrice(book.getPrice() + premiumMarkup);
            decorationApplied = true;
            
            System.out.println("Applied premium feature '" + premiumFeature + "' to book: " + book.getTitle());
        }
    }
    
    @Override
    public String getDecorationType() {
        return "PREMIUM_" + premiumFeature.toUpperCase();
    }
    
    @Override
    public String getDecoratedTitle() {
        if (!decorationApplied) {
            applyDecoration();
        }
        return "[PREMIUM] " + book.getTitle() + " [" + premiumFeature + "]";
    }
    
    @Override
    public String getDecoratedDescription() {
        if (!decorationApplied) {
            applyDecoration();
        }
        return "PREMIUM EDITION: " + book.getTitle() + " by " + book.getAuthor() + 
               " - " + premiumFeature + " (" + book.getCategory() + ")";
    }
    
    /**
     * Get premium feature
     * @return the premium feature applied
     */
    public String getPremiumFeature() {
        return premiumFeature;
    }
    
    /**
     * Check if book has premium features
     * @return true if premium features are applied
     */
    public boolean isPremium() {
        return decorationApplied;
    }
} 