package net.doughughes.testifier.service;

import com.google.gson.Gson;
import net.doughughes.testifier.entity.Notification;
import net.doughughes.testifier.entity.TestException;
import net.doughughes.testifier.exception.TiyConfigNotFoundException;
import net.doughughes.testifier.watcher.CodeWatcher;
import org.apache.http.client.fluent.Request;
import org.apache.http.entity.ContentType;
import org.junit.runner.Description;

import java.io.File;
import java.io.IOException;

/**
 * This holds data sent to the notifier webapp when tests are run.
 */
public class NotificationService {

    private CodeWatcher codeWatcher;
    private String notificationUrl;

    public NotificationService(String notificationUrl, CodeWatcher codeWatcher) {
        setNotificationUrl(notificationUrl);
        this.codeWatcher = codeWatcher;
    }

    public void setNotificationUrl(String notificationUrl) {
        // if we have an environment variable named notifier-url we must ALWAYS use that
        if(System.getenv("NOTIFIER_URL") != null){
            this.notificationUrl = System.getenv("NOTIFIER_URL");
            System.out.printf("[Testifier:] Ignoring configured notification url, using %s instead.\n", this.notificationUrl);
        } else {
            this.notificationUrl = notificationUrl;
        }
    }

    public void notifyUrl(Throwable exception, Description description) {
        try{
            // student details
            Long studentId = ConfigService.getStudentId();

            // project (root dir) name
            String projectName = getProjectName();

            // get the name of the class under test
            String className = codeWatcher.getMainSourceCodeService().getClassName();

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
            //String sourcePath = codeWatcher.getMainSourceCodeService().getSourcePath();
            String classSource = codeWatcher.getMainSourceCodeService().getSource();

            // try to figure out who the instructor is for this class (as in classroom / students)
            String instructor = ConfigService.getInstructor();

            Notification notification = new Notification(
                    studentId,
                    projectName,
                    className,
                    unitTestName,
                    testMethodName,
                    result,
                    classSource,
                    new TestException(exception),
                    instructor
            );

            // get the json version of this notification
            String json = new Gson().toJson(notification);

            Request.Post(notificationUrl)
                    .connectTimeout(500)
                    .socketTimeout(500)
                    .bodyString(json, ContentType.APPLICATION_JSON)
                    .execute();

        } catch (TiyConfigNotFoundException | IOException e) {
            // log a short version of the error to the console
            System.out.println("[Testifier Reporting " + e.getClass().getSimpleName() + "]: " + e.getMessage());
            Throwable causedBy = e.getCause();
            String indent = "\t[Caused By]: ";
            while (causedBy != null) {
                System.out.println(indent + causedBy.getMessage());

                // setup for the next caused by
                causedBy = causedBy.getCause();
                indent += "\t";
            }
        }
    }

    private String getProjectName() {
        try {
            return new File(".").getCanonicalFile().getName();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "";
    }
}
