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
public class SendMessageCommandTest {

    private CommHandler comm;
    private User user;
    private SendMessageCommand send;
    
    public SendMessageCommandTest() {
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
        user = new User("bob2","bobsPass");
        user.setLogin(true);
        send = new SendMessageCommand(comm, user);
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of getName method, of class SendMessageCommand.
     */
    @Test
    public void testGetName() {

        String expResult = "send";
        String result = send.getName();
        assertEquals(expResult, result);

    }

    /**
     * Test of execute method, of class SendMessageCommand.
     */
    @Test
    public void testExecute() {
        send.execute("bob,This is a Test Message");
    }

    /**
     * Test of getArgCount method, of class SendMessageCommand.
     */
    @Test
    public void testGetArgCount() {

        int expResult = 2;
        int result = send.getArgCount();
        assertEquals(expResult, result);

    }

    class CommHandler extends telecomlab3.CommHandler {

        @Override
        public void sendMessage(Message msg, Callback call) {
            assertEquals(msg.getType(), Message.TYPE_SEND_MSG);
            assertEquals(msg.getSubType(), 0);
            assertEquals(msg.getDataAsString(), "bob,This is a Test Message");
        }
    }

}
