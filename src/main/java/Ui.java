import java.util.Scanner;

public class Ui {

    private final String NAME = "Usagi";
    private final String HORIZONTAL_LINE = "____________________________________________________________";
    private Scanner scanner;
    boolean isRunning;

    public Ui() {
        this.scanner = new Scanner(System.in);
        this.isRunning = true;
    }

    public void printLine() {
        System.out.println(HORIZONTAL_LINE);
    }

    public boolean hasNextLine() {
        return this.scanner.hasNextLine();
    }

    public String readCommand() {
        return scanner.nextLine();
    }

    public void greet() {
        this.printLine();
        System.out.println("Hello! I'm " + NAME + ", your friendly task manager!");
        System.out.println("What can I do for you?");
        this.printLine();
    }

    public void endConvo() {
        this.scanner.close();
        this.printLine();
        System.out.println("Bye. Hope to see you again soon!");
        this.printLine();
    }
    public void printErrorMessage(String message) {
        this.printLine();
        System.out.println("Oops! " + message);
        this.printLine();
    }

    public void displayTaskList(TaskList tasks) {
        this.printLine();
        if (tasks.isEmpty()) {
            System.out.println("Your list is empty! Add some tasks first.");
        } else {
            System.out.println("Here are the tasks in your list:");
            for (int i = 0; i < tasks.size(); i++) {
                System.out.println((i + 1) + "." + tasks.get(i).toString());
            }
        }
        this.printLine();
    }

    public void displayTaskAdded(TaskList tasks, Task t) {
        this.printLine();
        System.out.println("Got it. I've added this task:");
        System.out.println("  " + t);
        System.out.println("Now you have " + tasks.size() + " tasks in the list.");
        this.printLine();
    }

    public void displayTaskDeleted(TaskList tasks, Task t) {
        this.printLine();
        System.out.println("Noted. I've removed this task:");
        System.out.println("  " + t.toString());
        System.out.println("Now you have " + tasks.size() + " tasks in the list.");
        this.printLine();
    }

    public void displayMarked(Task t) {
        this.printLine();
        System.out.println("Nice! I've marked this task as done:");
        System.out.println("  " + t.toString());
        this.printLine();
    }

    public void displayUnmarked(Task t) {
        this.printLine();
        System.out.println("OK, I've marked this task as not done yet:");
        System.out.println("  " + t.toString());
        this.printLine();
    }

}
