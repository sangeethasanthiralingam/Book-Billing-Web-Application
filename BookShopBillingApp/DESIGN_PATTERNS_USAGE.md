# 🎯 Design Patterns Usage Guide

## Overview
This document demonstrates how to use all the implemented design patterns in the BookShop Billing Application.

## 🔧 1. Command Pattern Usage

### Creating and Executing Commands
```java
// Create order command
OrderCommand createCommand = new CreateOrderCommand(bill, cartItems);

// Execute command
OrderInvoker invoker = new OrderInvoker();
boolean success = invoker.executeCommand(createCommand);

// Undo command if needed
if (!success) {
    invoker.undoLastCommand();
}
```

### Available Commands
- **CreateOrderCommand**: Creates new orders
- **AdminApprovalCommand**: Handles admin approval for high-value orders
- **CollectionRequestCommand**: Manages collection/delivery requests

### Web Endpoints
- `GET /controller/command-history?action=history` - View command history
- `GET /controller/command-history?action=undo` - Undo last command

## 👁️ 2. Observer Pattern Usage

### Registering Observers
```java
OrderManager orderManager = OrderManager.getInstance();

// Register inventory observer
orderManager.registerObserver(new InventoryObserver());

// Register customer notification observer
orderManager.registerObserver(new CustomerNotificationObserver("customer@email.com", "John Doe"));
```

### Notifying Observers
```java
// Update order status - all observers will be notified
orderManager.updateOrderStatus(orderId, "COMPLETED", "Order successfully processed");
```

### Available Observers
- **InventoryObserver**: Updates inventory levels
- **CustomerNotificationObserver**: Sends email notifications to customers

## 🔄 3. State Pattern Usage

### Managing Order States
```java
// Create order context
OrderContext orderContext = new OrderContext(bill);

// Process order through states
orderContext.processOrder();  // PENDING → PROCESSING
orderContext.completeOrder(); // PROCESSING → COMPLETED
orderContext.cancelOrder();   // Any state → CANCELLED
```

### Available States
- **CollectionRequestState**: Initial state
- **PendingState**: Order pending
- **ProcessingState**: Order being processed
- **CompletedState**: Order completed
- **CancelledState**: Order cancelled
- **AdminReviewState**: Requires admin approval
- **ApprovedState**: Admin approved
- **RejectedState**: Admin rejected

### Web Endpoints
- `GET /controller/order-state?billId=1&action=process` - Process order
- `GET /controller/order-state?billId=1&action=complete` - Complete order
- `GET /controller/order-state?billId=1&action=cancel` - Cancel order

## 🎨 4. Decorator Pattern Usage

### Enhancing Books
```java
// Create premium book decorator
Book book = new Book();
book.setTitle("Advanced Java");
book.setPrice(45.99);

PremiumBookDecorator premiumBook = new PremiumBookDecorator(book, "Hardcover Edition");
premiumBook.applyDecoration();

// Get enhanced properties
String decoratedTitle = premiumBook.getDecoratedTitle();
double decoratedPrice = premiumBook.getDecoratedPrice();
```

### Available Decorators
- **PremiumBookDecorator**: Adds premium features (15% price markup)
- **DiscountBookDecorator**: Applies discounts to books

### Automatic Application
The decorator pattern is automatically applied during billing:
- Books > $50: Premium decoration
- Books ≤ $50: Discount decoration (5% off)

## 📊 5. Template Pattern Usage

### Generating Reports
```java
// Create report template
SalesReportTemplate reportTemplate = new SalesReportTemplate();

// Generate report
String report = reportTemplate.generateReport("SALES");
```

### Available Report Types
- **SALES**: General sales report
- **DAILY_SALES**: Daily sales summary
- **MONTHLY_SALES**: Monthly sales summary
- **REVENUE**: Revenue analysis

### Web Endpoints
- `GET /controller/generate-report?reportType=SALES` - Generate sales report
- `GET /controller/pattern-reports?reportType=DAILY_SALES` - Generate daily report

## 🚶 6. Visitor Pattern Usage

### Analyzing Books
```java
// Create visitor
SalesReportVisitor visitor = new SalesReportVisitor();

// Visit books
visitor.visit(regularBook);
visitor.visit(premiumBookDecorator);

// Get analysis report
String salesReport = visitor.getSalesReport();
double totalRevenue = visitor.getTotalRevenue();
```

### Automatic Application
The visitor pattern is automatically applied during billing to:
- Collect sales analytics
- Calculate revenue statistics
- Generate post-processing reports

## 🎯 7. Complete Integration Example

### Enhanced Billing Process
```java
public void processEnhancedBill(Bill bill, List<CartItem> items) {
    // 1. Apply Decorator Pattern
    List<CartItem> enhancedItems = applyBookDecorators(items);
    
    // 2. Use Command Pattern
    OrderCommand createCommand = new CreateOrderCommand(bill, enhancedItems);
    boolean commandSuccess = orderInvoker.executeCommand(createCommand);
    
    // 3. Use State Pattern
    OrderContext orderContext = new OrderContext(bill);
    orderContext.processOrder();
    
    // 4. Save bill
    boolean success = saveBillWithItems(bill, items, billDAO);
    
    if (success) {
        // 5. Complete order state
        orderContext.completeOrder();
        
        // 6. Notify observers (Observer Pattern)
        orderManager.updateOrderStatus(bill.getId(), "COMPLETED", "Order processed successfully");
        
        // 7. Use Visitor Pattern for analytics
        SalesReportVisitor visitor = new SalesReportVisitor();
        for (CartItem item : items) {
            Book book = bookDAO.getBookById(item.bookId);
            visitor.visit(book);
        }
    } else {
        // Undo command if save failed
        orderInvoker.undoLastCommand();
    }
}
```

## 🌐 Web Interface Usage

### Design Patterns Demo Page
Access the interactive demo at: `http://localhost:8080/bookshop-billing/jsp/design-patterns.jsp`

### Features Available
1. **Command Pattern Demo**: View history and undo operations
2. **Observer Pattern Demo**: Process orders and see notifications
3. **State Pattern Demo**: Manage order state transitions
4. **Template Pattern Demo**: Generate various reports
5. **Decorator Pattern Info**: See how books are enhanced
6. **Visitor Pattern Info**: View analytics collection
7. **Complete Demo**: Run all patterns together

### Dashboard Integration
The design patterns demo is accessible from the main dashboard via the "Design Patterns Demo" button.

## 🔍 Testing the Patterns

### 1. Test Command Pattern
```bash
# View command history
curl "http://localhost:8080/bookshop-billing/controller/command-history?action=history"

# Undo last command
curl "http://localhost:8080/bookshop-billing/controller/command-history?action=undo"
```

### 2. Test State Pattern
```bash
# Process an order
curl "http://localhost:8080/bookshop-billing/controller/order-state?billId=1&action=process"

# Complete an order
curl "http://localhost:8080/bookshop-billing/controller/order-state?billId=1&action=complete"
```

### 3. Test Template Pattern
```bash
# Generate sales report
curl "http://localhost:8080/bookshop-billing/controller/generate-report?reportType=SALES"
```

### 4. Test Complete Demo
```bash
# Run complete pattern demonstration
curl "http://localhost:8080/bookshop-billing/controller/pattern-demo"
```

## 📈 Benefits Achieved

### 1. **Maintainability**
- Modular design with single responsibility
- Easy to add new commands, states, decorators, and observers
- Clear separation of concerns

### 2. **Extensibility**
- New payment methods via Strategy Pattern
- New discount types via Factory Pattern
- New order states via State Pattern
- New decorations via Decorator Pattern

### 3. **Flexibility**
- Runtime behavior modification
- Undo/redo functionality
- Dynamic object enhancement
- Pluggable components

### 4. **Testability**
- Each pattern can be unit tested independently
- Mock objects can be easily created
- Behavior can be verified in isolation

## 🎉 Conclusion

All design patterns are now fully integrated and functional in the BookShop Billing Application. The patterns work together seamlessly to provide a robust, maintainable, and extensible billing system.

### Pattern Integration Summary:
- ✅ **Singleton**: Database connections and managers
- ✅ **Strategy**: Payment methods
- ✅ **Factory**: Discount types
- ✅ **Builder**: Bill construction
- ✅ **Command**: Order operations with undo
- ✅ **Observer**: Order status notifications
- ✅ **State**: Order lifecycle management
- ✅ **Decorator**: Book enhancement
- ✅ **Template**: Report generation
- ✅ **Visitor**: Sales analytics
- ✅ **MVC**: Application architecture
- ✅ **DAO**: Data access abstraction

**Total Patterns Implemented: 12**

The application now demonstrates professional software design principles and is ready for production use with all design patterns actively utilized in the billing workflow.