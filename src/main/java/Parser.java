public class Parser {

    public static void interpretCommand(String input, Ui ui, TaskList tasks) throws Usagi.UsagiException {
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
        } else if (!input.isEmpty()) {
            throw new Usagi.InvalidCommandException();
        }
    }

    private static void addTodoTask(Ui ui, TaskList tasks, String input) throws Usagi.UsagiException {
        String description = input.substring(5).trim();
        if (description.isEmpty()) {
            throw new Usagi.EmptyDescriptionException("todo");
        }

        Task t = new Todo(description);
        tasks.add(t);
        ui.displayTaskAdded(tasks, t);
    }

    private static void addDeadlineTask(Ui ui, TaskList tasks, String input) throws Usagi.UsagiException {
        String withoutPrefix = input.substring(9).trim();
        if (withoutPrefix.isEmpty()) {
            throw new Usagi.EmptyDescriptionException("deadline");
        }

        String[] parts = withoutPrefix.split("/by", 2);

        if (parts.length < 2) {
            throw new Usagi.InvalidFormatException("deadline <description> /by <time>");
        }

        String description = parts[0].trim();
        String dueDate = parts[1].trim();

        if (description.isEmpty()) {
            throw new Usagi.EmptyDescriptionException("deadline");
        }
        if (dueDate.isEmpty()) {
            throw new Usagi.InvalidFormatException("deadline <description> /by <time> (time cannot be empty)");
        }

        Task t = new Deadline(description, dueDate);
        tasks.add(t);
        ui.displayTaskAdded(tasks, t);
    }

    private static void addEventTask(Ui ui, TaskList tasks, String input) throws Usagi.UsagiException {
        String withoutPrefix = input.substring(6).trim();
        if (withoutPrefix.isEmpty()) {
            throw new Usagi.EmptyDescriptionException("event");
        }

        String[] parts = withoutPrefix.split("/from", 2);

        if (parts.length < 2) {
            throw new Usagi.InvalidFormatException("event <description> /from <start> /to <end>");
        }

        String description = parts[0].trim();
        String[] timeParts = parts[1].split("/to", 2);

        if (timeParts.length < 2) {
            throw new Usagi.InvalidFormatException("event <description> /from <start> /to <end>");
        }

        String from = timeParts[0].trim();
        String to = timeParts[1].trim();

        if (description.isEmpty()) {
            throw new Usagi.EmptyDescriptionException("event");
        }
        if (from.isEmpty() || to.isEmpty()) {
            throw new Usagi.InvalidFormatException("event <description> /from <start> /to <end> (times cannot be empty)");
        }

        Task t = new Event(description, from, to);
        tasks.add(t);
        ui.displayTaskAdded(tasks, t);
    }

    private static void handleMarkCommand(Ui ui, TaskList tasks, String input, boolean markAsDone) throws Usagi.UsagiException {
        try {
            String[] parts = input.split(" ", 2);
            if (parts.length < 2) {
                throw new Usagi.InvalidFormatException((markAsDone ? "mark" : "unmark") + " <task-number>");
            }

            int taskNumber = Integer.parseInt(parts[1]) - 1;

            if (taskNumber < 0 || taskNumber >= tasks.size()) {
                throw new Usagi.InvalidTaskNumberException(tasks.size());
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
            throw new Usagi.InvalidFormatException((markAsDone ? "mark" : "unmark") + " <task-number> (must be a number)");
        }
    }

    private static void deleteTask(Ui ui, TaskList tasks, String input) throws Usagi.UsagiException {
        try {
            String[] parts = input.split(" ", 2);
            if (parts.length < 2) {
                throw new Usagi.InvalidFormatException("delete <task-number>");
            }

            int taskNumber = Integer.parseInt(parts[1]) - 1;

            if (taskNumber < 0 || taskNumber >= tasks.size()) {
                throw new Usagi.InvalidTaskNumberException(tasks.size());
            }

            Task t = tasks.remove(taskNumber);
            ui.displayTaskDeleted(tasks, t);

        } catch (NumberFormatException e) {
            throw new Usagi.InvalidFormatException("delete <task-number> (must be a number)");
        }
    }

}
