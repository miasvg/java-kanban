package src;
import org.junit.jupiter.api.Test;
import src.Models.Epic;
import src.Models.Subtask;
import src.Models.Task;
import src.Models.TaskStatus;
import src.Service.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class tests {
    @Test
    void testTaskEqualityById() {
        Task task1 = new Task("Task 1", "Description", TaskStatus.NEW);
        Task task2 = new Task("Task 1", "Description", TaskStatus.NEW);

        task1.setId(1);
        task2.setId(1);

        assertEquals(task1, task2, "Tasks should be equal if their IDs are the same");
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

        historyManager.add(task1); // Re-add task1

        assertEquals(2, historyManager.getHistory().size(), "History should not contain duplicates");
    }
}