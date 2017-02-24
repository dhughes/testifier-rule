package net.doughughes.testifier.exception;

/**
 * Created by doug on 2/24/17.
 */
public class MethodIsNotStaticException extends Exception {
    public MethodIsNotStaticException(String message, Throwable cause) {
        super(message, cause);
    }

    public MethodIsNotStaticException(String message) {
        super(message);
    }
}
