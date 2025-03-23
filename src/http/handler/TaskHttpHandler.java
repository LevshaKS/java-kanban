package http.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import exceptions.ManagerException;
import managers.TaskManager;
import tasks.Task;

import java.io.IOException;
import java.util.List;


public class TaskHttpHandler extends BaseHttpHandler implements HttpHandler {

      public TaskHttpHandler(TaskManager manager) {
        this.manager = manager;
        gson = getGson();
    }

    public Integer getIdFromPath(String path) {
        String[] paths = path.split("/");
        if (paths.length > 2)
            return Integer.parseInt(paths[2]);
        else return null;
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        final String path = httpExchange.getRequestURI().getPath();
        final Integer idPath = getIdFromPath(path);
        final String method = httpExchange.getRequestMethod();
        switch (method) {
            case ("GET"): {
                if ((path.split("/").length > 3))
                    sendMethodNotAllowed(httpExchange);
                else {
                    if (idPath == null) {
                        List<Task> tasks = manager.getTasks();
                        String response = gson.toJson(tasks);
                        sendText(httpExchange, response);
                        System.out.println("возвращаем все таски");
                        return;
                    }
                    Task task = manager.getToIdTask(idPath);
                    if (task != null) {
                        String response = gson.toJson(task);
                        sendText(httpExchange, response);
                        System.out.println("возвращаем  таску " + idPath);
                    } else {
                        sendNotFound(httpExchange);
                        System.out.println("таска id=" + idPath + " нет");
                    }
                }
                break;
            }
            case ("POST"): {
                if (idPath == null) {
                    String json = readText(httpExchange);
                    Task task = gson.fromJson(json, Task.class);
                    final int id = task.getId();
                    if (id > 0) {
                        manager.updateTask(id, task);
                        System.out.println("обновили таску id=" + id);
                        sendOk(httpExchange);
                    } else
                        try {
                            manager.newTack(task);
                            System.out.println("Задача создана");
                            sendOk(httpExchange);
                        } catch (ManagerException e) {
                            System.out.println("пересекается с существующими");
                            sendHasInteractions(httpExchange);
                        }
                } else {
                    sendMethodNotAllowed(httpExchange);
                }
                break;
            }
            case ("DELETE"): {
                if (idPath == null) {
                    manager.clearTaskList();
                    System.out.println("удалили все таски");
                    sendText(httpExchange, "все задачи удалены");
                    return;
                } else {
                    if (manager.getToIdTask(idPath) != null) {
                        manager.removeToIdTask(idPath);
                        System.out.println("удалили таску id= " + idPath);
                        sendText(httpExchange, "удалена задача id= " + idPath);
                    } else
                        sendNotFound(httpExchange);
                }
                break;
            }
            default:
                sendBadRequest(httpExchange);
        }
    }
}

