package seedu.Usagi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import usagi.exception.InvalidCommandException;
import usagi.exception.InvalidTaskNumberException;
import usagi.exception.UsagiException;
import usagi.parser.Parser;
import usagi.task.TaskList;
import usagi.task.Todo;
import usagi.ui.Ui;

/**
 * JUnit tests for the Parser class interpretCommand method.
 */
public class ParserTest {

    private Ui ui;
    private TaskList tasks;

    @BeforeEach
    public void setUp() {
        ui = new Ui();
        tasks = new TaskList();
    }

    @Test
    public void interpretCommand_todoCommand_taskAdded() throws UsagiException {
        Parser.interpretCommand("todo read book", ui, tasks);

        assertEquals(1, tasks.size());
        assertTrue(tasks.get(0).getFullDescription().contains("read book"));
    }

    @Test
    public void interpretCommand_markCommand_taskMarked() throws UsagiException {
        tasks.add(new Todo("test task"));

        Parser.interpretCommand("mark 1", ui, tasks);

        assertTrue(tasks.get(0).getStatusIcon().equals("[X]"));
    }

    @Test
    public void interpretCommand_invalidCommand_throwsException() {
        assertThrows(InvalidCommandException.class, () -> {
            Parser.interpretCommand("invalid", ui, tasks);
        });
    }

    @Test
    public void interpretCommand_markOutOfRange_throwsException() {
        tasks.add(new Todo("test task"));

        assertThrows(InvalidTaskNumberException.class, () -> {
            Parser.interpretCommand("mark 5", ui, tasks);
        });
    }
}