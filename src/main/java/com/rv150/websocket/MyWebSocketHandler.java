package com.rv150.websocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

/**
 * Created by ivan on 10.05.17.
 */

public class MyWebSocketHandler extends TextWebSocketHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(MyWebSocketHandler.class.getName());

    @Override
    public void afterConnectionEstablished(WebSocketSession webSocketSession) {
        LOGGER.info("Connection established!");
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage textMessage) {
        LOGGER.info("Handled text message with size {}", textMessage.getPayloadLength());
    }


    @Override
    public void handleTransportError(WebSocketSession webSocketSession, Throwable throwable) {
        LOGGER.warn("Websocket transport problem", throwable);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession webSocketSession, CloseStatus closeStatus) {
        LOGGER.info("Connection closed!");
    }


    @Override
    public boolean supportsPartialMessages() {
        return false;
    }
}
