package visitor;

import model.Book;
import decorator.PremiumBookDecorator;

public interface BookVisitor {
    void visit(Book book);
    void visit(PremiumBookDecorator premiumBook);
    // Add more visit methods as needed
} 