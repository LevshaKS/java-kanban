package tasks;

import util.Status;
import util.Type;

import java.util.ArrayList;

public class Epic extends Task {

    private final ArrayList<Integer> listSubTaskId;

    public Epic(String name, String description) {
        super(name, description);
        this.status = Status.NEW;
        this.listSubTaskId = new ArrayList<>();
        this.type = Type.EPIC;
    }

    public Epic(int id, Type type, String name, Status status, String description) {
        super(id, type, name, status, description);
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
        listSubTaskId.clear();
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
                "}" + '\n';
    }
}
