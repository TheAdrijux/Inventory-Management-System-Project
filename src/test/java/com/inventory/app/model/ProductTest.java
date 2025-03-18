package com.inventory.app.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
        assertEquals(category, product.getCategory());
    }

    @Test
    void testProductToString() {
        String expected = "Test Product";
        assertEquals(expected, product.toString());
    }

    @Test
    void testSetInvalidPrice() {
        assertThrows(IllegalArgumentException.class, () -> 
            product.setPrice(new BigDecimal("-10.00")));
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