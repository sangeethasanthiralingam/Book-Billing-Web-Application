package integration;

import dao.*;
import model.*;
import service.*;
import builder.BillBuilder;
import factory.DiscountFactory;
import util.TestDataBuilder;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("Billing System Integration Tests")
class BillingSystemIntegrationTest {

    private User testCustomer;
    private User testCashier;
    private Book testBook;
    private BookDAO bookDAO;
    private UserDAO userDAO;
    private BillDAO billDAO;

    @BeforeAll
    void setupTestData() {
        bookDAO = new BookDAO();
        userDAO = new UserDAO();
        billDAO = new BillDAO();

        testCustomer = TestDataBuilder.createTestCustomer();
        testCashier = TestDataBuilder.createTestCashier();
        testBook = TestDataBuilder.createTestBook();

        userDAO.save(testCustomer);
        userDAO.save(testCashier);
        bookDAO.save(testBook);
    }

    @Test
    @Order(1)
    @DisplayName("Should create complete billing workflow")
    void testCompleteBillingWorkflow() {
        BillItem item = new BillItem();
        item.setBook(testBook);
        item.setQuantity(2);
        item.setUnitPrice(testBook.getPrice());
        item.setTotal(testBook.getPrice() * 2);

        Bill bill = BillBuilder.createNewBill()
                .withCustomer(testCustomer)
                .withCashier(testCashier)
                .addItem(item)
                .withPaymentMethod("CASH")
                .build();

        boolean saved = billDAO.save(bill);

        assertTrue(saved);
        assertNotNull(bill.getBillNumber());
        assertEquals(testCustomer.getId(), bill.getCustomer().getId());
        assertEquals(1, bill.getItems().size());
    }

    @Test
    @Order(2)
    @DisplayName("Should process payment with Strategy pattern")
    void testPaymentProcessing() {
        double amount = 100.0;

        PaymentStrategy cashPayment = new CashPayment();
        PaymentStrategy cardPayment = new CardPayment("1234-5678-9012-3456", "VISA");
        PaymentStrategy upiPayment = new UpiPayment("test@upi");

        assertTrue(cashPayment.processPayment(amount));
        assertTrue(cardPayment.processPayment(amount));
        assertTrue(upiPayment.processPayment(amount));

        assertTrue(cashPayment.getPaymentDetails().contains("Cash"));
        assertTrue(cardPayment.getPaymentDetails().contains("VISA"));
        assertTrue(upiPayment.getPaymentDetails().contains("UPI"));
    }

    @Test
    @Order(3)
    @DisplayName("Should apply discounts with Factory pattern")
    void testDiscountApplication() {
        var percentageDiscount = DiscountFactory.createDiscount("PERCENTAGE", 10.0);
        var fixedDiscount = DiscountFactory.createDiscount("FIXED", 15.0);

        double originalAmount = 200.0;

        double percentageDiscountAmount = percentageDiscount.calculateDiscount(originalAmount);
        double fixedDiscountAmount = fixedDiscount.calculateDiscount(originalAmount);

        assertEquals(20.0, percentageDiscountAmount, 0.01);
        assertEquals(15.0, fixedDiscountAmount, 0.01);
    }

    @Test
    @Order(4)
    @DisplayName("Should handle inventory updates")
    void testInventoryManagement() {
        int initialQuantity = testBook.getQuantity();
        int soldQuantity = 3;

        testBook.setQuantity(initialQuantity - soldQuantity);
        boolean updated = bookDAO.update(testBook);

        assertTrue(updated);
        Book updatedBook = bookDAO.findById(testBook.getId());
        assertEquals(initialQuantity - soldQuantity, updatedBook.getQuantity());
    }

    @Test
    @Order(5)
    @DisplayName("Should retrieve customer purchase history")
    void testCustomerPurchaseHistory() {
        List<Bill> customerBills = billDAO.getBillsByCustomer(testCustomer.getId());

        assertNotNull(customerBills);
        assertFalse(customerBills.isEmpty());

        Bill firstBill = customerBills.get(0);
        assertNotNull(firstBill.getCustomer());
        assertEquals(testCustomer.getId(), firstBill.getCustomer().getId());
    }

    @Test
    @Order(6)
    @DisplayName("Should handle concurrent bill creation")
    void testConcurrentBillCreation() throws InterruptedException, ExecutionException {
        int threadCount = 5;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        List<Future<Boolean>> futures = new ArrayList<>();

        for (int i = 0; i < threadCount; i++) {
            futures.add(executor.submit(() -> {
                Bill bill = BillBuilder.createNewBill()
                        .withCustomer(testCustomer)
                        .withCashier(testCashier)
                        .withPaymentMethod("CASH")
                        .build();
                return billDAO.save(bill);
            }));
        }

        executor.shutdown();
        executor.awaitTermination(5, TimeUnit.SECONDS);

        for (Future<Boolean> f : futures) {
            assertTrue(f.get());
        }
    }

    @Test
    @Order(7)
    @DisplayName("Should validate business rules")
    void testBusinessRuleValidation() {
        assertThrows(IllegalArgumentException.class, () -> {
            BillItem invalidItem = new BillItem();
            invalidItem.setBook(testBook);
            invalidItem.setQuantity(-1);

            BillBuilder.createNewBill()
                    .addItem(invalidItem)
                    .build();
        });

        assertThrows(IllegalArgumentException.class, () -> {
            BillBuilder.createNewBill()
                    .withCustomer(null)
                    .build();
        });

        PaymentStrategy payment = new CashPayment();
        assertFalse(payment.processPayment(-100.0));
        assertFalse(payment.processPayment(0.0));
    }

    @AfterAll
    void cleanupTestData() {
        // Optional: remove test data from DAOs
    }
}
