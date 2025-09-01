package Usagi.storage;

import Usagi.task.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class Storage {

    public String filePath;

    public Storage(String filepath) {
        this.filePath = filepath;
    }

    public TaskList load() throws IOException {
        TaskList tasks = new TaskList();

        File f = new File(this.filePath);
        File folder = f.getParentFile();

        if (!folder.exists()) {
            folder.mkdirs();
        }

        if (!f.exists()) {
            f.createNewFile();
            return tasks; // empty list on first run
        }

        Scanner s = new Scanner(f);
        while (s.hasNextLine()) {
            String line = s.nextLine().trim();
            if (line.isEmpty()) continue;

            String[] parts = line.split("\\|");
            for (int i = 0; i < parts.length; i++) {
                parts[i] = parts[i].trim();
            }

            String type = parts[0]; // T, D, or E
            boolean isDone = parts[1].equals("1");
            String desc = parts[2];

            Task t = null;

            switch (type) {
            case "T":
                t = new Todo(desc);
                break;
            case "D":
                t = new Deadline(desc, parts[3]);
                break;
            case "E":
                t = new Event(desc, parts[3], parts[4]);
                break;
            }

            if (isDone && t != null) {
                t.markAsDone();
            }
            if (t != null) {
                tasks.add(t);
            }
        }
        s.close();
        return tasks;
    }

    public void save(TaskList tasks) throws IOException {
        FileWriter fw = new FileWriter(this.filePath);
        for (int i = 0; i < tasks.size(); i++) {
            fw.write(tasks.get(i).toFileString() + System.lineSeparator());
        }
        fw.close();
    }

}
