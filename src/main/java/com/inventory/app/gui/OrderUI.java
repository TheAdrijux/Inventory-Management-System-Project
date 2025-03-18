package com.inventory.app.gui;

import com.inventory.app.MainApp;
import com.inventory.app.model.*;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.scene.Scene;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

public class OrderUI extends ComponentUI {
    private TableView<Order> orderTable;
    private TableView<OrderItem> orderItemsTable;
    private ComboBox<Client> clientFilter;
    private DatePicker startDate;
    private DatePicker endDate;
    private Button newOrderButton;
    private Button viewButton;
    private Button deleteButton;

    public OrderUI(Stage stage) {
        super(stage);
        initialize();
    }

    @Override
    public void initialize() {
        createTables();
        createControls();
        loadData();
    }

    @Override
    public Parent getContent() {
        BorderPane content = new BorderPane();
        
        // Top controls
        VBox topControls = new VBox(10);
        topControls.setPadding(new Insets(10));
        
        HBox filterControls = new HBox(10);
        filterControls.getChildren().addAll(
            new Label("Client:"),
            clientFilter,
            new Label("From:"),
            startDate,
            new Label("To:"),
            endDate
        );
        
        HBox actionControls = new HBox(10);
        actionControls.getChildren().addAll(
            newOrderButton,
            viewButton,
            deleteButton
        );
        
        topControls.getChildren().addAll(filterControls, actionControls);
        content.setTop(topControls);
        
        // Center split view
        SplitPane splitPane = new SplitPane();
        VBox orderSection = new VBox(10);
        orderSection.getChildren().addAll(
            new Label("Orders"),
            orderTable
        );
        
        VBox itemsSection = new VBox(10);
        itemsSection.getChildren().addAll(
            new Label("Order Items"),
            orderItemsTable
        );
        
        splitPane.getItems().addAll(orderSection, itemsSection);
        content.setCenter(splitPane);
        
        return content;
    }

    private void createTables() {
        // Orders table
        orderTable = new TableView<>();
        
        TableColumn<Order, String> orderIdCol = new TableColumn<>("Order ID");
        orderIdCol.setCellValueFactory(data -> 
            new SimpleStringProperty(String.valueOf(data.getValue().getId())));
        
        TableColumn<Order, String> clientCol = new TableColumn<>("Client");
        clientCol.setCellValueFactory(data -> 
            new SimpleStringProperty(data.getValue().getClient().getName()));
        
        TableColumn<Order, String> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(data -> 
            new SimpleStringProperty(data.getValue().getOrderDate().toString()));
        
        TableColumn<Order, String> totalCol = new TableColumn<>("Total");
        totalCol.setCellValueFactory(data -> 
            new SimpleStringProperty("$" + data.getValue().getTotalAmount().toString()));
        
        orderTable.getColumns().addAll(orderIdCol, clientCol, dateCol, totalCol);
        
        // Order items table
        orderItemsTable = new TableView<>();
        
        TableColumn<OrderItem, String> productCol = new TableColumn<>("Product");
        productCol.setCellValueFactory(data -> 
            new SimpleStringProperty(data.getValue().getProduct().getName()));
        
        TableColumn<OrderItem, Integer> quantityCol = new TableColumn<>("Quantity");
        quantityCol.setCellValueFactory(data -> 
            new SimpleIntegerProperty(data.getValue().getQuantity()).asObject());
        
        TableColumn<OrderItem, String> priceCol = new TableColumn<>("Price");
        priceCol.setCellValueFactory(data -> 
            new SimpleStringProperty("$" + data.getValue().getUnitPrice().toString()));
        
        TableColumn<OrderItem, String> subtotalCol = new TableColumn<>("Subtotal");
        subtotalCol.setCellValueFactory(data -> 
            new SimpleStringProperty("$" + data.getValue().getSubtotal().toString()));
        
        orderItemsTable.getColumns().addAll(productCol, quantityCol, priceCol, subtotalCol);
        
        // Link tables
        orderTable.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldSelection, newSelection) -> {
                if (newSelection != null) {
                    orderItemsTable.setItems(
                        FXCollections.observableArrayList(newSelection.getOrderItems())
                    );
                } else {
                    orderItemsTable.getItems().clear();
                }
            }
        );
    }

    private void createControls() {
        clientFilter = new ComboBox<>();
        clientFilter.setPromptText("All Clients");
        clientFilter.setOnAction(e -> filterOrders());
        
        startDate = new DatePicker();
        startDate.setOnAction(e -> filterOrders());
        
        endDate = new DatePicker();
        endDate.setOnAction(e -> filterOrders());
        
        newOrderButton = new Button("New Order");
        newOrderButton.setOnAction(e -> showOrderDialog(null));
        
        viewButton = new Button("View");
        viewButton.setOnAction(e -> {
            Order selected = orderTable.getSelectionModel().getSelectedItem();
            if (selected != null) {
                showOrderDialog(selected);
            }
        });
        
        deleteButton = new Button("Delete");
        deleteButton.setOnAction(e -> {
            Order selected = orderTable.getSelectionModel().getSelectedItem();
            if (selected != null && showConfirmation("Delete Order", 
                "Are you sure you want to delete Order #" + selected.getId() + "?")) {
                deleteOrder(selected);
            }
        });
    }

    private void showOrderDialog(Order order) {
        Dialog<Order> dialog = new Dialog<>();
        dialog.setTitle(order == null ? "New Order" : "View Order");
        
        BorderPane content = new BorderPane();
        content.setPadding(new Insets(20));
        
        // Client selection
        ComboBox<Client> clientBox = new ComboBox<>();
        
        // Load clients
        try {
            var em = MainApp.getEntityManagerFactory().createEntityManager();
            var clients = em.createQuery("SELECT c FROM Client c", Client.class).getResultList();
            clientBox.setItems(FXCollections.observableArrayList(clients));
            em.close();
        } catch (Exception e) {
            showError("Error", "Failed to load clients: " + e.getMessage());
            e.printStackTrace();
        }
        
        if (order != null) {
            clientBox.setValue(order.getClient());
            clientBox.setDisable(true);
        }
        
        // Products table
        TableView<OrderItem> itemsTable = new TableView<>();
        
        // Create columns for the items table
        TableColumn<OrderItem, String> productCol = new TableColumn<>("Product");
        productCol.setCellValueFactory(data -> 
            new SimpleStringProperty(data.getValue().getProduct().getName()));
        
        TableColumn<OrderItem, Integer> quantityCol = new TableColumn<>("Quantity");
        quantityCol.setCellValueFactory(data -> 
            new SimpleIntegerProperty(data.getValue().getQuantity()).asObject());
        
        TableColumn<OrderItem, String> priceCol = new TableColumn<>("Price");
        priceCol.setCellValueFactory(data -> 
            new SimpleStringProperty("$" + data.getValue().getUnitPrice().toString()));
        
        TableColumn<OrderItem, String> subtotalCol = new TableColumn<>("Subtotal");
        subtotalCol.setCellValueFactory(data -> 
            new SimpleStringProperty("$" + data.getValue().getSubtotal().toString()));
        
        itemsTable.getColumns().addAll(productCol, quantityCol, priceCol, subtotalCol);
        
        // Total label
        Label totalLabel = new Label();
        totalLabel.setStyle("-fx-font-weight: bold;");
        
        if (order == null) {
            // Add product button
            Button addProductButton = new Button("Add Product");
            addProductButton.setOnAction(e -> showAddProductDialog(itemsTable, totalLabel));
            
            // Edit and Delete buttons for items
            Button editItemButton = new Button("Edit Item");
            editItemButton.setOnAction(e -> {
                OrderItem selected = itemsTable.getSelectionModel().getSelectedItem();
                if (selected != null) {
                    showEditProductDialog(itemsTable, selected, totalLabel);
                }
            });
            
            Button deleteItemButton = new Button("Delete Item");
            deleteItemButton.setOnAction(e -> {
                OrderItem selected = itemsTable.getSelectionModel().getSelectedItem();
                if (selected != null && showConfirmation("Delete Item", 
                    "Are you sure you want to remove this item from the order?")) {
                    itemsTable.getItems().remove(selected);
                    updateTotal(itemsTable, totalLabel);
                }
            });
            
            HBox itemControls = new HBox(10);
            itemControls.getChildren().addAll(addProductButton, editItemButton, deleteItemButton);
            
            VBox topSection = new VBox(10);
            topSection.getChildren().addAll(
                new HBox(10, new Label("Client:"), clientBox),
                itemControls
            );
            content.setTop(topSection);
            
            // Update total when items change
            itemsTable.getItems().addListener((javafx.collections.ListChangeListener.Change<?> c) -> 
                updateTotal(itemsTable, totalLabel));
        } else {
            content.setTop(new HBox(10, new Label("Client: " + order.getClient().getName())));
            itemsTable.setItems(FXCollections.observableArrayList(order.getOrderItems()));
            updateTotal(itemsTable, totalLabel);
        }
        
        // Add total label at the bottom
        VBox bottomSection = new VBox(10);
        bottomSection.getChildren().add(totalLabel);
        bottomSection.setStyle("-fx-padding: 10;");
        
        VBox centerSection = new VBox(10);
        centerSection.getChildren().addAll(itemsTable, bottomSection);
        content.setCenter(centerSection);
        
        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().addAll(
            order == null ? ButtonType.OK : ButtonType.CLOSE,
            ButtonType.CANCEL
        );
        
        if (order == null) {
            dialog.setResultConverter(buttonType -> {
                if (buttonType == ButtonType.OK) {
                    if (clientBox.getValue() == null) {
                        showError("Invalid Input", "Please select a client.");
                        return null;
                    }
                    if (itemsTable.getItems().isEmpty()) {
                        showError("Invalid Input", "Please add at least one product.");
                        return null;
                    }
                    
                    Order newOrder = new Order();
                    newOrder.setClient(clientBox.getValue());
                    newOrder.setOrderDate(LocalDateTime.now());
                    newOrder.setOrderItems(new ArrayList<>(itemsTable.getItems()));
                    for (OrderItem item : newOrder.getOrderItems()) {
                        item.setOrder(newOrder);
                    }
                    return newOrder;
                }
                return null;
            });
            
            dialog.showAndWait().ifPresent(this::saveOrder);
        } else {
            dialog.showAndWait();
        }
    }

    private void showAddProductDialog(TableView<OrderItem> itemsTable, Label totalLabel) {
        Dialog<OrderItem> dialog = new Dialog<>();
        dialog.setTitle("Add Product");
        
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));
        
        ComboBox<Product> productBox = new ComboBox<>();
        TextField quantityField = new TextField();
        Label priceLabel = new Label();
        
        // Load products
        try {
            var em = MainApp.getEntityManagerFactory().createEntityManager();
            var products = em.createQuery("SELECT p FROM Product p", Product.class).getResultList();
            productBox.setItems(FXCollections.observableArrayList(products));
            em.close();
        } catch (Exception e) {
            showError("Error", "Failed to load products: " + e.getMessage());
            e.printStackTrace();
        }

        // Update price label when product is selected
        productBox.setOnAction(e -> {
            Product selectedProduct = productBox.getValue();
            if (selectedProduct != null) {
                priceLabel.setText("Price: $" + selectedProduct.getPrice());
            } else {
                priceLabel.setText("");
            }
        });
        
        grid.add(new Label("Product:"), 0, 0);
        grid.add(productBox, 1, 0);
        grid.add(priceLabel, 1, 1);
        grid.add(new Label("Quantity:"), 0, 2);
        grid.add(quantityField, 1, 2);
        
        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        
        dialog.setResultConverter(buttonType -> {
            if (buttonType == ButtonType.OK) {
                try {
                    Product product = productBox.getValue();
                    int quantity = Integer.parseInt(quantityField.getText());
                    if (product != null && quantity > 0) {
                        if (!product.hasEnoughStock(quantity)) {
                            showError("Invalid Quantity", "Not enough stock available. Current stock: " + product.getQuantity());
                            return null;
                        }
                        OrderItem item = new OrderItem();
                        item.setProduct(product);
                        item.setQuantity(quantity);
                        BigDecimal price = product.getPrice();
                        item.setPricePerUnit(price);
                        item.setUnitPrice(price);
                        return item;
                    } else {
                        showError("Invalid Input", "Please select a product and enter a valid quantity.");
                    }
                } catch (NumberFormatException e) {
                    showError("Invalid Input", "Please enter a valid quantity.");
                }
            }
            return null;
        });
        
        dialog.showAndWait().ifPresent(item -> {
            itemsTable.getItems().add(item);
            updateTotal(itemsTable, totalLabel);
        });
    }

    private void showEditProductDialog(TableView<OrderItem> itemsTable, OrderItem item, Label totalLabel) {
        Dialog<OrderItem> dialog = new Dialog<>();
        dialog.setTitle("Edit Item");
        
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));
        
        Label productLabel = new Label(item.getProduct().getName());
        TextField quantityField = new TextField(String.valueOf(item.getQuantity()));
        Label priceLabel = new Label("Price: $" + item.getUnitPrice());
        
        grid.add(new Label("Product:"), 0, 0);
        grid.add(productLabel, 1, 0);
        grid.add(new Label("Price:"), 0, 1);
        grid.add(priceLabel, 1, 1);
        grid.add(new Label("Quantity:"), 0, 2);
        grid.add(quantityField, 1, 2);
        
        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        
        dialog.setResultConverter(buttonType -> {
            if (buttonType == ButtonType.OK) {
                try {
                    int quantity = Integer.parseInt(quantityField.getText());
                    if (quantity > 0) {
                        if (!item.getProduct().hasEnoughStock(quantity)) {
                            showError("Invalid Quantity", 
                                "Not enough stock available. Current stock: " + 
                                item.getProduct().getQuantity());
                            return null;
                        }
                        item.setQuantity(quantity);
                        return item;
                    } else {
                        showError("Invalid Input", "Please enter a valid quantity.");
                    }
                } catch (NumberFormatException e) {
                    showError("Invalid Input", "Please enter a valid quantity.");
                }
            }
            return null;
        });
        
        dialog.showAndWait().ifPresent(updatedItem -> {
            itemsTable.refresh();
            updateTotal(itemsTable, totalLabel);
        });
    }

    private Dialog<?> findParentDialog(DialogPane currentPane) {
        Window window = currentPane.getScene().getWindow();
        if (window instanceof Dialog) {
            Window owner = ((Dialog<?>) window).getOwner();
            if (owner instanceof Stage) {
                Scene ownerScene = ((Stage) owner).getScene();
                if (ownerScene != null && ownerScene.getRoot() instanceof DialogPane) {
                    Window ownerWindow = ownerScene.getWindow();
                    if (ownerWindow instanceof Dialog) {
                        return (Dialog<?>) ownerWindow;
                    }
                }
            }
        }
        return null;
    }

    private Label findTotalLabel(DialogPane dialogPane) {
        for (Node node : dialogPane.getContent().lookupAll(".label")) {
            if (node instanceof Label && ((Label) node).getText().startsWith("Total: ")) {
                return (Label) node;
            }
        }
        return null;
    }

    private void updateTotal(TableView<OrderItem> itemsTable, Label totalLabel) {
        BigDecimal total = BigDecimal.ZERO;
        for (OrderItem item : itemsTable.getItems()) {
            total = total.add(item.getSubtotal());
        }
        if (totalLabel != null) {
            totalLabel.setText("Total: $" + total);
        }
    }

    private void loadData() {
        try {
            var em = MainApp.getEntityManagerFactory().createEntityManager();
            
            // Load orders with clients and items
            var query = em.createQuery(
                "SELECT DISTINCT o FROM Order o " +
                "LEFT JOIN FETCH o.client " +
                "LEFT JOIN FETCH o.orderItems i " +
                "LEFT JOIN FETCH i.product", 
                Order.class
            );
            var orders = query.getResultList();
            orderTable.setItems(FXCollections.observableArrayList(orders));
            
            // Load clients for filter
            loadClients();
            
            em.close();
        } catch (Exception e) {
            showError("Data Loading Error", "Failed to load orders: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadClients() {
        try {
            var em = MainApp.getEntityManagerFactory().createEntityManager();
            var query = em.createQuery("SELECT c FROM Client c", Client.class);
            var clients = query.getResultList();
            
            clientFilter.getItems().clear();
            clientFilter.getItems().add(null); // "All Clients" option
            clientFilter.getItems().addAll(clients);
            clientFilter.setValue(null); // Set "All Clients" as default
            
            em.close();
        } catch (Exception e) {
            showError("Client Loading Error", "Failed to load clients: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void filterOrders() {
        try {
            var em = MainApp.getEntityManagerFactory().createEntityManager();
            var cb = em.getCriteriaBuilder();
            var cq = cb.createQuery(Order.class);
            var root = cq.from(Order.class);
            root.fetch("client", JoinType.LEFT);
            root.fetch("orderItems", JoinType.LEFT);
            
            var predicates = new ArrayList<Predicate>();
            
            // Client filter
            if (clientFilter.getValue() != null) {
                predicates.add(cb.equal(root.get("client"), clientFilter.getValue()));
            }
            
            // Date range filter
            if (startDate.getValue() != null) {
                predicates.add(cb.greaterThanOrEqualTo(
                    root.get("orderDate"), 
                    startDate.getValue().atStartOfDay()
                ));
            }
            if (endDate.getValue() != null) {
                predicates.add(cb.lessThanOrEqualTo(
                    root.get("orderDate"), 
                    endDate.getValue().plusDays(1).atStartOfDay()
                ));
            }
            
            if (!predicates.isEmpty()) {
                cq.where(predicates.toArray(new Predicate[0]));
            }
            
            var query = em.createQuery(cq);
            var orders = query.getResultList();
            orderTable.setItems(FXCollections.observableArrayList(orders));
            
            em.close();
        } catch (Exception e) {
            showError("Filter Error", "Failed to filter orders: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void saveOrder(Order order) {
        try {
            var em = MainApp.getEntityManagerFactory().createEntityManager();
            em.getTransaction().begin();
            
            // Calculate and set the total amount
            BigDecimal total = BigDecimal.ZERO;
            for (OrderItem item : order.getOrderItems()) {
                total = total.add(item.getSubtotal());
            }
            order.setTotalAmount(total);
            
            if (order.getId() == null) {
                em.persist(order);
            } else {
                em.merge(order);
            }
            
            em.getTransaction().commit();
            em.close();
            loadData();
        } catch (Exception e) {
            showError("Save Error", "Failed to save order: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void deleteOrder(Order order) {
        try {
            var em = MainApp.getEntityManagerFactory().createEntityManager();
            em.getTransaction().begin();
            
            // Refresh the entity to ensure we have the latest version
            order = em.find(Order.class, order.getId());
            if (order != null) {
                // Remove all order items first
                for (OrderItem item : order.getOrderItems()) {
                    em.remove(item);
                }
                em.remove(order);
            }
            
            em.getTransaction().commit();
            em.close();
            loadData();
        } catch (Exception e) {
            showError("Delete Error", "Failed to delete order: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void show() {
        // Not used in component
    }
} 