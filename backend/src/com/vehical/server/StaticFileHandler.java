package com.vehical.server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class StaticFileHandler implements HttpHandler {
    private final String baseDir;

    public StaticFileHandler(String baseDir) {
        this.baseDir = baseDir;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String requestPath = exchange.getRequestURI().getPath();
        
        // Default to index.html for root
        if (requestPath.equals("/")) {
            requestPath = "/index.html";
        }

        // Remove leading slash
        if (requestPath.startsWith("/")) {
            requestPath = requestPath.substring(1);
        }

        Path filePath = Paths.get(baseDir, requestPath).normalize();
        
        // Security check: prevent directory traversal
        if (!filePath.startsWith(Paths.get(baseDir).normalize())) {
            sendResponse(exchange, 403, "Forbidden");
            return;
        }

        if (!Files.exists(filePath) || Files.isDirectory(filePath)) {
            sendResponse(exchange, 404, "Not Found");
            return;
        }

        String contentType = getContentType(filePath.toString());
        byte[] fileContent = Files.readAllBytes(filePath);

        exchange.getResponseHeaders().set("Content-Type", contentType);
        exchange.sendResponseHeaders(200, fileContent.length);
        OutputStream os = exchange.getResponseBody();
        os.write(fileContent);
        os.close();
    }

    private void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
        exchange.sendResponseHeaders(statusCode, response.length());
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes(StandardCharsets.UTF_8));
        os.close();
    }

    private String getContentType(String fileName) {
        if (fileName.endsWith(".html")) return "text/html";
        if (fileName.endsWith(".css")) return "text/css";
        if (fileName.endsWith(".js")) return "application/javascript";
        if (fileName.endsWith(".json")) return "application/json";
        if (fileName.endsWith(".png")) return "image/png";
        if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg")) return "image/jpeg";
        if (fileName.endsWith(".svg")) return "image/svg+xml";
        if (fileName.endsWith(".ico")) return "image/x-icon";
        return "application/octet-stream";
    }
}
