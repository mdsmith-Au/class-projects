package telecomlab3;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CommHandler {

    private Logger logger = Logger.getLogger(CommHandler.class.getName());
    Socket socket;
    BufferedOutputStream out;
    BufferedInputStream in;

    public CommHandler(ExecutorService executorService, String hostname, int portNumber) {
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


    }

    public void test() {
        try {
            String echoMsg = "This is really a test. Really. UTF!!!! : HajoƸ̵̡Ӝ̵̨̄Ʒ哈乔";
            byte[] byteArray = echoMsg.getBytes("UTF-8");
            Message msg = new Message(Message.TYPE_ECHO, byteArray);

            msg.writeToStream(out);
            out.flush();

            Message msgResponse = new Message(in);
            System.out.println("Message : " + msgResponse.getDataAsString());
        } catch (Exception ex) {
            Logger.getLogger(CommHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
