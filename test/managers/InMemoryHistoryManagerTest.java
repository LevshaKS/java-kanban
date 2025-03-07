package managers;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import tasks.Task;

import java.util.List;

public class InMemoryHistoryManagerTest extends TaskManagerTest {

    @Test
    void addHistoryInTaskManager() {
        taskManager.newTack(task1);
        taskManager.newTack(task2);
        taskManager.newEpic(epic1);
        taskManager.newEpic(epic2);
        taskManager.getToIdTask(1);
        List<Task> historyList = taskManager.getHistory();
        Assertions.assertNotNull(historyList, "После просмотра задач история не должна быть пустой");
        Assertions.assertEquals(1, historyList.size(),
                "После просмотра задач история не должна быть пустой, 1 элемент");
        taskManager.getToIdTask(10);
        Assertions.assertEquals(1, historyList.size(),
                "при запросе задачи с несуществующим id не должна добавлятся в историю");
        taskManager.getToIdEpic(3);
        System.out.println(historyList);
        Assertions.assertEquals(2, taskManager.getHistory().size(),
                "Задача не добавилась в историю");
    }

    @Test
    void isNullHistoryList() {
        Assertions.assertTrue(taskManager.getHistory().isEmpty(), "список History не пустой");
    }

    @Test
    void addTaskHistoryList() {
        taskManager.newTack(task1);
        taskManager.newEpic(epic1);
        taskManager.getToIdTask(1);
        taskManager.getToIdEpic(2);
        Assertions.assertEquals(List.of(task1, epic1), taskManager.getHistory(),
                "History должен соотвествовать просмотру");
        taskManager.getToIdEpic(2);
        taskManager.getToIdTask(1);
        Assertions.assertEquals(List.of(epic1, task1), taskManager.getHistory(),
                "History порядок не соответсвует вызову");
    }

    @Test
    void removeTaskHistoryList() {
        taskManager.newTack(task1);
        taskManager.newEpic(epic1);
        taskManager.newSubTask(subTask3);
        taskManager.getToIdTask(1);
        taskManager.getToIdEpic(2);
        taskManager.getToIdSubTask(3);
        taskManager.removeToIdTask(11);
        Assertions.assertEquals(3, taskManager.getHistory().size(),
                "при удаление нусещ. id History изменился");
        taskManager.newTack(task2);
        taskManager.getToIdTask(4);
        taskManager.removeToIdSubTask(3);
        Assertions.assertEquals(List.of(task1, epic1, task2), taskManager.getHistory(),
                "при удаление в середине History порядок не должен менятся");
        taskManager.removeToIdTask(4);
        Assertions.assertEquals(List.of(task1, epic1), taskManager.getHistory(),
                "при удаление последнего History порядок не должен менятся");
        taskManager.removeToIdTask(1);
        Assertions.assertEquals(List.of(epic1), taskManager.getHistory(),
                "при удаление первого History порядок не должен менятся");
    }

    @Test
    void addDoubleTaskHistoryList() {
        taskManager.newTack(task1);
        taskManager.newEpic(epic1);
        taskManager.getToIdTask(1);
        taskManager.getToIdEpic(2);
        taskManager.getToIdTask(1);
        taskManager.getToIdEpic(2);
        Assertions.assertEquals(List.of(task1, epic1), taskManager.getHistory(),
                "History при повторном просмотре не должно увеличиваться количество");
    }
}

