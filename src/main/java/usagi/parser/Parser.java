package usagi.parser;

import usagi.exception.*;
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

    // Command constants
    private static final String CMD_HI = "hi";
    private static final String CMD_BYE = "bye";
    private static final String CMD_LIST = "list";
    private static final String CMD_MARK = "mark ";
    private static final String CMD_UNMARK = "unmark ";
    private static final String CMD_TODO = "todo ";
    private static final String CMD_DEADLINE = "deadline ";
    private static final String CMD_EVENT = "event ";
    private static final String CMD_DELETE = "delete ";
    private static final String CMD_FIND = "find ";

    // Magic number constants
    private static final int TODO_PREFIX_LENGTH = 5;
    private static final int FIND_PREFIX_LENGTH = 5;
    private static final int EVENT_PREFIX_LENGTH = 6;
    private static final int DEADLINE_PREFIX_LENGTH = 9;
    private static final int SPLIT_LIMIT = 2;
    private static final int TASK_INDEX_OFFSET = 1;
    private static final int MIN_PARTS_REQUIRED = 2;

    // Format constants
    private static final String DELIMITER_BY = "/by";
    private static final String DELIMITER_FROM = "/from";
    private static final String DELIMITER_TO = "/to";

    /**
     * Interprets and executes the given user command.
     * Supports commands: hi, bye, list, mark, unmark, todo, deadline, event, delete, find.
     *
     * @param input User input command string.
     * @param ui User interface for displaying messages.
     * @param tasks Task list to operate on.
     * @throws UsagiException If the command is invalid or malformed.
     */
    public static void interpretCommand(String input, Ui ui, TaskList tasks) throws UsagiException {
        assert input != null : "Input cannot be null";
        assert ui != null : "UI cannot be null";
        assert tasks != null : "TaskList cannot be null";

        if (input.trim().isEmpty()) {
            return; // Guard clause for empty input
        }

        if (input.equalsIgnoreCase(CMD_HI)) {
            ui.sayHi();
        } else if (input.equalsIgnoreCase(CMD_BYE)) {
            ui.endConvo();
        } else if (input.equalsIgnoreCase(CMD_LIST)) {
            ui.displayTaskList(tasks);
        } else if (input.startsWith(CMD_MARK)) {
            handleMarkCommand(ui, tasks, input, true);
        } else if (input.startsWith(CMD_UNMARK)) {
            handleMarkCommand(ui, tasks, input, false);
        } else if (isTaskCreationCommand(input)) {
            handleTaskCreation(input, ui, tasks);
        } else if (input.startsWith(CMD_DELETE)) {
            deleteTask(ui, tasks, input);
        } else if (input.startsWith(CMD_FIND)) {
            findTasks(ui, tasks, input);
        } else {
            throw new InvalidCommandException();
        }
    }

    /**
     * Checks if the input is a task creation command (todo, deadline, event).
     *
     * @param input User input command string to check
     * @return true if input is a task creation command, false otherwise
     */
    private static boolean isTaskCreationCommand(String input) {
        return input.equals(CMD_TODO.trim()) || input.startsWith(CMD_TODO)
                || input.equals(CMD_DEADLINE.trim()) || input.startsWith(CMD_DEADLINE)
                || input.equals(CMD_EVENT.trim()) || input.startsWith(CMD_EVENT);
    }

    /**
     * Routes task creation to the appropriate handler based on command type.
     *
     * @param input User input command string
     * @param ui User interface for displaying messages
     * @param tasks Task list to add the task to
     * @throws UsagiException If task creation fails
     */
    private static void handleTaskCreation(String input, Ui ui, TaskList tasks) throws UsagiException {
        if (input.equals(CMD_TODO.trim()) || input.startsWith(CMD_TODO)) {
            addTodoTask(ui, tasks, input);
        } else if (input.equals(CMD_DEADLINE.trim()) || input.startsWith(CMD_DEADLINE)) {
            addDeadlineTask(ui, tasks, input);
        } else if (input.equals(CMD_EVENT.trim()) || input.startsWith(CMD_EVENT)) {
            addEventTask(ui, tasks, input);
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
        String keyword = extractKeyword(input);
        TaskList matchingTasks = searchTasksForKeyword(tasks, keyword);
        ui.displaySearchResults(matchingTasks, keyword);
    }

    /**
     * Extracts the search keyword from the find command input.
     *
     * @param input Command string in format "find <keyword>"
     * @return The extracted keyword for searching
     * @throws UsagiException If the keyword is empty
     */
    private static String extractKeyword(String input) throws UsagiException {
        String keyword = input.substring(FIND_PREFIX_LENGTH).trim();
        if (keyword.isEmpty()) {
            throw new EmptyDescriptionException("find");
        }
        return keyword;
    }

    /**
     * Searches through all tasks to find those containing the specified keyword.
     *
     * @param tasks Task list to search through
     * @param keyword Keyword to search for (case-insensitive)
     * @return TaskList containing all matching tasks
     */
    private static TaskList searchTasksForKeyword(TaskList tasks, String keyword) {
        TaskList matchingTasks = new TaskList();
        String lowerKeyword = keyword.toLowerCase();

        for (int i = 0; i < tasks.size(); i++) {
            Task task = tasks.get(i);
            String taskDescription = task.getFullDescription().toLowerCase();

            if (taskDescription.contains(lowerKeyword)) {
                matchingTasks.add(task);
            }
        }

        return matchingTasks;
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
        String description = extractDescription(input, TODO_PREFIX_LENGTH, "todo");
        Task task = new Todo(description);

        if (checkDuplicate(task, tasks)) {
            throw new DuplicateException("You already have this task in your task list!");

        }

        addTaskToList(ui, tasks, task);
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
        String withoutPrefix = extractDescription(input, DEADLINE_PREFIX_LENGTH, "deadline");
        String[] parts = withoutPrefix.split(DELIMITER_BY, SPLIT_LIMIT);

        validateDeadlineFormat(parts);

        String description = parts[0].trim();
        String dueDate = parts[1].trim();

        validateDeadlineContent(description, dueDate);

        Task task = new Deadline(description, dueDate);

        if (checkDuplicate(task, tasks)) {
            throw new DuplicateException("You already have this task in your task list!");

        }

        addTaskToList(ui, tasks, task);
    }

    /**
     * Validates the format of deadline command parts.
     *
     * @param parts Array of command parts after splitting by "/by"
     * @throws InvalidFormatException If the format is invalid
     */
    private static void validateDeadlineFormat(String[] parts) throws InvalidFormatException {
        if (parts.length < MIN_PARTS_REQUIRED) {
            throw new InvalidFormatException("deadline <description> /by <time>");
        }
    }

    /**
     * Validates the content of deadline task components.
     *
     * @param description Task description string
     * @param dueDate Due date string
     * @throws UsagiException If description or due date is empty
     */
    private static void validateDeadlineContent(String description, String dueDate) throws UsagiException {
        if (description.isEmpty()) {
            throw new EmptyDescriptionException("deadline");
        }
        if (dueDate.isEmpty()) {
            throw new InvalidFormatException("deadline <description> /by <time> (time cannot be empty)");
        }
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
        String withoutPrefix = extractDescription(input, EVENT_PREFIX_LENGTH, "event");
        String[] parts = withoutPrefix.split(DELIMITER_FROM, SPLIT_LIMIT);

        validateEventFormatStep1(parts);

        String description = parts[0].trim();
        String[] timeParts = parts[1].split(DELIMITER_TO, SPLIT_LIMIT);

        validateEventFormatStep2(timeParts);

        String from = timeParts[0].trim();
        String to = timeParts[1].trim();

        validateEventContent(description, from, to);

        Task task = new Event(description, from, to);

        if (checkDuplicate(task, tasks)) {
            throw new DuplicateException("You already have this task in your task list!");

        }

        addTaskToList(ui, tasks, task);
    }

    /**
     * Validates the first step of event command format (checking /from delimiter).
     *
     * @param parts Array of command parts after splitting by "/from"
     * @throws InvalidFormatException If the format is invalid
     */
    private static void validateEventFormatStep1(String[] parts) throws InvalidFormatException {
        if (parts.length < MIN_PARTS_REQUIRED) {
            throw new InvalidFormatException("event <description> /from <start> /to <end>");
        }
    }

    /**
     * Validates the second step of event command format (checking /to delimiter).
     *
     * @param timeParts Array of time parts after splitting by "/to"
     * @throws InvalidFormatException If the format is invalid
     */
    private static void validateEventFormatStep2(String[] timeParts) throws InvalidFormatException {
        if (timeParts.length < MIN_PARTS_REQUIRED) {
            throw new InvalidFormatException("event <description> /from <start> /to <end>");
        }
    }

    /**
     * Validates the content of event task components.
     *
     * @param description Task description string
     * @param from Start time string
     * @param to End time string
     * @throws UsagiException If any component is empty
     */
    private static void validateEventContent(String description, String from, String to) throws UsagiException {
        if (description.isEmpty()) {
            throw new EmptyDescriptionException("event");
        }
        if (from.isEmpty() || to.isEmpty()) {
            throw new InvalidFormatException("event <description> /from <start> /to <end> (times cannot be empty)");
        }
    }

    /**
     * Extracts description from command input after removing the command prefix.
     * Handles cases where user types just the command without space or description.
     *
     * @param input Full command input string
     * @param prefixLength Length of the command prefix to remove
     * @param commandType Type of command for error messages
     * @return Extracted description string
     * @throws UsagiException If description is empty
     */
    private static String extractDescription(String input, int prefixLength, String commandType) throws UsagiException {
        String description;

        // Handle case where input is exactly the command (e.g., "todo", "deadline", "event")
        if (input.length() <= prefixLength) {
            description = "";
        } else {
            description = input.substring(prefixLength).trim();
        }

        if (description.isEmpty()) {
            throw new EmptyDescriptionException(commandType);
        }

        return description;
    }

    /**
     * Adds a task to the task list and displays confirmation to user.
     *
     * @param ui User interface for displaying messages
     * @param tasks Task list to add the task to
     * @param task Task object to add
     */
    private static void addTaskToList(Ui ui, TaskList tasks, Task task) {
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
            int taskNumber = parseTaskNumber(input, getCommandName(markAsDone));
            Task task = getTaskByNumber(tasks, taskNumber);
            updateTaskStatus(ui, task, markAsDone);
        } catch (NumberFormatException e) {
            String commandName = getCommandName(markAsDone);
            throw new InvalidFormatException(commandName + " <task-number> (must be a number)");
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
            int taskNumber = parseTaskNumber(input, "delete");
            Task task = tasks.remove(taskNumber);
            ui.displayTaskDeleted(tasks, task);
        } catch (NumberFormatException e) {
            throw new InvalidFormatException("delete <task-number> (must be a number)");
        }
    }

    /**
     * Parses and validates the task number from command input.
     *
     * @param input Full command input string
     * @param commandName Name of the command for error messages
     * @return Zero-based task index
     * @throws UsagiException If format is invalid or parsing fails
     */
    private static int parseTaskNumber(String input, String commandName) throws UsagiException {
        String[] parts = input.split(" ", SPLIT_LIMIT);

        if (parts.length < MIN_PARTS_REQUIRED) {
            throw new InvalidFormatException(commandName + " <task-number>");
        }

        int taskNumber = Integer.parseInt(parts[1]) - TASK_INDEX_OFFSET;
        return taskNumber;
    }

    /**
     * Retrieves a task from the task list by its number with bounds checking.
     *
     * @param tasks Task list to retrieve from
     * @param taskNumber Zero-based task index
     * @return Task object at the specified index
     * @throws InvalidTaskNumberException If task number is out of bounds
     */
    private static Task getTaskByNumber(TaskList tasks, int taskNumber) throws InvalidTaskNumberException {
        if (taskNumber < 0 || taskNumber >= tasks.size()) {
            throw new InvalidTaskNumberException(tasks.size());
        }
        return tasks.get(taskNumber);
    }

    /**
     * Updates the completion status of a task and displays appropriate message.
     *
     * @param ui User interface for displaying messages
     * @param task Task to update
     * @param markAsDone true to mark as done, false to mark as not done
     */
    private static void updateTaskStatus(Ui ui, Task task, boolean markAsDone) {
        if (markAsDone) {
            task.markAsDone();
            ui.displayMarked(task);
        } else {
            task.markAsNotDone();
            ui.displayUnmarked(task);
        }
    }

    /**
     * Gets the appropriate command name string based on marking operation.
     *
     * @param markAsDone true for "mark", false for "unmark"
     * @return Command name string
     */
    private static String getCommandName(boolean markAsDone) {
        return markAsDone ? "mark" : "unmark";
    }

    private static boolean checkDuplicate(Task t, TaskList tasks) {
        return tasks.contains(t);
    }

}