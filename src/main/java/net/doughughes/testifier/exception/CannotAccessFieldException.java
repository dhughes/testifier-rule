package net.doughughes.testifier.exception;

/**
 * Created by doug on 11/10/16.
 */
public class CannotAccessFieldException extends Exception {
    public CannotAccessFieldException(String message, Throwable cause) {
        super(message, cause);
    }

    public CannotAccessFieldException(String message) {
        super(message);
    }
}
