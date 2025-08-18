package util;

import model.*;
import java.math.BigDecimal;
import java.util.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Test Data Builder for generating test objects
 */
public class TestDataBuilder {
    
    private static final AtomicInteger COUNTER = new AtomicInteger(1);
    
    public static User createTestCustomer() {
        int id = COUNTER.getAndIncrement();
        User user = new User();
        user.setId(id);
        user.setUsername("customer" + id);
        user.setPassword("password123");
        user.setEmail("customer" + id + "@test.com");
        user.setRole("CUSTOMER");
        user.setFullName("Test Customer " + id);
        user.setPhone("555-000" + String.format("%04d", id));
        return user;
    }
    
    public static User createTestCashier() {
        int id = COUNTER.getAndIncrement();
        User user = new User();
        user.setId(id);
        user.setUsername("cashier" + id);
        user.setPassword("cashier123");
        user.setEmail("cashier" + id + "@test.com");
        user.setRole("CASHIER");
        user.setFullName("Test Cashier " + id);
        user.setPhone("555-100" + String.format("%04d", id));
        return user;
    }
    
    public static User createTestAdmin() {
        int id = COUNTER.getAndIncrement();
        User user = new User();
        user.setId(id);
        user.setUsername("admin" + id);
        user.setPassword("admin123");
        user.setEmail("admin" + id + "@test.com");
        user.setRole("ADMIN");
        user.setFullName("Test Admin " + id);
        user.setPhone("555-200" + String.format("%04d", id));
        return user;
    }
    
    public static Book createTestBook() {
        int id = COUNTER.getAndIncrement();
        Book book = new Book();
        book.setId(id);
        book.setTitle("Test Book " + id);
        book.setAuthor("Test Author " + id);
        book.setIsbn("978-0-123-45678-" + (id % 10));
        book.setPrice(10.99 + id);
        book.setQuantity(50 + id);
        book.setCategory("Test Category");
        return book;
    }
    
    public static Bill createTestBill() {
        Bill bill = new Bill();
        bill.setId(COUNTER.getAndIncrement());
        bill.setBillNumber("BILL-" + System.currentTimeMillis());
        bill.setBillDate(new Date());
        bill.setCustomer(createTestCustomer());
        bill.setCashier(createTestCashier());
        bill.setSubtotal(100.0);
        bill.setDiscount(10.0);
        bill.setTax(8.5);
        bill.setTotal(98.5);
        bill.setPaymentMethod("CASH");
        bill.setStatus("PENDING");
        
        List<BillItem> items = new ArrayList<>();
        items.add(createTestBillItem());
        bill.setItems(items);
        
        return bill;
    }
    
    public static BillItem createTestBillItem() {
        BillItem item = new BillItem();
        item.setId(COUNTER.getAndIncrement());
        item.setBook(createTestBook());
        item.setQuantity(2);
        item.setUnitPrice(19.99);
        item.setTotal(39.98);
        return item;
    }
    
    public static String generateCollectionJSON(int bookCount) {
        StringBuilder json = new StringBuilder("{\"books\":[");
        for (int i = 0; i < bookCount; i++) {
            if (i > 0) json.append(",");
            json.append(String.format(
                "{\"id\":%d,\"title\":\"Test Book %d\",\"author\":\"Test Author %d\",\"price\":%.2f}",
                i + 1, i + 1, i + 1, 10.99 + i
            ));
        }
        json.append("],\"note\":\"Test collection note\"}");
        return json.toString();
    }
}