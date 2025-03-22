package http.handler;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import exceptions.ManagerException;
import managers.TaskManager;
import tasks.Epic;
import tasks.SubTask;

import java.io.IOException;
import java.util.List;

public class EpicTaskHandler extends BaseHttpHandler implements HttpHandler {

    private final TaskManager manager;
    private final Gson gson;

    public EpicTaskHandler(TaskManager manager) {
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
                        List<Epic> epics = manager.getEpic();
                        String response = gson.toJson(epics);
                        sendText(httpExchange, response);
                        System.out.println("возвращаем все епики");
                        return;
                    }
                    Epic epic = manager.getToIdEpic(idPath);
                    if (epic != null) {
                        if ((path.split("/").length > 3) && (path.split("/")[3].equals("subtasks"))) {
                            List<SubTask> subTaskList = manager.getToIdSubtaskInEpic(idPath);
                            String response = gson.toJson(subTaskList);
                            sendText(httpExchange, response);
                            System.out.println("возвращаем список подзадачть по епик id= " + idPath);
                        } else if (path.split("/").length == 3) {
                            String response = gson.toJson(epic);
                            sendText(httpExchange, response);
                            System.out.println("возвращаем  епик " + idPath);
                        } else {
                            sendMethodNotAllowed(httpExchange);
                            System.out.println("нет такого действия");
                        }

                    } else {
                        sendNotFound(httpExchange);
                        System.out.println("епик id=" + idPath + " нет");
                    }

                }
                sendMethodNotAllowed(httpExchange);
                break;
            }
            case ("POST"): {
                if (idPath == null) {
                    String json = readText(httpExchange);
                    Epic epic = gson.fromJson(json, Epic.class);
                    final int id = epic.getId();
                    if (id > 0) {
                        System.out.println("обновлять епик нельзя");
                        sendNotFound(httpExchange);
                    } else
                        try {
                            manager.newEpic(epic);
                            System.out.println("Епик создан");
                            sendOk(httpExchange);
                        } catch (ManagerException e) {
                            System.out.println("пересекается с существующими");
                            sendHasInteractions(httpExchange);
                        }
                } else {
                    System.out.println("нет такого действия");
                    sendMethodNotAllowed(httpExchange);
                }
                break;
            }
            case ("DELETE"): {
                if (idPath == null) {
                    manager.clearEpicList();
                    System.out.println("удалили все Епики");
                    sendText(httpExchange, "все задачи(Епик) удалены");
                    return;
                } else {
                    if (manager.getToIdEpic(idPath) != null) {
                        manager.removeToIdEpic(idPath);
                        System.out.println("удалили Епик id= " + idPath);
                        sendText(httpExchange, "удалена задача id= " + idPath);
                    } else
                        sendNotFound(httpExchange);
                }
            }
            default:
                sendBadRequest(httpExchange);
        }
    }
}
