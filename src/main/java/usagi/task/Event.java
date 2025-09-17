package usagi.task;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Represents an usagi.task.Event task with a start and end time
 * Start/end must be in the format: yyyy-MM-dd'T'HH:mm[:ss]
 */
public class Event extends Task {
    private static final DateTimeFormatter IO_FMT = DateTimeFormatter.ISO_LOCAL_DATE_TIME;       // e.g., 2015-02-20T06:30
    private static final DateTimeFormatter VIEW_FMT = DateTimeFormatter.ofPattern("MMM d yyyy HH:mm");

    private final LocalDateTime start;
    private final LocalDateTime end;

    // start/end must be ISO: yyyy-MM-dd'T'HH:mm[:ss]
    public Event(String description, String start, String end) {
        super(description);
        this.start = parseDateTime(start);
        this.end = parseDateTime(end);
        validateOrder();
    }

    public Event(String description, boolean isDone, String start, String end) {
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
            throw new IllegalArgumentException("Usagi.task.Event end time cannot be before start time.");
        }
    }

    @Override
    String getTaskType() {
        return "[E]";
    }

    @Override
    public String getFullDescription() {
        return getTaskType() + super.toString()
                + " (from: " + start.format(VIEW_FMT) + " to: " + end.format(VIEW_FMT) + ")";
    }

    @Override public String toString() {
        return getFullDescription();
    }

    @Override
    public String toFileString() {
        return "E | " + (isDone ? "1" : "0") + " | " + description + " | "
                + start.format(IO_FMT) + " | " + end.format(IO_FMT);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Event event = (Event) obj;
        return description.equals(event.description) &&
                start.equals(event.start) &&
                end.equals(event.end);
    }
}