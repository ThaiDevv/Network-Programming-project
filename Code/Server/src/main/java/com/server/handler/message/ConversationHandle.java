package com.server.handler.message;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.server.service.ConversationService;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.SQLException;

public class ConversationHandle implements HttpHandler {
    private final Gson gson = new Gson();
    private final ConversationService conversationService = new ConversationService();
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        // Xử lý CORS preflight
        if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
            exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
            exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "POST, OPTIONS");
            exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type");
            exchange.sendResponseHeaders(204, -1);
            return;
        }

        if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(405, -1);
            return;
        }
        try (InputStreamReader reader = new InputStreamReader(exchange.getRequestBody())) {
            JsonObject json = gson.fromJson(reader, JsonObject.class);

            if (json == null || !json.has("user1Id") || !json.has("user2Id")) {
                sendResponse(exchange, 400, "{\"error\": \"Missing required fields: user1Id, user2Id\"}");
                return;
            }

            long user1Id = json.get("user1Id").getAsLong();
            long user2Id = json.get("user2Id").getAsLong();
            long conversationId = conversationService.getOrCreatePrivateConversation(user1Id, user2Id);

            String response = "{\"status\": \"success\", \"conversationId\": " + conversationId + "}";
            sendResponse(exchange, 200, response);
        } catch (Exception e) {
            sendResponse(exchange, 500, "{\"error\": \"Internal Server Error\"}");
        }
    }
    private void sendResponse(HttpExchange exchange, int status, String body) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        byte[] bytes = body.getBytes();
        exchange.sendResponseHeaders(status, bytes.length);
        exchange.getResponseBody().write(bytes);
        exchange.getResponseBody().close();
    }
}
