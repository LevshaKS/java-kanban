package http;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import managers.InMemoryHistoryManager;
import managers.InMemoryTaskManager;
import managers.TaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.SubTask;
import util.Status;
import util.Type;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.List;

public class HttpTaskManagerSubTaskTest {

    TaskManager taskManager = new InMemoryTaskManager(new InMemoryHistoryManager());
    HttpTaskServer httpTaskServer = new HttpTaskServer(taskManager);
    Gson gson = HttpTaskServer.getGson();

    HttpResponse<String> response;
    HttpRequest request;
    HttpClient client = HttpClient.newHttpClient();

    final Epic epic1 = new Epic("epic 1", "test epic1");
    final String jsonEpic1 = gson.toJson(epic1);
    final SubTask subTask1 = new SubTask("test subtask1_1", "subtask1 descriprion", 1,
            LocalDateTime.of(2025, 01, 01, 03, 00, 00), 25);
    final SubTask subTask2 = new SubTask("test subtask1_1", "subtask1 descriprion", 1,
            LocalDateTime.of(2025, 02, 01, 03, 00, 00), 25);
    final String jsonSubTask1 = gson.toJson(subTask1);
    final String jsonSubTask2 = gson.toJson(subTask2);


    public HttpTaskManagerSubTaskTest() throws IOException {
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

    class UserListTypeToken extends TypeToken<List<SubTask>> {
    }

    @Test
    public void addSubTask() throws IOException, InterruptedException {

        request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/epics"))
                .POST(HttpRequest.BodyPublishers.ofString(jsonEpic1))
                .build();
        client.send(request, HttpResponse.BodyHandlers.ofString());

        // добавляем саптаск

        request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/subtasks"))
                .POST(HttpRequest.BodyPublishers.ofString(jsonSubTask1))
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(201, response.statusCode());
        List<SubTask> allSubTasks = taskManager.getSubTask();
        Assertions.assertNotNull(allSubTasks, "задача не добавилась");
        Assertions.assertEquals(1, allSubTasks.size(), "колличество задач не совпадает");

// проверяем на пересечение, вывод 406 ошибки
        SubTask subTask3 = new SubTask("test subtask 3", "subtask descriprion", 1,
                LocalDateTime.of(2025, 01, 01, 03, 00, 00), 25);
        String jsonSubTask3 = gson.toJson(subTask3);

        request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/subtasks"))
                .POST(HttpRequest.BodyPublishers.ofString(jsonSubTask3))
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(406, response.statusCode());
    }

    @Test
    public void updatSubTask() throws IOException, InterruptedException {

        request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/epics"))
                .POST(HttpRequest.BodyPublishers.ofString(jsonEpic1))
                .build();
        client.send(request, HttpResponse.BodyHandlers.ofString());

        URI uri = URI.create("http://localhost:8080/subtasks");
        request = HttpRequest.newBuilder()
                .uri(uri)
                .POST(HttpRequest.BodyPublishers.ofString(jsonSubTask1))
                .build();
        client.send(request, HttpResponse.BodyHandlers.ofString());

        SubTask subTaskUpdate = new SubTask(2, Type.SUBTUSK, "test subtask update", Status.IN_PROGRESS, "subtask descriprion",
                LocalDateTime.of(2025, 01, 01, 03, 00, 00), 25, 1);
        String jsonTaskUpdate = gson.toJson(subTaskUpdate);
        HttpRequest requestUpdate = HttpRequest.newBuilder()
                .uri(uri)
                .POST(HttpRequest.BodyPublishers.ofString(jsonTaskUpdate))
                .build();
        response = client.send(requestUpdate, HttpResponse.BodyHandlers.ofString());
        List<SubTask> allTasks = taskManager.getSubTask();
        Assertions.assertEquals(201, response.statusCode(), "Не верный код ответа");
        Assertions.assertEquals(1, allTasks.size(), "колличество задач не совпадает");
        Assertions.assertEquals("test subtask update", allTasks.get(0).getName(), "Некорректное имя задачи");
    }

    /////////
    @Test
    public void getSubTask() throws IOException, InterruptedException {

        request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/epics"))
                .POST(HttpRequest.BodyPublishers.ofString(jsonEpic1))
                .build();
        client.send(request, HttpResponse.BodyHandlers.ofString());

        //проверяем на несуществующую Субтаск
        request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/subtasks/110"))
                .GET()
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(404, response.statusCode(), "не верный код ответа");
//добавляем
        request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/subtasks"))
                .POST(HttpRequest.BodyPublishers.ofString(jsonSubTask1))
                .build();

        client.send(request, HttpResponse.BodyHandlers.ofString());
// провеяем что вывелась по id
        request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/subtasks/2"))
                .GET()
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        SubTask subTaskFromJson = gson.fromJson(response.body(), SubTask.class);
        Assertions.assertEquals(200, response.statusCode(), "не верный код ответа");
        Assertions.assertEquals(taskManager.getToIdSubTask(2), subTaskFromJson, "не не совпали Subtask");
    }

    @Test
    public void getAllSubTask() throws IOException, InterruptedException {
        request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/epics"))
                .POST(HttpRequest.BodyPublishers.ofString(jsonEpic1))
                .build();
        client.send(request, HttpResponse.BodyHandlers.ofString());

        //пустой запрос всех саптасков
        HttpClient client = HttpClient.newHttpClient();
        request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/subtasks"))
                .GET()
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals("[]", response.body(), "вернулся не пустой список задач");

        // проверяем возращение списка из всех тасков


        request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/subtasks"))
                .POST(HttpRequest.BodyPublishers.ofString(jsonSubTask1))
                .build();
        client.send(request, HttpResponse.BodyHandlers.ofString());

        request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/subtasks"))
                .POST(HttpRequest.BodyPublishers.ofString(jsonSubTask2))
                .build();
        client.send(request, HttpResponse.BodyHandlers.ofString());

        request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/subtasks"))
                .GET()
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        List<SubTask> allSubTask = gson.fromJson(response.body(), new UserListTypeToken().getType());
        Assertions.assertEquals(taskManager.getSubTask().size(), allSubTask.size(),
                "не свопадает количество записей");
    }

    @Test
    public void deleteSubTask() throws IOException, InterruptedException {
        request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/epics"))
                .POST(HttpRequest.BodyPublishers.ofString(jsonEpic1))
                .build();
        client.send(request, HttpResponse.BodyHandlers.ofString());

        final SubTask subTask3 = new SubTask("test subtask1_1", "subtask1 descriprion", 1,
                LocalDateTime.of(2025, 04, 01, 03, 00, 00), 25);
        String jsonSubTask3 = gson.toJson(subTask3);

        request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/subtasks"))
                .POST(HttpRequest.BodyPublishers.ofString(jsonSubTask1))
                .build();
        client.send(request, HttpResponse.BodyHandlers.ofString());

        request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/subtasks"))
                .POST(HttpRequest.BodyPublishers.ofString(jsonSubTask2))
                .build();
        client.send(request, HttpResponse.BodyHandlers.ofString());

        request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/subtasks"))
                .POST(HttpRequest.BodyPublishers.ofString(jsonSubTask3))
                .build();
        client.send(request, HttpResponse.BodyHandlers.ofString());

        //удаляем 1 задачу
        request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/subtasks/2"))
                .DELETE()
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertEquals(taskManager.getSubTask().size(), 2, "не удалилась задача");

        // удаляем все задачи
        request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/subtasks"))
                .DELETE()
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertEquals(taskManager.getSubTask().size(), 0, "не удалилась задача");
    }

}
