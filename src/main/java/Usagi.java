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

        ui.greet();

        try {
            tasks = readFileContent("data/Usagi.txt");
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
            writeFileContent(tasks, "data/Usagi.txt");
        } catch (IOException e) {
            System.out.println("Something went wrong while reading the file: " + e.getMessage());
        }
    }

    private static TaskList readFileContent(String filePath) throws IOException {
        TaskList tasks = new TaskList();

        File f = new File(filePath);
        File folder = f.getParentFile();

        if (!folder.exists()) {
            folder.mkdirs();
        }

        if (!f.exists()) {
            f.createNewFile();
            return tasks; // empty list on first run
        }

        Scanner s = new Scanner(f);
        while (s.hasNextLine()) {
            String line = s.nextLine().trim();
            if (line.isEmpty()) continue;

            String[] parts = line.split("\\|");
            for (int i = 0; i < parts.length; i++) {
                parts[i] = parts[i].trim();
            }

            String type = parts[0]; // T, D, or E
            boolean isDone = parts[1].equals("1");
            String desc = parts[2];

            Task t = null;

            switch (type) {
            case "T":
                t = new Todo(desc);
                break;
            case "D":
                t = new Deadline(desc, parts[3]);
                break;
            case "E":
                t = new Event(desc, parts[3], parts[4]);
                break;
            }

            if (isDone && t != null) {
                t.markAsDone();
            }
            if (t != null) {
                tasks.add(t);
            }
        }
        s.close();
        return tasks;
    }

    private static void writeFileContent(TaskList tasks, String filePath) throws IOException {
        FileWriter fw = new FileWriter(filePath);
        for (int i = 0; i < tasks.size(); i++) {
            fw.write(tasks.get(i).toFileString() + System.lineSeparator());
        }
        fw.close();
    }

}
