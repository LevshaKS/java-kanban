import managers.FileBackedTaskManager;
import managers.InMemoryTaskManager;
import managers.Managers;
import tasks.Epic;
import tasks.SubTask;
import tasks.Task;
import util.Status;

import java.io.File;
import java.time.LocalDateTime;

import static managers.FileBackedTaskManager.loadFromFile;

public class Main {

    public static void main(String[] args) {
        String home = System.getProperty("user.dir");
        home += "\\resources\\";


        Task task1 = new Task("задача_5", "описание задачи_5", LocalDateTime.now().plusHours(2), 15);
        Task task2 = new Task("задача_6", "описание задачи_6", LocalDateTime.now().plusHours(1), 25);
        Epic epic1 = new Epic("эпик_7", "описание эпик_7");
        Epic epic2 = new Epic("эпик_8", "описание эпик_8");
        SubTask subTask1 = new SubTask("subtask1", "подзадача epic3", 3, LocalDateTime.now().plusHours(5), 25);
        SubTask subTask2 = new SubTask("subtask2", "подзадача epic3", 3, LocalDateTime.now().plusHours(10), 225);

        // загрузка существуещего файла с добавлением в него Задач
        FileBackedTaskManager fileBackedTaskManager = (loadFromFile(new File(home + "text.txt")));
        fileBackedTaskManager.newTack(task1);
        fileBackedTaskManager.newTack(task2);
        fileBackedTaskManager.newEpic(epic1);
        fileBackedTaskManager.newEpic(epic2);
        printAllTasks(fileBackedTaskManager);
        // создание нового файла с задачами*/
        FileBackedTaskManager fileBackedTaskManager2 = new FileBackedTaskManager(Managers.getDefaultHistory(),
                home + "text2.txt");
        fileBackedTaskManager2.newTack(task1);
        fileBackedTaskManager2.newTack(task2);
        fileBackedTaskManager2.newEpic(epic1);
        fileBackedTaskManager2.newEpic(epic2);
        fileBackedTaskManager2.newSubTask(subTask1);
        fileBackedTaskManager2.newSubTask(subTask2);
        printAllTasks(fileBackedTaskManager2);
        System.out.println("________________");
        fileBackedTaskManager2.getToIdTask(1);
        fileBackedTaskManager2.getToIdSubTask(5);
        fileBackedTaskManager2.getToIdEpic(3);
        task1.setStatus(Status.IN_PROGRESS);
        task1.setStartTime(LocalDateTime.now());
        fileBackedTaskManager2.updateTask(1, task1);
        fileBackedTaskManager2.removeToIdEpic(3);
        printAllTasks(fileBackedTaskManager2);
    }

    private static void printAllTasks(InMemoryTaskManager manager) {
        System.out.println("Задачи:");
        for (Task task : manager.getTasks()) {
            System.out.println(task);
        }
        System.out.println("Эпики:");
        for (Task epic : manager.getEpic()) {
            System.out.println(epic);
            for (Task task : manager.getToIdSubtaskInEpic(epic.getId())) {
                System.out.println("--> " + task);
            }
        }
        System.out.println("Подзадачи:");
        for (Task subtask : manager.getSubTask()) {
            System.out.println(subtask);
        }
        System.out.println("История:");
        for (Task task : manager.getHistory()) {
            System.out.println(task);
        }

        System.out.println("задачи по времени:");
        for (Task task : manager.getPrioritizedTasks()) {
            System.out.println(task);
        }
    }
}
