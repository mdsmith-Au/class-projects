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
    ExecutorService execServ;

    Socket socket;
    BufferedOutputStream out;
    BufferedInputStream in;

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

    }

    public void sendMessage(Message msg, Callback call) {
        messageProcess proc = new messageProcess(msg, call);
        execServ.submit(proc);
    }

    private class messageProcess implements Runnable {

        private Message message;
        private Callback call;

        public messageProcess(Message msg, Callback call) {
            message = msg;
            this.call = call;
        }

        @Override
        public void run() {

            try {
                message.writeToStream(out);
                out.flush();
                Message msgResponse = new Message(in);
                call.handleResponse(msgResponse);
                
            } catch (IOException ex) {
                logger.log(Level.SEVERE, null, ex);
            } catch (ClassNotFoundException ex) {
                logger.log(Level.SEVERE, null, ex);
            }
        }
    }

}
