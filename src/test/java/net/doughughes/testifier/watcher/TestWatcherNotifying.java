package net.doughughes.testifier.watcher;


import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.IOException;
import java.lang.annotation.Annotation;

@RunWith(PowerMockRunner.class)
//@PrepareForTest({Request.class, Response.class})
public class TestWatcherNotifying {

    private static String url = "https://testifier.herokuapp.com";

    @Before
    public void before() throws IOException {}

    /**
     * Given a url to notify
     * When a successful test notification is received
     * Then the url specified is notified via an http request
     */
    @Test
    public void whenSuccessfulThenNotificationSent() throws IOException {
        /* arrange */
        /*Request request = mock(Request.class);
        Response response = mock(Response.class);
        mockStatic(Request.class);
        when(Request.Post(Mockito.anyString())).thenReturn(request);
        when(request.bodyString(Mockito.anyString(), Mockito.any(ContentType.class))).thenReturn(request);
        when(request.execute()).thenReturn(response);

        // setup the watcher
        NotifyingWatcher watcher = new NotifyingWatcher(url);

        *//* act *//*
        watcher.succeeded(Description.createTestDescription(this.getClass(), "whenSuccessfulThenNotificationSent", new Annotation[0]));

        *//* assert *//*
        // make sure the request was executed
        verify(request, times(1)).execute();*/
    }

    /**
     * Given a url to notify that isn't real
     * When a successful test notification is received
     * Then a message is logged to the console.
     */
    @Test
    @Ignore
    public void whenSuccessfulWithBadUrlThenSilentError(){
        /* arrange */
        // setup the watcher
        NotifyingWatcher watcher = new NotifyingWatcher("http://localhost/bla");

        /* act */
        watcher.succeeded(Description.createTestDescription(this.getClass(), "whenSuccessfulWithBadUrlThenSilentError", new Annotation[0]));

        /* assert */
        // the assertion here is that no exception is thrown
        // todo: consider redirecting console output and validating what we see there
    }



}
