package telecomlab3.commands;

import java.io.UnsupportedEncodingException;
import java.util.logging.Level;
import java.util.logging.Logger;
import telecomlab3.Callback;
import telecomlab3.CommHandler;
import telecomlab3.Command;
import telecomlab3.Message;
import telecomlab3.User;

public class LogoffCommand implements Command, Callback {

    private final String name = "logoff";
    private final int argCount = 0;

    private static final Logger logger = Logger.getLogger(LogoffCommand.class.getName());

    private final CommHandler comm;
    private User user;

    public LogoffCommand(CommHandler comm, User user) {
        this.comm = comm;
        this.user = user;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void execute(String[] arguments) {

        // Note: user might already be logged on from a previous (say crashed) session, so we don't
        // want to prevent them from logging off by checking the user object
        logoff();

    }

    @Override
    public int getArgCount() {
        return argCount;
    }

    @Override
    public void handleResponse(Message msg) {
        if (msg.getType() == Message.TYPE_LOGOFF) {
            if (msg.getSubType() == Message.SUBTYPE_LOGOFF_SUCCESS) {
                System.out.println("User successfully logged off.");
                user.setLogin(false);
            } else if (msg.getSubType() == Message.SUBTYPE_LOGOFF_NOT_LOG_IN) {
                System.out.println("Cannot logoff; user not logged in.");
                // Just in case our code screwed up somewhere
                user.setLogin(false);
            } else if (msg.getSubType() == Message.SUBTYPE_LOGOFF_SESSION_EXPIRED) {
                System.out.println("Cannot logoff; session already expired.");
                user.setLogin(false);
            }
        }
    }

    private void logoff() {
        try {
            comm.sendMessage(new Message(Message.TYPE_LOGOFF, " "), this);
        } catch (UnsupportedEncodingException ex) {
            logger.log(Level.SEVERE, null, ex);
        }
    }
}
