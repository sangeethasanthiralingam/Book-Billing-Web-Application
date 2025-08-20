package visitor;

import model.Book;
import decorator.PremiumBookDecorator;

public class SalesReportVisitor implements BookVisitor {
    @Override
    public void visit(Book book) {
        System.out.println("Reporting for book: " + book.getTitle());
    }
    @Override
    public void visit(PremiumBookDecorator premiumBook) {
        System.out.println("Reporting for premium book: " + premiumBook.getDecoratedTitle());
    }
} 