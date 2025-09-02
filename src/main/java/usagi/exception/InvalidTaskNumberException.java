package usagi.exception;

public class InvalidTaskNumberException extends UsagiException {
    public InvalidTaskNumberException(int maxTasks) {
        super("Please enter a valid task number between 1 and " + maxTasks);
    }
}