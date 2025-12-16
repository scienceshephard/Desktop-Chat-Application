package com.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LoginController {
    @FXML
    private TextField usernameField;

    @FXML
    private void handleLogin() {
        String username = usernameField.getText().trim();

        if (username.isEmpty()) {
            showAlert("Error", "Please enter a username");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/chat.fxml"));
            Parent root = loader.load();

            ChatController chatController = loader.getController();
            chatController.setUsername(username);
            chatController.connect();

            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setScene(new Scene(root, 600, 500));
            stage.setTitle("Chat - " + username);
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Could not open chat window");
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
