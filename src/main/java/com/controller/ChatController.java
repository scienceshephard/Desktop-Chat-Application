package com.controller;

import com.config.WebSocketClient;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import com.model.ChatMessage;
import lombok.Setter;

public class ChatController {
    @FXML
    private ListView<String> messageList;

    @FXML
    private TextField messageField;

    private WebSocketClient webSocketClient;
    @Setter
    private String username;

    public void connect() {
        webSocketClient = new WebSocketClient();
        webSocketClient.connect("ws://localhost:8080/ws", username, this::handleIncomingMessage);
    }

    private void handleIncomingMessage(ChatMessage message) {
        Platform.runLater(() -> {
            String displayMessage;
            try {
                displayMessage = message.getType() != null ? switch (message.getType()) {
                    case JOIN -> message.getSender() + " joined the chat";
                    case LEAVE -> message.getSender() + " left the chat";
                    case CHAT -> message.getSender() + ": " + message.getContent();
                }: "JOIN" ;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            messageList.getItems().add(displayMessage);
            messageList.scrollTo(messageList.getItems().size() - 1);
        });
    }

    @FXML
    private void handleSendMessage() {
        String content = messageField.getText().trim();

        if (!content.isEmpty()) {
            webSocketClient.sendMessage(content);
            messageField.clear();
        }
    }

    @FXML
    private void initialize() {
        messageField.setOnAction(event -> handleSendMessage());
    }

    public void disconnect() {
        if (webSocketClient != null) {
            webSocketClient.disconnect();
        }
    }
}
