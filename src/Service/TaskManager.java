package Service;


import Models.Epic;
import Models.Subtask;
import Models.Task;

import java.util.List;

public interface TaskManager {
    List<Task> getTasks();
    List<Subtask> getSubtasks();
    List<Epic> getEpics();
    List<Subtask> getEpicSubtasks(int epicId);
    Task getTask(int id);
    Subtask getSubtask(int id);
    Epic getEpic(int id);
    int addNewTask(Task task);
    int addNewEpic(Epic epic);
    int addNewSubtask(Subtask subtask);
    void updateTask(Task task);
    void updateEpic(Epic epic);
    void updateSubtask(Subtask subtask);
    void updateStatus(Epic epic);
    void deleteTask(int id);
    void deleteEpic(int id);
    void deleteSubtask(int id);
    void deleteTasks();
    void deleteSubtasks();
    void deleteEpics();

}