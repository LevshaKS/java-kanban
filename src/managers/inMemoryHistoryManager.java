package managers;

import tasks.Task;

import java.util.ArrayList;
import java.util.List;

public class inMemoryHistoryManager implements HistoryManager {

    private final ArrayList<Task> listHistory;

    public inMemoryHistoryManager() {
        listHistory = new ArrayList<>();
    }

    @Override
    public void add(Task task) {
        if (listHistory.isEmpty())
            listHistory.addLast(new Task(task));
        else if (listHistory.size() < 10) {
            listHistory.addLast(new Task(task));
        } else {
            listHistory.remove(0);
            listHistory.addLast(new Task(task));
        }
    }

    @Override
    public List<Task> getHistory() {
        return listHistory;
    }
}
