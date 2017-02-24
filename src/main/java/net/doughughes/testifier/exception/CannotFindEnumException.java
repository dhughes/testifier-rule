package net.doughughes.testifier.exception;

/**
 * Created by doug on 11/10/16.
 */
public class CannotFindEnumException extends Exception {

    public CannotFindEnumException(String message, Throwable cause) {
        super(message, cause);
    }

    public CannotFindEnumException(String message) {
        super(message);
    }
}
