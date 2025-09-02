package usagi.parser;

import usagi.exception.EmptyDescriptionException;
import usagi.exception.InvalidCommandException;
import usagi.exception.InvalidFormatException;
import usagi.exception.InvalidTaskNumberException;
import usagi.exception.UsagiException;
import usagi.task.Deadline;
import usagi.task.Event;
import usagi.task.Task;
import usagi.task.TaskList;
import usagi.task.Todo;
import usagi.ui.Ui;

/**
 * Parses user input commands and executes corresponding operations on tasks.
 * Handles various command types including task creation, marking, deletion, listing, and searching.
 */
public class Parser {

    /**
     * Interprets and executes the given user command.
     * Supports commands: bye, list, mark, unmark, todo, deadline, event, delete, find.
     *
     * @param input User input command string.
     * @param ui User interface for displaying messages.
     * @param tasks Task list to operate on.
     * @throws UsagiException If the command is invalid or malformed.
     */
    public static void interpretCommand(String input, Ui ui, TaskList tasks) throws UsagiException {
        if (input.equalsIgnoreCase("bye")) {
            ui.endConvo();
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
        } else if (input.startsWith("find ")) {
            findTasks(ui, tasks, input);
        } else if (!input.isEmpty()) {
            throw new InvalidCommandException();
        }
    }

    /**
     * Searches for tasks containing the specified keyword in their description.
     *
     * @param ui User interface for displaying messages.
     * @param tasks Task list to search through.
     * @param input Command string in format "find <keyword>".
     * @throws UsagiException If the keyword is empty.
     */
    private static void findTasks(Ui ui, TaskList tasks, String input) throws UsagiException {
        String keyword = input.substring(5).trim();
        if (keyword.isEmpty()) {
            throw new EmptyDescriptionException("find");
        }

        TaskList matchingTasks = new TaskList();

        // Search through all tasks for the keyword (case-insensitive)
        for (int i = 0; i < tasks.size(); i++) {
            Task task = tasks.get(i);
            if (task.getFullDescription().toLowerCase().contains(keyword.toLowerCase())) {
                matchingTasks.add(task);
            }
        }

        ui.displaySearchResults(matchingTasks, keyword);
    }

    /**
     * Creates and adds a Todo task from the given input command.
     *
     * @param ui User interface for displaying messages.
     * @param tasks Task list to add the task to.
     * @param input Command string in format "todo <description>".
     * @throws UsagiException If the description is empty.
     */
    private static void addTodoTask(Ui ui, TaskList tasks, String input) throws UsagiException {
        String description = input.substring(5).trim();
        if (description.isEmpty()) {
            throw new EmptyDescriptionException("todo");
        }

        Task task = new Todo(description);
        tasks.add(task);
        ui.displayTaskAdded(tasks, task);
    }

    /**
     * Creates and adds a Deadline task from the given input command.
     *
     * @param ui User interface for displaying messages.
     * @param tasks Task list to add the task to.
     * @param input Command string in format "deadline <description> /by <time>".
     * @throws UsagiException If the format is invalid or description/time is empty.
     */
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

        Task task = new Deadline(description, dueDate);
        tasks.add(task);
        ui.displayTaskAdded(tasks, task);
    }

    /**
     * Creates and adds an Event task from the given input command.
     *
     * @param ui User interface for displaying messages.
     * @param tasks Task list to add the task to.
     * @param input Command string in format "event <description> /from <start> /to <end>".
     * @throws UsagiException If the format is invalid or any field is empty.
     */
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

        Task task = new Event(description, from, to);
        tasks.add(task);
        ui.displayTaskAdded(tasks, task);
    }

    /**
     * Handles mark and unmark commands for tasks.
     *
     * @param ui User interface for displaying messages.
     * @param tasks Task list containing the task to mark/unmark.
     * @param input Command string in format "mark/unmark <task-number>".
     * @param markAsDone True to mark as done, false to mark as not done.
     * @throws UsagiException If the format is invalid or task number is out of range.
     */
    private static void handleMarkCommand(Ui ui, TaskList tasks, String input, boolean markAsDone)
            throws UsagiException {
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
                ui.displayMarked(task);
            } else {
                task.markAsNotDone();
                ui.displayUnmarked(task);
            }
        } catch (NumberFormatException e) {
            throw new InvalidFormatException((markAsDone ? "mark" : "unmark")
                    + " <task-number> (must be a number)");
        }
    }

    /**
     * Deletes a task from the task list based on the given command.
     *
     * @param ui User interface for displaying messages.
     * @param tasks Task list to remove the task from.
     * @param input Command string in format "delete <task-number>".
     * @throws UsagiException If the format is invalid or task number is out of range.
     */
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

            Task task = tasks.remove(taskNumber);
            ui.displayTaskDeleted(tasks, task);

        } catch (NumberFormatException e) {
            throw new InvalidFormatException("delete <task-number> (must be a number)");
        }
    }
}