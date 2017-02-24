package net.doughughes.testifier.exception;

public class CannotInvokeMethodException extends Throwable {

    public CannotInvokeMethodException(Throwable e) {
        super(e.toString(), e.getCause());
    }

}
