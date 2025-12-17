package com.config;

import com.fasterxml.jackson.databind.ObjectMapper;
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
        MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
        converter.setObjectMapper(new ObjectMapper());
        stompClient.setMessageConverter(converter);

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
                        System.out.println("New payload class: " + payload.getClass().getName());
                        System.out.println("Payload content: " + payload);

                        ChatMessage chatMessage = (ChatMessage) payload;
                        System.out.println("Received message: "
                                + chatMessage.getContent() + " from " +
                                chatMessage.getSender() + "and type"
                                + chatMessage.getMessageType());
                        msgHandler.accept(chatMessage);
                    }
                });

                ChatMessage joinMessage = new ChatMessage( username, " :has joined the Chat", MessageType.JOIN);
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
            ChatMessage msg = new ChatMessage(username, content, MessageType.CHAT);
            stompSession.send("/app/chat.sendMessage", msg);
        }
    }
    public void disconnect (){
        if(stompSession != null && stompSession.isConnected()){
            stompSession.disconnect();
        }
    }
}
