package usagi.ui;

import usagi.task.Task;
import usagi.task.TaskList;

import java.util.Scanner;

/**
 * Handles all user interface interactions for the Usagi application.
 * Manages input/output operations, message formatting, and user feedback.
 */
public class Ui {

    private static final String APPLICATION_NAME = "Usagi";
    private static final String HORIZONTAL_LINE = "____________________________________________________________";
    private Scanner scanner;
    private String output;

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
     * Displays the welcome greeting message with available commands.
     */
    public void greet() {
        this.output = HORIZONTAL_LINE +
                System.lineSeparator() +
                "Hello! I'm " + APPLICATION_NAME + ", your friendly task manager!" +
                System.lineSeparator() +
                "What can I do for you?" +
                System.lineSeparator() +
                System.lineSeparator() +
                "Here are the commands you can use:" +
                System.lineSeparator() +
                "• todo <description> - Add a todo task" +
                System.lineSeparator() +
                "• deadline <description> /by <yyyy-MM-dd> - Add a task with deadline" +
                System.lineSeparator() +
                "• event <description> /from <yyyy-MM-ddTHH:mm> /to <yyyy-MM-ddTHH:mm> - Add an event" +
                System.lineSeparator() +
                "• list - Show all your tasks" +
                System.lineSeparator() +
                "• mark <number> - Mark a task as done" +
                System.lineSeparator() +
                "• unmark <number> - Mark a task as not done" +
                System.lineSeparator() +
                "• delete <number> - Delete a task" +
                System.lineSeparator() +
                "• find <keyword> - Search for tasks containing keyword" +
                System.lineSeparator() +
                "• bye - Exit the application" +
                System.lineSeparator() +
                HORIZONTAL_LINE;
        System.out.println(this.output);
    }

    /**
     * Displays the goodbye message when the application ends.
     */
    public void sayHi() {
        this.output = System.lineSeparator() +
                "What do you want from me again Momonga..." +
                System.lineSeparator();
    }

    /**
     * Displays the goodbye message when the application ends.
     */
    public void endConvo() {
        this.output = HORIZONTAL_LINE +
                System.lineSeparator() +
                "Bye. Hope to see you again soon!" +
                System.lineSeparator() +
                HORIZONTAL_LINE;
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
        this.output = HORIZONTAL_LINE +
                System.lineSeparator() +
                "Oops! " + message +
                System.lineSeparator() +
                HORIZONTAL_LINE;
        System.out.println(this.output);
    }

    /**
     * Displays the current list of tasks to the user.
     * Shows appropriate message if the list is empty.
     *
     * @param tasks The task list to display.
     */
    public void displayTaskList(TaskList tasks) {
        this.output = HORIZONTAL_LINE + System.lineSeparator();
        if (tasks.isEmpty()) {
            this.output = output + "Your list is empty! Add some tasks first." + System.lineSeparator();
        } else {
            this.output = output + "Here are the tasks in your list:" + System.lineSeparator();
            for (int i = 0; i < tasks.size(); i++) {
                this.output = output + (i + 1) + "." + tasks.get(i).toString() + System.lineSeparator();
            }
        }
        this.output = output + HORIZONTAL_LINE;
    }

    /**
     * Displays confirmation message when a task is successfully added.
     *
     * @param tasks The task list containing the new task.
     * @param task The task that was added.
     */
    public void displayTaskAdded(TaskList tasks, Task task) {
        this.output = HORIZONTAL_LINE +
                System.lineSeparator() +
                "Got it. I've added this task:" +
                System.lineSeparator() +
                "  " + task +
                System.lineSeparator() +
                "Now you have " + tasks.size() + " tasks in the list." +
                System.lineSeparator() +
                HORIZONTAL_LINE;
    }

    /**
     * Displays confirmation message when a task is successfully deleted.
     *
     * @param tasks The task list after the task removal.
     * @param task The task that was deleted.
     */
    public void displayTaskDeleted(TaskList tasks, Task task) {
        this.output = HORIZONTAL_LINE +
                System.lineSeparator() +
                "Noted. I've removed this task:" +
                System.lineSeparator() +
                "  " + task.toString() +
                System.lineSeparator() +
                "Now you have " + tasks.size() + " tasks in the list." +
                System.lineSeparator() +
                HORIZONTAL_LINE;
    }

    /**
     * Displays confirmation message when a task is marked as done.
     *
     * @param task The task that was marked as done.
     */
    public void displayMarked(Task task) {
        this.output = HORIZONTAL_LINE +
                System.lineSeparator() +
                "Nice! I've marked this task as done:" +
                System.lineSeparator() +
                "  " + task.toString() +
                System.lineSeparator() +
                HORIZONTAL_LINE;
    }

    /**
     * Displays confirmation message when a task is marked as not done.
     *
     * @param task The task that was marked as not done.
     */
    public void displayUnmarked(Task task) {
        this.output = HORIZONTAL_LINE +
                System.lineSeparator() +
                "OK, I've marked this task as not done yet:" +
                System.lineSeparator() +
                "  " + task.toString() +
                System.lineSeparator() +
                HORIZONTAL_LINE;
    }

    /**
     * Displays search results for tasks containing the specified keyword.
     *
     * @param matchingTasks TaskList containing tasks that match the search.
     * @param keyword The keyword that was searched for.
     */
    public void displaySearchResults(TaskList matchingTasks, String keyword) {
        this.output = HORIZONTAL_LINE + System.lineSeparator();
        if (matchingTasks.isEmpty()) {
            this.output = output + "No tasks found containing: " + keyword + System.lineSeparator();
        } else {
            this.output = output + "Here are the matching tasks in your list:" + System.lineSeparator();
            for (int i = 0; i < matchingTasks.size(); i++) {
                this.output = output + " " + (i + 1) + "." + matchingTasks.get(i).toString() + System.lineSeparator();
            }
        }
        this.output = output + HORIZONTAL_LINE;
    }

    /**
     * Returns the stored output.
     *
     * @return The stored output string.
     */
    public String returnOutput() {
        return output;
    }
}