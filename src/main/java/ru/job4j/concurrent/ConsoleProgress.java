package ru.job4j.concurrent;

public class ConsoleProgress implements Runnable {

    @Override
    public void run() {
        try {
            int i = 0;
            var process = new char[] {'-', '\\', '|', '/'};
            while (!Thread.currentThread().isInterrupted()) {
                Thread.sleep(500);
                System.out.print("\r Loading ... " + process[i++]);
                if (i == 4) {
                    i = 0;
                }
        }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("\nLoading complete");
        }
    }

        public static void main(String[] args) throws InterruptedException {
        Thread progress = new Thread(new ConsoleProgress());
        progress.start();
        Thread.sleep(5000);
        progress.interrupt();
    }
}
