package com.server.websocket;

import org.java_websocket.server.WebSocketServer;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.WebSocket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

public class ChatWebSocket extends WebSocketServer {
    private static final Logger logger = LoggerFactory.getLogger(ChatWebSocket.class);

    public ChatWebSocket(InetSocketAddress address) {
        super(address);
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        logger.info("WebSocket connection opened: {}", conn.getRemoteSocketAddress());
        conn.send("server worked! (via websocket)");
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        logger.info("WebSocket connection closed: {}", conn.getRemoteSocketAddress());
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        logger.debug("Received message: {}", message);
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        logger.error("WebSocket error occurred", ex);
    }

    @Override
    public void onStart() {
        logger.info("WebSocket Server started on port {}", getPort());
    }
}
