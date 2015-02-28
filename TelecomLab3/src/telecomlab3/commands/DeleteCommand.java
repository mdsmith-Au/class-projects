package telecomlab3.commands;

import java.io.UnsupportedEncodingException;
import java.util.logging.Level;
import java.util.logging.Logger;
import telecomlab3.Callback;
import telecomlab3.CommHandler;
import telecomlab3.Command;
import telecomlab3.Message;
import telecomlab3.User;

public class DeleteCommand implements Command, Callback {

    private final String name = "delete";
    private final int argCount = 0;

    private static final Logger logger = Logger.getLogger(DeleteCommand.class.getName());

    private final CommHandler comm;
    private User user;

    public DeleteCommand(CommHandler comm, User user) {
        this.comm = comm;
        this.user = user;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void execute(String[] arguments) {

        if (user == null) {
            System.out.println("Error: user not logged in.");
        } else if (user != null && !user.getLoginState()) {
            System.out.println("Error: user not logged in.");
        } else {
            try {
                comm.sendMessage(new Message(Message.TYPE_DELETE_USER, "DELETE"), this);
            } catch (UnsupportedEncodingException ex) {
                logger.log(Level.SEVERE, null, ex);
            }
        }

    }

    @Override
    public int getArgCount() {
        return argCount;
    }

    @Override
    public void handleResponse(Message msg) {
        if (msg.getType() == Message.TYPE_DELETE_USER) {
            if (msg.getSubType() == Message.SUBTYPE_DELETE_USER_SUCCESS) {
                System.out.println("User successfully deleted.");
                user.setLogin(false);
                user.setUsername(null);
                user.setPassword(null);
            } else if (msg.getSubType() == Message.SUBTYPE_DELETE_USER_NOT_LOG_IN) {
                System.out.println("User not logged in; cannot delete.");
            } else if (msg.getSubType() == Message.SUBTYPE_DELETE_USER_ERROR) {
                System.out.println("Error deleting user.");
            }
        }
    }

}
