package http;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpServer;
import http.handler.*;
import managers.Managers;
import managers.TaskManager;
import tasks.Epic;
import tasks.SubTask;
import tasks.Task;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.LocalDateTime;

public class HttpTaskServer {
    public static final int PORT = 8080;
    public final HttpServer httpServer;

    public final TaskManager manager;

    public HttpTaskServer(TaskManager manager) throws IOException {

        this.manager = manager;

        httpServer = HttpServer.create(new InetSocketAddress("localhost", PORT), 0);
        httpServer.createContext("/tasks", new TaskHttpHandler(manager));
        httpServer.createContext("/subtasks", new SubTaskHandler(manager));
        httpServer.createContext("/epics", new EpicTaskHandler(manager));
        httpServer.createContext("/history", new HistoryHandler(manager));
        httpServer.createContext("/prioritized", new PrioritizedHandler(manager));
    }

    public static Gson getGson() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(LocalDateTime.class, new LocalDataTimeAdapter());
        return gsonBuilder.create();
    }

    public void start() {
        httpServer.start();
        System.out.println("старт сервера на порту:" + PORT);
    }

    public void stop() {
        httpServer.stop(0);
        System.out.println("остановка сервера на порту:" + PORT);
    }


    public static void main(String[] args) throws IOException {

        Task task1 = new Task("задача_5", "описание задачи_5", LocalDateTime.now().plusHours(2), 15);
        Task task2 = new Task("задача_6", "описание задачи_6", LocalDateTime.now().plusHours(1), 25);
        Epic epic1 = new Epic("эпик_7", "описание эпик_7");
        Epic epic2 = new Epic("эпик_8", "описание эпик_8");
        SubTask subTask1 = new SubTask("subtask1", "подзадача epic3", 3, LocalDateTime.now().plusHours(5), 25);
        SubTask subTask2 = new SubTask("subtask2", "подзадача epic3", 3, LocalDateTime.now().plusHours(10), 225);
        SubTask subTask3 = new SubTask("subtask3", "подзадача epic3", 3, LocalDateTime.now().plusHours(6), 25);

        TaskManager manager = Managers.getDefault();

        manager.newTack(task1);
        manager.newTack(task2);
        manager.newEpic(epic1);
        manager.newEpic(epic2);
        manager.newSubTask(subTask1);
        manager.newSubTask(subTask2);
        manager.newSubTask(subTask3);

        HttpTaskServer taskServer = new HttpTaskServer(manager);
        taskServer.start();

    }

}
