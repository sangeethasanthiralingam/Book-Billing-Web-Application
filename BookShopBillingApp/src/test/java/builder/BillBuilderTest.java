package builder;

import model.*;
import util.TestDataBuilder;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("BillBuilder Pattern Tests")
class BillBuilderTest {
    
    private User testCustomer;
    private User testCashier;
    private Book testBook;
    
    @BeforeEach
    void setUp() {
        testCustomer = TestDataBuilder.createTestCustomer();
        testCashier = TestDataBuilder.createTestCashier();
        testBook = TestDataBuilder.createTestBook();
    }
    
    @Test
    @DisplayName("Should create empty bill with default values")
    void testCreateEmptyBill() {
        // When
        Bill bill = BillBuilder.createNewBill().build();
        
        // Then
        assertNotNull(bill);
        assertNotNull(bill.getBillNumber());
        assertNotNull(bill.getBillDate());
        assertEquals("PENDING", bill.getStatus());
        assertEquals(0.0, bill.getSubtotal(), 0.01);
        assertNotNull(bill.getItems());
        assertTrue(bill.getItems().isEmpty());
    }
    
    @Test
    @DisplayName("Should build bill with customer and cashier")
    void testBuildBillWithUsers() {
        // When
        Bill bill = BillBuilder.createNewBill()
            .withCustomer(testCustomer)
            .withCashier(testCashier)
            .build();
        
        // Then
        assertEquals(testCustomer, bill.getCustomer());
        assertEquals(testCashier, bill.getCashier());
    }
    
    @Test
    @DisplayName("Should add items and calculate totals")
    void testAddItemsAndCalculateTotals() {
        // Given
        BillItem item1 = new BillItem();
        item1.setBook(testBook);
        item1.setQuantity(2);
        item1.setUnitPrice(10.99);
        item1.setTotal(21.98);
        
        BillItem item2 = new BillItem();
        item2.setBook(testBook);
        item2.setQuantity(1);
        item2.setUnitPrice(15.99);
        item2.setTotal(15.99);
        
        // When
        Bill bill = BillBuilder.createNewBill()
            .addItem(item1)
            .addItem(item2)
            .build();
        
        // Then
        assertEquals(2, bill.getItems().size());
        assertEquals(37.97, bill.getSubtotal(), 0.01);
    }
    
    @Test
    @DisplayName("Should validate customer is not null")
    void testValidateCustomerNotNull() {
        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            BillBuilder.createNewBill()
                .withCustomer(null)
                .build();
        });
    }
    
    @Test
    @DisplayName("Should build complete bill with all components")
    void testBuildCompleteBill() {
        // Given
        BillItem item = new BillItem();
        item.setBook(testBook);
        item.setQuantity(2);
        item.setUnitPrice(19.99);
        item.setTotal(39.98);
        
        // When
        Bill bill = BillBuilder.createNewBill()
            .withCustomer(testCustomer)
            .withCashier(testCashier)
            .addItem(item)
            .withPaymentMethod("CASH")
            .build();
        
        // Then
        assertNotNull(bill);
        assertEquals(testCustomer, bill.getCustomer());
        assertEquals(testCashier, bill.getCashier());
        assertEquals(1, bill.getItems().size());
        assertEquals("CASH", bill.getPaymentMethod());
        assertEquals("PENDING", bill.getStatus());
    }
}