package factory;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Discount Factory Pattern Tests")
class DiscountFactoryTest {
    
    @Test
    @DisplayName("Should create percentage discount")
    void testCreatePercentageDiscount() {
        // When
        Discount discount = DiscountFactory.createDiscount("PERCENTAGE", 10.0);
        
        // Then
        assertNotNull(discount);
        assertTrue(discount instanceof PercentageDiscount);
        assertEquals(10.0, discount.calculateDiscount(100.0), 0.01);
    }
    
    @Test
    @DisplayName("Should create fixed discount")
    void testCreateFixedDiscount() {
        // When
        Discount discount = DiscountFactory.createDiscount("FIXED", 15.0);
        
        // Then
        assertNotNull(discount);
        assertTrue(discount instanceof FixedDiscount);
        assertEquals(15.0, discount.calculateDiscount(100.0), 0.01);
    }
    
    @Test
    @DisplayName("Should throw exception for invalid discount type")
    void testInvalidDiscountType() {
        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            DiscountFactory.createDiscount("INVALID", 10.0);
        });
    }
    
    @Test
    @DisplayName("Should handle case insensitive discount types")
    void testCaseInsensitiveDiscountTypes() {
        // When
        Discount percentageDiscount = DiscountFactory.createDiscount("percentage", 10.0);
        Discount fixedDiscount = DiscountFactory.createDiscount("fixed", 15.0);
        
        // Then
        assertTrue(percentageDiscount instanceof PercentageDiscount);
        assertTrue(fixedDiscount instanceof FixedDiscount);
    }
    
    @Test
    @DisplayName("Should validate percentage discount range")
    void testPercentageDiscountValidation() {
        // Given
        Discount discount = DiscountFactory.createDiscount("PERCENTAGE", 150.0);
        
        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            discount.calculateDiscount(100.0);
        });
    }
    
    @Test
    @DisplayName("Should validate fixed discount amount")
    void testFixedDiscountValidation() {
        // Given
        Discount discount = DiscountFactory.createDiscount("FIXED", -10.0);
        
        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            discount.calculateDiscount(100.0);
        });
    }
    
    @Test
    @DisplayName("Should calculate percentage discount correctly")
    void testPercentageDiscountCalculation() {
        // Given
        Discount discount = DiscountFactory.createDiscount("PERCENTAGE", 25.0);
        
        // When
        double result = discount.calculateDiscount(200.0);
        
        // Then
        assertEquals(50.0, result, 0.01);
    }
    
    @Test
    @DisplayName("Should calculate fixed discount correctly")
    void testFixedDiscountCalculation() {
        // Given
        Discount discount = DiscountFactory.createDiscount("FIXED", 30.0);
        
        // When
        double result = discount.calculateDiscount(200.0);
        
        // Then
        assertEquals(30.0, result, 0.01);
    }
    
    @Test
    @DisplayName("Should not exceed original amount for fixed discount")
    void testFixedDiscountNotExceedOriginal() {
        // Given
        Discount discount = DiscountFactory.createDiscount("FIXED", 150.0);
        
        // When
        double result = discount.calculateDiscount(100.0);
        
        // Then
        assertEquals(100.0, result, 0.01); // Should not exceed original amount
    }
}