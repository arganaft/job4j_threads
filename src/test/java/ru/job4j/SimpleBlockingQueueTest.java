package ru.job4j;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SimpleBlockingQueueTest {

    @Test
    void whenSizeIs0() {
        assertThat(assertThrows(IllegalArgumentException.class, () -> new SimpleBlockingQueue<Integer>(0))
                .getMessage())
                .isEqualTo("Размер очереди не может быть 0 или отрицательным, размер очереди установлен по умолчанию на - 1");

    }
    @Test
    void whenSizeIsNegative() {
        assertThat(assertThrows(IllegalArgumentException.class, () -> new SimpleBlockingQueue<Integer>(-15))
                .getMessage())
                .isEqualTo("Размер очереди не может быть 0 или отрицательным, размер очереди установлен по умолчанию на - 1");

    }
    @Test
    void whenOfferNull() {
        SimpleBlockingQueue<Integer> sbq = new SimpleBlockingQueue<>(4);
        assertThat(assertThrows(NullPointerException.class, () -> sbq.offer(null))
                .getMessage())
                .isEqualTo("Попытка добавить NULL значение");

    }

    @Test
    void isProducerBlockedWhenQueueIsFull() throws InterruptedException {
        SimpleBlockingQueue<Integer> sbq = new SimpleBlockingQueue<>(1);
        sbq.offer(1);
        Thread producer = new Thread(() -> sbq.offer(2));
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


}