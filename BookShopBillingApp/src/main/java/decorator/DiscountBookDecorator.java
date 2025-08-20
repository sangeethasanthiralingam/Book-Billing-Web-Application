package decorator;

import model.Book;

/**
 * Decorator Pattern: Discount Book Decorator
 * Applies discount to book price
 */
public class DiscountBookDecorator extends AbstractBookDecorator {
    private final double discountPercentage;
    private double originalPrice;
    
    public DiscountBookDecorator(Book book, double discountPercentage) {
        super(book);
        this.discountPercentage = discountPercentage;
        this.originalPrice = book.getPrice();
    }
    
    @Override
    public void applyDecoration() {
        if (!decorationApplied) {
            double discountAmount = originalPrice * (discountPercentage / 100.0);
            double discountedPrice = originalPrice - discountAmount;
            book.setPrice(discountedPrice);
            decorationApplied = true;
            
            System.out.println("Applied " + discountPercentage + "% discount to book: " + book.getTitle());
        }
    }
    
    @Override
    public String getDecorationType() {
        return "DISCOUNT_" + discountPercentage + "_PERCENT";
    }
    
    @Override
    public double getDecoratedPrice() {
        if (!decorationApplied) {
            applyDecoration();
        }
        return book.getPrice();
    }
    
    @Override
    public String getDecoratedTitle() {
        if (!decorationApplied) {
            applyDecoration();
        }
        return book.getTitle() + " [DISCOUNTED " + discountPercentage + "%]";
    }
    
    /**
     * Get the original price before discount
     * @return original price
     */
    public double getOriginalPrice() {
        return originalPrice;
    }
    
    /**
     * Get discount percentage
     * @return discount percentage
     */
    public double getDiscountPercentage() {
        return discountPercentage;
    }
    
    /**
     * Get discount amount
     * @return discount amount
     */
    public double getDiscountAmount() {
        return originalPrice - getDecoratedPrice();
    }
} 