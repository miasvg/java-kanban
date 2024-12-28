package Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import src.Models.Epic;
import src.Models.Subtask;
import src.Models.Task;
import src.Models.TaskStatus;
import src.Service.InMemoryTaskManager;
import src.Service.TaskManager;

import java.io.File;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
abstract class TaskManagerTest<T extends TaskManager> {

    protected T taskManager;

    protected abstract T createTaskManager();

    @BeforeEach
    void setUp() {
        taskManager = createTaskManager();
    }

    @Test
    void testAddAndGetTask() {
        Task task = new Task("Task 1", "Description 1", TaskStatus.NEW);
        int id = taskManager.addNewTask(task);

        Task retrievedTask = taskManager.getTask(id);
        assertNotNull(retrievedTask);
        assertEquals(task, retrievedTask);
    }

    @Test
    void testAddAndGetEpic() {
        Epic epic = new Epic("Epic 1", "Description 1");
        int id = taskManager.addNewEpic(epic);

        Epic retrievedEpic = taskManager.getEpic(id);
        assertNotNull(retrievedEpic);
        assertEquals(epic, retrievedEpic);
    }

    @Test
    void testAddAndGetSubtask() {
        Epic epic = new Epic("Epic 1", "Description 1");
        int epicId = taskManager.addNewEpic(epic);

        Subtask subtask = new Subtask("Subtask 1", "Description 1", epicId);
        int subtaskId = taskManager.addNewSubtask(subtask);

        Subtask retrievedSubtask = taskManager.getSubtask(subtaskId);
        assertNotNull(retrievedSubtask);
        assertEquals(subtask, retrievedSubtask);
    }

    @Test
    void testUpdateTask() {
        Task task = new Task("Task 1", "Description 1", TaskStatus.NEW);
        int id = taskManager.addNewTask(task);

        task.setName("Updated Task 1");
        taskManager.updateTask(task);

        Task updatedTask = taskManager.getTask(id);
        assertNotNull(updatedTask);
        assertEquals("Updated Task 1", updatedTask.getName());
    }

    @Test
    void testDeleteTask() {
        Task task = new Task("Task 1", "Description 1", TaskStatus.NEW);
        int id = taskManager.addNewTask(task);

        taskManager.deleteTask(id);
        assertNull(taskManager.getTask(id));
    }

    @Test
    void testEpicStatusCalculation() {
        Epic epic = new Epic("Epic 1", "Description 1");
        int epicId = taskManager.addNewEpic(epic);

        Subtask subtask1 = new Subtask("Subtask 1", "Description 1", epicId);
        subtask1.setStatus(TaskStatus.NEW);
        taskManager.addNewSubtask(subtask1);

        Subtask subtask2 = new Subtask("Subtask 2", "Description 2", epicId);
        subtask2.setStatus(TaskStatus.DONE);
        taskManager.addNewSubtask(subtask2);

        Epic retrievedEpic = taskManager.getEpic(epicId);
        assertEquals(TaskStatus.IN_PROGRESS, retrievedEpic.getStatus());
    }

    @Test
    void testTaskOverlapDetection() {
        InMemoryTaskManager manager = new InMemoryTaskManager();
        Task task1 = new Task("Task 1", "Description 1", TaskStatus.NEW);
        task1.setStartTime(LocalDateTime.of(2023, 10, 1, 10, 0));
        task1.setDuration(Duration.ofHours(2));
        taskManager.addNewTask(task1);

        Task task2 = new Task("Task 2", "Description 2", TaskStatus.NEW);
        task2.setStartTime(LocalDateTime.of(2023, 10, 1, 11, 0));
        task2.setDuration(Duration.ofHours(2));

        assertTrue(manager.isTaskOverlapping(task2));

        task2.setStartTime(LocalDateTime.of(2023, 10, 1, 13, 0));
        assertFalse(manager.isTaskOverlapping(task2));
    }


    // Тесты для InMemoryTaskManager
    class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {
        @Override
        protected InMemoryTaskManager createTaskManager() {
            return new InMemoryTaskManager();
        }
    }
}
