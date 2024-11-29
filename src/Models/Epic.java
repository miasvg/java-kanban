package Models;

import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    private final ArrayList<Subtask> subtasks;


    public Epic(String name, String description) {
        super(name, description, TaskStatus.NEW);
        this.subtasks = new ArrayList<>();

    }


    public void addSubtask(Subtask subtask) {
        subtasks.add(subtask);
    }


    public ArrayList<Subtask> getSubtasks() {
        return subtasks;
    }

    @Override
    public String toString() {
        return "Epic{" +
                "id=" + getId() +
                ", name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", status=" + getStatus() +
                '}';
    }


}