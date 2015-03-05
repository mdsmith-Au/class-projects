package telecomlab3;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Handles communication with the server
 *
 * @author Michael
 */
public class CommHandler {

    private static final Logger logger = Logger.getLogger(CommHandler.class.getName());
    private ExecutorService execServ;

    private Socket socket;
    private BufferedOutputStream out;
    private BufferedInputStream in;
    private final ResponseHandler respHandle;

    /**
     * Create the connection to the remote server.
     *
     * @param executorService Will be used to create threads on demand as
     * needed.
     * @param hostname Hostname of the remote server.
     * @param portNumber Port number of the remote server.
     */
    public CommHandler(ExecutorService executorService, String hostname, int portNumber) {

        execServ = executorService;

        try {
            // Open connection to server
            socket = new Socket(hostname, portNumber);
        } catch (IOException ex) {
            logger.log(Level.SEVERE, "Warning: Unable to connect to server at {0}", hostname);
        }

        try {
            /* 
             Create Buffered Streams for input and output
             This should be a little more efficient than raw streams while maintaining
             compatibility with raw stream methods
             */
            in = new BufferedInputStream(socket.getInputStream());
            out = new BufferedOutputStream(socket.getOutputStream());

        } catch (IOException ex) {
            logger.log(Level.SEVERE, "Warning: Unable to properly communicate with server");
        }
        logger.log(Level.INFO, "Connected to the Telecom Server at {0}:{1}", new Object[]{hostname, portNumber});
        // Create the object that will process all responses
        respHandle = new ResponseHandler(execServ, in);
    }

    /**
     * Send a message. Callback will be called once when a message of the same
     * type is received from the server. Further messages of that type will not
     * be called back.
     *
     * @param msg The message to send; the type will be used to register
     * callbacks.
     * @param call The class to callback.
     */
    public void sendMessage(Message msg, Callback call) {
        respHandle.addCallbackMap(msg.getType(), call);
        createSendThread(msg);
    }

    /**
     * Send a message, with permanent callbacks. Functions like
     * {@link #sendMessage(telecomlab3.Message, telecomlab3.Callback) sendMessage}
     * except all future messages of this type will result in callbacks, rather
     * than only a single one.
     *
     * @param msg The message to send; the type will be used to register
     * callbacks.
     * @param call The class to callback.
     */
    public void sendMessagePermanentCallback(Message msg, Callback call) {
        respHandle.addCallbackMapPermanent(msg.getType(), call);
        createSendThread(msg);
    }

    /**
     * Create a new thread to send the message.
     *
     * @param msg The message to send.
     */
    private void createSendThread(Message msg) {
        messageProcess proc = new messageProcess(msg);
        execServ.submit(proc);
    }

    /**
     * Remove messages of this type from the permanent callback registry.
     *
     * @param msg The message specifying the type to remove. All data except the
     * type will be ignored.
     */
    public void removeCallbackPerm(Message msg) {
        respHandle.removeFromCallbackMapPerm(msg.getType());
    }

    /**
     * A class used when creating new threads to send messages.
     */
    private class messageProcess implements Runnable {

        private final Message message;

        public messageProcess(Message msg) {
            message = msg;
        }

        @Override
        public void run() {

            try {
                // Use the message's built in class to send
                message.writeToStream(out);
                // Ensure all data has been sent to server
                out.flush();

            } catch (IOException ex) {
                logger.log(Level.SEVERE, null, ex);
            }
        }
    }

}
