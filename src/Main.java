/* import managers.FileBackedTaskManager;
import managers.InMemoryTaskManager;
import managers.Managers;
import tasks.Epic;
import tasks.SubTask;
import tasks.Task;
import util.Status;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;

import static managers.FileBackedTaskManager.loadFromFile;

public class Main {

   public static void main(String[] args) throws IOException {
        String home = System.getProperty("user.dir");
        home += "\\resources\\";

        Task task1 = new Task("задача_5", "описание задачи_5", LocalDateTime.now().plusHours(2), 15);
        Task task2 = new Task("задача_6", "описание задачи_6", LocalDateTime.now().plusHours(1), 25);
        Epic epic1 = new Epic("эпик_7", "описание эпик_7");
        Epic epic2 = new Epic("эпик_8", "описание эпик_8");
        SubTask subTask1 = new SubTask("subtask1", "подзадача epic3", 3, LocalDateTime.now().plusHours(5), 25);
        SubTask subTask2 = new SubTask("subtask2", "подзадача epic3", 3, LocalDateTime.now().plusHours(10), 225);
        SubTask subTask3 = new SubTask("subtask3", "подзадача epic3", 3, LocalDateTime.now().plusHours(6), 25);


        // загрузка существуещего файла с добавлением в него Задач
       FileBackedTaskManager fileBackedTaskManager = (loadFromFile(new File(home + "text.txt")));
        fileBackedTaskManager.newTack(task1);
        fileBackedTaskManager.newTack(task2);
        fileBackedTaskManager.newEpic(epic1);
        fileBackedTaskManager.newEpic(epic2);
        printAllTasks(fileBackedTaskManager);
       // создание нового файла с задачами
        FileBackedTaskManager fileBackedTaskManager2 = new FileBackedTaskManager(Managers.getDefaultHistory(),
                home + "text2.txt");
        fileBackedTaskManager2.newTack(task1);
        fileBackedTaskManager2.newTack(task2);
        fileBackedTaskManager2.newEpic(epic1);
        fileBackedTaskManager2.newEpic(epic2);
        fileBackedTaskManager2.newSubTask(subTask1);
        fileBackedTaskManager2.newSubTask(subTask2);
        fileBackedTaskManager2.newSubTask(subTask3);
        printAllTasks(fileBackedTaskManager2);
        System.out.println("________________");

        Task task3 = new Task("задача_6", "описание задачи_5", LocalDateTime.now(), 15);
        Epic epic3 = new Epic("эпик_9", "описание эпик_8");
        fileBackedTaskManager2.newTack(task3);
        fileBackedTaskManager2.newEpic(epic3);
        fileBackedTaskManager2.clearEpicList();
        printAllTasks(fileBackedTaskManager2);
        fileBackedTaskManager2.getToIdTask(1);
        fileBackedTaskManager2.getToIdSubTask(5);
        fileBackedTaskManager2.getToIdEpic(3);
        task1.setStatus(Status.IN_PROGRESS);
        fileBackedTaskManager2.updateTask(1, task1);
        subTask3.setStartTime(LocalDateTime.now().plusMinutes(30));
        fileBackedTaskManager2.updateSubTask(7, subTask3);
        fileBackedTaskManager2.removeToIdSubTask(5);
        printAllTasks(fileBackedTaskManager2);
    }


    private static void printAllTasks(InMemoryTaskManager inMemoryTaskManager) {
        System.out.println("Задачи:");
        for (Task task : inMemoryTaskManager.getTasks()) {
            System.out.println(task);
        }
        System.out.println("Эпики:");
        for (Task epic : inMemoryTaskManager.getEpic()) {
            System.out.println(epic);
            for (Task task : inMemoryTaskManager.getToIdSubtaskInEpic(epic.getId())) {
                System.out.println("--> " + task);
            }
        }
        System.out.println("Подзадачи:");
        for (Task subtask : inMemoryTaskManager.getSubTask()) {
            System.out.println(subtask);
        }
        System.out.println("История:");
        for (Task task : inMemoryTaskManager.getHistory()) {
            System.out.println(task);
        }

        System.out.println("задачи по времени:");
        for (Task task : inMemoryTaskManager.getPrioritizedTasks()) {
            System.out.println(task);
        }
    }
}
*/