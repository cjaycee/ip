import java.io.IOException;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.io.File;
import java.io.FileWriter;

public class Usagi {

    // Custom exception classes
    static class UsagiException extends Exception {
        public UsagiException(String message) {
            super(message);
        }
    }

    static class EmptyDescriptionException extends UsagiException {
        public EmptyDescriptionException(String taskType) {
            super("The description of a " + taskType + " cannot be empty.");
        }
    }

    static class InvalidCommandException extends UsagiException {
        public InvalidCommandException() {
            super("I'm sorry, but I don't know what that means. Please try again!");
        }
    }


    static class InvalidFormatException extends UsagiException {
        public InvalidFormatException(String correctFormat) {
            super("Please use the correct format: " + correctFormat);
        }
    }

    static class InvalidTaskNumberException extends UsagiException {
        public InvalidTaskNumberException(int maxTasks) {
            super("Please enter a valid task number between 1 and " + maxTasks);
        }
    }

    public static void main(String[] args) {
        TaskList tasks = new TaskList();
        Ui ui = new Ui();
        boolean isRunning = true;

        ui.greet();

        try {
            tasks = readFileContent("data/Usagi.txt");
        } catch (IOException e) {
            System.out.println("Something went wrong while reading the file: " + e.getMessage());
        }

        while (isRunning && ui.hasNextLine()) {
            try {
                String input = ui.readCommand().trim();

                if (input.equalsIgnoreCase("bye")) {
                    isRunning = false;
                } else if (input.equalsIgnoreCase("list")) {
                    ui.displayTaskList(tasks);
                } else if (input.startsWith("mark ")) {
                    handleMarkCommand(ui, tasks, input, true);
                } else if (input.startsWith("unmark ")) {
                    handleMarkCommand(ui, tasks, input, false);
                } else if (input.startsWith("todo ")) {
                    addTodoTask(ui, tasks, input);
                } else if (input.startsWith("deadline ")) {
                    addDeadlineTask(ui, tasks, input);
                } else if (input.startsWith("event ")) {
                    addEventTask(ui, tasks, input);
                } else if (input.startsWith("delete ")) {
                    deleteTask(ui, tasks, input);
                } else if (!input.isEmpty()) {
                    throw new InvalidCommandException();
                }
            } catch (UsagiException e) {
                ui.printErrorMessage(e.getMessage());
            } catch (NoSuchElementException e) {
                // Handle end of input gracefully
                isRunning = false;
            }
        }

        ui.endConvo();

        try {
            writeFileContent(tasks, "data/Usagi.txt");
        } catch (IOException e) {
            System.out.println("Something went wrong while reading the file: " + e.getMessage());
        }
    }

    private static void addTodoTask(Ui ui, TaskList tasks, String input) throws UsagiException {
        String description = input.substring(5).trim();
        if (description.isEmpty()) {
            throw new EmptyDescriptionException("todo");
        }

        Task t = new Todo(description);
        tasks.add(t);
        ui.displayTaskAdded(tasks, t);
    }

    private static void addDeadlineTask(Ui ui, TaskList tasks, String input) throws UsagiException {
        String withoutPrefix = input.substring(9).trim();
        if (withoutPrefix.isEmpty()) {
            throw new EmptyDescriptionException("deadline");
        }

        String[] parts = withoutPrefix.split("/by", 2);

        if (parts.length < 2) {
            throw new InvalidFormatException("deadline <description> /by <time>");
        }

        String description = parts[0].trim();
        String dueDate = parts[1].trim();

        if (description.isEmpty()) {
            throw new EmptyDescriptionException("deadline");
        }
        if (dueDate.isEmpty()) {
            throw new InvalidFormatException("deadline <description> /by <time> (time cannot be empty)");
        }

        Task t = new Deadline(description, dueDate);
        tasks.add(t);
        ui.displayTaskAdded(tasks, t);
    }

    private static void addEventTask(Ui ui, TaskList tasks, String input) throws UsagiException {
        String withoutPrefix = input.substring(6).trim();
        if (withoutPrefix.isEmpty()) {
            throw new EmptyDescriptionException("event");
        }

        String[] parts = withoutPrefix.split("/from", 2);

        if (parts.length < 2) {
            throw new InvalidFormatException("event <description> /from <start> /to <end>");
        }

        String description = parts[0].trim();
        String[] timeParts = parts[1].split("/to", 2);

        if (timeParts.length < 2) {
            throw new InvalidFormatException("event <description> /from <start> /to <end>");
        }

        String from = timeParts[0].trim();
        String to = timeParts[1].trim();

        if (description.isEmpty()) {
            throw new EmptyDescriptionException("event");
        }
        if (from.isEmpty() || to.isEmpty()) {
            throw new InvalidFormatException("event <description> /from <start> /to <end> (times cannot be empty)");
        }

        Task t = new Event(description, from, to);
        tasks.add(t);
        ui.displayTaskAdded(tasks, t);
    }

    private static void handleMarkCommand(Ui ui, TaskList tasks, String input, boolean markAsDone) throws UsagiException {
        try {
            String[] parts = input.split(" ", 2);
            if (parts.length < 2) {
                throw new InvalidFormatException((markAsDone ? "mark" : "unmark") + " <task-number>");
            }

            int taskNumber = Integer.parseInt(parts[1]) - 1;

            if (taskNumber < 0 || taskNumber >= tasks.size()) {
                throw new InvalidTaskNumberException(tasks.size());
            }

            Task t = tasks.get(taskNumber);

            if (markAsDone) {
                t.markAsDone();
                ui.displayMarked(t);
            } else {
                t.markAsNotDone();
                ui.displayUnmarked(t);
            }
        } catch (NumberFormatException e) {
            throw new InvalidFormatException((markAsDone ? "mark" : "unmark") + " <task-number> (must be a number)");
        }
    }

    private static void deleteTask(Ui ui, TaskList tasks, String input) throws UsagiException {
        try {
            String[] parts = input.split(" ", 2);
            if (parts.length < 2) {
                throw new InvalidFormatException("delete <task-number>");
            }

            int taskNumber = Integer.parseInt(parts[1]) - 1;

            if (taskNumber < 0 || taskNumber >= tasks.size()) {
                throw new InvalidTaskNumberException(tasks.size());
            }

            Task t = tasks.remove(taskNumber);
            ui.displayTaskDeleted(tasks, t);

        } catch (NumberFormatException e) {
            throw new InvalidFormatException("delete <task-number> (must be a number)");
        }
    }

    private static TaskList readFileContent(String filePath) throws IOException {
        TaskList tasks = new TaskList();

        File f = new File(filePath);
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

    private static void writeFileContent(TaskList tasks, String filePath) throws IOException {
        FileWriter fw = new FileWriter(filePath);
        for (int i = 0; i < tasks.size(); i++) {
            fw.write(tasks.get(i).toFileString() + System.lineSeparator());
        }
        fw.close();
    }

}
