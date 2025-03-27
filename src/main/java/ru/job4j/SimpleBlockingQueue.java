package ru.job4j;

import net.jcip.annotations.GuardedBy;
import net.jcip.annotations.ThreadSafe;

import java.util.LinkedList;
import java.util.Queue;

@ThreadSafe
public class SimpleBlockingQueue<T> {

    @GuardedBy("this")
    private final Queue<T> queue = new LinkedList<>();
    private final int size;

    public SimpleBlockingQueue(int size) {
        if (size <= 0) {
            throw new IllegalArgumentException("Размер очереди не может быть 0 или отрицательным");
        }
        this.size = size;
    }

    public void offer(T value) throws InterruptedException {
        synchronized (this) {
            while (queue.size() >= size) {
                this.wait();
            }
            queue.offer(value);
            this.notify();

        }
    }

    public T poll() throws InterruptedException {
        synchronized (this) {
            while (queue.isEmpty()) {
                this.wait();
            }
            T result = queue.poll();
            this.notify();
            return result;
        }
    }
}
