package com.inventory.app.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import java.util.ArrayList;
import static org.junit.jupiter.api.Assertions.*;

public class ClientTest {
    private Client client;
    private Order order1;
    private Order order2;

    @BeforeEach
    void setUp() {
        client = new Client();
        client.setId(1L);
        client.setName("Test Client");
        client.setEmail("test@example.com");
        client.setPhone("1234567890");
        client.setOrders(new ArrayList<>());

        order1 = new Order();
        order1.setId(1L);
        order1.setOrderDate(LocalDateTime.now());
        order1.setOrderItems(new ArrayList<>());

        order2 = new Order();
        order2.setId(2L);
        order2.setOrderDate(LocalDateTime.now());
        order2.setOrderItems(new ArrayList<>());
    }

    @Test
    void testClientCreation() {
        assertNotNull(client);
        assertEquals(1L, client.getId());
        assertEquals("Test Client", client.getName());
        assertEquals("test@example.com", client.getEmail());
        assertEquals("1234567890", client.getPhone());
        assertTrue(client.getOrders().isEmpty());
    }

    @Test
    void testAddOrder() {
        client.addOrder(order1);
        assertEquals(1, client.getOrders().size());
        assertEquals(order1, client.getOrders().get(0));
        assertEquals(client, order1.getClient());
    }

    @Test
    void testRemoveOrder() {
        client.addOrder(order1);
        assertEquals(1, client.getOrders().size());
        
        client.removeOrder(order1);
        assertEquals(0, client.getOrders().size());
        assertNull(order1.getClient());
    }

    @Test
    void testMultipleOrders() {
        client.addOrder(order1);
        client.addOrder(order2);
        
        assertEquals(2, client.getOrders().size());
        assertTrue(client.getOrders().contains(order1));
        assertTrue(client.getOrders().contains(order2));
        assertEquals(client, order1.getClient());
        assertEquals(client, order2.getClient());
    }

    @Test
    void testToString() {
        String expected = "Test Client (test@example.com)";
        assertEquals(expected, client.toString());
    }

    @Test
    void testEqualsAndHashCode() {
        Client sameClient = new Client();
        sameClient.setId(1L);
        sameClient.setName("Test Client");
        sameClient.setEmail("test@example.com");
        
        Client differentClient = new Client();
        differentClient.setId(2L);
        differentClient.setName("Different Client");
        differentClient.setEmail("different@example.com");

        assertEquals(client, sameClient);
        assertEquals(client.hashCode(), sameClient.hashCode());
        assertNotEquals(client, differentClient);
        assertNotEquals(client.hashCode(), differentClient.hashCode());
    }

    @Test
    void testConstructorWithFields() {
        Client newClient = new Client("New Client", "new@example.com", "0987654321");
        assertEquals("New Client", newClient.getName());
        assertEquals("new@example.com", newClient.getEmail());
        assertEquals("0987654321", newClient.getPhone());
    }
} 