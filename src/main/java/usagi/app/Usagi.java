package usagi.app;

import usagi.exception.UsagiException;
import usagi.parser.Parser;
import usagi.storage.Storage;
import usagi.task.TaskList;
import usagi.ui.Ui;

import java.io.IOException;

/**
 * Main application class for the Usagi chatbot
 */
public class Usagi {

    private Storage storage;
    private TaskList tasks;
    private Ui ui;

    /**
     * Creates a new Usagi application instance with the specified file path for data storage.
     * Initializes the UI, storage, and attempts to load existing tasks from the file.
     * If loading fails, starts with an empty task list.
     *
     * @param filePath Path to the file where tasks will be stored and loaded from.
     */
    public Usagi(String filePath) {
        ui = new Ui();
        storage = new Storage(filePath);
        try {
            tasks = new TaskList(storage.load().displayTasks());
        } catch (IOException e) {
            ui.printErrorMessage(e.getMessage());
            tasks = new TaskList();
        }
    }

    /**
     * Starts the main application loop.
     * Displays greeting, processes user commands until termination,
     * and saves tasks to storage before exiting.
     */
    public void run() {
        ui.greet();

        while (ui.hasNextLine()) {
            try {
                String input = ui.readCommand();
                Parser.interpretCommand(input, ui, tasks);
            } catch (UsagiException e) {
                ui.printErrorMessage(e.getMessage());
            }
        }

        ui.closeScanner();

        try {
            storage.save(tasks);
        } catch (IOException e) {
            System.out.println("Something went wrong while reading the file: " + e.getMessage());
        }
    }

    /**
     * Entry point for the Usagi application.
     * Creates and runs a new Usagi instance with default data file path.
     *
     * @param args Command line arguments (not used).
     */
    public static void main(String[] args) {
        new Usagi("data/tasks.txt").run();
    }
}