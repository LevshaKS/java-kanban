package managers;


import org.junit.Assert;
import org.junit.Test;
import tasks.Epic;
import tasks.SubTask;
import tasks.Task;

import java.util.List;

public class InMemoryTaskManagerTest {

    managers.InMemoryTaskManager taskManager = (managers.InMemoryTaskManager) Managers.getDefault();


    @Test
    public void NewManagers() {
        TaskManager taskManager1 = Managers.getDefault();
        TaskManager taskManager2 = Managers.getDefault();
        Assert.assertNotNull("Не создался новый элемент", taskManager1);
        Assert.assertNotNull("Не создался новый элемент", taskManager2);
        Task task = new Task("test add task", "add task descriprion");
        taskManager1.newTack(task);
        taskManager2.newTack(task);
        Assert.assertNotEquals("задачи в разных менеджерах должны быть не равны",
                taskManager1.getToIdTask(1), taskManager1.getToIdTask(2));
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
        Assert.assertTrue("при удаление необходимо удалить из истории ", taskManager.getHistory().isEmpty());
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
        Assert.assertTrue("при удаление необходимо удалить из истории ", taskManager.getHistory().isEmpty());
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
        Assert.assertEquals("Не совпадает возращаемая подзадача по id эпика", subTasks,
                taskManager.getToIdSubtaskInEpic(1));
        taskManager.removeToIdSubTask(2);
        subTasks = taskManager.getSubTask();
        Assert.assertEquals("Неверное количество подзадач", 0, subTasks.size());
        Assert.assertEquals("Из списка подзадач в эпике не удалилась подзадача", 0,
                taskManager.getToIdSubtaskInEpic(1).size());
        Assert.assertTrue("при удаление необходимо удалить из истории ", taskManager.getHistory().isEmpty());
    }

    @Test
    public void clearTask() {
        taskManager.newTack(new Task("test add task1", "add task1 descriprion"));
        taskManager.newTack(new Task("test add task2", "add task2 descriprion"));
        taskManager.newTack(new Task("test add task3", "add task3 descriprion"));
        taskManager.getToIdTask(1);
        taskManager.getToIdTask(2);
        taskManager.getToIdTask(3);
        taskManager.clearTask();
        Assert.assertTrue("список задач не пустой", taskManager.getTasks().isEmpty());
        Assert.assertTrue("список в History не пустой", taskManager.getHistory().isEmpty());
    }

    @Test
    public void clearSubTask() {
        taskManager.newEpic(new Epic("test add epic1", "add epic1 descriprion"));
        taskManager.newEpic(new Epic("test add epic2", "add epic2 descriprion"));
        taskManager.newSubTask(new SubTask("test add subtask1", "add subtask1 descriprion", 1));
        taskManager.newSubTask(new SubTask("test add subtask2", "add subtask2 descriprion", 1));
        taskManager.newSubTask(new SubTask("test add subtask3", "add subtask3 descriprion", 2));
        taskManager.getToIdSubTask(3);
        taskManager.getToIdSubTask(4);
        taskManager.getToIdSubTask(5);
        taskManager.clearSubTask();
        Assert.assertTrue("Список подзадач не пустой", taskManager.getSubTask().isEmpty());
        Assert.assertTrue("список в History не пустой", taskManager.getHistory().isEmpty());
    }

    @Test
    public void clearEpic() {
        taskManager.newEpic(new Epic("test add epic1", "add epic1 descriprion"));
        taskManager.newEpic(new Epic("test add epic2", "add epic2 descriprion"));
        taskManager.newSubTask(new SubTask("test add subtask1", "add subtask1 descriprion", 1));
        taskManager.newSubTask(new SubTask("test add subtask2", "add subtask2 descriprion", 1));
        taskManager.newSubTask(new SubTask("test add subtask3", "add subtask3 descriprion", 2));
        taskManager.getToIdEpic(1);
        taskManager.getToIdEpic(2);
        taskManager.getToIdSubTask(3);
        taskManager.getToIdSubTask(4);
        taskManager.getToIdSubTask(5);
        taskManager.clearEpic();
        Assert.assertTrue("Список епиков не пустой", taskManager.getEpic().isEmpty());
        Assert.assertTrue("Список подзадач не пустой", taskManager.getSubTask().isEmpty());
        Assert.assertTrue("список в History не пустой", taskManager.getHistory().isEmpty());
    }

}
