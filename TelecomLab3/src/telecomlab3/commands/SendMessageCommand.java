package telecomlab3.commands;

import java.io.UnsupportedEncodingException;
import java.util.logging.Level;
import java.util.logging.Logger;
import telecomlab3.Callback;
import telecomlab3.CommHandler;
import telecomlab3.Command;
import telecomlab3.Message;
import telecomlab3.User;

public class SendMessageCommand implements Command, Callback {

    private final String name = "sendMsg";
    private final int argCount = 2;

    private static final Logger logger = Logger.getLogger(SendMessageCommand.class.getName());

    private final CommHandler comm;
    private User user;

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
            // TODO
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
    public void handleResponse(Message msg) {
        if (msg.getType() == Message.TYPE_SEND_MSG) {
            if (msg.getSubType() == Message.SUBTYPE_SEND_MSG_SUCCESS) {
                System.out.println("Message sent.");
            } else if (msg.getSubType() == Message.SUBTYPE_SEND_MSG_USER_NOT_LOGIN) {
                System.out.println("Error: cannot send message; user not logged in.");
            } else if (msg.getSubType() == Message.SUBTYPE_SEND_MSG_USER_NOT_EXIST) {
                System.out.println("Error: cannot send message; user does not exist.");
            } else if (msg.getSubType() == Message.SUBTYPE_SEND_MSG_FAIL_DATA_STORE) {
                System.out.println("Error: destination user does not have a data store.");
            } else if (msg.getSubType() == Message.SUBTYPE_SEND_MSG_BAD_FORMAT) {
                System.out.println("Error: bad message format for sending message.");
            }
        }
    }
    
    private void sendMessageToUser(String user, String message) {
        try {
            comm.sendMessage(new Message(Message.TYPE_SEND_MSG, user + ',' + message), this);
        } catch (UnsupportedEncodingException ex) {
            logger.log(Level.SEVERE, null, ex);
        }
    }

}
