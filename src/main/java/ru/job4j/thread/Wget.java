package ru.job4j.thread;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;

public class Wget implements Runnable {
    private final String url;
    private final int speed;
    private final String tempFile;

    public Wget(String url, int speed, String tempFile) {
        this.url = url;
        this.speed = speed;
        this.tempFile = tempFile;
    }

    @Override
    public void run() {
        var startAt = System.currentTimeMillis();
        var file = new File(tempFile);
        try (var input = new URL(url).openStream();
             var output = new FileOutputStream(file)) {
            long startDowload = System.currentTimeMillis();
            System.out.println("Open connection: " + (System.currentTimeMillis() - startAt) + " ms");
            var dataBuffer = new byte[512];
            int bytesRead;
            int dowloadByte = 0;
            var downloadAt = System.currentTimeMillis();
            while ((bytesRead = input.read(dataBuffer, 0, dataBuffer.length)) != -1) {
                output.write(dataBuffer, 0, bytesRead);
                dowloadByte += bytesRead;
                if (dowloadByte >= speed) {
                    long dowloadTimeMillis = System.currentTimeMillis() - downloadAt;
                    System.out.printf("Dowload %d Byte in: %d Millis%n", dowloadByte, dowloadTimeMillis);
                    long sleepTime = (1000 - dowloadTimeMillis) * (dowloadByte / speed);
                    System.out.printf("Thread.sleep(%d)%n", sleepTime);
                    Thread.sleep(sleepTime);
                    dowloadByte = 0;
                    downloadAt = System.currentTimeMillis();
                }
            }
            long finish = System.currentTimeMillis() - startDowload;
            System.out.printf("Finish in %d%n", finish);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        try {
            System.out.println(Files.size(file.toPath()) + " bytes");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public static void main(String[] args) throws InterruptedException {
        if (args.length < 2) {
            throw new IllegalArgumentException("Не определены аргументы");
        }
        int urlIndex = validateURL(args);
        int speedIndex = validateSpeed(args);
        if (urlIndex == -1 && speedIndex == -1) {
            throw new IllegalArgumentException("Переданные аргументы не содержат URL файла(которое нужно загрузить) и число(скорость загрузки)");
        }
        String url = args[urlIndex];
        int speed = Integer.parseInt(args[speedIndex]);
        Thread wget = new Thread(new Wget(url, speed, "tmp.txt"));
        wget.start();
        wget.join();

    }

    private static int validateURL(String[] args) {
        int urlIndex = -1;
        for (int i = 0; i < args.length; i++) {
            if (args[i].startsWith("https://") || args[i].startsWith("http://")) {
                urlIndex = i;
                break;
            }
        }
        return urlIndex;
    }

    private static int validateSpeed(String[] args) {
        int speedIndex = -1;
        for (int i = 0; i < args.length; i++) {
            if (args[i].matches("-?\\d+")) {
                speedIndex = i;
                break;
            }
        }
        return speedIndex;
    }
}