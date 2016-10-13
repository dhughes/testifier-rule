package net.doughughes.testifier.watcher;


import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class MetaTest {

    @Rule
    public NotifyingWatcher watcher = new NotifyingWatcher("https://testifier.herokuapp.com");

    @Test
    @Ignore
    public void successTest(){
        assertThat(1, is(1));
    }


}
