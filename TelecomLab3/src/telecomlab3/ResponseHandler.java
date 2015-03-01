package telecomlab3;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.logging.Level;
import java.util.logging.Logger;
import telecomlab3.commands.LoginCommand;

public class ResponseHandler {

    private InputStream in;
    private HashMap<Integer, Callback> callbackMap;
    private HashMap<Integer, Callback> callbackMapPerm;

    private static final Logger logger = Logger.getLogger(LoginCommand.class.getName());

    public ResponseHandler(ExecutorService execService, InputStream in) {
        this.in = in;
        this.callbackMap = new HashMap<>();
        this.callbackMapPerm = new HashMap<>();
        responseProcess respP = new responseProcess();
        execService.submit(respP);
    }

    public void addCallbackMap(int type, Callback call) {
        callbackMap.put(type, call);
    }

    public void addCallbackMapPermanent(int type, Callback call) {
        //Hashmap accepts duplicates as long as they are the same map I believe
        callbackMapPerm.put(type, call);
    }

    public void removeFromCallbackMapPerm(int type) {
        callbackMapPerm.remove(type);
    }

    private class responseProcess implements Runnable {
        @Override
        public void run() {
            try {
                while (true) {
                    Message responseMsg = new Message(in);
                    int messageType = responseMsg.getType();
                    // Run normal callback
                    if (callbackMap.containsKey(messageType)) {
                        callbackMap.get(messageType).handleResponse(responseMsg);
                        callbackMap.remove(messageType);
                    }
                    // Check if exists as a permanent callback
                    else if (callbackMapPerm.containsKey(messageType)){
                        callbackMapPerm.get(messageType).handleResponse(responseMsg);
                    }
                    // Unexpected message
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
