package net.doughughes.testifier.exception;

/**
 * Created by doug on 11/10/16.
 */
public class CannotFindConstructorException extends Exception {

    public CannotFindConstructorException(String message, Throwable cause) {
        super(message, cause);
    }

    public CannotFindConstructorException(String message) {
        super(message);
    }
}
