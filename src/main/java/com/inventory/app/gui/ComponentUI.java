package com.inventory.app.gui;

import javafx.scene.Parent;
import javafx.stage.Stage;

public abstract class ComponentUI extends BaseUI {
    public ComponentUI(Stage stage) {
        super(stage);
    }

    public abstract Parent getContent();
} 