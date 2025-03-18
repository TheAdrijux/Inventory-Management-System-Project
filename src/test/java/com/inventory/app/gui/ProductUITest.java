package com.inventory.app.gui;

import com.inventory.app.model.Category;
import com.inventory.app.model.Product;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(ApplicationExtension.class)
public class ProductUITest {
    private ProductUI productUI;
    private TableView<Product> productTable;
    private TextField searchField;

    @Start
    private void start(Stage stage) {
        productUI = new ProductUI(stage);
        productTable = (TableView<Product>) productUI.getContent().lookup("#productTable");
        searchField = (TextField) productUI.getContent().lookup("TextField");
    }

    @Test
    void testInitialState() {
        assertNotNull(productUI);
        assertNotNull(productTable);
        assertNotNull(searchField);
        assertTrue(productTable.getItems().isEmpty());
    }

    @Test
    void testAddProduct() {
        Category category = new Category();
        category.setId(1L);
        category.setName("Test Category");

        Product product = new Product();
        product.setId(1L);
        product.setName("Test Product");
        product.setPrice(new BigDecimal("10.00"));
        product.setQuantity(100);
        product.setCategory(category);

        productTable.getItems().add(product);

        assertEquals(1, productTable.getItems().size());
        assertEquals(product, productTable.getItems().get(0));
    }

    @Test
    void testFilterProducts() {
        Category category = new Category();
        category.setId(1L);
        category.setName("Test Category");

        Product product1 = new Product();
        product1.setId(1L);
        product1.setName("Test Product 1");
        product1.setPrice(new BigDecimal("10.00"));
        product1.setQuantity(100);
        product1.setCategory(category);

        Product product2 = new Product();
        product2.setId(2L);
        product2.setName("Different Product");
        product2.setPrice(new BigDecimal("20.00"));
        product2.setQuantity(50);
        product2.setCategory(category);

        productTable.getItems().addAll(product1, product2);

        // Test search filter
        searchField.setText("Test");
        assertEquals(1, productTable.getItems().size());
        assertEquals(product1, productTable.getItems().get(0));

        // Clear filter
        searchField.clear();
        assertEquals(2, productTable.getItems().size());
    }

    @Test
    void testTableColumns() {
        assertTrue(productTable.getColumns().stream()
            .anyMatch(col -> col.getText().equals("Name")));
        assertTrue(productTable.getColumns().stream()
            .anyMatch(col -> col.getText().equals("Category")));
        assertTrue(productTable.getColumns().stream()
            .anyMatch(col -> col.getText().equals("Price")));
        assertTrue(productTable.getColumns().stream()
            .anyMatch(col -> col.getText().equals("Quantity")));
    }
} 