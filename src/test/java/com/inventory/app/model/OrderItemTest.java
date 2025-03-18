package com.inventory.app.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

public class OrderItemTest {
    private OrderItem orderItem;
    private Order order;
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

        Client client = new Client();
        client.setId(1L);
        client.setName("Test Client");

        order = new Order();
        order.setId(1L);
        order.setClient(client);
        order.setOrderDate(LocalDateTime.now());

        orderItem = new OrderItem();
        orderItem.setId(1L);
        orderItem.setOrder(order);
        orderItem.setProduct(product);
        orderItem.setQuantity(5);
        orderItem.setPricePerUnit(product.getPrice());
        orderItem.setUnitPrice(product.getPrice());
    }

    @Test
    void testOrderItemCreation() {
        assertNotNull(orderItem);
        assertEquals(1L, orderItem.getId());
        assertEquals(order, orderItem.getOrder());
        assertEquals(product, orderItem.getProduct());
        assertEquals(5, orderItem.getQuantity());
        assertEquals(new BigDecimal("10.00"), orderItem.getPricePerUnit());
        assertEquals(new BigDecimal("10.00"), orderItem.getUnitPrice());
    }

    @Test
    void testConstructorWithFields() {
        OrderItem newItem = new OrderItem(order, product, 3);
        assertEquals(order, newItem.getOrder());
        assertEquals(product, newItem.getProduct());
        assertEquals(3, newItem.getQuantity());
        assertEquals(product.getPrice(), newItem.getPricePerUnit());
        assertEquals(product.getPrice(), newItem.getUnitPrice());
        assertEquals(product.getPrice().multiply(new BigDecimal("3")), newItem.getTotalPrice());
    }

    @Test
    void testUpdateQuantity() {
        orderItem.setQuantity(10);
        assertEquals(10, orderItem.getQuantity());
        assertEquals(new BigDecimal("100.00"), orderItem.getTotalPrice());
    }

    @Test
    void testUpdatePrices() {
        BigDecimal newPrice = new BigDecimal("15.00");
        orderItem.setPricePerUnit(newPrice);
        orderItem.setUnitPrice(newPrice);
        assertEquals(newPrice, orderItem.getPricePerUnit());
        assertEquals(newPrice, orderItem.getUnitPrice());
        assertEquals(newPrice.multiply(new BigDecimal("5")), orderItem.getTotalPrice());
    }

    @Test
    void testGetSubtotal() {
        BigDecimal expectedSubtotal = new BigDecimal("50.00"); // 5 * 10.00
        assertEquals(expectedSubtotal, orderItem.getSubtotal());
    }

    @Test
    void testToString() {
        String expected = "Test Product x 5 @ 10.00";
        assertEquals(expected, orderItem.toString());
    }

    @Test
    void testSetProduct() {
        Product newProduct = new Product();
        newProduct.setId(2L);
        newProduct.setName("New Product");
        newProduct.setPrice(new BigDecimal("20.00"));
        newProduct.setQuantity(50);
        newProduct.setCategory(category);

        orderItem.setProduct(newProduct);
        assertEquals(newProduct, orderItem.getProduct());
        assertEquals(newProduct.getPrice(), orderItem.getPricePerUnit());
        assertEquals(newProduct.getPrice(), orderItem.getUnitPrice());
        assertEquals(newProduct.getPrice().multiply(new BigDecimal("5")), orderItem.getTotalPrice());
    }
} 