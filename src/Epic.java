import java.util.ArrayList;

public class Epic extends Task {

    private final ArrayList<Integer> listSubTaskId;

    Epic(String name, String description) {
        super(name, description);
        this.status = Status.NEW;
        listSubTaskId = new ArrayList<>();
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

    @Override
    public String toString() {
        return "Epic{" +
                "name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", id=" + getId() +
                ", status=" + status +
                ", listSubTaskId=" + listSubTaskId.toString() +
                "}" + '\n';
    }
}
