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
public class RegisterCommandTest {

    public RegisterCommandTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of getName method, of class RegisterCommand.
     */
    @Test
    public void testGetName() {
        RegisterCommand reg = new RegisterCommand(new CommHandler(), new User());
        String expResult = "register";
        String result = reg.getName();
        assertEquals(expResult, result);

    }

    /**
     * Test of execute method, of class RegisterCommand.
     */
    @Test
    public void testExecute() {

        CommHandlerTestCreate comm = new CommHandlerTestCreate();
        User user = new User();
        RegisterCommand reg = new RegisterCommand(comm, user);
        String arguments = "bob,BobsPass";
        reg.execute(arguments);

        assertEquals(user.getUsername(), "bob");
        assertEquals(user.getPassword(), "BobsPass");
    }

    /**
     * Test of getArgCount method, of class RegisterCommand.
     */
    @Test
    public void testGetArgCount() {

        RegisterCommand reg = new RegisterCommand(new CommHandler(), new User());
        int expResult = 2;
        int result = reg.getArgCount();
        assertEquals(expResult, result);

    }

    /**
     * Test of handleResponse method, of class RegisterCommand.
     */
    @Test
    public void testHandleResponse() {
        // Test first response: user created
        CommHandlerTestLogin comm = new CommHandlerTestLogin();
        User user = new User("bob", "BobsPass");
        user.setLogin(false);
        RegisterCommand reg = new RegisterCommand(comm, user);
        reg.handleResponse(new Message(Message.TYPE_CREATE_USER, Message.SUBTYPE_CREATE_USER_SUCCESS, new byte[] {}));

        
        // Test next response: user logged in
        CommHandlerTestStore comm2 = new CommHandlerTestStore();
        reg = new RegisterCommand(comm2, user);
        reg.handleResponse(new Message(Message.TYPE_LOGIN, Message.SUBTYPE_LOGIN_SUCCESS, new byte[] {}));
        assertEquals(user.getLoginState(), true);
        
        // Can't test last response, because it's a print statement
    }

    class CommHandler extends telecomlab3.CommHandler {

        @Override
        public void sendMessage(Message msg, Callback call) {
        }
    }

    class CommHandlerTestCreate extends telecomlab3.CommHandler {

        @Override
        public void sendMessage(Message msg, Callback call) {
            assertEquals(msg.getType(), Message.TYPE_CREATE_USER);
            assertEquals(msg.getSubType(), 0);
            assertEquals(msg.getDataAsString(), "bob,BobsPass");
        }
    }

    class CommHandlerTestLogin extends telecomlab3.CommHandler {

        @Override
        public void sendMessage(Message msg, Callback call) {
            assertEquals(msg.getType(), Message.TYPE_LOGIN);
            assertEquals(msg.getSubType(), 0);
            assertEquals(msg.getDataAsString(),"bob,BobsPass" );
        }
    }

    class CommHandlerTestStore extends telecomlab3.CommHandler {

        @Override
        public void sendMessage(Message msg, Callback call) {
            assertEquals(msg.getType(), Message.TYPE_CREATE_STORE);
            assertEquals(msg.getSubType(), 0);
        }
    }

}
