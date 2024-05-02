package ru.job4j.thread;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;

public class Wget implements Runnable {
    private final String url;
    private final int speed;

    public Wget(String url, int speed) {
        this.url = url;
        this.speed = speed;
    }

    @Override
    public void run() {
        var startAt = System.currentTimeMillis();
        var file = new File("tmp.xml");
        try (var input = new URL(url).openStream();
             var output = new FileOutputStream(file)) {
            System.out.println("Open connection: " + (System.currentTimeMillis() - startAt) + " ms");
            var dataBuffer = new byte[512];
            int bytesRead;
            while ((bytesRead = input.read(dataBuffer, 0, dataBuffer.length)) != -1) {
                var downloadAt = System.nanoTime();
                output.write(dataBuffer, 0, bytesRead);
                long dowloadTimeNano = System.nanoTime() - downloadAt;
                System.out.println("Read 512 bytes : " + dowloadTimeNano + " nano.");
                if (speed != 6000) {
                    long calculateSleep = calculateSleep(dowloadTimeNano);
                    System.out.printf("Thread.sleep(%d)%n", calculateSleep);
                    Thread.sleep(calculateSleep);
                }

            }
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

    private long calculateSleep(long dowloadTimeNano) {
        return (512 * (1000000 / dowloadTimeNano)) / speed;
    }

    public static void main(String[] args) throws InterruptedException {
        if (validate(args)) {
            String url = args[0];
            int speed = Integer.parseInt(args[1]);
            Thread wget = new Thread(new Wget(url, speed));
            wget.start();
            wget.join();
        }

    }

    private static boolean validate(String[] args) {
        return (args[0].startsWith("https://") || args[0].startsWith("http://"))
                && args.length >= 2 && args[1].matches("-?\\d+");
    }
}