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
 * Handles the sending of messages to other users.
 *
 * @author Michael
 */
public class SendMessageCommand implements Command, Callback {

    private final String name = "send";
    private final int argCount = 2;

    private static final Logger logger = Logger.getLogger(SendMessageCommand.class.getName());

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
    public SendMessageCommand(CommHandler comm, User user) {
        this.comm = comm;
        this.user = user;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void execute(String[] arguments) {
        if (arguments.length != argCount) {
            System.out.println("Error: bad number of arguments.");
        } else {
            if (user != null && user.getLoginState()) {
                sendMessageToUser(arguments[0], arguments[1]);
            } else {
                System.out.println("Error: user not logged in.");

            }
        }
    }

    @Override
    public int getArgCount() {
        return argCount;
    }

    @Override
    // Message always of send message type
    public void handleResponse(Message msg) {
        System.out.println(msg.getDataAsString());
    }

    private void sendMessageToUser(String user, String message) {
        try {
            comm.sendMessage(new Message(Message.TYPE_SEND_MSG, user + ',' + message), this);
        } catch (UnsupportedEncodingException ex) {
            logger.log(Level.SEVERE, null, ex);
        }
    }

}
