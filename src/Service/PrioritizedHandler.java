package src.Service;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import src.Models.Task;

import java.io.IOException;
import java.util.List;


public class PrioritizedHandler extends BaseHttpHandler {
    private final TaskManager taskManager;
    private final Gson gson = new Gson();

    public PrioritizedHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        if ("GET".equalsIgnoreCase(method)) {
            handleGetPrioritizedTasks(exchange);
        } else {
            sendText(exchange, "Метод не поддерживается для эндпоинта /prioritized.", 405);
        }
    }

    private void handleGetPrioritizedTasks(HttpExchange exchange) throws IOException {
        List<Task> prioritizedTasks = taskManager.getPrioritizedTasks();
        if (prioritizedTasks.isEmpty()) {
            sendText(exchange, "Список задач по приоритету пуст.", 404);
        } else {
            sendText(exchange, gson.toJson(prioritizedTasks), 200);
        }
    }
}


