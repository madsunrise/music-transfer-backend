package com.rv150.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;

import java.io.IOException;
import java.util.concurrent.ThreadLocalRandom;

import static com.rv150.websocket.Message.RECEIVER_ID;
import static com.rv150.websocket.Message.SENDING_FINISHED;

/**
 * Created by ivan on 11.05.17.
 */
public class MyHandler extends AbstractWebSocketHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(MyHandler.class.getName());

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final BiMap<Integer, WebSocketSession> connections = HashBiMap.create();

    private final BiMap<WebSocketSession, WebSocketSession> pairs = HashBiMap.create();

    public static final String NO_RECEIVER_WITH_THIS_ID = "No receiver found for this ID!";




    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        LOGGER.info(message.getPayload());
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        Integer id = genRandomId();
        while (connections.containsKey(id)) {
            id = genRandomId();
        }

        connections.put(id, session);
        sendIdToUser(session, id);
        LOGGER.info("New binary session, total: {}", connections.size());
    }

    @Override
    protected void handlePongMessage(WebSocketSession session, PongMessage message) throws Exception {
        LOGGER.info("binary 13");
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        LOGGER.info("binary 323");
    }



    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        LOGGER.info("Binary connection closed");
        removePair(session);
        removeConnection(session);
    }


    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) {
        if (message.getPayloadLength() < 50) {
            try {
                Message msg = objectMapper.readValue((String)message.getPayload(), Message.class);
                switch (msg.getType()) {
                    case RECEIVER_ID:
                        LOGGER.info("We get receiver id = {}", msg.getData());
                        makePair(session, msg);
                        break;
                    case SENDING_FINISHED:
                        LOGGER.info("FINISHED!");
                        finishSending(session);
                        break;
                    default:
                        redirectFrame(session, message);
                }
                return;
            }
            catch (Exception ex) {
                LOGGER.error(ex.getMessage());
                redirectFrame(session, message);
            }
        }
        redirectFrame(session, message);
    }


    private void makePair(WebSocketSession sender, Message msg) {
        int receiverId = Integer.valueOf(msg.getData());
        WebSocketSession receiver = connections.get(receiverId);
        if (receiver == null) {
            LOGGER.error("No receiver found with ID = {}!", receiverId);
            try {
                sendErrorSignal(sender, NO_RECEIVER_WITH_THIS_ID);
            }
            catch (IOException ex) {
                LOGGER.error("Failed to send error signal about no receiver with this ID! {}", ex.getMessage());
            }
            return;
        }
        pairs.put(sender, receiver);
        LOGGER.debug("New pair! Total: {}", pairs.size());
    }

    private void finishSending(WebSocketSession sender) {
        WebSocketSession receiver = pairs.get(sender);
        pairs.remove(sender);
        sendFinishSignal(receiver);
    }


    private void redirectFrame(WebSocketSession sender, WebSocketMessage<?> message) {
        LOGGER.info("Redirecting frame... length = {}", message.getPayloadLength());
        WebSocketSession receiver = pairs.get(sender);
        try {
            receiver.sendMessage(message);
        }
        catch (IOException ex) {
            LOGGER.error("Failed to send frame to receiver! {}", ex.getMessage());
        }
        catch (NullPointerException ex) {
            LOGGER.error("Receiver is null! Sending error msg...");
            try {
                sendErrorSignal(sender, "Receiver is not found");
                removePair(sender);
            }
            catch (Exception e) {
                LOGGER.error("Failed to disconnect sender! {}", e.getMessage());
            }
        }
    }



    private void sendFinishSignal(WebSocketSession session) {
        final Message message = new Message(Message.SENDING_FINISHED, "ok");
        try {
            final String json = objectMapper.writeValueAsString(message);
            session.sendMessage(new TextMessage(json));
        }
        catch (Exception e) {
            LOGGER.error("Sending finish signal failed");
        }
    }



    private void sendErrorSignal(WebSocketSession session, String errorDsc) throws IOException {
        final Message message = new Message(Message.ERROR, errorDsc);
        final String json = objectMapper.writeValueAsString(message);
        session.sendMessage(new TextMessage(json));
    }



    @Override
    protected void handleBinaryMessage(WebSocketSession session, BinaryMessage message) throws Exception {
        LOGGER.info("Handled binary message?");
    }

    private int genRandomId() {
        return ThreadLocalRandom.current().nextInt(1000, 10000);
    }

    private void removeConnection(WebSocketSession session) {
        connections.inverse().remove(session);
    }

    private void removePair(WebSocketSession session) {
        if (pairs.containsKey(session)) {
            pairs.remove(session);
        } else {
            pairs.inverse().remove(session);
        }
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
