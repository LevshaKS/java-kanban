package managers;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import tasks.Task;

import java.time.LocalDateTime;

public class InMemoryTaskManagerTest extends TaskManagerTest {

    @Test
    void NewManagers() {
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
    void addPrioritizedTasks() {
        Assertions.assertTrue(taskManager.getPrioritizedTasks().isEmpty(), "Список не пустой");
        taskManager.newTack(task1);
        taskManager.newTack(task2);
        Assertions.assertEquals(2, taskManager.getPrioritizedTasks().size(), "количество записей не совпадает");
    }

    @Test
    void removePrioritizedTasks() {
        taskManager.newTack(task1);
        taskManager.newTack(task2);
        Assertions.assertEquals(2, taskManager.getPrioritizedTasks().size(), "запись не добавилась");
        taskManager.removeToIdTask(2);
        Assertions.assertEquals(1, taskManager.getPrioritizedTasks().size(), "запись не удалилась");
    }

    @Test
    void intersectionStartPrioritizedTasks() {
        task1.setStartTime(LocalDateTime.now());
        task1.setDuration(25);
        task2.setStartTime(LocalDateTime.now().plusMinutes(20));
        task2.setDuration(25);
        taskManager.newTack(task1);
        taskManager.newTack(task2);
        Assertions.assertEquals(1, taskManager.getPrioritizedTasks().size(), "пересечения в начало, запись добавилась");
    }

    @Test
    void intersectionEndPrioritizedTasks() {
        task1.setStartTime(LocalDateTime.now());
        task1.setDuration(25);
        task2.setStartTime(LocalDateTime.now().minusMinutes(24));
        task2.setDuration(25);
        taskManager.newTack(task1);
        taskManager.newTack(task2);
        Assertions.assertEquals(1, taskManager.getPrioritizedTasks().size(), "пересечения в конце, запись добавилась");
    }

    @Test
    void insidePrioritizedTasks() {
        task1.setStartTime(LocalDateTime.now());
        task1.setDuration(25);
        task2.setStartTime(LocalDateTime.now().plusMinutes(5));
        task2.setDuration(10);
        taskManager.newTack(task1);
        taskManager.newTack(task2);
        Assertions.assertEquals(1, taskManager.getPrioritizedTasks().size(), "внутри времени задачи 1, запись добавилась");
    }

    @Test
    void outsidePrioritizedTasks() {
        task1.setStartTime(LocalDateTime.now());
        task1.setDuration(25);
        task2.setStartTime(LocalDateTime.now().minusMinutes(5));
        task2.setDuration(40);
        taskManager.newTack(task1);
        taskManager.newTack(task2);
        Assertions.assertEquals(1, taskManager.getPrioritizedTasks().size(), "Обволакивание времени задачи 1, запись добавилась");
    }

    @Test
    void orderPrioritizedTasks() {
        task1.setStartTime(LocalDateTime.now());
        task2.setStartTime(LocalDateTime.now().plusHours(1));
        taskManager.newTack(task1);
        taskManager.newTack(task2);
        Assertions.assertEquals(task2, taskManager.getPrioritizedTasks().get(1), "порядок записей не соответствует");
        task2.setStartTime(LocalDateTime.now().minusHours(2));
        taskManager.updateTask(2, task2);
        Assertions.assertEquals(task2, taskManager.getPrioritizedTasks().get(0), "порядок записей не соответствует");
    }

}
