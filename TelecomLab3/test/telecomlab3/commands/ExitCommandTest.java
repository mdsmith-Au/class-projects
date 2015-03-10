/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package telecomlab3.commands;

import junit.runner.Version;
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
public class ExitCommandTest {

    
    private CommHandler comm;
    private User user;
    private ExitCommand exit;

    public ExitCommandTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
        user = new User();
        comm = new CommHandler();
        exit = new ExitCommand(comm);
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of getName method, of class ExitCommand.
     */
    @Test
    public void testGetName() {

        String expResult = "exit";
        String result = exit.getName();
        assertEquals(expResult, result);

    }

    /**
     * Test of execute method, of class ExitCommand.
     */
    @Test
    public void testExecute() {
        /* 
        Can't be tested with this version of Junit, because
        we need support to check for JVM exit
        exit.execute(null);
        i.e. expect.expectSystemExit();
        where as a class variable
        
        @Rule
        public final ExpectedSystemExit expect = ExpectedSystemExit.none();
        */
    }

    /**
     * Test of getArgCount method, of class ExitCommand.
     */
    @Test
    public void testGetArgCount() {

        int expResult = 0;
        int result = exit.getArgCount();
        assertEquals(expResult, result);

    }

    class CommHandler extends telecomlab3.CommHandler {

        @Override
        public void sendMessage(Message msg, Callback call) {
            assertEquals(msg.getType(), Message.TYPE_EXIT);
            assertEquals(msg.getSubType(), 0);
        }
    }

}
