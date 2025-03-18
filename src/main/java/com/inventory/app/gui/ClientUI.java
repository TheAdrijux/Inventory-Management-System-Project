package com.inventory.app.gui;

import com.inventory.app.MainApp;
import com.inventory.app.model.Client;
import com.inventory.app.model.Order;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Root;

public class ClientUI extends ComponentUI {
    private TableView<Client> clientTable;
    private TableView<Order> orderHistoryTable;
    private TextField searchField;
    private Button addButton;
    private Button editButton;
    private Button deleteButton;

    public ClientUI(Stage stage) {
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
        
        // Center split view
        SplitPane splitPane = new SplitPane();
        VBox clientSection = new VBox(10);
        clientSection.getChildren().addAll(
            new Label("Clients"),
            clientTable
        );
        
        VBox orderSection = new VBox(10);
        orderSection.getChildren().addAll(
            new Label("Order History"),
            orderHistoryTable
        );
        
        splitPane.getItems().addAll(clientSection, orderSection);
        content.setCenter(splitPane);
        
        return content;
    }

    private void createTables() {
        // Client table
        clientTable = new TableView<>();
        
        TableColumn<Client, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getName()));
        
        TableColumn<Client, String> emailCol = new TableColumn<>("Email");
        emailCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getEmail()));
        
        TableColumn<Client, String> phoneCol = new TableColumn<>("Phone");
        phoneCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getPhone()));
        
        TableColumn<Client, Integer> orderCountCol = new TableColumn<>("Orders");
        orderCountCol.setCellValueFactory(data -> 
            new SimpleIntegerProperty(data.getValue().getOrders().size()).asObject());
        
        clientTable.getColumns().addAll(nameCol, emailCol, phoneCol, orderCountCol);
        
        // Order history table
        orderHistoryTable = new TableView<>();
        
        TableColumn<Order, String> orderIdCol = new TableColumn<>("Order ID");
        orderIdCol.setCellValueFactory(data -> 
            new SimpleStringProperty(String.valueOf(data.getValue().getId())));
        
        TableColumn<Order, String> orderDateCol = new TableColumn<>("Date");
        orderDateCol.setCellValueFactory(data -> 
            new SimpleStringProperty(data.getValue().getOrderDate().toString()));
        
        TableColumn<Order, String> totalCol = new TableColumn<>("Total");
        totalCol.setCellValueFactory(data -> 
            new SimpleStringProperty(data.getValue().getTotalAmount().toString()));
        
        orderHistoryTable.getColumns().addAll(orderIdCol, orderDateCol, totalCol);
        
        // Link tables
        clientTable.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldSelection, newSelection) -> {
                if (newSelection != null) {
                    orderHistoryTable.setItems(
                        FXCollections.observableArrayList(newSelection.getOrders())
                    );
                } else {
                    orderHistoryTable.getItems().clear();
                }
            }
        );
    }

    private void createControls() {
        searchField = new TextField();
        searchField.setPromptText("Search clients...");
        searchField.textProperty().addListener((obs, oldVal, newVal) -> filterClients());
        
        addButton = new Button("Add");
        addButton.setOnAction(e -> showClientDialog(null));
        
        editButton = new Button("Edit");
        editButton.setOnAction(e -> {
            Client selected = clientTable.getSelectionModel().getSelectedItem();
            if (selected != null) {
                showClientDialog(selected);
            }
        });
        
        deleteButton = new Button("Delete");
        deleteButton.setOnAction(e -> {
            Client selected = clientTable.getSelectionModel().getSelectedItem();
            if (selected != null) {
                if (selected.getOrders().isEmpty()) {
                    if (showConfirmation("Delete Client", 
                        "Are you sure you want to delete " + selected.getName() + "?")) {
                        deleteClient(selected);
                    }
                } else {
                    showError("Cannot Delete", 
                        "Client has orders. Cannot delete clients with order history.");
                }
            }
        });
    }

    private void showClientDialog(Client client) {
        Dialog<Client> dialog = new Dialog<>();
        dialog.setTitle(client == null ? "Add Client" : "Edit Client");
        
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));
        
        TextField nameField = new TextField();
        TextField emailField = new TextField();
        TextField phoneField = new TextField();
        
        if (client != null) {
            nameField.setText(client.getName());
            emailField.setText(client.getEmail());
            phoneField.setText(client.getPhone());
        }
        
        grid.add(new Label("Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Email:"), 0, 1);
        grid.add(emailField, 1, 1);
        grid.add(new Label("Phone:"), 0, 2);
        grid.add(phoneField, 1, 2);
        
        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        
        dialog.setResultConverter(buttonType -> {
            if (buttonType == ButtonType.OK) {
                Client result = client != null ? client : new Client();
                result.setName(nameField.getText());
                result.setEmail(emailField.getText());
                result.setPhone(phoneField.getText());
                return result;
            }
            return null;
        });
        
        dialog.showAndWait().ifPresent(this::saveClient);
    }

    private void loadData() {
        try {
            var em = MainApp.getEntityManagerFactory().createEntityManager();
            var query = em.createQuery("SELECT c FROM Client c LEFT JOIN FETCH c.orders", Client.class);
            var clients = query.getResultList();
            clientTable.setItems(FXCollections.observableArrayList(clients));
            em.close();
        } catch (Exception e) {
            showError("Data Loading Error", "Failed to load clients: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void filterClients() {
        try {
            var em = MainApp.getEntityManagerFactory().createEntityManager();
            var cb = em.getCriteriaBuilder();
            var cq = cb.createQuery(Client.class);
            var root = cq.from(Client.class);
            root.fetch("orders", JoinType.LEFT);

            if (searchField.getText() != null && !searchField.getText().trim().isEmpty()) {
                cq.where(cb.or(
                    cb.like(cb.lower(root.get("name")), "%" + searchField.getText().toLowerCase() + "%"),
                    cb.like(cb.lower(root.get("email")), "%" + searchField.getText().toLowerCase() + "%"),
                    cb.like(cb.lower(root.get("phone")), "%" + searchField.getText().toLowerCase() + "%")
                ));
            }

            var query = em.createQuery(cq);
            var clients = query.getResultList();
            clientTable.setItems(FXCollections.observableArrayList(clients));
            em.close();
        } catch (Exception e) {
            showError("Filter Error", "Failed to filter clients: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void saveClient(Client client) {
        try {
            var em = MainApp.getEntityManagerFactory().createEntityManager();
            em.getTransaction().begin();

            if (client.getId() == null) {
                em.persist(client);
            } else {
                em.merge(client);
            }

            em.getTransaction().commit();
            em.close();
            loadData();
        } catch (Exception e) {
            showError("Save Error", "Failed to save client: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void deleteClient(Client client) {
        try {
            var em = MainApp.getEntityManagerFactory().createEntityManager();
            em.getTransaction().begin();

            client = em.merge(client);
            em.remove(client);

            em.getTransaction().commit();
            em.close();
            loadData();
        } catch (Exception e) {
            showError("Delete Error", "Failed to delete client: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void show() {
        // Not used in component
    }
} 