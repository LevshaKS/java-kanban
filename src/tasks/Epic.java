package tasks;

import util.Status;
import util.Type;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class Epic extends Task {

    private final ArrayList<Integer> listSubTaskId;

    private LocalDateTime endTime;


    public Epic(String name, String description) {
        super(name, description);
        this.status = Status.NEW;
        this.listSubTaskId = new ArrayList<>();
        this.type = Type.EPIC;
        this.startTime = LocalDateTime.of(0000, 01, 01, 01, 01, 00);
        this.duration = 0;
        this.endTime = LocalDateTime.of(9999, 01, 01, 01, 01, 00);
    }

    public Epic(int id, Type type, String name, Status status, String description) {
        super(id, type, name, status, description);
        this.listSubTaskId = new ArrayList<>();
    }


    public Epic(int id, Type type, String name, Status status, String description, LocalDateTime startTime, long duration) {
        super(id, type, name, status, description, startTime, duration);
        this.listSubTaskId = new ArrayList<>();
    }

    public void setListSubTask(int id) {
        listSubTaskId.add(id);
    }

    public ArrayList<Integer> getSubTaskList() {
        return listSubTaskId;
    }

    public void removeValueListSubTask(Integer value) {
        listSubTaskId.remove(value);
    }

    public void clearListSubTask() {
        if (!listSubTaskId.isEmpty())
            listSubTaskId.clear();
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    @Override
    public String toString() {
        return "Epic{" +
                "type='" + type + '\'' +
                ", name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", id=" + getId() +
                ", status=" + status +
                ", listSubTaskId=" + listSubTaskId.toString() +
                ", startTime=" + startTime.format(getFormatter()) +
                ", duration=" + duration +
                "}" + '\n';
    }
}
