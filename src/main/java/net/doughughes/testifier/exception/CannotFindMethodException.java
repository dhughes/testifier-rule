package net.doughughes.testifier.exception;

/**
 * Created by doug on 11/10/16.
 */
public class CannotFindMethodException extends Exception {

    public CannotFindMethodException(String message, Throwable cause) {
        super(message, cause);
    }

    public CannotFindMethodException(String message) {
        super(message);
    }
}
