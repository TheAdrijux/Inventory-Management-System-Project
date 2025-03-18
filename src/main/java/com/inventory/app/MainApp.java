package com.inventory.app;

import com.inventory.app.gui.*;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class MainApp extends Application {
    private static EntityManagerFactory emf;
    private static TabPane tabPane;
    private ProductUI productUI;
    private CategoryUI categoryUI;
    private OrderUI orderUI;
    private ClientUI clientUI;

    @Override
    public void init() {
        try {
            // Initialize JPA
            emf = Persistence.createEntityManagerFactory("inventory-pu");
        } catch (Exception e) {
            System.err.println("Database connection error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            primaryStage.setTitle("Inventory Management System");
            
            // Initialize UI components
            tabPane = new TabPane();
            productUI = new ProductUI(primaryStage);
            categoryUI = new CategoryUI(primaryStage);
            orderUI = new OrderUI(primaryStage);
            clientUI = new ClientUI(primaryStage);

            // Create tabs with custom styling
            Tab productsTab = createStyledTab("Products", productUI.getContent());
            Tab categoriesTab = createStyledTab("Categories", categoryUI.getContent());
            Tab ordersTab = createStyledTab("Orders", orderUI.getContent());
            Tab clientsTab = createStyledTab("Clients", clientUI.getContent());

            // Add tabs to the pane
            tabPane.getTabs().addAll(productsTab, categoriesTab, ordersTab, clientsTab);
            
            // Style the TabPane
            tabPane.setStyle("-fx-tab-min-height: 35px; " +
                           "-fx-tab-max-height: 35px;");
            
            // Apply CSS to make tabs more prominent
            tabPane.getStylesheets().add(getClass().getResource("/styles/tabs.css").toExternalForm());

            // Create scene and show
            Scene scene = new Scene(tabPane, 1024, 768);
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (Exception e) {
            System.err.println("Error starting application: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private Tab createStyledTab(String text, Node content) {
        Tab tab = new Tab(text, content);
        tab.setClosable(false);
        return tab;
    }

    @Override
    public void stop() {
        // Close JPA resources
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
    }

    public static EntityManagerFactory getEntityManagerFactory() {
        return emf;
    }

    public static TabPane getTabPane() {
        return tabPane;
    }

    public static void setTabPane(TabPane pane) {
        tabPane = pane;
    }

    public static void main(String[] args) {
        try {
            launch(args);
        } catch (Exception e) {
            System.err.println("Fatal error: " + e.getMessage());
            e.printStackTrace();
        }
    }
} 