package telecomserver;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class for a LeakyBucket type rate limiter
 *
 * @author michael
 */
public class LeakyBucket extends OutputStream {

    // Queue to use as base implementation for sending data
    private final ArrayBlockingQueue<Byte> queue;
    // Object representing the recurring read from queue
    private final ScheduledFuture schedF;
    // The delay between queue reads
    private static final int DELAY_BETWEEN_TRANSMISSION = 50; // milliseconds

    /**
     *
     * @param out OutputStream from the connection to write to
     * @param queueSize The size of the underlying queue to use
     * @param outputRate The output rate, in bytes/s, of data to send
     * @param execS The Executor to use when scheduling queue reads
     */
    public LeakyBucket(OutputStream out, int queueSize, int outputRate, ScheduledExecutorService execS) {
        // Create queue and schedule a recurring read (leak) from the queue
        // This runs on a 'clock' of a DELAY_BETWEEN_TRANSMISSION period
        this.queue = new ArrayBlockingQueue<>(queueSize, true);
        // Note the conversion of bytes/s to data/execution
        schedF = execS.scheduleAtFixedRate(new Transmit(out, outputRate / (1000 / DELAY_BETWEEN_TRANSMISSION)), 0, DELAY_BETWEEN_TRANSMISSION, TimeUnit.MILLISECONDS);
    }

    /**
     * Writes the specified byte to this output stream. The general contract for
     * <code>write</code> is that one byte is written to the output stream. The
     * byte to be written is the eight low-order bits of the argument
     * <code>b</code>. The 24 high-order bits of <code>b</code> are ignored.
     * <p>
     *
     * @param b the <code>byte</code>.
     * @exception IOException if an I/O error occurs. In particular, an
     * <code>IOException</code> may be thrown if the output stream has been
     * closed.
     */
    @Override
    public void write(int b) throws IOException {
        // Proceed if space in queue; else do nothing (discard data)
        if (queue.remainingCapacity() != 0) {
            queue.add((byte) b);
        }
    }

    /**
     * Class that is called repeatedly to send data
     */
    private class Transmit implements Runnable {

        // amountToSend = amount to send in one execution (clock cycle tick)
        private final int amountToSend;
        ArrayList<Byte> dataToSend;
        private final OutputStream out;
        private final Logger logT = Logger.getLogger(LeakyBucket.class.getName());

        public Transmit(OutputStream out, int amountToSend) {
            this.amountToSend = amountToSend;
            this.dataToSend = new ArrayList<>(amountToSend);
            this.out = out;
        }

        @Override
        public void run() {
            // Clear array, dump at most the amountToSend data into it
            dataToSend.clear();
            queue.drainTo(dataToSend, amountToSend);
            try {
                // Send all data in array
                for (Byte b : dataToSend) {
                    out.write(b);
                }
                out.flush();
            } catch (IOException ex) {
                // Cancel this tread to prevent infinite attempts to write 
                // to a bad stream (i.e. client died)
                logT.log(Level.INFO, ex.getLocalizedMessage());
                schedF.cancel(false);
            }
        }
    }
}
