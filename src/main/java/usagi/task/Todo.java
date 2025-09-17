package usagi.task;

/**
 * Represents a usagi.task.Todo task
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
    public String getFullDescription() {
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

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Todo todo = (Todo) obj;
        return description.equals(todo.description);
    }
}