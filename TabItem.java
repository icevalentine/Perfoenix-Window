package org.example.perfoenixwindow;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class TabItem extends HBox {

    private final DashBoardApp app;
    private final VBox content = new VBox();
    private boolean active = false;

    public TabItem(String title, DashBoardApp app) {
        this.app = app;

        setPadding(new Insets(6, 10, 6, 10));
        setSpacing(6);
        setStyle(inactiveStyle());

        ImageView deviceIcon = new ImageView(new Image(getClass().getResourceAsStream("/assets/ic_device.png")));

        Label label = new Label(title);
        label.setStyle("-fx-text-fill: black;");

        Button close = new Button();
        close.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("/assets/ic_close.png"))));
        close.getStyleClass().add("close-button");
        close.setOnAction(e -> app.closeTab(this));
        setAlignment(Pos.CENTER);
        getChildren().addAll(deviceIcon, label, close);
        setStyle("-fx-alignment: center;");
        content.getChildren().add(new Label("Content of " + title));
        content.setStyle("-fx-background-color: #1e1e1e; -fx-text-fill: white;");

        setOnMouseClicked(e -> app.setActiveTab(this));
    }

    public VBox getContent() {
        return content;
    }

    public void setActive(boolean active) {
        this.active = active;
        setStyle(active ? activeStyle() : inactiveStyle());
    }

    private String activeStyle() {
        return "-fx-background-color: white; -fx-background-radius: 8 8 0 0;";
    }

    private String inactiveStyle() {
        return "-fx-background-color: transparent; -fx-background-radius: 8 8 0 0;";
    }
}

