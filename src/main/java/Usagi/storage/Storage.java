package Usagi.storage;

import Usagi.task.Deadline;
import Usagi.task.Event;
import Usagi.task.Task;
import Usagi.task.TaskList;
import Usagi.task.Todo;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

/**
 * Handles loading and saving of tasks to and from a file.
 * Manages file creation and directory setup for task persistence.
 */
public class Storage {

    private String filePath;

    /**
     * Creates a Storage instance with the specified file path.
     *
     * @param filePath Path to the file where tasks will be stored.
     */
    public Storage(String filePath) {
        this.filePath = filePath;
    }

    /**
     * Loads tasks from the storage file.
     * Creates the file and necessary directories if they don't exist.
     * Returns an empty task list if the file is new or empty.
     *
     * @return TaskList containing all loaded tasks.
     * @throws IOException If file operations fail.
     */
    public TaskList load() throws IOException {
        TaskList tasks = new TaskList();

        File file = new File(this.filePath);
        File folder = file.getParentFile();

        if (!folder.exists()) {
            folder.mkdirs();
        }

        if (!file.exists()) {
            file.createNewFile();
            return tasks; // empty list on first run
        }

        Scanner scanner = new Scanner(file);
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine().trim();
            if (line.isEmpty()) {
                continue;
            }

            String[] parts = line.split("\\|");
            for (int i = 0; i < parts.length; i++) {
                parts[i] = parts[i].trim();
            }

            String type = parts[0]; // T, D, or E
            boolean isDone = parts[1].equals("1");
            String description = parts[2];

            Task task = null;

            switch (type) {
            case "T":
                task = new Todo(description);
                break;
            case "D":
                task = new Deadline(description, parts[3]);
                break;
            case "E":
                task = new Event(description, parts[3], parts[4]);
                break;
            default:
                // Skip unknown task types
                break;
            }

            if (isDone && task != null) {
                task.markAsDone();
            }
            if (task != null) {
                tasks.add(task);
            }
        }
        scanner.close();
        return tasks;
    }

    /**
     * Saves all tasks from the task list to the storage file.
     * Overwrites the existing file content.
     *
     * @param tasks TaskList containing all tasks to be saved.
     * @throws IOException If file writing fails.
     */
    public void save(TaskList tasks) throws IOException {
        FileWriter fileWriter = new FileWriter(this.filePath);
        for (int i = 0; i < tasks.size(); i++) {
            fileWriter.write(tasks.get(i).toFileString() + System.lineSeparator());
        }
        fileWriter.close();
    }
}