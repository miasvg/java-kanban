package src.Models;

import src.Service.TaskType;

import java.util.Objects;

public class Task {
    protected String name;
    protected String description;
    protected int id;
    protected  TaskStatus status;
    protected TaskType type;


    public Task(String name, String description, TaskStatus aNew) {
        this.status = TaskStatus.NEW;

    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public TaskType getType(){return type;}

    public Task() {
    }

    public int getId() {
        return id;
    }

    public TaskStatus getStatus() {
        return status;

    }

    public void setId(int id) {
        this.id = id;
    }

    public void setStatus(TaskStatus taskStatus) {
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                '}';
    }

    public void setType(TaskType taskType) {
        this.type = type;
    }
}