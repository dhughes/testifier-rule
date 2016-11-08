package net.doughughes.testifier.watcher;

import com.github.javaparser.ParseException;
import net.doughughes.testifier.service.SourceCodeService;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;

import java.io.FileNotFoundException;

public class CodeWatcher extends TestWatcher {

    private String sourceRoot;
    private SourceCodeService mainSourceCodeService;

    public CodeWatcher(String sourceRoot) {
        this.sourceRoot = sourceRoot;
    }

    @Override
    protected void starting(Description description) {
        super.starting(description);

        try {
            // create the source code service that we can use to extract source code and analyze it
            mainSourceCodeService = new SourceCodeService(getClassName(description), getMainSourcePath(description));
        } catch (FileNotFoundException | ParseException e) {
            e.printStackTrace();
        }
    }

    /**
     * Given a Description, returns a string which is the path to the class-under-test's .java file.
     * @param description
     * @return
     */
    private String getMainSourcePath(Description description){

        // full name of the class being tested
        String className = getClassName(description);

        // path to the class
        return sourceRoot + "/main/java/" + className.replaceAll("\\.", "/") + ".java";
    }

    /**
     * Given a Description, returns a string which is the path to the test class' .java file.
     * @param description
     * @return
     */
    private String getTestSourcePath(Description description){

        // full name of the class being tested
        String className = getClassName(description);

        // path to the class
        return sourceRoot + "/test/java/" + description.getTestClass().getCanonicalName().replaceAll("\\.", "/") + ".java";
    }

    private String getClassName(Description description) {
        // full name of the test class
        String canonicalTestName = description.getTestClass().getCanonicalName();

        return canonicalTestName.substring(0, canonicalTestName.length() - 4);
    }

    public SourceCodeService getMainSourceCodeService() {
        return mainSourceCodeService;
    }
}
