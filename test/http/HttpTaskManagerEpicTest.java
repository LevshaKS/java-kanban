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

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.List;

public class HttpTaskManagerEpicTest {
    TaskManager taskManager = new InMemoryTaskManager(new InMemoryHistoryManager());
    HttpTaskServer httpTaskServer = new HttpTaskServer(taskManager);
    Gson gson = HttpTaskServer.getGson();

    public HttpTaskManagerEpicTest() throws IOException {
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

    @Test
    public void addEpic() throws IOException, InterruptedException {
        // cоздаем таску для отпрвки на сервер
        Epic epic = new Epic("Epic 1", "test epic1");
        String jsonEpic = gson.toJson(epic);
        //создаем http клиента
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/epics");
        //создраем запрос
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .POST(HttpRequest.BodyPublishers.ofString(jsonEpic))
                .build();
        //отправяем запрос, получаем в ответ код
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        //проверем значения
        Assertions.assertEquals(201, response.statusCode(), "Не верный код ответа");
        List<Epic> allTasks = taskManager.getEpic();
        Assertions.assertNotNull(allTasks, "задача не добавилась");
        Assertions.assertEquals(1, allTasks.size(), "колличество задач не совпадает");
    }

    @Test
    public void getEpic() throws IOException, InterruptedException {

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/epics/1"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(404, response.statusCode(), "не верный код ответа");

        Epic epic = new Epic("Epic 1", "test epic1");
        String jsonEpic = gson.toJson(epic);

        request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/epics"))
                .POST(HttpRequest.BodyPublishers.ofString(jsonEpic))
                .build();

        client.send(request, HttpResponse.BodyHandlers.ofString());

        request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/epics/1"))
                .GET()
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Epic epicFromJson = gson.fromJson(response.body(), Epic.class);
        Assertions.assertEquals(200, response.statusCode(), "не верный код ответа");
        Assertions.assertEquals(taskManager.getToIdEpic(1), epicFromJson, "не совпали Epic");

        //проверяем на несуществующую Епик
        request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/epics/121"))
                .GET()
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(404, response.statusCode(), "не верный код ответа");
    }

    @Test
    public void getAllEpic() throws IOException, InterruptedException {
        HttpResponse<String> response;
        HttpRequest request;
        Epic epic1 = new Epic("epic 1", "test epic1");
        String jsonEpic1 = gson.toJson(epic1);
        Epic epic2 = new Epic("epic 2", "test epic2");
        String jsonEpic2 = gson.toJson(epic2);


        //пустой запрос всех epic
        HttpClient client = HttpClient.newHttpClient();
        request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/epics"))
                .GET()
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals("[]", response.body(), "вернулся не пустой список задач");

        // добавлеям epic
        request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/epics"))
                .POST(HttpRequest.BodyPublishers.ofString(jsonEpic1))
                .build();
        client.send(request, HttpResponse.BodyHandlers.ofString());

        request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/epics"))
                .POST(HttpRequest.BodyPublishers.ofString(jsonEpic2))
                .build();
        client.send(request, HttpResponse.BodyHandlers.ofString());

        // проверяем возращение списка из всех тасков
        request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/epics"))
                .GET()
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        List<Epic> allEpic = gson.fromJson(response.body(), new HttpTaskManagerEpicTest.UserListTypeToken().getType());
        Assertions.assertEquals(taskManager.getEpic().size(), allEpic.size(),
                "не свопадает количество записей");
    }

    @Test
    public void getSubtaskToEpicId() throws IOException, InterruptedException {
        HttpResponse<String> response;
        HttpRequest request;
        HttpClient client = HttpClient.newHttpClient();

        Epic epic1 = new Epic("epic 1", "test epic1");
        String jsonEpic1 = gson.toJson(epic1);
        SubTask subTask1 = new SubTask("test subtask1_1", "subtask1 descriprion", 1,
                LocalDateTime.of(2025, 01, 01, 03, 00, 00), 25);
        SubTask subTask2 = new SubTask("test subtask1_1", "subtask1 descriprion", 1,
                LocalDateTime.of(2025, 02, 01, 03, 00, 00), 25);
        String jsonSubTask1 = gson.toJson(subTask1);
        String jsonSubTask2 = gson.toJson(subTask2);

        request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/epics"))
                .POST(HttpRequest.BodyPublishers.ofString(jsonEpic1))
                .build();
        client.send(request, HttpResponse.BodyHandlers.ofString());

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
        List<SubTask> allSubtaskToEpicId = gson.fromJson(response.body(), new UserListTypeToken().getType());
        Assertions.assertEquals(allSubtaskToEpicId.size(), taskManager.getToIdSubtaskInEpic(1).size(),
                "количество не совпало");
    }

    @Test
    public void deleteEpic() throws IOException, InterruptedException {
        HttpResponse<String> response;
        HttpRequest request;

        Epic epic1 = new Epic("epic 1", "test epic1");
        String jsonEpic1 = gson.toJson(epic1);
        Epic epic2 = new Epic("epic 2", "test epic2");
        String jsonEpic2 = gson.toJson(epic2);
        Epic epic3 = new Epic("epic 3", "test epic3");
        String jsonEpic3 = gson.toJson(epic3);
        HttpClient client = HttpClient.newHttpClient();

        request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/epics"))
                .POST(HttpRequest.BodyPublishers.ofString(jsonEpic1))
                .build();
        client.send(request, HttpResponse.BodyHandlers.ofString());

        request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/epics"))
                .POST(HttpRequest.BodyPublishers.ofString(jsonEpic2))
                .build();
        client.send(request, HttpResponse.BodyHandlers.ofString());

        request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/epics"))
                .POST(HttpRequest.BodyPublishers.ofString(jsonEpic3))
                .build();
        client.send(request, HttpResponse.BodyHandlers.ofString());

        //удаляем 1 epic
        request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/epics/2"))
                .DELETE()
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertEquals(taskManager.getEpic().size(), 2, "не удалилась задача");

        // удаляем все задачи
        request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/epics"))
                .DELETE()
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertEquals(taskManager.getEpic().size(), 0, "не удалилась задача");
    }


}
