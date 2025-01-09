package test;

import managers.Managers;
import managers.inMemoryTaskManager;
import org.junit.Assert;
import org.junit.Test;
import tasks.Epic;
import tasks.SubTask;
import tasks.Task;
import util.Status;

import java.util.List;

public class inMemoryHistoryManagerTest {
    inMemoryTaskManager taskManager = (inMemoryTaskManager) Managers.getDefault();

    @Test
    public void HistoryManager() {
        Task task = new Task("test add task", "add task descriprion");
        taskManager.newTack(task);
        Epic epic = new Epic("test add suptask in epic", "add suptask in epic descriprion");
        taskManager.newEpic(epic);
        SubTask subTask = new SubTask("test add suptask", "add suptask descriprion", 2);
        taskManager.newSubTask(subTask);
        taskManager.getToIdTask(1);
        List<Task> history = taskManager.getHistory();
        Assert.assertNotNull("После просмотра задач история не должна быть пустой", history);
        Assert.assertEquals("После просмотра задач история не должна быть пустой, 1 элемент", 1, history.size());
        taskManager.getToIdEpic(2);
        Assert.assertEquals("Задача не добавилась в историю", 2, taskManager.getHistory().size());
        Task updste = taskManager.getToIdTask(1);
        updste.setStatus(Status.IN_PROGRESS);
        taskManager.updateTask(1, updste);
        taskManager.getToIdTask(1);
        Assert.assertEquals("С одинаковым статусом не равны задачи в истории", taskManager.getHistory().get(0), taskManager.getHistory().get(2));
        Assert.assertNotEquals("с разным статусом равны задачи в истории", taskManager.getHistory().get(0), taskManager.getHistory().get(3));
    }
}
