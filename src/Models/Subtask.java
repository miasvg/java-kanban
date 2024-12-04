package src.Models;

public  class Subtask extends Task {
    private int epicId;


    public Subtask(String name, String description, int epicId) {
        super(name, description, TaskStatus.NEW);
        this.epicId = epicId;

    }

    public Subtask(String name, String description, TaskStatus status, int epicId) {
        super(name, description, status); // Передаем параметры родителю
        this.epicId = epicId; // Инициализируем epicId
    }



    // Геттер для получения эпика
    public int getEpicId() {
        return epicId;
    }
    public void setEpicId(int epicId) {
        this.epicId = epicId;
    }
    public void setId(int id){
        this.id = id;
    }

    @Override
    public String toString() {
        return "Subtask{" +
                "id=" + getId() +
                ", name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", status=" + getStatus() +
                ", epicId=" + epicId +
                '}';
    }

}
