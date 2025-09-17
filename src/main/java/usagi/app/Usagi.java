package usagi.app;

import usagi.exception.*;
import usagi.parser.Parser;
import usagi.storage.Storage;
import usagi.task.TaskList;
import usagi.ui.Ui;

import java.io.IOException;

/**
 * Main application class for the Usagi chatbot
 */
public class Usagi {

    private final Storage storage;
    private TaskList tasks;
    private final Ui ui;

    /**
     * Creates a new Usagi application instance with the specified file path for data storage.
     * Initializes the UI, storage, and attempts to load existing tasks from the file.
     * If loading fails, starts with an empty task list.
     *
     * @param filePath Path to the file where tasks will be stored and loaded from.
     */
    public Usagi(String filePath) {
        assert !filePath.isEmpty() : "File path cannot be empty";
        assert !filePath.trim().isEmpty() : "File path cannot be empty or whitespace only";

        ui = new Ui();
        assert ui != null: "new Ui object must be created";

        storage = new Storage(filePath);
        assert storage != null : "new Storage object must be created";
        try {
            tasks = storage.load();
            assert tasks != null : "Loaded TaskList cannot be null";
        } catch (IOException e) {
            ui.printErrorMessage(e.getMessage());
            tasks = new TaskList();
            assert tasks != null : "Fallback TaskList cannot be null";
        }

    }

    public String getResponse(String input) {
        assert input != null : "Input cannot be null";
        assert tasks != null : "TaskList must be initialized before processing commands";
        assert ui != null : "Ui must be initialized before processing commands";
        assert storage != null : "Storage must be initialized before processing commands";

        try {
            Parser.interpretCommand(input, this.ui, this.tasks);
            assert tasks != null : "TaskList should not become null after command processing";

            if (!input.trim().equalsIgnoreCase("bye")) {
                storage.save(this.tasks);
            }

            String output = ui.returnOutput();
            assert output != null : "UI output cannot be null";

            return output;

        } catch (EmptyDescriptionException e) {
            return "Empty description error: " + e.getMessage();
        } catch (InvalidFormatException e) {
            return "Format error: " + e.getMessage();
        } catch (InvalidTaskNumberException e) {
            return "Invalid task number: " + e.getMessage();
        } catch (InvalidCommandException e) {
            return "Invalid command: " + e.getMessage();
        } catch (UsagiException e) {
            return "Oops! " + e.getMessage(); // Catch any other UsagiException
        } catch (IOException e) {
            return "Error saving tasks: " + e.getMessage();
        }
    }

}