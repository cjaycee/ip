import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.io.File;
import java.io.FileWriter;

public class Usagi {

    // Custom exception classes
    static class UsagiException extends Exception {
        public UsagiException(String message) {
            super(message);
        }
    }

    static class EmptyDescriptionException extends UsagiException {
        public EmptyDescriptionException(String taskType) {
            super("The description of a " + taskType + " cannot be empty.");
        }
    }

    static class InvalidCommandException extends UsagiException {
        public InvalidCommandException() {
            super("I'm sorry, but I don't know what that means. Please try again!");
        }
    }


    static class InvalidFormatException extends UsagiException {
        public InvalidFormatException(String correctFormat) {
            super("Please use the correct format: " + correctFormat);
        }
    }

    static class InvalidTaskNumberException extends UsagiException {
        public InvalidTaskNumberException(int maxTasks) {
            super("Please enter a valid task number between 1 and " + maxTasks);
        }
    }

    public static void main(String[] args) {
        TaskList tasks = new TaskList();
        Ui ui = new Ui();
        Storage storage = new Storage("data/Usagi.txt");

        ui.greet();

        try {
            tasks = storage.readFileContent();
        } catch (IOException e) {
            System.out.println("Something went wrong while reading the file: " + e.getMessage());
        }

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
            storage.writeFileContent(tasks);
        } catch (IOException e) {
            System.out.println("Something went wrong while reading the file: " + e.getMessage());
        }
    }

}
