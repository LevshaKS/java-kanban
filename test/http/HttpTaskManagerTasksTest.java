package http;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import http.HttpTaskServer;
import managers.InMemoryHistoryManager;
import managers.InMemoryTaskManager;
import managers.TaskManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import tasks.Task;
import util.Status;
import util.Type;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.List;

public class HttpTaskManagerTasksTest {
    TaskManager taskManager = new InMemoryTaskManager(new InMemoryHistoryManager());
    HttpTaskServer httpTaskServer = new HttpTaskServer(taskManager);
    Gson gson = HttpTaskServer.getGson();

    public HttpTaskManagerTasksTest() throws IOException {
    }

    @BeforeEach
    public void setUp() {
        taskManager.clearTaskList();
        taskManager.clearTaskList();
        taskManager.clearEpicList();
        httpTaskServer.start();
    }

    @AfterEach
    public void setDown() {
        httpTaskServer.stop();
    }

    class UserListTypeToken extends TypeToken<List<Task>> {
    }


    @Test
    public void addTask() throws IOException, InterruptedException {
        // cоздаем таску для отпрвки на сервер
        Task task = new Task("Task 1", "test task1", LocalDateTime.now(), 25);
        String jsonTask = gson.toJson(task);
        //создаем http клиента
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/tasks");
        //создраем запрос
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .POST(HttpRequest.BodyPublishers.ofString(jsonTask))
                .build();
        //отправяем запрос, получаем в ответ код
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        //проверем значения
        Assertions.assertEquals(201, response.statusCode(), "Не верный код ответа");
        List<Task> allTasks = taskManager.getTasks();
        Assertions.assertNotNull(allTasks, "задача не добавилась");
        Assertions.assertEquals(1, allTasks.size(), "колличество задач не совпадает");
    }

    @Test
    public void updateTask() throws IOException, InterruptedException {
        Task task = new Task("Task 1", "test task1",
                LocalDateTime.of(2025, 03, 22, 10, 00), 25);
        String jsonTask = gson.toJson(task);
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .POST(HttpRequest.BodyPublishers.ofString(jsonTask))
                .build();
        client.send(request, HttpResponse.BodyHandlers.ofString());
        Task taskUpdate = new Task(1, Type.TASK, "Task update", Status.IN_PROGRESS, "test task1",
                LocalDateTime.of(2025, 03, 22, 10, 00), 25);
        String jsonTaskUpdate = gson.toJson(taskUpdate);
        HttpRequest requestUpdate = HttpRequest.newBuilder()
                .uri(uri)
                .POST(HttpRequest.BodyPublishers.ofString(jsonTaskUpdate))
                .build();
        HttpResponse<String> response = client.send(requestUpdate, HttpResponse.BodyHandlers.ofString());
        List<Task> allTasks = taskManager.getTasks();
        Assertions.assertEquals(201, response.statusCode(), "Не верный код ответа");
        Assertions.assertEquals(1, allTasks.size(), "колличество задач не совпадает");
        Assertions.assertEquals("Task update", allTasks.get(0).getName(), "Некорректное имя задачи");
    }

    @Test
    public void getTask() throws IOException, InterruptedException {
//проверяем на несуществующую Таску
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/1"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(404, response.statusCode(), "не верный код ответа");

        Task task = new Task("Task 1", "test task1", LocalDateTime.now(), 25);
        String jsonTask = gson.toJson(task);

        request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks"))
                .POST(HttpRequest.BodyPublishers.ofString(jsonTask))
                .build();

        client.send(request, HttpResponse.BodyHandlers.ofString());

        request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/1"))
                .GET()
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Task taskFromJson = gson.fromJson(response.body(), Task.class);
        Assertions.assertEquals(200, response.statusCode(), "не верный код ответа");
        Assertions.assertEquals(taskManager.getToIdTask(1), taskFromJson, "не не совпали Task");
    }

    @Test
    public void getAllTask() throws IOException, InterruptedException {
        HttpResponse<String> response;
        HttpRequest request;
        Task task = new Task("Task 1", "test task1", LocalDateTime.now(), 25);
        String jsonTask1 = gson.toJson(task);
        Task task2 = new Task("Task 2", "test task2", LocalDateTime.now(), 25);
        String jsonTask2 = gson.toJson(task2);
        Task task3 = new Task("Task 3", "test task2", LocalDateTime.now().plusHours(1), 25);
        String jsonTask3 = gson.toJson(task3);

        //пустой запрос всех тасков
        HttpClient client = HttpClient.newHttpClient();
        request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks"))
                .GET()
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals("[]", response.body(), "вернулся не пустой список задач");

        // проверяем на пересечение при добавление таск 2
        request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks"))
                .POST(HttpRequest.BodyPublishers.ofString(jsonTask1))
                .build();
        client.send(request, HttpResponse.BodyHandlers.ofString());

        request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks"))
                .POST(HttpRequest.BodyPublishers.ofString(jsonTask2))
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(406, response.statusCode(), "вернулся не код ошибки пересечения записей");

        // проверяем возращение списка из всех тасков
        request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks"))
                .POST(HttpRequest.BodyPublishers.ofString(jsonTask3))
                .build();
        client.send(request, HttpResponse.BodyHandlers.ofString());

        request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks"))
                .GET()
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        List<Task> allTask = gson.fromJson(response.body(), new UserListTypeToken().getType());
        Assertions.assertEquals(taskManager.getTasks().size(), allTask.size(),
                "не свопадает количество записей");
    }

    @Test
    public void deleteTask() throws IOException, InterruptedException {
        HttpResponse<String> response;
        HttpRequest request;
        Task task = new Task("Task 1", "test task1", LocalDateTime.now(), 25);
        String jsonTask1 = gson.toJson(task);
        Task task2 = new Task("Task 2", "test task2", LocalDateTime.now().plusHours(1), 25);
        String jsonTask2 = gson.toJson(task2);
        Task task3 = new Task("Task 3", "test task2", LocalDateTime.now().plusHours(2), 25);
        String jsonTask3 = gson.toJson(task3);
        HttpClient client = HttpClient.newHttpClient();

        request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks"))
                .POST(HttpRequest.BodyPublishers.ofString(jsonTask1))
                .build();
        client.send(request, HttpResponse.BodyHandlers.ofString());

        request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks"))
                .POST(HttpRequest.BodyPublishers.ofString(jsonTask2))
                .build();
        client.send(request, HttpResponse.BodyHandlers.ofString());

        request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks"))
                .POST(HttpRequest.BodyPublishers.ofString(jsonTask3))
                .build();
        client.send(request, HttpResponse.BodyHandlers.ofString());

        //удаляем 1 задачу
        request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/2"))
                .DELETE()
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertEquals(taskManager.getTasks().size(), 2, "не удалилась задача");

        // удаляем все задачи
        request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks"))
                .DELETE()
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertEquals(taskManager.getTasks().size(), 0, "не удалилась задача");
    }


}


