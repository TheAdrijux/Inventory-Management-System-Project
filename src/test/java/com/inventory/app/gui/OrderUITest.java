package com.inventory.app.gui;

import com.inventory.app.model.*;
import javafx.scene.control.TableView;
import javafx.scene.control.ComboBox;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(ApplicationExtension.class)
public class OrderUITest {
    private OrderUI orderUI;
    private TableView<Order> orderTable;
    private TableView<OrderItem> orderItemsTable;
    private ComboBox<Client> clientFilter;

    @Start
    private void start(Stage stage) {
        orderUI = new OrderUI(stage);
        orderTable = (TableView<Order>) orderUI.getContent().lookup("#orderTable");
        orderItemsTable = (TableView<OrderItem>) orderUI.getContent().lookup("#orderItemsTable");
        clientFilter = (ComboBox<Client>) orderUI.getContent().lookup("ComboBox");
    }

    @Test
    void testInitialState() {
        assertNotNull(orderUI);
        assertNotNull(orderTable);
        assertNotNull(orderItemsTable);
        assertNotNull(clientFilter);
        assertTrue(orderTable.getItems().isEmpty());
        assertTrue(orderItemsTable.getItems().isEmpty());
    }

    @Test
    void testAddOrder() {
        Client client = new Client();
        client.setId(1L);
        client.setName("Test Client");
        client.setEmail("test@example.com");

        Category category = new Category();
        category.setId(1L);
        category.setName("Test Category");

        Product product = new Product();
        product.setId(1L);
        product.setName("Test Product");
        product.setPrice(new BigDecimal("10.00"));
        product.setQuantity(100);
        product.setCategory(category);

        Order order = new Order();
        order.setId(1L);
        order.setClient(client);
        order.setOrderDate(LocalDateTime.now());

        OrderItem item = new OrderItem();
        item.setId(1L);
        item.setOrder(order);
        item.setProduct(product);
        item.setQuantity(5);
        item.setPricePerUnit(product.getPrice());
        item.setUnitPrice(product.getPrice());

        order.addOrderItem(item);
        orderTable.getItems().add(order);

        assertEquals(1, orderTable.getItems().size());
        assertEquals(order, orderTable.getItems().get(0));

        // Select the order to show its items
        orderTable.getSelectionModel().select(order);
        assertEquals(1, orderItemsTable.getItems().size());
        assertEquals(item, orderItemsTable.getItems().get(0));
    }

    @Test
    void testTableColumns() {
        // Order table columns
        assertTrue(orderTable.getColumns().stream()
            .anyMatch(col -> col.getText().equals("Order ID")));
        assertTrue(orderTable.getColumns().stream()
            .anyMatch(col -> col.getText().equals("Client")));
        assertTrue(orderTable.getColumns().stream()
            .anyMatch(col -> col.getText().equals("Date")));
        assertTrue(orderTable.getColumns().stream()
            .anyMatch(col -> col.getText().equals("Total")));

        // Order items table columns
        assertTrue(orderItemsTable.getColumns().stream()
            .anyMatch(col -> col.getText().equals("Product")));
        assertTrue(orderItemsTable.getColumns().stream()
            .anyMatch(col -> col.getText().equals("Quantity")));
        assertTrue(orderItemsTable.getColumns().stream()
            .anyMatch(col -> col.getText().equals("Price")));
        assertTrue(orderItemsTable.getColumns().stream()
            .anyMatch(col -> col.getText().equals("Subtotal")));
    }

    @Test
    void testOrderTotal() {
        Client client = new Client();
        client.setId(1L);
        client.setName("Test Client");

        Category category = new Category();
        category.setId(1L);
        category.setName("Test Category");

        Product product1 = new Product();
        product1.setId(1L);
        product1.setName("Product 1");
        product1.setPrice(new BigDecimal("10.00"));
        product1.setQuantity(100);
        product1.setCategory(category);

        Product product2 = new Product();
        product2.setId(2L);
        product2.setName("Product 2");
        product2.setPrice(new BigDecimal("20.00"));
        product2.setQuantity(50);
        product2.setCategory(category);

        Order order = new Order();
        order.setId(1L);
        order.setClient(client);
        order.setOrderDate(LocalDateTime.now());

        OrderItem item1 = new OrderItem();
        item1.setOrder(order);
        item1.setProduct(product1);
        item1.setQuantity(2);
        item1.setPricePerUnit(product1.getPrice());
        item1.setUnitPrice(product1.getPrice());
        order.addOrderItem(item1);

        OrderItem item2 = new OrderItem();
        item2.setOrder(order);
        item2.setProduct(product2);
        item2.setQuantity(3);
        item2.setPricePerUnit(product2.getPrice());
        item2.setUnitPrice(product2.getPrice());
        order.addOrderItem(item2);

        order.recalculateTotal();
        orderTable.getItems().add(order);

        assertEquals(1, orderTable.getItems().size());
        assertEquals(new BigDecimal("80.00"), order.getTotalAmount()); // (2 * 10.00) + (3 * 20.00)
    }
} 