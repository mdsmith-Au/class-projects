/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package telecomlab3.commands;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import telecomlab3.Callback;
import telecomlab3.Message;
import telecomlab3.User;

/**
 *
 * @author Michael
 */
public class DeleteCommandTest {

    private CommHandler comm;
    private User user;
    private DeleteCommand del;

    public DeleteCommandTest() {
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
        del = new DeleteCommand(comm, user);
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of getName method, of class DeleteCommand.
     */
    @Test
    public void testGetName() {

        String expResult = "delete";
        String result = del.getName();
        assertEquals(expResult, result);
    }

    /**
     * Test of execute method, of class DeleteCommand.
     */
    @Test
    public void testExecute() {
        String arguments = "";
        del.execute(arguments);

    }

    /**
     * Test of getArgCount method, of class DeleteCommand.
     */
    @Test
    public void testGetArgCount() {
        int expResult = 0;
        int result = del.getArgCount();
        assertEquals(expResult, result);
    }

    /**
     * Test of handleResponse method, of class DeleteCommand.
     */
    @Test
    public void testHandleResponse() {
        Message msg = new Message(Message.TYPE_DELETE_USER, Message.SUBTYPE_CREATE_USER_SUCCESS, new byte[] {});
        del.handleResponse(msg);
        assertEquals(user.getLoginState(), false);
        assertEquals(user.getUsername(), null);
        assertEquals(user.getPassword(), null);
    }

    class CommHandler extends telecomlab3.CommHandler {

        @Override
        public void sendMessage(Message msg, Callback call) {
            assertEquals(msg.getType(), Message.TYPE_DELETE_USER);
            assertEquals(msg.getSubType(), 0);
        }
    }
}
