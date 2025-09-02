package usagi.exception;

public class InvalidCommandException extends UsagiException {
    public InvalidCommandException() {
        super("I'm sorry, but I don't know what that means. Please try again!");
    }
}