package src.Models;

import src.Service.TaskType;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public class Task {
    protected String name;
    protected String description;
    protected int id;
    protected TaskStatus status;
    protected TaskType type;
    protected Duration duration; // Продолжительность задачи
    protected LocalDateTime startTime;




    public Task(String name, String description, TaskStatus aNew) {
        this.status = TaskStatus.NEW;
        this.duration = Duration.ZERO;
        this.startTime = null;
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
    public TaskType getType() {return type;}
    public int getId() {return id;}
    public TaskStatus getStatus() {return status;}
    public void setId(int id) {
        this.id = id;
    }
    public void setStatus(TaskStatus taskStatus) {}
    public Duration getDuration() {return duration;}
    public void setDuration(Duration duration) {this.duration = duration;}
    public LocalDateTime getStartTime() {return startTime;}
    public void setStartTime(LocalDateTime startTime) {this.startTime = startTime;}
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
        return String.format("%d,%s,%s,%s,%s,%s,%s,",
                getId(),
                getType(),
                getName(),
                getStatus(),
                getDescription(),
        duration != null ? duration.toMinutes() : 0,
                startTime != null ? startTime : "null");
    }

    public void setType(TaskType taskType) {
        this.type = type;
    }
    public LocalDateTime getEndTime() {
        return (startTime != null && duration != null) ? startTime.plus(duration) : null;
    }
}
