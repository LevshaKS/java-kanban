package http.handler;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import managers.TaskManager;
import tasks.Task;

import java.io.IOException;
import java.util.List;

public class HistoryHandler extends BaseHttpHandler implements HttpHandler {

    private final TaskManager manager;
    private final Gson gson;

    public HistoryHandler(TaskManager manager) {
        this.manager = manager;
        this.gson = getGson();
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        final String path = httpExchange.getRequestURI().getPath();
        final String method = httpExchange.getRequestMethod();
        switch (method) {
            case ("GET"): {
                List<Task> historyList = manager.getHistory();
                String repsone = gson.toJson(historyList);
                sendText(httpExchange, repsone);
                System.out.println("выводим историю");
            }
            default:
                sendMethodNotAllowed(httpExchange);
        }
    }
}
