package telecomlab3;

import java.util.HashMap;
import org.junit.After;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;

public class CommandHandlerTest {
    private CommandHandler cmdHandler;

    @Before
    public void setUp() {
        cmdHandler = CommandHandler.getInstance();
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of registerCommand method, of class CommandHandler.
     */
    @Test
    public void testRegisterCommand() {
        Command handler = new MockCommandHandler();
        cmdHandler.registerCommand("test", 0, handler);
        HashMap<String, CommandHandler.CommandEntry> map = cmdHandler.getMapping();

        assertEquals(map.size(), 1);
        assertEquals(map.get("test").getCommand(), handler);
        assertEquals(map.get("test").getArgCount(), 0);
    }

    /**
     * Test of parseCommand method, of class CommandHandler.
     */
    @Test
    public void testParseCommand() {
        Command handler = new MockCommandHandler();
        cmdHandler.registerCommand("test", 0, handler);
        cmdHandler.parseCommand("/test myargs");
    }

    /**
     * Test of getInstance method, of class CommandHandler.
     */
    @Test
    public void testGetInstance() {
        CommandHandler cmdHandler2 = CommandHandler.getInstance();
        assertTrue(cmdHandler == cmdHandler2);
    }

    private class MockCommandHandler implements Command, Callback {

        @Override
        public String getName() {
            return "test";
        }

        @Override
        public int getArgCount() {
            return 0;
        }

        @Override
        public void execute(String arguments) {
            assertEquals(arguments, "myargs");
        }

        @Override
        public void handleResponse(Message msg) {
            throw new UnsupportedOperationException();
        }

    }

}