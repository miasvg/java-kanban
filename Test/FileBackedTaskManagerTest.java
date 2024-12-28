package Test;
import org.junit.jupiter.api.Test;
import src.Models.Task;
import src.Models.TaskStatus;
import src.Service.FileBackedTaskManager;
import java.io.File;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {
    @Override
    protected FileBackedTaskManager createTaskManager() {
        return new FileBackedTaskManager(new File("test_tasks.csv"));
    }

    @Test
    void testSaveAndLoad() {
        Task task = new Task("Task 1", "Description 1", TaskStatus.NEW);
        int id = taskManager.addNewTask(task);

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(new File("test_tasks.csv"));
        Task loadedTask = loadedManager.getTask(id);

        assertNotNull(loadedTask);
        assertEquals(task, loadedTask);
    }
}