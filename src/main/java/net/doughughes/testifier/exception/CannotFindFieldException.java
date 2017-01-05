package net.doughughes.testifier.exception;

/**
 * Created by doug on 11/10/16.
 */
public class CannotFindFieldException extends Exception {

    public CannotFindFieldException(String message, Throwable cause) {
        super(message, cause);
    }

    public CannotFindFieldException(String message) {
        super(message);
    }
}
