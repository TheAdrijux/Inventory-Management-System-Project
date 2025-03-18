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
public class CategoryUITest {
    private CategoryUI categoryUI;
    private TableView<Category> categoryTable;
    private TextField searchField;

    @Start
    private void start(Stage stage) {
        categoryUI = new CategoryUI(stage);
        categoryTable = (TableView<Category>) categoryUI.getContent().lookup("#categoryTable");
        searchField = (TextField) categoryUI.getContent().lookup("TextField");
    }

    @Test
    void testInitialState() {
        assertNotNull(categoryUI);
        assertNotNull(categoryTable);
        assertNotNull(searchField);
        assertTrue(categoryTable.getItems().isEmpty());
    }

    @Test
    void testAddCategory() {
        Category category = new Category();
        category.setId(1L);
        category.setName("Test Category");

        categoryTable.getItems().add(category);

        assertEquals(1, categoryTable.getItems().size());
        assertEquals(category, categoryTable.getItems().get(0));
    }

    @Test
    void testFilterCategories() {
        Category category1 = new Category();
        category1.setId(1L);
        category1.setName("Test Category");

        Category category2 = new Category();
        category2.setId(2L);
        category2.setName("Different Category");

        categoryTable.getItems().addAll(category1, category2);

        // Test search filter
        searchField.setText("Test");
        assertEquals(1, categoryTable.getItems().size());
        assertEquals(category1, categoryTable.getItems().get(0));

        // Clear filter
        searchField.clear();
        assertEquals(2, categoryTable.getItems().size());
    }

    @Test
    void testProductCount() {
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

        category.getProducts().add(product1);
        category.getProducts().add(product2);
        categoryTable.getItems().add(category);

        assertEquals(1, categoryTable.getItems().size());
        assertEquals(2, category.getProducts().size());
    }

    @Test
    void testTableColumns() {
        assertTrue(categoryTable.getColumns().stream()
            .anyMatch(col -> col.getText().equals("Name")));
        assertTrue(categoryTable.getColumns().stream()
            .anyMatch(col -> col.getText().equals("Products")));
    }
} 