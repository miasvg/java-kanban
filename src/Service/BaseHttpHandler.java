package src.Service;


import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

abstract class BaseHttpHandler implements HttpHandler {
    protected void sendText(HttpExchange exchange, String text, int statusCode) throws IOException {
        byte[] response = text.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        exchange.sendResponseHeaders(statusCode, response.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response);
        }
    }
    protected void sendNotFound(HttpExchange exchange) throws IOException {
        sendText(exchange, "{\"error\":\"Resource not found\"}", 404);
    }
    protected void sendHasInteractions(HttpExchange exchange) throws IOException {
        sendText(exchange, "{\"error\":\"Task conflict\"}", 406);
    }
    protected void sendServerError(HttpExchange exchange, String message) throws IOException {
        sendText(exchange, "{\"error\":\"" + message + "\"}", 500);
    }
    protected int extractIdFromPath(String path) throws NumberFormatException {
        // Убираем базовый путь из полного пути
        String idPart = path.substring(basePath.length());
        // Удаляем ведущий слэш, если он есть
        if (idPart.startsWith("/")) {
            idPart = idPart.substring(1);
        }
        // Преобразуем оставшуюся часть в число
        return Integer.parseInt(idPart);
    }

}