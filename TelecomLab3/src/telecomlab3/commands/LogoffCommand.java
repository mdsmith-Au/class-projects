package telecomlab3.commands;

import java.io.UnsupportedEncodingException;
import java.util.logging.Level;
import java.util.logging.Logger;
import telecomlab3.Callback;
import telecomlab3.CommHandler;
import telecomlab3.Command;
import telecomlab3.Message;
import telecomlab3.User;

/**
 * Handles the logoff command.
 */
public class LogoffCommand implements Command, Callback {

    private final String name = "logoff";
    private final int argCount = 0;

    private static final Logger logger = Logger.getLogger(LogoffCommand.class.getName());

    private final CommHandler comm;
    private final User user;

    /**
     * Initializes the command.
     *
     * @param comm The {@link CommHandler CommHanlder} to use when sending
     * messages.
     * @param user The {@link User User} to use for representing the current
     * user.
     */
    public LogoffCommand(CommHandler comm, User user) {
        this.comm = comm;
        this.user = user;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void execute(String arguments) {
        // Note: user might already be logged on from a previous (say crashed) session, so we don't
        // want to prevent them from logging off by checking the user object
        logoff();
    }

    @Override
    public int getArgCount() {
        return argCount;
    }

    @Override
    // Message is always logoff type
    public void handleResponse(Message msg) {
        user.setLogin(false);
        System.out.println(msg.getDataAsString());
    }

    private void logoff() {
        try {
            comm.sendMessage(new Message(Message.TYPE_LOGOFF, " "), this);
        } catch (UnsupportedEncodingException ex) {
            logger.log(Level.SEVERE, null, ex);
        }
    }
}
