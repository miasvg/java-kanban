package src.Service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpServer;
import src.Service.Handlers.*;

import java.io.IOException;
import java.net.InetSocketAddress;

public class HttpTaskServer {
    private final HttpServer server;
    private final TaskManager taskManager;
    private final HistoryManager historyManager;
    private static final int PORT = 8080;
    private static final Gson gson = new GsonBuilder().create();

    public HttpTaskServer(TaskManager taskManager, HistoryManager historyManager) throws IOException {
        this.taskManager = taskManager;
        this.historyManager = historyManager;
        server = HttpServer.create(new InetSocketAddress(PORT), 0);
        registerHandlers();
    }

    public void start() {
        server.start();
        System.out.println("Server started on port " + PORT);
    }

    public void stop() {
        server.stop(0);
        System.out.println("Server stopped");
    }

    private void registerHandlers() {
        server.createContext("/tasks", new TaskHandler(taskManager, gson));
        server.createContext("/subtasks", new SubtaskHandler(taskManager, gson));
        server.createContext("/epics", new EpicHandler(taskManager));
        server.createContext("/history", new HistoryHandler(historyManager));
        server.createContext("/prioritized", new PrioritizedHandler(taskManager));

    }

    public static Gson getGson() {
        return gson;
    }
}