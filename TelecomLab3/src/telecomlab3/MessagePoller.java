package telecomlab3;

import java.io.UnsupportedEncodingException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MessagePoller implements Callback {

    private static final Logger logger = Logger.getLogger(MessagePoller.class.getName());

    private final CommHandler comm;
    private User user;

    public MessagePoller(CommHandler comm, User user) {
        this.comm = comm;
        this.user = user;

        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        Poller poll = new Poller();
        executorService.scheduleAtFixedRate(poll, 0, 2, TimeUnit.SECONDS);
    }

    @Override
    // Msg type must be query b/c we registered this callback
    public void handleResponse(Message msg) {

        if (msg.getSubType() == Message.SUBTYPE_QUERY_MSG_MESSAGES) {
            System.out.println("Message received:");
            System.out.println(msg.getDataAsString());
        }
        else if (msg.getSubType() == Message.SUBTYPE_QUERY_MSG_NOT_LOG_IN) {
            // Somehow we're not logged in, update our variable
            user.setLogin(false);
            System.out.println(msg.getDataAsString());
        }
        // If it's anything but no messages, print error msg
        else if (msg.getSubType() != Message.SUBTYPE_QUERY_MSG_NO_MSG) {
            System.out.println("Error polling: " + msg.getDataAsString());
        }
    }

    // TODO: Is having this and handleRespons outside the Runnable class going to cause issues?
    private void queryMessages() {
        try {
            comm.sendMessagePermanentCallback(new Message(Message.TYPE_QUERY_MSG, " "), this);
        } catch (UnsupportedEncodingException ex) {
            logger.log(Level.SEVERE, null, ex);
        }
    }

    private class Poller implements Runnable {

        @Override
        public void run() {
            if (user != null && user.getLoginState()) {
                queryMessages();
            }
        }

    }
}
