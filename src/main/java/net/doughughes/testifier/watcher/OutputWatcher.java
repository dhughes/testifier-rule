package net.doughughes.testifier.watcher;

import net.doughughes.testifier.output.OutputStreamInterceptor;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;

import java.util.ArrayList;
import java.util.List;

public class OutputWatcher extends TestWatcher {

    // redirect output so we can capture it for our tests
    private OutputStreamInterceptor outputStreamInterceptor;

    @Override
    protected void starting(Description description) {
        super.starting(description);
        this.outputStreamInterceptor = new OutputStreamInterceptor(System.out);
        System.setOut(this.outputStreamInterceptor);
    }

    @Override
    protected void finished(Description description) {
        super.finished(description);
        System.setOut(this.outputStreamInterceptor.getOut());
    }

    public ArrayList getPrinted() {
        return ((OutputStreamInterceptor) System.out).getPrinted();
    }

    public List<String> getLines() {
        return ((OutputStreamInterceptor) System.out).getLines();
    }
}
