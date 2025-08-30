
import java.util.ArrayList;

/**
 * Represents a list of tasks.
 * Provides operations to add, delete, retrieve, and return all tasks.
 */
public class TaskList {
    private final ArrayList<Task> tasks;

    /**
     * Creates an empty task list.
     */
    public TaskList() {
        this.tasks = new ArrayList<>();
    }

    /**
     * Creates a task list initialized with existing tasks.
     *
     * @param tasks Tasks to initialize with. Can be null.
     */
    public TaskList(ArrayList<Task> tasks) {
        this.tasks = tasks != null ? tasks : new ArrayList<>();
    }


    /**
     * Returns boolean of whether task list is empty
     *
     * @return Whether task list is empty
     */
    public boolean isEmpty() {
        return this.size() == 0;
    }

    /**
     * Returns the number of tasks in the list.
     *
     * @return Number of tasks.
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Returns the task at the specified index.
     *
     * @param idx Index of the task to return.
     * @return Task at the given index.
     */
    public Task get(int idx) {
        return tasks.get(idx);
    }

    /**
     * Adds a task to the list.
     *
     * @param t Task to be added.
     */
    public void add(Task t) {
        tasks.add(t);
    }

    /**
     * Removes the task at the specified index.
     *
     * @param idx Index of the task to remove.
     * @return Removed task.
     */
    public Task remove(int idx) {
        return tasks.remove(idx);
    }

    /**
     * Returns the underlying list of tasks.
     *
     * @return List of tasks.
     */
    public ArrayList<Task> displayTasks() {
        return tasks;
    }

}
