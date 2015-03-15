/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package telecomlab3.commands;

import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import telecomlab3.Callback;
import telecomlab3.Message;
import telecomlab3.User;

/**
 *
 * @author Michael
 */
public class LogoffCommandTest {

    private CommHandler comm;
    private User user;
    private LogoffCommand log;

    public LogoffCommandTest() {
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
        user = new User("Bob", "BobsPass");
        user.setLogin(true);
        log = new LogoffCommand(comm, user);
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of getName method, of class LogoffCommand.
     */
    @Test
    public void testGetName() {
        String expResult = "logoff";
        String result = log.getName();
        assertEquals(expResult, result);
    }

    /**
     * Test of execute and handleResponse methods, of class LogoffCommand.
     */
    @Test
    public void testExecuteAndHandleResponse() {
        log.execute(null);
        log.handleResponse(new Message(Message.TYPE_LOGOFF, Message.SUBTYPE_LOGOFF_SUCCESS, new byte[] {}));
        assertEquals(user.getLoginState(), false);

    }

    /**
     * Test of getArgCount method, of class LogoffCommand.
     */
    @Test
    public void testGetArgCount() {
        int expResult = 0;
        int result = log.getArgCount();
        assertEquals(expResult, result);

    }

    class CommHandler extends telecomlab3.CommHandler {

        @Override
        public void sendMessage(Message msg, Callback call) {
            assertEquals(msg.getType(), Message.TYPE_LOGOFF);
            assertEquals(msg.getSubType(), 0);
        }
    }
}
