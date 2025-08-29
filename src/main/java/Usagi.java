import java.io.IOException;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.io.File;
import java.io.FileWriter;

public class Usagi {
    private static final String HORIZONTAL_LINE = "____________________________________________________________";

    private static void printLine() {
        System.out.println(HORIZONTAL_LINE);
    }

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

    // Base Task class
    abstract static class Task {
        protected String description;
        protected boolean isDone;

        Task(String description) {
            this.description = description;
            this.isDone = false;
        }

        Task(String description, boolean isDone) {
            this.description = description;
            this.isDone = isDone;
        }

        String getStatusIcon() {
            return isDone ? "[X]" : "[ ]";
        }

        void markAsDone() {
            this.isDone = true;
        }

        void markAsNotDone() {
            this.isDone = false;
        }

        @Override
        public String toString() {
            return getStatusIcon() + " " + description;
        }

        abstract String toFileString();
        abstract String getTaskType();
        abstract String getFullDescription();
    }

    static class Todo extends Task {
        Todo(String description) {
            super(description);
        }

        Todo(String description, boolean isDone) {
            super(description, isDone);
        }

        @Override
        String getTaskType() {
            return "[T]";
        }

        @Override
        String getFullDescription() {
            return getTaskType() + super.toString();
        }

        @Override
        public String toString() {
            return getFullDescription();
        }

        @Override
        String toFileString() {
            return "T | " + (isDone ? "1" : "0") + " | " + description;
        }
    }

    static class Deadline extends Task {
        protected String dueDate;

        Deadline(String description, String dueDate) {
            super(description);
            this.dueDate = dueDate;
        }

        Deadline(String description, boolean isDone, String dueDate) {
            super(description, isDone);
            this.dueDate = dueDate;
        }

        @Override
        String getTaskType() {
            return "[D]";        }

        @Override
        String getFullDescription() {
            return getTaskType() + super.toString() + " (by: " + dueDate + ")";
        }

        @Override
        public String toString() {
            return getFullDescription();
        }

        @Override
        String toFileString() {
            return "D | " + (isDone ? "1" : "0") + " | " + description + " | " + dueDate;
        }
    }

    // Event class
    static class Event extends Task {
        protected String start;
        protected String end;

        Event(String description, String start, String end) {
            super(description);
            this.start = start;
            this.end = end;
        }

        Event(String description, boolean isDone, String start, String end) {
            super(description, isDone);
            this.start = start;
            this.end = end;
        }

        @Override
        String getTaskType() {
            return "[E]";
        }

        @Override
        String getFullDescription() {
            return getTaskType() + super.toString() + " (from: " + start + " to: " + end + ")";
        }

        @Override
        public String toString() {
            return getFullDescription();
        }

        @Override
        String toFileString() {
            return "E | " + (isDone ? "1" : "0") + " | " + description + " | " + start + " to " + end;
        }
    }

    public static void main(String[] args) {
        ArrayList<Task> tasks = new ArrayList<>();
        Scanner scanner = new Scanner(System.in);

        printLine();
        System.out.println("Hello! I'm Usagi, your friendly task manager!");
        System.out.println("What can I do for you?");
        printLine();

    boolean isRunning = true;

    try {
        tasks = readFileContent("data/Usagi.txt");
    } catch (IOException e) {
        System.out.println("Something went wrong while reading the file: " + e.getMessage());
    }

    while (isRunning && scanner.hasNextLine()) {
        try {
            String input = scanner.nextLine().trim();

            if (input.equalsIgnoreCase("bye")) {
                isRunning = false;
            } else if (input.equalsIgnoreCase("list")) {
                displayTaskList(tasks);
            } else if (input.startsWith("mark ")) {
                handleMarkCommand(tasks, input, true);
            } else if (input.startsWith("unmark ")) {
                handleMarkCommand(tasks, input, false);
            } else if (input.startsWith("todo ")) {
                addTodoTask(tasks, input);
            } else if (input.startsWith("deadline ")) {
                addDeadlineTask(tasks, input);
            } else if (input.startsWith("event ")) {
                addEventTask(tasks, input);
            } else if (input.startsWith("delete ")) {
                deleteTask(tasks, input);
            } else if (!input.isEmpty()) {
                throw new InvalidCommandException();
            }
        } catch (UsagiException e) {
            printErrorMessage(e.getMessage());
        } catch (NoSuchElementException e) {
            // Handle end of input gracefully
            isRunning = false;
        }
    }

        printLine();
        System.out.println("Bye. Hope to see you again soon!");
        printLine();
        scanner.close();

        try {
            writeFileContent(tasks, "data/Usagi.txt");
        } catch (IOException e) {
            System.out.println("Something went wrong while reading the file: " + e.getMessage());
        }
    }

    private static void printErrorMessage(String message) {
        printLine();
        System.out.println("Oops! " + message);
        printLine();
    }

    private static void displayTaskList(ArrayList<Task> tasks) {
        printLine();
        if (tasks.isEmpty()) {
            System.out.println("Your list is empty! Add some tasks first.");
        } else {
            System.out.println("Here are the tasks in your list:");
            for (int i = 0; i < tasks.size(); i++) {
                System.out.println((i + 1) + "." + tasks.get(i).toString());
            }
        }
        printLine();
    }

    private static void addTodoTask(ArrayList<Task> tasks, String input) throws UsagiException {
        String description = input.substring(5).trim();
        if (description.isEmpty()) {
            throw new EmptyDescriptionException("todo");
        }

        Task newTask = new Todo(description);
        tasks.add(newTask);
        printLine();
        System.out.println("Got it. I've added this task:");
        System.out.println("  " + newTask.toString());
        System.out.println("Now you have " + tasks.size() + " tasks in the list.");
        printLine();
    }

    private static void addDeadlineTask(ArrayList<Task> tasks, String input) throws UsagiException {
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

        Task newTask = new Deadline(description, dueDate);
        tasks.add(newTask);
        printLine();
        System.out.println("Got it. I've added this task:");
        System.out.println("  " + newTask.toString());
        System.out.println("Now you have " + tasks.size() + " tasks in the list.");
        printLine();
    }

    private static void addEventTask(ArrayList<Task> tasks, String input) throws UsagiException {
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

        Task newTask = new Event(description, from, to);
        tasks.add(newTask);
        printLine();
        System.out.println("Got it. I've added this task:");
        System.out.println("  " + newTask.toString());
        System.out.println("Now you have " + tasks.size() + " tasks in the list.");
        printLine();
    }

    private static void handleMarkCommand(ArrayList<Task> tasks, String input, boolean markAsDone) throws UsagiException {
        try {
            String[] parts = input.split(" ", 2);
            if (parts.length < 2) {
                throw new InvalidFormatException((markAsDone ? "mark" : "unmark") + " <task-number>");
            }

            int taskNumber = Integer.parseInt(parts[1]) - 1;

            if (taskNumber < 0 || taskNumber >= tasks.size()) {
                throw new InvalidTaskNumberException(tasks.size());
            }

            Task task = tasks.get(taskNumber);

            if (markAsDone) {
                task.markAsDone();
                printLine();
                System.out.println("Nice! I've marked this task as done:");
                System.out.println("  " + task.toString());
                printLine();
            } else {
                task.markAsNotDone();
                printLine();
                System.out.println("OK, I've marked this task as not done yet:");
                System.out.println("  " + task.toString());
                printLine();
            }
        } catch (NumberFormatException e) {
            throw new InvalidFormatException((markAsDone ? "mark" : "unmark") + " <task-number> (must be a number)");
        }
    }

    private static void deleteTask(ArrayList<Task> tasks, String input) throws UsagiException {
        try {
            String[] parts = input.split(" ", 2);
            if (parts.length < 2) {
                throw new InvalidFormatException("delete <task-number>");
            }

            int taskNumber = Integer.parseInt(parts[1]) - 1;

            if (taskNumber < 0 || taskNumber >= tasks.size()) {
                throw new InvalidTaskNumberException(tasks.size());
            }

            Task removedTask = tasks.remove(taskNumber);
            printLine();
            System.out.println("Noted. I've removed this task:");
            System.out.println("  " + removedTask.toString());
            System.out.println("Now you have " + tasks.size() + " tasks in the list.");
            printLine();

        } catch (NumberFormatException e) {
            throw new InvalidFormatException("delete <task-number> (must be a number)");
        }
    }

    private static ArrayList<Task> readFileContent(String filePath) throws IOException {
        ArrayList<Task> tasks = new ArrayList<>();

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
                String[] times = parts[3].split(" to ", 2);
                t = new Event(desc, times[0], times[1]);
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

    private static void writeFileContent(ArrayList<Task> tasks, String filePath) throws IOException {
        FileWriter fw = new FileWriter(filePath);
        for (Task t : tasks) {
            fw.write(t.toFileString() + System.lineSeparator());
        }
        fw.close();
    }

}
