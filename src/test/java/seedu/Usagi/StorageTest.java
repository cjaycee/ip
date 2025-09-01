package seedu.Usagi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import Usagi.storage.Storage;
import Usagi.task.TaskList;
import Usagi.task.Todo;

/**
 * JUnit tests for the Storage class load and save methods.
 */
public class StorageTest {

    @TempDir
    Path tempDir;

    private Storage storage;
    private String testFilePath;

    @BeforeEach
    public void setUp() {
        testFilePath = tempDir.resolve("test_tasks.txt").toString();
        storage = new Storage(testFilePath);
    }

    @Test
    public void load_nonExistentFile_returnsEmptyList() throws IOException {
        TaskList loadedTasks = storage.load();

        assertEquals(0, loadedTasks.size());
    }

    @Test
    public void load_validTaskData_tasksLoadedCorrectly() throws IOException {
        createTestFile("T | 1 | read book\nT | 0 | exercise");

        TaskList loadedTasks = storage.load();

        assertEquals(2, loadedTasks.size());
        assertTrue(loadedTasks.get(0).getStatusIcon().equals("[X]"));
        assertTrue(loadedTasks.get(1).getStatusIcon().equals("[ ]"));
    }

    @Test
    public void saveAndLoad_taskList_dataPreserved() throws IOException {
        TaskList originalTasks = new TaskList();
        Todo task = new Todo("buy groceries");
        task.markAsDone();
        originalTasks.add(task);

        storage.save(originalTasks);
        TaskList loadedTasks = storage.load();

        assertEquals(1, loadedTasks.size());
        assertTrue(loadedTasks.get(0).getFullDescription().contains("buy groceries"));
        assertTrue(loadedTasks.get(0).getStatusIcon().equals("[X]"));
    }

    private void createTestFile(String content) throws IOException {
        FileWriter writer = new FileWriter(testFilePath);
        writer.write(content);
        writer.close();
    }
}