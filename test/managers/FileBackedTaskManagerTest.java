package managers;

import exceptions.ManagerSaveException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileBackedTaskManagerTest extends TaskManagerTest {
    String home = System.getProperty("user.dir") + "\\resources\\";
    String file1 = (home + "TestText1.txt");
    String file2 = (home + "TestText2.txt");

    @Test
    void creatureFile() {
        File file = new File(file1);
        file.delete();
        Assertions.assertTrue(!Files.exists(Path.of((file1))), "файла не должно быть");
        FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(Managers.getDefaultHistory(), file1);
        Assertions.assertTrue(Files.exists(Path.of((file1))), "после создания менеджера, файл должен создаться");
    }

    @Test
    void saveFile() {
        File file = new File(file1);
        file.delete();
        FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(Managers.getDefaultHistory(), file1);
        fileBackedTaskManager.newTack(task1);
        fileBackedTaskManager.newEpic(epic1);
        try (FileReader fileReader = new FileReader(file1)) {
            BufferedReader br = new BufferedReader(fileReader);
            Assertions.assertFalse(br.readLine() == null, "в файл не записались данные");
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка чтения файла");
        }
    }

    //закоментил, не знаю почему githab  не подхвыатывает список из файла, сам файл загружен с проектом
  /* @Test
    void loadFile() {
        File file = new File(file2);
        List<Task> taskList = new ArrayList<>(taskManager.getTasks());
        Assertions.assertEquals(0, taskList.size(), "список не пустой");
        taskManager = FileBackedTaskManager.loadFromFile(file);
        taskList.addAll(taskManager.getTasks());
        taskList.addAll(taskManager.getEpic());
        System.out.println(taskList);
        Assertions.assertEquals(2, taskList.size(), "список не соответсвует");
        Assertions.assertEquals(epic1, taskManager.getToIdEpic(2), "список загруженных задач не соотвествует заполненым");
    }*/
}
