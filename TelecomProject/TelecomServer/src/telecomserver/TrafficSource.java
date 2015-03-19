package telecomserver;

import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Data sender for both Burst and CBR
 *
 * @author michael
 */
public class TrafficSource {

    private final int dataSize;
    private final int delay;
    private final OutputStream out;
    private final byte[] data;
    private static final Logger log = Logger.getLogger(TrafficSource.class.getName());
    private int offset;
    private final ScheduledExecutorService execS;
    private ScheduledFuture schedF;

    // Data amount to send, in bytes
    private final int CBR_AMOUNT = 800;
    private final int BURST_AMOUNT = 15000;

    // Delay for each type, in milliseconds
    private final int CBR_DELAY = 100;
    private final int BURST_DELAY = 15000;

    /**
     * 
     * @param out OutputStream to write to
     * @param data Data to write
     * @param type Requested type (0 = CBR, 1 = burst)
     * @param execS Executor to use
     */
    public TrafficSource(OutputStream out, byte[] data, int type, ScheduledExecutorService execS) {
        this.out = out;
        this.data = data;
        this.offset = 0;
        this.execS = execS;
        // Set delay + data amount for each type
        if (type == 0) {
            dataSize = CBR_AMOUNT;
            delay = CBR_DELAY;
        } else {
            dataSize = BURST_AMOUNT;
            delay = BURST_DELAY;
        }
    }

    // Start a recurring thread to send data
    public void start() {
        schedF = execS.scheduleAtFixedRate(new recurringWrite(), 0, delay, TimeUnit.MILLISECONDS);
    }

    private class recurringWrite implements Runnable {

        @Override
        public void run() {
            // Calculate how many bytes left in file 
            int bytesUntilEOF = data.length - offset;
            // This session, we won't have to loop back to the beginning of the file
            if (bytesUntilEOF > dataSize) {
                try {
                    out.write(data, offset, dataSize);
                    offset += dataSize;
                    out.flush();
                } catch (IOException ex) {
                    log.log(Level.SEVERE, "{0}\nCancelling any future attempts to send data.", ex.getLocalizedMessage());
                    // Cancel this tread to prevent infinite attempts to write 
                    // to a bad stream (i.e. client died)
                    if (schedF != null) {
                        schedF.cancel(false);
                    }
                }
            } // This session, we do have to loop back to the beginning
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
                    log.log(Level.INFO, ex.getLocalizedMessage());
                    // Cancel this tread to prevent infinite attempts to write 
                    // to a bad stream (i.e. client died)
                    if (schedF != null) {
                        schedF.cancel(false);
                    }
                }
            }
        }
    }
}
