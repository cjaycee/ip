import java.util.Scanner;
import java.util.ArrayList;

public class Usagi {

    // Task class to represent each task with its description and status
    static class Task {
        String description;
        boolean isDone;

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
            } else if (!input.isEmpty()) {
                addTask(tasks, input);
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

    private static void addTask(ArrayList<Task> tasks, String description) {
        Task newTask = new Task(description);
        tasks.add(newTask);
        System.out.println("____________________________________________________________");
        System.out.println("added: " + description);
        System.out.println("____________________________________________________________");
    }

    private static void markTask(ArrayList<Task> tasks, String input, boolean markAsDone) {
        try {
            // Extract the task number from the input
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