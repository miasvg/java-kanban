package src.Models;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;

public class Epic extends Task {
    private final ArrayList<Subtask> subtasks;
    private LocalDateTime endTime;

    public Epic(String name, String description) {
        super(name, description, TaskStatus.NEW);
        this.subtasks = new ArrayList<>();
        this.duration = Duration.ZERO;
        this.endTime = null;
        this.startTime = null;
    }
    private void recalculateFields() {
        this.duration = subtasks.stream()
                .filter(subtask -> subtask.getDuration() != null)
                .map(Subtask::getDuration)
                .reduce(Duration.ZERO, Duration::plus);

        this.startTime = subtasks.stream()
                .filter(subtask -> subtask.getStartTime() != null)
                .map(Subtask::getStartTime)
                .min(Comparator.naturalOrder())
                .orElse(null);

        this.endTime = subtasks.stream()
                .filter(subtask -> subtask.getEndTime() != null)
                .map(Subtask::getEndTime)
                .max(Comparator.naturalOrder())
                .orElse(null);
    }

    public void addSubtask(Subtask subtask) {
        subtasks.add(subtask);
    }
    public ArrayList<Subtask> getSubtasks() {
        return subtasks;
    }

    @Override
    public String toString() {
        return String.format("%s,%s,%s",
                super.toString(),
                duration.toMinutes(),
                endTime != null ? endTime : "null");
    }
}
