public class SubTask extends Task {
    private int epicId;

    SubTask(String name, String description, int epicId) {
        super(name, description);
        this.epicId = epicId;
        status = Status.NEW;
    }

    public int getEpicId() {
        return epicId;
    }

    @Override
    public String toString() {
        return "SubTask{" +
                "name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", id=" + getId() +
                ", status=" + status +
                ", epicId=" + epicId +
                "} " + '\n';
    }
}
