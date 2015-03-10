/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package telecomlab3;

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.HashMap;
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
    
    private boolean callbackRan;
    
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
        callbackRan = false;
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of addCallbackMap method, of class ResponseHandler.
     */
    @Test
    public void testAddCallbackMap() {
        System.out.println("addCallbackMap");
        int type = 0;
        Callback call = null;
        ResponseHandler instance = null;
        instance.addCallbackMap(type, call);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of addCallbackMapPermanent method, of class ResponseHandler.
     */
    @Test
    public void testAddCallbackMapPermanent() {
        System.out.println("addCallbackMapPermanent");
        int type = 0;
        Callback call = null;
        ResponseHandler instance = null;
        instance.addCallbackMapPermanent(type, call);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of removeFromCallbackMapPerm method, of class ResponseHandler.
     */
    @Test
    public void testRemoveFromCallbackMapPerm() {
        System.out.println("removeFromCallbackMapPerm");
        int type = 0;
        ResponseHandler instance = null;
        instance.removeFromCallbackMapPerm(type);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getPermanentMap method, of class ResponseHandler.
     */
    @Test
    public void testGetPermanentMap() {
        System.out.println("getPermanentMap");
        ResponseHandler instance = null;
        HashMap<Integer, Callback> expResult = null;
        HashMap<Integer, Callback> result = instance.getPermanentMap();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getMap method, of class ResponseHandler.
     */
    @Test
    public void testGetMap() {
        System.out.println("getMap");
        ResponseHandler instance = null;
        HashMap<Integer, Callback> expResult = null;
        HashMap<Integer, Callback> result = instance.getMap();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
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
