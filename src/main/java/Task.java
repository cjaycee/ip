import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Represents a generic task with a description and completion status.
 */
public abstract class Task {

    protected String description;
    protected boolean isDone;

    public Task(String description) {
        this.description = description;
        this.isDone = false;
    }

    public Task(String description, boolean isDone) {
        this.description = description;
        this.isDone = isDone;
    }

    /**
     * Returns symbol to show if task is done.
     */
    public String getStatusIcon() {
        return isDone ? "[X]" : "[ ]";
    }

    /**
     * Marks task as done.
     */
    public void markAsDone() {
        this.isDone = true;
    }

    /**
     * Marks task as undone.
     */
    public void markAsNotDone() {
        this.isDone = false;
    }

    @Override
    public String toString() {
        return getStatusIcon() + " " + description;
    }

    /**
     * Returns a string suitable for saving to file.
     */
    abstract String toFileString();

    /**
     * Returns the task type label, e.g., [T], [D], [E].
     */
    abstract String getTaskType();

    /**
     * Returns a human-readable full description of the task.
     */
    abstract String getFullDescription();

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
        private static final DateTimeFormatter IO_FMT = DateTimeFormatter.ISO_LOCAL_DATE;   // e.g., 2025-08-29
        private static final DateTimeFormatter VIEW_FMT = DateTimeFormatter.ofPattern("MMM d yyyy");    // e.g., Aug 29 2025

        private final LocalDate due;

        // dueDate must be ISO: yyyy-MM-dd
        Deadline(String description, String dueDate) {
            super(description);
            this.due = parseDue(dueDate);
        }

        Deadline(String description, boolean isDone, String dueDate) {
            super(description, isDone);
            this.due = parseDue(dueDate);
        }

        /**
         * Parses a string into a {@link LocalDate} using the predefined input format.
         * The expected format is {@code yyyy-MM-dd}. If the input does not match this format,
         * an {@code IllegalArgumentException} will be thrown.
         *
         * @param s String representing the date to parse.
         * @return LocalDate object corresponding to the given string.
         * @throws IllegalArgumentException If the string cannot be parsed into a date
         *                                  in the expected format.
         */
        private static LocalDate parseDue(String s) {
            try {
                return LocalDate.parse(s, IO_FMT);
            } catch (DateTimeParseException e) {
                throw new IllegalArgumentException("Invalid deadline date '" + s + "'. Expected yyyy-MM-dd.", e);
            }
        }

        @Override
        String getTaskType() {
            return "[D]";
        }

        @Override
        String getFullDescription() {
            return getTaskType() + super.toString() + " (by: " + due.format(VIEW_FMT) + ")";
        }

        @Override
        public String toString() {
            return getFullDescription();
        }

        @Override String toFileString() {
            return "D | " + (isDone ? "1" : "0") + " | " + description + " | " + due.format(IO_FMT);
        }
    }

    // Event class
    static class Event extends Usagi.Task {
        private static final DateTimeFormatter IO_FMT = DateTimeFormatter.ISO_LOCAL_DATE_TIME;       // e.g., 2015-02-20T06:30
        private static final DateTimeFormatter VIEW_FMT = DateTimeFormatter.ofPattern("MMM d yyyy HH:mm");

        private final LocalDateTime start;
        private final LocalDateTime end;

        // start/end must be ISO: yyyy-MM-dd'T'HH:mm[:ss]
        Event(String description, String start, String end) {
            super(description);
            this.start = parseDateTime(start);
            this.end = parseDateTime(end);
            validateOrder();
        }

        Event(String description, boolean isDone, String start, String end) {
            super(description, isDone);
            this.start = parseDateTime(start);
            this.end = parseDateTime(end);
            validateOrder();
        }
        /**
         * Parses a string into a {@link LocalDateTime} using the predefined input format.
         * The expected format is {@code yyyy-MM-dd'T'HH:mm[:ss]}. If the input does not
         * match this format, an {@code IllegalArgumentException} will be thrown.
         *
         * @param s String representing the date to parse.
         * @return LocalDateTime object corresponding to the given string.
         * @throws IllegalArgumentException If the string cannot be parsed into a datetime
         *                                  in the expected format.
         */
        private static LocalDateTime parseDateTime(String s) {
            try {
                return LocalDateTime.parse(s, IO_FMT); // accepts both with & without seconds
            } catch (DateTimeParseException ex) {
                throw new IllegalArgumentException(
                        "Invalid event datetime '" + s + "'. Expected ISO, e.g., 2015-02-20T06:30.", ex);
            }
        }

        /**
         * Validates that end time > start time
         *
         * @throws IllegalArgumentException If the event end time is before start time.
         */
        private void validateOrder() {
            if (end.isBefore(start)) {
                throw new IllegalArgumentException("Event end time cannot be before start time.");
            }
        }

        @Override
        String getTaskType() {
            return "[E]";
        }

        @Override String getFullDescription() {
            return getTaskType() + super.toString()
                    + " (from: " + start.format(VIEW_FMT) + " to: " + end.format(VIEW_FMT) + ")";
        }

        @Override public String toString() {
            return getFullDescription();
        }

        @Override String toFileString() {
            return "E | " + (isDone ? "1" : "0") + " | " + description + " | "
                    + start.format(IO_FMT) + " | " + end.format(IO_FMT);
        }
    }
}
