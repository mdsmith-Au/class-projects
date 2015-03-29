package telecomclient;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConnectionManager {
    private String server;
    private int port;
    private int numberOfConnections;

    private Socket socket;
    private BufferedReader in;
    private BufferedOutputStream out;

    private ExecutorService execService;
    private static final Logger logger = Logger.getLogger(ConnectionManager.class.getName());

    public ConnectionManager(Properties config, ExecutorService execService) {
        this(config.getProperty("server"),
            Integer.decode(config.getProperty("port")),
            Integer.decode(config.getProperty("connections")),
            execService);
    }

    public ConnectionManager(String server, int port, int numConnections, ExecutorService execService) {
        this.server = server;
        this.port = port;
        this.numberOfConnections = numConnections;
        this.execService = execService;

        try {
            this.socket = new Socket(server, port);
        } catch (IOException ex) {
            logger.log(Level.SEVERE, "Warning: Unable to connect to server at {0}", server);
        }

        try {
            in = new BufferedReader(new InputStreamReader(new BufferedInputStream(socket.getInputStream()), StandardCharsets.UTF_8));
            out = new BufferedOutputStream(socket.getOutputStream());
        } catch (IOException ex) {
            logger.log(Level.SEVERE, "Warning: Unable to properly communicate with server");
        }

        ConnectionListener listener = new ConnectionListener();
        execService.submit(listener);
    }

    public void sendPacket(RequestPacket packet) {
        MessageProcess msgProc = new MessageProcess(packet);
        execService.submit(msgProc);
    }

    public void sendAllPackets(RequestPacket packet) {
        System.out.println("Opening " + numberOfConnections + " threads");
        for (int i = 0; i < numberOfConnections; i++) {
            sendPacket(packet);
        }
    }


    /**
     * A class used when creating new threads to send messages.
     */
    private class MessageProcess implements Runnable {
        private final RequestPacket message;

        public MessageProcess(RequestPacket msg) {
            message = msg;
        }

        @Override
        public void run() {
            try {
                out.write(message.getTrafficType());
                out.write(message.getActivateFlag());
                out.flush();
            } catch (IOException ex) {
                logger.log(Level.SEVERE, null, ex);
            }
        }
    }

    private class ConnectionListener implements Runnable {
        @Override
        public void run() {
            String l;
            try {
                while ((l = in.readLine()) != null) {
                    System.out.println(l);
                }
            }
            catch (IOException ex) {
                logger.log(Level.SEVERE, "Could not read from reader", ex);
            }
        }
    }

}
