package src.Service;

import com.sun.net.httpserver.HttpHandler;
import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import src.Models.Epic;
import src.Models.Subtask;

import java.io.IOException;
import java.util.List;

public class EpicHandler extends BaseHttpHandler {
    private final TaskManager taskManager;
    private final Gson gson = new Gson();

    public EpicHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();

        if ("GET".equalsIgnoreCase(method)) {
            if (path.matches("/epics")) {
                handleGetEpics(exchange);
            } else if (path.matches("/epics/\\d+")) {
                handleGetEpicById(exchange);
            } else if (path.matches("/epics/\\d+/subtasks")) {
                handleGetEpicSubtasks(exchange);
            } else {
                sendText(exchange, "Эндпоинт не найден.", 404);
            }
        } else if ("POST".equalsIgnoreCase(method) && "/epics".equals(path)) {
            handleCreateEpic(exchange);
        } else if ("DELETE".equalsIgnoreCase(method) && path.matches("/epics/\\d+")) {
            handleDeleteEpic(exchange);
        } else {
            sendText(exchange, "Метод или эндпоинт не поддерживаются.", 405);
        }
    }

    private void handleGetEpics(HttpExchange exchange) throws IOException {
        List<Epic> epics = taskManager.getEpics();
        if (epics.isEmpty()) {
            sendText(exchange, "Список эпиков пуст.", 404);
        } else {
            sendText(exchange, gson.toJson(epics), 200);
        }
    }

    private void handleGetEpicById(HttpExchange exchange) throws IOException {
        try {
            int id = extractIdFromPath(exchange.getRequestURI().getPath());
            Epic epic = taskManager.getEpic(id);
            if (epic == null) {
                sendText(exchange, "Эпик с id " + id + " не найден.", 404);
                return;
            }
            sendText(exchange, gson.toJson(epic), 200);
        } catch (NumberFormatException e) {
            sendText(exchange, "Некорректный id.", 400);
        }
    }

    private void handleGetEpicSubtasks(HttpExchange exchange) throws IOException {
        try {
            int id = extractIdFromPath(exchange.getRequestURI().getPath());
            List<Subtask> subtasks = taskManager.getEpicSubtasks(id);
            if (subtasks == null || subtasks.isEmpty()) {
                sendText(exchange, "Подзадачи для эпика с id " + id + " не найдены.", 404);
                return;
            }
            sendText(exchange, gson.toJson(subtasks), 200);
        } catch (NumberFormatException e) {
            sendText(exchange, "Некорректный id.", 400);
        }
    }

    private void handleCreateEpic(HttpExchange exchange) throws IOException {
        String requestBody = new String(exchange.getRequestBody().readAllBytes());
        Epic epic = gson.fromJson(requestBody, Epic.class);
        taskManager.addNewEpic(epic);
        sendText(exchange, "Эпик создан.", 201);
    }

    private void handleDeleteEpic(HttpExchange exchange) throws IOException {
        try {
            int id = extractIdFromPath(exchange.getRequestURI().getPath());
            taskManager.deleteEpic(id);
            sendText(exchange, "Эпик с id " + id + " удалён.", 200);
        } catch (NumberFormatException e) {
            sendText(exchange, "Некорректный id.", 400);
        }
    }
}

