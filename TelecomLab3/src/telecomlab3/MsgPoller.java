package telecomlab3;

import java.io.UnsupportedEncodingException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Polls the server for new messages.
 *
 * @author Michael
 */
public class MsgPoller implements Callback {

    private static final Logger logger = Logger.getLogger(MsgPoller.class.getName());

    private final CommHandler comm;
    private final User user;

    /**
     * Create the poller and begin querying the server for new messages
     * repeatedly if logged in.
     *
     * @param comm The commHandler to use when sending messages.
     * @param user The instance of {@link telecomlab3.User user} representing
     * the user. Used to determine login state.
     */
    public MsgPoller(CommHandler comm, User user) {
        this.comm = comm;
        this.user = user;

        // We put the poller on its own thread, to run every 2 seconds.
        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        Poller poll = new Poller();
        executorService.scheduleAtFixedRate(poll, 0, 2, TimeUnit.SECONDS);
    }

    /**
     * Used to process responses from server for our query commands. We can be
     * assured the message type will always be for our queryCommands thanks to
     * the {@link ResponseHandler responseHandler}.
     *
     * @param msg The message received.
     */
    @Override
    public void handleResponse(Message msg) {

        if (msg.getSubType() == Message.SUBTYPE_QUERY_MSG_MESSAGES) {
            System.out.println("Message received:");
            System.out.println(msg.getDataAsString());
        } else if (msg.getSubType() == Message.SUBTYPE_QUERY_MSG_NOT_LOG_IN) {
            // Somehow we're not logged in, update our variable
            user.setLogin(false);
            System.out.println(msg.getDataAsString());
        } // If it's anything but no messages, print error msg
        else if (msg.getSubType() != Message.SUBTYPE_QUERY_MSG_NO_MSG) {
            System.out.println("Error polling: " + msg.getDataAsString());
        }

    }

    // TODO: Is having this and handleRespons outside the Runnable class going to cause issues?
    // Query the server for messages.
    private void queryMessages() {
        try {
            comm.sendMessagePermanentCallback(new Message(Message.TYPE_QUERY_MSG, " "), this);
        } catch (UnsupportedEncodingException ex) {
            logger.log(Level.SEVERE, null, ex);
        }
    }

    // Small class used to have the poller run in its own thread.
    private class Poller implements Runnable {

        @Override
        public void run() {
            Thread thread = Thread.currentThread();
            thread.setName("Message Poller");
            if (user != null && user.getLoginState()) {
                queryMessages();
            }
        }

    }
}
