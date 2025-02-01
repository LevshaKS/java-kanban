package managers;

import org.junit.Assert;
import org.junit.Test;
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
        Assert.assertNotNull("После просмотра задач история не должна быть пустой", historyList);
        Assert.assertEquals("После просмотра задач история не должна быть пустой, 1 элемент", 1,
                historyList.size());
        taskManager.getToIdTask(10);
        Assert.assertEquals("при запросе задачи с несуществующим id не должна добавлятся в историю", 1,
                historyList.size());
        taskManager.getToIdEpic(2);
        Assert.assertEquals("Задача не добавилась в историю", 2, taskManager.getHistory().size());
    }

    @Test
    public void isNullHistoryList() {
        Assert.assertTrue("список History не пустой", historyManager.getHistory().isEmpty());
    }

    @Test
    public void isNullAddNullHistoryList() {
        historyManager.add(null);
        Assert.assertTrue("список History не пустой", historyManager.getHistory().isEmpty());
    }

    @Test
    public void addTaskHistoryList() {
        fillTaskManager();
        taskManager.getToIdTask(1);
        taskManager.getToIdEpic(1);
        taskManager.getToIdEpic(2);
        Assert.assertEquals("History должен соотвествовать просмотру", List.of(task, epic),
                historyManager.getHistory());
        taskManager.getToIdEpic(2);
        taskManager.getToIdTask(1);
        Assert.assertEquals("History порядок не соответсвует вызову", List.of(epic, task),
                historyManager.getHistory());
    }

    @Test
    public void removeTaskHistoryList() {
        fillTaskManager();
        taskManager.getToIdTask(1);
        taskManager.getToIdEpic(2);
        taskManager.getToIdSubTask(3);
        taskManager.getToIdTask(4);
        historyManager.remove(44);
        Assert.assertEquals("при удаление нусещ. id History изменился", 4,
                historyManager.getHistory().size());
        historyManager.remove(4);
        Assert.assertEquals("при удаление в середине History порядок не должен менятся",
                List.of(task, epic, subTask), historyManager.getHistory());
        historyManager.remove(3);
        Assert.assertEquals("при удаление последнего History порядок не должен менятся", List.of(task, epic),
                historyManager.getHistory());
        historyManager.remove(1);
        Assert.assertEquals("при удаление первого History порядок не должен менятся", List.of(epic),
                historyManager.getHistory());
     }
}

