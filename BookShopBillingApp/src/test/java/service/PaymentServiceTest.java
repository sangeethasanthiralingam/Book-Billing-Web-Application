package service;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Payment Strategy Pattern Tests")
class PaymentServiceTest {

    @ParameterizedTest
    @DisplayName("Should process payments correctly for all strategies")
    @MethodSource("paymentStrategyProvider")
    void testPaymentStrategies(PaymentStrategy strategy, double amount, boolean expectedResult) {
        boolean result = strategy.processPayment(amount);
        assertEquals(expectedResult, result);
    }

    static Stream<Arguments> paymentStrategyProvider() {
        return Stream.of(
            Arguments.of(new CashPayment(), 100.0, true),
            Arguments.of(new CardPayment("1234-5678-9012-3456", "VISA"), 100.0, true),
            Arguments.of(new UpiPayment("test@paytm"), 100.0, true),
            Arguments.of(new CashPayment(), -10.0, false),
            Arguments.of(new CardPayment("invalid", "VISA"), 100.0, false),
            Arguments.of(new UpiPayment("invalid-upi"), 100.0, false),
            Arguments.of(new CashPayment(), 0.0, false)
        );
    }

    @Test
    @DisplayName("Should get correct payment details for cash payment")
    void testCashPaymentDetails() {
        PaymentStrategy cashPayment = new CashPayment();
        String details = cashPayment.getPaymentDetails();
        assertTrue(details.contains("Cash"));
    }

    @Test
    @DisplayName("Should get correct payment details for card payment")
    void testCardPaymentDetails() {
        PaymentStrategy cardPayment = new CardPayment("1234-5678-9012-3456", "VISA");
        String details = cardPayment.getPaymentDetails();
        assertTrue(details.contains("VISA"));
        assertTrue(details.contains("3456")); // Last 4 digits
    }

    @Test
    @DisplayName("Should get correct payment details for UPI payment")
    void testUpiPaymentDetails() {
        PaymentStrategy upiPayment = new UpiPayment("test@paytm");
        String details = upiPayment.getPaymentDetails();
        assertTrue(details.contains("UPI"));
        assertTrue(details.contains("test@paytm"));
    }

    @Test
    @DisplayName("Should handle zero amount payment")
    void testZeroAmountPayment() {
        PaymentStrategy cashPayment = new CashPayment();
        boolean result = cashPayment.processPayment(0.0);
        assertFalse(result);
    }

    @Test
    @DisplayName("Should handle large amount payment")
    void testLargeAmountPayment() {
        PaymentStrategy cashPayment = new CashPayment();
        boolean result = cashPayment.processPayment(999999.99);
        assertTrue(result);
    }

    @Test
    @DisplayName("Should validate card number format")
    void testCardNumberValidation() {
        PaymentStrategy invalidCard = new CardPayment("123", "VISA");
        boolean result = invalidCard.processPayment(100.0);
        assertFalse(result);
    }

    @Test
    @DisplayName("Should validate UPI ID format")
    void testUpiIdValidation() {
        PaymentStrategy invalidUpi = new UpiPayment("invalid-upi");
        boolean result = invalidUpi.processPayment(100.0);
        assertFalse(result);
    }
}
