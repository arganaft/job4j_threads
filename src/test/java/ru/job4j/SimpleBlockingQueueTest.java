package ru.job4j;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SimpleBlockingQueueTest {

    @Test
    void whenSizeIs0() {
        assertThat(assertThrows(IllegalArgumentException.class, () -> new SimpleBlockingQueue<Integer>(0))
                .getMessage())
                .isEqualTo("Размер очереди не может быть 0 или отрицательным");

    }
    @Test
    void whenSizeIsNegative() {
        assertThat(assertThrows(IllegalArgumentException.class, () -> new SimpleBlockingQueue<Integer>(-15))
                .getMessage())
                .isEqualTo("Размер очереди не может быть 0 или отрицательным");

    }

    @Test
    void isProducerBlockedWhenQueueIsFull() throws InterruptedException {
        SimpleBlockingQueue<Integer> sbq = new SimpleBlockingQueue<>(1);
        sbq.offer(1);
        Thread producer = new Thread(() -> {
            try {
                sbq.offer(2);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        producer.start();
        long deadLine = System.currentTimeMillis() + 5000;
        while (producer.getState().equals(Thread.State.RUNNABLE)) {
            if (System.currentTimeMillis() > deadLine) {
                fail("Поток не перешёл в WAITING за 5 секунд");
            }
            producer.join(10);
        }
        assertThat(producer.getState()).isEqualTo(Thread.State.WAITING);
    }

    @Test
    void isConsumerBlockedWhenQueueIsEmpty() throws InterruptedException {
        SimpleBlockingQueue<Integer> sbq = new SimpleBlockingQueue<>(1);
        sbq.offer(1);
        sbq.poll();
        Thread consumer = new Thread(() -> {
            try {
                sbq.poll();
            } catch (InterruptedException e) {
                e.printStackTrace();
                Thread.currentThread().interrupt();
            }
        });
        consumer.start();
        long deadLine = System.currentTimeMillis() + 5000;
        while (consumer.getState().equals(Thread.State.RUNNABLE)) {
            if (System.currentTimeMillis() > deadLine) {
                fail("Поток не перешёл в WAITING за 5 секунд");
            }
            consumer.join(10);
        }
        assertThat(consumer.getState()).isEqualTo(Thread.State.WAITING);
    }

    @Test
    public void whenFetchAllThenGetIt() throws InterruptedException {
        final List<Integer> buffer = Collections.synchronizedList(new ArrayList<>());
        final SimpleBlockingQueue<Integer> queue = new SimpleBlockingQueue<>(2);
            Thread producer = new Thread(
                    () -> IntStream.range(0, 5).forEach(i -> {
                        try {
                            queue.offer(i);
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                    })
            );
            producer.start();
            Thread consumer = new Thread(
                    () -> {
                        while (!queue.isEmpty() || !Thread.currentThread().isInterrupted()) {
                            try {
                                buffer.add(queue.poll());
                            } catch (InterruptedException e) {
                                Thread.currentThread().interrupt();
                                break;
                            }
                        }
                    }
            );
            consumer.start();
            producer.join();
            consumer.interrupt();
            consumer.join();

            assertThat(buffer.size()).isEqualTo(5);
            assertThat(buffer).containsExactlyInAnyOrder(0, 1, 2, 3, 4);
    }
}