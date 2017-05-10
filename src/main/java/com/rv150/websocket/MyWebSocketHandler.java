package com.rv150.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by ivan on 10.05.17.
 */

public class MyWebSocketHandler extends TextWebSocketHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(MyWebSocketHandler.class.getName());

    private final BiMap<Integer, WebSocketSession> connections = HashBiMap.create();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void afterConnectionEstablished(WebSocketSession webSocketSession) {
        Integer id = genRandomId();
        while (connections.containsKey(id)) {
            id = genRandomId();
        }

        connections.put(id, webSocketSession);
        sendIdToUser(webSocketSession, id);

        LOGGER.info("Connection established! ID = {}", id);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage textMessage) throws IOException {
        LOGGER.info("Handled text message {}", textMessage.getPayload());
    }


    @Override
    public void handleTransportError(WebSocketSession webSocketSession, Throwable throwable) {
        LOGGER.warn("Websocket transport problem", throwable);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession webSocketSession, CloseStatus closeStatus) {
        LOGGER.info("Connection closed!");
        connections.inverse().remove(webSocketSession);
    }


    @Override
    public boolean supportsPartialMessages() {
        return false;
    }

    private int genRandomId() {
        return ThreadLocalRandom.current().nextInt(1000, 10000);
    }



    private void sendIdToUser(WebSocketSession webSocketSession, int id) {
        final Message message = new Message(Message.INITIALIZE_USER, String.valueOf(id));
        try {
            final String json = objectMapper.writeValueAsString(message);
            webSocketSession.sendMessage(new TextMessage(json));
        }
        catch (Exception e) {
            LOGGER.error("Sending ID to user FAILED");
        }
    }
}
