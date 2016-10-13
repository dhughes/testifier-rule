package net.doughughes.testifier.entity;

public class TestException {

    private String message;
    private String stackTrace;

    private TestException causedBy = null;

    public TestException() {
    }

    public TestException(Throwable exception) {
        if(exception != null) {
            this.message = exception.getMessage();
            StringBuilder stackTraceBuilder = new StringBuilder();

            for(StackTraceElement element : exception.getStackTrace()){
                stackTraceBuilder.append(element.toString() + "\r");
            }

            this.stackTrace = stackTraceBuilder.toString();

            if (exception.getCause() != null) {
                this.causedBy = new TestException(exception.getCause());
            }
        }
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStackTrace() {
        return stackTrace;
    }

    public void setStackTrace(String stackTrace) {
        this.stackTrace = stackTrace;
    }

    public TestException getCausedBy() {
        return causedBy;
    }

    public void setCausedBy(TestException causedBy) {
        this.causedBy = causedBy;
    }
}
