package com.inventory.app.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.util.ArrayList;
import static org.junit.jupiter.api.Assertions.*;

public class CategoryTest {
    private Category category;
    private Product product1;
    private Product product2;

    @BeforeEach
    void setUp() {
        category = new Category();
        category.setId(1L);
        category.setName("Test Category");
        category.setProducts(new ArrayList<>());

        product1 = new Product();
        product1.setId(1L);
        product1.setName("Product 1");
        product1.setPrice(new BigDecimal("10.00"));
        product1.setQuantity(100);
        product1.setCategory(category);

        product2 = new Product();
        product2.setId(2L);
        product2.setName("Product 2");
        product2.setPrice(new BigDecimal("20.00"));
        product2.setQuantity(50);
        product2.setCategory(category);
    }

    @Test
    void testCategoryCreation() {
        assertNotNull(category);
        assertEquals(1L, category.getId());
        assertEquals("Test Category", category.getName());
        assertTrue(category.getProducts().isEmpty());
    }

    @Test
    void testAddProduct() {
        category.getProducts().add(product1);
        assertEquals(1, category.getProducts().size());
        assertEquals(product1, category.getProducts().get(0));
        assertEquals(category, product1.getCategory());
    }

    @Test
    void testRemoveProduct() {
        category.getProducts().add(product1);
        assertEquals(1, category.getProducts().size());
        
        category.getProducts().remove(product1);
        assertEquals(0, category.getProducts().size());
    }

    @Test
    void testMultipleProducts() {
        category.getProducts().add(product1);
        category.getProducts().add(product2);
        
        assertEquals(2, category.getProducts().size());
        assertTrue(category.getProducts().contains(product1));
        assertTrue(category.getProducts().contains(product2));
    }

    @Test
    void testToString() {
        String expected = "Test Category";
        assertEquals(expected, category.toString());
    }

    @Test
    void testEqualsAndHashCode() {
        Category sameCategory = new Category();
        sameCategory.setId(1L);
        sameCategory.setName("Test Category");
        
        Category differentCategory = new Category();
        differentCategory.setId(2L);
        differentCategory.setName("Different Category");

        assertEquals(category, sameCategory);
        assertEquals(category.hashCode(), sameCategory.hashCode());
        assertNotEquals(category, differentCategory);
        assertNotEquals(category.hashCode(), differentCategory.hashCode());
    }
} 