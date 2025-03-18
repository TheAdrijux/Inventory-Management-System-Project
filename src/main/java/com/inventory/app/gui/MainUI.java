package com.inventory.app.gui;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MainUI extends BaseUI {
    private BorderPane root;
    private MenuBar menuBar;
    private TabPane tabPane;

    public MainUI(Stage stage) {
        super(stage);
    }

    @Override
    public void initialize() {
        root = new BorderPane();
        createMenuBar();
        createTabPane();

        Scene scene = new Scene(root, 1024, 768);
        scene.getStylesheets().add(getClass().getResource("/styles/main.css").toExternalForm());
        
        stage.setTitle("Inventory Management System");
        stage.setScene(scene);
    }

    private void createMenuBar() {
        menuBar = new MenuBar();

        // File Menu
        Menu fileMenu = new Menu("File");
        MenuItem exitItem = new MenuItem("Exit");
        exitItem.setOnAction(e -> stage.close());
        fileMenu.getItems().add(exitItem);

        // Data Menu
        Menu dataMenu = new Menu("Data");
        MenuItem importItem = new MenuItem("Import");
        MenuItem exportItem = new MenuItem("Export");
        dataMenu.getItems().addAll(importItem, exportItem);

        // Help Menu
        Menu helpMenu = new Menu("Help");
        MenuItem aboutItem = new MenuItem("About");
        aboutItem.setOnAction(e -> showAboutDialog());
        helpMenu.getItems().add(aboutItem);

        menuBar.getMenus().addAll(fileMenu, dataMenu, helpMenu);
        root.setTop(menuBar);
    }

    private void createTabPane() {
        tabPane = new TabPane();
        
        // Products Tab
        Tab productsTab = new Tab("Products");
        productsTab.setContent(new ProductUI(stage).getContent());
        productsTab.setClosable(false);

        // Categories Tab
        Tab categoriesTab = new Tab("Categories");
        categoriesTab.setContent(new CategoryUI(stage).getContent());
        categoriesTab.setClosable(false);

        // Orders Tab
        Tab ordersTab = new Tab("Orders");
        ordersTab.setContent(new OrderUI(stage).getContent());
        ordersTab.setClosable(false);

        // Clients Tab
        Tab clientsTab = new Tab("Clients");
        clientsTab.setContent(new ClientUI(stage).getContent());
        clientsTab.setClosable(false);

        tabPane.getTabs().addAll(productsTab, categoriesTab, ordersTab, clientsTab);
        root.setCenter(tabPane);
    }

    private void showAboutDialog() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About");
        alert.setHeaderText("Inventory Management System");
        alert.setContentText("Version 1.0\n© 2024 Your Company");
        alert.showAndWait();
    }

    @Override
    public void show() {
        stage.show();
    }
} 