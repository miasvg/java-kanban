package src.Service;

import src.Models.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private static class Node{
        Task task;
        Node prev;
        Node next;
        public Node(Task task){
            this.task = task;
        }
    }
    private Node head;
    private Node tail;
    private HashMap<Integer, Node> historyMap = new HashMap<>();

    private static final int MAX_HISTORY_SIZE = 10;

    private final List<Task> history = new ArrayList<>();

    @Override
    public void add(Task task) {
        remove(task.getId());
        Node newNode = new Node(task);
        if (tail == null) {
            head = newNode;
            tail = newNode;
        } else {
            tail.next = newNode;
            newNode.prev = tail;
            tail = newNode;
        }
        historyMap.put(task.getId(), newNode);
        history.add(task);
        if (history.size() > MAX_HISTORY_SIZE) {
            history.remove(0);
        }
    }


    @Override
    public List<Task> getHistory() {
        List<Task> history = new ArrayList<>();
        Node current = head;
        while (current != null) {
            history.add(current.task);
            current = current.next;
        }
        return history;
    }
    private void removeNode(Node node) {
        if (node.prev != null) {
            node.prev.next = node.next;
        } else {
            head = node.next; // Если удаляем голову, сдвигаем её
        }
        if (node.next != null) {
            node.next.prev = node.prev;
        } else {
            tail = node.prev; // Если удаляем хвост, сдвигаем его
        }
    }
    @Override
    public void remove(int id) {
        Node node = historyMap.get(id);
        if (node != null) {
            removeNode(node);
            historyMap.remove(id);
        }
    }

}
