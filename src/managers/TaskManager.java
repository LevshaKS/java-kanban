package managers;

import tasks.Epic;
import tasks.SubTask;
import tasks.Task;
import util.Status;

import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {

    public static int id;

    private final HashMap<Integer, Task> taskMaps;
    private final HashMap<Integer, Epic> epicMaps;
    private final HashMap<Integer, SubTask> subTaskMaps;

    public TaskManager() {
        taskMaps = new HashMap<>();
        epicMaps = new HashMap<>();
        subTaskMaps = new HashMap<>();
    }

    public int countId() {       // уникалньый идентификатор
        return ++id;
    }

    // методы task
    public void newTack(Task task) { //метод новый task
        if (task != null) {
            task.setId(countId());
            taskMaps.put(task.getId(), task);
        }
    }

    public ArrayList<Task> getTasks() {      //вывод всего списка task
        return new ArrayList<>(taskMaps.values());
    }

    public void clearTask() {       // очистить весь список task
        taskMaps.clear();
    }

    public Task getToIdTask(int id) {        // получение task по id
        return taskMaps.get(id);
    }

    public void removeToIdTask(int id) {        // удаление task по id
        taskMaps.remove(id);
    }

    public void updateTask(int id, Task task) {     // передача обновленого task по id
        if (id != 0 && task != null && taskMaps.containsKey(id))
            taskMaps.put(id, task);
    }

    // методы epic
    public void newEpic(Epic epic) {    //создание нового epic
        if (epic != null) {
            epic.setId(countId());
            epicMaps.put(epic.getId(), epic);
        }
    }

    public ArrayList<Epic> getEpic() {        //вывод списка epic
        return new ArrayList<>(epicMaps.values());
    }

    public Epic getToIdEpic(int id) {        // получение epic по id
        return epicMaps.get(id);
    }

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

    public void removeToIdEpic(int id) {        // удаление epic по id
        if (epicMaps.containsKey(id)) {
            ArrayList<Integer> subTaskList = epicMaps.get(id).getSubTaskList();
            for (int subTask : subTaskList) {
                subTaskMaps.remove(subTask);
            }
        }
        epicMaps.remove(id);
    }

    public void clearEpic() {           // очиска списка epic
        epicMaps.clear();
        clearSubTask();
    }

    //метод subtask
    public ArrayList<SubTask> getSubTask() {  //вывод списка subtask
        return new ArrayList<>(subTaskMaps.values());
    }

    public void newSubTask(SubTask subTask) {    //создание новой subtask
        if (subTask != null) {
            int id = countId();
            subTask.setId(id);
            subTaskMaps.put(subTask.getId(), subTask);
            epicMaps.get(subTask.getEpicId()).setListSubTask(id);
            calculateStatusEpic(subTask.getEpicId());
        }
    }

    public void clearSubTask() { // очистка списка subtask
        for (Epic epics : epicMaps.values()) {
            epics.clearListSubTask();
            calculateStatusEpic(epics.getId());
        }
        subTaskMaps.clear();
    }

    public SubTask getToIdSubTask(int id) { //получение subtask по id
        return subTaskMaps.get(id);
    }

    public void removeToIdSubTask(int id) {        // удаление subtask по id
        if (subTaskMaps.containsKey(id)) {
            int epicid = subTaskMaps.get(id).getEpicId();
            epicMaps.get(epicid).removeValueListSubTask(id);
            subTaskMaps.remove(id);
            calculateStatusEpic(epicid);
        }
    }

    public void updateSubTask(int id, SubTask subTask) {     // передача обновленой subtask по id
        if (id != 0 && subTask != null && subTaskMaps.containsKey(id)) {
            subTaskMaps.put(id, subTask);
            calculateStatusEpic(subTask.getEpicId());
        }
    }

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
}
