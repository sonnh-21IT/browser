package com.example.browser;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class BrowserController {
    @FXML
    private TabPane tabPane;
    private static final int MAX_TABS = 5;

    @FXML
    public void addTab() {
        if (tabPane.getTabs().size() >= MAX_TABS + 1) {
            showAlert("Số lượng tab đã đạt giới hạn tối đa.");
            return;
        }
        try {
            // Tải file FXML của tab
            FXMLLoader loader = new FXMLLoader(getClass().getResource("tab.fxml"));
            AnchorPane tabContent = loader.load();

            // Tạo một tab mới và thiết lập nội dung từ file FXML
            Tab newTab = new Tab();
            newTab.setText("New Tab");
            newTab.setContent(tabContent);

            // Thêm tab mới vào tabPane
            tabPane.getTabs().add(tabPane.getTabs().size() - 1, newTab);

            // Chuyển đến tab mới tạo
            tabPane.getSelectionModel().select(newTab);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Thông báo");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
