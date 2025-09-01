package Usagi.task;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Represents a Usagi.task.Deadline task with a due date
 * Due date should be in the format: yyyy-MM-dd
 */
public class Deadline extends Task {
    private static final DateTimeFormatter IO_FMT = DateTimeFormatter.ISO_LOCAL_DATE;   // e.g., 2025-08-29
    private static final DateTimeFormatter VIEW_FMT = DateTimeFormatter.ofPattern("MMM d yyyy");    // e.g., Aug 29 2025

    private final LocalDate due;

    // dueDate must be ISO: yyyy-MM-dd
    public Deadline(String description, String dueDate) {
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
    public String getFullDescription() {
        return getTaskType() + super.toString() + " (by: " + due.format(VIEW_FMT) + ")";
    }

    @Override
    public String toString() {
        return getFullDescription();
    }

    @Override
    public String toFileString() {
        return "D | " + (isDone ? "1" : "0") + " | " + description + " | " + due.format(IO_FMT);
    }

}