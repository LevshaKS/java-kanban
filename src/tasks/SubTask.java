package tasks;

import util.Status;
import util.Type;

import java.time.LocalDateTime;

public class SubTask extends Task {
    private final int epicId;

    public SubTask(String name, String description, int epicId) {
        super(name, description);
        this.epicId = epicId;
        status = Status.NEW;
        this.type = Type.SUBTUSK;
    }

    public SubTask(int id, Type type, String name, Status status, String description, int epicId) {
        super(id, type, name, status, description);
        this.epicId = epicId;
    }

    public SubTask(String name, String description, int epicId, LocalDateTime startTime, long duration) {
        super(name, description, startTime, duration);
        this.epicId = epicId;
        status = Status.NEW;
        this.type = Type.SUBTUSK;
    }

    public SubTask(int id, Type type, String name, Status status, String description, LocalDateTime startTime, long duration, int epicId) {
        super(id, type, name, status, description, startTime, duration);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }

    @Override
    public String toStringInFile() {
        return String.format("%s,%s,%s,%s,%s,%s,%s,%s", id, type, name, status, description, startTime.format(formatter), duration, epicId);
    }

    @Override
    public String toString() {
        return "SubTask{" +
                "type='" + type + '\'' +
                ", name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", id=" + getId() +
                ", status=" + status +
                ", startTime=" + startTime.format(formatter) +
                ", duration=" + duration +
                ", epicId=" + epicId +
                "} " + '\n';
    }
}
