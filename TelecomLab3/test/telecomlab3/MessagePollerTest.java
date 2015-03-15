/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package telecomlab3;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author Michael
 */
public class MessagePollerTest {

    private CommHandler comm;
    private User user;
    private MessagePoller poll;

    public MessagePollerTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
        comm = new CommHandler();
        user = new User("bob","Bobpass");
        user.setLogin(true);
    }

    @After
    public void tearDown() {
    }

    /**
     * General polling test for class MessagePoller.
     * @throws java.lang.InterruptedException
     */
    @Test
    public void testPolling() throws InterruptedException {

       ScheduledExecutorService execServSched = Executors.newSingleThreadScheduledExecutor();
       poll = new MessagePoller(comm, user, execServSched);
       // Because this is scheduled, we wait for it to run
       Thread.sleep(2100);
       execServSched.shutdown();
       execServSched.awaitTermination(10, TimeUnit.SECONDS);

    }

    class CommHandler extends telecomlab3.CommHandler {

        @Override
        public void sendMessagePermanentCallback(Message msg, Callback call) {
            assertEquals(msg.getType(), Message.TYPE_QUERY_MSG);
            assertEquals(msg.getSubType(), 0);
        }
    }

}
