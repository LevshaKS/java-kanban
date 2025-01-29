package managers;

import tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class inMemoryHistoryManager implements HistoryManager {

    private static class Node {
        Task task; //задача
        Node next; // следующая Нода
        Node prev; // предыдущая Нода

        public Node(Node prev, Task task, Node next) { // конструктор Ноды
            this.prev = prev;
            this.task = task;
            this.next = next;
        }
    }

    private final Map<Integer, Node> nodeMap = new HashMap<>();  //мапа ид тасков с нодами тасков
    private Node head;  //голова Нода
    private Node tail;  // хвост Нода

    public inMemoryHistoryManager() {
    }

    @Override
    public void add(Task task) { //добавялем задачу в историю
        if (task == null)
            return;
        linkAddLast(task);
    }

    private void linkAddLast(Task task) { //добавляем новую задачу в список Ноды
        if (nodeMap.containsKey(task.getId()))
            removeNode(nodeMap.get(task.getId()));

        final Node node = new Node(tail, task, null);
        if (head == null)
            head = tail = node;
        else {
            tail = node;
            tail.prev.next = node;
        }
        nodeMap.put(task.getId(), node);
    }

    private ArrayList<Task> getTasks() { //формируем массив нодов в истории
        ArrayList<Task> listPrintNode = new ArrayList<>();
        Node node = head;
        while (node != null) {
            listPrintNode.add(node.task);
            node = node.next;
        }
        return listPrintNode;
    }

    @Override
    public List<Task> getHistory() {
        return getTasks();
    }

    @Override
    public void remove(int id) {
        if (nodeMap.containsKey(id))
            removeNode(nodeMap.get(id));
    }

    private void removeNode(Node node) { ///удаляем повторяущию ноду из истории
        nodeMap.remove(node.task.getId());

        if (node.prev == null) {
            head = node.next;
            node.next.prev = null;
        } else if (node.prev != null && node.next != null) {
            node.prev.next = node.next;
            node.next.prev = node.prev;
        } else {
            tail = node.prev;
            node.prev.next = null;

        }
    }

}
