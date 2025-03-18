package com.inventory.app.gui;

import com.inventory.app.model.*;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(ApplicationExtension.class)
public class ClientUITest {
    private ClientUI clientUI;
    private TableView<Client> clientTable;
    private TableView<Order> orderHistoryTable;
    private TextField searchField;

    @Start
    private void start(Stage stage) {
        clientUI = new ClientUI(stage);
        clientTable = (TableView<Client>) clientUI.getContent().lookup("#clientTable");
        orderHistoryTable = (TableView<Order>) clientUI.getContent().lookup("#orderHistoryTable");
        searchField = (TextField) clientUI.getContent().lookup("TextField");
    }

    @Test
    void testInitialState() {
        assertNotNull(clientUI);
        assertNotNull(clientTable);
        assertNotNull(orderHistoryTable);
        assertNotNull(searchField);
        assertTrue(clientTable.getItems().isEmpty());
        assertTrue(orderHistoryTable.getItems().isEmpty());
    }

    @Test
    void testAddClient() {
        Client client = new Client();
        client.setId(1L);
        client.setName("Test Client");
        client.setEmail("test@example.com");
        client.setPhone("1234567890");

        clientTable.getItems().add(client);

        assertEquals(1, clientTable.getItems().size());
        assertEquals(client, clientTable.getItems().get(0));
    }

    @Test
    void testFilterClients() {
        Client client1 = new Client();
        client1.setId(1L);
        client1.setName("Test Client");
        client1.setEmail("test@example.com");

        Client client2 = new Client();
        client2.setId(2L);
        client2.setName("Different Client");
        client2.setEmail("different@example.com");

        clientTable.getItems().addAll(client1, client2);

        // Test search filter
        searchField.setText("Test");
        assertEquals(1, clientTable.getItems().size());
        assertEquals(client1, clientTable.getItems().get(0));

        // Clear filter
        searchField.clear();
        assertEquals(2, clientTable.getItems().size());
    }

    @Test
    void testOrderHistory() {
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
        client.addOrder(order);
        clientTable.getItems().add(client);

        // Select the client to show their order history
        clientTable.getSelectionModel().select(client);
        assertEquals(1, orderHistoryTable.getItems().size());
        assertEquals(order, orderHistoryTable.getItems().get(0));
    }

    @Test
    void testTableColumns() {
        // Client table columns
        assertTrue(clientTable.getColumns().stream()
            .anyMatch(col -> col.getText().equals("Name")));
        assertTrue(clientTable.getColumns().stream()
            .anyMatch(col -> col.getText().equals("Email")));
        assertTrue(clientTable.getColumns().stream()
            .anyMatch(col -> col.getText().equals("Phone")));

        // Order history table columns
        assertTrue(orderHistoryTable.getColumns().stream()
            .anyMatch(col -> col.getText().equals("Order ID")));
        assertTrue(orderHistoryTable.getColumns().stream()
            .anyMatch(col -> col.getText().equals("Date")));
        assertTrue(orderHistoryTable.getColumns().stream()
            .anyMatch(col -> col.getText().equals("Total")));
    }
} 