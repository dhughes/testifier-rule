package net.doughughes.testifier.watcher;

import com.github.javaparser.ParseException;
import com.google.gson.Gson;
import net.doughughes.testifier.entity.Notification;
import net.doughughes.testifier.entity.TestException;
import net.doughughes.testifier.util.SourceCodeExtractor;
import net.doughughes.testifier.annotation.Testifier;
import net.doughughes.testifier.util.GitService;
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
        this.notificationUrl = notificationUrl;
        this.exceptionHandling = exceptionHandling;
    }

    public NotifyingWatcher(String notificationUrl) {
        this(notificationUrl, Exceptions.LOG_SHORT);
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

        // class name
        String className = getClazz(description).getCanonicalName();

        // method details
        String methodName = getMethod(description);
        Class[] methodArguments = getArgs(description);

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
        String sourcePath = getSourcePath(description);
        String methodSource = "";

        try {
            methodSource = new SourceCodeExtractor(sourcePath).getMethodSource(methodName, methodArguments);
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }

        Notification notification = new Notification(
                studentName,
                studentEmail,
                projectName,
                className,
                methodName,
                methodArguments,
                unitTestName,
                testMethodName,
                result,
                methodSource,
                new TestException(exception)
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


        // old
        //String gitRepo = GitService.getOriginUrl();


        /*try {
            RestTemplate template = new RestTemplate();

            String notificationResult = template.postForObject(notificationUrl, notification, String.class);

            System.out.println("[Testifier Reporting Result]: " + notificationResult);

        } catch (ResourceAccessException e){
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

            } else if(exceptionHandling == Exceptions.THROW){
                // just throw the error
                throw e;

            }
            // we ignore Exceptions.IGNORE

        }*/
    }

    private Class getClazz(Description description) {
        if(description.getTestClass().getAnnotation(Testifier.class).clazz() != null){
            return description.getTestClass().getAnnotation(Testifier.class).clazz();
        } else {
            return description.getAnnotation(Testifier.class).clazz();
        }
    }

    private String getSourcePath(Description description) {
        if(!description.getTestClass().getAnnotation(Testifier.class).sourcePath().isEmpty()){
            return description.getTestClass().getAnnotation(Testifier.class).sourcePath();
        } else {
            return description.getAnnotation(Testifier.class).sourcePath();
        }
    }

    private String getMethod(Description description) {
        if(!description.getTestClass().getAnnotation(Testifier.class).method().isEmpty()){
            return description.getTestClass().getAnnotation(Testifier.class).method();
        } else {
            return description.getAnnotation(Testifier.class).method();
        }
    }

    private Class[] getArgs(Description description) {
        if(description.getTestClass().getAnnotation(Testifier.class).args().length != 0){
            return description.getTestClass().getAnnotation(Testifier.class).args();
        } else {
            return description.getAnnotation(Testifier.class).args();
        }
    }

}
