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
        if (task == null)
            return;
        listHistory.addLast(new Task(task));
        if (listHistory.size() > 10)
            listHistory.removeFirst();
    }

    @Override
    public List<Task> getHistory() {
        return listHistory;
    }
}
