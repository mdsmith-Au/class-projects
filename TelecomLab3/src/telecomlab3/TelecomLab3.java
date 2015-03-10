package telecomlab3;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * Main class; calls the constructors of various methods to initialize
 * everything.
 */
public class TelecomLab3 {

    // This service will be used to create all non-schedule threads
    // It creates threads on demand as needed
    private static final ExecutorService execServ = Executors.newCachedThreadPool();

    /**
     * The main method.
     *
     * @param args
     */
    public static void main(String[] args) {
        // Initialze connection to server
        CommHandler comm = new CommHandler(execServ, "ecse-489.ece.mcgill.ca", 5001);
        // Create a user object to keep track of login info and log in state
        User user = new User();
        // Create UI and handle user input
        UI ui = new UI(execServ, comm, user);
        // Create poller to poll server for new messages
        ScheduledExecutorService execServSched = Executors.newSingleThreadScheduledExecutor();
        MessagePoller poller = new MessagePoller(comm, user, execServSched);
    }
}
