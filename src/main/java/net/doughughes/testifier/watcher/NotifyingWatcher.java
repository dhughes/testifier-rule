package net.doughughes.testifier.watcher;

import net.doughughes.testifier.service.GitService;
import net.doughughes.testifier.service.NotificationService;
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

    private NotificationService notificationService;

    public NotifyingWatcher(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @Override
    protected void succeeded(Description description) {
        super.succeeded(description);
        notificationService.notifyUrl(null, description);
    }

    @Override
    protected void failed(Throwable e, Description description) {
        super.failed(e, description);
        notificationService.notifyUrl(e, description);
    }

    @Override
    protected void skipped(AssumptionViolatedException e, Description description) {
        super.skipped(e, description);
        notificationService.notifyUrl(e, description);
    }

}
