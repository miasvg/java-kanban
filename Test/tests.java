package Test;

//import org.junit.Test;
import src.Models.Epic;
import src.Models.Subtask;
import src.Models.Task;
import src.Models.TaskStatus;
import src.Service.*;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

//import static org.junit.Assert.assertEquals;
//import static org.junit.Assert.assertNotEquals;
import static org.junit.jupiter.api.Assertions.*;
//import static org.junit.jupiter.api.Assertions.assertTrue;
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import org.junit.jupiter.api.Test;

//import static org.junit.jupiter.api.*;

public class tests {
    @Test
    void testTaskEqualityById() {
        Task task1 = new Task("Task 1", "Description", TaskStatus.NEW);
        Task task2 = new Task("Task 1", "Description", TaskStatus.NEW);

        task1.setId(1);
        task2.setId(1);

        assertEquals(task1,task2,"Tasks should be equal if their IDs are the same");
    }

    @Test
    void testTaskInequalityById() {
        Task task1 = new Task("Task 1", "Description", TaskStatus.NEW);
        Task task2 = new Task("Task 2", "Description", TaskStatus.NEW);

        task1.setId(1);
        task2.setId(2);

        assertNotEquals(task1, task2, "Tasks should not be equal if their IDs are different");
    }

    @Test
    void testSubtaskEqualityById() {
        Subtask subtask1 = new Subtask("Subtask 1", "Description", 1);
        Subtask subtask2 = new Subtask("Subtask 1", "Description", 1);

        subtask1.setId(1);
        subtask2.setId(1);

        assertEquals(subtask1, subtask2, "Subtasks should be equal if their IDs are the same");
    }

    @Test
    void testSubtaskInequalityById() {
        Subtask subtask1 = new Subtask("Subtask 1", "Description", 1);
        Subtask subtask2 = new Subtask("Subtask 2", "Description", 1);

        subtask1.setId(1);
        subtask2.setId(2);

        assertNotEquals(subtask1, subtask2, "Subtasks should not be equal if their IDs are different");
    }

    @Test
    void testEpicCannotBeAddedAsSubtask() {
        Epic epic = new Epic("Epic", "Description");
        epic.setId(1);

        // Пытаемся создать подзадачу, которая является эпиком
        Subtask subtask = new Subtask("Subtask", "Description", epic.getId());
        subtask.setId(2);

        // Проверяем, что такая подзадача не добавляется
        assertNotEquals(epic.getId(), subtask.getEpicId(), "Epic cannot be added as its own subtask");
    }

    @Test
    void testSubtaskCannotBeItsOwnEpic() {
        Subtask subtask = new Subtask("Subtask", "Description", 0);
        subtask.setId(1);

        // Пытаемся сделать подзадачу её же эпиком
        subtask.setEpicId(subtask.getId());

        assertNotEquals(subtask.getId(), subtask.getEpicId(), "Subtask cannot be its own epic");
    }

    @Test
    void testManagersReturnInitializedInstances() {
        TaskManager taskManager = Managers.getDefault();
        HistoryManager historyManager = Managers.getDefaultHistory();

        assertNotNull(taskManager, "TaskManager should be initialized");
        assertNotNull(historyManager, "HistoryManager should be initialized");
    }

    @Test
    void testTaskManagerAddsAndFindsTasks() {
        InMemoryTaskManager taskManager = new InMemoryTaskManager();

        Task task = new Task("Task 1", "Description", TaskStatus.NEW);
        int taskId = taskManager.addNewTask(task);

        Task retrievedTask = taskManager.getTask(taskId);

        assertNotNull(retrievedTask, "Task should be found by ID");
        assertEquals(taskId, retrievedTask.getId(), "Task ID should match");
    }

    @Test
    void testTaskManagerAddsAndFindsEpic() {
        InMemoryTaskManager taskManager = new InMemoryTaskManager();

        Epic epic = new Epic("Epic 1", "Epic description");
        int epicId = taskManager.addNewEpic(epic);

        Epic retrievedEpic = taskManager.getEpic(epicId);

        assertNotNull(retrievedEpic, "Epic should be found by ID");
        assertEquals(epicId, retrievedEpic.getId(), "Epic ID should match");
    }

    @Test
    void testTaskManagerAddsAndFindsSubtask() {
        InMemoryTaskManager taskManager = new InMemoryTaskManager();

        Epic epic = new Epic("Epic 1", "Epic description");
        int epicId = taskManager.addNewEpic(epic);

        Subtask subtask = new Subtask("Subtask 1", "Subtask description", epicId);
        int subtaskId = taskManager.addNewSubtask(subtask);

        Subtask retrievedSubtask = taskManager.getSubtask(subtaskId);

        assertNotNull(retrievedSubtask, "Subtask should be found by ID");
        assertEquals(subtaskId, retrievedSubtask.getId(), "Subtask ID should match");
    }

    @Test
    void testNoIdConflict() {
        InMemoryTaskManager taskManager = new InMemoryTaskManager();

        Task task1 = new Task("Task 1", "Description", TaskStatus.NEW);
        int task1Id = taskManager.addNewTask(task1);

        Task task2 = new Task("Task 2", "Description", TaskStatus.NEW);
        int task2Id = taskManager.addNewTask(task2);

        assertNotEquals(task1Id, task2Id, "Task IDs should not conflict");
    }

    @Test
    void testTaskImmutableAfterAdding() {
        InMemoryTaskManager taskManager = new InMemoryTaskManager();

        Task task = new Task("Task 1", "Description", TaskStatus.NEW);
        int taskId = taskManager.addNewTask(task);

        Task retrievedTask = taskManager.getTask(taskId);

        // Проверяем, что после добавления в менеджер задача не изменилась
        assertEquals(task.getName(), retrievedTask.getName(), "Task name should remain unchanged");
        assertEquals(task.getDescription(), retrievedTask.getDescription(), "Task description should remain unchanged");
        assertEquals(task.getStatus(), retrievedTask.getStatus(), "Task status should remain unchanged");
    }

    @Test
    void testHistoryManagerSavesPreviousVersion() {
        InMemoryHistoryManager historyManager = new InMemoryHistoryManager();

        Task task = new Task("Task 1", "Description", TaskStatus.NEW);
        historyManager.add(task);

        Task updatedTask = new Task("Task 1", "Updated Description", TaskStatus.IN_PROGRESS);
        historyManager.add(updatedTask);

        List<Task> history = historyManager.getHistory();

        assertEquals(2, history.size(), "History should contain both tasks");
        assertEquals(task.getId(), history.get(0).getId(), "History should contain the original task");
        assertEquals(updatedTask.getId(), history.get(1).getId(), "History should contain the updated task");
    }

    @Test
    void testHistoryManagerAddAndRemove() {
        InMemoryHistoryManager historyManager = new InMemoryHistoryManager();

        Task task1 = new Task("Task 1", "Description", TaskStatus.NEW);
        task1.setId(1);
        historyManager.add(task1);

        Task task2 = new Task("Task 2", "Description", TaskStatus.IN_PROGRESS);
        task2.setId(2);
        historyManager.add(task2);

        assertEquals(2, historyManager.getHistory().size(), "History should contain 2 tasks");

        historyManager.remove(1);
        assertEquals(1, historyManager.getHistory().size(), "History should contain 1 task after removal");
        assertNull(historyManager.getHistory().stream().filter(task -> task.getId() == 1).findFirst().orElse(null), "Task 1 should be removed");
    }

    @Test
    void testTaskHistoryIsUpdatedOnAdd() {
        InMemoryHistoryManager historyManager = new InMemoryHistoryManager();

        Task task1 = new Task("Task 1", "Description", TaskStatus.NEW);
        task1.setId(1);
        historyManager.add(task1);

        Task task2 = new Task("Task 2", "Description", TaskStatus.IN_PROGRESS);
        task2.setId(2);
        historyManager.add(task2);

        Task task1Updated = new Task("Task 1 Updated", "Updated Description", TaskStatus.DONE);
        task1Updated.setId(1);
        historyManager.add(task1Updated);

        assertEquals(2, historyManager.getHistory().size(), "History should contain 2 tasks after update");
        assertEquals(1, historyManager.getHistory().get(0).getId(), "Task 1 should be the first in history after update");
        assertEquals(2, historyManager.getHistory().get(1).getId(), "Task 2 should remain second in history");
    }

    @Test
    void testHistoryDoesNotContainDuplicateTasks() {
        InMemoryHistoryManager historyManager = new InMemoryHistoryManager();

        Task task1 = new Task("Task 1", "Description", TaskStatus.NEW);
        task1.setId(1);
        historyManager.add(task1);

        Task task2 = new Task("Task 2", "Description", TaskStatus.IN_PROGRESS);
        task2.setId(2);
        historyManager.add(task2);

        historyManager.add(task1);

        assertEquals(2, historyManager.getHistory().size(), "History should not contain duplicates");
    }



        @Test
        void testSaveAndLoad() throws IOException {
            // Создаем временный файл
            File tempFile = File.createTempFile("tasks", ".csv");
            tempFile.deleteOnExit();

            // Создаем FileBackedTaskManager через фабрику
            FileBackedTaskManager manager = (FileBackedTaskManager) Managers.getFileBackedManager(tempFile);
            Task task1 = new Task("Task 1", "Description", TaskStatus.NEW);
            manager.addNewTask(task1);

            Epic epic = new Epic("Epic 1", "Epic Description");
            manager.addNewEpic(epic);

            Subtask subtask = new Subtask("Subtask 1", "Subtask Description", epic.getId());
            manager.addNewSubtask(subtask);

            // Сохраняем в файл
            manager.save();

            // Загружаем задачи из файла
            FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);

            // Проверяем, что задачи корректно загружены
            assertEquals(3, loadedManager.getTasks().size() + loadedManager.getEpics().size() + loadedManager.getSubtasks().size(), "Loaded tasks count should match");
            assertNotNull(loadedManager.getTask(task1.getId()), "Task 1 should be loaded");
            assertNotNull(loadedManager.getEpic(epic.getId()), "Epic 1 should be loaded");
            assertNotNull(loadedManager.getSubtask(subtask.getId()), "Subtask 1 should be loaded");
        }

        @Test
        void testLoadEmptyFile() throws IOException {
            // Создаем пустой временный файл
            File tempFile = File.createTempFile("emptyTasks", ".csv");
            tempFile.deleteOnExit();

            // Загружаем пустой файл
            FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);

            // Проверяем, что менеджер пуст
            assertTrue(loadedManager.getTasks().isEmpty(), "Tasks should be empty");
            assertTrue(loadedManager.getEpics().isEmpty(), "Epics should be empty");
            assertTrue(loadedManager.getSubtasks().isEmpty(), "Subtasks should be empty");
        }

        @Test
        void testLoadMultipleTasksFromFile() throws IOException {
            // Создаем временный файл
            File tempFile = File.createTempFile("multipleTasks", ".csv");
            tempFile.deleteOnExit();

            // Создаем FileBackedTaskManager через фабрику и добавляем несколько задач
            FileBackedTaskManager manager = (FileBackedTaskManager) Managers.getFileBackedManager(tempFile);
            Task task1 = new Task("Task 1", "Description", TaskStatus.NEW);
            manager.addNewTask(task1);

            Epic epic = new Epic("Epic 1", "Epic Description");
            manager.addNewEpic(epic);

            Subtask subtask = new Subtask("Subtask 1", "Subtask Description", epic.getId());
            manager.addNewSubtask(subtask);

            // Сохраняем в файл
            manager.save();

            // Загружаем задачи из файла
            FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);

            // Проверяем, что все задачи корректно загружены
            assertEquals(1, loadedManager.getTasks().size(), "One task should be loaded");
            assertEquals(1, loadedManager.getEpics().size(), "One epic should be loaded");
            assertEquals(1, loadedManager.getSubtasks().size(), "One subtask should be loaded");
        }

        @Test
        void testTaskEqualityAfterLoad() throws IOException {
            // Создаем временный файл
            File tempFile = File.createTempFile("taskEquality", ".csv");
            tempFile.deleteOnExit();

            // Создаем FileBackedTaskManager через фабрику и добавляем задачи
            FileBackedTaskManager manager = (FileBackedTaskManager) Managers.getFileBackedManager(tempFile);
            Task task1 = new Task("Task 1", "Description", TaskStatus.NEW);
            manager.addNewTask(task1);

            Epic epic = new Epic("Epic 1", "Epic Description");
            manager.addNewEpic(epic);

            Subtask subtask = new Subtask("Subtask 1", "Subtask Description", epic.getId());
            manager.addNewSubtask(subtask);

            // Сохраняем в файл
            manager.save();

            // Загружаем задачи из файла
            FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);

            // Проверяем идентичность задач
            Task loadedTask = loadedManager.getTask(task1.getId());
            assertNotNull(loadedTask, "Loaded task should not be null");
            assertEquals(task1.getName(), loadedTask.getName(), "Task names should match");
            assertEquals(task1.getDescription(), loadedTask.getDescription(), "Task descriptions should match");
            assertEquals(task1.getStatus(), loadedTask.getStatus(), "Task statuses should match");

            Epic loadedEpic = loadedManager.getEpic(epic.getId());
            assertNotNull(loadedEpic, "Loaded epic should not be null");
            assertEquals(epic.getName(), loadedEpic.getName(), "Epic names should match");
            assertEquals(epic.getDescription(), loadedEpic.getDescription(), "Epic descriptions should match");

            Subtask loadedSubtask = loadedManager.getSubtask(subtask.getId());
            assertNotNull(loadedSubtask, "Loaded subtask should not be null");
            assertEquals(subtask.getName(), loadedSubtask.getName(), "Subtask names should match");
            assertEquals(subtask.getDescription(), loadedSubtask.getDescription(), "Subtask descriptions should match");
            assertEquals(subtask.getEpicId(), loadedSubtask.getEpicId(), "Subtask epic IDs should match");
        }

        @Test
        void testNoIdConflictAfterLoad() throws IOException {
            // Создаем временный файл
            File tempFile = File.createTempFile("noIdConflict", ".csv");
            tempFile.deleteOnExit();

            // Создаем FileBackedTaskManager через фабрику и добавляем задачи
            FileBackedTaskManager manager = (FileBackedTaskManager) Managers.getFileBackedManager(tempFile);
            Task task1 = new Task("Task 1", "Description", TaskStatus.NEW);
            int task1Id = manager.addNewTask(task1);

            Task task2 = new Task("Task 2", "Description", TaskStatus.NEW);
            int task2Id = manager.addNewTask(task2);

            // Сохраняем в файл
            manager.save();

            // Загружаем задачи из файла
            FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);

            // Проверяем отсутствие конфликтов ID
            assertNotEquals(task1Id, task2Id, "Task IDs should not conflict");
            assertNotEquals(loadedManager.getTask(task1Id).getId(), loadedManager.getTask(task2Id).getId(), "Loaded tasks IDs should not conflict");
        }
    @Test
    void testEpicStatusWithAllNewSubtasks() {
        Epic epic = new Epic("Epic", "Description");
        InMemoryTaskManager taskManager = new InMemoryTaskManager();
        int epicId = taskManager.addNewEpic(epic);

        Subtask subtask1 = new Subtask("Subtask 1", "Description", epicId);
        Subtask subtask2 = new Subtask("Subtask 2", "Description", epicId);

        taskManager.addNewSubtask(subtask1);
        taskManager.addNewSubtask(subtask2);

        assertEquals(TaskStatus.NEW, taskManager.getEpic(epicId).getStatus(), "Epic status should be NEW when all subtasks are NEW");
    }

    @Test
    void testEpicStatusWithAllDoneSubtasks() {
        Epic epic = new Epic("Epic", "Description");
        InMemoryTaskManager taskManager = new InMemoryTaskManager();
        int epicId = taskManager.addNewEpic(epic);

        Subtask subtask1 = new Subtask("Subtask 1", "Description", epicId);
        subtask1.setStatus(TaskStatus.DONE);
        Subtask subtask2 = new Subtask("Subtask 2", "Description", epicId);
        subtask2.setStatus(TaskStatus.DONE);

        taskManager.addNewSubtask(subtask1);
        taskManager.addNewSubtask(subtask2);

        assertEquals(TaskStatus.DONE, taskManager.getEpic(epicId).getStatus(), "Epic status should be DONE when all subtasks are DONE");
    }

    @Test
    void testEpicStatusWithMixedSubtasks() {
        Epic epic = new Epic("Epic", "Description");
        InMemoryTaskManager taskManager = new InMemoryTaskManager();
        int epicId = taskManager.addNewEpic(epic);

        Subtask subtask1 = new Subtask("Subtask 1", "Description", epicId);
        subtask1.setStatus(TaskStatus.NEW);
        Subtask subtask2 = new Subtask("Subtask 2", "Description", epicId);
        subtask2.setStatus(TaskStatus.DONE);

        taskManager.addNewSubtask(subtask1);
        taskManager.addNewSubtask(subtask2);

        assertEquals(TaskStatus.IN_PROGRESS, taskManager.getEpic(epicId).getStatus(), "Epic status should be IN_PROGRESS when subtasks are mixed NEW and DONE");
    }

    @Test
    void testEpicStatusWithAllInProgressSubtasks() {
        Epic epic = new Epic("Epic", "Description");
        InMemoryTaskManager taskManager = new InMemoryTaskManager();
        int epicId = taskManager.addNewEpic(epic);

        Subtask subtask1 = new Subtask("Subtask 1", "Description", epicId);
        subtask1.setStatus(TaskStatus.IN_PROGRESS);
        Subtask subtask2 = new Subtask("Subtask 2", "Description", epicId);
        subtask2.setStatus(TaskStatus.IN_PROGRESS);

        taskManager.addNewSubtask(subtask1);
        taskManager.addNewSubtask(subtask2);

        assertEquals(TaskStatus.IN_PROGRESS, taskManager.getEpic(epicId).getStatus(), "Epic status should be IN_PROGRESS when all subtasks are IN_PROGRESS");
    }
    @Test
    void testFileLoadException() {
        assertThrows(IOException.class, () -> {
            FileBackedTaskManager.loadFromFile(new File("nonexistent.csv"));
        }, "Loading from a non-existent file should throw IOException");
    }

    @Test
    void testSaveDoesNotThrowException() {
        assertDoesNotThrow(() -> {
            FileBackedTaskManager manager = Managers.getFileBackedManager(new File("tasks.csv"));
            manager.save();
        }, "Saving should not throw exceptions");
    }

    }





