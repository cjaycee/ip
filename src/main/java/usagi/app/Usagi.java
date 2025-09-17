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

    private static final String BYE_COMMAND = "bye";
    private static final String ERROR_PREFIX = "Error saving tasks: ";
    private static final String GENERIC_ERROR_PREFIX = "Oops! ";

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
        validateFilePath(filePath);

        ui = createUi();
        storage = createStorage(filePath);
        tasks = initializeTasks();
    }

    /**
     * Processes user input and returns the appropriate response.
     * Handles command interpretation, task saving, and error handling.
     *
     * @param input User input command string
     * @return Response message to display to user
     */
    public String getResponse(String input) {
        validateInputAndState(input);

        try {
            return processCommand(input);
        } catch (EmptyDescriptionException e) {
            return createErrorResponse("Empty description error: ", e);
        } catch (InvalidFormatException e) {
            return createErrorResponse("Format error: ", e);
        } catch (InvalidTaskNumberException e) {
            return createErrorResponse("Invalid task number: ", e);
        } catch (InvalidCommandException e) {
            return createErrorResponse("Invalid command: ", e);
        } catch (UsagiException e) {
            return createErrorResponse(GENERIC_ERROR_PREFIX, e);
        } catch (IOException e) {
            return createErrorResponse(ERROR_PREFIX, e);
        }
    }

    private void validateFilePath(String filePath) {
        assert filePath != null : "File path cannot be null";
        assert !filePath.isEmpty() : "File path cannot be empty";
        assert !filePath.trim().isEmpty() : "File path cannot be whitespace only";
    }

    private Ui createUi() {
        Ui newUi = new Ui();
        assert newUi != null : "Ui object must be created successfully";
        return newUi;
    }

    private Storage createStorage(String filePath) {
        Storage newStorage = new Storage(filePath);
        assert newStorage != null : "Storage object must be created successfully";
        return newStorage;
    }

    private TaskList initializeTasks() {
        try {
            TaskList loadedTasks = storage.load();
            assert loadedTasks != null : "Loaded TaskList cannot be null";
            return loadedTasks;
        } catch (IOException e) {
            ui.printErrorMessage(e.getMessage());
            TaskList fallbackTasks = new TaskList();
            assert fallbackTasks != null : "Fallback TaskList cannot be null";
            return fallbackTasks;
        }
    }

    private void validateInputAndState(String input) {
        assert input != null : "Input cannot be null";
        assert tasks != null : "TaskList must be initialized";
        assert ui != null : "Ui must be initialized";
        assert storage != null : "Storage must be initialized";
    }

    private String processCommand(String input) throws UsagiException, IOException {
        Parser.interpretCommand(input, ui, tasks);
        assert tasks != null : "TaskList should not become null after processing";

        saveTasksIfNeeded(input);

        String output = ui.returnOutput();
        assert output != null : "UI output cannot be null";

        return output;
    }

    private void saveTasksIfNeeded(String input) throws IOException {
        boolean isByeCommand = input.trim().equalsIgnoreCase(BYE_COMMAND);
        if (!isByeCommand) {
            storage.save(tasks);
        }
    }

    private String createErrorResponse(String prefix, Exception e) {
        assert e.getMessage() != null : "Exception message should not be null";
        return prefix + e.getMessage();
    }
}