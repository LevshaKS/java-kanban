package tasks;

import util.Status;
import util.Type;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class Task {
    protected String name;
    protected String description;
    protected int id;
    protected Status status;
    protected Type type;
    protected LocalDateTime startTime;

    protected long duration;  // minuts

    protected DateTimeFormatter getFormatter() {
        return DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss");
    }
    //  DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss");


    public Task(String name, String description) {
        this.name = name;
        this.description = description;
        this.status = Status.NEW;
        this.type = Type.TASK;
    }


    public Task(Task task) {
        this.name = task.getName();
        this.description = task.getDescription();
        this.status = task.getStatus();
        this.id = task.getId();
        this.type = task.getType();
    }

    public Task(int id, Type type, String name, Status status, String description) {
        this.id = id;
        this.type = type;
        this.name = name;
        this.status = status;
        this.description = description;
    }

    public Task(String name, String description, LocalDateTime startTime, long duration) {

        this.name = name;
        this.status = Status.NEW;
        this.description = description;
        this.startTime = startTime;
        this.duration = duration;
        this.type = Type.TASK;
    }

    public Task(int id, Type type, String name, Status status, String description, LocalDateTime startTime, long duration) {
        this.id = id;
        this.type = type;
        this.name = name;
        this.status = status;
        this.description = description;
        this.startTime = startTime;
        this.duration = duration;
    }


    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Status getStatus() {
        return status;
    }

    public Type getType() {
        return type;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }


    public LocalDateTime getStartTime() {
        return startTime;
    }

    public long getDuration() {
        return duration;
    }

    public LocalDateTime getEndTime() {
        return startTime.plusMinutes(duration);
    }

    public String toStringInFile() {
        return String.format("%s,%s,%s,%s,%s,%s,%s", id, type, name, status, description, startTime.format(getFormatter()), duration);
    }

    @Override
    public String toString() {
        return "Task{" +
                "type='" + type + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", id=" + id +
                ", status=" + status +
                ", startTime=" + startTime.format(getFormatter()) +
                ", duration=" + duration +
                "}" + '\n';
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        Task task = (Task) object;
        return Objects.equals(name, task.name) && Objects.equals(description, task.description) && Objects.equals(status, task.status);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description, status);
    }

}


