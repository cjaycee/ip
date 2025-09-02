package usagi.ui;

import usagi.task.Task;
import usagi.task.TaskList;

import java.util.Scanner;

/**
 * Handles all user interface interactions for the Usagi application.
 * Manages input/output operations, message formatting, and user feedback.
 */
public class Ui {

    private static final String APPLICATION_NAME = "usagi";
    private static final String HORIZONTAL_LINE = "____________________________________________________________";
    private Scanner scanner;

    /**
     * Creates a new Ui instance and initializes the input scanner.
     */
    public Ui() {
        this.scanner = new Scanner(System.in);
    }

    /**
     * Prints a horizontal line separator to the console.
     */
    public void printLine() {
        System.out.println(HORIZONTAL_LINE);
    }

    /**
     * Checks if there is another line of input available.
     *
     * @return True if there is another line available, false otherwise.
     */
    public boolean hasNextLine() {
        return this.scanner.hasNextLine();
    }

    /**
     * Reads the next line of user input.
     *
     * @return The next line of input as a string.
     */
    public String readCommand() {
        return scanner.nextLine();
    }

    /**
     * Displays the welcome greeting message to the user.
     */
    public void greet() {
        this.printLine();
        System.out.println("Hello! I'm " + APPLICATION_NAME + ", your friendly task manager!");
        System.out.println("What can I do for you?");
        this.printLine();
    }

    /**
     * Displays the goodbye message when the application ends.
     */
    public void endConvo() {
        this.printLine();
        System.out.println("Bye. Hope to see you again soon!");
        this.printLine();
    }

    /**
     * Closes the input scanner to free resources.
     */
    public void closeScanner() {
        this.scanner.close();
    }

    /**
     * Displays an error message with proper formatting.
     *
     * @param message The error message to display.
     */
    public void printErrorMessage(String message) {
        this.printLine();
        System.out.println("Oops! " + message);
        this.printLine();
    }

    /**
     * Displays the current list of tasks to the user.
     * Shows appropriate message if the list is empty.
     *
     * @param tasks The task list to display.
     */
    public void displayTaskList(TaskList tasks) {
        this.printLine();
        if (tasks.isEmpty()) {
            System.out.println("Your list is empty! Add some tasks first.");
        } else {
            System.out.println("Here are the tasks in your list:");
            for (int i = 0; i < tasks.size(); i++) {
                System.out.println((i + 1) + "." + tasks.get(i).toString());
            }
        }
        this.printLine();
    }

    /**
     * Displays confirmation message when a task is successfully added.
     *
     * @param tasks The task list containing the new task.
     * @param task The task that was added.
     */
    public void displayTaskAdded(TaskList tasks, Task task) {
        this.printLine();
        System.out.println("Got it. I've added this task:");
        System.out.println("  " + task);
        System.out.println("Now you have " + tasks.size() + " tasks in the list.");
        this.printLine();
    }

    /**
     * Displays confirmation message when a task is successfully deleted.
     *
     * @param tasks The task list after the task removal.
     * @param task The task that was deleted.
     */
    public void displayTaskDeleted(TaskList tasks, Task task) {
        this.printLine();
        System.out.println("Noted. I've removed this task:");
        System.out.println("  " + task.toString());
        System.out.println("Now you have " + tasks.size() + " tasks in the list.");
        this.printLine();
    }

    /**
     * Displays confirmation message when a task is marked as done.
     *
     * @param task The task that was marked as done.
     */
    public void displayMarked(Task task) {
        this.printLine();
        System.out.println("Nice! I've marked this task as done:");
        System.out.println("  " + task.toString());
        this.printLine();
    }

    /**
     * Displays confirmation message when a task is marked as not done.
     *
     * @param task The task that was marked as not done.
     */
    public void displayUnmarked(Task task) {
        this.printLine();
        System.out.println("OK, I've marked this task as not done yet:");
        System.out.println("  " + task.toString());
        this.printLine();
    }

    /**
     * Displays search results for tasks containing the specified keyword.
     *
     * @param matchingTasks TaskList containing tasks that match the search.
     * @param keyword The keyword that was searched for.
     */
    public void displaySearchResults(TaskList matchingTasks, String keyword) {
        this.printLine();
        if (matchingTasks.isEmpty()) {
            System.out.println("No tasks found containing: " + keyword);
        } else {
            System.out.println("Here are the matching tasks in your list:");
            for (int i = 0; i < matchingTasks.size(); i++) {
                System.out.println(" " + (i + 1) + "." + matchingTasks.get(i).toString());
            }
        }
        this.printLine();
    }
}