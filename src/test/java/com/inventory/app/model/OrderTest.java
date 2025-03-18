package com.inventory.app.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import static org.junit.jupiter.api.Assertions.*;

public class OrderTest {
    private Order order;
    private Client client;
    private Product product1;
    private Product product2;

    @BeforeEach
    void setUp() {
        client = new Client();
        client.setId(1L);
        client.setName("Test Client");
        client.setEmail("test@example.com");
        client.setPhone("1234567890");

        Category category = new Category();
        category.setId(1L);
        category.setName("Test Category");

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

        order = new Order();
        order.setId(1L);
        order.setClient(client);
        order.setOrderDate(LocalDateTime.now());
        order.setOrderItems(new ArrayList<>());
    }

    @Test
    void testOrderCreation() {
        assertNotNull(order);
        assertEquals(1L, order.getId());
        assertEquals(client, order.getClient());
        assertNotNull(order.getOrderDate());
        assertTrue(order.getOrderItems().isEmpty());
    }

    @Test
    void testAddOrderItem() {
        OrderItem item = new OrderItem();
        item.setProduct(product1);
        item.setQuantity(2);
        item.setPricePerUnit(product1.getPrice());
        item.setUnitPrice(product1.getPrice());
        
        order.addOrderItem(item);

        assertEquals(1, order.getOrderItems().size());
        assertEquals(item, order.getOrderItems().get(0));
        assertEquals(order, item.getOrder());
    }

    @Test
    void testCalculateTotal() {
        // Add first item
        OrderItem item1 = new OrderItem();
        item1.setProduct(product1);
        item1.setQuantity(2);
        item1.setPricePerUnit(product1.getPrice());
        item1.setUnitPrice(product1.getPrice());
        order.addOrderItem(item1);

        // Add second item
        OrderItem item2 = new OrderItem();
        item2.setProduct(product2);
        item2.setQuantity(3);
        item2.setPricePerUnit(product2.getPrice());
        item2.setUnitPrice(product2.getPrice());
        order.addOrderItem(item2);

        // Calculate expected total: (2 * 10.00) + (3 * 20.00) = 80.00
        BigDecimal expectedTotal = new BigDecimal("80.00");
        order.recalculateTotal();
        assertEquals(expectedTotal, order.getTotalAmount());
    }

    @Test
    void testRemoveOrderItem() {
        OrderItem item = new OrderItem();
        item.setProduct(product1);
        item.setQuantity(2);
        item.setPricePerUnit(product1.getPrice());
        item.setUnitPrice(product1.getPrice());
        
        order.addOrderItem(item);
        assertEquals(1, order.getOrderItems().size());
        
        order.removeOrderItem(item);
        assertEquals(0, order.getOrderItems().size());
    }

    @Test
    void testEqualsAndHashCode() {
        Order sameOrder = new Order();
        sameOrder.setId(1L);
        sameOrder.setClient(client);
        
        Order differentOrder = new Order();
        differentOrder.setId(2L);
        differentOrder.setClient(client);

        assertEquals(order, sameOrder);
        assertEquals(order.hashCode(), sameOrder.hashCode());
        assertNotEquals(order, differentOrder);
        assertNotEquals(order.hashCode(), differentOrder.hashCode());
    }
} 