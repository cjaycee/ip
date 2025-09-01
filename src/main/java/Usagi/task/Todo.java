package Usagi.task;

/**
 * Represents a Usagi.task.Todo task
 */
public class Todo extends Task {

    public Todo(String description) {
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
    public String toFileString() {
        return "T | " + (isDone ? "1" : "0") + " | " + description;
    }

}