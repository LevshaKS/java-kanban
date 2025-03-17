package managers;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.SubTask;
import tasks.Task;
import util.Status;

import java.time.LocalDateTime;
import java.util.List;

abstract class TaskManagerTest<T extends TaskManager> {

    protected T taskManager;
    protected Task task1, task2, task3;
    protected Epic epic1, epic2;
    protected SubTask subTask1, subTask2, subTask3;

    @BeforeEach
    void initTask() {
        taskManager = (T) new InMemoryTaskManager(new InMemoryHistoryManager());
        task1 = new Task("task 1", "task 1 descriprion",
                LocalDateTime.of(2025, 01, 01, 00, 00, 00), 25);
        task2 = new Task("task 2", "task 2 descriprion",
                LocalDateTime.of(2025, 01, 01, 01, 00, 00), 25);
        task3 = new Task("task 3", "task 3 descriprion",
                LocalDateTime.of(2025, 01, 01, 02, 00, 00), 25);
        epic1 = new Epic("test epic1", "epic1 descriprion");
        epic2 = new Epic("test epic2", "epic2 descriprion");
        subTask1 = new SubTask("test subtask1_1", "subtask1 descriprion", 1,
                LocalDateTime.of(2025, 01, 01, 03, 00, 00), 25);
        subTask2 = new SubTask("test subtask1_2", "subtask2 descriprion", 1,
                LocalDateTime.of(2025, 01, 01, 04, 00, 00), 25);
        subTask3 = new SubTask("test subtask3_1", "subtask3 descriprion", 2,
                LocalDateTime.of(2025, 01, 01, 05, 00, 00), 25);
    }

    @Test
    void newTack() {
        taskManager.newTack(task1);
        Assertions.assertNotNull(taskManager.getToIdTask(1), "Задача не записалась");
    }

    @Test
    void getTasks() {
        taskManager.newTack(task1);
        List<Task> tasks = taskManager.getTasks();
        Assertions.assertNotNull(tasks, "Список задач не возращается");
        Assertions.assertEquals(1, tasks.size(), "Неверное количество задач");
        Assertions.assertEquals(task1, tasks.get(0), "Не совпадает задача с задачей в списке");
    }

    @Test
    void removeToIdTask() {
        taskManager.newTack(task1);
        List<Task> tasks = taskManager.getTasks();
        Assertions.assertEquals(1, tasks.size(), "Не совпадает список");
        taskManager.removeToIdTask(1);
        tasks = taskManager.getTasks();
        Assertions.assertEquals(0, tasks.size(), "Не совпадает список после удаления");
    }

    @Test
    void getToIdTask() {
        taskManager.newTack(task1);
        Assertions.assertEquals(task1, taskManager.getToIdTask(1), "Задачи не совпали");
    }

    @Test
    void clearTask() {
        taskManager.newTack(task1);
        taskManager.newTack(task2);
        List<Task> tasks = taskManager.getTasks();
        Assertions.assertEquals(2, tasks.size(), "Не совпадает список");
        taskManager.clearTaskList();
        tasks = taskManager.getTasks();
        Assertions.assertEquals(0, tasks.size(), "Не совпадает список после удаления");
    }

    @Test
    void updateTask() {
        taskManager.newTack(task1);
        taskManager.updateTask(1, task2);
        Assertions.assertEquals(task2, taskManager.getToIdTask(1), "обновленная задача не записалась");
    }

    @Test
    void newEpic() {
        taskManager.newEpic(epic1);
        Assertions.assertNotNull(taskManager.getToIdEpic(1), "Эпик не записался");
    }


    @Test
    void getEpic() {
        taskManager.newEpic(epic1);
        taskManager.newEpic(epic2);
        List<Epic> epics = taskManager.getEpic();
        Assertions.assertNotNull(epics, "Список эпиков не возращается");
        Assertions.assertEquals(2, epics.size(), "Неверное количество эпиков");
    }

    @Test
    void getToIdEpic() {
        taskManager.newEpic(epic1);
        Assertions.assertEquals(epic1, taskManager.getToIdEpic(1), "Эпик не совпали");
    }

    @Test
    void getToIdSubtaskInEpic() {
        taskManager.newEpic(epic1);
        taskManager.newEpic(epic2);
        List<SubTask> subTask = taskManager.getToIdSubtaskInEpic(1);
        Assertions.assertTrue(subTask.isEmpty(), "Список sabtask не пустой");
        taskManager.newSubTask(subTask1);
        taskManager.newSubTask(subTask2);
        taskManager.newSubTask(subTask3);
        subTask = taskManager.getToIdSubtaskInEpic(1);
        Assertions.assertEquals(2, subTask.size(), "Неверное количество sabtask в эпик");
    }

    @Test
    void removeToIdEpic() {
        taskManager.newEpic(epic1);
        taskManager.newEpic(epic2);
        List<Epic> epics = taskManager.getEpic();
        Assertions.assertEquals(2, epics.size(), "Неверное количество эпиков");
        taskManager.removeToIdEpic(1);
        epics = taskManager.getEpic();
        Assertions.assertEquals(1, epics.size(), "Неверное количество эпиков");

    }

    @Test
    void clearEpic() {
        taskManager.newEpic(epic1);
        taskManager.newEpic(epic2);
        List<Epic> epics = taskManager.getEpic();
        Assertions.assertEquals(2, epics.size(), "Неверное количество эпиков");
        taskManager.newSubTask(subTask1);
        taskManager.newSubTask(subTask2);
        List<SubTask> subTask = taskManager.getSubTask();
        Assertions.assertEquals(2, subTask.size(), "Неверное количество subtask");
        taskManager.clearEpicList();
        epics = taskManager.getEpic();
        Assertions.assertEquals(0, epics.size(), "Неверное количество эпиков");
        subTask = taskManager.getSubTask();
        Assertions.assertEquals(0, subTask.size(), "Неверное количество subtask");
    }

    @Test
    void getSubTask() {
        taskManager.newEpic(epic1);
        taskManager.newEpic(epic2);
        List<SubTask> subTask = taskManager.getSubTask();
        Assertions.assertTrue(subTask.isEmpty(), "Список sabtask не пустой");
        taskManager.newSubTask(subTask1);
        taskManager.newSubTask(subTask2);
        taskManager.newSubTask(subTask3);
        subTask = taskManager.getSubTask();
        Assertions.assertEquals(3, subTask.size(), "Неверное количество subtask");
    }

    @Test
    void newSubTask() {
        taskManager.newEpic(epic1);
        List<SubTask> subTask = taskManager.getSubTask();
        Assertions.assertTrue(subTask.isEmpty(), "Список sabtask не пустой");
        taskManager.newSubTask(subTask1);
        taskManager.newSubTask(subTask2);
        subTask = taskManager.getSubTask();
        Assertions.assertEquals(2, subTask.size(), "Неверное количество subtask");
    }

    @Test
    void clearSubTask() {
        taskManager.newEpic(epic1);
        taskManager.newEpic(epic2);
        taskManager.newSubTask(subTask1);
        taskManager.newSubTask(subTask2);
        taskManager.newSubTask(subTask3);
        List<SubTask> subTask = taskManager.getSubTask();
        Assertions.assertEquals(3, subTask.size(), "Неверное количество subtask");
        taskManager.clearSubTaskList();
        subTask = taskManager.getSubTask();
        Assertions.assertEquals(0, subTask.size(), "Неверное количество subtask");
    }

    @Test
    void getToIdSubTask() {
        taskManager.newEpic(epic1);
        taskManager.newEpic(epic2);
        taskManager.newSubTask(subTask1);
        Assertions.assertEquals(subTask1, taskManager.getToIdSubTask(3), "Подзадачи не совпали");
    }

    @Test
    void removeToIdSubTask() {
        taskManager.newEpic(epic1);
        taskManager.newEpic(epic2);
        taskManager.newSubTask(subTask1);
        taskManager.newSubTask(subTask2);
        taskManager.newSubTask(subTask3);
        Assertions.assertEquals(subTask1, taskManager.getToIdSubTask(3), "Подзадачи не найдена");
        taskManager.removeToIdSubTask(3);
        Assertions.assertNotEquals(subTask1, taskManager.getToIdSubTask(3), "Подзадачи не удалилась");
    }

    @Test
    void updateSubTask() {
        taskManager.newEpic(epic1);
        taskManager.newEpic(epic2);
        taskManager.newSubTask(subTask1);
        taskManager.updateSubTask(3, subTask3);
        Assertions.assertEquals(subTask3, taskManager.getToIdSubTask(3), "Подзадачи не найдена");
    }

    @Test
    void calculateStatusEpic() {
        taskManager.newEpic(epic1);
        taskManager.newSubTask(subTask1);
        taskManager.newSubTask(subTask2);
        Assertions.assertEquals(Status.NEW, taskManager.getToIdEpic(1).getStatus(), "статус не совпал");
        subTask1.setStatus(Status.IN_PROGRESS);
        taskManager.updateSubTask(2, subTask1);
        Assertions.assertEquals(Status.IN_PROGRESS, taskManager.getToIdEpic(1).getStatus(), "статус не совпал");
        subTask2.setStatus(Status.IN_PROGRESS);
        taskManager.updateSubTask(2, subTask2);
        Assertions.assertEquals(Status.IN_PROGRESS, taskManager.getToIdEpic(1).getStatus(), "статус не совпал");
        subTask1.setStatus(Status.DONE);
        taskManager.updateSubTask(2, subTask1);
        Assertions.assertEquals(Status.IN_PROGRESS, taskManager.getToIdEpic(1).getStatus(), "статус не совпал");
        subTask2.setStatus(Status.DONE);
        taskManager.updateSubTask(2, subTask2);
        Assertions.assertEquals(Status.DONE, taskManager.getToIdEpic(1).getStatus(), "статус не совпал");
    }
}

