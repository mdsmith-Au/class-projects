/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package telecomlab3;

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Michael
 */
public class ResponseHandlerTest {
    
    public ResponseHandlerTest() {
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
    
    @Test
    public void testListen() throws InterruptedException {
         
        String dataInMSg = "TEST";
        byte[] dataInMsgByte = new byte[] {};
        try {
            dataInMsgByte = dataInMSg.getBytes("UTF-8");
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(ResponseHandlerTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        ByteBuffer tempByteBuffer = ByteBuffer.allocate(12 + dataInMsgByte.length);
        
        tempByteBuffer.putInt(Message.TYPE_SEND_MSG);
        tempByteBuffer.putInt(Message.SUBTYPE_SEND_MSG_FAIL_DATA_STORE);
        tempByteBuffer.putInt(dataInMsgByte.length);
        tempByteBuffer.put(dataInMsgByte);
        
        
        ByteArrayInputStream in = new ByteArrayInputStream(tempByteBuffer.array());
        
        ExecutorService exec = Executors.newCachedThreadPool();
        
        ResponseHandler resp = new ResponseHandler(exec, in);
        MockCallback mock = new MockCallback();
        resp.addCallbackMap(Message.TYPE_SEND_MSG, mock);
        resp.startListening();

        exec.shutdown();
        exec.awaitTermination(10, TimeUnit.SECONDS);
    }
    
    public class MockCallback implements Callback{

        @Override
        public void handleResponse(Message msg) {
            assertEquals(msg.getType(), Message.TYPE_SEND_MSG);
            assertEquals(msg.getSubType(), Message.SUBTYPE_SEND_MSG_FAIL_DATA_STORE);
            assertEquals(msg.getDataAsString(), "TEST");
        }
        
    }
    
}
