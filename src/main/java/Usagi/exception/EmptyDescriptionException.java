package Usagi.exception;

public class EmptyDescriptionException extends UsagiException {
    public EmptyDescriptionException(String taskType) {
        super("The description of a " + taskType + " cannot be empty.");
    }
}