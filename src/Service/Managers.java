package src.Service;

import java.io.File;

public class Managers {

    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }


    public static Object getFileBackedManager(File tempFile) {
        return new FileBackedTaskManager(tempFile);
    }
}
