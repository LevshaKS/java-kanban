package managers;

import exceptions.ManagerSaveException;
import tasks.Epic;
import tasks.SubTask;
import tasks.Task;
import util.Status;
import util.Type;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;

public class FileBackedTaskManager extends InMemoryTaskManager {

    private final File file;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss");

    public FileBackedTaskManager(HistoryManager historyManager, String file) {
        super(historyManager);
        if (!Files.exists(Path.of((file)))) {
            try {
                Files.createFile(Path.of(file));
            } catch (IOException e) {
                throw new ManagerSaveException("Ошибка создания файла");
            }
        }
        this.file = new File(file);
    }

    @Override
    public void newTack(Task task) {
        super.newTack(task);
        save();
    }

    @Override
    public void newEpic(Epic epic) {
        super.newEpic(epic);
        save();
    }

    @Override
    public void newSubTask(SubTask subTask) {
        super.newSubTask(subTask);
        save();
    }

    @Override
    public void updateTask(int id, Task task) {
        super.updateTask(id, task);
        save();
    }

    @Override
    public void updateSubTask(int id, SubTask subTask) {
        super.updateSubTask(id, subTask);
        save();
    }

    @Override
    public void clearTaskList() {
        super.clearTaskList();
        save();
    }

    @Override
    public void clearEpicList() {
        super.clearEpicList();
        save();
    }

    @Override
    public void clearSubTaskList() {
        super.clearSubTaskList();
        save();
    }

    @Override
    public void removeToIdTask(int id) {
        super.removeToIdTask(id);
        save();
    }

    @Override
    public void removeToIdEpic(int id) {
        super.removeToIdEpic(id);
        save();
    }

    @Override
    public void removeToIdSubTask(int id) {
        super.removeToIdSubTask(id);
        save();
    }

    public void save() {
        try (Writer fileWrite = new FileWriter(file)) {
            HashMap<Integer, String> allTasks = new HashMap<>();
            fileWrite.write("id,type,name,status,description,epic");
            ArrayList<Task> tasks = super.getTasks();   // заполняем Task-ми
            for (Task element : tasks) {
                int id = element.getId();
                allTasks.put(id, element.toStringInFile());
            }
            ArrayList<Epic> epics = super.getEpic(); // заполняем Epic-ми
            for (Epic element : epics) {
                int id = element.getId();
                allTasks.put(id, element.toStringInFile());
            }
            ArrayList<SubTask> subTasks = super.getSubTask(); //заполняем Subtask-ми
            for (SubTask element : subTasks) {
                int id = element.getId();
                allTasks.put(id, element.toStringInFile());
            }
            for (String element : allTasks.values()) { //перенесли из МАРы в файл строки
                fileWrite.write("\n" + element);
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка записи файла");
        }
    }

    protected Task fromString(String value) {
        int epicId = 0;
        String[] element = value.split(",");
        int id = Integer.parseInt(element[0]);
        Type type = Type.valueOf(element[1]);
        String name = element[2];
        Status status = Status.valueOf(element[3]);
        String description = element[4];
        LocalDateTime startTime = LocalDateTime.parse(element[5], formatter);
        long duration = Long.parseLong(element[6]);
        if (element.length == 8) {
            epicId = Integer.parseInt(element[7]);
        }
        switch (type) {
            case TASK:
                return new Task(id, type, name, status, description, startTime, duration);
            case EPIC:
                return new Epic(id, type, name, status, description, startTime, duration);
            case SUBTUSK:
                return new SubTask(id, type, name, status, description, startTime, duration, epicId);
            default:
                return null;
        }
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(Managers.getDefaultHistory(),
                String.valueOf(file));
        try (FileReader fileReader = new FileReader(file)) {
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            while (bufferedReader.ready()) {
                String line = bufferedReader.readLine();
                if (line.equals("") || line.contains("id"))
                    continue;
                else {
                    Task task;
                    task = fileBackedTaskManager.fromString(line);
                    if (task.getType() == Type.EPIC)
                        fileBackedTaskManager.newEpic((Epic) task);
                    else if (task.getType() == Type.SUBTUSK) {
                        fileBackedTaskManager.newSubTask((SubTask) task);
                    } else
                        fileBackedTaskManager.newTack(task);
                }
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка чтения файла");
        }
        return fileBackedTaskManager;
    }
}
