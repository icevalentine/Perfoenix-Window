package org.example.perfoenixwindow;

import javafx.application.Application;
import javafx.scene.Cursor;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.geometry.Insets;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class DashBoardApp extends Application {
    private static final int RESIZE_MARGIN = 6;

    private ResizeDirection resizeDir = ResizeDirection.NONE;
    private double startX, startY;
    private double startW, startH;
    private double mouseX, mouseY;
    private Stage stage;
    private double dragX, dragY;

    private HBox tabBar = new HBox();
    private StackPane contentArea = new StackPane();

    private final List<TabItem> tabs = new ArrayList<>();
    private TabItem activeTab;

    private boolean maximized = false;
    private double oldX, oldY, oldW, oldH;

    private static final double MIN_TAB_WIDTH = 100;
    private static final double MAX_TAB_WIDTH = 150;

    @Override
    public void start(Stage stage) {
        this.stage = stage;

        stage.initStyle(StageStyle.UNDECORATED);

        BorderPane root = new BorderPane();

        HBox titleBar = createTitleBar();
        root.setTop(titleBar);
        root.getStyleClass().add("dashboard");
        root.setCenter(contentArea);
        URL url = getClass().getResource("/DashBoard.css");

        Scene scene = new Scene(root, 1200, 600);

        if (url == null) {
            System.out.println("❌ CSS NOT FOUND");
        } else {
            System.out.println("✅ CSS FOUND: " + url);
            scene.getStylesheets().add(url.toExternalForm());
        }

        enableResize(scene);
        stage.setScene(scene);
        stage.show();
    }

    // ================= TITLE BAR =================
    private HBox createTitleBar() {
        HBox titleBar = new HBox();
        titleBar.setPadding(new Insets(4));
        titleBar.getStyleClass().add("topbar");
        titleBar.setPrefHeight(36);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button btnMin = createWindowButton("_");
        Button btnMax = createWindowButton("□");
        Button btnClose = createWindowButton("X");

        btnMin.setOnAction(e -> stage.setIconified(true));
        btnClose.setOnAction(e -> stage.close());
        btnMax.setOnAction(e -> toggleMaximize());

        HBox windowButtons = new HBox(btnMin, btnMax, btnClose);

        // Drag window
        titleBar.setOnMousePressed(this::startDrag);
        titleBar.setOnMouseDragged(this::dragging);

        tabBar.setPadding(new Insets(0, 8, 0, 8));
        tabBar.setStyle("-fx-alignment: center;");
        Button menuBtn = new Button();
        menuBtn.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("/assets/ic_btn_menu.png"))));
        menuBtn.getStyleClass().add("menu-button");
        Button refreshButton = new Button();
        refreshButton.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("/assets/ic_btn_refresh.png"))));
        refreshButton.setOnAction(e -> addNewTab("SM-S908U"));
        refreshButton.getStyleClass().add("refresh-button");

        tabBar.getChildren().add(menuBtn);

        titleBar.getChildren().addAll(tabBar, refreshButton, spacer, windowButtons);
        return titleBar;
    }

    // ================= TAB =================
    private void addNewTab(String tabtilte) {
        TabItem tab = new TabItem(tabtilte, this);
        tabs.add(tab);
        tabBar.getChildren().add(tab);
        setActiveTab(tab);
        updateTabWidths();
    }

    void closeTab(TabItem tab) {
        tabs.remove(tab);
        tabBar.getChildren().remove(tab);
        contentArea.getChildren().remove(tab.getContent());

        if (tab == activeTab && !tabs.isEmpty()) {
            setActiveTab(tabs.get(0));
        }
        updateTabWidths();
    }

    void setActiveTab(TabItem tab) {
        if (activeTab != null) activeTab.setActive(false);
        activeTab = tab;
        tab.setActive(true);

        contentArea.getChildren().setAll(tab.getContent());
    }

    private void updateTabWidths() {
        double availableWidth = stage.getWidth() - 200;
        double width = availableWidth / Math.max(1, tabs.size());
        width = Math.max(MIN_TAB_WIDTH, Math.min(MAX_TAB_WIDTH, width));

        for (TabItem t : tabs) {
            t.setPrefWidth(width);
        }
    }

    // ================= WINDOW =================
    private void startDrag(MouseEvent e) {
        dragX = e.getSceneX();
        dragY = e.getSceneY();
    }

    private void dragging(MouseEvent e) {
        if (!maximized) {
            stage.setX(e.getScreenX() - dragX);
            stage.setY(e.getScreenY() - dragY);
        }
    }

    private void toggleMaximize() {
        if (!maximized) {
            oldX = stage.getX();
            oldY = stage.getY();
            oldW = stage.getWidth();
            oldH = stage.getHeight();

            Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
            stage.setX(bounds.getMinX());
            stage.setY(bounds.getMinY());
            stage.setWidth(bounds.getWidth());
            stage.setHeight(bounds.getHeight());
        } else {
            stage.setX(oldX);
            stage.setY(oldY);
            stage.setWidth(oldW);
            stage.setHeight(oldH);
        }
        maximized = !maximized;
    }

    // ================= UTILS =================
    private Button createWindowButton(String text) {
        Button btn = new Button(text);
        btn.setStyle("-fx-background-color: transparent; -fx-text-fill: white;");
        btn.setPrefSize(40, 30);
        btn.setOnMouseEntered(e -> btn.setStyle("-fx-background-color: #444; -fx-text-fill: white;"));
        btn.setOnMouseExited(e -> btn.setStyle("-fx-background-color: transparent; -fx-text-fill: white;"));
        return btn;
    }

    private String tabButtonStyle() {
        return "-fx-background-color: transparent; -fx-text-fill: white;";
    }

    private void enableResize(Scene scene) {

        scene.setOnMouseMoved(e -> {
            if (maximized) return;

            double x = e.getSceneX();
            double y = e.getSceneY();
            double w = stage.getWidth();
            double h = stage.getHeight();

            ResizeDirection dir = ResizeDirection.NONE;

            if (x < RESIZE_MARGIN && y < RESIZE_MARGIN) dir = ResizeDirection.TOP_LEFT;
            else if (x > w - RESIZE_MARGIN && y < RESIZE_MARGIN) dir = ResizeDirection.TOP_RIGHT;
            else if (x < RESIZE_MARGIN && y > h - RESIZE_MARGIN) dir = ResizeDirection.BOTTOM_LEFT;
            else if (x > w - RESIZE_MARGIN && y > h - RESIZE_MARGIN) dir = ResizeDirection.BOTTOM_RIGHT;
            else if (x < RESIZE_MARGIN) dir = ResizeDirection.LEFT;
            else if (x > w - RESIZE_MARGIN) dir = ResizeDirection.RIGHT;
            else if (y < RESIZE_MARGIN) dir = ResizeDirection.TOP;
            else if (y > h - RESIZE_MARGIN) dir = ResizeDirection.BOTTOM;

            resizeDir = dir;
            scene.setCursor(getCursor(dir));
        });

        scene.setOnMousePressed(e -> {
            mouseX = e.getScreenX();
            mouseY = e.getScreenY();
            startX = stage.getX();
            startY = stage.getY();
            startW = stage.getWidth();
            startH = stage.getHeight();
        });

        scene.setOnMouseDragged(e -> {
            if (resizeDir == ResizeDirection.NONE || maximized) return;

            double dx = e.getScreenX() - mouseX;
            double dy = e.getScreenY() - mouseY;

            switch (resizeDir) {
                case LEFT -> {
                    stage.setX(startX + dx);
                    stage.setWidth(startW - dx);
                }
                case RIGHT -> stage.setWidth(startW + dx);
                case TOP -> {
                    stage.setY(startY + dy);
                    stage.setHeight(startH - dy);
                }
                case BOTTOM -> stage.setHeight(startH + dy);
                case TOP_LEFT -> {
                    stage.setX(startX + dx);
                    stage.setWidth(startW - dx);
                    stage.setY(startY + dy);
                    stage.setHeight(startH - dy);
                }
                case TOP_RIGHT -> {
                    stage.setWidth(startW + dx);
                    stage.setY(startY + dy);
                    stage.setHeight(startH - dy);
                }
                case BOTTOM_LEFT -> {
                    stage.setX(startX + dx);
                    stage.setWidth(startW - dx);
                    stage.setHeight(startH + dy);
                }
                case BOTTOM_RIGHT -> {
                    stage.setWidth(startW + dx);
                    stage.setHeight(startH + dy);
                }
            }
        });
    }

    private Cursor getCursor(ResizeDirection dir) {
        return switch (dir) {
            case LEFT, RIGHT -> Cursor.H_RESIZE;
            case TOP, BOTTOM -> Cursor.V_RESIZE;
            case TOP_LEFT, BOTTOM_RIGHT -> Cursor.NW_RESIZE;
            case TOP_RIGHT, BOTTOM_LEFT -> Cursor.NE_RESIZE;
            default -> Cursor.DEFAULT;
        };
    }


    public static void main(String[] args) {
        launch(args);
    }
}