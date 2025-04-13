package com.inventory.app.gui;

import com.inventory.app.MainApp;
import com.inventory.app.model.Product;
import com.inventory.app.model.Category;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.math.BigDecimal;
import java.util.ArrayList;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import java.util.Objects;

public class ProductUI extends ComponentUI {
    private TableView<Product> productTable;
    private ComboBox<Category> categoryFilter;
    private TextField searchField;
    private Button addButton;
    private Button editButton;
    private Button deleteButton;

    public ProductUI(Stage stage) {
        super(stage);
        initialize();
    }

    @Override
    public void initialize() {
        // Initialize components
        createTable();
        createControls();
        loadData();
    }

    @Override
    public Parent getContent() {
        BorderPane content = new BorderPane();
        
        // Top controls
        HBox controls = new HBox(10);
        controls.setPadding(new Insets(10));
        controls.getChildren().addAll(
            new Label("Category:"),
            categoryFilter,
            new Label("Search:"),
            searchField,
            addButton,
            editButton,
            deleteButton
        );
        content.setTop(controls);
        
        // Center table
        content.setCenter(productTable);
        
        return content;
    }

    private void createTable() {
        productTable = new TableView<>();
        
        // Create columns
        TableColumn<Product, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getName()));
        
        TableColumn<Product, String> priceCol = new TableColumn<>("Price");
        priceCol.setCellValueFactory(data -> 
            new SimpleStringProperty("$" + data.getValue().getPrice().toString()));
        
        TableColumn<Product, Integer> quantityCol = new TableColumn<>("Quantity");
        quantityCol.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getQuantity()));
        
        TableColumn<Product, String> categoryCol = new TableColumn<>("Category");
        categoryCol.setCellValueFactory(data -> new SimpleStringProperty(
            data.getValue().getCategory() != null ? data.getValue().getCategory().getName() : ""
        ));
        
        productTable.getColumns().addAll(nameCol, priceCol, quantityCol, categoryCol);
    }

    private void createControls() {
        categoryFilter = new ComboBox<>();
        categoryFilter.setPromptText("All Categories");
        categoryFilter.setOnAction(e -> filterProducts());
        
        searchField = new TextField();
        searchField.setPromptText("Search products...");
        searchField.textProperty().addListener((obs, oldVal, newVal) -> filterProducts());
        
        addButton = new Button("Add");
        addButton.setOnAction(e -> showProductDialog(null));
        
        editButton = new Button("Edit");
        editButton.setOnAction(e -> {
            Product selected = productTable.getSelectionModel().getSelectedItem();
            if (selected != null) {
                showProductDialog(selected);
            }
        });
        
        deleteButton = new Button("Delete");
        deleteButton.setOnAction(e -> {
            Product selected = productTable.getSelectionModel().getSelectedItem();
            if (selected != null && showConfirmation("Delete Product", 
                "Are you sure you want to delete " + selected.getName() + "?")) {
                deleteProduct(selected);
            }
        });
    }

    private void showProductDialog(Product product) {
        Dialog<Product> dialog = new Dialog<>();
        dialog.setTitle(product == null ? "Add Product" : "Edit Product");
        
        // Create the form
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));
        
        TextField nameField = new TextField();
        TextField priceField = new TextField();
        TextField quantityField = new TextField();
        ComboBox<Category> categoryBox = new ComboBox<>();
        
        // Load categories
        try {
            var em = MainApp.getEntityManagerFactory().createEntityManager();
            var categories = em.createQuery("SELECT c FROM Category c", Category.class).getResultList();
            categoryBox.setItems(FXCollections.observableArrayList(categories));
            em.close();
        } catch (Exception e) {
            showError("Error", "Failed to load categories: " + e.getMessage());
            e.printStackTrace();
        }
        
        if (product != null) {
            nameField.setText(product.getName());
            priceField.setText(product.getPrice().toString());
            quantityField.setText(product.getQuantity().toString());
            categoryBox.setValue(product.getCategory());
        }
        
        grid.add(new Label("Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Price:"), 0, 1);
        grid.add(priceField, 1, 1);
        grid.add(new Label("Quantity:"), 0, 2);
        grid.add(quantityField, 1, 2);
        grid.add(new Label("Category:"), 0, 3);
        grid.add(categoryBox, 1, 3);
        
        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        
        dialog.setResultConverter(buttonType -> {
            if (buttonType == ButtonType.OK) {
                try {
                    Product result = product != null ? product : new Product();
                    result.setName(nameField.getText());
                    result.setPrice(new BigDecimal(priceField.getText()));
                    result.setQuantity(Integer.parseInt(quantityField.getText()));
                    result.setCategory(categoryBox.getValue());
                    return result;
                } catch (NumberFormatException e) {
                    showError("Invalid Input", "Please enter valid numbers for price and quantity.");
                    return null;
                }
            }
            return null;
        });
        
        dialog.showAndWait().ifPresent(this::saveProduct);
    }

    public void loadData() {
        try {
            var em = MainApp.getEntityManagerFactory().createEntityManager();
            
            // Load categories for filter
            var categories = em.createQuery("SELECT c FROM Category c", Category.class).getResultList();
            categoryFilter.getItems().clear();
            categoryFilter.getItems().add(null); // This will show as "All Categories" due to prompt text
            categoryFilter.getItems().addAll(categories);
            categoryFilter.setValue(null); // Set "All Categories" as default
            
            // Load products
            var query = em.createQuery("SELECT p FROM Product p LEFT JOIN FETCH p.category", Product.class);
            var products = query.getResultList();
            
            productTable.setItems(FXCollections.observableArrayList(products));
            
            em.close();
        } catch (Exception e) {
            showError("Data Loading Error", "Failed to load products: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void filterProducts() {
        try {
            var em = MainApp.getEntityManagerFactory().createEntityManager();
            var cb = em.getCriteriaBuilder();
            var cq = cb.createQuery(Product.class);
            var root = cq.from(Product.class);
            var category = root.join("category", JoinType.LEFT);
            var orderItems = root.join("orderItems", JoinType.LEFT);
            var order = orderItems.join("order", JoinType.LEFT);

            var predicates = new ArrayList<Predicate>();

            // Filter by category
            if (categoryFilter.getValue() != null) {
                predicates.add(cb.equal(category, categoryFilter.getValue()));
            }

            // Filter by search text in name or description
            if (searchField.getText() != null && !searchField.getText().trim().isEmpty()) {
                String search = "%" + searchField.getText().toLowerCase() + "%";
                predicates.add(cb.or(
                    cb.like(cb.lower(root.get("name")), search),
                    cb.like(cb.lower(root.get("description")), search)
                ));
            }

            // Filter by stock status
            predicates.add(cb.greaterThan(root.get("quantity"), 0));

            // Filter by price range
            predicates.add(cb.between(
                root.get("price"),
                new BigDecimal("0.01"),
                new BigDecimal("9999.99")
            ));

            // Apply all predicates
            if (!predicates.isEmpty()) {
                cq.where(predicates.toArray(new Predicate[0]));
            }

            // Group by product and get sales statistics
            cq.groupBy(root.get("id"), root.get("name"), root.get("price"), 
                      root.get("quantity"), category);
            
            // Having clause for products with orders
            cq.having(cb.greaterThan(cb.count(orderItems), 0L));

            // Order by name
            cq.orderBy(cb.asc(root.get("name")));

            var query = em.createQuery(cq);
            var products = query.getResultList();
            productTable.setItems(FXCollections.observableArrayList(products));
            em.close();
        } catch (Exception e) {
            showError("Filter Error", "Failed to filter products: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void saveProduct(Product product) {
        try {
            var em = MainApp.getEntityManagerFactory().createEntityManager();
            em.getTransaction().begin();
            
            // If this is an existing product, check if category changed
            boolean categoryChanged = false;
            if (product.getId() != null) {
                Product existingProduct = em.find(Product.class, product.getId());
                categoryChanged = existingProduct != null && 
                    !Objects.equals(existingProduct.getCategory(), product.getCategory());
            }
            
            if (product.getId() == null) {
                em.persist(product);
            } else {
                em.merge(product);
            }
            
            em.getTransaction().commit();
            em.close();
            
            loadData();
            
            // If category changed or this is a new product, refresh CategoryUI
            if (categoryChanged || product.getId() == null) {
                refreshCategoryUI();
            }
        } catch (Exception e) {
            showError("Save Error", "Failed to save product: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void deleteProduct(Product product) {
        try {
            var em = MainApp.getEntityManagerFactory().createEntityManager();
            
            // Check if product has any orders
            var query = em.createQuery(
                "SELECT COUNT(oi) FROM OrderItem oi WHERE oi.product = :product", 
                Long.class
            );
            query.setParameter("product", product);
            long orderCount = query.getSingleResult();
            
            if (orderCount > 0) {
                showError("Cannot Delete", 
                    "This product is referenced in orders. Please delete the orders first.");
                em.close();
                return;
            }
            
            em.getTransaction().begin();
            
            // Refresh the entity to ensure we have the latest version
            product = em.find(Product.class, product.getId());
            if (product != null) {
                // Remove the product from its category
                if (product.getCategory() != null) {
                    product.getCategory().getProducts().remove(product);
                    product.setCategory(null);
                }
                em.remove(product);
            }
            
            em.getTransaction().commit();
            em.close();
            
            // Refresh both product and category lists
            loadData();
            refreshCategoryUI();
        } catch (Exception e) {
            showError("Delete Error", "Failed to delete product: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void refreshCategoryUI() {
        // Find and refresh the CategoryUI instance
        var categoryTab = MainApp.getTabPane().getTabs().stream()
            .filter(tab -> tab.getText().equals("Categories"))
            .findFirst()
            .orElse(null);
            
        if (categoryTab != null && categoryTab.getContent() instanceof Parent) {
            Parent content = (Parent) categoryTab.getContent();
            if (content.getUserData() instanceof CategoryUI) {
                ((CategoryUI) content.getUserData()).loadData();
            }
        }
    }

    @Override
    public void show() {
        // Not used in component
    }
} 