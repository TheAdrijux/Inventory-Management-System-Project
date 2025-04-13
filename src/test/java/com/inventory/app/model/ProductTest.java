package com.inventory.app.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;

public class ProductTest {
    private Product product;
    private Category category;

    @BeforeEach
    void setUp() {
        category = new Category();
        category.setId(1L);
        category.setName("Test Category");

        product = new Product();
        product.setId(1L);
        product.setName("Test Product");
        product.setPrice(new BigDecimal("10.00"));
        product.setQuantity(100);
        product.setCategory(category);
    }

    @Test
    void testProductCreation() {
        assertNotNull(product);
        assertEquals(1L, product.getId());
        assertEquals("Test Product", product.getName());
        assertEquals(new BigDecimal("10.00"), product.getPrice());
        assertEquals(100, product.getQuantity());
        assertSame(category, product.getCategory());
    }

    @ParameterizedTest
    @CsvSource({
        "5.99, true",
        "0.01, true",
        "999.99, true",
        "0.00, false",
        "-1.00, false"
    })
    void testValidPrices(BigDecimal price, boolean isValid) {
        if (isValid) {
            product.setPrice(price);
            assertEquals(price, product.getPrice());
            assertFalse(product.getPrice().compareTo(BigDecimal.ZERO) < 0);
        } else {
            assertThrows(IllegalArgumentException.class, () -> product.setPrice(price));
        }
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1, -100})
    void testInvalidQuantities(int invalidQuantity) {
        assertThrows(IllegalArgumentException.class, () -> product.setQuantity(invalidQuantity));
    }

    @Test
    void testProductToString() {
        String expected = "Test Product";
        assertEquals(expected, product.toString());
        assertNotNull(product.toString());
    }

    @Test
    void testSetInvalidPrice() {
        assertAll("Price validation",
            () -> assertThrows(IllegalArgumentException.class, () -> 
                product.setPrice(new BigDecimal("-10.00"))),
            () -> assertThrows(IllegalArgumentException.class, () -> 
                product.setPrice(BigDecimal.ZERO)),
            () -> {
                product.setPrice(new BigDecimal("1.00"));
                assertTrue(product.getPrice().compareTo(BigDecimal.ZERO) > 0);
            }
        );
    }

    @Test
    void testSetInvalidQuantity() {
        assertThrows(IllegalArgumentException.class, () -> product.setQuantity(-1));
    }

    @Test
    void testUpdateQuantity() {
        product.setQuantity(50);
        assertEquals(50, product.getQuantity());
    }

    @Test
    void testEqualsAndHashCode() {
        Product sameProduct = new Product();
        sameProduct.setId(1L);
        sameProduct.setName("Test Product");
        
        Product differentProduct = new Product();
        differentProduct.setId(2L);
        differentProduct.setName("Different Product");

        assertEquals(product, sameProduct);
        assertEquals(product.hashCode(), sameProduct.hashCode());
        assertNotEquals(product, differentProduct);
        assertNotEquals(product.hashCode(), differentProduct.hashCode());
    }
} 