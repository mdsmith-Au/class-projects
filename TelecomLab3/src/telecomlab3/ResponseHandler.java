/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package telecomlab3;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.logging.Level;
import java.util.logging.Logger;
import telecomlab3.commands.LoginCommand;

/**
 *
 * @author Michael
 */
public class ResponseHandler {

    private InputStream in;
    private HashMap<Integer, Callback> callbackMap;
    
    private static final Logger logger = Logger.getLogger(LoginCommand.class.getName());

    public ResponseHandler(ExecutorService execService, InputStream in) {
        this.in = in;
        this.callbackMap = new HashMap<>();
        responseProcess respP = new responseProcess();
        execService.submit(respP);
    }

    public void addCallbackMap(int type, Callback call) {
        // This will cause an exception if it already exists; we want that
        callbackMap.put(type, call);
    }

    private class responseProcess implements Runnable {

        @Override
        public void run() {
            try {
                while (true) {
                    Message responseMsg = new Message(in);
                    int messageType = responseMsg.getType();
                    if (callbackMap.containsKey(messageType)) {
                        // Run callback
                        callbackMap.get(messageType).handleResponse(responseMsg);
                        callbackMap.remove(messageType);
                    }
                    else {
                        System.out.println("New unexpected message from server: " + responseMsg.getDataAsString());
                    }
                }
            } catch (IOException | ClassNotFoundException ex) {
                logger.log(Level.SEVERE, null, ex);
            }
        }

    }
}
