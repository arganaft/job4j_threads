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
            this.size = 1;
            throw new IllegalArgumentException("Размер очереди не может быть 0 или отрицательным, размер очереди установлен по умолчанию на - 1");
        } else {
            this.size = size;
        }
    }

    public void offer(T value) {
        synchronized (this) {
            if (value == null) {
                throw new NullPointerException("Попытка добавить NULL значение");
            }
            try {
                while (queue.size() >= size) {
                    this.wait();
                }
                queue.offer(value);
                this.notify();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public T poll() throws InterruptedException {
        synchronized (this) {
            while (queue.isEmpty()) {
                this.wait();
            }
            this.notify();
            return queue.poll();
        }
    }
}
