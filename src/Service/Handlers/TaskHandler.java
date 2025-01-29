package src.Service.Handlers;
import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import src.Models.Task;
import src.Service.TaskManager;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class TaskHandler extends BaseHttpHandler {
    private final TaskManager taskManager;
    private final Gson gson;

    public TaskHandler(TaskManager taskManager, Gson gson) {

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
                    if ("/tasks".equals(path)) {
                        sendText(exchange, gson.toJson(taskManager.getTasks()), 200);
                    } else {
                        int id = extractIdFromPath(path);
                        Task task = taskManager.getTask(id);
                        if (task == null) {
                            sendNotFound(exchange);
                        } else {
                            sendText(exchange, gson.toJson(task), 200);
                        }
                    }
                    break;

                case "POST":
                    try (InputStreamReader reader = new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8)) {
                        Task newTask = gson.fromJson(reader, Task.class);
                        if (newTask == null) {
                            sendText(exchange, "{\"error\":\"Invalid task data\"}", 400);
                        } else {
                            taskManager.addNewTask(newTask);
                            sendText(exchange, "Task successfully added", 201);
                        }
                    }
                    break;

                case "DELETE":
                    if ("/tasks".equals(path)) {
                        taskManager.deleteTasks();
                        sendText(exchange, "All tasks deleted", 200);
                    } else {
                        int id = extractIdFromPath(path);
                        taskManager.deleteTask(id);
                        sendText(exchange, "Task deleted", 200);
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


