package src.Service;


import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import src.Models.Subtask;


import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class SubtaskHandler extends BaseHttpHandler {
    private final TaskManager taskManager;
    private final Gson gson;

    public SubtaskHandler(TaskManager taskManager, Gson gson) {
        this.taskManager = taskManager;
        this.gson = gson;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String method = exchange.getRequestMethod();
            String path = exchange.getRequestURI().getPath();

            switch (method) {
                case "GET":
                    if ("/subtasks".equals(path)) {
                        sendText(exchange, gson.toJson(taskManager.getSubtasks()), 200);
                    } else {
                        int id = extractIdFromPath(path);
                        Subtask subtask = taskManager.getSubtask(id);
                        if (subtask == null) {
                            sendNotFound(exchange);
                        } else {
                            sendText(exchange, gson.toJson(subtask), 200);
                        }
                    }
                    break;

                case "POST":
                    try (InputStreamReader reader = new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8)) {
                        Subtask newSubtask = gson.fromJson(reader, Subtask.class);
                        if (newSubtask == null) {
                            sendText(exchange, "{\"error\":\"Invalid subtask data\"}", 400);
                        } else {
                            taskManager.addNewSubtask(newSubtask);
                            sendText(exchange, "Subtask successfully added", 201);
                        }
                    }
                    break;

                case "DELETE":
                    if ("/subtasks".equals(path)) {
                        taskManager.deleteSubtasks();
                        sendText(exchange, "All subtasks deleted", 200);
                    } else {
                        int id = extractIdFromPath(path);
                        taskManager.deleteSubtask(id);
                        sendText(exchange, "Subtask deleted", 200);
                    }
                    break;

                default:
                    sendText(exchange, "{\"error\":\"Unsupported HTTP method\"}", 405);
            }
        } catch (NumberFormatException e) {
            sendText(exchange, "{\"error\":\"Invalid ID format\"}", 400);
        } catch (Exception e) {
            sendText(exchange, "{\"error\":\"Internal server error\"}", 500);
        }
    }
}

