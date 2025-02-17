package managers;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
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
        Assertions.assertNotNull(taskManager1, "Не создался новый элемент");
        Assertions.assertNotNull(taskManager2, "Не создался новый элемент");
        Task task = new Task("test add task", "add task descriprion");
        taskManager1.newTack(task);
        taskManager2.newTack(task);
        Assertions.assertNotEquals(taskManager1.getToIdTask(1), taskManager1.getToIdTask(2),
                "задачи в разных менеджерах должны быть не равны");
    }

    @Test
    public void addNewTask() {
        Task task = new Task("test add task", "add task descriprion");
        taskManager.newTack(task);
        Assertions.assertEquals(task, taskManager.getToIdTask(1), "Задачи не совпали");
        Assertions.assertNotNull(taskManager.getToIdTask(1), "Задача не записалась");
        List<Task> tasks = taskManager.getTasks();
        Assertions.assertNotNull(tasks, "Список задач не возращается");
        Assertions.assertEquals(1, tasks.size(), "Неверное количество задач");
        Assertions.assertEquals(task, tasks.get(0), "Не совпадает задача с задачей в списке");
        taskManager.removeToIdTask(1);
        tasks = taskManager.getTasks();
        Assertions.assertEquals(0, tasks.size(), "Не совпадает список после удаления");
        Assertions.assertTrue(taskManager.getHistory().isEmpty(), "при удаление необходимо удалить из истории ");
    }

    @Test
    public void addNewEpic() {
        Epic epic = new Epic("test add epic", "add epic descriprion");
        taskManager.newEpic(epic);
        Assertions.assertEquals(epic, taskManager.getToIdEpic(1), "Эпик не совпали");
        Assertions.assertNotNull(taskManager.getToIdEpic(1), "Эпик не записался");
        List<Epic> epics = taskManager.getEpic();
        Assertions.assertNotNull(epics, "Список эпиков не возращается");
        Assertions.assertEquals(1, epics.size(), "Неверное количество эпиков");
        Assertions.assertEquals(epic, epics.get(0), "Не совпадает эпик с эпиком в списке");
        taskManager.removeToIdEpic(1);
        epics = taskManager.getEpic();
        Assertions.assertEquals(0, epics.size(), "Не совпадает эпик после удаления");
        Assertions.assertTrue(taskManager.getHistory().isEmpty(), "при удаление необходимо удалить из истории ");
    }

    @Test
    public void addNewSupTask() {
        Epic epic = new Epic("test add suptask in epic", "add suptask in epic descriprion");
        taskManager.newEpic(epic);
        SubTask subTask = new SubTask("test add suptask", "add suptask descriprion", 1);
        taskManager.newSubTask(subTask);
        Assertions.assertEquals(subTask, taskManager.getToIdSubTask(2), "Подзадачи не совпали");
        Assertions.assertNotNull(taskManager.getToIdSubTask(2), "Подзадача не записалась");
        List<SubTask> subTasks = taskManager.getSubTask();
        Assertions.assertNotNull(subTasks, "Список подзадач не возращается");
        Assertions.assertEquals(1, subTasks.size(), "Неверное количество подзадач");
        Assertions.assertEquals(subTask, subTasks.get(0), "Не совпадает подзадача с подзадачей в списке");
        Assertions.assertEquals(subTasks, taskManager.getToIdSubtaskInEpic(1),
                "Не совпадает возращаемая подзадача по id эпика");
        taskManager.removeToIdSubTask(2);
        subTasks = taskManager.getSubTask();
        Assertions.assertEquals(0, subTasks.size(), "Неверное количество подзадач");
        Assertions.assertEquals(0, taskManager.getToIdSubtaskInEpic(1).size(),
                "Из списка подзадач в эпике не удалилась подзадача");
        Assertions.assertTrue(taskManager.getHistory().isEmpty(), "при удаление необходимо удалить из истории ");
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
        Assertions.assertTrue(taskManager.getTasks().isEmpty(), "список задач не пустой");
        Assertions.assertTrue(taskManager.getHistory().isEmpty(), "список в History не пустой");
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
        Assertions.assertTrue(taskManager.getSubTask().isEmpty(), "Список подзадач не пустой");
        Assertions.assertTrue(taskManager.getHistory().isEmpty(), "список в History не пустой");
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
        Assertions.assertTrue(taskManager.getEpic().isEmpty(), "Список епиков не пустой");
        Assertions.assertTrue(taskManager.getSubTask().isEmpty(), "Список подзадач не пустой");
        Assertions.assertTrue(taskManager.getHistory().isEmpty(), "список в History не пустой");
    }

}
