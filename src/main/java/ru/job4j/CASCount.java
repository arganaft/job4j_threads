package ru.job4j;

import net.jcip.annotations.ThreadSafe;

import java.util.concurrent.atomic.AtomicInteger;

@ThreadSafe
public class CASCount {
    private final AtomicInteger count = new AtomicInteger();

    public void increment() {
        count.incrementAndGet();
    }

    public int get() {
        return count.get();
    }

    public static void main(String[] args) throws InterruptedException {
        CASCount casCount = new CASCount();
        for (int i = 0; i < 10000; i++) {
            new Thread(() -> {
                    casCount.increment();
            }).start();
        }
        Thread.sleep(2000);
        System.out.println(casCount.get());
    }


}