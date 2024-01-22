package org.example.abstraction.response;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;

public class HttpResponse {
    private final ObjectMapper mapper = new ObjectMapper();

    public void sendJsonResponse(HttpExchange exchange, Object response, int statusCode) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        String jsonResponse = mapper.writeValueAsString(response);
        exchange.sendResponseHeaders(statusCode, jsonResponse.length());
        OutputStream os = exchange.getResponseBody();
        os.write(jsonResponse.getBytes());
        os.close();
    }
}