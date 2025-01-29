package Test;

import com.google.gson.Gson;
import org.junit.jupiter.api.*;
import src.Models.*;
import src.Service.HttpTaskServer;
import src.Service.InMemoryHistoryManager;
import src.Service.InMemoryTaskManager;
import src.Service.TaskManager;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class HttpTaskServerTest {
    private TaskManager manager;
    private HttpTaskServer taskServer;
    private HttpClient client;
    private Gson gson;

    @BeforeAll
    void setUpServer() throws IOException {
        InMemoryTaskManager taskManager = new InMemoryTaskManager();
        InMemoryHistoryManager historyManager = new InMemoryHistoryManager();
        taskServer = new HttpTaskServer(taskManager, historyManager);
        client = HttpClient.newHttpClient();
        gson = HttpTaskServer.getGson();
        taskServer.start();
    }

    @AfterAll
    void shutDownServer() {
        taskServer.stop();
    }

    @BeforeEach
    void clearData() {
        manager.deleteTasks();
        manager.deleteSubtasks();
        manager.deleteEpics();
    }

    @Test
    void testAddAndGetTask() throws IOException, InterruptedException {
        Task task = new Task("Test Task", "Task Description", TaskStatus.NEW);
        String taskJson = gson.toJson(task);

        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());

        List<Task> tasks = manager.getTasks();
        assertEquals(1, tasks.size());
        assertEquals("Test Task", tasks.get(0).getName());
    }

    @Test
    void testDeleteTask() throws IOException, InterruptedException {
        Task task = new Task("Task To Delete", "Desc", TaskStatus.NEW);
        manager.addNewTask(task);
        int taskId = task.getId();

        URI url = URI.create("http://localhost:8080/tasks/" + taskId);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertNull(manager.getTask(taskId));
    }

    @Test
    void testGetPrioritizedTasks() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/prioritized");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode());
    }

    @Test
    void testGetEpics() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode());
    }

    @Test
    void testGetHistory() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/history");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode());
    }
}
