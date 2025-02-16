package managers;

import tasks.Epic;
import tasks.SubTask;
import tasks.Task;
import util.Status;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryTaskManager implements TaskManager {

    public int id;

    private final HashMap<Integer, Task> taskMaps;
    private final HashMap<Integer, Epic> epicMaps;
    private final HashMap<Integer, SubTask> subTaskMaps;
    private final HistoryManager historyManager;



    public InMemoryTaskManager(HistoryManager historyManager) {
        taskMaps = new HashMap<>();
        epicMaps = new HashMap<>();
        subTaskMaps = new HashMap<>();
        this.historyManager = historyManager;
    }

    @Override
    public int countId() {       // уникалньый идентификатор
        return ++id;
    }

    // методы task
    @Override
    public void newTack(Task task) { //метод новый task
        if (task != null) {
            task.setId(countId());
            taskMaps.put(task.getId(), task);
        }
    }

    @Override
    public ArrayList<Task> getTasks() {      //вывод всего списка task
        return new ArrayList<>(taskMaps.values());
    }

    @Override
    public void clearTask() {       // очистить весь список task
        for (int id : taskMaps.keySet()) {
            historyManager.remove(id);
        }
        taskMaps.clear();
    }

    @Override
    public Task getToIdTask(int id) {        // получение task по id
        historyManager.add(taskMaps.get(id));
        return taskMaps.get(id);
    }

    @Override
    public void removeToIdTask(int id) {        // удаление task по id
        taskMaps.remove(id);
        historyManager.remove(id);
    }

    @Override
    public void updateTask(int id, Task task) {     // передача обновленого task по id
        if (id != 0 && task != null && taskMaps.containsKey(id))
            taskMaps.put(id, task);
    }

    // методы epic
    @Override
    public void newEpic(Epic epic) {    //создание нового epic
        if (epic != null) {
            epic.setId(countId());
            epicMaps.put(epic.getId(), epic);
        }
    }

    @Override
    public ArrayList<Epic> getEpic() {        //вывод списка epic
        return new ArrayList<>(epicMaps.values());
    }

    @Override
    public Epic getToIdEpic(int id) {        // получение epic по id
        historyManager.add(epicMaps.get(id));
        return epicMaps.get(id);
    }

    @Override
    public ArrayList<SubTask> getToIdSubtaskInEpic(int id) { ///получение списка subtask по id epic
        ArrayList<SubTask> subTasks = new ArrayList<>();
        if (epicMaps.containsKey(id)) {
            ArrayList<Integer> subTaskList = epicMaps.get(id).getSubTaskList();
            for (Integer idSubTask : subTaskList) {
                subTasks.add(subTaskMaps.get(idSubTask));
            }
        }
        return subTasks;
    }

    @Override
    public void removeToIdEpic(int id) {        // удаление epic по id
        if (epicMaps.containsKey(id)) {
            ArrayList<Integer> subTaskList = epicMaps.get(id).getSubTaskList();
            for (int subTask : subTaskList) {
                subTaskMaps.remove(subTask);
                historyManager.remove(subTask);
            }
        }
        epicMaps.remove(id);
        historyManager.remove(id);
    }

    @Override
    public void clearEpic() {           // очиска списка epic
        clearSubTask();
        for (int id : epicMaps.keySet()) {
            historyManager.remove(id);
        }
        epicMaps.clear();

    }

    //метод subtask
    @Override
    public ArrayList<SubTask> getSubTask() {  //вывод списка subtask
        return new ArrayList<>(subTaskMaps.values());
    }

    @Override
    public void newSubTask(SubTask subTask) {    //создание новой subtask
        if (subTask != null) {
            int id = countId();
            subTask.setId(id);
            subTaskMaps.put(subTask.getId(), subTask);
            epicMaps.get(subTask.getEpicId()).setListSubTask(id);
            calculateStatusEpic(subTask.getEpicId());
        }
    }

    @Override
    public void clearSubTask() { // очистка списка subtask
        for (Epic epics : epicMaps.values()) {
            epics.clearListSubTask();
            calculateStatusEpic(epics.getId());
        }
        for (int id : subTaskMaps.keySet()) {
            historyManager.remove(id);
        }
        subTaskMaps.clear();
    }

    @Override
    public SubTask getToIdSubTask(int id) { //получение subtask по id
        historyManager.add(subTaskMaps.get(id));
        return subTaskMaps.get(id);
    }

    @Override
    public void removeToIdSubTask(int id) {        // удаление subtask по id
        if (subTaskMaps.containsKey(id)) {
            int epicid = subTaskMaps.get(id).getEpicId();
            epicMaps.get(epicid).removeValueListSubTask(id);
            subTaskMaps.remove(id);
            historyManager.remove(id);
            calculateStatusEpic(epicid);
        }
    }

    @Override
    public void updateSubTask(int id, SubTask subTask) {     // передача обновленой subtask по id
        if (id != 0 && subTask != null && subTaskMaps.containsKey(id)) {
            subTaskMaps.put(id, subTask);
            calculateStatusEpic(subTask.getEpicId());
        }
    }

    @Override
    public void calculateStatusEpic(int epicId) {     //расчет статуса epic
        if (id != 0 && epicMaps.containsKey(epicId)) {
            ArrayList<Integer> listSubTask = epicMaps.get(epicId).getSubTaskList();
            int count = 0;
            if (listSubTask.isEmpty()) {
                epicMaps.get(epicId).setStatus(Status.NEW);
                return;
            }

            if (listSubTask.size() == 1) {
                epicMaps.get(epicId).setStatus(subTaskMaps.get(listSubTask.get(0)).getStatus());
                return;
            }

            for (Integer listId : listSubTask) {
                if (subTaskMaps.get(listId).getStatus() != Status.NEW)
                    epicMaps.get(epicId).setStatus(Status.IN_PROGRESS);
                if (subTaskMaps.get(listId).getStatus() == Status.DONE)
                    count++;
            }

            if (count > 0 && count < listSubTask.size()) {
                epicMaps.get(epicId).setStatus(Status.IN_PROGRESS);
            } else if (count == listSubTask.size()) {
                epicMaps.get(epicId).setStatus(Status.DONE);
                epicMaps.get(epicId).setStatus(Status.NEW);
            }
        }
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }
}
