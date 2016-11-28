package net.doughughes.testifier.exception;

import java.io.IOException;

/**
 * Represents an exception where a .tiy-config file can't be found in the user's home directory.
 */
public class TiyConfigNotFoundException extends Throwable {

    public TiyConfigNotFoundException(IOException e) {
        super(e);
    }
}
