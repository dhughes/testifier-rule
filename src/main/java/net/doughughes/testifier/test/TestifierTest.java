package net.doughughes.testifier.test;

import net.doughughes.testifier.service.NotificationService;
import net.doughughes.testifier.watcher.CodeWatcher;
import net.doughughes.testifier.watcher.NotifyingWatcher;
import net.doughughes.testifier.watcher.OutputWatcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;

public class TestifierTest {

    @Rule
    public CodeWatcher codeWatcher = new CodeWatcher("./src");

    private NotificationService notificationService = new NotificationService("https://tiy-notifier.herokuapp.com/notifications/testResult", codeWatcher);

    @Rule
    public NotifyingWatcher notifyingWatcher = new NotifyingWatcher(notificationService);

    @Rule
    public OutputWatcher outputWatcher = new OutputWatcher();

}
