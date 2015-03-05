package telecomlab3;

import static org.junit.Assert.fail;

import java.io.InputStream;import java.io.OutputStream;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class MessageTest {

    public MessageTest() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of writeToStream method, of class Message.
     */
    @Test
    public void testWriteToStream() throws Exception {
        System.out.println("writeToStream");
        OutputStream out = null;
        Message instance = new Message();
        instance.writeToStream(out);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of readFromStream method, of class Message.
     */
    @Test
    public void testReadFromStream() throws Exception {
        System.out.println("readFromStream");
        InputStream in = null;
        Message instance = new Message(in);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
}
