package src.Service;


import src.Models.Epic;
import src.Models.Subtask;
import src.Models.Task;
import src.Models.TaskStatus;


import java.io.*;
import java.nio.file.*;
import java.util.*;


public class FileBackedTaskManager extends InMemoryTaskManager {


    private final File file;

    public FileBackedTaskManager(File file) {
        this.file = file;
    }

    // Метод для преобразования задачи в строку
    public String toString(Task task) {
        return task.toString();
    }

    // Метод для восстановления задачи из строки
    public Task fromString(String line) {

        String[] parts = line.split(",");
        int id = Integer.parseInt(parts[0]);
        TaskType type = TaskType.valueOf(parts[1]);
        String name = parts[2];
        TaskStatus status = TaskStatus.valueOf(parts[3]);
        String description = parts[4];
        int epicId = (parts.length > 5 && !parts[5].isEmpty()) ? Integer.parseInt(parts[5]) : -1;

        return switch (type) {
            case TASK ->  new Task(name, description, status);
            case EPIC ->  new Epic(name, description);
            case SUBTASK ->   new Subtask(name, description, epicId);
        };
    }

    // Метод для автосохранения
    public void save() {
        try (BufferedWriter writer = Files.newBufferedWriter(file.toPath())) {
            writer.write("id,type,name,status,description,epic\n");

            for (Task task : getTasks()) {
                writer.write(toString(task));
                writer.newLine();
            }

            for (Epic epic : getEpics()) {
                writer.write(toString(epic));
                writer.newLine();
            }

            for (Subtask subtask : getSubtasks()) {
                writer.write(toString(subtask));
                writer.newLine();
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Error saving tasks to file", e);
        }
    }


    @Override
    public int addNewTask(Task task) {
        int id = super.addNewTask(task);
        save();
        return id;
    }


    @Override
    public int addNewEpic(Epic epic) {
        int id = super.addNewEpic(epic);
        save();
        return id;
    }


    @Override
    public int addNewSubtask(Subtask subtask) {
        int id = super.addNewSubtask(subtask);
        save();
        return id;
    }
    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }

    @Override
    public void deleteTask(int id) {
        super.deleteTask(id);
        save();
    }

    @Override
    public void deleteEpic(int id) {
        super.deleteEpic(id);
        save();
    }

    @Override
    public void deleteSubtask(int id) {
        super.deleteSubtask(id);
        save();
    }


    //  метод для загрузки данных из файла
    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager manager = new FileBackedTaskManager(file);


        try {
            // Считываем все строки из файла
            List<String> lines = Files.readAllLines(file.toPath());


            // Пропускаем заголовок (первая строка)
            for (String line : lines.subList(1, lines.size())) {
                // Преобразуем строку в объект задачи
                Task task = manager.fromString(line);


                // В зависимости от типа задачи добавляем её в правильный список
                switch (task.getType()) {
                    case EPIC -> manager.addNewEpic((Epic) task);
                    case SUBTASK -> manager.addNewSubtask((Subtask) task);
                    case TASK -> manager.addNewTask(task);
                    default -> throw new IllegalArgumentException("Unknown task type: " + task.getType());
                }
            }

        } catch (IOException e) {
            throw new ManagerSaveException("Error loading tasks from file", e);
        }

        return manager;
    }
}

