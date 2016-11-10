package net.doughughes.testifier.exception;

/**
 * Created by doug on 11/10/16.
 */
public class CannotAccessMethodException extends Exception {
    public CannotAccessMethodException(String message, Throwable cause) {
        super(message, cause);
    }

    public CannotAccessMethodException(String message) {
        super(message);
    }
}
