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
 */
public class ResponseHandler {

    private final InputStream in;
    private final ExecutorService execServ;
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
        this.execServ = execService;
    }
    
    public void startListening() {
        ResponseProcess respP = new ResponseProcess();
        execServ.submit(respP);
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
     * Adds a Callback for a given type of message. The Callback is never removed
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

    public HashMap<Integer, Callback> getPermanentMap() {
        return callbackMapPerm;
    }

    public HashMap<Integer, Callback> getMap() {
        return callbackMap;
    }

    // Handles the server responses
    private class ResponseProcess implements Runnable {
        private Message responseMsg;

        public Message getLastResponseMsg() {
            return responseMsg;
        }

        @Override
        public void run() {
            try {
                Thread thread = Thread.currentThread();
                thread.setName("ResponseProcess");

                // We always wait for new messages
                while (true) {
                    // This will create a new message if it comes in
                    // Otherwise, it will block
                    responseMsg = new Message(in);

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
                    else if (responseMsg.getType() == Message.TYPE_LOGOFF && responseMsg.getSubType() == Message.SUBTYPE_LOGOFF_SESSION_EXPIRED) {
                        System.out.println("Server says user session has expired.");
                    }
                    
                }
            } catch (IOException | ClassNotFoundException ex) {
                logger.log(Level.SEVERE, null, ex);
            }
        }
    }
}
