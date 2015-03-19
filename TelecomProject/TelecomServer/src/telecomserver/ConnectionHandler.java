package telecomserver;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Handles individual connections from clients.
 *
 * @author michael
 */
public class ConnectionHandler implements Runnable {

    private final Socket connection;
    private final byte[] data;

    private static final Logger log = Logger.getLogger(ConnectionHandler.class.getName());

    private final int queueSize;
    private final int outputRate;

    private final ScheduledExecutorService execS = Executors.newScheduledThreadPool(1);

    public ConnectionHandler(String data, Socket connection, Properties prop) {
        this.connection = connection;
        this.data = data.getBytes(StandardCharsets.UTF_8);
        this.queueSize = Integer.decode(prop.getProperty("queueSize"));
        this.outputRate = Integer.decode(prop.getProperty("outputRate"));
    }

    @Override
    public void run() {
        // Give thread a useful name based on connection
        // Note that Netbeans may continue to show this name even if it changes
        Thread thr = Thread.currentThread();
        thr.setName("connection-" + connection.getPort());

        // Read two bytes = config message
        BufferedInputStream in;

        byte[] config = new byte[2];
        try {
            in = new BufferedInputStream(connection.getInputStream());
            in.read(config, 0, 2);
        } catch (IOException ex) {
            log.log(Level.SEVERE, "Unable to read input stream: {0}", ex.getLocalizedMessage());
            return;
        }

        // Create ouput stream for later use if we don't use the bucket
        BufferedOutputStream out;

        try {
            out = new BufferedOutputStream(connection.getOutputStream());
        } catch (IOException ex) {
            log.log(Level.SEVERE, "Unable to create output stream: {0}", ex.getLocalizedMessage());
            return;
        }

        // Get distinct variables for the client parameters
        byte type = config[0];
        byte activate = config[1];

        // Check if type or activate are invalid (i.e. not 0 or 1)
        if (!((type == 0 || type == 1) && (activate == 0 || activate == 1))) {
            log.log(Level.WARNING, "Data type or activation status invalid. Received type: {0}. Received activation: {1}. Assuming 0 for both settings.", new Object[]{type, activate});
            type = 0;
            activate = 0;
        }

        // Bucket not activated -> use BufferedOutput
        if (activate == 0) {
            TrafficSource traf = new TrafficSource(out, data, type, execS);
            traf.start();
        } // Bucket activated -> use LeakyBucket
        else {

            LeakyBucket leak = new LeakyBucket(out, queueSize, outputRate, execS);
            TrafficSource traf = new TrafficSource(leak, data, type, execS);
            traf.start();
        }
        // Wait for exit request from client
        // This will keep reading bytes from the client and ignore them until
        // it sees a -1, at which point it closes the connection
        while (true) {
            try {
                byte d[] = new byte[1];
                in.read(d);
                if (d[0] == -1) {
                    connection.close();
                    execS.shutdown();
                    return;
                }
            } // If this fires, the connection is already dead; just kill the
            // scheduled task and leave
            catch (IOException ex) {
                log.log(Level.INFO, "{0} for client at {1}", new Object[]{ex.getLocalizedMessage(), connection.toString()});
                execS.shutdown();
                return;
            }
        }
    }
}
