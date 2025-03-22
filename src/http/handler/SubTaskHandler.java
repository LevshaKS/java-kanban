package http.handler;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import exceptions.ManagerException;
import managers.TaskManager;
import tasks.SubTask;

import java.io.IOException;
import java.util.List;


public class SubTaskHandler extends BaseHttpHandler implements HttpHandler {

    private final TaskManager manager;
    private final Gson gson;

    public SubTaskHandler(TaskManager manager) {
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
                        List<SubTask> subTask = manager.getSubTask();
                        String response = gson.toJson(subTask);
                        sendText(httpExchange, response);
                        System.out.println("возвращаем все саптаски");
                        return;
                    }
                    SubTask subTasktask = manager.getToIdSubTask(idPath);
                    if (subTasktask != null) {
                        String response = gson.toJson(subTasktask);
                        sendText(httpExchange, response);
                        System.out.println("возвращаем  саптаску " + idPath);
                    } else {
                        sendNotFound(httpExchange);
                        System.out.println("саптаски id=" + idPath + " нет");
                    }
                }
                sendMethodNotAllowed(httpExchange);
                break;
            }
            case ("POST"): {
                if (idPath == null) {
                    String json = readText(httpExchange);
                    SubTask subTask = gson.fromJson(json, SubTask.class);
                    final int id = subTask.getId();
                    if (id > 0) {
                        manager.updateSubTask(id, subTask);
                        System.out.println("обновили саптаску id=" + id);
                        sendOk(httpExchange);
                    } else
                        try {
                            manager.newSubTask(subTask);
                            System.out.println("подзадача создана");
                            sendOk(httpExchange);
                        } catch (ManagerException e) {
                            System.out.println("пересекается с существующими");
                            sendHasInteractions(httpExchange);
                        }
                } else
                    sendMethodNotAllowed(httpExchange);
                break;
            }
            case ("DELETE"): {
                if (idPath == null) {
                    manager.clearSubTaskList();
                    System.out.println("удалили все саптаски");
                    sendText(httpExchange, "все подзадачи удалены");
                    return;
                } else {
                    if (manager.getToIdSubTask(idPath) != null) {
                        manager.removeToIdSubTask(idPath);
                        System.out.println("удалили саптаску id= " + idPath);
                        sendText(httpExchange, "удалена подзадача id= " + idPath);
                    } else
                        sendMethodNotAllowed(httpExchange);
                }
                break;
            }
            default:
                sendBadRequest(httpExchange);
        }
    }
}
