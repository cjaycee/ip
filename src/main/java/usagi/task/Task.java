package usagi.task;

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
    public abstract String toFileString();

    /**
     * Returns the task type label, e.g., [T], [D], [E].
     */
    abstract String getTaskType();

    /**
     * Returns a human-readable full description of the task.
     */
    public abstract String getFullDescription();

}
