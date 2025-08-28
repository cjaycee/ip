import java.util.Scanner;
import java.util.ArrayList;
import java.util.NoSuchElementException;

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

        abstract String getTaskType();
        abstract String getFullDescription();
    }

    // Todo class
    static class Todo extends Task {
        Todo(String description) {
            super(description);
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
    }

    // Deadline class
    static class Deadline extends Task {
        protected String by;

        Deadline(String description, String by) {
            super(description);
            this.by = by;
        }

        @Override
        String getTaskType() {
            return "[D]";
        }

        @Override
        String getFullDescription() {
            return getTaskType() + super.toString() + " (by: " + by + ")";
        }

        @Override
        public String toString() {
            return getFullDescription();
        }
    }

    // Event class
    static class Event extends Task {
        protected String from;
        protected String to;

        Event(String description, String from, String to) {
            super(description);
            this.from = from;
            this.to = to;
        }

        @Override
        String getTaskType() {
            return "[E]";
        }

        @Override
        String getFullDescription() {
            return getTaskType() + super.toString() + " (from: " + from + " to: " + to + ")";
        }

        @Override
        public String toString() {
            return getFullDescription();
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
        String by = parts[1].trim();

        if (description.isEmpty()) {
            throw new EmptyDescriptionException("deadline");
        }
        if (by.isEmpty()) {
            throw new InvalidFormatException("deadline <description> /by <time> (time cannot be empty)");
        }

        Task newTask = new Deadline(description, by);
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
}