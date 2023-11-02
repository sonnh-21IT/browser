package com.example.browser;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class BrowserApplication extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(BrowserController.class.getResource("browser-tab.fxml"));

        Scene scene = new Scene(root, 800, 600);

        Screen screen = Screen.getPrimary();
        Rectangle2D bounds = screen.getVisualBounds();

        double centerX = bounds.getMinX() + bounds.getWidth() / 2;
        double centerY = bounds.getMinY() + bounds.getHeight() / 2;
        primaryStage.setX(centerX - (bounds.getWidth() * 0.8) / 2);
        primaryStage.setY(centerY - (bounds.getHeight() * 0.8) / 2);
        primaryStage.setWidth(bounds.getWidth() * 0.8);
        primaryStage.setHeight(bounds.getHeight() * 0.8);

        primaryStage.setScene(scene);
        primaryStage.setTitle("Simple Browser");
        primaryStage.show();
    }
}
