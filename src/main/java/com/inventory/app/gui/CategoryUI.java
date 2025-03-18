package com.inventory.app.gui;

import com.inventory.app.model.Category;
import com.inventory.app.model.Product;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import com.inventory.app.MainApp;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.JoinType;

public class CategoryUI extends ComponentUI {
    private TableView<Category> categoryTable;
    private TextField searchField;
    private Button addButton;
    private Button editButton;
    private Button deleteButton;

    public CategoryUI(Stage stage) {
        super(stage);
        initialize();
    }

    @Override
    public void initialize() {
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
            new Label("Search:"),
            searchField,
            addButton,
            editButton,
            deleteButton
        );
        content.setTop(controls);
        
        // Center table
        content.setCenter(categoryTable);
        
        // Store the CategoryUI instance in the content's user data
        content.setUserData(this);
        
        return content;
    }

    private void createTable() {
        categoryTable = new TableView<>();
        
        // Create columns
        TableColumn<Category, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getName()));
        
        TableColumn<Category, Integer> productCountCol = new TableColumn<>("Products");
        productCountCol.setCellValueFactory(data -> 
            new SimpleIntegerProperty(data.getValue().getProducts().size()).asObject());
        
        categoryTable.getColumns().addAll(nameCol, productCountCol);
    }

    private void createControls() {
        searchField = new TextField();
        searchField.setPromptText("Search categories...");
        searchField.textProperty().addListener((obs, oldVal, newVal) -> filterCategories());
        
        addButton = new Button("Add");
        addButton.setOnAction(e -> showCategoryDialog(null));
        
        editButton = new Button("Edit");
        editButton.setOnAction(e -> {
            Category selected = categoryTable.getSelectionModel().getSelectedItem();
            if (selected != null) {
                showCategoryDialog(selected);
            }
        });
        
        deleteButton = new Button("Delete");
        deleteButton.setOnAction(e -> {
            Category selected = categoryTable.getSelectionModel().getSelectedItem();
            if (selected != null) {
                if (selected.getProducts().isEmpty()) {
                    if (showConfirmation("Delete Category", 
                        "Are you sure you want to delete " + selected.getName() + "?")) {
                        deleteCategory(selected);
                    }
                } else {
                    showError("Cannot Delete", 
                        "Category contains products. Remove or reassign products first.");
                }
            }
        });
    }

    private void showCategoryDialog(Category category) {
        Dialog<Category> dialog = new Dialog<>();
        dialog.setTitle(category == null ? "Add Category" : "Edit Category");
        
        // Create the form
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));
        
        TextField nameField = new TextField();
        if (category != null) {
            nameField.setText(category.getName());
        }
        
        grid.add(new Label("Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        
        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        
        dialog.setResultConverter(buttonType -> {
            if (buttonType == ButtonType.OK) {
                Category result = category != null ? category : new Category();
                result.setName(nameField.getText());
                return result;
            }
            return null;
        });
        
        dialog.showAndWait().ifPresent(this::saveCategory);
    }

    public void loadData() {
        try {
            var em = MainApp.getEntityManagerFactory().createEntityManager();
            var query = em.createQuery("SELECT c FROM Category c LEFT JOIN FETCH c.products", Category.class);
            var categories = query.getResultList();
            categoryTable.setItems(FXCollections.observableArrayList(categories));
            em.close();
        } catch (Exception e) {
            showError("Data Loading Error", "Failed to load categories: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void initializeDefaultCategories(EntityManager em) {
        try {
            em.getTransaction().begin();
            
            String[] defaultCategories = {
                "Electronics",
                "Furniture",
                "Office Supplies"
            };

            for (String categoryName : defaultCategories) {
                Category category = new Category();
                category.setName(categoryName);
                em.persist(category);
            }

            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            showError("Initialization Error", "Failed to initialize default categories: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void filterCategories() {
        try {
            var em = MainApp.getEntityManagerFactory().createEntityManager();
            var cb = em.getCriteriaBuilder();
            var cq = cb.createQuery(Category.class);
            var root = cq.from(Category.class);
            root.fetch("products", JoinType.LEFT);

            if (searchField.getText() != null && !searchField.getText().trim().isEmpty()) {
                cq.where(cb.like(cb.lower(root.get("name")), 
                    "%" + searchField.getText().toLowerCase() + "%"));
            }

            var query = em.createQuery(cq);
            var categories = query.getResultList();
            categoryTable.setItems(FXCollections.observableArrayList(categories));
            em.close();
        } catch (Exception e) {
            showError("Filter Error", "Failed to filter categories: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void saveCategory(Category category) {
        try {
            var em = MainApp.getEntityManagerFactory().createEntityManager();
            em.getTransaction().begin();

            if (category.getId() == null) {
                em.persist(category);
            } else {
                em.merge(category);
            }

            em.getTransaction().commit();
            em.close();
            loadData();
            
            // Refresh ProductUI to show updated category name
            refreshProductUI();
        } catch (Exception e) {
            showError("Save Error", "Failed to save category: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void refreshProductUI() {
        // Find and refresh the ProductUI instance
        var productTab = MainApp.getTabPane().getTabs().stream()
            .filter(tab -> tab.getText().equals("Products"))
            .findFirst()
            .orElse(null);
            
        if (productTab != null && productTab.getContent() instanceof Parent) {
            Parent content = (Parent) productTab.getContent();
            if (content.getUserData() instanceof ProductUI) {
                ((ProductUI) content.getUserData()).loadData();
            }
        }
    }

    private void deleteCategory(Category category) {
        try {
            var em = MainApp.getEntityManagerFactory().createEntityManager();
            em.getTransaction().begin();

            category = em.merge(category);
            em.remove(category);

            em.getTransaction().commit();
            em.close();
            loadData();
        } catch (Exception e) {
            showError("Delete Error", "Failed to delete category: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void show() {
        // Not used in component
    }
} 