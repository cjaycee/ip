import java.util.Scanner;
import java.util.ArrayList;

public class Usagi {

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

        // Abstract method to be implemented by subclasses
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

        System.out.println("____________________________________________________________");
        System.out.println("Hello! I'm Usagi");
        System.out.println("What can I do for you?");
        System.out.println("____________________________________________________________");

        String input;
        while (true) {
            input = scanner.nextLine().trim();

            if (input.equalsIgnoreCase("bye")) {
                break;
            } else if (input.equalsIgnoreCase("list")) {
                displayTaskList(tasks);
            } else if (input.startsWith("mark ")) {
                markTask(tasks, input, true);
            } else if (input.startsWith("unmark ")) {
                markTask(tasks, input, false);
            } else if (input.startsWith("todo ")) {
                addTodoTask(tasks, input);
            } else if (input.startsWith("deadline ")) {
                addDeadlineTask(tasks, input);
            } else if (input.startsWith("event ")) {
                addEventTask(tasks, input);
            } else if (!input.isEmpty()) {
                System.out.println("____________________________________________________________");
                System.out.println("Sorry, I don't understand that command. Please use one of:");
                System.out.println("  todo <description>");
                System.out.println("  deadline <description> /by <time>");
                System.out.println("  event <description> /from <start> /to <end>");
                System.out.println("  list");
                System.out.println("  mark <number>");
                System.out.println("  unmark <number>");
                System.out.println("  bye");
                System.out.println("____________________________________________________________");
            }
        }

        System.out.println("____________________________________________________________");
        System.out.println("Bye. Hope to see you again soon!");
        System.out.println("____________________________________________________________");
        scanner.close();
    }

    private static void displayTaskList(ArrayList<Task> tasks) {
        System.out.println("____________________________________________________________");
        if (tasks.isEmpty()) {
            System.out.println("Your list is empty! Add some tasks first.");
        } else {
            System.out.println("Here are the tasks in your list:");
            for (int i = 0; i < tasks.size(); i++) {
                System.out.println((i + 1) + "." + tasks.get(i).toString());
            }
        }
        System.out.println("____________________________________________________________");
    }

    private static void addTodoTask(ArrayList<Task> tasks, String input) {
        String description = input.substring(5).trim();
        if (description.isEmpty()) {
            System.out.println("____________________________________________________________");
            System.out.println("Oops! The description of a todo cannot be empty.");
            System.out.println("Usage: todo <description>");
            System.out.println("____________________________________________________________");
            return;
        }

        Task newTask = new Todo(description);
        tasks.add(newTask);
        System.out.println("____________________________________________________________");
        System.out.println("Got it. I've added this task:");
        System.out.println("  " + newTask.toString());
        System.out.println("Now you have " + tasks.size() + " tasks in the list.");
        System.out.println("____________________________________________________________");
    }

    private static void addDeadlineTask(ArrayList<Task> tasks, String input) {
        String withoutPrefix = input.substring(9).trim();
        String[] parts = withoutPrefix.split("/by", 2);

        if (parts.length < 2) {
            System.out.println("____________________________________________________________");
            System.out.println("Oops! Please use the format: deadline <description> /by <time>");
            System.out.println("Example: deadline return book /by Sunday");
            System.out.println("____________________________________________________________");
            return;
        }

        String description = parts[0].trim();
        String by = parts[1].trim();

        if (description.isEmpty() || by.isEmpty()) {
            System.out.println("____________________________________________________________");
            System.out.println("Oops! Both description and time are required for deadline.");
            System.out.println("Usage: deadline <description> /by <time>");
            System.out.println("____________________________________________________________");
            return;
        }

        Task newTask = new Deadline(description, by);
        tasks.add(newTask);
        System.out.println("____________________________________________________________");
        System.out.println("Got it. I've added this task:");
        System.out.println("  " + newTask.toString());
        System.out.println("Now you have " + tasks.size() + " tasks in the list.");
        System.out.println("____________________________________________________________");
    }

    private static void addEventTask(ArrayList<Task> tasks, String input) {
        String withoutPrefix = input.substring(6).trim();
        String[] parts = withoutPrefix.split("/from", 2);

        if (parts.length < 2) {
            System.out.println("____________________________________________________________");
            System.out.println("Oops! Please use the format: event <description> /from <start> /to <end>");
            System.out.println("Example: event project meeting /from Mon 2pm /to 4pm");
            System.out.println("____________________________________________________________");
            return;
        }

        String description = parts[0].trim();
        String[] timeParts = parts[1].split("/to", 2);

        if (timeParts.length < 2) {
            System.out.println("____________________________________________________________");
            System.out.println("Oops! Please include both start and end times.");
            System.out.println("Usage: event <description> /from <start> /to <end>");
            System.out.println("____________________________________________________________");
            return;
        }

        String from = timeParts[0].trim();
        String to = timeParts[1].trim();

        if (description.isEmpty() || from.isEmpty() || to.isEmpty()) {
            System.out.println("____________________________________________________________");
            System.out.println("Oops! Description, start time, and end time are all required for events.");
            System.out.println("Usage: event <description> /from <start> /to <end>");
            System.out.println("____________________________________________________________");
            return;
        }

        Task newTask = new Event(description, from, to);
        tasks.add(newTask);
        System.out.println("____________________________________________________________");
        System.out.println("Got it. I've added this task:");
        System.out.println("  " + newTask.toString());
        System.out.println("Now you have " + tasks.size() + " tasks in the list.");
        System.out.println("____________________________________________________________");
    }

    private static void markTask(ArrayList<Task> tasks, String input, boolean markAsDone) {
        try {
            int taskNumber = Integer.parseInt(input.split(" ")[1]) - 1;

            if (taskNumber >= 0 && taskNumber < tasks.size()) {
                Task task = tasks.get(taskNumber);

                if (markAsDone) {
                    task.markAsDone();
                    System.out.println("____________________________________________________________");
                    System.out.println("Nice! I've marked this task as done:");
                    System.out.println("  " + task.toString());
                    System.out.println("____________________________________________________________");
                } else {
                    task.markAsNotDone();
                    System.out.println("____________________________________________________________");
                    System.out.println("OK, I've marked this task as not done yet:");
                    System.out.println("  " + task.toString());
                    System.out.println("____________________________________________________________");
                }
            } else {
                System.out.println("____________________________________________________________");
                System.out.println("Invalid task number! Please enter a number between 1 and " + tasks.size());
                System.out.println("____________________________________________________________");
            }
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            System.out.println("____________________________________________________________");
            System.out.println("Please specify a valid task number after '" + (markAsDone ? "mark" : "unmark") + "'");
            System.out.println("Example: " + (markAsDone ? "mark" : "unmark") + " 2");
            System.out.println("____________________________________________________________");
        }
    }
}