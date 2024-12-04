package src.Models;

import java.util.ArrayList;

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
        return "src.Models.Epic{" +
                "id=" + getId() +
                ", name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", status=" + getStatus() +
                '}';
    }


}
