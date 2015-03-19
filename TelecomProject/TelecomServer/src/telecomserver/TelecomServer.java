package telecomserver;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import static common.Common.SERVER_PORT;
import static common.Common.EXIT_ERROR;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Initializes the server and waits for incoming connections.
 * @author michael
 */
public class TelecomServer {

    private static final Logger log = Logger.getLogger(TelecomServer.class.getName());

    // Propertu file with fields like server port or bucket parameters
    private static final String PROPERTY_FILE = "server.properties";
    // Text file (UTF-8) with data to send to client
    private static final String DATA_FILE_NAME = "data.txt";
    
    // Default bucket parameters
    private static final String QUEUE_SIZE = "4096";
    private static final String OUTPUT_RATE = "1024";
    
    // Service for creating threads for new client connections
    private static final ExecutorService exec = Executors.newCachedThreadPool();
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        // Create properties object for port number
        Properties prop = new Properties();
        // Set default port, queue size, output rate
        prop.setProperty("port", SERVER_PORT);
        prop.setProperty("queueSize", QUEUE_SIZE);
        prop.setProperty("outputRate", OUTPUT_RATE);
        try {
            // Try reading from file, but not fatal - fall back to defaults
            // if we can't
            try (FileInputStream in = new FileInputStream(PROPERTY_FILE)) {
                prop.load(in);
            }
        } catch (FileNotFoundException ex) {
            log.log(Level.WARNING, "Unable to load properties file {0}", ex.getLocalizedMessage());
        } catch (IOException ex) {
            log.log(Level.WARNING, "Unable to load properties file {0}", ex.getLocalizedMessage());
        }

        // Load (entire) data file from disk
        String data = null;
        try {
            data = new String(Files.readAllBytes(Paths.get(DATA_FILE_NAME)), StandardCharsets.UTF_8);
        } catch (IOException ex) {
            // Exit if we can't read this file, because it's essential
            log.log(Level.SEVERE, "Unable to read data file {0}", ex.getLocalizedMessage());
            System.exit(EXIT_ERROR);
        }

        // Convert port to integer
        String portNumber = prop.getProperty("port");
        int port = Integer.decode(portNumber);

        ServerSocket serverSocket = null;
        try {
            // Create server to listen
            serverSocket = new ServerSocket(port);
            log.log(Level.INFO, "Server started on {0}:{1}", new Object[]{serverSocket.getInetAddress(), String.valueOf(serverSocket.getLocalPort())});
        } catch (IOException ex) {
            // Exit if fatal error
            log.log(Level.SEVERE, null, ex);
            System.exit(EXIT_ERROR);
        }
        
        // Wait for incoming connections indefinitely
        while (true) {
            try {
                // Accept connection from client, pass it to a connection handler
                // in a new thread
                Socket clientSocket = serverSocket.accept();
                exec.submit(new ConnectionHandler(data, clientSocket, prop));
                log.log(Level.INFO, "Connection from {0}:{1} accepted", new Object[]{clientSocket.getInetAddress(), String.valueOf(clientSocket.getPort())});
            } catch (IOException ex) {
                log.log(Level.SEVERE, "Unable to accept connection.\n{0}", ex.getLocalizedMessage());
            }
        }
    }

}
