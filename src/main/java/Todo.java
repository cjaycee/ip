/**
 * Represents a Todo task
 */
public class Todo extends Task {

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