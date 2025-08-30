import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.io.File;
import java.io.FileWriter;

public class Usagi {

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
