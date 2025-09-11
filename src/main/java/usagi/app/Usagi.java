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
            tasks = storage.load();;
        } catch (IOException e) {
            ui.printErrorMessage(e.getMessage());
            tasks = new TaskList();
        }
    }

    public String getResponse(String input) {
        try {
            Parser.interpretCommand(input, this.ui, this.tasks);

            if (!input.trim().equalsIgnoreCase("bye")) {
                storage.save(this.tasks);
            }

            return ui.returnOutput();

        } catch (UsagiException e) {
            return "Oops! " + e.getMessage();
        } catch (IOException e) {
            return "Error saving tasks: " + e.getMessage();
        }
    }

}