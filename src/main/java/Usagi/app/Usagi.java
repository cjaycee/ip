package Usagi.app;

import Usagi.exception.UsagiException;
import Usagi.parser.Parser;
import Usagi.storage.Storage;
import Usagi.task.TaskList;
import Usagi.ui.Ui;

import java.io.IOException;

public class Usagi {

    private Storage storage;
    private TaskList tasks;
    private Ui ui;

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

    public static void main(String[] args) {
        new Usagi("data/tasks.txt").run();
    }

}
