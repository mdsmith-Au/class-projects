package telecomlab3;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.util.Arrays;
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
     * @throws java.lang.Exception
     */
    @Test
    public void testWriteToStream() throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        String dataInMSg = "TEST";
        byte[] dataInMsgByte = dataInMSg.getBytes("UTF-8");
        Message test = new Message(Message.TYPE_SEND_MSG, Message.SUBTYPE_SEND_MSG_BAD_FORMAT, dataInMsgByte );
        test.writeToStream(out);
        
        ByteBuffer tempByteBuffer = ByteBuffer.allocate(12 + dataInMsgByte.length);
     
        byte[] outArray = out.toByteArray();
        tempByteBuffer.put(outArray);
        tempByteBuffer.rewind();

        int type = tempByteBuffer.getInt();
        int subType = tempByteBuffer.getInt();
        int size = tempByteBuffer.getInt();

        assert(size == dataInMsgByte.length);
        
        byte[] data = new byte[size];
        tempByteBuffer.get(data, 0, size);
        
        assert(type == Message.TYPE_SEND_MSG);
        assert(subType == Message.SUBTYPE_SEND_MSG_BAD_FORMAT);
        assert(Arrays.equals(data, dataInMsgByte));
        
    }

    /**
     * Test of readFromStream method, of class Message.
     */
    @Test
    public void testReadFromStream() throws Exception {
        
        String dataInMSg = "TEST";
        byte[] dataInMsgByte = dataInMSg.getBytes("UTF-8");
        ByteBuffer tempByteBuffer = ByteBuffer.allocate(12 + dataInMsgByte.length);
        
        tempByteBuffer.putInt(Message.TYPE_SEND_MSG);
        tempByteBuffer.putInt(Message.SUBTYPE_SEND_MSG_FAIL_DATA_STORE);
        tempByteBuffer.putInt(dataInMsgByte.length);
        tempByteBuffer.put(dataInMsgByte);
        
        ByteArrayInputStream in = new ByteArrayInputStream(tempByteBuffer.array());
        
        Message msg = new Message(in);

        assert(msg.getType() == Message.TYPE_SEND_MSG);
        assert(msg.getSubType() == Message.SUBTYPE_SEND_MSG_FAIL_DATA_STORE);
        assert(msg.getSize() == dataInMsgByte.length);
        assert(Arrays.equals(msg.getData(), dataInMsgByte));
    }
}
