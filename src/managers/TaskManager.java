package managers;

import tasks.Epic;
import tasks.SubTask;
import tasks.Task;

import java.util.ArrayList;
import java.util.List;

public interface TaskManager {
    int countId();

    // методы task
    void newTack(Task task);

    ArrayList<Task> getTasks();

    void clearTask();

    Task getToIdTask(int id);

    void removeToIdTask(int id);

    void updateTask(int id, Task task);

    // методы epic
    void newEpic(Epic epic);

    ArrayList<Epic> getEpic();

    Epic getToIdEpic(int id);

    ArrayList<SubTask> getToIdSubtaskInEpic(int id);

    void removeToIdEpic(int id);

    void clearEpic();

    //метод subtask
    ArrayList<SubTask> getSubTask();

    void newSubTask(SubTask subTask);

    void clearSubTask();

    SubTask getToIdSubTask(int id);

    void removeToIdSubTask(int id);

    void updateSubTask(int id, SubTask subTask);

    void calculateStatusEpic(int epicId);

    List<Task> getHistory();

    }
