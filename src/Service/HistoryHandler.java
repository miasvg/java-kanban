package src.Service;

import com.sun.net.httpserver.HttpHandler;
import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import src.Models.Task;

import java.io.IOException;
import java.util.List;

public class HistoryHandler extends BaseHttpHandler {
    private final HistoryManager historyManager;
    private final Gson gson = new Gson();

    public HistoryHandler(HistoryManager historyManager) {
        this.historyManager = historyManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        if ("GET".equalsIgnoreCase(method)) {
            handleGetHistory(exchange);
        } else {
            sendText(exchange, "Метод не поддерживается для эндпоинта /history.", 405);
        }
    }

    private void handleGetHistory(HttpExchange exchange) throws IOException {
        List<Task> history = historyManager.getHistory();
        if (history.isEmpty()) {
            sendText(exchange, "История действий пуста.", 404);
        } else {
            sendText(exchange, gson.toJson(history), 200);
        }
    }
}

