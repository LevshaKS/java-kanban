import managers.FileBackedTaskManager;
import managers.InMemoryTaskManager;
import managers.Managers;
import tasks.Epic;
import tasks.Task;
import java.io.File;
import java.io.IOException;

import static managers.FileBackedTaskManager.loadFromFile;

public class Main {

    public static void main(String[] args) {

        FileBackedTaskManager fileBackedTaskManager2;
        FileBackedTaskManager fileBackedTaskManager = (loadFromFile(new File("text.txt")));
        printAllTasks(fileBackedTaskManager);

        Task task1 = new Task("задача_5", "описание задачи_5");
        Task task2 = new Task("задача_6", "описание задачи_6");
        Epic epic1 = new Epic("эпик_7", "описание эпик_7");
        Epic epic2 = new Epic("эпик_8", "описание эпик_8");

        fileBackedTaskManager.newTack(task1);
        fileBackedTaskManager.newTack(task2);
        fileBackedTaskManager.newEpic(epic1);
        fileBackedTaskManager.newEpic(epic2);
        printAllTasks(fileBackedTaskManager);

        try {
            File tmpFile = File.createTempFile("test","tmp");
             fileBackedTaskManager2 = new FileBackedTaskManager(Managers.getDefaultHistory(),tmpFile.getCanonicalPath());
        }  catch (IOException e) {
            throw new RuntimeException(e);
        }
        fileBackedTaskManager2.newTack(task1);
        fileBackedTaskManager2.newTack(task2);
        fileBackedTaskManager2.newEpic(epic1);
        fileBackedTaskManager2.newEpic(epic2);
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
    }
}
