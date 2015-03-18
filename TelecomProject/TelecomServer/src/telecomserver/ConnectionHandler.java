/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package telecomserver;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Properties;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Handles connections from clients.
 *
 * @author michael
 */
public class ConnectionHandler implements Runnable {

    private final Socket connection;
    private final byte[] data;

    private static final Logger log = Logger.getLogger(ConnectionHandler.class.getName());

    // For debugging via web browser
    private static final String CRLF = "\r\n";
    private static final String HTTP_HEADER = "HTTP/1.1 200" + CRLF + "Content-type: " + "text; charset=UTF-8" + CRLF + CRLF;

    private final int queueSize;
    private final int outputRate;

    // Data amount to send, in bytes
    private final int CBR_AMOUNT = 800;
    private final int BURST_AMOUNT = 15000;

    // Delay for each type, in milliseconds
    private final int CBR_DELAY = 100;
    private final int BURST_DELAY = 15000;

    private final ScheduledExecutorService execS;

    public ConnectionHandler(String data, Socket connection, Properties prop, ScheduledExecutorService execS) {
        this.connection = connection;
        this.data = data.getBytes(StandardCharsets.UTF_8);
        this.queueSize = Integer.decode(prop.getProperty("queueSize"));
        this.outputRate = Integer.decode(prop.getProperty("outputRate"));
        this.execS = execS;
    }

    @Override
    public void run() {
        // Give thread a useful name based on connection
        // Note that Netbeans may continue to show this name even if it changes
        Thread thr = Thread.currentThread();
        thr.setName("connection-" + connection.getPort());
        System.out.println("Connected!");

        // Read two bytes: config message (two bytes)
        BufferedInputStream in;

        byte[] config = new byte[2];
        try {
            in = new BufferedInputStream(connection.getInputStream());
            in.read(config, 0, 2);
        } catch (IOException ex) {
            log.log(Level.SEVERE, "Unable to read input stream");
            return;
        }

        // Create ouput stream for later use
        BufferedOutputStream out;

        try {
            out = new BufferedOutputStream(connection.getOutputStream());
        } catch (IOException ex) {
            log.log(Level.SEVERE, "Unable to create output stream");
            return;
        }

        byte type = config[0];
        byte activate = config[1];

        // Type / activate invalid
        if (!((type == 0 || type == 1) && (activate == 0 || activate == 1))) {
            log.log(Level.WARNING, "Data type or activation status invalid. Received type: {0}. Received activation: {1}. Assuming 0 for both settings.", new Object[]{type, activate});
            type = 0;
            activate = 0;
        }

        // Bucket not activated
        if (activate == 0) {
            // Constant Bit Rate
            if (type == 0) {
                TrafficSource traf = new TrafficSource(CBR_AMOUNT, out, data);
                ScheduledFuture task = execS.scheduleAtFixedRate(traf, 0, CBR_DELAY, TimeUnit.MILLISECONDS);
                traf.setTask(task);
            } // Burst traffic
            else {
                TrafficSource traf = new TrafficSource(BURST_AMOUNT, out, data);
                ScheduledFuture task = execS.scheduleAtFixedRate(traf, 0, BURST_DELAY, TimeUnit.MILLISECONDS);
                traf.setTask(task);
            }
        } // Bucket activated
        else {
            // Constant Bit Rate
            if (type == 0) {

            } // Burst traffic
            else {

            }
        }

    }
}
