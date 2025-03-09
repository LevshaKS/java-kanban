package managers;

import exceptions.ManagerException;
import tasks.Epic;
import tasks.SubTask;
import tasks.Task;
import util.Status;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class InMemoryTaskManager implements TaskManager {

    public int id;

    private final HashMap<Integer, Task> taskMaps;
    private final HashMap<Integer, Epic> epicMaps;
    private final HashMap<Integer, SubTask> subTaskMaps;
    private final HistoryManager historyManager;

    private final Set<Task> prioritizedTasks;

    private final Comparator<Task> comparator = Comparator.comparing(Task::getStartTime);


    public InMemoryTaskManager(HistoryManager historyManager) {
        taskMaps = new HashMap<>();
        epicMaps = new HashMap<>();
        subTaskMaps = new HashMap<>();
        this.historyManager = historyManager;
        prioritizedTasks = new TreeSet<>(comparator);
    }

    @Override
    public int countId() {       // уникалньый идентификатор
        return ++id;
    }

    // методы task
    @Override
    public void newTack(Task task) { //метод новый task
        if (task != null) {
            addPrioritizedTasks(task);
            task.setId(countId());
            taskMaps.put(task.getId(), task);
        }
    }

    @Override
    public ArrayList<Task> getTasks() {      //вывод всего списка task
        return new ArrayList<>(taskMaps.values());
    }

    @Override
    public void clearTaskList() {       // очистить весь список task
        for (int id : taskMaps.keySet()) {
            historyManager.remove(id);
            prioritizedTasks.remove(getToIdTask(id));
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
        if (taskMaps.containsKey(id)) {
            prioritizedTasks.remove(getToIdTask(id));
            taskMaps.remove(id);
            historyManager.remove(id);
        }
    }

    @Override
    public void updateTask(int id, Task task) {     // передача обновленого task по id
        if (id != 0 && task != null && taskMaps.containsKey(id)) {
            prioritizedTasks.remove(task);
            addPrioritizedTasks(task);
            taskMaps.put(id, task);
        }
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
        if (!epicMaps.containsKey(id))
            return null;
        // метод переписан через stream, оставил для себя чтобы помнить как было написано раньше
   /*     ArrayList<SubTask> subTasks = new ArrayList<>();
        if (epicMaps.containsKey(id)) {
            ArrayList<Integer> subTaskList = epicMaps.get(id).getSubTaskList();
            for (Integer idSubTask : subTaskList) {
                subTasks.add(subTaskMaps.get(idSubTask));
            }
        }
        return subTasks;*/
        return (ArrayList<SubTask>) epicMaps.get(id).getSubTaskList().stream()
                .map(integer -> subTaskMaps.get(integer))
                .collect(Collectors.toList());
    }

    @Override
    public void removeToIdEpic(int id) {        // удаление epic по id
        if (epicMaps.containsKey(id)) {        // метод переписан через stream, оставил для себя чтобы помнить как было написано раньше
          /*   ArrayList<Integer> subTaskList = epicMaps.get(id).getSubTaskList();
            for (int subTask : subTaskList) {
                prioritizedTasks.remove(getToIdSubTask(subTask));
                subTaskMaps.remove(subTask);
                historyManager.remove(subTask);
            }*/
            epicMaps.get(id).getSubTaskList()
                    .forEach(integer -> {
                        prioritizedTasks.remove(getToIdSubTask(integer));
                        subTaskMaps.remove(integer);
                        historyManager.remove(integer);
                    });
            historyManager.remove(id);
            epicMaps.remove(id);
        }
    }

    @Override
    public void clearEpicList() {           // очиска списка epic
        clearSubTaskList();
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
            addPrioritizedTasks(subTask);
            int id = countId(); //id subtask
            subTask.setId(id);
            subTaskMaps.put(subTask.getId(), subTask);
            int epicID = subTask.getEpicId();
            epicMaps.get(epicID).setListSubTask(id);
            calculateStatusEpic(epicID);
            calculateTimeSubTask(subTask);
        }
    }

    @Override
    public void clearSubTaskList() { // очистка списка subtask
        for (Epic epics : epicMaps.values()) {
            epics.clearListSubTask();
            calculateStatusEpic(epics.getId());
        }
        for (int id : subTaskMaps.keySet()) {
            historyManager.remove(id);
            prioritizedTasks.remove(getToIdSubTask(id));
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
            prioritizedTasks.remove(subTaskMaps.get(id));
            recalculateTimeEpic(subTaskMaps.get(id).getEpicId());
            subTaskMaps.remove(id);
            historyManager.remove(id);
            calculateStatusEpic(epicid);
        }
    }

    @Override
    public void updateSubTask(int id, SubTask subTask) {     // передача обновленой subtask по id
        if (id != 0 && subTask != null && subTaskMaps.containsKey(id)) {
            List<Task> tempPrioritizedTasks = new ArrayList<>(prioritizedTasks);
            prioritizedTasks.clear();
            tempPrioritizedTasks
                    .forEach(task -> {
                        if (task.getId() != id)
                            addPrioritizedTasks(task);
                    });
            addPrioritizedTasks(subTask);
            subTaskMaps.put(id, subTask);
            calculateStatusEpic(subTask.getEpicId());
            recalculateTimeEpic(subTask.getEpicId());
        }
    }

    public void recalculateTimeEpic(int epicId) { //перерасчет времени епика при обновление подзадачи
        Epic epic = epicMaps.get(epicId);
        epic.setStartTime(LocalDateTime.MAX);
        epic.setEndTime(LocalDateTime.MIN);
        epic.setDuration(0);
        epic.getSubTaskList()
                .forEach(integer -> calculateTimeSubTask(subTaskMaps.get(integer)));
    }

    public void calculateTimeSubTask(SubTask subTask) { //расчет времени епика
        int epicID = subTask.getEpicId();
        if (subTask.getStartTime().isBefore(epicMaps.get(epicID).getStartTime()))
            epicMaps.get(epicID).setStartTime(subTask.getStartTime());
        if (subTask.getStartTime().plusMinutes(subTask.getDuration()).isAfter(epicMaps.get(epicID).getEndTime()))
            epicMaps.get(epicID).setEndTime(subTask.getStartTime().plusMinutes(subTask.getDuration()));
        epicMaps.get(epicID).setDuration(epicMaps.get(epicID).getDuration() + subTask.getDuration());
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
            }
        }
    }

    @Override
    public List<Task> getHistory() { //вывод истории
        return historyManager.getHistory();
    }

    public void addPrioritizedTasks(Task task) {  //добавление задачи/подзадачи в список по времени
        if (!(task.getStartTime() == null)) {
            if (!validity(task))
                prioritizedTasks.add(task);
            else throw new ManagerException("Пересечение времени " + task);
        }
    }

    public Boolean validity(Task task) {        // валидатность при добавление в список задачи/поздадачи
        LocalDateTime startTaskTime = task.getStartTime();
        LocalDateTime endTaskTime = task.getEndTime();
        if (prioritizedTasks.isEmpty())
            return false;
        else {
            return getPrioritizedTasks().stream()
                    .anyMatch(task1 -> {
                        return (task1.getStartTime().isBefore(startTaskTime) && task1.getEndTime().isAfter(endTaskTime)) ||
                                (task1.getStartTime().isAfter(startTaskTime) && task1.getEndTime().isBefore(endTaskTime)) ||
                                (task1.getStartTime().isBefore(startTaskTime) && task1.getEndTime().isAfter(startTaskTime)) ||
                                (task1.getStartTime().isBefore(endTaskTime) && task1.getEndTime().isAfter(endTaskTime));
                    });
        }
    }

    public List<Task> getPrioritizedTasks() {   //вывод списка по времени
        return new ArrayList<>(prioritizedTasks);
    }

}


