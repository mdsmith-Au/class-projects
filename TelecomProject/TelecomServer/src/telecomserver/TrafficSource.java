/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package telecomserver;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Constant Bit Rate Data Sender
 *
 * @author michael
 */
public class TrafficSource implements Runnable {

    private final int dataSize;
    private final OutputStream out;
    private final byte[] data;
    private static final Logger log = Logger.getLogger(TrafficSource.class.getName());
    private int offset;
    private ScheduledFuture task;

    /**
     *
     * @param dataSize Amount of data to send when run
     * @param out OutputStream to write to
     * @param data Data to write
     */
    public TrafficSource(int dataSize, OutputStream out, byte[] data) {
        this.dataSize = dataSize;
        this.out = out;
        this.data = data;
        this.offset = 0;
        task = null;
    }

    public void setTask(ScheduledFuture task) {
        this.task = task;
    }

    @Override
    public void run() {
        int bytesUntilEOF = data.length - offset;
        // More data until end of file than we write this session
        if (bytesUntilEOF > dataSize) {
            try {
                out.write(data, offset, dataSize);
                offset += dataSize;
                out.flush();
            } catch (IOException ex) {
                log.log(Level.SEVERE, "{0}\nCancelling any future attempts to send data.", ex.getLocalizedMessage());
                // Cancel this tread to prevent infinite attempts to write 
                // to a bad stream (i.e. client died)
                if (task != null) {
                    task.cancel(false);
                }
            }
        } // Writing data this session needs to loop back to the beginning of the file
        else {
            int bytesFromBeginning = dataSize - bytesUntilEOF;
            try {
                //Write until end of file
                out.write(data, offset, bytesUntilEOF);
                // Finish writing from beginning of file
                out.write(data, 0, bytesFromBeginning);
                offset = bytesFromBeginning;
                out.flush();
            } catch (IOException ex) {
                log.log(Level.SEVERE, "{0}\nCancelling any future attempts to send data.", ex.getLocalizedMessage());
                // Cancel this tread to prevent infinite attempts to write 
                // to a bad stream (i.e. client died)
                if (task != null) {
                    task.cancel(false);
                }
            }

        }

    }

}
