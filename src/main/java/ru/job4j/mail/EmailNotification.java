package ru.job4j.mail;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EmailNotification {
    ExecutorService pool;

    public EmailNotification() {
        this.pool = Executors.newFixedThreadPool(
                Runtime.getRuntime().availableProcessors());
    }

    public void close() {
        pool.shutdown();
        while (!pool.isTerminated()) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void emailTo(User user) {
        pool.submit(() -> send(subject(user), body(user), user.email()));
    }

    public void send(String subject, String body, String email) {

    }

    private String subject(User user) {
        return new StringBuilder("Notification ")
                .append(user.username())
                .append(" to email ")
                .append(user.email())
                .toString();
    }

    private String body(User user) {
        return new StringBuilder("Add a new event to ")
                .append(user.username())
                .toString();
    }

}
