package ru.job4j.io;

import java.io.*;

public class SaveToFile {
    private final File file;
    private final int buffer;

    public SaveToFile(File file, int buffer) {
        this.file = file;
        this.buffer = buffer;
    }

    public synchronized void saveContent(String content) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file), 8192)) {
            writer.write(content);
        }
    }
}
