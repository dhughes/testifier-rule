package net.doughughes.testifier.exception;

/**
 * Created by doug on 11/10/16.
 */
public class CannotInstantiateClassException extends Exception {

    public CannotInstantiateClassException(String message, Throwable cause) {
        super(message, cause);
    }

    public CannotInstantiateClassException(String message) {
        super(message);
    }
}
