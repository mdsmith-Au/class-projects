package telecomlab3;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CommHandler {

    private static final Logger logger = Logger.getLogger(CommHandler.class.getName());
    private ExecutorService execServ;

    private Socket socket;
    private BufferedOutputStream out;
    private BufferedInputStream in;
    private ResponseHandler respHandle;

    public CommHandler(ExecutorService executorService, String hostname, int portNumber) {
        execServ = executorService;
        try {
            socket = new Socket(hostname, portNumber);
        } catch (IOException ex) {
            logger.log(Level.SEVERE, "Warning: Unable to connect to server at " + hostname);
        }

        try {
            in = new BufferedInputStream(socket.getInputStream());
            out = new BufferedOutputStream(socket.getOutputStream());

        } catch (IOException ex) {
            logger.log(Level.SEVERE, "Warning: Unable to properly communicate with server");
        }
        logger.log(Level.INFO, "Connected to the Telecom Server at " + hostname + ":" + portNumber);
        respHandle = new ResponseHandler(execServ, in);
    }

    public void sendMessage(Message msg, Callback call) {
        respHandle.addCallbackMap(msg.getType(), call);
        createSendThread(msg);
    }

    public void sendMessagePermanentCallback(Message msg, Callback call) {
        respHandle.addCallbackMapPermanent(msg.getType(), call);
        createSendThread(msg);
    }
    
    private void createSendThread(Message msg) {
        messageProcess proc = new messageProcess(msg);
        execServ.submit(proc);
    }
    
    // Message = message with callback type to remove
    // Content can be anything; it is ignored
    public void removeCallbackPerm(Message msg) {
        respHandle.removeFromCallbackMapPerm(msg.getType());
    }

    private class messageProcess implements Runnable {

        private Message message;

        public messageProcess(Message msg) {
            message = msg;
        }

        @Override
        public void run() {

            try {
                message.writeToStream(out);
                out.flush();

            } catch (IOException ex) {
                logger.log(Level.SEVERE, null, ex);
            }
        }
    }

}
