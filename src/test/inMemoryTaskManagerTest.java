package test;

import managers.Managers;
import managers.TaskManager;
import managers.inMemoryTaskManager;
import org.junit.Assert;
import org.junit.Test;
import tasks.Epic;
import tasks.SubTask;
import tasks.Task;

import java.util.List;

public class inMemoryTaskManagerTest {

    inMemoryTaskManager taskManager = (inMemoryTaskManager) Managers.getDefault();

    @Test
    public void NewManagersNotNull() {
        TaskManager taskManager1 = Managers.getDefault();
        Assert.assertNotNull("Не создался новый элемент", taskManager1);
    }

    @Test
    public void addNewTask() {
        Task task = new Task("test add task", "add task descriprion");
        taskManager.newTack(task);
        Assert.assertEquals("Задачи не совпали", task, taskManager.getToIdTask(1));
        Assert.assertNotNull("Задача не записалась", taskManager.getToIdTask(1));
        List<Task> tasks = taskManager.getTasks();
        Assert.assertNotNull("Список задач не возращается", tasks);
        Assert.assertEquals("Неверное количество задач", 1, tasks.size());
        Assert.assertEquals("Не совпадает задача с задачей в списке", task, tasks.get(0));
        taskManager.removeToIdTask(1);
        tasks = taskManager.getTasks();
        Assert.assertEquals("Не совпадает список после удаления", 0, tasks.size());
    }

    @Test
    public void addNewEpic() {
        Epic epic = new Epic("test add epic", "add epic descriprion");
        taskManager.newEpic(epic);
        Assert.assertEquals("Эпик не совпали", epic, taskManager.getToIdEpic(1));
        Assert.assertNotNull("Эпик не записался", taskManager.getToIdEpic(1));
        List<Epic> epics = taskManager.getEpic();
        Assert.assertNotNull("Список эпиков не возращается", epics);
        Assert.assertEquals("Неверное количество эпиков", 1, epics.size());
        Assert.assertEquals("Не совпадает эпик с эпиком в списке", epic, epics.get(0));
        taskManager.removeToIdEpic(1);
        epics = taskManager.getEpic();
        Assert.assertEquals("Не совпадает эпик после удаления", 0, epics.size());
    }

    @Test
    public void addNewSupTask() {
        Epic epic = new Epic("test add suptask in epic", "add suptask in epic descriprion");
        taskManager.newEpic(epic);
        SubTask subTask = new SubTask("test add suptask", "add suptask descriprion", 1);
        taskManager.newSubTask(subTask);
        Assert.assertEquals("Подзадачи не совпали", subTask, taskManager.getToIdSubTask(2));
        Assert.assertNotNull("Подзадача не записалась", taskManager.getToIdSubTask(2));
        List<SubTask> subTasks = taskManager.getSubTask();
        Assert.assertNotNull("Список подзадач не возращается", subTasks);
        Assert.assertEquals("Неверное количество подзадач", 1, subTasks.size());
        Assert.assertEquals("Не совпадает подзадача с подзадачей в списке", subTask, subTasks.get(0));
        Assert.assertEquals("Не совпадает возращаемая подзадача по id эпика", subTasks, taskManager.getToIdSubtaskInEpic(1));
        taskManager.removeToIdSubTask(2);
        subTasks = taskManager.getSubTask();
        Assert.assertEquals("Неверное количество подзадач", 0, subTasks.size());
        Assert.assertEquals("Из списка подзадач в эпике не удалилась подзадача", 0, taskManager.getToIdSubtaskInEpic(1).size());
    }
}
