package managers;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.SubTask;
import tasks.Task;

import java.util.List;

public class InMemoryHistoryManagerTest {

    private final HistoryManager historyManager = new InMemoryHistoryManager();
    private final InMemoryTaskManager taskManager = new InMemoryTaskManager(historyManager);
    private final Task task = new Task("test add task", "add task descriprion");
    private final Epic epic = new Epic("test add  epic", "add epic descriprion");
    private final SubTask subTask = new SubTask("test add subtask", "add subtask descriprion", 2);

    private void fillTaskManager() {
        taskManager.newTack(task);
        taskManager.newEpic(epic);
        taskManager.newSubTask(subTask);
        taskManager.newTack(new Task(task));
    }

    @Test
    public void addHistoryInTaskManager() {
        fillTaskManager();
        taskManager.getToIdTask(1);
        List<Task> historyList = taskManager.getHistory();
        Assertions.assertNotNull(historyList, "После просмотра задач история не должна быть пустой");
        Assertions.assertEquals(1, historyList.size(),
                "После просмотра задач история не должна быть пустой, 1 элемент");
        taskManager.getToIdTask(10);
        Assertions.assertEquals(1, historyList.size(),
                "при запросе задачи с несуществующим id не должна добавлятся в историю");
        taskManager.getToIdEpic(2);
        Assertions.assertEquals(2, taskManager.getHistory().size(),
                "Задача не добавилась в историю");
    }

    @Test
    public void isNullHistoryList() {
        Assertions.assertTrue(historyManager.getHistory().isEmpty(), "список History не пустой");
    }

    @Test
    public void isNullAddNullHistoryList() {
        historyManager.add(null);
        Assertions.assertTrue(historyManager.getHistory().isEmpty(), "список History не пустой");
    }

    @Test
    public void addTaskHistoryList() {
        fillTaskManager();
        taskManager.getToIdTask(1);
        taskManager.getToIdEpic(1);
        taskManager.getToIdEpic(2);
        Assertions.assertEquals(List.of(task, epic), historyManager.getHistory(),
                "History должен соотвествовать просмотру");
        taskManager.getToIdEpic(2);
        taskManager.getToIdTask(1);
        Assertions.assertEquals(List.of(epic, task), historyManager.getHistory(),
                "History порядок не соответсвует вызову");
    }

    @Test
    public void removeTaskHistoryList() {
        fillTaskManager();
        taskManager.getToIdTask(1);
        taskManager.getToIdEpic(2);
        taskManager.getToIdSubTask(3);
        taskManager.getToIdTask(4);
        historyManager.remove(44);
        Assertions.assertEquals(4, historyManager.getHistory().size(),
                "при удаление нусещ. id History изменился");
        historyManager.remove(4);
        Assertions.assertEquals(List.of(task, epic, subTask), historyManager.getHistory(),
                "при удаление в середине History порядок не должен менятся");
        historyManager.remove(3);
        Assertions.assertEquals(List.of(task, epic), historyManager.getHistory(),
                "при удаление последнего History порядок не должен менятся");
        historyManager.remove(1);
        Assertions.assertEquals(List.of(epic), historyManager.getHistory(),
                "при удаление первого History порядок не должен менятся");
    }
}

