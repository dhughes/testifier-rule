package net.doughughes.testifier.watcher;

import com.github.javaparser.ParseException;
import com.google.gson.Gson;
import net.doughughes.testifier.entity.Notification;
import net.doughughes.testifier.entity.TestException;
import net.doughughes.testifier.util.Instructor;
import net.doughughes.testifier.util.SourceCodeExtractor;
import net.doughughes.testifier.annotation.Testifier;
import net.doughughes.testifier.util.GitService;
import net.doughughes.testifier.util.TestifierAnnotationReader;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.fluent.Response;
import org.apache.http.entity.ContentType;
import org.junit.AssumptionViolatedException;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;

import java.io.File;
import java.io.IOException;

/**
 * This class is a test watcher that will notify an external server when tests
 * pass or fail.
 */
public class NotifyingWatcher extends TestWatcher {

    // constants
    public static enum Exceptions {
        LOG_SHORT,
        IGNORE
    }

    private String notificationUrl;
    private Exceptions exceptionHandling;

    public NotifyingWatcher(String notificationUrl, Exceptions exceptionHandling) {
        setNotificationUrl(notificationUrl);
        this.exceptionHandling = exceptionHandling;
    }

    public NotifyingWatcher(String notificationUrl) {
        this(notificationUrl, Exceptions.LOG_SHORT);
    }

    public void setNotificationUrl(String notificationUrl) {
        // if we have an environment variable named testifier-url, this overrides
        // any value passed in via the constructor.
        if(System.getenv("testifier-url") != null){
            this.notificationUrl = System.getenv("testifier-url");
            System.out.printf("[Testifier:] Ignoring configured notification url, using %s instead.\n", this.notificationUrl);
        } else {
            this.notificationUrl = notificationUrl;
        }
    }

    @Override
    protected void succeeded(Description description) {
        super.succeeded(description);
        notifyUrl(null, description);
    }

    @Override
    protected void failed(Throwable e, Description description) {
        super.failed(e, description);
        notifyUrl(e, description);
    }

    @Override
    protected void skipped(AssumptionViolatedException e, Description description) {
        super.skipped(e, description);
        notifyUrl(e, description);
    }

    private void notifyUrl(Throwable exception, Description description) {
        // student details
        String studentName = GitService.getGitUserName();
        String studentEmail = GitService.getGitEmail();

        // project (root dir) name
        String projectName  = "";
        try {
            projectName = new File(".").getCanonicalFile().getName();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // create an annotation reader
        TestifierAnnotationReader reader = new TestifierAnnotationReader(
                description.getTestClass().getAnnotation(Testifier.class),
                description.getAnnotation(Testifier.class)
        );

        // class name
        String className = reader.getClazz().getCanonicalName();

        // method details
        String methodName = reader.getMethod();

        // method or constructor argument details
        Class[] arguments = reader.getArgs();

        // test name and method
        String unitTestName = description.getTestClass().getName();
        String testMethodName = description.getMethodName();

        // was there an exception or failure?
        String result = "success";
        if(exception != null){
            if(exception.getClass().equals(AssertionError.class)){
                result = "failure";
            } else {
                result = "exception";
            }
        }

        // get the method being tested's source code
        String sourcePath = reader.getSourcePath();
        String methodSource = "";
        String constructorSource = "";
        String classSource = "";

        try {
            SourceCodeExtractor extractor = new SourceCodeExtractor(sourcePath);
            if(!methodName.isEmpty()) {
                methodSource = extractor.getMethodSource(methodName, arguments);
            }
            if(reader.getConstructor()) {
                constructorSource = extractor.getConstructorSource(arguments);
            }

            classSource = extractor.getClassSource();
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }

        // try to figure out who the instructor is for this class (as in classroom / students)
        String instructor = Instructor.identify();

        Notification notification = new Notification(
                studentName,
                studentEmail,
                projectName,
                className,
                methodName,
                arguments,
                unitTestName,
                testMethodName,
                result,
                methodSource,
                constructorSource,
                classSource,
                new TestException(exception),
                instructor
        );

        // get the json version of this notification
        String json = new Gson().toJson(notification);

        try {
            Response response = Request.Post(notificationUrl)
                    .bodyString(json, ContentType.APPLICATION_JSON)
                    .execute();

        } catch (IOException e) {
            if(exceptionHandling == Exceptions.LOG_SHORT){
                // log a short version of the error to the console
                System.out.println("[Testifier Reporting TestException]: " + e.getMessage());
                Throwable causedBy = e.getCause();
                String indent = "\t[Caused By]: ";
                while(causedBy != null){
                    System.out.println(indent + causedBy.getMessage());

                    // setup for the next caused by
                    causedBy = causedBy.getCause();
                    indent += "\t";
                }

            }
        }


    }



}
