package ru.job4j.io;

import java.io.*;
import java.util.function.Predicate;

public class ParseFile {
    private final File file;
    private final int buffer;

    public ParseFile(File file, int buffer) {
        this.file = file;
        this.buffer = buffer;
    }

    public synchronized String content(Predicate<Character> filter) throws IOException {
        StringBuilder output = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(file), buffer)) {
            int data;
            while ((data = reader.read()) != -1) {
                if (filter.test((char) data)) {
                    output.append((char) data);
                }
            }
        }
        return output.toString();
    }
}
