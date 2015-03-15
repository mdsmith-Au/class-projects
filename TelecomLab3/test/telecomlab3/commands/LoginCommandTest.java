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
public class LoginCommandTest {

    private CommHandler comm;
    private User user;
    private LoginCommand log;
    
    public LoginCommandTest() {
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
        user = new User();
        log = new LoginCommand(comm, user);
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of getName method, of class LoginCommand.
     */
    @Test
    public void testGetName() {


        String expResult = "login";
        String result = log.getName();
        assertEquals(expResult, result);

    }

    /**
     * Test of execute and handleResponse method, of class LoginCommand.
     */
    @Test
    public void testExecuteAndHandleResponse() {


        // Normally, we would mock this kind of command with a local server
        // but since that's not possible, we assume it calls sendMessage()
        // in CommHandler with the appropriate message, which we verify
        // in our stub class
        log.execute("Bob,BobsPassword");

        // Assume message sent and good response received,
        // run callback
        log.handleResponse(new Message(Message.TYPE_LOGIN, Message.SUBTYPE_LOGIN_SUCCESS, new byte[] {}));
        assertEquals(user.getUsername(), "Bob");
        assertEquals(user.getPassword(), "BobsPassword");
        assertEquals(user.getLoginState(), true);
    }

    /**
     * Test of getArgCount method, of class LoginCommand.
     */
    @Test
    public void testGetArgCount() {
        int expResult = 2;
        int result = log.getArgCount();
        assertEquals(expResult, result);

    }

    class CommHandler extends telecomlab3.CommHandler {

        @Override
        public void sendMessage(Message msg, Callback call) {
            assertEquals(msg.getType(), Message.TYPE_LOGIN);
            assertEquals(msg.getSubType(), 0);
            assertEquals(msg.getDataAsString(), "Bob,BobsPassword");
        }
    }
}
