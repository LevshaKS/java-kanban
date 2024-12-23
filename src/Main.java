import managers.TaskManager;
import tasks.Epic;
import tasks.SubTask;
import tasks.Task;
import util.Status;

public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = new TaskManager();
        Task task1 = new Task("задача_1", "описание задачи_1");
        Task task2 = new Task("задача_2", "описание задачи_2");
        taskManager.newTack(task1);
        taskManager.newTack(task2);
        Epic epic1 = new Epic("эпик_1", "описание эпик_1");
        Epic epic2 = new Epic("эпик_2", "описание эпик_2");
        taskManager.newEpic(epic1);
        taskManager.newEpic(epic2);
        SubTask subTask1 = new SubTask("подзадача_1_эпик_1", "описание подзадачи", 3);
        SubTask subTask2 = new SubTask("подзадача_2_эпик_1", "описание подзадачи", 3);
        SubTask subTask3 = new SubTask("подзадача_1_эпик_2", "описание подзадачи", 4);
        taskManager.newSubTask(subTask1);
        taskManager.newSubTask(subTask2);
        taskManager.newSubTask(subTask3);
        System.out.println("_________список задач");
        System.out.println(taskManager.getTasks());
        System.out.println("_________список эпиков");
        System.out.println(taskManager.getEpic());
        System.out.println("_________список подзадач");
        System.out.println(taskManager.getSubTask());
        System.out.println("______________ задача по id");
        System.out.println(taskManager.getToIdTask(1));
        System.out.println("______________ эпик по id");
        System.out.println(taskManager.getToIdEpic(4));
        System.out.println("______________список подзадач эпика по id");
        System.out.println(taskManager.getToIdSubtaskInEpic(3));
        System.out.println("______________ подзадача по id");
        System.out.println(taskManager.getToIdSubTask(7));
        System.out.println("______________вывод обновленого статуса списка задач по id");
        int valueId = 1;
        Task updateTask1 = taskManager.getToIdTask(valueId);
        updateTask1.setStatus(Status.IN_PROGRESS);
        taskManager.updateTask(valueId, updateTask1);
        SubTask updateSubTask3 = taskManager.getToIdSubTask(6);
        updateSubTask3.setStatus(Status.IN_PROGRESS);
        taskManager.updateSubTask(6, updateSubTask3);
        System.out.println(taskManager.getTasks());
        System.out.println(taskManager.getEpic());
        System.out.println(taskManager.getSubTask());
        System.out.println("______________вывод обновленого статуса списка задач по id и удаление задачи");
        Task updateTask2 = taskManager.getToIdTask(valueId);
        updateTask2.setStatus(Status.DONE);
        taskManager.updateTask(valueId, updateTask2);
        SubTask updateSubTask = taskManager.getToIdSubTask(7);
        updateSubTask.setStatus(Status.IN_PROGRESS);
        taskManager.updateSubTask(7, updateSubTask);
        taskManager.removeToIdEpic(5);
        taskManager.removeToIdTask(2);
        System.out.println(taskManager.getTasks());
        System.out.println(taskManager.getEpic());
        System.out.println(taskManager.getSubTask());
        taskManager.removeToIdSubTask(6);
        System.out.println("______________добавление статуса подзадачи");
        updateSubTask = taskManager.getToIdSubTask(7);
        updateSubTask.setStatus(Status.DONE);
        taskManager.updateSubTask(7, updateSubTask);
        System.out.println(taskManager.getTasks());
        System.out.println(taskManager.getEpic());
        System.out.println(taskManager.getSubTask());
    }
}
