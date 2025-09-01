package Usagi.exception;

public class InvalidFormatException extends UsagiException {
    public InvalidFormatException(String correctFormat) {
        super("Please use the correct format: " + correctFormat);
    }
}