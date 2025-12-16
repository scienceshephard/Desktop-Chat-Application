package com.config;

import com.model.ChatMessage;
import com.model.MessageType;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.*;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class WebSocketClient {
    private WebSocketStompClient stompClient;
    private StompSession stompSession;
    private Consumer<ChatMessage> msgHandler;
    private String username;

    public void connect(String url, String username, Consumer<ChatMessage> msgHandler){
        this.username = username;
        this.msgHandler = msgHandler;

        List<Transport> transports = new ArrayList<>();
        transports.add(new WebSocketTransport(new StandardWebSocketClient()));
        SockJsClient sockJsClient = new SockJsClient(transports);
        stompClient = new WebSocketStompClient(sockJsClient);
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());

        stompClient.connectAsync(url, new StompSessionHandlerAdapter() {
            @Override
            public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
                stompSession = session;
                session.subscribe("/topic/public", new StompFrameHandler() {
                    @Override
                    public Type getPayloadType(StompHeaders headers) {
                        return ChatMessage.class;
                    }

                    @Override
                    public void handleFrame(StompHeaders headers, Object payload) {
                        ChatMessage chatMessage = (ChatMessage) payload;
                        msgHandler.accept(chatMessage);
                    }
                });

                ChatMessage joinMessage = new ChatMessage(MessageType.JOIN, " :has joined the Chat", username);
                session.send("/app/chat.addUser", joinMessage);
//                super.afterConnected(session, connectedHeaders);
            }

            @Override
            public void handleException(StompSession session, StompCommand command, StompHeaders headers, byte[] payload, Throwable exception) {
                exception.printStackTrace();
//                super.handleException(session, command, headers, payload, exception);
            }

            @Override
            public void handleTransportError(StompSession session, Throwable exception) {
                exception.printStackTrace();
//                super.handleTransportError(session, exception);
            }
        });
    }
    public void sendMessage(String content){
        if(stompSession != null && stompSession.isConnected()){
            ChatMessage msg = new ChatMessage(MessageType.CHAT, content, username);
            stompSession.send("/app/chat.sendMessage", msg);
        }
    }
    public void disconnect (){
        if(stompSession != null && stompSession.isConnected()){
            stompSession.disconnect();
        }
    }
}
