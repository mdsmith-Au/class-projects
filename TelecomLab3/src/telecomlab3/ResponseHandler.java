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
 * Waits for and processes all responses from server. Executes callbacks for
 * expected messages.
 *
 * @author Michael
 */
public class ResponseHandler {

    private final InputStream in;
    private final HashMap<Integer, Callback> callbackMap;
    private final HashMap<Integer, Callback> callbackMapPerm;

    private static final Logger logger = Logger.getLogger(LoginCommand.class.getName());

    /**
     * Initialize and start waiting for responses from the remote server.
     *
     * @param execService The executor service to use when creating a listener
     * thread.
     * @param in The input stream on which to listen.
     */
    public ResponseHandler(ExecutorService execService, InputStream in) {
        this.in = in;
        this.callbackMap = new HashMap<>();
        this.callbackMapPerm = new HashMap<>();
        responseProcess respP = new responseProcess();
        execService.submit(respP);
    }

    /**
     * Adds a Callback for a given type of message. Callback is removed after
     * one message of this type is received.
     *
     * @param type The message type.
     * @param call The class to callback.
     */
    public void addCallbackMap(int type, Callback call) {
        callbackMap.put(type, call);
    }

    /**
     * Adds a Callback for a given type of message. The Callback is never remove
     * unless {@link  #removeFromCallbackMapPerm(int) removeFromCallbackMapPerm}
     * is called.
     *
     * @param type The message type.
     * @param call The class to callback.
     */
    public void addCallbackMapPermanent(int type, Callback call) {
        //Hashmap accepts duplicates as long as they are the same map I believe
        callbackMapPerm.put(type, call);
    }

    /**
     * Removes a message of the given type from the permanent callback map.
     *
     * @param type The message type.
     */
    public void removeFromCallbackMapPerm(int type) {
        callbackMapPerm.remove(type);
    }

    // Handles the server responses
    private class responseProcess implements Runnable {

        @Override
        public void run() {
            try {
                Thread thread = Thread.currentThread();
                thread.setName("ResponseProcess");

                // We always wait for new messages
                while (true) {
                    // This will create a new message if it comes in
                    // Otherwise, it will block
                    Message responseMsg = new Message(in);

                    // Determine the message type to figure out the appropriate callback
                    int messageType = responseMsg.getType();

                    // If it's in the normal callback list, use that
                    if (callbackMap.containsKey(messageType)) {
                        // Run callback method
                        callbackMap.get(messageType).handleResponse(responseMsg);
                        // Remove from callback map
                        callbackMap.remove(messageType);
                    } // Otherwise, check if it's a permanent callback and use that
                    else if (callbackMapPerm.containsKey(messageType)) {
                        callbackMapPerm.get(messageType).handleResponse(responseMsg);
                    } // No in any of our maps, so we aren't expecting it
                    else {
                        System.out.println("New unexpected message from server of type " + responseMsg.getType() + " and sub type " + responseMsg.getSubType() + " with content:\n" + responseMsg.getDataAsString());
                    }
                }
            } catch (IOException | ClassNotFoundException ex) {
                logger.log(Level.SEVERE, null, ex);
            }
        }

    }
}
