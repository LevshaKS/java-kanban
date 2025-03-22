package http;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import http.HttpTaskServer;
import managers.InMemoryHistoryManager;
import managers.InMemoryTaskManager;
import managers.TaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.SubTask;
import tasks.Task;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.List;

public class HttpTaskManagerGetListTest {

    TaskManager taskManager = new InMemoryTaskManager(new InMemoryHistoryManager());
    HttpTaskServer httpTaskServer = new HttpTaskServer(taskManager);
    Gson gson = HttpTaskServer.getGson();

    public HttpTaskManagerGetListTest() throws IOException {
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

    class UserListTypeToken extends TypeToken<List<Epic>> {
    }

    HttpResponse<String> response;
    HttpRequest request;
    HttpClient client = HttpClient.newHttpClient();
    final Task task = new Task("Task 1", "test task1", LocalDateTime.now(), 25);

    final Epic epic1 = new Epic("epic 1", "test epic1");

    final SubTask subTask1 = new SubTask("test subtask1_1", "subtask1 descriprion", 2,
            LocalDateTime.of(2025, 01, 01, 03, 00, 00), 25);
    final SubTask subTask2 = new SubTask("test subtask1_1", "subtask1 descriprion", 2,
            LocalDateTime.of(2025, 02, 01, 03, 00, 00), 25);

    @Test
    public void GetHistoryList() throws IOException, InterruptedException {
        taskManager.newTack(task);
        taskManager.newEpic(epic1);
        taskManager.newSubTask(subTask1);
        taskManager.newSubTask(subTask2);

        request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/history"))
                .GET()
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(200, response.statusCode(), "вернулся не правильный код");
        Assertions.assertEquals("[]", response.body(), "вернулся не пустой список задач");

        taskManager.getToIdTask(1);
        taskManager.getToIdSubTask(3);

        request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/history"))
                .GET()
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(200, response.statusCode(), "вернулся не правильный код");
        List<Task> allTaskHistory = gson.fromJson(response.body(), new UserListTypeToken().getType());
        Assertions.assertEquals(taskManager.getHistory().size(), allTaskHistory.size(), "вернулся не пустой список задач");
    }

    @Test
    public void GetPrioritizedList() throws IOException, InterruptedException {
        request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/prioritized"))
                .GET()
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(200, response.statusCode(), "вернулся не правильный код");
        Assertions.assertEquals("[]", response.body(), "вернулся не пустой список задач");

        taskManager.newTack(task);
        taskManager.newEpic(epic1);
        taskManager.newSubTask(subTask1);
        taskManager.newSubTask(subTask2);

        request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/prioritized"))
                .GET()
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(200, response.statusCode(), "вернулся не правильный код");
        List<Task> allTaskHistory = gson.fromJson(response.body(), new UserListTypeToken().getType());
        Assertions.assertEquals(taskManager.getPrioritizedTasks().size(), allTaskHistory.size(), "вернулся не пустой список задач");
    }

}
