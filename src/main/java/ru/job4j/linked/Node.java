package ru.job4j.linked;

public class Node<T> {
    private Node<T> next;
    private final T value;
    private boolean isHasNext;

    public Node(T value) {
        this.value = value;
    }

    public Node<T> getNext() {
        return next;
    }

    public void setNext(Node<T> next) {
        if (!isHasNext) {
            this.next = next;
            isHasNext = true;
        }
    }

    public T getValue() {
        return value;
    }
}
